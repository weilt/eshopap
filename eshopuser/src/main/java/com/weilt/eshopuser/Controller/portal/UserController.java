package com.weilt.eshopuser.Controller.portal;

import com.weilt.common.dto.Const;
import com.weilt.common.dto.ResponseCode;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.User;
import com.weilt.eshopuser.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sun.awt.SunHints;

import javax.servlet.http.HttpSession;

/**
 * @author weilt
 * @com.weilt.eshopuser.Controller.portal
 * @date 2018/8/21 == 0:36
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录接口
     * @param userName
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ServerResponse<User> login(String userName, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(userName,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 登出
     * @param session
     * @return
     */
    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册用户
     * @param user
     * @return
     */
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }


    @RequestMapping(value = "/checkvalid",method = RequestMethod.POST)
    public ServerResponse<String> checkValid(String str,String type){
        return  iUserService.checkValid(str,type);
    }

    @RequestMapping(value = "/getuserinfo",method = RequestMethod.POST)
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
    }

    @RequestMapping(value = "/getforgetquestion")
    public ServerResponse<String> forgetGetQuestion(String userName){
        return iUserService.selectQuestion(userName);
    }

    @RequestMapping(value = "/checkanswer",method = RequestMethod.POST)
    public ServerResponse<String> forgetCheckAnswer(String userName,String question,String answer){
        return iUserService.checkAnswer(userName,question,answer);
    }

    @RequestMapping(value = "/resetforgetpassword",method = RequestMethod.POST)
    public ServerResponse<String> forgetResetPassword(String userName,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(userName,passwordNew,forgetToken);

    }


    @RequestMapping(value = "/resetpassword",method = RequestMethod.POST)
    public ServerResponse<String> resetPassword(HttpSession session,String password,String passwordNew){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(password,passwordNew,user);
    }


    @RequestMapping(value = "/updateinfomation",method = RequestMethod.POST)
    public ServerResponse<User> update_infomation(HttpSession session,User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        ServerResponse<User> response = iUserService.updateInfomation(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,user);
        }
        return response;
    }


    @RequestMapping(value = "/getinfomation",method = RequestMethod.POST)
    public ServerResponse<User> get_infomation(HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "未登录，需要强制登录！！！");
        }
        return iUserService.getInfomation(user.getId());
    }
}
