package com.ratel.modules.system.rest;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.ratel.framework.GlobalConstant;
import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.framework.utils.SecurityUtils;
import com.ratel.modules.system.config.DataScope;
import com.ratel.modules.system.domain.SysDept;
import com.ratel.modules.system.domain.SysUser;
import com.ratel.modules.system.domain.SysVerificationCode;
import com.ratel.modules.system.domain.vo.UserPassVo;
import com.ratel.modules.system.service.SysDeptService;
import com.ratel.modules.system.service.SysRoleService;
import com.ratel.modules.system.service.SysUserService;
import com.ratel.modules.system.service.SysVerificationCodeService;
import com.ratel.modules.system.service.dto.RoleSmallDto;
import com.ratel.modules.system.service.dto.SysDeptQueryCriteria;
import com.ratel.modules.system.service.dto.SysUserQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "系统：用户管理")
@RestController
@RequestMapping("/api/sys/users")
public class SysUserController {

    @Value("${rsa.private_key}")
    private String privateKey;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private DataScope dataScope;
    @Autowired
    private SysDeptService deptService;
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysVerificationCodeService sysVerificationCodeService;


    @RatelLog("导出用户数据")
    @ApiOperation("导出用户数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@ratel.check('user:list')")
    public void download(HttpServletResponse response, SysUserQueryCriteria criteria) throws IOException {
        sysUserService.download(sysUserService.queryAll(criteria), response);
    }

    @RatelLog("查询用户")
    @ApiOperation("查询用户")
    @GetMapping(value = "/getDeptsAndUsers")
    @PreAuthorize("@ratel.check('user:list','accountmsg:add')")
    public ResponseEntity<Object> getDeptsAndUsers(SysUserQueryCriteria criteria, Pageable pageable) {
        Set<String> result = new HashSet<>();
        Set<String> deptIds = dataScope.getDeptIds();
        result.addAll(deptIds);
        criteria.setSysDeptIds(result);
        List<SysUser> userDtos = sysUserService.queryAll(criteria);
        SysDeptQueryCriteria deptCriteria = new SysDeptQueryCriteria();
        deptCriteria.setIds(dataScope.getDeptIds());
        List<SysDept> deptDtos = deptService.queryAll(deptCriteria);

        List<Map> list = new ArrayList<>();
        for (SysUser userDto : userDtos) {
            Map map = new HashMap();
            map.put("value", userDto.getId());
            map.put("label", userDto.getNickName());
            map.put("type", "user");
            map.put("pid", userDto.getSysDept().getId());
            list.add(map);
        }
        for (SysDept deptDto : deptDtos) {
            Map map = new HashMap();
            map.put("value", deptDto.getId());
            map.put("label", deptDto.getName());
            map.put("type", "dept");
            map.put("pid", deptDto.getPid());
            list.add(map);
        }

        return FormsHttpEntity.ok(list);
    }

    @RatelLog("查询用户")
    @ApiOperation("查询用户")
    @GetMapping
    @PreAuthorize("@ratel.check('user:list')")
    public ResponseEntity<Object> getUsers(SysUserQueryCriteria criteria, Pageable pageable) {
        Set<String> deptSet = new HashSet<>();
        Set<String> result = new HashSet<>();
        if (!ObjectUtils.isEmpty(criteria.getSysDeptId())) {
            deptSet.add(criteria.getSysDeptId());
            deptSet.addAll(dataScope.getDeptChildren(deptService.findByPid(criteria.getSysDeptId())));
        }
        // 数据权限
        Set<String> deptIds = dataScope.getDeptIds();
        // 查询条件不为空并且数据权限不为空则取交集
        if (!CollectionUtils.isEmpty(deptIds) && !CollectionUtils.isEmpty(deptSet)) {
            // 取交集
            result.addAll(deptSet);
            result.retainAll(deptIds);
            // 若无交集，则代表无数据权限
            criteria.setSysDeptIds(result);
            if (result.size() == 0) {
                return FormsHttpEntity.ok(new PageImpl(null, pageable, 0L));
            } else {
                return FormsHttpEntity.ok(sysUserService.queryAll(criteria, pageable));
            }
            // 否则取并集
        } else {
            result.addAll(deptSet);
            result.addAll(deptIds);
            criteria.setSysDeptIds(result);
            return FormsHttpEntity.ok(sysUserService.queryAll(criteria, pageable));
        }
    }

    @RatelLog("新增用户")
    @ApiOperation("新增用户")
    @PostMapping
    @PreAuthorize("@ratel.check('user:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SysUser resources) {
        checkLevel(resources);
        // 默认密码 123456
        resources.setPassword(passwordEncoder.encode("123456"));
        return FormsHttpEntity.ok(sysUserService.create(resources));
    }

    @RatelLog("修改用户")
    @ApiOperation("修改用户")
    @PutMapping
    @PreAuthorize("@ratel.check('user:edit')")
    public ResponseEntity<Object> update(@Validated(SysUser.Update.class) @RequestBody SysUser resources) {
        checkLevel(resources);
        sysUserService.update(resources);
        return FormsHttpEntity.ok();
    }

    @RatelLog("修改用户：个人中心")
    @ApiOperation("修改用户：个人中心")
    @PutMapping(value = "center")
    public ResponseEntity<Object> center(@Validated(SysUser.Update.class) @RequestBody SysUser resources) {
        SysUser userDto = sysUserService.findByName(SecurityUtils.getUsername());
        if (!resources.getId().equals(userDto.getId())) {
            throw new BadRequestException("不能修改他人资料");
        }
        sysUserService.updateCenter(resources);
        return FormsHttpEntity.ok();
    }

    @RatelLog("删除用户")
    @ApiOperation("删除用户")
    @DeleteMapping
    @PreAuthorize("@ratel.check('user:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<String> ids) {
        SysUser user = sysUserService.findByName(SecurityUtils.getUsername());
        for (String id : ids) {
            Integer currentLevel = Collections.min(roleService.findByUsersId(user.getId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
            Integer optLevel = Collections.min(roleService.findByUsersId(id).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
            if (currentLevel > optLevel) {
                throw new BadRequestException("角色权限不足，不能删除：" + sysUserService.findByName(SecurityUtils.getUsername()).getUsername());
            }
        }
        sysUserService.delete(ids);
        return FormsHttpEntity.ok();
    }

    @ApiOperation("修改密码")
    @PostMapping(value = "/updatePass")
    public ResponseEntity<Object> updatePass(@RequestBody UserPassVo passVo) {
        // 密码解密
        RSA rsa = new RSA(privateKey, null);
        String oldPass = new String(rsa.decrypt(passVo.getOldPass(), KeyType.PrivateKey));
        String newPass = new String(rsa.decrypt(passVo.getNewPass(), KeyType.PrivateKey));
        SysUser user = sysUserService.findByName(SecurityUtils.getUsername());
        if (!passwordEncoder.matches(oldPass, user.getPassword())) {
            throw new BadRequestException("修改失败，旧密码错误");
        }
        if (passwordEncoder.matches(newPass, user.getPassword())) {
            throw new BadRequestException("新密码不能与旧密码相同");
        }
        sysUserService.updatePass(user.getUsername(), passwordEncoder.encode(newPass));
        return FormsHttpEntity.ok();
    }

    @ApiOperation("修改头像")
    @PostMapping(value = "/updateAvatar")
    public ResponseEntity<Object> updateAvatar(@RequestParam MultipartFile file) {
        return FormsHttpEntity.ok(sysUserService.updateAvatar(file));
    }

    @RatelLog("修改邮箱")
    @ApiOperation("修改邮箱")
    @PostMapping(value = "/updateEmail/{code}")
    public ResponseEntity<Object> updateEmail(@PathVariable String code, @RequestBody SysUser user) {
        // 密码解密
        RSA rsa = new RSA(privateKey, null);
        String password = new String(rsa.decrypt(user.getPassword(), KeyType.PrivateKey));
        SysUser userDto = sysUserService.findByName(SecurityUtils.getUsername());
        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            throw new BadRequestException("密码错误");
        }
        SysVerificationCode verificationCode = new SysVerificationCode(code, GlobalConstant.RESET_MAIL, "email", user.getEmail());
        sysVerificationCodeService.validated(verificationCode);
        sysUserService.updateEmail(userDto.getUsername(), user.getEmail());
        return FormsHttpEntity.ok();
    }

    /**
     * 如果当前用户的角色级别低于创建用户的角色级别，则抛出权限不足的错误
     *
     * @param resources /
     */
    private void checkLevel(SysUser resources) {
        SysUser user = sysUserService.findByName(SecurityUtils.getUsername());
        Integer currentLevel = Collections.min(roleService.findByUsersId(user.getId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
        Integer optLevel = roleService.findByRoles(resources.getSysRoles());
        if (currentLevel > optLevel) {
            throw new BadRequestException("角色权限不足");
        }
    }
}
