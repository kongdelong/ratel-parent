package com.ratel.framework.domain.interceptor.query;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.core.RepositoryInformation;

import java.lang.reflect.*;
import java.util.*;


public class JpqlBeanMethodInterceptor implements MethodInterceptor {

    private Map<Method, SelectAlias> selectAlias = new HashMap<>();

    public JpqlBeanMethodInterceptor(RepositoryInformation repositoryInformation) {
        Iterator<Method> iterable = repositoryInformation.getQueryMethods().iterator();
        SqlParser sqlParser = new DefaultSqlParser();
        while (iterable.hasNext()) {
            Method method = iterable.next();
            Query query = method.getAnnotation(Query.class);
            if (query == null || query.nativeQuery()) {
                continue;
            }
            //获取返回类型
            Class clazz = getGenericReturnClass(method);
            if (clazz == null) {
                continue;
            }
            SelectAlias alias = sqlParser.getAlias(query.value(), clazz);
            if (alias == null) {
                continue;
            }
            selectAlias.put(method, alias);
        }
    }

    private Class getGenericReturnClass(Method method) {
        Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
            if (actualTypeArguments.length == 1) {
                try {
                    return this.getClass().getClassLoader().loadClass(actualTypeArguments[0].getTypeName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object obj = invocation.proceed();
        if (!checkCanConvert(obj)) {
            return obj;
        }
        SelectAlias alias = selectAlias.get(invocation.getMethod());
        if (alias == null) {
            return obj;
        }
        List content = getPageContent((PageImpl) obj);
        convert(content, alias);
        return obj;
    }

    private static List getPageContent(PageImpl page) throws NoSuchFieldException, IllegalAccessException {
        Field field = PageImpl.class.getSuperclass().getDeclaredField("content");
        if (List.class.isAssignableFrom(field.getType())) {
            field.setAccessible(true);
            return (List) field.get(page);
        }
        return null;
    }

    private boolean checkCanConvert(Object result) {
        if (!(result instanceof PageImpl)) {
            return false;
        }
        List content = ((Page) result).getContent();
        if (content == null || content.size() <= 0) {
            return false;
        }
        if (!(content.get(0) instanceof Object[])) {
            return false;
        }
        return true;
    }

    protected void convert(List<Object[]> list, SelectAlias alias) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List resultList = new ArrayList(list.size());
        for (Object[] cols : list) {
            Object item = alias.getType().newInstance();
            for (int i = 0; i < alias.getMethods().length; i++) {
                if (alias.getMethods()[i] == null) {
                    continue;
                }
                alias.getMethods()[i].invoke(item, cols[i]);
            }
            resultList.add(item);
        }
        list.clear();
        list.addAll(resultList);
    }
}
