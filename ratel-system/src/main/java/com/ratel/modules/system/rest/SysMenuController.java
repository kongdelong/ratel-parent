package com.ratel.modules.system.rest;

import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.framework.utils.SecurityUtils;
import com.ratel.modules.system.domain.SysMenu;
import com.ratel.modules.system.domain.SysUser;
import com.ratel.modules.system.service.SysMenuService;
import com.ratel.modules.system.service.SysRoleService;
import com.ratel.modules.system.service.SysUserService;
import com.ratel.modules.system.service.dto.SysMenuQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Api(tags = "系统：菜单管理")
@RestController
@RequestMapping("/api/sys/menus")
@SuppressWarnings("unchecked")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    private static final String ENTITY_NAME = "menu";

    @RatelLog("导出菜单数据")
    @ApiOperation("导出菜单数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@ratel.check('menu:list')")
    public void download(HttpServletResponse response, SysMenuQueryCriteria criteria) throws IOException {
        sysMenuService.download(sysMenuService.queryAll(criteria), response);
    }

    @ApiOperation("获取前端所需菜单")
    @GetMapping(value = "/build")
    public ResponseEntity<Object> buildMenus() {
        SysUser user = sysUserService.findByName(SecurityUtils.getUsername());
        List<SysMenu> menuDtoList = sysMenuService.findByRoles(sysRoleService.findByUsersId(user.getId()));
//        List<SysMenu> menuDtos = (List<SysMenu>) sysMenuService.buildTree(menuDtoList).get("content");
//        return FormsHttpEntity.ok(sysMenuService.buildMenus(menuDtos));
        return FormsHttpEntity.ok(menuDtoList);
    }

    @ApiOperation("返回全部的菜单")
    @GetMapping(value = "/tree")
    @PreAuthorize("@ratel.check('menu:list','roles:list')")
    public ResponseEntity<Object> getMenuTree() {
        return FormsHttpEntity.ok(sysMenuService.getMenuTree(sysMenuService.findByPid("0")));
    }

    @ApiOperation("返回全部的菜单")
    @GetMapping(value = "/list")
    @PreAuthorize("@ratel.check('menu:list','roles:list')")
    public ResponseEntity<Object> getMenuList() {
        SysMenuQueryCriteria criteria = new SysMenuQueryCriteria();
        Set list = new HashSet();
        list.add(0);
        list.add(1);
        criteria.setTypes((Set<Integer>) list);
        return FormsHttpEntity.ok(sysMenuService.queryAll(criteria));
    }

    @RatelLog("查询菜单")
    @ApiOperation("查询菜单")
    @GetMapping
    @PreAuthorize("@ratel.check('menu:list')")
    public ResponseEntity<Object> getMenus(SysMenuQueryCriteria criteria) {
        List<SysMenu> menuDtoList = sysMenuService.queryAll(criteria);
        return FormsHttpEntity.ok(sysMenuService.buildTree(menuDtoList));
    }

    @RatelLog("新增菜单")
    @ApiOperation("新增菜单")
    @PostMapping
    @PreAuthorize("@ratel.check('menu:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SysMenu resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        return FormsHttpEntity.ok(sysMenuService.create(resources));
    }

    @RatelLog("修改菜单")
    @ApiOperation("修改菜单")
    @PutMapping
    @PreAuthorize("@ratel.check('menu:edit')")
    public ResponseEntity<Object> update(@Validated(SysMenu.Update.class) @RequestBody SysMenu resources) {
        sysMenuService.update(resources);
        return FormsHttpEntity.ok();
    }

    @RatelLog("删除菜单")
    @ApiOperation("删除菜单")
    @DeleteMapping
    @PreAuthorize("@ratel.check('menu:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<String> ids) {
        Set<SysMenu> menuSet = new HashSet<>();
        for (String id : ids) {
            List<SysMenu> menuList = sysMenuService.findByPid(id);
            menuSet.add(sysMenuService.findOne(id));
            menuSet = sysMenuService.getDeleteMenus(menuList, menuSet);
        }
        sysMenuService.delete(menuSet);
        return FormsHttpEntity.ok();
    }
}
