package com.ratel.modules;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;
import org.hibernate.resource.jdbc.spi.StatementInspector;

@Slf4j
public class SqlStatementInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
        // 这里可以拦截到sql ， 这里的sql格式会有占位符？  如：select u.name from user u where u.id = ?
        log.info("组织筛选解析开始，原始SQL：{}", sql);
        try {
            if (sql.toLowerCase().startsWith("update")) {
                return processSql(sql);
            } else {
                return sql;
            }
        } catch (Exception e) {
            log.error("组织筛选解析失败，解析SQL异常{}", e.getMessage());
            e.printStackTrace();
        }
        return sql;
    }

    private String processSql(String sql) throws JSQLParserException {
        Statements statements = CCJSqlParserUtil.parseStatements(sql);


        StringBuilder sqlStringBuilder = new StringBuilder();
        int i = 0;
        for (Statement statement : statements.getStatements()) {
            if (null != statement) {
                if (i++ > 0) {
                    sqlStringBuilder.append(';');
                }
                sqlStringBuilder.append(this.processParser(statement));
            }
        }
        String newSql = sqlStringBuilder.toString();
        return newSql;
    }


    private String processParser(Statement statement) {
        if (statement instanceof Insert) {
            this.processInsert((Insert) statement);
        } else if (statement instanceof Select) {
            this.processSelectBody(((Select) statement).getSelectBody());
        } else if (statement instanceof Update) {
            this.processUpdate((Update) statement);
        } else if (statement instanceof Delete) {
            this.processDelete((Delete) statement);
        }
        /**
         * 返回处理后的SQL
         */
        return statement.toString();
    }

    /**
     * insert 语句处理
     */

    public void processInsert(Insert insert) {

    }

    /**
     * update 语句处理
     */

    public void processUpdate(Update update) {
        update.getColumns().add(new Column("update_user_name"));
        update.getExpressions().add(new StringValue("000000"));
    }

    /**
     * delete 语句处理
     */

    public void processDelete(Delete delete) {

    }

    /**
     * select 语句处理
     */

    public void processSelectBody(SelectBody selectBody) {

    }
}
