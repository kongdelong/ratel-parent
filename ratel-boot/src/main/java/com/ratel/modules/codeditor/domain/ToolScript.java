package com.ratel.modules.codeditor.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ratel.framework.domain.BaseUuidEntity;
import com.ratel.modules.system.config.JpaConverterListJson;
import com.ratel.modules.system.domain.SysRole;
import com.ratel.modules.system.domain.SysUser;
import com.ratel.modules.system.domain.SysUserAvatar;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "tool_script")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ToolScript extends BaseUuidEntity {

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotNull
    private Boolean enabled;

    @OneToOne
    @JoinColumn(name = "tool_script_group_id")
    private ToolScriptGroup toolScriptGroup;

    @ManyToMany
    @JoinTable(name = "tool_scripts_join", joinColumns = {@JoinColumn(name = "script_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "base_id", referencedColumnName = "id")})
    private Set<ToolBaseScript> toolBaseScripts;

    @Column(name = "sort")
    private Long sort = 999L;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ToolScript user = (ToolScript) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Column(columnDefinition = "TEXT")
    private String content;
}
