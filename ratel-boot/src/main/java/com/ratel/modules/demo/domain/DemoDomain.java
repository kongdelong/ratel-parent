package com.ratel.modules.demo.domain;

import com.ratel.framework.domain.BaseNativeEntity;
import com.ratel.framework.domain.validation.SaveGroup;
import com.ratel.framework.domain.validation.UpdateGroup;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * id Long 类型
 * public class DemoDomain extends BaseNativeEntity
 * <p>
 * id 复合主键 类型
 * public class DemoWithMultiId extends BaseEntity
 * <p>
 * id String 类型
 * public class DemoWithStringId extends BaseUuidEntity
 * <p>
 * java validation 相关配置
 *
 * @Null 限制只能为null
 * @NotNull 限制必须不为null
 * @AssertFalse 限制必须为false
 * @AssertTrue 限制必须为true
 * @DecimalMax(value) 限制必须为一个不大于指定值的数字
 * @DecimalMin(value) 限制必须为一个不小于指定值的数字
 * @Digits(integer,fraction) 限制必须为一个小数，且整数部分的位数不能超过integer，小数部分的位数不能超过fraction
 * @Future 限制必须是一个将来的日期
 * @Max(value) 限制必须为一个不大于指定值的数字
 * @Min(value) 限制必须为一个不小于指定值的数字
 * @Past 限制必须是一个过去的日期
 * @Pattern(value) 限制必须符合指定的正则表达式
 * @Size(max,min) 限制字符长度必须在min到max之间
 * @Past 验证注解的元素值（日期类型）比当前时间早
 * @NotEmpty 验证注解的元素值不为null且不为空（字符串长度不为0、集合大小不为0）
 * @NotBlank 验证注解的元素值不为空（不为null、去除首位空格后长度为0），不同于@NotEmpty，@NotBlank只应用于字符串且在比较时会去除字符串的空格
 * @Email 验证注解的元素值是Email，也可以通过正则表达式和flag指定自定义的email格式
 * <p>
 * 例子
 * @Pattern(regexp="^[a-zA-Z0-9]+$",message="{account.username.space}")
 * @Size(min=3,max=20,message="{account.username.size}")
 */

@Data
@Entity
@Table(name = "demo_domain")
public class DemoDomain extends BaseNativeEntity {

    @NotNull(message = "姓名不能为空", groups = {SaveGroup.class, UpdateGroup.class})
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column
    private String nickName;

    @Column
    private String lastName;

    @Column
    private Integer age;

}

