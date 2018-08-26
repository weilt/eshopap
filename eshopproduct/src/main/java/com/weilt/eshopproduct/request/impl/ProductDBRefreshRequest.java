package com.weilt.eshopproduct.request.impl;

import com.weilt.common.dto.Const;
import com.weilt.common.entity.Product;
import com.weilt.common.entity.ProductDetailVo;
import com.weilt.common.redisservice.IRedisService;
import com.weilt.common.utils.DateTimeUtil;
import com.weilt.eshopproduct.request.Request;
import com.weilt.eshopproduct.service.IProductService;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * @author weilt
 * @com.weilt.eshopproduct.request.impl
 * @date 2018/8/27 == 2:23
 */
public class ProductDBRefreshRequest implements Request {
    private IRedisService iRedisService;
    /**
     * 商品详细
     */
    private ProductDetailVo productDetailVo;
    /**
     * 商品Service
     */
    private IProductService iProductService;

    public ProductDBRefreshRequest(ProductDetailVo productDetailVo, IProductService iProductService) {
        this.productDetailVo = productDetailVo;
        this.iProductService = iProductService;
    }

    @Override
    public void process() {

        // 删除redis中的缓存
        String key = Const.CATEGORY_REDIS_KEY+productDetailVo.getId().toString();
        iRedisService.del(Const.CATEGORY_REDIS_KEY+productDetailVo.getId().toString());
        //list缓存中也必删除.
        iRedisService.delCacheListProductId(key);
        // 为了模拟演示先删除了redis中的缓存，然后还没更新数据库的时候，读请求过来了，这里可以人工sleep一下
//		try {
//			Thread.sleep(20000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
        // 修改数据库中的库存
        Product product = null;
        product.setId(productDetailVo.getId());
        product.setMainImage(productDetailVo.getMainImage());
        product.setStock(productDetailVo.getStock());
        product.setCategoryId(productDetailVo.getCategoryId());
        product.setDetail(productDetailVo.getDetail());
        product.setName(productDetailVo.getName());
        product.setPrice(productDetailVo.getPrice());
        product.setStatus(productDetailVo.getStatus());
        product.setSubImages(productDetailVo.getSubImages());
        product.setSubtitle(productDetailVo.getSubtitle());
        product.setUpdateTime(DateTimeUtil.strToDate(productDetailVo.getUpdateTime()));
        iProductService.saveOrUpdateProduct(product);

    }

    /**
     * 获取商品id
     */
    public Integer getProductId() {
        return productDetailVo.getId();
    }

    @Override
    public boolean isForceRefresh() {
        return false;
    }
}
