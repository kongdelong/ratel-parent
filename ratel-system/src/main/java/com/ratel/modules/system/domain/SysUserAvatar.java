package com.ratel.modules.system.domain;

import cn.hutool.core.util.ObjectUtil;
import com.ratel.framework.domain.BaseUuidEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "sys_user_avatar")
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysUserAvatar extends BaseUuidEntity {

    private String realName;

    private String path;

    private String size;

    public SysUserAvatar(SysUserAvatar userAvatar, String realName, String path, String size) {
        this.id = ObjectUtil.isNotEmpty(userAvatar) ? userAvatar.getId() : null;
        this.realName = realName;
        this.path = path;
        this.size = size;
    }
}
