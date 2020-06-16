package com.ratel.framework.domain.interceptor.query;

import java.lang.reflect.Method;

/**
 * @version V1.0
 * @package club.geemi.club.geemi.core.jpa.query
 * @description:
 * @author: Geemi @Everlin
 * @date: 2017/12/2 17:59
 * Copyright©2017 All rights reserved.
 */
public class SelectAlias {

    private Class type;
    private String[] alias;
    private Method[] methods;

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String[] getAlias() {
        return alias;
    }

    public void setAlias(String[] alias) {
        this.alias = alias;
    }

    public Method[] getMethods() {
        return methods;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }
}
