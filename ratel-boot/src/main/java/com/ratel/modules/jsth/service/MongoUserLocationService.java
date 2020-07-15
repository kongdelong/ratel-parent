package com.ratel.modules.jsth.service;

import com.ratel.framework.modules.system.domain.RatelUser;
import com.ratel.framework.utils.SecurityUtils;
import com.ratel.modules.jsth.domain.MongoUserLocationDomain;
import com.ratel.modules.jsth.repository.MongoUserLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoUserLocationService {

    @Autowired
    public MongoTemplate mongoTemplate;

    @Autowired
    public MongoUserLocationRepository mongoUserLocationRepository;

    public MongoUserLocationDomain getOne(String openId) {
        return this.mongoUserLocationRepository.findByOpenId(openId);
    }

    public MongoUserLocationDomain getUserInfo() {

        RatelUser user = (RatelUser) SecurityUtils.getUserDetails();
        return this.mongoUserLocationRepository.findByOpenId(user.getUsername());
    }


    public List<MongoUserLocationDomain> getAll() {
        return this.mongoUserLocationRepository.findAll();
    }

    public void saveLocation(MongoUserLocationDomain domain) {
        if (mongoUserLocationRepository.findByOpenId(domain.getOpenId()) != null) {
            Query query = new Query(Criteria.where("openId").is(domain.getOpenId()));
            Update update = new Update();
            update.set("avatarUrl", domain.getAvatarUrl())
                    .set("nickName", domain.getNickName())
                    .set("latitude", domain.getLatitude())
                    .set("longitude", domain.getLongitude());

            mongoTemplate.updateFirst(query, update, MongoUserLocationDomain.class);
        } else {
            mongoUserLocationRepository.save(domain);
        }
    }


}
