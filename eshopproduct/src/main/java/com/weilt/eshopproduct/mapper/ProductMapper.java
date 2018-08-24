package com.weilt.eshopproduct.mapper;

import com.weilt.common.entity.Catagory;
import com.weilt.common.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author weilt
 * @com.weilt.eshopproduct.mapper
 * @date 2018/8/23 == 12:13
 */
@Mapper
public interface ProductMapper {
    int updateById(Product product);
    int insertProduct(Product product);
    int updateByPrimaryKeySelective(Product product);

    List<Product> selectProductList();
    List<Product> selectByNameProductId(@Param("productName")String productName,@Param("productId") Integer productId);

    Product selectByPrimaryKey(Integer productId);

    List<Product> selectList();

    List<Product> selectByNameAndProductId(@Param("productName")String productName,@Param("productId") Integer productId);

    List<Product> selectByNameAndCategoryIds(@Param("productName")String productName,@Param("categoryIdList")List<Integer> categoryIdList);
}
