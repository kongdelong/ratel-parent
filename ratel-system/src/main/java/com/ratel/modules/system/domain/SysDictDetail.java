package com.ratel.modules.system.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ratel.framework.domain.BaseUuidEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;


@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "sys_dict_detail")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysDictDetail extends BaseUuidEntity {

    /**
     * 字典标签
     */
    @Column(name = "label", nullable = false)
    private String label;

    /**
     * 字典值
     */
    @Column(name = "value", nullable = false)
    private String value;

    /**
     * 排序
     */
    @Column(name = "sort")
    private String sort = "999";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dict_id")
    @JsonBackReference
    private SysDict sysDict;

    @Transient
    private String sysDictName;

    public String getSysDictName() {
        return this.sysDict.getName();
    }

}
