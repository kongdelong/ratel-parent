package com.ratel.modules.system.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.system.domain.SysDict;
import org.springframework.stereotype.Repository;

@Repository
public interface SysDictRepository extends BaseRepository<SysDict, String> {

    SysDict findByName(String name);
}
