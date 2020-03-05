package com.github.service;

import com.github.druid.config.DataSourceContext;
import com.github.druid.config.DatasourceName;
import com.github.dto.CustomQueryDetail;
import com.github.dto.CustomQueryLogDetail;
import com.github.dto.QueryResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.github.dto.QueryResult.EMPTY_RESULT;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CustomQueryExecuteService {

    @Autowired
    private CustomQueryManageService customQueryManageService;

    @Autowired
    private QueryExecuteService queryExecuteService;



    /**
     * 运行sql
     * @param sqlId
     * @param params
     */
    public QueryResult executeSql(Long sqlId, Map<String, Object> params){
        CustomQueryDetail query = customQueryManageService.queryDetail(sqlId);
        String sql = query.getSql();
        //TODO datasource config with sql
        String datasource = (String) params.get("__datasource__");
        cleanParams(params);
        dealParamsExpression(params);

        params.put("__sql__",sql);

        List<Map<String, Object>> result = Collections.emptyList();

        //如果参数中提供datasource配置，切换使用对应数据源
        if(datasource != null){
            try {
                DataSourceContext.setDataSource(DatasourceName.valueOf(datasource));
                log.info("current database is : {}", params.get("__datasource__"));
                //此处由于需要切换数据源，需要新起事务管理，重新决定datasource
                result = queryExecuteService.execute(params);
            } catch (IllegalArgumentException e) {
            } finally {
                DataSourceContext.clearDataSource();
            }
        }else{
            result = queryExecuteService.execute(params);
        }

        cleanParams(params);

        QueryResult queryResult = formarResult(result);

        saveQueryLog(sqlId, params, sql, queryResult);

        return queryResult;
    }

    /**
     * 保存执行日志
     * @param sqlId
     * @param params
     * @param sql
     * @param queryResult
     */
    private void saveQueryLog(Long sqlId, Map<String, Object> params,
                              String sql, QueryResult queryResult) {
        CustomQueryLogDetail logDetail = new CustomQueryLogDetail();
        logDetail.setCreateBy((String) params.get("createBy"));
        logDetail.setCreateTime(LocalDateTime.now());
        logDetail.setSqlId(sqlId);
        logDetail.setSqlText(sql);
        logDetail.setParams(params);
        logDetail.setResult(queryResult);
        customQueryManageService.saveQueryLog(logDetail);
    }

    /**
     * 移除部分系统关键查询参数
     * 即形如 __aaa__ 格式
     * @param params
     */
    private void cleanParams(Map<String, Object> params) {
        params.keySet().removeIf(key -> {
            return Pattern.matches("__.*__", key);
        });
    }

    /**
     * 查询参数处理
     * @param params
     */
    private void dealParamsExpression(Map<String, Object> params) {
    }

    /**
     * 格式化返回数据
     * @param result
     * @return
     */
    private QueryResult formarResult(List<Map<String, Object>> result) {
        if(CollectionUtils.isEmpty(result)){
            return EMPTY_RESULT;
        }

        Map<String, Object> top = result.get(0);
        String[] colNames = top.keySet().toArray(new String[top.size()]);
        return QueryResult.builder()
                .colNames(colNames)
                .dataList(result)
                .size(result.size())
                .build();
    }

}
