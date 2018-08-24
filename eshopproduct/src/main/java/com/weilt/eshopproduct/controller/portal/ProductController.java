package com.weilt.eshopproduct.controller.portal;

import com.github.pagehelper.PageInfo;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.ProductDetailVo;
import com.weilt.eshopproduct.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weilt
 * @com.weilt.eshopproduct.controller.backend
 * @date 2018/8/23 == 21:43
 */
@RestController
@RequestMapping(value = "/product")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @RequestMapping(value = "/detail")
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping("/list")
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }
}
