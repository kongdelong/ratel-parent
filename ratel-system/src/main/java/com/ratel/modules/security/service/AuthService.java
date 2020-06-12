package com.ratel.modules.security.service;

import com.ratel.modules.security.config.SecurityProperties;
import com.ratel.modules.security.domain.vo.AuthCredentials;
import com.ratel.modules.security.domain.vo.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuthService {

    @Value("${ratel.single.login:false}")
    private Boolean singleLogin;

    @Autowired
    private SecurityProperties properties;
    @Autowired
    private OnlineUserService onlineUserService;
    @Autowired
    private TokenProviderService tokenProviderService;
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    public Map<String, Object> getAuthUserMap(String username, AuthCredentials authCredentials, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, authCredentials);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成令牌
        String token = tokenProviderService.createToken(authentication);
        final JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        // 保存在线信息
        onlineUserService.save(jwtUser, token, request);
        // 返回 token 与 用户信息
        Map<String, Object> authInfo = new HashMap<String, Object>(2) {{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUser);
        }};
        if (singleLogin) {
            //踢掉之前已经登录的token
            onlineUserService.checkLoginOnUser(username, token);
        }
        return authInfo;
    }


    public String getToken() {
        return null;
    }
}
