package com.ratel.framework.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.springframework.util.Assert;

import javax.persistence.*;

/**
 * 基于Nested Set Model的树形结构对象基类定义
 *
 * 注意：按照模型标准定义，需要一个默认的root根节点，因此凡是继承此树形模型的实体对象，必须要在相关的DatabaseDataInitializeProcessor中初始化根节点，
 * 如参考BasicDatabaseDataInitializeProcessor中对Department实体对象初始化逻辑
 *
 * @see "https://en.wikipedia.org/wiki/Nested_set_model"
 */
@Getter
@Setter
@Access(AccessType.FIELD)
@JsonInclude(Include.NON_EMPTY)
@MappedSuperclass
@AuditOverrides({@AuditOverride(forClass = BaseNativeNestedSetEntity.class)})
public abstract class BaseNativeNestedSetEntity<T extends BaseNativeNestedSetEntity> extends BaseNativeEntity {

    /**
     * 定义外键约束为none主要是为了方便删除表重建数据，为了避免意外删除大量数据，因此不支持递归删除方式,业务接口已限制只能删除子节点。
     * 由于采用了Nested Set数据模型，除非清楚模型数据规则，请勿随意删除数据，否则会导致lft和rgt等相关数据混乱
     */
    //@MetaData(value = "父节点")
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "none"))
    private T parent;

    @Column(name = "lft", nullable = false)
    private Integer lft = 0;

    @Column(name = "rgt", nullable = false)
    private Integer rgt = 0;

    @Column(name = "depth", nullable = false)
    private Integer depth = 0;

    //@MetaData(value = "禁用标识", tooltips = "禁用项目用户端不显示")
    private Boolean disabled = Boolean.FALSE;

    //@MetaData(value = "拖拉移动父子关系时新位置对应父对象ID")
    @Transient
    @JsonIgnore
    private Long newParentId;

    //@MetaData(value = "拖拉移动排序时新位置向上相邻位置对象ID")
    @Transient
    @JsonIgnore
    private Long sortPrevId;

    public void makeRoot() {
        Assert.isTrue(this.getLft() == null || this.getLft() <= 0, "Invalid entity");
        Assert.isTrue(this.getRgt() == null || this.getRgt() <= 0, "Invalid entity");
        this.setLft(1);
        this.setRgt(2);
        this.setDepth(0);
    }

    public boolean isRoot() {
        return this.getDepth() == 0;
    }

    public Boolean hasChildren() {
        return (this.getRgt() - this.getLft()) > 1;
    }
}
