package com.weilt.eshopcart.otherserviceinterface;

import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.ProductDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author weilt
 * @com.weilt.eshopcart.otherserviceinterface
 * @date 2018/8/24 == 22:41
 * 在iCartServiceImpl中需要调用product中是方法，所以有这有个接口，装product模块中的服务转接过来
 */
@FeignClient(value = "eshop-productservice")
public interface ProductService {
    @RequestMapping(value = "/product/detail",method = RequestMethod.GET)
    ServerResponse<ProductDetailVo> detail(Integer productId);
}
