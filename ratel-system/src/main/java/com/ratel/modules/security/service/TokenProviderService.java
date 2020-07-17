package com.ratel.modules.security.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.ratel.framework.modules.cache.RatelCacheProvider;
import com.ratel.framework.utils.StringUtils;
import com.ratel.modules.security.config.SecurityProperties;
import com.ratel.modules.security.domain.vo.AuthCredentials;
import com.ratel.modules.security.domain.vo.JwtUser;
import com.ratel.modules.security.domain.vo.OnlineUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
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
    private RatelCacheProvider ratelCacheProvider;
    private static final String AUTHORITIES_KEY = "auth";
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getBase64Secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 由字符串生成加密key
    public static SecretKey generalKey(String secret) {
        // 本地的密码解码
        byte[] encodedKey = Base64.decodeBase64(secret);
        // 根据给定的字节数组使用AES加密算法构造一个密钥
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    public String createToken(Authentication authentication, String tokenId, String clientId) {
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Date now = new Date();
        Date validity = new Date(now.getTime() + properties.getTokenValidityInSeconds());
        return Jwts.builder()
                .setHeaderParam("alg", "HS512")
                .setHeaderParam("typ", "JWT")
                .claim("accountOpenCode", jwtUser.getId())
                .setIssuer(properties.getIssuer())
                .setSubject(authentication.getName())
                .setAudience(clientId)
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(validity)
                .setNotBefore(now)
                .setIssuedAt(now)
                .setId(tokenId)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

//       String accessToken = Jwts.builder()
//                .setHeaderParam("alg", "HS256")
//                .setHeaderParam("typ", "JWT")
//                .claim("accountOpenCode", userInfo.getId())
//                .setIssuer(issuer)
//                .setSubject(userInfo.getUsername())
//                .setAudience(clientId)
//                .claim("roles", userInfo.getAuthorities().stream().map(e -> e.getAuthority()).collect(Collectors.toList()))
//                .setExpiration(tokenExpiration)
//                .setNotBefore(now)
//                .setIssuedAt(now)
//                .setId(tokenId)
//                .signWith(keyPair.getPrivate())
//                .compact();

    }

    public Authentication getAuthentication(String token, OnlineUser onlineUser) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities = StringUtils.isNotBlank(claims.get(AUTHORITIES_KEY).toString()) ?
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()) : null;

        //JwtUser principal = new JwtUser(claims.getSubject(), "", authorities);
        //UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());
        UserDetails principal = onlineUser.getJwtUser();
        return new UsernamePasswordAuthenticationToken(principal, new AuthCredentials(token), authorities);
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
        long time = ratelCacheProvider.getExpire(properties.getOnlineKey() + token) * 1000;
        Date expireDate = DateUtil.offset(new Date(), DateField.MILLISECOND, (int) time);
        // 判断当前时间与过期时间的时间差
        long differ = expireDate.getTime() - new Date().getTime();
        // 如果在续期检查的范围内，则续期
        if (differ <= properties.getDetect()) {
            long renew = time + properties.getRenew();
            ratelCacheProvider.expire(properties.getOnlineKey() + token, renew, TimeUnit.MILLISECONDS);
        }
    }


}
