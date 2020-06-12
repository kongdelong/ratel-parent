package com.ratel.modules.system.service.dto;

import com.ratel.framework.annotation.search.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * 公共查询类
 */
@Data
public class SysMenuQueryCriteria {

    @Query(blurry = "name,path,component")
    private String blurry;

    @Query(type = Query.Type.IN, propName = "type")
    private Set<Integer> types;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
