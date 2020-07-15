package com.ratel.framework.domain.event;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;


@Component
public class RatelUpdateEventListiner implements PostUpdateEventListener {

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        System.out.println(event);
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        System.out.println(persister);
        return false;
    }
}
