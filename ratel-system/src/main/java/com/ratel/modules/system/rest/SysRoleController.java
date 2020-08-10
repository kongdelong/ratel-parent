package com.ratel.modules.system.rest;

import cn.hutool.core.lang.Dict;
import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.framework.utils.SecurityUtils;
import com.ratel.framework.utils.ThrowableUtil;
import com.ratel.modules.system.domain.SysRole;
import com.ratel.modules.system.domain.SysUser;
import com.ratel.modules.system.service.SysRoleService;
import com.ratel.modules.system.service.SysUserService;
import com.ratel.modules.system.service.dto.RoleSmallDto;
import com.ratel.modules.system.service.dto.SysRoleQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Api(tags = "系统：角色管理")
@RestController
@RequestMapping("/api/sys/roles")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserService sysUserService;

    private static final String ENTITY_NAME = "sysRole";


    @ApiOperation("获取单个role")
    @GetMapping(value = "/{id}")
    @PreAuthorize("@ratel.check('roles:list')")
    public ResponseEntity<Object> getRoles(@PathVariable String id) {
        return FormsHttpEntity.ok(sysRoleService.findById(id));
    }

    @RatelLog("导出角色数据")
    @ApiOperation("导出角色数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@ratel.check('role:list')")
    public void download(HttpServletResponse response, SysRoleQueryCriteria criteria) throws IOException {
        sysRoleService.download(sysRoleService.queryAll(criteria), response);
    }

    @ApiOperation("返回全部的角色")
    @GetMapping(value = "/all")
    @PreAuthorize("@ratel.check('roles:list','user:add','user:edit')")
    public ResponseEntity<Object> getAll(@PageableDefault(value = 2000, sort = {"level"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return FormsHttpEntity.ok(sysRoleService.queryAll(pageable));
    }

    @RatelLog("查询角色")
    @ApiOperation("查询角色")
    @GetMapping
    @PreAuthorize("@ratel.check('roles:list')")
    public ResponseEntity<Object> getRoles(SysRoleQueryCriteria criteria, @PageableDefault(sort = {"level"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return FormsHttpEntity.ok(sysRoleService.queryAll(criteria, pageable));
    }

    @ApiOperation("获取用户级别")
    @GetMapping(value = "/level")
    public ResponseEntity<Object> getLevel() {
        return FormsHttpEntity.ok(Dict.create().set("level", getLevels(null)));
    }

    @RatelLog("新增角色")
    @ApiOperation("新增角色")
    @PostMapping
    @PreAuthorize("@ratel.check('roles:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SysRole resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        getLevels(resources.getLevel());
        return FormsHttpEntity.ok(sysRoleService.create(resources));
    }

    @RatelLog("修改角色")
    @ApiOperation("修改角色")
    @PutMapping
    @PreAuthorize("@ratel.check('roles:edit')")
    public ResponseEntity<Object> update(@Validated(SysRole.Update.class) @RequestBody SysRole resources) {
        getLevels(resources.getLevel());
        sysRoleService.update(resources);
        return FormsHttpEntity.ok();
    }

    @RatelLog("修改角色菜单")
    @ApiOperation("修改角色菜单")
    @PutMapping(value = "/menu")
    @PreAuthorize("@ratel.check('roles:edit')")
    public ResponseEntity<Object> updateMenu(@RequestBody SysRole resources) {
        SysRole role = sysRoleService.findById(resources.getId());
        getLevels(role.getLevel());
        sysRoleService.updateMenu(resources, role);
        return FormsHttpEntity.ok();
    }

    @RatelLog("删除角色")
    @ApiOperation("删除角色")
    @DeleteMapping
    @PreAuthorize("@ratel.check('roles:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<String> ids) {
        for (String id : ids) {
            SysRole role = sysRoleService.findById(id);
            getLevels(role.getLevel());
        }
        try {
            sysRoleService.delete(ids);
        } catch (Throwable e) {
            ThrowableUtil.throwForeignKeyException(e, "所选角色存在用户关联，请取消关联后再试");
        }
        return FormsHttpEntity.ok();
    }

    /**
     * 获取用户的角色级别
     *
     * @return /
     */
    private int getLevels(Integer level) {
        SysUser user = sysUserService.findByName(SecurityUtils.getUsername());
        List<Integer> levels = sysRoleService.findByUsersId(user.getId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList());
        int min = Collections.min(levels);
        if (level != null) {
            if (level < min) {
                throw new BadRequestException("权限不足，你的角色级别：" + min + "，低于操作的角色级别：" + level);
            }
        }
        return min;
    }
}
