package com.weilt.common.redisservice;

/**
 * @author weilt
 * @com.weilt.common.redisservice
 * @date 2018/8/22 == 17:30
 */
public interface IRedisService {

    String get(String key);

    void set(String key,String value);
}
