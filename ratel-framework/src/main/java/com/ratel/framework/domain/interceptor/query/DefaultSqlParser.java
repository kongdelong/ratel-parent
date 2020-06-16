package com.ratel.framework.domain.interceptor.query;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class DefaultSqlParser implements SqlParser {

    @Override
    public SelectAlias getAlias(String sql, Class clazz) {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        try {
            Statement statement = pm.parse(new StringReader(sql));
            if (statement instanceof Select) {
                Select selectStatement = (Select) statement;
                SelectBody selectBody = selectStatement.getSelectBody();

                if (selectBody instanceof PlainSelect) {
                    PlainSelect plainSelect = (PlainSelect) selectBody;
                    List<SelectItem> list = plainSelect.getSelectItems();
                    List<Method> methodList = new ArrayList<>();
                    List<String> aliasName = new ArrayList<>();
                    for (SelectItem item : list) {
                        if (!(item instanceof SelectExpressionItem)) {
                            continue;
                        }
                        SelectExpressionItem expressionItem = (SelectExpressionItem) item;
                        String fieldName = getFieldName(expressionItem);
                        Method setter = getSetter(clazz.getMethods(), fieldName);
                        methodList.add(setter);
                        aliasName.add(fieldName);
                    }
                    SelectAlias alias = new SelectAlias();
                    alias.setType(clazz);
                    alias.setAlias(aliasName.toArray(new String[aliasName.size()]));
                    alias.setMethods(methodList.toArray(new Method[methodList.size()]));
                    return alias;
                }
            }
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFieldName(SelectExpressionItem item) {
        if (item.getAlias() != null) {
            return item.getAlias().getName();
        } else if (item.getExpression() instanceof Column) {
            return ((Column) item.getExpression()).getColumnName();
        }
        return "";
    }

    private static Method getSetter(Method[] methods, String field) {
        for (Method method : methods) {
            if (method.getName().equals("set" + upperFirstLetter(field)) && method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;
    }

    private static String upperFirstLetter(String str) {
        if (str == null || str.length() <= 0) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
