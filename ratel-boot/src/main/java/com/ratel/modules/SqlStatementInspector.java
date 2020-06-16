package com.ratel.modules;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.List;

@Slf4j
public class SqlStatementInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
        // 这里可以拦截到sql ， 这里的sql格式会有占位符？  如：select u.name from user u where u.id = ?
        log.info("组织筛选解析开始，原始SQL：{}", sql);
        return sql;
    }
}
