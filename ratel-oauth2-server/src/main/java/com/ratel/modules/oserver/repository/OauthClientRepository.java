package com.ratel.modules.oserver.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.oserver.domain.OauthClient;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OauthClientRepository extends BaseRepository<OauthClient, String> {

    OauthClient findByClientIdAndEnable(String clientId,String enable);

    OauthClient findByClientIdAndClientSecretAndEnable(String clientId, String secret,String enable);

    @Modifying
    @Query(value = "update OauthClient set enable='0' where id IN(:ids)")
    void deleteByStatus(@Param("ids") List ids);
}
