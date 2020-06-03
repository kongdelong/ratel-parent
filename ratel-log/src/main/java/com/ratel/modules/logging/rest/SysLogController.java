package com.ratel.modules.logging.rest;

import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.framework.utils.SecurityUtils;
import com.ratel.modules.logging.service.SysLogService;
import com.ratel.modules.logging.service.dto.SysLogQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/logs")
@Api(tags = "监控：日志管理")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @RatelLog("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check()")
    public void download(HttpServletResponse response, SysLogQueryCriteria criteria) throws IOException {
        criteria.setLogType("INFO");
        sysLogService.download(sysLogService.queryAll(criteria), response);
    }

    @RatelLog("导出错误数据")
    @ApiOperation("导出错误数据")
    @GetMapping(value = "/error/download")
    @PreAuthorize("@el.check()")
    public void errorDownload(HttpServletResponse response, SysLogQueryCriteria criteria) throws IOException {
        criteria.setLogType("ERROR");
        sysLogService.download(sysLogService.queryAll(criteria), response);
    }

    @GetMapping
    @ApiOperation("日志查询")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> getLogs(SysLogQueryCriteria criteria, Pageable pageable) {
        criteria.setLogType("INFO");
        return FormsHttpEntity.ok(sysLogService.queryAll(criteria, pageable));
    }

    @GetMapping(value = "/user")
    @ApiOperation("用户日志查询")
    public ResponseEntity<Object> getUserLogs(SysLogQueryCriteria criteria, Pageable pageable) {
        criteria.setLogType("INFO");
        criteria.setBlurry(SecurityUtils.getUsername());
        return FormsHttpEntity.ok(sysLogService.queryAllByUser(criteria, pageable));
    }

    @GetMapping(value = "/error")
    @ApiOperation("错误日志查询")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> getErrorLogs(SysLogQueryCriteria criteria, Pageable pageable) {
        criteria.setLogType("ERROR");
        return FormsHttpEntity.ok(sysLogService.queryAll(criteria, pageable));
    }

    @GetMapping(value = "/error/{id}")
    @ApiOperation("日志异常详情查询")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> getErrorLogs(@PathVariable String id) {
        return FormsHttpEntity.ok(sysLogService.findByErrDetail(id));
    }

    @DeleteMapping(value = "/del/error")
    @RatelLog("删除所有ERROR日志")
    @ApiOperation("删除所有ERROR日志")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> delAllByError() {
        sysLogService.delAllByError();
        return FormsHttpEntity.ok();
    }

    @DeleteMapping(value = "/del/info")
    @RatelLog("删除所有INFO日志")
    @ApiOperation("删除所有INFO日志")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> delAllByInfo() {
        sysLogService.delAllByInfo();
        return FormsHttpEntity.ok();
    }
}
