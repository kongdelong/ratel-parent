package com.ratel.modules.system.rest;

import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.modules.system.domain.SysDictDetail;
import com.ratel.modules.system.service.SysDictDetailService;
import com.ratel.modules.system.service.dto.SysDictDetailQueryCriteria;
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

import java.util.HashMap;
import java.util.Map;


@RestController
@Api(tags = "系统：字典详情管理")
@RequestMapping("/api/sys/dictDetail")
public class SysDictDetailController {

    @Autowired
    private SysDictDetailService sysDictDetailService;

    private static final String ENTITY_NAME = "dictDetail";

    @RatelLog("查询字典详情")
    @ApiOperation("查询字典详情")
    @GetMapping
    public ResponseEntity<Object> getDictDetails(SysDictDetailQueryCriteria criteria,
                                                 @PageableDefault(sort = {"sort"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return FormsHttpEntity.ok(sysDictDetailService.queryAll(criteria, pageable));
    }

    @RatelLog("查询多个字典详情")
    @ApiOperation("查询多个字典详情")
    @GetMapping(value = "/map")
    public ResponseEntity<Object> getDictDetailMaps(SysDictDetailQueryCriteria criteria,
                                                    @PageableDefault(sort = {"sort"}, direction = Sort.Direction.ASC) Pageable pageable) {
        String[] names = criteria.getDictName().split(",");
        Map<String, Object> map = new HashMap<>(names.length);
        for (String name : names) {
            criteria.setDictName(name);
            map.put(name, sysDictDetailService.queryAll(criteria, pageable).getContent());
        }
        return FormsHttpEntity.ok(map);
    }

    @RatelLog("新增字典详情")
    @ApiOperation("新增字典详情")
    @PostMapping
    @PreAuthorize("@ratel.check('dict:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SysDictDetail resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        return FormsHttpEntity.ok(sysDictDetailService.create(resources));
    }

    @RatelLog("修改字典详情")
    @ApiOperation("修改字典详情")
    @PutMapping
    @PreAuthorize("@ratel.check('dict:edit')")
    public ResponseEntity<Object> update(@Validated(SysDictDetail.Update.class) @RequestBody SysDictDetail resources) {
        sysDictDetailService.update(resources);
        return FormsHttpEntity.ok();
    }

    @RatelLog("删除字典详情")
    @ApiOperation("删除字典详情")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("@ratel.check('dict:del')")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        sysDictDetailService.delete(id);
        return FormsHttpEntity.ok();
    }
}
