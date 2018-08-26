package com.weilt.eshopproduct.request;

/**
 * @author weilt
 * @com.weilt.eshopproduct.request
 * @date 2018/8/26 == 0:55
 */
public interface Request {
    void process();
    Integer getProductId();
    boolean isForceRefresh();
}
