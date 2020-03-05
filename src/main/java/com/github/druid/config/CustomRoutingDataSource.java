package com.github.druid.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class CustomRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContext.getDataSource();
    }
}
