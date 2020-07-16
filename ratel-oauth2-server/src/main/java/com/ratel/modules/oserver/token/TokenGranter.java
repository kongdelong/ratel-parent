package com.ratel.modules.oserver.token;

import com.ratel.modules.oserver.domain.OauthClient;

import java.util.Map;

public interface TokenGranter {

    Map<String, Object> grant(OauthClient client, String grantType, Map<String, String> parameters);

    default void validateGrantType(OauthClient client, String grantType) {

    }
}
