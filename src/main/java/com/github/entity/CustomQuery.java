package com.github.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "core_custom_query")
@Setter
@Getter
public class CustomQuery {


    @Id
    @Column(name = "sql_Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sqlId;
    @Column(name = "title")
    private String title;
    @Column(name = "remark")
    private String remark;
    @Column(name = "create_Time")
    private LocalDateTime createTime;
    @Column(name = "update_Time")
    private LocalDateTime updateTime;
}
