package com.ratel.modules.monitor.repository;

import com.ratel.framework.domain.BaseEntity;
import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.monitor.domain.MonitorServer;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitorServerRepository extends BaseRepository<MonitorServer, Integer> {
}
