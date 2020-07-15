package com.ratel.modules.jsth.rest;

import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.modules.jsth.domain.MongoUserLocationDomain;
import com.ratel.modules.jsth.service.MongoUserLocationService;
import com.ratel.modules.jsth.service.MongoUserSharesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "微信用户管理")
@RestController
@RequestMapping("/api/wx/userInfo")
public class WxUserInfoController {

    @Autowired
    private MongoUserLocationService mongoUserLocationService;


    @RatelLog("查询位置")
    @ApiOperation("查询位置")
    @GetMapping(value = "location")
//    @PreAuthorize("@ratel.check('user:list','dept:list')")
    public ResponseEntity<Object> getAll() {
        return FormsHttpEntity.ok(mongoUserLocationService.getAll());
    }


    @RatelLog("更新位置")
    @ApiOperation("更新位置")
    @PostMapping(value = "location")
//    @PreAuthorize("@ratel.check('user:list','dept:list')")
    public ResponseEntity<Object> saveLocation(@RequestBody MongoUserLocationDomain resource) {
        mongoUserLocationService.saveLocation(resource);
        return FormsHttpEntity.ok();
    }


    @RatelLog("查询位置")
    @ApiOperation("查询位置")
    @GetMapping(value = "getOne")
//    @PreAuthorize("@ratel.check('user:list','dept:list')")
    public ResponseEntity<Object> getOne(@RequestParam String openId) {
        return FormsHttpEntity.ok(mongoUserLocationService.getOne(openId));
    }

    @RatelLog("查询位置")
    @ApiOperation("查询位置")
    @GetMapping(value = "getUserInfo")
//    @PreAuthorize("@ratel.check('user:list','dept:list')")
    public ResponseEntity<Object> getUserInfo() {
        return FormsHttpEntity.ok(mongoUserLocationService.getUserInfo());
    }

}
