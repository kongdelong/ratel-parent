package com.ratel.modules.jsth.repository;

import com.ratel.modules.jsth.domain.MongoUserDomain;
import com.ratel.modules.jsth.domain.MongoUserTzDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoUserRepository extends MongoRepository<MongoUserDomain, String> {

}
