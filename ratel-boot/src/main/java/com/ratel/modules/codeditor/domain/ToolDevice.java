package com.ratel.modules.codeditor.domain;

import com.ratel.framework.domain.BaseUuidEntity;
import com.ratel.modules.system.config.JpaConverterListJson;
import com.ratel.modules.system.domain.SysUserAvatar;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "tool_device")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ToolDevice extends BaseUuidEntity {
    private String sid;
    private String deviceName;
    private String deviceSerial;
    private String deviceManufacturer;
    private String deviceModel;
    private String deviceBrand;
    private String clientVersion;
    private String appVersion;
    private String appVersionCode;

    private String runFlag;

    private String tag;

    private String gruopNum;

    private String ip;
}
