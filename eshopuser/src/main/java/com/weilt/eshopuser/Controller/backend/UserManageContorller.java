package com.weilt.eshopuser.Controller.backend;

import com.weilt.common.dto.Const;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.User;
import com.weilt.eshopuser.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * @author weilt
 * @com.weilt.eshopuser.Controller.backend
 * @date 2018/8/21 == 23:42
 */
@RequestMapping(value = "/manage/user")
public class UserManageContorller {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ServerResponse<User> login(String userName, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(userName,password);
        if(response.isSuccess()){
            User user = response.getData();
            if(user.getRole() == Const.Role.ROLE_ADMIN){
                session.setAttribute(Const.CURRENT_USER,user);
            }else{
                return ServerResponse.createByErrorMessage("非管理员，无法登录");
            }
        }
        return response;
    }
}
