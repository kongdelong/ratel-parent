package com.ratel.framework.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 框架提供一个基础的UUID方式的实体对象定义参考
 * 具体可根据项目考虑选用其他主键如自增、序列等方式，只需修改相关泛型参数类型和主键定义注解即可
 * 各属性定义可先简单定义MetaData注解即可，具体细节的控制属性含义可查看具体代码注释说明
 */
@Getter
@Access(AccessType.FIELD)
@JsonInclude(Include.ALWAYS)
@MappedSuperclass
@AuditOverrides({@AuditOverride(forClass = BaseUuidEntity.class)})
public abstract class BaseUuidEntity extends BaseEntity<String>  implements Serializable {

    @Id
    @Column(length = 40)
    @GeneratedValue(generator = "hibernate-uuid")
    //HHH000409: Using org.hibernate.id.UUIDHexGenerator which does not generate IETF RFC 4122 compliant UUID values; consider using org.hibernate.id.UUIDGenerator instead
    @GenericGenerator(name = "hibernate-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @JsonProperty
    protected String id;

    public void setId(String id) {
        //容错处理id以空字符提交参数时修改为null避免hibernate主键无效修改
        if (StringUtils.isBlank(id)) {
            this.id = null;
        } else {
            this.id = id;
        }
    }
}
