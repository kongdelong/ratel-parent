package com.ratel.modules.quartz.domain;

import com.ratel.framework.domain.BaseNativeEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "quartz_log")
public class QuartzLog extends BaseNativeEntity {

    /**
     * 任务名称
     */
    @Column(name = "job_name")
    private String jobName;

    /**
     * Bean名称
     */
    @Column(name = "baen_name")
    private String beanName;

    /**
     * 方法名称
     */
    @Column(name = "method_name")
    private String methodName;

    /**
     * 参数
     */
    @Column(name = "params")
    private String params;

    /**
     * cron表达式
     */
    @Column(name = "cron_expression")
    private String cronExpression;

    /**
     * 状态
     */
    @Column(name = "is_success")
    private Boolean isSuccess;

    /**
     * 异常详细
     */
    @Column(name = "exception_detail", columnDefinition = "text")
    private String exceptionDetail;

    /**
     * 耗时（毫秒）
     */
    private Long time;

}
