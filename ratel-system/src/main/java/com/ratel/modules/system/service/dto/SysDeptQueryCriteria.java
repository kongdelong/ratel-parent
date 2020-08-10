package com.ratel.modules.system.service.dto;

import com.ratel.framework.annotation.search.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
public class SysDeptQueryCriteria {

    @Query(type = Query.Type.IN, propName = "id")
    private Set<String> ids;

    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    @Query(type = Query.Type.INNER_LIKE)
    private String type;

    @Query
    private Boolean enabled;

    @Query
    private String pid;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
