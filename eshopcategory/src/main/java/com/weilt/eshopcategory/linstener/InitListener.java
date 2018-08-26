package com.weilt.eshopcategory.linstener;

import com.weilt.common.dto.Const;
import com.weilt.common.redisservice.IRedisService;
import com.weilt.eshopcategory.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author weilt
 * @com.weilt.eshopcategory.linstener
 * @date 2018/8/25 == 9:50
 */
@WebListener
public class InitListener implements ServletContextListener {
    @Autowired
    private ICategoryService iCategoryService;
    @Autowired
    private IRedisService iRedisService;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //服务启动时先将所有数据写入redis
        //TODO 可能会有问题,如果redis cluster 中数据太多，数据被redis 的LRU算法清理掉之后，怎么办？
        //加锁

        iCategoryService.putAllDataToRedis();

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
