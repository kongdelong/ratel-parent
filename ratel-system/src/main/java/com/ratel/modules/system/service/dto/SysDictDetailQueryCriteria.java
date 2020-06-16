package com.ratel.modules.system.service.dto;

import com.ratel.framework.annotation.search.Query;
import lombok.Data;

@Data
public class SysDictDetailQueryCriteria {

    @Query(type = Query.Type.INNER_LIKE)
    private String label;

    @Query(propName = "name",joinName = "sysDict")
    private String dictName;
}
