package com.ratel.modules.codeditor.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.codeditor.domain.ToolScriptGroup;
import com.ratel.modules.system.domain.SysDept;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ToolScriptGroupRepository extends BaseRepository<ToolScriptGroup, String> {

    /**
     * 根据 PID 查询
     *
     * @param id pid
     * @return /
     */
    List<ToolScriptGroup> findByPid(String id);

    /**
     * 根据ID查询名称
     *
     * @param id ID
     * @return /
     */
    @Query(value = "select name from tool_script_grop where id = ?1", nativeQuery = true)
    String findNameById(String id);

}
