package com.ratel.modules.oserver.token;

import com.ratel.modules.oserver.domain.OauthClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RefreshTokenGranter implements TokenGranter {

    private static final String GRANT_TYPE = "refresh_token";

    @Override
    public Map<String, Object> grant(OauthClient client, String grantType, Map<String, String> parameters) {

        Map<String, Object> result = new HashMap<>();
        result.put("status", 0);

        String refreshToken = parameters.get("refresh_token");

        if (!GRANT_TYPE.equals(grantType)) {
            return result;
        }

//               try {
//            Claims claims = Jwts.parser().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(refreshToken).getBody();
//            Date now = new Date();
//            Date tokenExpiration = Date.from(LocalDateTime.now().plusSeconds(client.getAccessTokenValidity()).atZone(ZoneId.systemDefault()).toInstant());
//            Date refreshTokenExpiration = Date.from(LocalDateTime.now().plusSeconds(client.getRefreshTokenValidity()).atZone(ZoneId.systemDefault()).toInstant());
//            String tokenId = UUID.randomUUID().toString();
//            claims.setId(tokenId);
//            claims.setIssuedAt(now);
//            claims.setExpiration(tokenExpiration);
//            claims.setNotBefore(now);
//
//            claims.put("jti", tokenId);
//            String newRefreshToken = Jwts.builder()
//                    .setHeaderParam("alg", "HS256")
//                    .setHeaderParam("typ", "JWT")
//                    .setClaims(claims)
//                    .signWith(keyPair.getPrivate())
//                    .compact();
//
//            claims.setId(tokenId);
//            claims.setIssuedAt(now);
//            claims.setExpiration(tokenExpiration);
//            claims.setNotBefore(now);
//            claims.remove("jti");
//            String accessToken = Jwts.builder()
//                    .setHeaderParam("alg", "HS256")
//                    .setHeaderParam("typ", "JWT")
//                    .setClaims(claims)
//                    .signWith(keyPair.getPrivate())
//                    .compact();
//
//            result.put("access_token", accessToken);
//            result.put("token_type", "bearer");
//            result.put("refresh_token", newRefreshToken);
//            result.put("expires_in", client.getAccessTokenValidity() - 1);
//            result.put("accountOpenCode", claims.get("accountOpenCode"));
//            result.put("scope", "user_info");
//            result.put("jti", tokenId);
//            result.put("status", 1);
//        } catch (Exception e) {
//            if (log.isDebugEnabled()) {
//                log.debug("exception", e);
//            }
//            throw e;
//        }


        return result;
    }
}
