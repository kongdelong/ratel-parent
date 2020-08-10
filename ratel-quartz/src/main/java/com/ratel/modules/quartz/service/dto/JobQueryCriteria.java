package com.ratel.modules.quartz.service.dto;

import com.ratel.framework.annotation.search.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class JobQueryCriteria {

    @Query(type = Query.Type.INNER_LIKE)
    private String jobName;

    @Query
    private Boolean isSuccess;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
