package com.weilt.eshopcategory.mapper;

import com.weilt.common.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Locale;

/**
 * @author weilt
 * @com.weilt.eshopcategory.mapper
 * @date 2018/8/24 == 23:34
 */
@Mapper
public interface CategoryMapper {
    int insertCategory(Category category);
    int updateCategorySelective(Category category);
    List<Category> selectCategoryChildById(Integer parentId);
    Category selectById(Integer categoryId);
}
