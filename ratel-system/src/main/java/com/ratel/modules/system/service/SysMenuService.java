package com.ratel.modules.system.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.exception.EntityExistException;
import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.FileUtil;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.framework.utils.StringUtils;
import com.ratel.framework.utils.ValidationUtil;
import com.ratel.modules.system.domain.SysMenu;
import com.ratel.modules.system.domain.vo.MenuMetaVo;
import com.ratel.modules.system.domain.vo.MenuVo;
import com.ratel.modules.system.repository.SysMenuRepository;
import com.ratel.modules.system.service.dto.RoleSmallDto;
import com.ratel.modules.system.service.dto.SysMenuQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@CacheConfig(cacheNames = "sysMenu")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class SysMenuService extends BaseService<SysMenu, Long> {
    @Autowired
    private SysMenuRepository sysMenuRepository;

    @Autowired
    private SysRoleService roleService;

    @Cacheable
    public List<SysMenu> queryAll(SysMenuQueryCriteria criteria) {
//        Sort sort = new Sort(Sort.Direction.DESC,"id");
        return sysMenuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
    }

    @Cacheable(key = "#p0")
    public SysMenu findById(String id) {
        return sysMenuRepository.findById(id).orElseGet(SysMenu::new);
    }

    public List<SysMenu> findByRoles(List<RoleSmallDto> roles) {
        Set<String> roleIds = roles.stream().map(RoleSmallDto::getId).collect(Collectors.toSet());
        LinkedHashSet<SysMenu> menus = sysMenuRepository.findBySysRoles_IdInAndTypeNotOrderBySortAsc(roleIds, 2);
        return menus.stream().collect(Collectors.toList());
    }

    @CacheEvict(allEntries = true)
    public SysMenu create(SysMenu resources) {
        if (sysMenuRepository.findByName(resources.getName()) != null) {
            throw new EntityExistException(SysMenu.class, "name", resources.getName());
        }
        if (StringUtils.isNotBlank(resources.getComponentName())) {
            if (sysMenuRepository.findByComponentName(resources.getComponentName()) != null) {
                throw new EntityExistException(SysMenu.class, "componentName", resources.getComponentName());
            }
        }
        if (resources.getIFrame()) {
            String http = "http://", https = "https://";
            if (!(resources.getPath().toLowerCase().startsWith(http) || resources.getPath().toLowerCase().startsWith(https))) {
                throw new BadRequestException("外链必须以http://或者https://开头");
            }
        }
        return sysMenuRepository.save(resources);
    }

    @CacheEvict(allEntries = true)
    public void update(SysMenu resources) {
        if (resources.getId().equals(resources.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }
        SysMenu menu = sysMenuRepository.findById(resources.getId()).orElseGet(SysMenu::new);
        ValidationUtil.isNull(menu.getId(), "Permission", "id", resources.getId());

        if (resources.getIFrame()) {
            String http = "http://", https = "https://";
            if (!(resources.getPath().toLowerCase().startsWith(http) || resources.getPath().toLowerCase().startsWith(https))) {
                throw new BadRequestException("外链必须以http://或者https://开头");
            }
        }
        SysMenu menu1 = sysMenuRepository.findByName(resources.getName());

        if (menu1 != null && !menu1.getId().equals(menu.getId())) {
            throw new EntityExistException(SysMenu.class, "name", resources.getName());
        }

        if (StringUtils.isNotBlank(resources.getComponentName())) {
            menu1 = sysMenuRepository.findByComponentName(resources.getComponentName());
            if (menu1 != null && !menu1.getId().equals(menu.getId())) {
                throw new EntityExistException(SysMenu.class, "componentName", resources.getComponentName());
            }
        }
        menu.setName(resources.getName());
        menu.setComponent(resources.getComponent());
        menu.setPath(resources.getPath());
        menu.setIcon(resources.getIcon());
        menu.setIFrame(resources.getIFrame());
        menu.setPid(resources.getPid());
        menu.setSort(resources.getSort());
        menu.setCache(resources.getCache());
        menu.setHidden(resources.getHidden());
        menu.setComponentName(resources.getComponentName());
        menu.setPermission(resources.getPermission());
        menu.setType(resources.getType());
        sysMenuRepository.save(menu);
    }

    public Set<SysMenu> getDeleteMenus(List<SysMenu> menuList, Set<SysMenu> menuSet) {
        // 递归找出待删除的菜单
        for (SysMenu menu1 : menuList) {
            menuSet.add(menu1);
            List<SysMenu> menus = sysMenuRepository.findByPid(menu1.getId());
            if (menus != null && menus.size() != 0) {
                getDeleteMenus(menus, menuSet);
            }
        }
        return menuSet;
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<SysMenu> menuSet) {
        for (SysMenu menu : menuSet) {
            roleService.untiedMenu(menu.getId());
            sysMenuRepository.deleteById(menu.getId());
        }
    }

    @Cacheable(key = "'tree'")
    public Object getMenuTree(List<SysMenu> menus) {
        List<Map<String, Object>> list = new LinkedList<>();
        menus.forEach(menu -> {
                    if (menu != null) {
                        List<SysMenu> menuList = sysMenuRepository.findByPid(menu.getId());
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("id", menu.getId());
                        map.put("label", menu.getName());
                        if (menuList != null && menuList.size() != 0) {
                            map.put("children", getMenuTree(menuList));
                        }
                        list.add(map);
                    }
                }
        );
        return list;
    }

    @Cacheable(key = "'pid:'+#p0")
    public List<SysMenu> findByPid(String pid) {
        return sysMenuRepository.findByPid(pid);
    }

    public Map<String, Object> buildTree(List<SysMenu> SysMenus) {
        List<SysMenu> trees = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        for (SysMenu sysMenu : SysMenus) {
            if (sysMenu.getPid() == 0) {
                trees.add(sysMenu);
            }
            for (SysMenu it : SysMenus) {
                if (it.getPid().equals(sysMenu.getId())) {
                    if (sysMenu.getChildren() == null) {
                        sysMenu.setChildren(new ArrayList<>());
                    }
                    sysMenu.getChildren().add(it);
                    ids.add(it.getId());
                }
            }
        }
        Map<String, Object> map = new HashMap<>(2);
        if (trees.size() == 0) {
            trees = SysMenus.stream().filter(s -> !ids.contains(s.getId())).collect(Collectors.toList());
        }
        map.put("content", trees);
        map.put("totalElements", SysMenus.size());
        return map;
    }

    public List<MenuVo> buildMenus(List<SysMenu> SysMenus) {
        List<MenuVo> list = new LinkedList<>();
        SysMenus.forEach(SysMenu -> {
                    if (SysMenu != null) {
                        List<SysMenu> SysMenuList = SysMenu.getChildren();
                        MenuVo menuVo = new MenuVo();
                        menuVo.setName(ObjectUtil.isNotEmpty(SysMenu.getComponentName()) ? SysMenu.getComponentName() : SysMenu.getName());
                        // 一级目录需要加斜杠，不然会报警告
                        menuVo.setPath(SysMenu.getPid() == 0 ? "/" + SysMenu.getPath() : SysMenu.getPath());
                        menuVo.setHidden(SysMenu.getHidden());
                        // 如果不是外链
                        if (!SysMenu.getIFrame()) {
                            if (SysMenu.getPid() == 0) {
                                menuVo.setComponent(StrUtil.isEmpty(SysMenu.getComponent()) ? "Layout" : SysMenu.getComponent());
                            } else if (!StrUtil.isEmpty(SysMenu.getComponent())) {
                                menuVo.setComponent(SysMenu.getComponent());
                            }
                        }
                        menuVo.setMeta(new MenuMetaVo(SysMenu.getName(), SysMenu.getIcon(), !SysMenu.getCache()));
                        if (SysMenuList != null && SysMenuList.size() != 0) {
                            menuVo.setAlwaysShow(true);
                            menuVo.setRedirect("noredirect");
                            menuVo.setChildren(buildMenus(SysMenuList));
                            // 处理是一级菜单并且没有子菜单的情况
                        } else if (SysMenu.getPid() == 0) {
                            MenuVo menuVo1 = new MenuVo();
                            menuVo1.setMeta(menuVo.getMeta());
                            // 非外链
                            if (!SysMenu.getIFrame()) {
                                menuVo1.setPath("index");
                                menuVo1.setName(menuVo.getName());
                                menuVo1.setComponent(menuVo.getComponent());
                            } else {
                                menuVo1.setPath(SysMenu.getPath());
                            }
                            menuVo.setName(null);
                            menuVo.setMeta(null);
                            menuVo.setComponent("Layout");
                            List<MenuVo> list1 = new ArrayList<>();
                            list1.add(menuVo1);
                            menuVo.setChildren(list1);
                        }
                        list.add(menuVo);
                    }
                }
        );
        return list;
    }

    public SysMenu findOne(String id) {
        SysMenu menu = sysMenuRepository.findById(id).orElseGet(SysMenu::new);
        ValidationUtil.isNull(menu.getId(), "SysMenu", "id", id);
        return menu;
    }

    public void download(List<SysMenu> SysMenus, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SysMenu SysMenu : SysMenus) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("菜单名称", SysMenu.getName());
            map.put("菜单类型", SysMenu.getType() == 0 ? "目录" : SysMenu.getType() == 1 ? "菜单" : "按钮");
            map.put("权限标识", SysMenu.getPermission());
            map.put("外链菜单", SysMenu.getIFrame() ? "是" : "否");
            map.put("菜单可见", SysMenu.getHidden() ? "否" : "是");
            map.put("是否缓存", SysMenu.getCache() ? "是" : "否");
            map.put("创建日期", SysMenu.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
