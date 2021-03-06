package com.ratel.modules.security.rest;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.annotation.security.AnonymousAccess;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.modules.cache.RatelCacheProvider;
import com.ratel.framework.utils.SecurityUtils;
import com.ratel.framework.utils.StringUtils;
import com.ratel.modules.security.config.SecurityProperties;
import com.ratel.modules.security.domain.vo.AuthCredentials;
import com.ratel.modules.security.domain.vo.AuthUser;
import com.ratel.modules.security.domain.vo.JwtUser;
import com.ratel.modules.security.service.AuthService;
import com.ratel.modules.security.service.OnlineUserService;
import com.ratel.modules.security.service.TokenProviderService;
import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Api(tags = "系统：系统授权接口")
public class AuthController {

    @Value("${ratel.loginCode.enableWeb}")
    private Boolean enableWeb;
    @Value("${ratel.loginCode.enableMobile}")
    private Boolean enableMobile;

    @Value("${ratel.loginCode.expiration}")
    private Long expiration;
    @Value("${ratel.rsa.private_key}")
    private String privateKey;
    @Value("${ratel.single.login:false}")
    private Boolean singleLogin;
    @Autowired
    private SecurityProperties properties;
    @Autowired
    private RatelCacheProvider ratelCacheProvider;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private OnlineUserService onlineUserService;
    @Autowired
    private TokenProviderService tokenProviderService;
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    private AuthService authService;

    @RatelLog("用户登录")
    @ApiOperation("登录授权")
    @AnonymousAccess
    @PostMapping(value = "/login")
    public ResponseEntity<Object> login(@Validated @RequestBody AuthUser authUser, HttpServletRequest request) {
        // 密码解密
        RSA rsa = new RSA(privateKey, null);
        String password = new String(rsa.decrypt(authUser.getPassword(), KeyType.PrivateKey));
        if ((authUser.getClientType().equals("mobile") && enableMobile)
                || (!authUser.getClientType().equals("mobile") && enableWeb)) {
            // 查询验证码
            String code = (String) ratelCacheProvider.get(authUser.getUuid());
            // 清除验证码
            ratelCacheProvider.del(authUser.getUuid());

            if (authUser.getClientType().equals("mobile") && StringUtils.isBlank(code)) {
                throw new BadRequestException("验证码不存在或已过期");
            }
            if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
                throw new BadRequestException("验证码错误");
            }
        }
        return ResponseEntity.ok(authService.getAuthUserMap(authUser.getUsername(), new AuthCredentials(password), request));
    }

    @ApiOperation("获取用户信息")
    @GetMapping(value = "/info")
    public ResponseEntity<Object> getUserInfo() {
        JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        return ResponseEntity.ok(jwtUser);
    }


    @AnonymousAccess
    @ApiOperation("获取验证码")
    @GetMapping(value = "/code")
    public ResponseEntity<Object> getCode() {
        // 算术类型 https://gitee.com/whvse/EasyCaptcha
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(111, 36);
        // 几位数运算，默认是两位
        captcha.setLen(2);
        // 获取运算的结果
        String result = captcha.text();
        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
        // 保存
        ratelCacheProvider.set(uuid, result, expiration, TimeUnit.MINUTES);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return ResponseEntity.ok(imgResult);
    }

    @ApiOperation("退出登录")
    @AnonymousAccess
    @DeleteMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        onlineUserService.logout(tokenProviderService.getToken(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
