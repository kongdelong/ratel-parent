package com.ratel.modules.oserver.token;

import com.ratel.framework.modules.system.domain.RatelUser;
import com.ratel.modules.oserver.domain.OauthClient;
import com.ratel.modules.oserver.exception.OAuth2Exception;
import com.ratel.modules.security.domain.vo.AuthCredentials;
import com.ratel.modules.security.service.TokenProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class PasswordTokenGranter implements TokenGranter {
    private static final String GRANT_TYPE = "password";

//    @Autowired
//    AuthenticationManager authenticationManager;

    @Autowired
    TokenProviderService tokenProviderService;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Override
    public Map<String, Object> grant(OauthClient client, String grantType, Map<String, String> parameters) {

        Map<String, Object> result = new HashMap<>();
        result.put("status", 0);

        String username = parameters.get("username");
        String password = parameters.get("password");
        String clientId = parameters.get("client_id");
        String scope = parameters.get("scope");

        if (!GRANT_TYPE.equals(grantType)) {
            return result;
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,  new AuthCredentials(password));
        authenticationToken.setDetails(parameters);
        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        try {
//            userAuth = authenticationManager.authenticate(userAuth);
            userAuth = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (AccountStatusException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new OAuth2Exception(ase.getMessage(), HttpStatus.UNAUTHORIZED, "invalid_request");
        } catch (BadCredentialsException e) {
            // If the username/password are wrong the spec says we should send 400/invalid grant
            throw new OAuth2Exception(e.getMessage(), HttpStatus.UNAUTHORIZED, "invalid_request");
        }
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new OAuth2Exception("Could not authenticate user: " + username, HttpStatus.UNAUTHORIZED, "invalid_request");
        }
//        Date now = new Date();
//        Date tokenExpiration = Date.from(LocalDateTime.now().plusSeconds(client.getAccessTokenValidity()).atZone(ZoneId.systemDefault()).toInstant());
//        Date refreshTokenExpiration = Date.from(LocalDateTime.now().plusSeconds(client.getAccessTokenValidity()).atZone(ZoneId.systemDefault()).toInstant());

        String tokenId = UUID.randomUUID().toString();
        String accessToken = tokenProviderService.createToken(userAuth, tokenId, clientId);
        RatelUser userInfo = (RatelUser) userAuth.getPrincipal();

//        String accessToken = Jwts.builder()
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
//
//        String refreshToken = Jwts.builder()
//                .setHeaderParam("alg", "HS256")
//                .setHeaderParam("typ", "JWT")
//                .claim("accountOpenCode", userInfo.getId())
//                .claim("jti", tokenId)
//                .setIssuer(issuer)
//                .setSubject(userInfo.getUsername())
//                .setAudience(clientId)
//                .claim("roles", userInfo.getAuthorities().stream().map(e -> e.getAuthority()).collect(Collectors.toList()))
//                .setExpiration(refreshTokenExpiration)
//                .setNotBefore(now)
//                .setIssuedAt(now)
//                .setId(UUID.randomUUID().toString())
//                .signWith(keyPair.getPrivate())
//                .compact();

        result.put("access_token", accessToken);
        result.put("token_type", "bearer");
//        result.put("refresh_token", refreshToken);
        result.put("expires_in", client.getAccessTokenValidity() - 1);
        result.put("accountOpenCode", userInfo.getId());
        result.put("scope", scope);
        result.put("jti", tokenId);
        result.put("status", 1);
        return result;
    }
}
