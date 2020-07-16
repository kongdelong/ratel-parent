package com.ratel.modules.oserver.domain;

import com.ratel.framework.domain.BaseUuidEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "oauth_client")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OauthClient extends BaseUuidEntity {
    private static final long serialVersionUID = 8626957691648832578L;
    private String clientId;
    private String applicationName;
    private String resourceIds;
    private String clientSecret;
    private String scope;
    private String authorizedGrantTypes;
    private String webServerRedirectUri;
    private String authorities;
    private int accessTokenValidity = 60 * 60 * 2;
    private int refreshTokenValidity = 60 * 60 * 24;
    private String additionalInformation;
    private String autoApprove;
    /**
     * 客户端过期时间
     */
    private LocalDateTime expirationDate;
}
