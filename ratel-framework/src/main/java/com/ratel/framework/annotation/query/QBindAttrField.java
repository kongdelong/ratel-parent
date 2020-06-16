package com.ratel.framework.annotation.query;

import com.ratel.framework.enums.query.Where;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询绑定属性
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QBindAttrField {
    public abstract String fieldName();

    public abstract Where where();
}
