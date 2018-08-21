package com.weilt.eshopuser.service.impl;

import com.weilt.common.dto.Const;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.dto.TokenCache;
import com.weilt.common.entity.User;
import com.weilt.common.utils.MD5Util;
import com.weilt.eshopuser.mapper.UserMapper;
import com.weilt.eshopuser.service.IUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author weilt
 * @com.weilt.eshopuser.service.impl
 * @date 2018/8/21 == 0:45
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password){
        int resultCount = userMapper.checkUserName(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        MD5Util md5Util = new MD5Util();
        password = md5Util.getmd5(password);

        User user = userMapper.selectLogin(username,password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return  ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse<String> register(User user){
//        int resultCount = userMapper.checkUserName(user.getUserName());
//        if(resultCount>0){
//            return ServerResponse.createByErrorMessage("用户名不存在");
//        }
//        resultCount = userMapper.checkEmail(user.getEmail());
//        if(resultCount>0){
//            return  ServerResponse.createByErrorMessage("email已注册");
//        }
        ServerResponse validResponse = this.checkValid(user.getUserName(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        MD5Util md5Util = new MD5Util();
        user.setPassword(md5Util.getmd5(user.getPassword()));
        int resultCount = userMapper.addUser(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUserName(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("Email已注册");
                }
            }

        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse selectQuestion(String userName){
        ServerResponse validResponse = this.checkValid(userName,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return  ServerResponse.createByErrorMessage("用户不存在");
        }

        String question = userMapper.selectQuestionByUserName(userName);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }else {
            return ServerResponse.createByErrorMessage("找回密码问题为空！！");
        }

    }


    public ServerResponse<String> checkAnswer(String userName,String question,String answer){
            int resultCount = userMapper.checkAnswer(userName,question,answer);
            if(resultCount>0){
                //说明问题答案正确，用户符合
                String forgetToken = UUID.randomUUID().toString();
                TokenCache.setKey(TokenCache.TOKEN_PREFIX+userName,forgetToken);
                return ServerResponse.createBySuccess(forgetToken);
            }
            else {
                return ServerResponse.createByErrorMessage("问题答案不符合！！");
            }
    }


    public ServerResponse<String> forgetResetPassword(String userName,String passwordNew,String forgetToken)
    {
        if(StringUtils.isNotBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("需要传递Token!!");
        }
        ServerResponse validResponse = this.checkValid(userName,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return  ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+userName);

        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if(StringUtils.equals(forgetToken,token)){
            String md5Password = new MD5Util().getmd5(passwordNew);
            int rowCount = userMapper.updatePasswordByUserName(userName,md5Password);
            if(rowCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token 错误，请重新获取重置密码token!!");
        }
        return ServerResponse.createByErrorMessage("修改密码失败，请重试！！！");
    }

    public ServerResponse<String> resetPassword(String password,String passwordNew,User user){
        //防止横向越权，需要校验用户的旧密码，一定要指定是这个用户，因为我们查询的是count(1),如果不指定id ,
        //结果一定是>0，也就是ture
        int resultCount = userMapper.checkPassword(password,user.getId());
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("原密码错误！！！");
        }

        user.setPassword(new MD5Util().getmd5(passwordNew));
        int updateCount = userMapper.updateByIdSelective(user);
        if(updateCount>0)
        {
            return ServerResponse.createBySuccessMessage("密码更新成功!!!");
        }
        return ServerResponse.createByErrorMessage("密码更新失败！！！");
    }


    public ServerResponse<User> updateInfomation(User user){
        //userName 是不能更新的。
        //email也要进行一个校验，校验email是不是已经存在，如果存在的话，不能是当前用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("email地址已存在，请更换email地址再尝试！！");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByIdSelective(updateUser);
        if(updateCount>0){
            return  ServerResponse.createBySuccessMessage("更新成功！");
        }
        return  ServerResponse.createByErrorMessage("更新失败！！！");
    }

    public ServerResponse<User> getInfomation(Integer userId){
        User user = userMapper.selectById(userId);
        if(user == null){
            ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}
