package com.ratel.modules.jsth.service;

import com.ratel.framework.modules.system.domain.RatelUser;
import com.ratel.framework.utils.DateUtils;
import com.ratel.framework.utils.SecurityUtils;
import com.ratel.modules.jsth.domain.MongoUserAccountDomain;
import com.ratel.modules.jsth.domain.MongoUserLocationDomain;
import com.ratel.modules.jsth.repository.MongoUserAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class MongoUserAccountService {

    @Autowired
    public MongoTemplate mongoTemplate;

    @Autowired
    public MongoUserAccountRepository mongoUserAccountRepository;

    @Autowired
    public MongoUserLocationService mongoUserLocationService;


    public List getAll(String type, Pageable pageable) {
        return mongoUserAccountRepository.findAllByType(type, pageable);
    }

    public void updateJf(Long jf, String bizz) {
        RatelUser user = (RatelUser) SecurityUtils.getUserDetails();
        MongoUserLocationDomain mongoUserLocationDomain = mongoUserLocationService.getOne(user.getUsername());
        Long jfcount = (mongoUserLocationDomain.getJf() != null ? mongoUserLocationDomain.getJf() : 0l) + jf;
        Query query = new Query(Criteria.where("openId").is(user.getUsername()));
        Update update = new Update();
        update.set("jf", jfcount);
        update.set("updateTime", new Date());
        mongoTemplate.updateFirst(query, update, MongoUserLocationDomain.class);

        MongoUserAccountDomain mongoUserAccountDomain = new MongoUserAccountDomain();
        mongoUserAccountDomain.setBizz(bizz);
        mongoUserAccountDomain.setType("jf");
        mongoUserAccountDomain.setJf(jf);
        mongoUserAccountDomain.setDate(DateUtils.formatDate(new Date()));
        mongoUserAccountRepository.save(mongoUserAccountDomain);
    }

    public Long updateJb(Long jb, String bizz, String adId) {
        RatelUser user = (RatelUser) SecurityUtils.getUserDetails();
        String date = DateUtils.formatDate(new Date());
        Long count = mongoUserAccountRepository.countByDateAndTypeAndAdId(date, "jb", adId);
        if (count < 21) {
            MongoUserLocationDomain mongoUserLocationDomain = mongoUserLocationService.getOne(user.getUsername());
            Long jbcount = (mongoUserLocationDomain.getJb() != null ? mongoUserLocationDomain.getJb() : 0l) + jb;
            Query query = new Query(Criteria.where("openId").is(user.getUsername()));
            Update update = new Update();
            update.set("jb", jbcount);
            update.set("updateTime", new Date());
            mongoTemplate.updateFirst(query, update, MongoUserLocationDomain.class);

            MongoUserAccountDomain mongoUserAccountDomain = new MongoUserAccountDomain();
            mongoUserAccountDomain.setBizz(bizz);
            mongoUserAccountDomain.setType("jb");
            mongoUserAccountDomain.setJb(jb);
            mongoUserAccountDomain.setDate(date);
            mongoUserAccountDomain.setAdId(adId);
            mongoUserAccountRepository.save(mongoUserAccountDomain);
            return count + 1;
        } else {
            return count;
        }
    }

}
