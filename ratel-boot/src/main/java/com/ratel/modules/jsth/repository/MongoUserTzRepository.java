package com.ratel.modules.jsth.repository;

import com.ratel.modules.jsth.domain.MongoUserTzDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoUserTzRepository extends MongoRepository<MongoUserTzDomain, String> {


    MongoUserTzDomain findByOpenId(String openId);

    List<MongoUserTzDomain> findAllByStatusAndOpenId(String status, String openId);

//    /**
//     * { '_id' : 1}表示结果只返回_id这个属性
//     * {'defId': -1} 表示不反回 defId这个属性
//     *
//     * @param defId
//     * @param status
//     * @return
//     */
//    @Query(value = "{'$and': [{ 'defId':?0},{'status':?1}]}", fields = "{'defId': -1,'status' : -1}")
//    List<MongoUserLocationDomain> findByDefIdAndStatus(String defId, String status);
//
//
//    /**
//     * findByDefIdLike 根据DefIdLike 不需要注解
//     *
//     * @param name
//     * @param pageable
//     * @return
//     */
//    Page<MongoUserLocationDomain> findByDefIdLike(String name, Pageable pageable);
}
