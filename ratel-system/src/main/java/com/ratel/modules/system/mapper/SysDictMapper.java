package com.ratel.modules.system.mapper;

import cn.hutool.db.PageResult;
import com.ratel.modules.system.domain.SysDict;
import org.springframework.stereotype.Component;

@Component
public interface SysDictMapper {
    SysDict getOne(String id);

    PageResult findPage();
}
