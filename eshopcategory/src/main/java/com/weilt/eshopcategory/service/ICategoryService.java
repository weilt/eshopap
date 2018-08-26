package com.weilt.eshopcategory.service;

import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.Category;

import java.util.List;
import java.util.Set;

/**
 * @author weilt
 * @com.weilt.eshopcategory.service
 * @date 2018/8/24 == 23:42
 */

public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);
    ServerResponse updateCategoryName(Integer categoryId,String categoryName);
    ServerResponse<List<Category>> getChildParallelCategory(Integer categoryId);
    ServerResponse<List<Integer>> selectCategoryAndDeepCategory(Integer categoryId);
    ServerResponse<Category> selectCategoryByPrimaryKey(Integer categoryId);
    ServerResponse<String> putAllDataToRedis();
    ServerResponse<List<Integer>> selectCacheCategoryAndDeepCategory(Integer categoryId);
}
