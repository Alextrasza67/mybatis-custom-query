package com.github.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Setter
@Getter
public class CustomQueryLogDetail {


    private Long logId;

    private Long sqlId;
    private String createBy;
    private LocalDateTime createTime;
    private String sqlText;
    private Map<String, Object> params;
    private QueryResult result;

}
