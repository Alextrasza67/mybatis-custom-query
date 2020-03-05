package com.github.druid.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.github.druid.datasource.CustomRoutingDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CustomDataSource {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("datasource1")
    public DataSource dataSourceOne() {
        return DruidDataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public CustomRoutingDataSource customRoutingDataSource(DataSource dataSource, DataSource dataSourceOne) {
        Map<Object, Object> targetDataSources = new HashMap<>(5);
        targetDataSources.put("default", dataSource);
        targetDataSources.put("datasource1", dataSourceOne);
        CustomRoutingDataSource customRoutingDataSource = new CustomRoutingDataSource();
        customRoutingDataSource.setDefaultTargetDataSource(dataSource);
        customRoutingDataSource.setTargetDataSources(targetDataSources);
        return customRoutingDataSource;
    }


    public static DruidDataSource buildDruidDataSource(Map<String, Object> params) throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        if (params.containsKey("userName")) {
            druidDataSource.setUsername((String) params.get("userName"));
        }
        if (params.containsKey("password")) {
            druidDataSource.setPassword((String) params.get("password"));
        }
        if (params.containsKey("dbUrl")) {
            druidDataSource.setUrl((String) params.get("dbUrl"));
        }
        if (params.containsKey("driverClassName")) {
            druidDataSource.setDriverClassName((String) params.get("driverClassName"));
        }
        if (params.containsKey("initialSize")) {
            druidDataSource.setInitialSize((Integer) params.get("initialSize"));
        }
        if (params.containsKey("maxActive")) {
            druidDataSource.setMaxActive((Integer) params.get("maxActive"));
        }
        if (params.containsKey("minIdle")) {
            druidDataSource.setMinIdle((Integer) params.get("minIdle"));
        }
        if (params.containsKey("maxWait")) {
            druidDataSource.setMaxWait((Long) params.get("maxWait"));
        }
        return druidDataSource;
    }

}
