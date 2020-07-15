package com.ratel.modules.gen.rest;

import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.modules.gen.domain.GenConfig;
import com.ratel.modules.gen.service.GenConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gen/genConfig")
@Api(tags = "系统：代码生成器配置管理")
public class GenConfigController {

    private final GenConfigService genConfigService;

    public GenConfigController(GenConfigService genConfigService) {
        this.genConfigService = genConfigService;
    }

    @ApiOperation("查询")
    @GetMapping(value = "/{tableName}")
    public ResponseEntity<Object> get(@PathVariable String tableName) {
        return FormsHttpEntity.ok(genConfigService.find(tableName));
    }

    @ApiOperation("修改")
    @PutMapping
    public ResponseEntity<Object> emailConfig(@Validated @RequestBody GenConfig genConfig) {
        return FormsHttpEntity.ok(genConfigService.update(genConfig.getTableName(), genConfig));
    }
}
