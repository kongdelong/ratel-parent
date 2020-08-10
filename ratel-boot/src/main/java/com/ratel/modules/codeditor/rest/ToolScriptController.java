package com.ratel.modules.codeditor.rest;

import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.framework.utils.StringUtils;
import com.ratel.modules.codeditor.domain.ToolScript;
import com.ratel.modules.codeditor.domain.ToolScriptGroup;
import com.ratel.modules.codeditor.service.ToolScriptGroupService;
import com.ratel.modules.codeditor.service.ToolScriptService;
import com.ratel.modules.codeditor.service.dto.ToolScriptGroupQueryCriteria;
import com.ratel.modules.codeditor.service.dto.ToolScriptQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Api(tags = "系统：脚本")
@RestController
@RequestMapping("/api/tool/script")
public class ToolScriptController {

    @Autowired
    private ToolScriptService toolScriptService;
    @Autowired
    private ToolScriptGroupService toolScriptGroupService;

    @GetMapping(value = "/download")
    @PreAuthorize("@ratel.check('script:list')")
    public void download(HttpServletResponse response, ToolScriptQueryCriteria criteria) throws IOException {
        toolScriptService.download(toolScriptService.queryAll(criteria), response);
    }

    @RatelLog("查询用户")
    @ApiOperation("查询用户")
    @GetMapping(value = "/getDeptsAndUsers")
    @PreAuthorize("@ratel.check('user:list','accountmsg:add')")
    public ResponseEntity<Object> getDeptsAndUsers(ToolScriptQueryCriteria criteria, Pageable pageable) {
        List<ToolScript> userDtos = toolScriptService.queryAll(criteria);
        ToolScriptGroupQueryCriteria deptCriteria = new ToolScriptGroupQueryCriteria();
        List<ToolScriptGroup> deptDtos = toolScriptGroupService.queryAll(deptCriteria);

        List<Map> list = new ArrayList<>();
        for (ToolScript userDto : userDtos) {
            Map map = new HashMap();
            map.put("value", userDto.getId());
            map.put("label", userDto.getUsername());
            map.put("type", "user");
            map.put("pid", userDto.getToolScriptGroup().getId());
            list.add(map);
        }
        for (ToolScriptGroup deptDto : deptDtos) {
            Map map = new HashMap();
            map.put("value", deptDto.getId());
            map.put("label", deptDto.getName());
            map.put("type", "dept");
            map.put("pid", deptDto.getPid());
            list.add(map);
        }

        return FormsHttpEntity.ok(list);
    }

    @RatelLog("查询用户")
    @ApiOperation("查询用户")
    @GetMapping
    @PreAuthorize("@ratel.check('user:list')")
    public ResponseEntity<Object> getUsers(ToolScriptQueryCriteria criteria, Pageable pageable) {
//        Set<String> deptSet = new HashSet<>();
//        Set<String> result = new HashSet<>();
//        if (!ObjectUtils.isEmpty(criteria.getSysDeptId())) {
//            deptSet.add(criteria.getSysDeptId());
//            deptSet.addAll(dataScope.getDeptChildren(deptService.findByPid(criteria.getSysDeptId())));
//        }
//        // 数据权限
//        Set<String> deptIds = dataScope.getDeptIds();
        // 查询条件不为空并且数据权限不为空则取交集
//        if (!CollectionUtils.isEmpty(deptIds) && !CollectionUtils.isEmpty(deptSet)) {
//            // 取交集
//            result.addAll(deptSet);
//            result.retainAll(deptIds);
//            // 若无交集，则代表无数据权限
//            criteria.setSysDeptIds(result);
//            if (result.size() == 0) {
//                return FormsHttpEntity.ok(new PageImpl(null, pageable, 0L));
//            } else {

        if (StringUtils.isNotBlank(criteria.getToolScriptGroupId())) {
            Set<String> deptIds = new HashSet<>();
            deptIds.add(criteria.getToolScriptGroupId());
            criteria.setToolScriptGroupIds(deptIds);
        }

        return FormsHttpEntity.ok(toolScriptService.queryAll(criteria, pageable));
//            }
//            // 否则取并集
//        } else {
//            result.addAll(deptSet);
//            result.addAll(deptIds);
//            criteria.setSysDeptIds(result);
//            return FormsHttpEntity.ok(toolScriptGroupService.queryAll(criteria, pageable));
//        }
    }

    @RatelLog("新增用户")
    @ApiOperation("新增用户")
    @PostMapping
    @PreAuthorize("@ratel.check('user:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ToolScript resources) {
        // checkLevel(resources);
        // 默认密码 123456
//        resources.setPassword(passwordEncoder.encode("123456"));
        return FormsHttpEntity.ok(toolScriptService.create(resources));
    }

    @RatelLog("修改用户")
    @ApiOperation("修改用户")
    @PutMapping
    @PreAuthorize("@ratel.check('user:edit')")
    public ResponseEntity<Object> update(@Validated(ToolScript.Update.class) @RequestBody ToolScript resources) {
        //checkLevel(resources);
        toolScriptService.update(resources);
        return FormsHttpEntity.ok();
    }

    @RatelLog("删除用户")
    @ApiOperation("删除用户")
    @DeleteMapping
    @PreAuthorize("@ratel.check('user:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<String> ids) {
        toolScriptService.delete(ids);
        return FormsHttpEntity.ok();
    }

}
