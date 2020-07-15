package com.ratel.modules.jsth.service;

import com.ratel.framework.modules.system.domain.RatelUser;
import com.ratel.framework.utils.DateUtils;
import com.ratel.framework.utils.SecurityUtils;
import com.ratel.modules.jsth.domain.MongoUserMbDomain;
import com.ratel.modules.jsth.domain.MongoUserTzDomain;
import com.ratel.modules.jsth.repository.MongoUserMbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MongoUserMbService {

    @Autowired
    public MongoTemplate mongoTemplate;

    @Autowired
    public MongoUserMbRepository mongoUserMbRepository;

    public List<MongoUserMbDomain> getAll() {
        RatelUser user = (RatelUser) SecurityUtils.getUserDetails();
        return this.mongoUserMbRepository.findAllByStatusAndOpenId("1", user.getUsername());
    }

    public void save(MongoUserMbDomain domain) {
        domain.setDate(DateUtils.formatDate(new Date()));
        mongoUserMbRepository.save(domain);
    }

    public void delete(MongoUserMbDomain domain) {
        Query query = new Query(Criteria.where("id").is(domain.getId()));
        Update update = new Update();
        update.set("status", "0");
        mongoTemplate.updateFirst(query, update, MongoUserTzDomain.class);
    }
}
