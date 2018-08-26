package com.weilt.common.redisservice;

import java.util.HashMap;
import java.util.List;

/**
 * @author weilt
 * @com.weilt.common.redisservice
 * @date 2018/8/22 == 17:30
 */
public interface IRedisService {

    String get(String key);

    void set(String key,String value);

    void del(String key);
    //根据前缀删除多个key
    void delAllByPrefix(String prefixKey);

    boolean exists(String key);

    void setCacheProductIdByCategory(HashMap<String,Double> map);

    List<String> getCacheProductIdByCategoryId(Integer categoryId, int pageNum, int pageSize);

    void delCacheListProductId(String key);
}
