package com.ratel.modules.oserver.rest;

import cn.hutool.core.collection.CollectionUtil;
import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.framework.utils.StringUtils;
import com.ratel.framework.utils.ThrowableUtil;
import com.ratel.modules.oserver.domain.OauthClient;
import com.ratel.modules.oserver.service.OauthClientService;
import com.ratel.modules.oserver.service.dto.OauthClientQueryCriteria;
import com.ratel.modules.system.config.DataScope;
import com.ratel.modules.system.domain.SysDept;
import com.ratel.modules.system.service.SysDeptService;
import com.ratel.modules.system.service.dto.SysDeptQueryCriteria;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@Api(tags = "鉴权：客户端管理")
@RequestMapping("/api/oauth/client")
public class OauthClientController {

    @Autowired
    private OauthClientService oauthClientService;

    @RatelLog("导出客户端数据")
    @ApiOperation("导出客户端数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@ratel.check('oauthClient:list')")
    public void download(HttpServletResponse response, OauthClientQueryCriteria criteria) throws IOException {
        oauthClientService.download(oauthClientService.queryAll(criteria), response);
    }

    @RatelLog("查询客户端")
    @ApiOperation("查询客户端")
    @GetMapping
    @PreAuthorize("@ratel.check('oauthClient:list')")
    public ResponseEntity<Object> list(OauthClientQueryCriteria criteria, Pageable pageable) {
        // 数据权限
        //criteria.setIds(dataScope.getDeptIds());
        return FormsHttpEntity.ok(oauthClientService.queryAll(criteria,pageable));
    }


    @RatelLog("新增客户端配置")
    @ApiOperation("新增客户端配置")
    @PostMapping
    @PreAuthorize("@ratel.check('oauthClient:add','oauthClient:edit')")
    public ResponseEntity<Object> create(@Validated @RequestBody OauthClient resources) {

        if(StringUtils.isBlank(resources.getId())){
            resources.setClientId(UUID.randomUUID().toString().replace("-",""));
            resources.setClientSecret(UUID.randomUUID().toString().replace("-",""));
        }
        return FormsHttpEntity.ok(oauthClientService.create(resources));
    }

    @RatelLog("删除客户端配置")
    @ApiOperation("删除客户端配置")
    @DeleteMapping
    @PreAuthorize("@ratel.check('dept:del')")
    public ResponseEntity<Object> delete(@RequestBody List<String> ids) {
        oauthClientService.deleteByStatus(ids);
        return FormsHttpEntity.ok();
    }
}
