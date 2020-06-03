package com.ratel.modules.system.service.dto;

import com.ratel.framework.annotation.search.Query;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
public class SysUserQueryCriteria implements Serializable {

    @Query
    private String id;

    @Query(propName = "id", type = Query.Type.IN, joinName = "sysDept")
    private Set<Long> sysDeptIds;

    @Query(blurry = "email,username,nickName")
    private String blurry;

    @Query
    private Boolean enabled;

    private String sysDeptId;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
