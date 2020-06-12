package com.ratel.modules.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "sys_dept")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysDept extends BaseUuidEntity {

    @Column(name = "name", nullable = false)
    @NotBlank
    private String name;

    @NotNull
    private Boolean enabled;

    @Column(name = "sort")
    private Long sort = 999L;

    @Transient
    public List<SysDept> children;

    @Column(name = "pid", nullable = false)
    @NotNull
    private String pid;

    @Column(name = "des_pid")
    private String desPid;

    @JsonIgnore
    @ManyToMany(mappedBy = "sysDepts")
    private Set<SysRole> sysRoles;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SysDept dept = (SysDept) o;
        return Objects.equals(id, dept.id) &&
                Objects.equals(name, dept.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
