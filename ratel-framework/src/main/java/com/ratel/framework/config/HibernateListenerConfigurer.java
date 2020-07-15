package com.ratel.framework.config;

import com.ratel.framework.domain.event.RatelInsertEventListiner;
import com.ratel.framework.domain.event.RatelUpdateEventListiner;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

//@Component
//public class HibernateIntegrator {
//
//    @Autowired
//    private PostUpdateEventListiner postUpdateEventListiner;
//
//    @Autowired
//    private SessionFactory sessionFactory;
//
//    @PostConstruct
//    public void registerListeners() {
//        EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(
//                EventListenerRegistry.class);
//        registry.getEventListenerGroup(EventType.SAVE_UPDATE).appendListener(postUpdateEventListiner);
//    }
//
//}


//@Component
public class HibernateListenerConfigurer {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Inject
    private RatelUpdateEventListiner ratelUpdateEventListiner;

    @Inject
    private RatelInsertEventListiner ratelInsertEventListiner;

    @PostConstruct
    protected void init() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(ratelInsertEventListiner);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(ratelUpdateEventListiner);

    }
}
