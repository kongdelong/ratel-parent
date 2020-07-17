package com.ratel.modules.oserver.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.oserver.domain.OauthClient;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthClientRepository extends BaseRepository<OauthClient, String> {

    OauthClient findByClientId(String clientId);

    OauthClient findByClientIdAndClientSecret(String clientId, String secret);
}
