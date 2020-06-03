package com.ratel.modules.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ratel.framework.domain.BaseUuidEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "sys_role")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysRole extends BaseUuidEntity {

    @Column(nullable = false)
    @NotBlank
    private String name;

    /**
     * 数据权限类型 全部 、 本级 、 自定义
     */
    @Column(name = "data_scope")
    private String dataScope = "本级";

    /**
     * 数值越小，级别越大
     */
    @Column(name = "level")
    private Integer level = 3;

    @Column
    private String remark;

    /**
     * 权限
     */
    @Column(name = "permission")
    private String permission;

    @JsonIgnore
    @ManyToMany(mappedBy = "sysRoles")
    private Set<SysUser> sysUsers;

    @ManyToMany
    @JoinTable(name = "sys_roles_menus", joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "menu_id", referencedColumnName = "id")})
    private Set<SysMenu> sysMenus;

    @ManyToMany
    @JoinTable(name = "sys_roles_depts", joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "dept_id", referencedColumnName = "id")})
    private Set<SysDept> sysDepts;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SysRole role = (SysRole) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Transient
    public List<SysRole> children;
}
