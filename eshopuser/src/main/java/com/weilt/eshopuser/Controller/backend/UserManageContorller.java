package com.weilt.eshopuser.Controller.backend;

import com.weilt.common.dto.Const;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.User;
import com.weilt.eshopuser.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * @author weilt
 * @com.weilt.eshopuser.Controller.backend
 * @date 2018/8/21 == 23:42
 */
@RequestMapping(value = "/manage")
@Api(value = "后台用户登录",tags = "非管理用户不能进入此模块")
public class UserManageContorller {
    @Autowired
    private IUserService iUserService;

    @PostMapping(value = "/login")
    @ApiImplicitParams({@ApiImplicitParam(name = "userName",value = "用户名",required = true,dataType = "String"),
                        @ApiImplicitParam(name = "password",value = "用户密码",required = true,dataType = "String"),
                        @ApiImplicitParam(name = "session",value = "session",required = true,dataType = "HttpSession")})
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
