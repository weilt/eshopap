package com.weilt.common.redisservice.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;
import com.weilt.common.entity.ProductDetailVo;
import com.weilt.common.entity.ProductListVo;
import com.weilt.common.redisservice.IRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
import redis.clients.util.JedisClusterCRC16;


import java.util.*;

/**
 * @author weilt
 * @com.weilt.common.redisservice.impl
 * @date 2018/8/22 == 17:31
 */
@Service
public class RedisServiceImpl implements IRedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);

    @Autowired
    private JedisCluster jedisCluster;

    @Override
    public String get(String key) {
        return jedisCluster.get(key);
    }

    @Override
    public void set(String key, String value) {
        jedisCluster.set(key, value);
    }

    @Override
    public void del(String key) {
        jedisCluster.del(key);
    }

    //读取redis list，不应该在这里读取

    /**
    public List<ProductListVo> getAllByIds(List<String> keys)
    {
        List<ProductListVo> productListVos = null;
        for(String key:keys){
            String productString = jedisCluster.get(key);
            if(org.apache.commons.lang3.StringUtils.isNotBlank(productString)) {
                //高并发环境下，随时可能被其它线程修改，所以在这需要看下是不是为空
                ProductDetailVo productDetailVo = (ProductDetailVo) JSON.parseObject(productString, ProductDetailVo.class);
                ProductListVo productListVo = new ProductListVo();
                productListVo.setId(productDetailVo.getId());
                productListVo.setName(productDetailVo.getName());
                productListVo.setSubtitle(productDetailVo.getSubtitle());
                productListVo.setStatus(productDetailVo.getStatus());
                productListVo.setPrice(productDetailVo.getPrice());
                productListVo.setImageHost(productDetailVo.getImageHost());
                productListVo.setMainImage(productDetailVo.getMainImage());
                productListVos.add(productListVo);
            }
        }
        return productListVos;
    }
     */

    //根据key的前缀清掉所有缓存
    @Override
    public void delAllByPrefix(String prefixKey) {
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        String keysPattern = prefixKey + "*";
        long countX = 0;
        long sTime = System.currentTimeMillis();
        for (Map.Entry<String, JedisPool> entry : clusterNodes.entrySet()) {
            Jedis jedisNode = entry.getValue().getResource();

            //如果当前节点不是slave节点
            if (!jedisNode.info("replication").contains("role:slave")) {
                //取得当前node 下所有key为Pattern开头的keys
                Set<String> keys = jedisNode.keys(keysPattern);
                Map<Integer, List<String>> map = new HashMap<>(6600);
                long countMap = 0;
                for (String key : keys) {
                    int slot = JedisClusterCRC16.getSlot(key);
                    if (map.containsKey(slot)) {
                        map.get(slot).add(key);
                    } else {
                        List<String> keyList = new ArrayList<String>();
                        keyList.add(key);
                        map.put(slot, keyList);
                    }
                }
                long count = 0;
                for (Map.Entry<Integer, List<String>> integerListEntry : map.entrySet()){
                    count += jedisNode.del(integerListEntry.getValue().toArray(new String[integerListEntry.getValue().size()]));
                    countX++;
                }
            }
        }
        //删除完成
        logger.info("删除userid key任务结束，一共删除key数量:{},耗时:{}", countX, System.currentTimeMillis() - sTime);
    }

    //批量查询，在redis cluster中，批量查询是个难题
    //当数据量大的时候，难道也要遍历完整个redis?
    //所以在存储之时，必须有计划，将productDetailVo数据存入redis cluster中时
    //需要有别一块缓存，专门记录存入redis中的所有key值，当批量查询时，根据需要，先查到所有的key
    //然后，在每一个节点上

    //map(常量+productid,cateoryid)
    //jedisCluster.zadd("productListKey",map)
    //所以查询的时候，先查100条出来
    //for循环，查到符合条件是记录，当符合条件的记录到pageSize是，跳出，返回
    //如果一直到整个id查完都没有找齐数据，查询，比对，看是否有新增的productId,如果有，查出来写入缓存，更新productListKey


    /**
     * 在redis中查出pagesize的符合要求的productKey,
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<String> getCacheProductIdByCategoryId(Integer categoryId, int pageNum, int pageSize) {
        List<String> productKeyList = new ArrayList<String>();
        for (int i = 0; i <= jedisCluster.zcard("productListKeys"); i++) {
            Set<Tuple> stringSet = jedisCluster.zrangeByScoreWithScores("productListKeys", 0, (i+1)*100);
            for (Tuple tuple : stringSet) {
                String productKey = tuple.getElement();
                Integer categoryIdItem = Integer.getInteger(Double.toString(tuple.getScore()));
                if (categoryIdItem.equals(categoryId)) {
                    productKeyList.add(productKey);
                    if (productKeyList.size() >= pageSize*(pageNum+1))
                        break;
                }
            }
            if (productKeyList.size() >= pageSize*(pageNum+1)) {
                break;
            }
        }
        return productKeyList;
    }

    //批量插入
    @Override
    public void setCacheProductIdByCategory(HashMap<String,Double> map) {
        jedisCluster.zadd("productListKeys",map);
    }

    @Override
    public boolean exists(String key) {
        if (jedisCluster.exists(key)) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void delCacheListProductId(String key){
        jedisCluster.zrem("productListKeys",key);
    }


}
