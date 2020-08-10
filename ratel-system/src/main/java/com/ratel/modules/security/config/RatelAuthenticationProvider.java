package com.ratel.modules.security.config;

import com.ratel.framework.utils.StringUtils;
import com.ratel.modules.security.domain.vo.AuthCredentials;
import com.ratel.modules.security.domain.vo.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Slf4j
public class RatelAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 自定义验证方式
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        AuthCredentials authCredentials = (AuthCredentials) authentication.getCredentials();
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        if (StringUtils.isBlank(authCredentials.getAuthtype())) {
            throw new DisabledException("Wrong password.");
        }
        if (AuthCredentials.QYWX.equals(authCredentials.getAuthtype())) {
            log.warn("企业微信登录，不需要校验密码！");
        } else {
//            String password = passwordEncoder.encode(authCredentials.getPassword());

            if (!passwordEncoder.matches(authCredentials.getPassword(), user.getPassword())) {
                throw new DisabledException("用户名密码错误！");
            }
        }
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, authentication, authorities);
    }

    @Override
    public boolean supports(Class<?> arg0) {
        return true;
    }
}
