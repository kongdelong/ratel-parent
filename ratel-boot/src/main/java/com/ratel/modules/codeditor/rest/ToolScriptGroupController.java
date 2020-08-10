package com.ratel.modules.codeditor.rest;

import cn.hutool.core.collection.CollectionUtil;
import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.framework.utils.ThrowableUtil;
import com.ratel.modules.codeditor.domain.ToolScriptGroup;
import com.ratel.modules.codeditor.service.ToolScriptGroupService;
import com.ratel.modules.codeditor.service.dto.ToolScriptGroupQueryCriteria;
import com.ratel.modules.system.config.DataScope;
import com.ratel.modules.system.domain.SysDept;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(tags = "系统：脚本组")
@RequestMapping("/api/tool/scriptGroup")
public class ToolScriptGroupController {

    @Autowired
    private ToolScriptGroupService toolScriptGroupService;

    @Autowired
    private DataScope dataScope;

    private static final String ENTITY_NAME = "scriptGroup";

    @GetMapping(value = "/download")
    @PreAuthorize("@ratel.check('scriptgroup:list')")
    public void download(HttpServletResponse response, ToolScriptGroupQueryCriteria criteria) throws IOException {
        toolScriptGroupService.download(toolScriptGroupService.queryAll(criteria), response);
    }

//    @RatelLog("查询部门")
//    @ApiOperation("查询部门")
//    @GetMapping
//    @PreAuthorize("@ratel.check('user:list','dept:list')")
//    public ResponseEntity<Object> getDepts(ToolScriptGroupQueryCriteria criteria) {
//        // 数据权限
//        criteria.setIds(dataScope.getDeptIds());
//        List<ToolScriptGroup> deptDtos = toolScriptGroupService.queryAll(criteria);
//        return FormsHttpEntity.ok(toolScriptGroupService.buildTree(deptDtos));
//    }

    @GetMapping(value = "/list")
    @PreAuthorize("@ratel.check('user:list','dept:list')")
    public ResponseEntity<Object> getDeptList(ToolScriptGroupQueryCriteria criteria) {
        return FormsHttpEntity.ok(toolScriptGroupService.queryAll(criteria));
    }


    @RatelLog("返回全部的部门")
    @ApiOperation("部门数据，提供数据权限，启用标志")
    @GetMapping(value = "/listTree")
    @PreAuthorize("@ratel.check('user:list','dept:list')")
    public ResponseEntity<Object> getDeptListTree(ToolScriptGroupQueryCriteria criteria) {
        // 数据权限
//        criteria.setIds(dataScope.getDeptIds());
//        criteria.setEnabled(true);
        return FormsHttpEntity.ok(toolScriptGroupService.queryAll(criteria));
    }


    @RatelLog("返回全部的部门")
    @ApiOperation("部门数据，启用标志")
    @GetMapping(value = "/listTreeNoScope")
    @PreAuthorize("@ratel.check('user:list','dept:list')")
    public ResponseEntity<Object> getDeptListTreeNoScope(ToolScriptGroupQueryCriteria criteria) {
        // 数据权限
        criteria.setEnabled(true);
        return FormsHttpEntity.ok(toolScriptGroupService.queryAll(criteria));
    }


    @RatelLog("新增部门")
    @ApiOperation("新增部门")
    @PostMapping
    @PreAuthorize("@ratel.check('dept:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ToolScriptGroup resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        if (resources.getPid() != null && !"0".equals(resources.getPid())) {
            ToolScriptGroup dept = toolScriptGroupService.findOne(resources.getPid());
            resources.setDesPid(dept.getId() + "," + dept.getDesPid());
        } else {
            resources.setDesPid("0");
        }
        return FormsHttpEntity.ok(toolScriptGroupService.create(resources));
    }

    @RatelLog("修改部门")
    @ApiOperation("修改部门")
    @PutMapping
    @PreAuthorize("@ratel.check('dept:edit')")
    public ResponseEntity<Object> update(@Validated(SysDept.Update.class) @RequestBody ToolScriptGroup resources) {
        if (resources.getPid() != null && !"0".equals(resources.getPid())) {
            ToolScriptGroup dept = toolScriptGroupService.findOne(resources.getPid());
            resources.setDesPid(dept.getId() + "," + dept.getDesPid());
        } else {
            resources.setDesPid("0");
        }
        toolScriptGroupService.update(resources);
        return FormsHttpEntity.ok();
    }

    @DeleteMapping
    @PreAuthorize("@ratel.check('dept:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<String> ids) {
        Set<ToolScriptGroup> deptDtos = new HashSet<>();
        for (String id : ids) {
            List<ToolScriptGroup> deptList = toolScriptGroupService.findByPid(id);
            deptDtos.add(toolScriptGroupService.findById(id));
            if (CollectionUtil.isNotEmpty(deptList)) {
                deptDtos = toolScriptGroupService.getDeleteDepts(deptList, deptDtos);
            }
        }
        try {
            toolScriptGroupService.delete(deptDtos);
        } catch (Throwable e) {
            ThrowableUtil.throwForeignKeyException(e, "所选部门中存在岗位或者角色关联，请取消关联后再试");
        }
        return FormsHttpEntity.ok();
    }


}
