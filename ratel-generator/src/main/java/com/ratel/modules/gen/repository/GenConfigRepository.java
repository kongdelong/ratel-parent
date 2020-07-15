package com.ratel.modules.gen.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.gen.domain.GenConfig;

public interface GenConfigRepository extends BaseRepository<GenConfig, Long> {

    /**
     * 查询表配置
     *
     * @param tableName 表名
     * @return /
     */
    GenConfig findByTableName(String tableName);
}
