package com.ratel.modules.system.rest;

import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.modules.system.domain.SysDict;
import com.ratel.modules.system.service.SysDictService;
import com.ratel.modules.system.service.dto.SysDictQueryCriteria;
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

@Api(tags = "系统：字典管理")
@RestController
@RequestMapping("/api/sys/dict")
public class SysDictController {

    @Autowired
    private SysDictService sysDictService;

    private static final String ENTITY_NAME = "dict";

    @RatelLog("导出字典数据")
    @ApiOperation("导出字典数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dict:list')")
    public void download(SysDictQueryCriteria criteria, HttpServletResponse response) throws IOException {
        sysDictService.download(sysDictService.queryAll(criteria), response);
    }

    @RatelLog("查询字典")
    @ApiOperation("查询字典")
    @GetMapping(value = "/all")
    @PreAuthorize("@el.check('dict:list')")
    public ResponseEntity<Object> all() {
        return FormsHttpEntity.ok(sysDictService.queryAll(new SysDictQueryCriteria()));
    }

    @RatelLog("查询字典")
    @ApiOperation("查询字典")
    @GetMapping
    @PreAuthorize("@el.check('dict:list')")
    public ResponseEntity<Object> getDicts(SysDictQueryCriteria resources, Pageable pageable) {
        return FormsHttpEntity.ok(sysDictService.queryAll(resources, pageable));
    }

    @RatelLog("新增字典")
    @ApiOperation("新增字典")
    @PostMapping
    @PreAuthorize("@el.check('dict:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SysDict resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        return FormsHttpEntity.ok(sysDictService.create(resources));
    }

    @RatelLog("修改字典")
    @ApiOperation("修改字典")
    @PutMapping
    @PreAuthorize("@el.check('dict:edit')")
    public ResponseEntity<Object> update(@Validated(SysDict.Update.class) @RequestBody SysDict resources) {
        sysDictService.update(resources);
        return FormsHttpEntity.ok();
    }

    @RatelLog("删除字典")
    @ApiOperation("删除字典")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("@el.check('dict:del')")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        sysDictService.delete(id);
        return FormsHttpEntity.ok();
    }

//    @ApiOperation("getOne")
//    @GetMapping(value = "/getOne")
//    public ResponseEntity<Object> getOne(@RequestParam String id) {
//        sysDictService.getOne(id);
//        return FormsHttpEntity.ok();
//    }
//
//    @ApiOperation("getPage")
//    @GetMapping(value = "/getPage")
//    public ResponseEntity<Object> getPage() {
//        PageResult pageResult = sysDictService.findPage();
//        return FormsHttpEntity.ok();
//    }

}
