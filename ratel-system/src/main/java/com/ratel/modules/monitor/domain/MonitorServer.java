package com.ratel.modules.monitor.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.ratel.framework.domain.BaseEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "monitor_server")
public class MonitorServer extends BaseEntity {

    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * IP地址
     */
    @Column(name = "address", nullable = false)
    private String address;

    /**
     * 访问端口
     */
    @Column(name = "port")
    private Integer port;

    /**
     * 状态
     */
    @Column(name = "state")
    private String state;

    /**
     * CPU使用率
     */
    @Column(name = "cpu_rate")
    private Double cpuRate;

    /**
     * CPU内核数
     */
    @Column(name = "cpu_core")
    private Integer cpuCore;

    /**
     * 内存总数
     */
    @Column(name = "mem_total")
    private Double memTotal;

    /**
     * 内存使用量
     */
    @Column(name = "mem_used")
    private Double memUsed;

    /**
     * 磁盘总量
     */
    @Column(name = "disk_total")
    private Double diskTotal;

    /**
     * 磁盘使用量
     */
    @Column(name = "disk_used")
    private Double diskUsed;

    /**
     * 交换区总量
     */
    @Column(name = "swap_total")
    private Double swapTotal;

    /**
     * 交换区使用量
     */
    @Column(name = "swap_used")
    private Double swapUsed;

    /**
     * 排序
     */
    @Column(name = "sort")
    private Integer sort;

    public void copy(MonitorServer source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @Override
    public String getDisplay() {
        return this.getName();
    }
}
