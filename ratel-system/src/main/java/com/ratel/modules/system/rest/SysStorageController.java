package com.ratel.modules.system.rest;

import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.modules.system.domain.SysStorage;
import com.ratel.modules.system.service.SysStorageService;
import com.ratel.modules.system.service.dto.SysStorageQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(tags = "系统：本地存储管理")
@RestController
@RequestMapping("/api/sys/storage")
public class SysStorageController {

    @Autowired
    private SysStorageService sysStorageService;

    @ApiOperation("查询文件")
    @GetMapping
    @PreAuthorize("@ratel.check('storage:list')")
    public ResponseEntity<Object> getLocalStorages(SysStorageQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(sysStorageService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @RatelLog("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@ratel.check('storage:list')")
    public void download(HttpServletResponse response, SysStorageQueryCriteria criteria) throws IOException {
        sysStorageService.download(sysStorageService.queryAll(criteria), response);
    }

    @ApiOperation("上传文件")
    @PostMapping
    @PreAuthorize("@ratel.check('storage:add')")
    public ResponseEntity<Object> create(@RequestParam String name, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(sysStorageService.create(name, file));
    }

    @ApiOperation("修改文件")
    @PutMapping
    @PreAuthorize("@ratel.check('storage:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody SysStorage resources) {
        sysStorageService.update(resources);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RatelLog("多选删除")
    @DeleteMapping
    @ApiOperation("多选删除")
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        sysStorageService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
