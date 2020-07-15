package com.ratel.modules.jsth.service;

import com.ratel.framework.modules.system.domain.RatelUser;
import com.ratel.framework.utils.DateUtils;
import com.ratel.framework.utils.SecurityUtils;
import com.ratel.modules.jsth.domain.MongoUserDkDomain;
import com.ratel.modules.jsth.repository.MongoUserDkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class MongoUserDkService {

    @Autowired
    public MongoTemplate mongoTemplate;

    @Autowired
    public MongoUserDkRepository mongoUserDkRepository;

    @Autowired
    public MongoUserAccountService mongoUserAccountService;

    public List<MongoUserDkDomain> getAll() {
        RatelUser user = (RatelUser) SecurityUtils.getUserDetails();
        return this.mongoUserDkRepository.findAllByStatusAndOpenId("1", user.getUsername());
    }

    public void save(MongoUserDkDomain domain) {
        String date = DateUtils.formatDate(new Date());
        RatelUser user = (RatelUser) SecurityUtils.getUserDetails();
        List list = mongoUserDkRepository.findAllByStatusAndOpenIdAndAndDkDateAndType("1", user.getUsername(), date, "dk");
        if (list == null || list.size() == 0) {
            mongoUserAccountService.updateJf(5L, "健身打卡");
        }
        domain.setDkDate(date);
        mongoUserDkRepository.save(domain);
    }

    public void jsShare() {
        MongoUserDkDomain domain = new MongoUserDkDomain();
        String date = DateUtils.formatDate(new Date());
        RatelUser user = (RatelUser) SecurityUtils.getUserDetails();
        List list = mongoUserDkRepository.findAllByStatusAndOpenIdAndAndDkDateAndType("1", user.getUsername(), date,"fx");
        if (list == null || list.size() == 0) {
            mongoUserAccountService.updateJf(5L, "小程序分享");
        }
        domain.setDkDate(date);
        mongoUserDkRepository.save(domain);
    }

    public void delete(MongoUserDkDomain domain) {
        Query query = new Query(Criteria.where("id").is(domain.getId()));
        Update update = new Update();
        update.set("status", "0");
        mongoTemplate.updateFirst(query, update, MongoUserDkDomain.class);
    }
}
