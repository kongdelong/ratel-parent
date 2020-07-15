package com.ratel.modules.jsth.service;

import com.ratel.framework.utils.DateUtils;
import com.ratel.modules.jsth.domain.MongoEditorArticleDomain;
import com.ratel.modules.jsth.repository.MongoEditorArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MongoEditorArticleService {

    @Autowired
    public MongoTemplate mongoTemplate;

    @Autowired
    public MongoEditorArticleRepository mongoEditorArticleRepository;

    public void save(MongoEditorArticleDomain resource) {
        resource.setDate(DateUtils.formatDate(new Date()));
        resource.setStatus("0");
        mongoEditorArticleRepository.save(resource);
    }

    public List getAll(String pid, Pageable pageable) {
        return mongoEditorArticleRepository.findAllByParent(pid, pageable).getContent();
    }

    public List findAllByLevel(String level, Pageable pageable) {
        return mongoEditorArticleRepository.findAllByLevel(level, pageable).getContent();
    }


    public MongoEditorArticleDomain getArticle(String id) {
        return mongoEditorArticleRepository.findById(id).orElse(null);
    }
}
