package com.weilt.common.dto;

/**
 * @author weilt
 * @com.weilt.eshopuser.dto
 * @date 2018/8/21 == 1:41
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";
    public interface Role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1;   //管理员
    }
    public static final String EMAIL="email";
    public static final String USERNAME="username";
}
