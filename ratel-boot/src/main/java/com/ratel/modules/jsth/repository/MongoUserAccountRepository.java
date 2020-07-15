package com.ratel.modules.jsth.repository;

import com.ratel.modules.jsth.domain.MongoUserAccountDomain;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoUserAccountRepository extends MongoRepository<MongoUserAccountDomain, String> {

    Long countByDateAndTypeAndAdId(String date, String type, String adId);

    List findAllByType(String type, Pageable pageable);
}
