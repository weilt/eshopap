package com.weilt.eshopproduct.service;

import com.weilt.eshopproduct.request.Request;

/**
 * 针对productId，异步多线程执行
 * @author weilt
 * @com.weilt.eshopproduct.service
 * @date 2018/8/27 == 1:22
 */
public interface RequestAsyncProcessService {
    void process(Request request);
}
