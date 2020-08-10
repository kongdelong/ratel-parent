package com.ratel.modules.codeditor.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ratel.framework.domain.BaseUuidEntity;
import com.ratel.modules.system.domain.SysRole;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "tool_base_script")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ToolBaseScript extends BaseUuidEntity {

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotNull
    private Boolean enabled;

    @Column(name = "sort")
    private Long sort = 999L;


    @JsonIgnore
    @ManyToMany(mappedBy = "toolBaseScripts")
    private Set<ToolScript> toolScripts;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ToolBaseScript user = (ToolBaseScript) o;
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
