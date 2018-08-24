package com.weilt.eshopcart.service;

import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.CartVo;

/**
 * @author weilt
 * @com.weilt.eshopcart.service
 * @date 2018/8/24 == 22:26
 */
public interface ICartService {
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count);
    ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);
    ServerResponse<CartVo> list (Integer userId);
    ServerResponse<CartVo> selectOrUnSelect (Integer userId,Integer productId,Integer checked);
    ServerResponse<Integer> getCartProductCount(Integer userId);
}
