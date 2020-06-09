//package com.ratel.modules.system.domain;
//
//import com.ratel.framework.domain.BaseUuidEntity;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.experimental.Accessors;
//import org.hibernate.annotations.CacheConcurrencyStrategy;
//
//import javax.persistence.*;
//
//@Getter
//@Setter
//@Accessors(chain = true)
//@Access(AccessType.FIELD)
//@Entity
//@Table(name = "sys_params")
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//public class SysParams extends BaseUuidEntity {
//
//    //乐观锁版本
//    @Version
//    @Column(name = "version", nullable = false)
//    private Integer version = 0;
//
//    private String paramCode;//参数代码
//
//    private String dataType;//参数类型
//
//    private String paramValue;//参数值
//
//    private Long parentId;//父参数值
//
//    private String paramSubCode;//子代码
//
//    private String keepField;//预留
//
//    private String channelId;//渠道标示
//
//    private String loadflag;//装载标志
//
//    private String startTime;//生效日期
//
//    private String endTime;//失效日期
//
//    private Integer loadFre;//系统加载标志
//
//}
