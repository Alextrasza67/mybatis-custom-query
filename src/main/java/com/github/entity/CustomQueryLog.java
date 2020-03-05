package com.github.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "core_custom_query_log")
@Setter
@Getter
public class CustomQueryLog {


    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;
    @Column(name = "sql_id")
    private Long sqlId;
    @Column(name = "create_by")
    private String createBy;
    @Column(name = "create_Time")
    private LocalDateTime createTime;

}
