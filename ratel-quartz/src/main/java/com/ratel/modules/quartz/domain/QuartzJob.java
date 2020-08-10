package com.ratel.modules.quartz.domain;

import com.ratel.framework.domain.BaseNativeEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Entity
@Table(name = "quartz_job")
public class QuartzJob extends BaseNativeEntity {

    public static final String JOB_KEY = "JOB_KEY";

    /**
     * 定时器名称
     */
    @Column(name = "job_name")
    private String jobName;

    /**
     * Bean名称
     */
    @Column(name = "bean_name")
    @NotBlank
    private String beanName;

    /**
     * 方法名称
     */
    @Column(name = "method_name")
    @NotBlank
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
    @NotBlank
    private String cronExpression;

    /**
     * 状态
     */
    @Column(name = "is_pause")
    private Boolean isPause = false;

    /**
     * 备注
     */
    @Column(name = "remark")
    @NotBlank
    private String remark;
}
