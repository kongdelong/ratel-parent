package com.ratel.modules.system.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.ratel.framework.domain.BaseUuidEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "sys_storage")
@NoArgsConstructor
public class SysStorage extends BaseUuidEntity {

    /**
     * 真实文件名
     */
    @Column(name = "real_name")
    private String realName;

    /**
     * 文件名
     */
    @Column(name = "name")
    private String name;

    /**
     * 后缀
     */
    @Column(name = "suffix")
    private String suffix;

    /**
     * 路径
     */
    @Column(name = "path")
    private String path;

    /**
     * 路径
     */
    @Column(name = "small_path")
    private String smallPath;

    /**
     * 类型
     */
    @Column(name = "type")
    private String type;

    /**
     * 大小
     */
    @Column(name = "size")
    private String size;

    @Column(name = "attr_path")
    private String attrPath;

    @Column(name = "thumb_path")
    private String thumbPath;


    public SysStorage(String realName, String name, String suffix, String path, String smallPath, String type, String size, String attrPath, String thumbPath) {
        this.realName = realName;
        this.name = name;
        this.suffix = suffix;
        this.path = path;
        this.smallPath = smallPath;
        this.type = type;
        this.size = size;
        this.attrPath = attrPath;
        this.thumbPath = thumbPath;
    }

    public void copy(SysStorage source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
