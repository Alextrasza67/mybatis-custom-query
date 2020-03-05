package com.github.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "core_custom_query_log_text")
@Setter
@Getter
public class CustomQueryLogText {


    @Id
    @Column(name = "log_id")
    @GenericGenerator(name = "assigned", strategy = "assigned")
    @GeneratedValue(generator = "assigned")
    private Long logId;
    @Column(name = "sql_text")
    private String sqlText;
    @Column(name = "params")
    private String params;
    @Column(name = "result")
    private String result;

}
