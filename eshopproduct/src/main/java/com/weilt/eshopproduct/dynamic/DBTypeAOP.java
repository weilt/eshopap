package com.weilt.eshopproduct.dynamic;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author weilt
 * @com.weilt.eshopproduct.dynamic
 * @date 2018/9/8 == 11:24
 */
@Component
@Order(-10) //保证该AOP在@Transactional之前执行
@Aspect
public class DBTypeAOP {

    @Pointcut("@annotation(TargetDataSource)")
    public void dbType(){
    }

    @Before("dbType()")
    public void before(JoinPoint joinPoint) throws Throwable{
        System.out.println("------------before----------------");
        TargetDataSource targetDataSource = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(TargetDataSource.class);
        String dsId = targetDataSource.value();
        if(!DataSourceHolder.containsDataSource(dsId)){
            System.err.println("============数据源[{}]不存在，使用默认数据源=======================: {}"+targetDataSource.value()+joinPoint.getSignature());
        }else {
            System.out.println("Use datasource: {}>{}"+targetDataSource.value()+joinPoint.getSignature());
            DataSourceHolder.setDataSources(targetDataSource.value());
        }
    }
    @After("dbType()")
    public void after(JoinPoint joinPoint){
        System.out.println("-----------after--------------");
        TargetDataSource targetDataSource = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(TargetDataSource.class);
        System.out.println("Revert datasource:{}>{}"+targetDataSource.value()+joinPoint.getSignature());
        DataSourceHolder.clearDataSources();
    }


}
