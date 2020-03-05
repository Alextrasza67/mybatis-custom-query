package com.github.mapper;

import com.alibaba.fastjson.JSON;
import com.github.MybatisCustomQueryApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@Slf4j
public class CustomQueryMapperTest extends MybatisCustomQueryApplicationTests {

    @Autowired
    private CustomQueryMapper customQueryMapper;

    @Test
    public void test(){
        Map<String, Object> params = new HashMap<>();
        params.put("sql", "select t.id,t.log_name,t.removed from tb_user t where t.id = #{id}");
        params.put("id", "1");
        List<Map<String, Object>> result = customQueryMapper.customQuery(params);
        log.info(JSON.toJSONString(result));
    }
}
