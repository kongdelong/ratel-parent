package com.ratel.modules.security.config;

import com.ratel.framework.utils.SpringContextHolder;
import com.ratel.modules.security.domain.vo.OnlineUser;
import com.ratel.modules.security.service.OnlineUserService;
import com.ratel.modules.security.service.TokenProviderService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class TokenFilter extends GenericFilterBean {

    private TokenProviderService tokenProviderService;

    public TokenFilter(TokenProviderService tokenProviderService) {
        this.tokenProviderService = tokenProviderService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String token = resolveToken(httpServletRequest);
        String requestRri = httpServletRequest.getRequestURI();

        SecurityProperties properties = SpringContextHolder.getBean(SecurityProperties.class);
        OnlineUserService onlineUserService = SpringContextHolder.getBean(OnlineUserService.class);

        try {
            if (StringUtils.hasText(token) && tokenProviderService.validateToken(token)) {
                Claims tokenBody = tokenProviderService.getJwtBody(token);
                OnlineUser onlineUser = onlineUserService.getOne(properties.getOnlineKey() + tokenBody.getId());
                if (onlineUser == null) {
                    onlineUser = onlineUserService.save(authentication, tokenProviderService.getJwtUser(token), token, (HttpServletRequest) servletRequest);
//                    httpServletResponse.setStatus(401);
//                    return;
                }
                authentication = tokenProviderService.getAuthentication(tokenBody, token, onlineUser);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                tokenProviderService.checkRenewal(tokenBody.getId()); //续期
            } else {
                log.debug("no valid JWT token found, uri: {}", requestRri);
            }
        } catch (ExpiredJwtException e) { //token 解析错误
            httpServletResponse.setStatus(401);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String resolveToken(HttpServletRequest request) {
        SecurityProperties properties = SpringContextHolder.getBean(SecurityProperties.class);
        String bearerToken = request.getHeader(properties.getHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(properties.getTokenStartWith())) {
            return bearerToken.substring(7);
        }
        return null;
    }


}
