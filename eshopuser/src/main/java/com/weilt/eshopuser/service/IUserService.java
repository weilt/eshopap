package com.weilt.eshopuser.service;

import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.User;

/**
 * @author weilt
 * @com.weilt.eshopuser.service
 * @date 2018/8/21 == 0:44
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse selectQuestion(String username);

    ServerResponse<String> checkAnswer(String userName,String question,String answer);

    ServerResponse<String> forgetResetPassword(String userName,String passwordNew,String forgetToken);

    ServerResponse<String> resetPassword(String password,String passwordNew,User user);

    ServerResponse<User> updateInfomation(User user);

    ServerResponse<User> getInfomation(Integer userId);

}
