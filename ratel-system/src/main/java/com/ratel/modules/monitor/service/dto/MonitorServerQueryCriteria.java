package com.ratel.modules.monitor.service.dto;

import com.ratel.framework.annotation.search.Query;
import lombok.Data;

@Data
public class MonitorServerQueryCriteria {

    @Query(blurry = "name,address")
    private String blurry;
}
