package com.ratel.modules.oserver.service;

import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.FileUtil;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.modules.oserver.domain.OauthClient;
import com.ratel.modules.oserver.repository.OauthClientRepository;
import com.ratel.modules.oserver.service.dto.OauthClientQueryCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@CacheConfig(cacheNames = "oauthClient")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class OauthClientService extends BaseService<OauthClient, String> {

    @Autowired
    private OauthClientRepository oauthClientRepository;

    public OauthClient findByClientId(String clientId) {
        return oauthClientRepository.findByClientIdAndEnable(clientId, "1");
    }

    public OauthClient findByClientIdAndClientSecret(String clientId, String clientSecret) {
        return oauthClientRepository.findByClientIdAndClientSecretAndEnable(clientId, clientSecret, "1");
    }

    @Cacheable
    public List<OauthClient> queryAll(OauthClientQueryCriteria criteria) {
        return oauthClientRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
    }

    @Cacheable
    public Page<OauthClient> queryAll(OauthClientQueryCriteria criteria, Pageable pageable) {
        return oauthClientRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder),pageable);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public OauthClient create(OauthClient resources) {
        return oauthClientRepository.save(resources);
    }


    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void deleteByStatus(List<String> ids) {
        for (String id : ids) {
            oauthClientRepository.deleteByStatus(ids);
        }
    }

    public void download(List<OauthClient> oauthClients, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OauthClient deptDTO : oauthClients) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("客户端标示", deptDTO.getClientId());
            map.put("客户端名称", deptDTO.getApplicationName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


}
