package com.ratel.modules.system.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.system.domain.SysRole;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SysRoleRepository extends BaseRepository<SysRole, String> {

    /**
     * 根据名称查询
     *
     * @param name /
     * @return /
     */
    SysRole findByName(String name);

    /**
     * 根据用户ID查询
     *
     * @param id 用户ID
     * @return
     */
    Set<SysRole> findBySysUsers_Id(String id);

    /**
     * 解绑角色菜单
     *
     * @param id 菜单ID
     */
    @Modifying
    @Query(value = "delete from sys_roles_menus where menu_id = ?1", nativeQuery = true)
    void untiedMenu(String id);

    /**
     * 根据角色权限查询
     *
     * @param permission /
     * @return /
     */
    SysRole findByPermission(String permission);
}
