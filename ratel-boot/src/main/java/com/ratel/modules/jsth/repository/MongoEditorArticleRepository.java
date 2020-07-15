package com.ratel.modules.jsth.repository;

import com.ratel.modules.jsth.domain.MongoEditorArticleDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoEditorArticleRepository extends MongoRepository<MongoEditorArticleDomain, String> {

    List findAllByAndComments(String content, Pageable pageable);

    @Query(value = "{parent:?0}", fields = "{'tags': -1,'name': -1,'listImg': -1,'comments':-1,'likeNum':-1,'title':-1}")
    Page<MongoEditorArticleDomain> findAllByParent(String pid, Pageable pageable);

    @Query(value = "{level:?0}", fields = "{'tags': -1,'name': -1,'listImg': -1,'comments':-1,'likeNum':-1,'title':-1}")
    Page<MongoEditorArticleDomain> findAllByLevel(String pid, Pageable pageable);
}
