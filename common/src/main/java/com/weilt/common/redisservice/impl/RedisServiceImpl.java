package com.weilt.common.redisservice.impl;

import com.weilt.common.redisservice.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

/**
 * @author weilt
 * @com.weilt.common.redisservice.impl
 * @date 2018/8/22 == 17:31
 */
@Service
public class RedisServiceImpl implements IRedisService {

    @Autowired
    private JedisCluster jedisCluster;

    @Override
    public String get(String key) {
            return jedisCluster.get(key);
    }

    @Override
    public void set(String key, String value) {
        jedisCluster.set(key,value);
    }

    public void del(String key){
        jedisCluster.del(key);
    }
}
