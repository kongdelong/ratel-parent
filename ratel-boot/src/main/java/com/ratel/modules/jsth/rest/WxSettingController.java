package com.ratel.modules.jsth.rest;

import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.modules.jsth.domain.*;
import com.ratel.modules.jsth.repository.MongoUserRepository;
import com.ratel.modules.jsth.service.*;
import com.ratel.modules.system.domain.SysUser;
import com.ratel.modules.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Api(tags = "微信用户管理")
@RestController
@RequestMapping("/api/wx/userSetting")
public class WxSettingController {

    @Autowired
    private MongoUserLocationService mongoUserLocationService;

    @Autowired
    private MongoUserTzService mongoUserTzService;


    @Autowired
    private MongoUserMbService mongoUserMbService;

    @Autowired
    private MongoUserDkService mongoUserDkService;

    @Autowired
    private MongoUserSharesService mongoUserSharesService;

    @Autowired
    private MongoUserAccountService mongoUserAccountService;

    @Autowired
    private MongoUserRepository mongoUserRepository;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private MongoEditorArticleService mongoEditorArticleService;

    @ApiOperation("查询分享")
    @GetMapping(value = "getShares")
    public ResponseEntity<Object> getShares(@RequestParam(required = false) String content, Pageable pageable) {
        return FormsHttpEntity.ok(mongoUserSharesService.getAll(content, pageable));
    }

    @ApiOperation("查询积分")
    @GetMapping(value = "getJfs")
    public ResponseEntity<Object> getJfs(Pageable pageable) {
        return FormsHttpEntity.ok(mongoUserAccountService.getAll("jf", pageable));
    }

    @ApiOperation("查询金币")
    @GetMapping(value = "getJbs")
    public ResponseEntity<Object> getJbs(Pageable pageable) {
        return FormsHttpEntity.ok(mongoUserAccountService.getAll("jb", pageable));
    }

    @ApiOperation("查询体重")
    @GetMapping(value = "getTzs")
    public ResponseEntity<Object> getTzAll() {
        return FormsHttpEntity.ok(mongoUserTzService.getAll());
    }

    @ApiOperation("保存体重")
    @PostMapping(value = "saveTz")
    public ResponseEntity<Object> saveTz(@RequestBody MongoUserTzDomain resource) {
        mongoUserTzService.save(resource);
        return FormsHttpEntity.ok();
    }

    @ApiOperation("删除体重")
    @PostMapping(value = "deleteTz")
    public ResponseEntity<Object> deleteTz(@RequestBody MongoUserTzDomain resource) {
        mongoUserTzService.delete(resource);
        return FormsHttpEntity.ok();
    }

    @ApiOperation("查询目标")
    @GetMapping(value = "getMbs")
    public ResponseEntity<Object> getMbAll() {
        return FormsHttpEntity.ok(mongoUserMbService.getAll());
    }

    @ApiOperation("保存目标")
    @PostMapping(value = "saveMb")
    public ResponseEntity<Object> saveMb(@RequestBody MongoUserMbDomain resource) {
        mongoUserMbService.save(resource);
        return FormsHttpEntity.ok();
    }

    @ApiOperation("删除目标")
    @PostMapping(value = "deleteMb")
    public ResponseEntity<Object> deleteMb(@RequestBody MongoUserMbDomain resource) {
        mongoUserMbService.delete(resource);
        return FormsHttpEntity.ok();
    }

    @ApiOperation("打卡")
    @PostMapping(value = "saveDk")
    public ResponseEntity<Object> saveDk(@RequestBody MongoUserDkDomain resource) {
        mongoUserDkService.save(resource);
        return FormsHttpEntity.ok();
    }

    @ApiOperation("打卡")
    @PostMapping(value = "jsShare")
    public ResponseEntity<Object> jsShare() {
        mongoUserDkService.jsShare();
        return FormsHttpEntity.ok();
    }

    @ApiOperation("点击激励广告")
    @PostMapping(value = "clickAd")
    public ResponseEntity<Object> clickAd(@RequestBody Map<String, String> adIds) {
        String adId = adIds.get("adId");
        Long count = mongoUserAccountService.updateJb(5l, "激励广告收益", adId);
        return FormsHttpEntity.ok(count);
    }


    @GetMapping(value = "getUsers")
    public ResponseEntity<Object> getUsers() {
        List<MongoUserDomain> list = mongoUserRepository.findAll();
        for (MongoUserDomain mongoUserDomain : list) {
            Map map = mongoUserDomain.getWxUserInfo();
            if (map.get("openid") != null && mongoUserLocationService.getOne((String) map.get("openid")) == null) {

                SysUser sysUser = new SysUser();
                sysUser.setUsername((String) map.get("openid"));
                sysUser.setNickName((String) map.get("openid"));
                sysUser.setEnabled(true);
                sysUser = sysUserService.save(sysUser);

                MongoUserLocationDomain mld = new MongoUserLocationDomain();
                mld.setOpenId((String) map.get("openid"));
                mld.setNickName((String) map.get("nickName"));
                mld.setAvatarUrl((String) map.get("avatarUrl"));
                mld.setProvince((String) map.get("province"));
                mld.setCountry((String) map.get("country"));
                mld.setUserId(sysUser.getId());
                mld.setGender(((Integer) map.get("gender")).toString());
                try {
                    if (map.get("location") != null) {
                        mld.setLatitude((Double) ((Map) map.get("location")).get("latitude"));
                        mld.setLongitude((Double) ((Map) map.get("location")).get("longitude"));
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }

                mongoUserLocationService.saveLocation(mld);
            }
        }
        return FormsHttpEntity.ok();
    }


    @ApiOperation("新增文章")
    @PostMapping(value = "articleAdd")
    public ResponseEntity<Object> articleAdd(@RequestBody MongoEditorArticleDomain resource) {
        mongoEditorArticleService.save(resource);
        return FormsHttpEntity.ok();
    }

    @ApiOperation("查询文章")
    @GetMapping(value = "getArticleList")
    public ResponseEntity<Object> getArticleList(String level, Pageable pageable) {
        return FormsHttpEntity.ok(mongoEditorArticleService.findAllByLevel(level, pageable));
    }


    @ApiOperation("查询文章")
    @GetMapping(value = "getArticles")
    public ResponseEntity<Object> getArticles(String pid, Pageable pageable) {
        return FormsHttpEntity.ok(mongoEditorArticleService.getAll(pid, pageable));
    }

    @ApiOperation("查询文章")
    @GetMapping(value = "getArticle")
    public ResponseEntity<Object> getArticle(@RequestParam String id) {
        return FormsHttpEntity.ok(mongoEditorArticleService.getArticle(id));
    }

}
