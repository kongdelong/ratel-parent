package com.ratel.modules.codeditor.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.codeditor.domain.ToolScript;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolScriptRepository extends BaseRepository<ToolScript, String> {

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return /
     */
    ToolScript findByUsername(String username);

    @Query(value = "select * from tool_script where tool_script_group_id in (:groupIds) and enable='1'", nativeQuery = true)
    List<ToolScript> findByToolScriptGroupIn(@Param("groupIds") List<String> groupId);

}
