package com.ratel.framework.domain.interceptor.query;

public interface SqlParser {
    /**
     * 解析SQL中的查询返回字段，若不存在则返回null
     *
     * @param sql
     * @param clazz
     * @return
     */
    SelectAlias getAlias(String sql, Class clazz);
}
