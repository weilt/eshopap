package com.weilt.eshopcategory.controller.protal;

import com.weilt.common.dto.Const;
import com.weilt.common.dto.ResponseCode;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.User;
import com.weilt.eshopcategory.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @author weilt
 * @com.weilt.eshopcategory.controller.protal
 * @date 2018/8/25 == 1:34
 */
@RequestMapping(value = "/category")
public class CategoryController {
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("/getcategory")
    public ServerResponse getCategory(@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
            return iCategoryService.selectCategoryByPrimaryKey(categoryId);
    }

    @PostMapping(value = "/getdeepcategory")
    public ServerResponse getCategoryAndDeepChildCategory(@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
            return iCategoryService.selectCategoryAndDeepCategory(categoryId);
    }
}
