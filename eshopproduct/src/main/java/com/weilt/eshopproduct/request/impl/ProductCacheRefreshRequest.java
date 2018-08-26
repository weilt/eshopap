package com.weilt.eshopproduct.request.impl;

import com.alibaba.fastjson.JSONObject;
import com.weilt.common.dto.Const;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.ProductDetailVo;
import com.weilt.common.redisservice.IRedisService;
import com.weilt.eshopproduct.request.Request;
import com.weilt.eshopproduct.service.IProductService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author weilt
 * @com.weilt.eshopproduct.request.impl
 * @date 2018/8/27 == 1:48
 */
public class ProductCacheRefreshRequest implements Request {
    /**
     * 商品id
     */
    private Integer productId;
    /**
     * 商品库存Service
     */
    private IProductService iProductService;
    /**
     * 是否强制刷新缓存
     */
    private IRedisService iRedisService;
    private boolean forceRefresh;

    public ProductCacheRefreshRequest(Integer productId, IProductService iProductService, boolean forceRefresh) {
        this.productId = productId;
        this.iProductService = iProductService;
        this.forceRefresh = forceRefresh;
    }

    @Override
    public void process() {
        // 从数据库中查询最新的商品信息
        ServerResponse<ProductDetailVo> response = iProductService.getProductDetail(productId);
        ProductDetailVo productDetailVo =(ProductDetailVo) response.getData();
        System.out.println("===========日志===========: 已查询到商品最新的库存数量，商品id=" + productId );
        //TODO 将最新的商品库存数量，刷新到redis缓存中去
        iRedisService.set(Const.CATEGORY_REDIS_KEY+productId.toString(),JSONObject.toJSONString(productDetailVo));
        //还需要装这个key值刷新到list 的缓存中去
        HashMap<String,Double> map = new HashMap<String,Double>();
        map.put(Const.CATEGORY_REDIS_KEY+productId.toString(),productDetailVo.getCategoryId().doubleValue());
        iRedisService.setCacheProductIdByCategory(map);

    }

    @Override
    public Integer getProductId() {
        return productId;
    }

    @Override
    public boolean isForceRefresh() {
        return forceRefresh;
    }
}
