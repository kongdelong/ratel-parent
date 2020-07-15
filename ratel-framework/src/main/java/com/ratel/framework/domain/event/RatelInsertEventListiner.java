package com.ratel.framework.domain.event;

import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;


@Component
public class RatelInsertEventListiner implements PostInsertEventListener {

    @Override
    public void onPostInsert(PostInsertEvent event) {
        System.out.println(event);
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        System.out.println(persister);
        return false;
    }
}
