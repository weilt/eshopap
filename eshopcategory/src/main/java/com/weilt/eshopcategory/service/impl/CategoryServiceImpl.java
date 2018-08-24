package com.weilt.eshopcategory.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.Category;
import com.weilt.eshopcategory.mapper.CategoryMapper;
import com.weilt.eshopcategory.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author weilt
 * @com.weilt.eshopcategory.service.impl
 * @date 2018/8/24 == 23:44
 */
@Service
public class CategoryServiceImpl implements ICategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId){
        if(parentId ==null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//这个分类是可用的。

        int rowCount = categoryMapper.insertCategory(category);
        if(rowCount>0){
            return ServerResponse.createBySuccess("添加分类成功!!");
        }
        return ServerResponse.createByErrorMessage("添加分类失败！！");
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount = categoryMapper.updateCategorySelective(category);
        if(rowCount>0){
            return  ServerResponse.createBySuccessMessage("更新品类成功");
        }
        return ServerResponse.createByErrorMessage("更新品类失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildParallelCategory(Integer categoryId){
        List<Category> categorieList = categoryMapper.selectCategoryChildById(categoryId);
        if(CollectionUtils.isEmpty(categorieList)){
            //这不是一个错误，所以不用ServerResponse.createErrormessage
            logger.info("未找到任何子节点");
        }
        return ServerResponse.createBySuccess(categorieList);
    }

    @Override
    public ServerResponse<List<Integer>> selectCategoryAndDeepCategory(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId!=null){
            for(Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectById(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        List<Category> categorieList = categoryMapper.selectCategoryChildById(categoryId);
        for(Category categoryItem:categorieList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }

    @Override
    public ServerResponse<Category> selectCategoryByPrimaryKey(Integer categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if(category!=null){
            return ServerResponse.createBySuccess("成功获取数据",category);
        }
        return ServerResponse.createByErrorMessage("找不到当前商品分类记录");
    }
}
