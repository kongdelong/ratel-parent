package com.ratel.modules.system.service.dto;

import com.ratel.framework.annotation.search.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 公共查询类
 */
@Data
public class SysMenuQueryCriteria {

    @Query(blurry = "name,path,component")
    private String blurry;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
