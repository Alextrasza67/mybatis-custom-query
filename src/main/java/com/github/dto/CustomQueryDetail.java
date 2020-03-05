package com.github.dto;

import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CustomQueryDetail {


    private Long sqlId;
    private String title;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String sql;
    private JSONArray params;
}
