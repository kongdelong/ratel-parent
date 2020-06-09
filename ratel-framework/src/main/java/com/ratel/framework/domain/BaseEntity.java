package com.ratel.framework.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ratel.framework.GlobalConstant;
import com.ratel.framework.domain.auditable.DefaultAuditable;
import com.ratel.framework.domain.auditable.SaveUpdateAuditListener;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Access(AccessType.FIELD)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "javassistLazyInitializer", "revisionEntity", "handler"}, ignoreUnknown = true)
@MappedSuperclass
@EntityListeners({SaveUpdateAuditListener.class})
@AuditOverrides({@AuditOverride(forClass = BaseEntity.class)})
public abstract class BaseEntity<ID extends Serializable> extends AbstractPersistableEntity<ID> implements DefaultAuditable {

    //乐观锁版本
    //@Version
    //@Column(name = "version", nullable = false)
    //private Integer version = 0;

    @Column(name = "create_user_name", length = 256, updatable = false)
    protected String createUserName = GlobalConstant.NONE_VALUE;

    @Column(name = "create_user_id", length = 256, updatable = false)
    protected String createUserId;

    @Column(name = "create_time", updatable = false)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;

    @Column(name = "update_user_name", length = 256)
    protected String updateUserName;

    @Column(name = "update_user_id", length = 256)
    protected String updateUserId;

    @Column(name = "update_time")
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;

    @Column(name = "dept_id")
    protected String deptId;

    //	@MetaData(value = "数据隔离域", comments = "类似Windows域的概念，进行用户数据隔离")
    @Column(name = "data_domain", length = 100, updatable = false)
    protected String dataDomain;

    //	@MetaData(value = "系统备注说明", comments = "预留一个通用的系统备注字段，业务根据需要合理使用")
    @Column(name = "description", length = 1000)
    protected String description;

    @Column(name = "enable", length = 1)
    protected String enable = GlobalConstant.STATUS_VALUE;

    private static final String[] PROPERTY_LIST = new String[]{"id", "version"};

    public String[] retriveCommonProperties() {
        return PROPERTY_LIST;
    }

    @Override
    @Transient
    public String getDisplay() {
        return "[" + getId() + "]" + this.getClass().getSimpleName();
    }

    public @interface Update {
    }
}
