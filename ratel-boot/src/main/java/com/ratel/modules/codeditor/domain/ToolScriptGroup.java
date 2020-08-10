package com.ratel.modules.codeditor.domain;

import com.ratel.framework.domain.BaseUuidEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "tool_script_grop")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ToolScriptGroup extends BaseUuidEntity {

    @Column(name = "name", nullable = false)
    @NotBlank
    private String name;

    @Column(name = "type")
    private String type;

    @NotNull
    private Boolean enabled;

    @Column(name = "sort")
    private Long sort = 999L;

    @Transient
    public List<ToolScriptGroup> children;

    @Column(name = "pid", nullable = false)
    @NotNull
    private String pid;

    @Column(name = "des_pid")
    private String desPid;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ToolScriptGroup dept = (ToolScriptGroup) o;
        return Objects.equals(id, dept.id) &&
                Objects.equals(name, dept.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
