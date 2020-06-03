package com.ratel.modules.system.domain;

import com.ratel.framework.domain.BaseUuidEntity;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "sys_verification_code")
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysVerificationCode extends BaseUuidEntity {

    private String code;

    /**
     * 使用场景，自己定义
     */
    private String scenes;

    /**
     * true 为有效，false 为无效，验证时状态+时间+具体的邮箱或者手机号
     */
    private Boolean status = true;

    /**
     * 类型 ：phone 和 email
     */
    @NotBlank
    private String type;

    /**
     * 具体的phone与email
     */
    @NotBlank
    private String value;


    public SysVerificationCode(String code, String scenes, @NotBlank String type, @NotBlank String value) {
        this.code = code;
        this.scenes = scenes;
        this.type = type;
        this.value = value;
    }
}
