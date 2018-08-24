package com.weilt.eshopproduct.service;

import com.github.pagehelper.PageInfo;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.Product;
import com.weilt.common.entity.ProductDetailVo;

/**
 * @author weilt
 * @com.weilt.eshopproduct.service
 * @date 2018/8/23 == 12:25
 */
public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse setProductStatus(Integer productId,Integer status);
    ServerResponse<PageInfo> getProductList(int pageNum,int pageSize);
    ServerResponse<PageInfo> secrchProduct(String productName, Integer productId, int pageNum, int pageSize);
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
