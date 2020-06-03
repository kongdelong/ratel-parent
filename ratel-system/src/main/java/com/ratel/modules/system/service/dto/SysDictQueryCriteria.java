package com.ratel.modules.system.service.dto;

import com.ratel.framework.annotation.search.Query;
import lombok.Data;

@Data
public class SysDictQueryCriteria {

    @Query(blurry = "name,remark")
    private String blurry;
}
