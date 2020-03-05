package com.github.service;

import com.alibaba.fastjson.JSONObject;
import com.github.druid.config.CustomDataSource;
import com.github.druid.datasource.CustomRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@Slf4j
public class RegisterDataSourceService {

    @Autowired
    private CustomRoutingDataSource customRoutingDataSource;

    public void registerDataSource(JSONObject jsonObject) throws SQLException {
        customRoutingDataSource.registerDataSource(jsonObject.get("dataSourceKey"),
                CustomDataSource.buildDruidDataSource(jsonObject));
    }
}
