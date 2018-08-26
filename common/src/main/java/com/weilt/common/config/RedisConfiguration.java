package com.weilt.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * @author weilt
 * @com.weilt.common.config
 * @date 2018/8/25 == 2:57
 */
@Configuration
public class RedisConfiguration {
    @Bean
    public JedisCluster JedisClusterFactory() {
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        jedisClusterNodes.add(new HostAndPort("192.168.201.120", 7001));
        jedisClusterNodes.add(new HostAndPort("192.168.201.121", 7003));
        jedisClusterNodes.add(new HostAndPort("192.168.201.122", 7005));
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
        return jedisCluster;
    }
}
