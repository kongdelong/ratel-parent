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
@Table(name = "sys_menu")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysMenu extends BaseUuidEntity {

    @NotBlank
    private String name;

    @Column(name = "sort")
    private Long sort = 999L;

    @Column(name = "path")
    private String path;

    private String component;

    /**
     * 类型，目录、菜单、按钮
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 权限
     */
    @Column(name = "permission")
    private String permission;

    @Column(unique = true, name = "component_name")
    private String componentName;

    private String icon;

    @Column(columnDefinition = "bit(1) default 0")
    private Boolean cache;

    @Column(columnDefinition = "bit(1) default 0")
    private Boolean hidden;

    /**
     * 上级菜单ID
     */
    @Column(name = "pid", nullable = false)
    private String pid;

    /**
     * 是否为外链 true/false
     */
    @Column(name = "i_frame")
    private Boolean iFrame;

    @ManyToMany(mappedBy = "sysMenus")
    @JsonIgnore
    private Set<SysRole> sysRoles;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SysMenu menu = (SysMenu) o;
        return Objects.equals(id, menu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Transient
    public List<SysMenu> children;
}
