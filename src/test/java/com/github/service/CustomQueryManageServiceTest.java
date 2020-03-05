package com.github.service;

import com.alibaba.fastjson.JSON;
import com.github.MybatisCustomQueryApplicationTests;
import com.github.mapper.CustomQueryMapper;
import com.github.repository.CustomQueryLogRepository;
import com.github.repository.CustomQueryRepository;
import com.github.repository.CustomQueryTextRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@Slf4j
@Transactional(readOnly = true)
public class CustomQueryManageServiceTest extends MybatisCustomQueryApplicationTests {


    @Autowired
    private CustomQueryRepository customQueryRepository;
    @Autowired
    private CustomQueryLogRepository customQueryLogRepository;
    @Autowired
    private CustomQueryTextRepository customQueryTextRepository;

    @Test
    public void testJpa(){
        log.info("customQuery size : {}",customQueryRepository.count());
        log.info("customQueryLog size : {}",customQueryLogRepository.count());
        log.info("customQueryText size : {}",customQueryTextRepository.count());
    }



    @Autowired
    private CustomQueryMapper customQueryMapper;

    @Test
    public void testMybatis(){
        Map<String, Object> params = new HashMap<>();
        params.put("sql", "select count(1) as size from core_custom_query t ");
        List<Map<String, Object>> result = customQueryMapper.customQuery(params);
        log.info(JSON.toJSONString(result));
    }
}
