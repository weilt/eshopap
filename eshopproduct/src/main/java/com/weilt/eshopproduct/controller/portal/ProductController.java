package com.weilt.eshopproduct.controller.portal;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.weilt.common.dto.Const;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.ProductDetailVo;
import com.weilt.common.redisservice.IRedisService;
import com.weilt.eshopproduct.request.Request;
import com.weilt.eshopproduct.request.impl.ProductCacheRefreshRequest;
import com.weilt.eshopproduct.service.IProductService;
import com.weilt.eshopproduct.service.RequestAsyncProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    @Resource
    RequestAsyncProcessService requestAsyncProcessService;
    @Resource
    private IRedisService iRedisService;

    /**
     * 直接查询数据库的方式太过于低效，不能扛高并发
     * 所以，在这里，应该改成多线程，多级缓存架构
     * @param productId
     * @return
     * redis 是Nosql数据库,redis cluster不能使和pipeline批量操作，
     * 所以，需要自已写一些中间方法，来批量操作redis cluster
     * list部分，list 一页中显示多个商品，
     * list 只显示商品主图，productId,categoryId,status,price,productName
     * list部分可以随机显示，也可以按分类来显示，如果是数据库查询，很容易实现
     * 但是不管理redis cluster还是ehcache，就都不是那么友好了。
     * 所以，redis cluster缓存，一部分是productListVo,建立这部分key时，不单需要productId信息
     * 还需要categoryId的信息
     *当第一次打开list页面时，商品是按所以商品是最热商品显示对对，但我们数据库并没有设计这个表专门用来存放热点商品或
     * 者list商品表，查询数据库可以按order by 任何一个字段，而redis 和 ehcache都是nosql的方式，我们取得数据之后还需要自已进行
     * 排序，想当于要自已重写sql 的实现。
     * 1.list部分Key以常量+product_id+"_"+categoryId组成
     * 2.当收到前台请求时，异步执行读取数据程序。
     * 所以，开始吧
     *
     */


    @RequestMapping(value = "/detail")
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        ProductDetailVo productDetailVo = null;
        try{
            Request request = new ProductCacheRefreshRequest(productId,iProductService,false);
            requestAsyncProcessService.process(request);
            // 将请求扔给service异步去处理以后，就需要while(true)一会儿，在这里hang住
            // 去尝试等待前面有商品库存更新的操作，同时缓存刷新的操作，将最新的数据刷新到缓存中
            long startTime = System.currentTimeMillis();
            long endTime = 0L;
            long waitTime = 0L;
            while(true) {
//				if(waitTime > 25000) {
//					break;
//				}

                // 一般公司里面，面向用户的读请求控制在200ms就可以了
                if(waitTime > 200) {
                    break;
                }

                // 尝试去redis中读取一次商品库存的缓存数据
                String key = Const.CATEGORY_REDIS_KEY+productId.toString();
                String productString =  iRedisService.get(key);
                productDetailVo =(ProductDetailVo) JSON.parseObject(productString,ProductDetailVo.class);

                // 如果读取到了结果，那么就返回
                if(productDetailVo != null) {
                    return ServerResponse.createBySuccess(productDetailVo);
                }

                // 如果没有读取到结果，那么等待一段时间
                else {
                    Thread.sleep(20);
                    endTime = System.currentTimeMillis();
                    waitTime = endTime - startTime;
                }
            }

            // 等待超过200ms没有从缓存中获取到结果

        }catch (Exception e){

        }
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
