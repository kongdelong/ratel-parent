package com.ratel.modules.oserver.service;

import com.ratel.framework.service.BaseService;
import com.ratel.modules.oserver.domain.OauthClient;
import com.ratel.modules.oserver.repository.OauthClientRepository;
import com.ratel.modules.system.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@CacheConfig(cacheNames = "oauthClient")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class OauthClientService extends BaseService<SysUser, String> {

    @Autowired
    private OauthClientRepository oauthClientRepository;


    public OauthClient findByClientId(String clientId) {
        return oauthClientRepository.findByClientId(clientId);
    }

    public OauthClient findByClientIdAndClientSecret(String clientId, String clientSecret) {
        return oauthClientRepository.findByClientIdAndClientSecret(clientId, clientSecret);
    }
}
