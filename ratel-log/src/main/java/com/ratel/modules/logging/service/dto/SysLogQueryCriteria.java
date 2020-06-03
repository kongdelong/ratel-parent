package com.ratel.modules.logging.service.dto;

import com.ratel.framework.annotation.search.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 日志查询类
 */
@Data
public class SysLogQueryCriteria {

    @Query(blurry = "username,description,address,requestIp,method,params")
    private String blurry;

    @Query
    private String logType;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
