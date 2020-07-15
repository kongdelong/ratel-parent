package com.ratel.modules.jsth.service;

import com.ratel.framework.utils.StringUtils;
import com.ratel.modules.jsth.domain.MongoUserSharesDomain;
import com.ratel.modules.jsth.repository.MongoUserSharesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoUserSharesService {

    @Autowired
    public MongoTemplate mongoTemplate;

    @Autowired
    public MongoUserSharesRepository mongoUserSharesRepository;

    public List<MongoUserSharesDomain> getAll(String content, Pageable pageable) {
        if (StringUtils.isBlank(content))
            return this.mongoUserSharesRepository.findAllByEnableOrderByDateDesc(true, pageable);
        else
            return this.mongoUserSharesRepository.findAllBySql(content, true, pageable);
    }

}
