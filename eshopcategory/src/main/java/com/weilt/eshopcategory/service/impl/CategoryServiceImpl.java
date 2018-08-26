package com.weilt.eshopcategory.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.weilt.common.dto.Const;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.Category;
import com.weilt.common.redisservice.IRedisService;
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
    @Autowired
    private IRedisService iRedisService;

    //添加商品分类，商品分类不使用多线程
    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//这个分类是可用的。
        //单条数据插入，不使transaction
        int rowCount = categoryMapper.insertCategory(category);
        if (rowCount > 0) {
            //TODO 写入redis cluster,然后返回

            Category categoryItem = categoryMapper.selectByNameAndParentId(categoryName, parentId);
            //下面开始写入redis cluster
            //写入之前先获得锁，如果redis中已存在针对这个id的lock，说明正在被别的进程修改，稍等会
            if(safeUpdateToRedis(categoryItem)) {
                return ServerResponse.createBySuccess("添加分类成功!!");
            }else{
                categoryMapper.deleteById(categoryItem.getId());
            }
            return ServerResponse.createByErrorMessage("Redis故障！！");
        }
        return ServerResponse.createByErrorMessage("添加分类失败！！");
    }


    /**
     * 确保在多进程条件下，更新到redis 双写一致性方案
     * @param categoryItem
     * @return
     */
    private boolean safeUpdateToRedis(Category categoryItem){
        long startTime = System.currentTimeMillis();
        long endTime = 0L;
        long waitTime = 0L;
        boolean isPutRedisKeySuccess = true;
        if (!iRedisService.exists(Const.Redis_Lock.REDIS_LOCK_CATEGORY_KEY + categoryItem.getId().toString())) {
            while (isPutRedisKeySuccess) {

                if (waitTime > 200) {
                    break;
                }
                isPutRedisKeySuccess = updateToRedis(categoryItem);
                //如果写入不成功，等20ms
                if(!isPutRedisKeySuccess){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                endTime = System.currentTimeMillis();
                waitTime = endTime - startTime;
            }

        }else {
            while (isPutRedisKeySuccess) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (waitTime > 200) {
                    break;
                }

                if (!iRedisService.exists(Const.Redis_Lock.REDIS_LOCK_CATEGORY_KEY + categoryItem.getId().toString())) {
                    isPutRedisKeySuccess = updateToRedis(categoryItem);
                } else {
                    endTime = System.currentTimeMillis();
                    waitTime = endTime - startTime;
                }
            }
        }
        return false;
    }
    private boolean updateToRedis(Category categoryItem) {
                try {
                    //取得锁
                    iRedisService.set(Const.Redis_Lock.REDIS_LOCK_CATEGORY_KEY + categoryItem.getId().toString(), Const.Redis_Lock.GET_REDIS_LOCK);
                    iRedisService.set(Const.CATEGORY_REDIS_KEY + categoryItem.getId(), JSONObject.toJSONString(categoryItem));
                    //释放锁
                    iRedisService.del(Const.Redis_Lock.REDIS_LOCK_CATEGORY_KEY + categoryItem.getId().toString());
                    return false;

                } catch (Exception e) {
                    iRedisService.del(Const.CATEGORY_REDIS_KEY + categoryItem.getId());
                    iRedisService.del(Const.Redis_Lock.REDIS_LOCK_CATEGORY_KEY + categoryItem.getId().toString());
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    logger.info("写入失败，重试",e);
                }
        return true;
    }



    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount = categoryMapper.updateCategorySelective(category);
        if (rowCount > 0) {
            //需要更新Redis
            Category categoryItem = categoryMapper.selectById(categoryId);
            //下面开始写入redis cluster
            //写入之前先获得锁，如果redis中已存在针对这个id的lock，说明正在被别的进程修改，稍等会
            if(safeUpdateToRedis(categoryItem)) {
                return ServerResponse.createBySuccessMessage("更新品类成功");
            }
            return ServerResponse.createBySuccessMessage("更新成功，但Redis故障！！");
        }
        return ServerResponse.createByErrorMessage("更新品类失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildParallelCategory(Integer categoryId) {
        List<Category> categorieList = categoryMapper.selectCategoryChildById(categoryId);
        if (CollectionUtils.isEmpty(categorieList)){
            //这不是一个错误，所以不用ServerResponse.createErrormessage
            logger.info("未找到任何子节点");
        }
        return ServerResponse.createBySuccess(categorieList);
    }


    //查找当前目录下所有子节点及子子节点，递归，直到没有任何子节点
    //此方法功能能够实现，就是读取数据库的次数太多，不利于高并发环境下数据库的健康
    //鉴于商品目录数据量并不大，办法是有服务启动进就先将所有数据读入redis然后从redis中读取数据。
    //所有数据存入redis之前，必须先获得锁，获得之后才准将数据写入
    //没获得锁的情况下，说明别的进程在时行写入redis操作，这时候hang住，等待，等待别的进程将redis更新
    //在200ms内，如果一直获取不到锁，直接读库
    //在这里直接采用redis本身做锁，视后期情况再采用需要采用zookeeper
    //唯一的问题是redis cluster如果数据超多，有可能被LRU算法清理，这样得到的数据就不是完整的
    //所有，需要有一种机制来保证，在某个节点刷新缓存，确保数据完整性。
    //当然，如果更新数据表的设计，也许没这么麻烦，不过这种递归表的设计真是反人性。找时间再改进



    @Override
    public ServerResponse<List<Integer>> selectCategoryAndDeepCategory(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet, categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null) {
            for (Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category != null) {
            categorySet.add(category);
        }
        List<Category> categorieList = categoryMapper.selectCategoryChildById(categoryId);
        for (Category categoryItem : categorieList) {
            findChildCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }


    @Override
    public ServerResponse<List<Integer>> selectCacheCategoryAndDeepCategory(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findCacheChildCategory(categorySet, categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null) {
            for (Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    private Set<Category> findCacheChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = null;
        String redisString = iRedisService.get(Const.CATEGORY_REDIS_KEY + categoryId.toString());
        if(StringUtils.isNotBlank(redisString) ||redisString!="nil") {
            category = (Category) JSON.parseObject(redisString, Category.class);
            if (category != null) {
                categorySet.add(category);
            }
            //TODO 这里递归不好做。不过已经减少了绝大部分访库请求，先这样吧。
            List<Category> categorieList = categoryMapper.selectCategoryChildById(categoryId);
            for (Category categoryItem : categorieList) {
                findCacheChildCategory(categorySet, categoryItem.getId());
            }
        }
        return categorySet;
    }

    @Override
    public ServerResponse<String> putAllDataToRedis() {
        boolean isDelRedisKeySuccess = true;
        //确定将redis中所有与category的key全部删除
        while (isDelRedisKeySuccess) {
            try {
                iRedisService.delAllByPrefix(Const.CATEGORY_REDIS_KEY);
                isDelRedisKeySuccess = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Category> categoryList = categoryMapper.selectAllCategory();
        //这种方式效率不高，好在category中数据不多，所以可以采用这种方式，如果数据多，采购pipeline方式
        for(Category categoryItem:categoryList){
            iRedisService.set(Const.CATEGORY_REDIS_KEY+categoryItem.getId(),JSONObject.toJSONString(categoryItem));
        }
        return ServerResponse.createBySuccessMessage("初始化categroy 缓存成功!");
    }


    //按主键查找当前商品分类目录数据
    @Override
    public ServerResponse<Category> selectCategoryByPrimaryKey(Integer categoryId) {
        //从redisString中查找数据
        String redisString = iRedisService.get(Const.CATEGORY_REDIS_KEY + categoryId.toString());
        Category category = null;
        if (StringUtils.isNotBlank(redisString)) {
            category = (Category) JSON.parseObject(redisString, Category.class);
            return ServerResponse.createBySuccess("成功获取数据", category);
        } else {
            //redis中未找到，从数据库中查找
            category = categoryMapper.selectById(categoryId);
            if (category != null) {
                try {
                    //将取得的数据写入redis cluster ，为防止出错，使用try catch,出错不影响程序运行。
                    iRedisService.set(Const.CATEGORY_REDIS_KEY + category.getId().toString(), JSONObject.toJSONString(category));
                    return ServerResponse.createBySuccess("成功获取数据", category);
                } catch (Exception e) {
                    logger.info("redis中已存有此商品目录数据", e);
                }

            }
        }
        return ServerResponse.createByErrorMessage("找不到当前商品分类记录");
    }
}
