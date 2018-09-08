package com.weilt.eshopproduct.dynamic;

import org.apache.naming.factory.DataSourceLinkFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author weilt
 * @com.weilt.eshopproduct.dynamic
 * @date 2018/9/8 == 11:11
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.getDataSources();
    }
}
