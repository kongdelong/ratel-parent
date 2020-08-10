package com.ratel.modules.codeditor.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.codeditor.domain.ToolBaseScript;
import com.ratel.modules.codeditor.domain.ToolScript;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolBaseScriptRepository extends BaseRepository<ToolBaseScript, String> {

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return /
     */
    ToolBaseScript findByUsername(String username);

}
