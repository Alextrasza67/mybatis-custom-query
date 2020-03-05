package com.github.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.dto.CustomQueryDetail;
import com.github.dto.CustomQueryLogDetail;
import com.github.dto.QueryResult;
import com.github.entity.CustomQuery;
import com.github.entity.CustomQueryLog;
import com.github.entity.CustomQueryLogText;
import com.github.entity.CustomQueryText;
import com.github.repository.CustomQueryLogRepository;
import com.github.repository.CustomQueryLogTextRepository;
import com.github.repository.CustomQueryRepository;
import com.github.repository.CustomQueryTextRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.github.constants.CustomQueryConstants.TEXT_TYPE_PARAMS;
import static com.github.constants.CustomQueryConstants.TEXT_TYPE_SQL;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CustomQueryManageService {

    @Autowired
    private CustomQueryRepository customQueryRepository;
    @Autowired
    private CustomQueryLogRepository customQueryLogRepository;
    @Autowired
    private CustomQueryTextRepository customQueryTextRepository;
    @Autowired
    private CustomQueryLogTextRepository customQueryLogTextRepository;

    /**
     * 查询检索sql列表
     *
     * @return
     */
    public List<CustomQuery> listAllQuery(JSONObject params) {
        return customQueryRepository.findAll(new Sort(Sort.Direction.DESC, "createTime"));
    }

    private static final String[] TEXT_TYPE_TO_DEL = {TEXT_TYPE_SQL, TEXT_TYPE_PARAMS};

    /**
     * 自定义sql维护
     *
     * @param queryDetail
     */
    @Transactional(readOnly = false)
    public void saveQuery(CustomQueryDetail queryDetail) {
        log.info("start save query : {}", JSON.toJSONString(queryDetail));

        //customQuery
        CustomQuery customQuery;
        if (queryDetail.getSqlId() != null) {
            customQuery = customQueryRepository.getOne(queryDetail.getSqlId());
            if (customQuery == null) {
                throw new IllegalArgumentException("can't find data to update");
            }
            customQuery.setUpdateTime(LocalDateTime.now());
        } else {
            customQuery = new CustomQuery();
            customQuery.setCreateTime(LocalDateTime.now());
        }

        customQuery.setTitle(queryDetail.getTitle());
        customQuery.setRemark(queryDetail.getRemark());
        customQueryRepository.save(customQuery);

        //delete old text
        customQueryTextRepository.deleteBySqlIdAndTextTypeIn(customQuery.getSqlId(), TEXT_TYPE_TO_DEL);

        //customQuerySql
        customQueryTextRepository.save(CustomQueryText.builder()
                .sqlId(customQuery.getSqlId())
                .textType(TEXT_TYPE_SQL)
                .textDetail(queryDetail.getSql())
                .build());
        customQueryTextRepository.save(CustomQueryText.builder()
                .sqlId(customQuery.getSqlId())
                .textType(TEXT_TYPE_PARAMS)
                .textDetail(JSON.toJSONString(queryDetail.getParams()))
                .build());
    }

    /**
     * 详情
     *
     * @param sqlId
     * @return
     */
    public CustomQueryDetail queryDetail(Long sqlId) {
        CustomQuery customQuery = customQueryRepository.getOne(sqlId);
        CustomQueryDetail result = new CustomQueryDetail();
        BeanUtils.copyProperties(customQuery, result);
        CustomQueryText sql = customQueryTextRepository.findFirstBySqlIdAndTextType(sqlId, TEXT_TYPE_SQL);
        if (sql != null) {
            result.setSql(sql.getTextDetail());
        }
        CustomQueryText params = customQueryTextRepository.findFirstBySqlIdAndTextType(sqlId, TEXT_TYPE_PARAMS);
        if (params != null) {
            result.setParams(JSON.parseArray(params.getTextDetail()));
        }
        return result;
    }


    /**
     * 执行日志list
     */
    public List<CustomQueryLog> findQueryLogList(JSONObject params) {
        return customQueryLogRepository.findBySqlIdOrderByCreateTimeDesc(params.getLong("sqlId"));
    }

    /**
     * 日志详情
     *
     * @param logId
     * @return
     */
    public CustomQueryLogDetail queryLogDetail(Long logId) {
        CustomQueryLog log = customQueryLogRepository.getOne(logId);
        CustomQueryLogDetail result = new CustomQueryLogDetail();
        BeanUtils.copyProperties(log, result);
        CustomQueryLogText logText = customQueryLogTextRepository.getOne(logId);
        result.setSqlText(logText.getSqlText());
        result.setParams(JSON.parseObject(logText.getParams(), new TypeReference<Map<String, Object>>(){}));
        result.setResult(JSON.parseObject(logText.getResult(), new TypeReference<QueryResult>(){}));
        return result;
    }

    /**
     * 保存日志
     * @param logDetail
     */
    @Async
    @Transactional(readOnly = false, propagation= Propagation.REQUIRES_NEW)
    public void saveQueryLog(CustomQueryLogDetail logDetail){
        CustomQueryLog log = new CustomQueryLog();
        BeanUtils.copyProperties(logDetail, log);
        customQueryLogRepository.save(log);
        CustomQueryLogText logText = new CustomQueryLogText();
        logText.setLogId(log.getLogId());
        logText.setSqlText(logDetail.getSqlText());
        logText.setParams(JSON.toJSONString(logDetail.getParams()));
        logText.setResult(JSON.toJSONString(logDetail.getResult()));
        customQueryLogTextRepository.save(logText);
    }

}
