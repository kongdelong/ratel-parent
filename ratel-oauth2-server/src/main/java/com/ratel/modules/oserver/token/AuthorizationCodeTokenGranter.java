package com.ratel.modules.oserver.token;

import com.ratel.framework.modules.cache.RatelCacheProvider;
import com.ratel.framework.modules.system.domain.RatelUser;
import com.ratel.modules.oserver.domain.OauthClient;
import com.ratel.modules.oserver.exception.OAuth2Exception;
import com.ratel.modules.security.config.SecurityProperties;
import com.ratel.modules.security.domain.vo.OnlineUser;
import com.ratel.modules.security.service.TokenProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AuthorizationCodeTokenGranter implements TokenGranter {
    private static final String GRANT_TYPE = "authorization_code";
//    @Autowired
//    AuthenticationManager authenticationManager;

    @Autowired
    TokenProviderService tokenProviderService;

    @Autowired
    private SecurityProperties properties;

    @Autowired
    private RatelCacheProvider ratelCacheProvider;


//    @Autowired
//    private RatelCacheProvider ratelCacheProvider;

    @Autowired
    CacheManager cacheManager;

    @Override
    public Map<String, Object> grant(OauthClient client, String grantType, Map<String, String> parameters) {

        Map<String, Object> result = new HashMap<>();
        result.put("status", 0);

        String authorizationCode = parameters.get("code");
        String redirectUri = parameters.get("redirect_uri");
        String clientId = parameters.get("client_id");
        String scope = parameters.get("scope");
        if (authorizationCode == null) {
            throw new OAuth2Exception("An authorization code must be supplied.", HttpStatus.BAD_REQUEST, "invalid_request");
        }

        OnlineUser user = (OnlineUser) ratelCacheProvider.get(properties.getOnlineKey() + authorizationCode);

        if (user != null) {
            Authentication userAuth = user.getAuthentication();
            RatelUser userInfo = (RatelUser) userAuth.getPrincipal();

            String tokenId = UUID.randomUUID().toString();
            String accessToken = tokenProviderService.createToken(userAuth, tokenId, clientId);


//            Date now = new Date();
//            Date tokenExpiration = Date.from(LocalDateTime.now().plusSeconds(client.getAccessTokenValidity()).atZone(ZoneId.systemDefault()).toInstant());
//            Date refreshTokenExpiration = Date.from(LocalDateTime.now().plusSeconds(client.getRefreshTokenValidity()).atZone(ZoneId.systemDefault()).toInstant());
//            String tokenId = UUID.randomUUID().toString();
//            String accessToken = Jwts.builder()
//                    .setHeaderParam("alg", "HS256")
//                    .setHeaderParam("typ", "JWT")
//                    .claim("accountOpenCode", userInfo.getId())
//                    .setIssuer(issuer)
//                    .setSubject(userInfo.getUsername())
//                    .setAudience(clientId)
//                    .claim("roles", userInfo.getAuthorities().stream().map(e -> e.getAuthority()).collect(Collectors.toList()))
//                    .setExpiration(tokenExpiration)
//                    .setNotBefore(now)
//                    .setIssuedAt(now)
//                    .setId(tokenId)
//                    .signWith(keyPair.getPrivate())
//                    .compact();
//
//            String refreshToken = Jwts.builder()
//                    .setHeaderParam("alg", "HS256")
//                    .setHeaderParam("typ", "JWT")
//                    .claim("accountOpenCode", userInfo.getId())
//                    .claim("jti", tokenId)
//                    .setIssuer(issuer)
//                    .setSubject(userInfo.getUsername())
//                    .setAudience(clientId)
//                    .claim("roles", userInfo.getAuthorities().stream().map(e -> e.getAuthority()).collect(Collectors.toList()))
//                    .setExpiration(refreshTokenExpiration)
//                    .setNotBefore(now)
//                    .setIssuedAt(now)
//                    .setId(UUID.randomUUID().toString())
//                    .signWith(keyPair.getPrivate())
//                    .compact();

//            cacheManager.getCache(CachesEnum.Oauth2AuthorizationCodeCache.name()).evictIfPresent(authorizationCode);

            result.put("access_token", accessToken);
            result.put("token_type", "bearer");
//            result.put("refresh_token", refreshToken);
            result.put("expires_in", client.getAccessTokenValidity() - 1);
            result.put("accountOpenCode", userInfo.getId());
            result.put("scope", scope);
            result.put("jti", tokenId);
            result.put("status", 1);
            return result;
        } else {
            throw new OAuth2Exception("An authorization code must be supplied.", HttpStatus.BAD_REQUEST, "invalid_request");
        }
    }
}
