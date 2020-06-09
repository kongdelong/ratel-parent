package com.ratel.modules.logging.domain;

import com.ratel.framework.domain.BaseUuidEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "sys_log")
@NoArgsConstructor
public class SysLog extends BaseUuidEntity {

    /**
     * 操作用户
     */
    private String username;

    /**
     * 描述
     */
    private String description;

    /**
     * 方法名
     */
    private String method;

    /**
     * 参数
     */
    @Column(columnDefinition = "text")
    private String params;

    /**
     * 日志类型
     */
    @Column(name = "log_type")
    private String logType;

    /**
     * 请求ip
     */
    @Column(name = "request_ip")
    private String requestIp;

    /**
     * 地址
     */
    @Column(name = "address")
    private String address;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 请求耗时
     */
    private Long time;

    /**
     * 异常详细
     */
    @Column(name = "exception_detail", columnDefinition = "MEDIUMTEXT")
    private byte[] exceptionDetail;

    public SysLog(String logType, Long time) {
        this.logType = logType;
        this.time = time;
    }
}
