package com.weilt.common.dto;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author weilt
 * @com.weilt.eshopuser.dto
 * @date 2018/8/21 == 1:41
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }


    public interface Cart{
        int CHECKED = 1; //购物车选中壮态
        int UN_CHECKED = 0;//购物车未选中
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
    }
    public interface Role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1;   //管理员
    }

    public interface Redis_Lock{
        String REDIS_LOCK_CATEGORY_KEY="REDIS_LOCK_CATEGORY_KEY";
        String GET_REDIS_LOCK = "GETED_REDIS_LOCK";//获得redis锁
        String  RELEASE_REDIS_LOCK = "RELEASED_REDIS_LOCK";//释放redis锁
    }
    public static final String EMAIL="email";
    public static final String USERNAME="username";

    public static final String CATEGORY_REDIS_KEY = "category_redis_key_";

   public enum ProductStatusEnum{
       ON_SALE(1,"在线");
       private String value;
       private int code;

       ProductStatusEnum(int code,String value){
           this.code = code;
           this.value = value;
       }

       public String getValue() {
           return value;
       }

       public int getCode() {
           return code;
       }
   }

}
