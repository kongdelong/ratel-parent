package com.ratel.modules.jsth.repository;

import com.ratel.modules.jsth.domain.MongoUserSharesDomain;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoUserSharesRepository extends MongoRepository<MongoUserSharesDomain, String> {

    public List<MongoUserSharesDomain> findAllByEnableOrderByDateDesc(Boolean status, Pageable pageable);


    @Query(value = "{'$and': [{ 'content':?0},{'enable':?1}]}")
    public List<MongoUserSharesDomain> findAllBySql(String content, Boolean status, Pageable pageable);
}
