package com.github.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryResult {

    private String[] colNames;
    private List<Map<String, Object>> dataList;
    private Integer size;

    public static final QueryResult EMPTY_RESULT = new QueryResult(null, null, 0);
}
