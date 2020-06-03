package com.ratel.modules.system.service;

import com.ratel.framework.exception.EntityExistException;
import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.FileUtil;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.framework.utils.StringUtils;
import com.ratel.framework.utils.ValidationUtil;
import com.ratel.modules.system.domain.SysMenu;
import com.ratel.modules.system.domain.SysRole;
import com.ratel.modules.system.domain.SysUser;
import com.ratel.modules.system.repository.SysRoleRepository;
import com.ratel.modules.system.service.dto.RoleSmallDto;
import com.ratel.modules.system.service.dto.SysRoleQueryCriteria;
import com.ratel.modules.system.service.mapstruct.RoleSmallMapstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@CacheConfig(cacheNames = "sysRole")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class SysRoleService extends BaseService<SysRole, String> {

    @Autowired
    private SysRoleRepository sysRoleRepository;

    @Autowired
    private RoleSmallMapstruct roleSmallMapstruct;

    @Cacheable
    public Object queryAll(Pageable pageable) {
        return sysRoleRepository.findAll(pageable).getContent();
    }

    @Cacheable
    public List<SysRole> queryAll(SysRoleQueryCriteria criteria) {
        return sysRoleRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
    }

    @Cacheable
    public Object queryAll(SysRoleQueryCriteria criteria, Pageable pageable) {
        Page<SysRole> page = sysRoleRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return page;
    }

    @Cacheable(key = "#p0")
    public SysRole findById(String id) {
        SysRole role = sysRoleRepository.findById(id).orElseGet(SysRole::new);
        ValidationUtil.isNull(role.getId(), "Role", "id", id);
        return role;
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public SysRole create(SysRole resources) {
        if (sysRoleRepository.findByName(resources.getName()) != null) {
            throw new EntityExistException(SysRole.class, "username", resources.getName());
        }
        return sysRoleRepository.save(resources);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(SysRole resources) {
        SysRole role = sysRoleRepository.findById(resources.getId()).orElseGet(SysRole::new);
        ValidationUtil.isNull(role.getId(), "Role", "id", resources.getId());

        SysRole role1 = sysRoleRepository.findByName(resources.getName());

        if (role1 != null && !role1.getId().equals(role.getId())) {
            throw new EntityExistException(SysRole.class, "username", resources.getName());
        }
        role1 = sysRoleRepository.findByPermission(resources.getPermission());
        if (role1 != null && !role1.getId().equals(role.getId())) {
            throw new EntityExistException(SysRole.class, "permission", resources.getPermission());
        }
        role.setName(resources.getName());
        role.setRemark(resources.getRemark());
        role.setDataScope(resources.getDataScope());
        role.setSysDepts(resources.getSysDepts());
        role.setLevel(resources.getLevel());
        role.setPermission(resources.getPermission());
        sysRoleRepository.save(role);
    }


    @CacheEvict(allEntries = true)
    public void updateMenu(SysRole resources, SysRole role) {
        role.setSysMenus(resources.getSysMenus());
        sysRoleRepository.save(role);
    }


    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void untiedMenu(String id) {
        sysRoleRepository.untiedMenu(id);
    }


    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<String> ids) {
        for (String id : ids) {
            sysRoleRepository.deleteById(id);
        }
    }


    @Cacheable(key = "'findByUsers_Id:' + #p0")
    public List<RoleSmallDto> findByUsersId(String id) {
        return roleSmallMapstruct.toDto(new ArrayList<>(sysRoleRepository.findBySysUsers_Id(id)));
    }


    @Cacheable
    public Integer findByRoles(Set<SysRole> roles) {
        Set<SysRole> roleDtos = new HashSet<>();
        for (SysRole role : roles) {
            roleDtos.add(findById(role.getId()));
        }
        return Collections.min(roleDtos.stream().map(SysRole::getLevel).collect(Collectors.toList()));
    }


    @Cacheable(key = "'loadPermissionByUser:' + #p0.username")
    public Collection<GrantedAuthority> mapToGrantedAuthorities(SysUser user) {
        Set<SysRole> roles = sysRoleRepository.findBySysUsers_Id(user.getId());
        Set<String> permissions = roles.stream().filter(role -> StringUtils.isNotBlank(role.getPermission()))
                .map(SysRole::getPermission).collect(Collectors.toSet());

        roles.stream().flatMap(role -> role.getSysMenus().stream())
                .filter(menu -> StringUtils.isNotBlank(menu.getPermission()))
                .map(SysMenu::getPermission).forEach(
                str -> {
                    String[] pms = str.split(",");
                    for (String item : pms) {
                        permissions.add(item);
                    }
                });
        return permissions.stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    public void download(List<SysRole> roles, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SysRole role : roles) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("角色名称", role.getName());
            map.put("默认权限", role.getPermission());
            map.put("角色级别", role.getLevel());
            map.put("描述", role.getRemark());
            map.put("创建日期", role.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
