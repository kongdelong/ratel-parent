package com.ratel.framework.domain.interceptor;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.data.domain.Auditable;

import java.io.Serializable;
import java.util.Date;


public class HibernateInterceptor extends EmptyInterceptor {
    private int updates;

    private int creates;

    private int loads;

    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state,
                          String[] propertyNames, Type[] types) {
        System.out.println(entity.getClass().getName() + " loaded.");
        return false;
    }

    public boolean onFlushDirty(Object entity,
                                Serializable id,
                                Object[] currentState,
                                Object[] previousState,
                                String[] propertyNames,
                                Type[] types) {

        if (entity instanceof Auditable) {
            updates++;
            for (int i = 0; i < propertyNames.length; i++) {
                if ("lastUpdateTimestamp".equals(propertyNames[i])) {
                    currentState[i] = new Date();
                    return true;
                }
            }
        }
        return false;
    }
}
