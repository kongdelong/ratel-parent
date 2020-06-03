package com.ratel.modules.system.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.system.domain.SysDept;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SysDeptRepository extends BaseRepository<SysDept, String> {

    /**
     * 根据 PID 查询
     *
     * @param id pid
     * @return /
     */
    List<SysDept> findByPid(String id);

    /**
     * 根据ID查询名称
     *
     * @param id ID
     * @return /
     */
    @Query(value = "select name from sys_dept where id = ?1", nativeQuery = true)
    String findNameById(String id);

    /**
     * 根据角色ID 查询
     *
     * @param id 角色ID
     * @return /
     */
    Set<SysDept> findBySysRoles_Id(String id);

}
