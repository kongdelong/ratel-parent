package com.ratel.modules.gen.rest;

import cn.hutool.core.util.PageUtil;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.modules.gen.domain.ColumnInfo;
import com.ratel.modules.gen.service.GenConfigService;
import com.ratel.modules.gen.service.GeneratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/gen/generator")
@Api(tags = "系统：代码生成管理")
public class GeneratorController {

    private final GeneratorService generatorService;

    private final GenConfigService genConfigService;

    @Value("${ratel.generator.enabled}")
    private Boolean generatorEnabled;

    public GeneratorController(GeneratorService generatorService, GenConfigService genConfigService) {
        this.generatorService = generatorService;
        this.genConfigService = genConfigService;
    }

    @ApiOperation("查询数据库数据")
    @GetMapping(value = "/tables/all")
    public ResponseEntity<Object> getTables() {
        return new ResponseEntity<>(generatorService.getTables(), HttpStatus.OK);
    }

    @ApiOperation("查询数据库数据")
    @GetMapping(value = "/tables")
    public ResponseEntity<Object> getTables(@RequestParam(defaultValue = "") String name, Pageable pageable) {
        int[] startEnd = PageUtil.transToStartEnd(pageable.getPageNumber() + 1, pageable.getPageSize());
        return FormsHttpEntity.ok(generatorService.getTables(name, startEnd, pageable));
    }

    @ApiOperation("查询字段数据")
    @GetMapping(value = "/columns")
    public ResponseEntity<Object> getTables(@RequestParam String tableName) {
        List<ColumnInfo> columnInfos = generatorService.getColumns(tableName);
        return FormsHttpEntity.ok(new PageImpl(columnInfos, null, columnInfos.size()));
    }

    @ApiOperation("保存字段数据")
    @PutMapping
    public ResponseEntity<HttpStatus> save(@RequestBody List<ColumnInfo> columnInfos) {
        generatorService.save(columnInfos);
        return FormsHttpEntity.ok();
    }

    @ApiOperation("同步字段数据")
    @PostMapping(value = "sync")
    public ResponseEntity<HttpStatus> sync(@RequestBody List<String> tables) {
        for (String table : tables) {
            generatorService.sync(generatorService.getColumns(table), generatorService.query(table));
        }
        return FormsHttpEntity.ok();
    }

    @ApiOperation("生成代码")
    @PostMapping(value = "/{tableName}/{type}")
    public ResponseEntity<Object> generator(@PathVariable String tableName, @PathVariable Integer type, HttpServletRequest request, HttpServletResponse response) {
        if (!generatorEnabled && type == 0) {
            throw new BadRequestException("此环境不允许生成代码，请选择预览或者下载查看！");
        }
        switch (type) {
            // 生成代码
            case 0:
                generatorService.generator(genConfigService.find(tableName), generatorService.getColumns(tableName));
                break;
            // 预览
            case 1:
                return generatorService.preview(genConfigService.find(tableName), generatorService.getColumns(tableName));
            // 打包
            case 2:
                generatorService.download(genConfigService.find(tableName), generatorService.getColumns(tableName), request, response);
                break;
            default:
                throw new BadRequestException("没有这个选项");
        }
        return FormsHttpEntity.ok();
    }
}
