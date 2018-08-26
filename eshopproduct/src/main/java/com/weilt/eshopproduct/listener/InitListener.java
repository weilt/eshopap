package com.weilt.eshopproduct.listener;

import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.weilt.eshopproduct.thread.RequestProcessorThreadPool;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author weilt
 * @com.weilt.eshopproduct.listener
 * @date 2018/8/27 == 3:16
 */
public class InitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 初始化工作线程池和内存队列
        RequestProcessorThreadPool.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
