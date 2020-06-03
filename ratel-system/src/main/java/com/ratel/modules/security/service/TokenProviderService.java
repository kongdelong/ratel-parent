package com.ratel.modules.security.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.ratel.framework.cache.RatelCache;
import com.ratel.modules.security.config.SecurityProperties;
import com.ratel.modules.security.domain.vo.JwtUser;
import com.ratel.modules.security.domain.vo.OnlineUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProviderService implements InitializingBean {

    @Autowired
    private SecurityProperties properties;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private RatelCache ratelCache;
    private static final String AUTHORITIES_KEY = "auth";
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getBase64Secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + properties.getTokenValidityInSeconds());

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token, OnlineUser onlineUser) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        //JwtUser principal = new JwtUser(claims.getSubject(), "", authorities);
//        UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());
        UserDetails principal = onlineUser.getJwtUser();
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }


    public JwtUser getJwtUser(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

//        Collection<? extends GrantedAuthority> authorities =
//                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());
        //JwtUser principal = new JwtUser(claims.getSubject(), "", authorities);
        UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());
        return (JwtUser) principal;
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
            e.printStackTrace();
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            e.printStackTrace();
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            e.printStackTrace();
        }
        return false;
    }

    public String getToken(HttpServletRequest request) {
        final String requestHeader = request.getHeader(properties.getHeader());
        if (requestHeader != null && requestHeader.startsWith(properties.getTokenStartWith())) {
            return requestHeader.substring(7);
        }
        return null;
    }

    /**
     * @param token 需要检查的token
     */
    public void checkRenewal(String token) {
        // 判断是否续期token,计算token的过期时间
        long time = ratelCache.getExpire(properties.getOnlineKey() + token) * 1000;
        Date expireDate = DateUtil.offset(new Date(), DateField.MILLISECOND, (int) time);
        // 判断当前时间与过期时间的时间差
        long differ = expireDate.getTime() - new Date().getTime();
        // 如果在续期检查的范围内，则续期
        if (differ <= properties.getDetect()) {
            long renew = time + properties.getRenew();
            ratelCache.expire(properties.getOnlineKey() + token, renew, TimeUnit.MILLISECONDS);
        }
    }
}
