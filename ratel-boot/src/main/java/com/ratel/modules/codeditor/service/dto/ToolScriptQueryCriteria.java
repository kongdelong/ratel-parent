package com.ratel.modules.codeditor.service.dto;

import com.ratel.framework.annotation.search.Query;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
public class ToolScriptQueryCriteria implements Serializable {

    @Query
    private String id;

    @Query(propName = "id", type = Query.Type.IN, joinName = "toolScriptGroup")
    private Set<String> toolScriptGroupIds;

    @Query(blurry = "username")
    private String blurry;

    @Query
    private Boolean enabled;

    private String toolScriptGroupId;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
