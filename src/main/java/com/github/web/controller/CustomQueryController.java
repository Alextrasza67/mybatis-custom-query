package com.github.web.controller;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.dto.CustomQueryDetail;
import com.github.dto.CustomQueryLogDetail;
import com.github.dto.QueryResult;
import com.github.entity.CustomQuery;
import com.github.entity.CustomQueryLog;
import com.github.service.CustomQueryExecuteService;
import com.github.service.CustomQueryManageService;
import com.github.service.RegisterDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class CustomQueryController {

    @Autowired
    private CustomQueryManageService customQueryManageService;
    @Autowired
    private CustomQueryExecuteService customQueryExecuteService;


    @PostMapping("/listAllQuery")
    public List<CustomQuery> listAllQuery(@RequestBody JSONObject params) {
        return customQueryManageService.listAllQuery(params);
    }

    @PostMapping("/saveQuery")
    public void saveQuery(@RequestBody JSONObject jsonObject) {
        customQueryManageService.saveQuery(jsonObject.toJavaObject(new TypeReference<CustomQueryDetail>() {
        }));
    }

    @PostMapping("/queryDetail")
    public CustomQueryDetail queryDetail(@RequestBody JSONObject jsonObject) {
        return customQueryManageService.queryDetail(jsonObject.getLong("sqlId"));
    }


    @PostMapping("/findQueryLogList")
    public List<CustomQueryLog> findQueryLogList(@RequestBody JSONObject params) {
        return customQueryManageService.findQueryLogList(params);
    }

    @PostMapping("/queryLogDetail")
    public CustomQueryLogDetail queryLogDetail(@RequestBody JSONObject jsonObject) {
        return customQueryManageService.queryLogDetail(jsonObject.getLong("logId"));
    }


    @PostMapping("/executeSql")
    public QueryResult executeSql(@RequestBody JSONObject jsonObject) {
        Long sqlId = jsonObject.getLong("sqlId");
        Map<String, Object> params = jsonObject.getObject("params", new TypeReference<Map<String, Object>>() {
        });
        QueryResult queryResult = customQueryExecuteService.executeSql(sqlId, params);
        return queryResult;
    }


    @Autowired
    private RegisterDataSourceService registerDataSourceService;

    @PostMapping("/registerDataSource")
    public String registerDataSource(@RequestBody JSONObject jsonObject) {
        registerDataSourceService.registerDataSource(jsonObject);
        return "success";
    }


}
