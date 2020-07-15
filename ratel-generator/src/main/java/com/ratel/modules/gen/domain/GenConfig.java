package com.ratel.modules.gen.domain;

import com.ratel.framework.domain.BaseNativeEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * 代码生成配置
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "gen_config")
public class GenConfig extends BaseNativeEntity {

    public GenConfig(String tableName) {
        this.cover = false;
        this.moduleName = "rtadmin-system";
        this.tableName = tableName;
    }

    @NotBlank
    private String tableName;

    /**
     * 接口名称
     **/
    private String apiAlias;

    /**
     * 包路径
     */
    @NotBlank
    private String pack;

    /**
     * 模块名
     */
    @Column(name = "module_name")
    @NotBlank
    private String moduleName;

    /**
     * 前端文件路径
     */
    @NotBlank
    private String path;

    /**
     * 前端文件路径
     */
    @Column(name = "api_path")
    private String apiPath;

    /**
     * 作者
     */
    private String author;

    /**
     * 表前缀
     */
    private String prefix;

    /**
     * 是否覆盖
     */
    private Boolean cover;
}
