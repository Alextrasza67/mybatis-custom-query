package com.github.druid.config;

import com.alibaba.fastjson.JSON;
import com.github.MybatisCustomQueryApplicationTests;
import com.github.druid.datasource.CustomRoutingDataSource;
import com.github.druid.datasource.DataSourceContext;
import com.github.mapper.CustomQueryMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class CustomDataSourceTest extends MybatisCustomQueryApplicationTests {


    @Autowired
    private CustomRoutingDataSource customRoutingDataSource;
    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;
    @Autowired
    @Qualifier("dataSourceOne")
    private DataSource dataSourceOne;



    @Service
    @Transactional(readOnly = true)
    public static class TmpService{
        @Autowired
        public CustomQueryMapper customQueryMapper;

        public void query(CountDownLatch countDownLatch){
            log.info("datasource confirm");
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
            }
            log.info("start query");
            Map<String, Object> params = new HashMap<>();
            params.put("sql", "select * from test");
            List<Map<String, Object>> result = customQueryMapper.customQuery(params);
            log.info(JSON.toJSONString(result));
            log.info("query end");
        }
    }

    @Autowired
    private TmpService tmpService;

    @Test
    public void test() throws Exception {
        CountDownLatch startQuery1 = new CountDownLatch(1);
        CountDownLatch startQuery2 = new CountDownLatch(1);

        Thread thread1 = new Thread(()->{
            DataSourceContext.setDataSource("datasource2");
            tmpService.query(startQuery1);
            DataSourceContext.clearDataSource();
        },"thread1");
        Thread thread2 = new Thread(()->{
            DataSourceContext.setDataSource("datasource2");
            tmpService.query(startQuery2);
            DataSourceContext.clearDataSource();
        },"thread2");

        thread1.start();
        Thread.sleep(500);
        log.info("change database");
        customRoutingDataSource.registerDataSource("datasource2", dataSourceOne);
        thread2.start();
        Thread.sleep(500);
        startQuery1.countDown();
        Thread.sleep(500);
        startQuery2.countDown();


        Thread.sleep(10*1000);



    }


}