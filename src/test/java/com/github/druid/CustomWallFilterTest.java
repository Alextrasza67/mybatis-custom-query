package com.github.druid;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.*;

@Slf4j
public class CustomWallFilterTest {

    @Test
    public void testWall() {
        String sql = "select * from A where 1=1 ";
        log.info("result is {}", WallUtils.isValidateMySql(sql));

        WallConfig wallConfig = new WallConfig();
        wallConfig.setUpdateAllow(false);
        sql = "update A set a.col1 = 1 where 1=1";
        log.info("result with config is {}", WallUtils.isValidateMySql(sql, wallConfig));

        MySqlWallProvider provider = new MySqlWallProvider(wallConfig);
        log.info("result with detail is {}", JSON.toJSONString(provider.check(sql)));

    }


}