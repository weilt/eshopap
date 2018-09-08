package com.weilt.eshopproduct.dynamic;

import java.lang.annotation.*;

/**
 * @author weilt
 * @com.weilt.eshopproduct.dynamic
 * @date 2018/9/8 == 11:21
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String value();
}
