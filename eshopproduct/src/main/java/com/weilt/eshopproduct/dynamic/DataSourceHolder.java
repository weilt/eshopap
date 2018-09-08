package com.weilt.eshopproduct.dynamic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weilt
 * @com.weilt.eshopproduct.dynamic
 * @date 2018/9/8 == 11:13
 */
public class DataSourceHolder {
    private static final ThreadLocal<String> dataSources = new ThreadLocal<String>();
    private static List<String> dataSourceIds = new ArrayList<String>();

    public static void setDataSources(String customerType){
        dataSources.set(customerType);
    }
    public static String getDataSources(){
        return(String) dataSources.get();
    }

    public static void clearDataSources(){
        dataSources.remove();
    }
    public static boolean containsDataSource(String dataSourceId){
        return dataSourceIds.contains(dataSourceId);
    }
}
