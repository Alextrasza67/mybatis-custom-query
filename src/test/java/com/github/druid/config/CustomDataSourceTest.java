package com.github.druid.config;

import com.alibaba.fastjson.JSON;
import com.github.MybatisCustomQueryApplicationTests;
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
            DataSourceContext.setDataSource(DatasourceName.DATASOURCE1);
            tmpService.query(startQuery1);
            DataSourceContext.clearDataSource();
        },"thread1");
        Thread thread2 = new Thread(()->{
            DataSourceContext.setDataSource(DatasourceName.DATASOURCE1);
            tmpService.query(startQuery2);
            DataSourceContext.clearDataSource();
        },"thread1");

        thread1.start();
        Thread.sleep(500);
        log.info("change database");
//        CustomDataSource.targetDataSources.put(DatasourceName.DATASOURCE1, dataSource);
        customRoutingDataSource.afterPropertiesSet();
        thread2.start();
        Thread.sleep(500);
        startQuery1.countDown();
        Thread.sleep(500);
        startQuery2.countDown();


        Thread.sleep(10*1000);



//        2020-03-05 18:09:13.870 ERROR [thread1] c.a.d.p.DruidDataSource.validationQueryCheck(DruidDataSource:1213) - testWhileIdle is true, validationQuery not set
//        2020-03-05 18:09:13.871 INFO  [thread1] c.a.d.p.DruidDataSource.init(DruidDataSource:1010) - {dataSource-2} inited
//        2020-03-05 18:09:13.883 INFO  [thread1] c.g.d.c.CustomDataSourceTest$TmpService.query(CustomDataSourceTest:39) - datasource confirm
//        2020-03-05 18:09:14.362 INFO  [main] c.g.d.c.CustomDataSourceTest.test(CustomDataSourceTest:74) - change database
//        2020-03-05 18:09:14.365 INFO  [thread1] c.g.d.c.CustomDataSourceTest$TmpService.query(CustomDataSourceTest:39) - datasource confirm
//        2020-03-05 18:09:14.865 INFO  [thread1] c.g.d.c.CustomDataSourceTest$TmpService.query(CustomDataSourceTest:44) - start query
//        2020-03-05 18:09:14.894 DEBUG [thread1] o.a.i.l.j.BaseJdbcLogger.debug(customQuery:159) - ==>  Preparing: select * from test
//        2020-03-05 18:09:14.911 DEBUG [thread1] o.a.i.l.j.BaseJdbcLogger.debug(customQuery:159) - ==> Parameters:
//        2020-03-05 18:09:14.928 TRACE [thread1] o.a.i.l.j.BaseJdbcLogger.trace(customQuery:165) - <==    Columns: ID, NAME
//        2020-03-05 18:09:14.929 TRACE [thread1] o.a.i.l.j.BaseJdbcLogger.trace(customQuery:165) - <==        Row: 1, test1
//        2020-03-05 18:09:14.935 TRACE [thread1] o.a.i.l.j.BaseJdbcLogger.trace(customQuery:165) - <==        Row: 2, test2
//        2020-03-05 18:09:14.936 DEBUG [thread1] o.a.i.l.j.BaseJdbcLogger.debug(customQuery:159) - <==      Total: 2
//        2020-03-05 18:09:14.984 INFO  [thread1] c.g.d.c.CustomDataSourceTest$TmpService.query(CustomDataSourceTest:48) - [{"ID":1,"NAME":"test1"},{"ID":2,"NAME":"test2"}]
//        2020-03-05 18:09:14.984 INFO  [thread1] c.g.d.c.CustomDataSourceTest$TmpService.query(CustomDataSourceTest:49) - query end
//        2020-03-05 18:09:15.367 INFO  [thread1] c.g.d.c.CustomDataSourceTest$TmpService.query(CustomDataSourceTest:44) - start query
//        2020-03-05 18:09:15.367 DEBUG [thread1] o.a.i.l.j.BaseJdbcLogger.debug(customQuery:159) - ==>  Preparing: select * from test
//        2020-03-05 18:09:15.375 INFO  [thread1] o.s.b.f.x.XmlBeanDefinitionReader.loadBeanDefinitions(XmlBeanDefinitionReader:316) - Loading XML bean definitions from class path resource [org/springframework/jdbc/support/sql-error-codes.xml]
//        2020-03-05 18:09:15.435 INFO  [thread1] o.s.j.s.SQLErrorCodesFactory.<init>(SQLErrorCodesFactory:128) - SQLErrorCodes loaded: [DB2, Derby, H2, HDB, HSQL, Informix, MS-SQL, MySQL, Oracle, PostgreSQL, Sybase]
//        Exception in thread "thread1" org.springframework.jdbc.BadSqlGrammarException:
//        ### Error querying database.  Cause: org.h2.jdbc.JdbcSQLException: Table "TEST" not found; SQL statement:
//        select * from test [42102-192]

    }


}