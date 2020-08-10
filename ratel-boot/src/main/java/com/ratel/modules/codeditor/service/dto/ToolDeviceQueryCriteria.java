package com.ratel.modules.codeditor.service.dto;

import com.ratel.framework.annotation.search.Query;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
public class ToolDeviceQueryCriteria implements Serializable {

    @Query(blurry = "deviceName,deviceModel,deviceBrand,tag")
    private String blurry;

    @Query
    private String enable;

    @Query(type = Query.Type.IN, propName = "tag")
    private Set<String> tags;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}

