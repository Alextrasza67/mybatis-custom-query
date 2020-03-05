package com.github.service;

import com.github.mapper.CustomQueryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class QueryExecuteService {


    @Autowired
    private CustomQueryMapper customQueryMapper;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> execute(Map<String, Object> params){
        List<Map<String, Object>> result = customQueryMapper.customQuery(params);
        return result;
    }
}
