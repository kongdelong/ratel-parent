package com.ratel.modules.gen.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.gen.domain.ColumnInfo;

import java.util.List;

public interface ColumnInfoRepository extends BaseRepository<ColumnInfo, Long> {

    /**
     * 查询表信息
     *
     * @param tableName 表格名
     * @return 表信息
     */
    List<ColumnInfo> findByTableNameOrderByIdAsc(String tableName);
}
