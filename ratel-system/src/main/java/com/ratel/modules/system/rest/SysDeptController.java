package com.ratel.modules.system.rest;

import cn.hutool.core.collection.CollectionUtil;
import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.framework.utils.ThrowableUtil;
import com.ratel.modules.system.config.DataScope;
import com.ratel.modules.system.domain.SysDept;
import com.ratel.modules.system.service.SysDeptService;
import com.ratel.modules.system.service.dto.SysDeptQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@Api(tags = "系统：部门管理")
@RequestMapping("/api/sys/dept")
public class SysDeptController {

    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private DataScope dataScope;

    private static final String ENTITY_NAME = "dept";

    @RatelLog("导出部门数据")
    @ApiOperation("导出部门数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@ratel.check('dept:list')")
    public void download(HttpServletResponse response, SysDeptQueryCriteria criteria) throws IOException {
        sysDeptService.download(sysDeptService.queryAll(criteria), response);
    }

    @RatelLog("查询部门")
    @ApiOperation("查询部门")
    @GetMapping
    @PreAuthorize("@ratel.check('user:list','dept:list')")
    public ResponseEntity<Object> getDepts(SysDeptQueryCriteria criteria) {
        // 数据权限
        criteria.setIds(dataScope.getDeptIds());
        List<SysDept> deptDtos = sysDeptService.queryAll(criteria);
        return FormsHttpEntity.ok(sysDeptService.buildTree(deptDtos));
    }

    @RatelLog("新增部门")
    @ApiOperation("新增部门")
    @PostMapping
    @PreAuthorize("@ratel.check('dept:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SysDept resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        if (resources.getPid() != null && !"0".equals(resources.getPid())) {
            SysDept dept = sysDeptService.findOne(resources.getPid());
            resources.setDesPid(dept.getId() + "," + dept.getDesPid());
        } else {
            resources.setDesPid("");
        }
        return FormsHttpEntity.ok(sysDeptService.create(resources));
    }

    @RatelLog("修改部门")
    @ApiOperation("修改部门")
    @PutMapping
    @PreAuthorize("@ratel.check('dept:edit')")
    public ResponseEntity<Object> update(@Validated(SysDept.Update.class) @RequestBody SysDept resources) {
        if (resources.getPid() != null && !"0".equals(resources.getPid())) {
            SysDept dept = sysDeptService.findOne(resources.getPid());
            resources.setDesPid(dept.getId() + "," + dept.getDesPid());
        } else {
            resources.setDesPid("");
        }
        sysDeptService.update(resources);
        return FormsHttpEntity.ok();
    }

    @RatelLog("删除部门")
    @ApiOperation("删除部门")
    @DeleteMapping
    @PreAuthorize("@ratel.check('dept:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<String> ids) {
        Set<SysDept> deptDtos = new HashSet<>();
        for (String id : ids) {
            List<SysDept> deptList = sysDeptService.findByPid(id);
            deptDtos.add(sysDeptService.findById(id));
            if (CollectionUtil.isNotEmpty(deptList)) {
                deptDtos = sysDeptService.getDeleteDepts(deptList, deptDtos);
            }
        }
        try {
            sysDeptService.delete(deptDtos);
        } catch (Throwable e) {
            ThrowableUtil.throwForeignKeyException(e, "所选部门中存在岗位或者角色关联，请取消关联后再试");
        }
        return FormsHttpEntity.ok();
    }
}
