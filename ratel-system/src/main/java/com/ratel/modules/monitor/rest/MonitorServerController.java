package com.ratel.modules.monitor.rest;

import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.modules.monitor.domain.MonitorServer;
import com.ratel.modules.monitor.service.MonitorServerService;
import com.ratel.modules.monitor.service.dto.MonitorServerQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@Api(tags = "服务监控管理")
@RestController
@RequestMapping("/api/server")
public class MonitorServerController {

    @Autowired
    private MonitorServerService monitorServerService;

    @GetMapping
    @RatelLog("查询服务监控")
    @ApiOperation("查询服务监控")
    @PreAuthorize("@ratel.check('server:list')")
    public ResponseEntity<Object> getServers(MonitorServerQueryCriteria criteria, Pageable pageable) {
        return FormsHttpEntity.ok(monitorServerService.queryAll(criteria, pageable));
    }

    @PostMapping
    @RatelLog("新增服务监控")
    @ApiOperation("新增服务监控")
    @PreAuthorize("@ratel.check('server:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MonitorServer resources) {
        return FormsHttpEntity.ok(monitorServerService.create(resources));
    }

    @PutMapping
    @RatelLog("修改服务监控")
    @ApiOperation("修改服务监控")
    @PreAuthorize("@ratel.check('server:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MonitorServer resources) {
        monitorServerService.update(resources);
        return FormsHttpEntity.ok();
    }

    @DeleteMapping
    @RatelLog("删除服务监控")
    @ApiOperation("删除服务监控")
    @PreAuthorize("@ratel.check('server:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Integer> ids) {
        monitorServerService.delete(ids);
        return FormsHttpEntity.ok();
    }
}
