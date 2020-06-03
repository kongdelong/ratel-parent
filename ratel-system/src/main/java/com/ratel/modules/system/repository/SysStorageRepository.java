package com.ratel.modules.system.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.system.domain.SysStorage;
import org.springframework.stereotype.Repository;

@Repository
public interface SysStorageRepository extends BaseRepository<SysStorage, String> {
}
