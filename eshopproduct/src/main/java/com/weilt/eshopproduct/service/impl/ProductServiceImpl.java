package com.weilt.eshopproduct.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.weilt.common.dto.Const;
import com.weilt.common.dto.ResponseCode;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.*;
import com.weilt.common.utils.DateTimeUtil;
import com.weilt.eshopproduct.mapper.ProductMapper;
import com.weilt.eshopproduct.service.ICategorySerice;
import com.weilt.eshopproduct.service.IProductService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weilt
 * @com.weilt.eshopproduct.service.impl
 * @date 2018/8/23 == 12:25
 */
@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ICategorySerice iCategorySerice;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product){
        if(product == null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
            }

            if(product.getId() !=null){
                int rowCount = productMapper.updateById(product);
                if(rowCount>0) {
                    return ServerResponse.createBySuccessMessage("更新产品成功！");
                }else {
                    return ServerResponse.createByErrorMessage("更新产品失败！！");
                }
            }
            else {
                int rowCount = productMapper.insertProduct(product);
                if(rowCount>0){
                    return ServerResponse.createBySuccessMessage("新增产品成功！");
                }
                else {
                    return ServerResponse.createByErrorMessage("新增产品失败！！");
                }
            }
        }
        return ServerResponse.createBySuccessMessage("新增或更新产品参数不正确！！！");
    }

    @Override
    public ServerResponse<String> setProductStatus(Integer productId,Integer status){
        if(productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("修改产品壮态成功"); //上架，下架之类
        }
        return ServerResponse.createByErrorMessage("修改产品壮态失败");

    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
        //startPage --start
        //填充自已的sql逻辑
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectProductList();
        List<ProductListVo> productListVoList = new ArrayList<ProductListVo>();
        for(Product productItem:productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return  ServerResponse.createBySuccess(pageResult);

    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperties().getStringProperty("http://img.happyeshop.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }


    @Override
    public ServerResponse<PageInfo> secrchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }

        List<Product> productList = productMapper.selectByNameProductId(productName,productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public  ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return  ServerResponse.createByErrorMessage("产品已下架或删除");
        }
        if(product.getStatus()!=Const.ProductStatusEnum.ON_SALE.getCode()){
            return  ServerResponse.createByErrorMessage("产品已下架");
        }

        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setImageHost(PropertiesUtil.getProperties().getStringProperty("ftp.server.http.prefix","http://img.happyeshop.com/"));
        //调用Category服务
        ServerResponse<Category> serverResponse= iCategorySerice.getCateory(product.getCategoryId());//TODO需要访问数据库一次，需要优化   weilt
        Category category = (Category) serverResponse.getData();
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if(StringUtils.isBlank(keyword) && categoryId ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> catagoryIdList = new ArrayList<Integer>();
        if(categoryId!=null){
            //TODO
            ServerResponse<Category> serverResponse= iCategorySerice.getCateory(categoryId);//TODO需要访问数据库一次，需要优化   weilt
            Category category =(Category) serverResponse.getData();
            if(category == null  && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = new ArrayList<ProductListVo>();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //TODO 调用category服务
            catagoryIdList = iCategorySerice.getCategoryAndDeepChildCategory(categoryId).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,catagoryIdList.size()==0?null:catagoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
        }
    }

