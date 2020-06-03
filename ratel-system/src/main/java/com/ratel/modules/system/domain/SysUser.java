package com.ratel.modules.system.domain;

import com.ratel.framework.domain.BaseUuidEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "sys_user")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysUser extends BaseUuidEntity {

    @NotBlank
    @Column(unique = true)
    private String username;

    /**
     * 用户昵称
     */
    @NotBlank
    private String nickName;

    /**
     * 性别
     */
    private String sex;

    @OneToOne
    @JoinColumn(name = "avatar_id")
    private SysUserAvatar sysUserAvatar;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String phone;

    @NotNull
    private Boolean enabled;

    private String password;

    @Column(name = "last_password_reset_time")
    private Date lastPasswordResetTime;

    @ManyToMany
    @JoinTable(name = "sys_users_roles", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<SysRole> sysRoles;

    @OneToOne
    @JoinColumn(name = "sys_dept_id")
    private SysDept sysDept;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SysUser user = (SysUser) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
