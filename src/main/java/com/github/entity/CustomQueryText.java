package com.github.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "core_custom_query_text")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomQueryText {

    @Id
    @Column(name = "text_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long textId;
    @Column(name = "sql_id")
    private Long sqlId;
    @Column(name = "text_Type")
    private String textType;
    @Column(name = "text_Detail")
    private String textDetail;
}
