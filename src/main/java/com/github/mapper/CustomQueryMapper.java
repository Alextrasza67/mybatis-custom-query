package com.github.mapper;

import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustomQueryMapper {

    @SelectProvider(type=CustomQueryProvider.class,method = "customQuery")
    List<Map<String, Object>> customQuery(Map<String, Object> params);

}
