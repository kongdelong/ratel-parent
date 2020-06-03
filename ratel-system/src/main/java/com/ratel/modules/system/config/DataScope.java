package com.ratel.modules.system.config;

import com.ratel.framework.utils.SecurityUtils;
import com.ratel.modules.system.domain.SysDept;
import com.ratel.modules.system.domain.SysUser;
import com.ratel.modules.system.service.SysDeptService;
import com.ratel.modules.system.service.SysRoleService;
import com.ratel.modules.system.service.SysUserService;
import com.ratel.modules.system.service.dto.RoleSmallDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据权限配置
 */
@Component
public class DataScope {

    private final String[] scopeType = {"全部", "本级", "自定义"};

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysDeptService sysDeptService;


    public Set<String> getDeptIds() {

        SysUser user = sysUserService.findByName(SecurityUtils.getUsername());

        // 用于存储部门id
        Set<String> deptIds = new HashSet<>();

        // 查询用户角色
        List<RoleSmallDto> roleSet = sysRoleService.findByUsersId(user.getId());

        for (RoleSmallDto role : roleSet) {

            if (scopeType[0].equals(role.getDataScope())) {
                return new HashSet<>();
            }

            // 存储本级的数据权限
            if (scopeType[1].equals(role.getDataScope())) {
                deptIds.add(user.getSysDept().getId());
            }

            // 存储自定义的数据权限
            if (scopeType[2].equals(role.getDataScope())) {
                Set<SysDept> depts = sysDeptService.findByRoleIds(role.getId());
                for (SysDept dept : depts) {
                    deptIds.add(dept.getId());
                    List<SysDept> deptChildren = sysDeptService.findByPid(dept.getId());
                    if (deptChildren != null && deptChildren.size() != 0) {
                        deptIds.addAll(getDeptChildren(deptChildren));
                    }
                }
            }
        }
        return deptIds;
    }


    public List<String> getDeptChildren(List<SysDept> deptList) {
        List<String> list = new ArrayList<>();
        deptList.forEach(dept -> {
                    if (dept != null && dept.getEnabled()) {
                        List<SysDept> depts = sysDeptService.findByPid(dept.getId());
                        if (deptList.size() != 0) {
                            list.addAll(getDeptChildren(depts));
                        }
                        list.add(dept.getId());
                    }
                }
        );
        return list;
    }
}
