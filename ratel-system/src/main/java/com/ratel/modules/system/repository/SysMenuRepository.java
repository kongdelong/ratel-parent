package com.ratel.modules.system.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.system.domain.SysMenu;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
public interface SysMenuRepository extends BaseRepository<SysMenu, String> {

    /**
     * 根据菜单名称查询
     *
     * @param name 菜单名称
     * @return /
     */
    SysMenu findByName(String name);

    /**
     * 根据组件名称查询
     *
     * @param name 组件名称
     * @return /
     */
    SysMenu findByComponentName(String name);

    /**
     * 根据菜单的 PID 查询
     *
     * @param pid /
     * @return /
     */
    List<SysMenu> findByPid(String pid);

    /**
     * 根据角色ID与菜单类型查询菜单
     *
     * @param roleIds roleIDs
     * @param type    类型
     * @return /
     */
    LinkedHashSet<SysMenu> findBySysRoles_IdInAndTypeNotOrderBySortAsc(Set<Long> roleIds, int type);
}
