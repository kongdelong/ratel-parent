package com.ratel.modules.oserver.service.dto;

import com.ratel.framework.GlobalConstant;
import com.ratel.framework.annotation.search.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
public class OauthClientQueryCriteria {

    @Query(blurry = "applicationName")
    private String blurry;

    @Query(type = Query.Type.IN, propName = "id")
    private Set<String> ids;

    @Query
    private String enable = GlobalConstant.STATUS_VALUE;//删除标志

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
