package com.ratel.modules.jsth.repository;

import com.ratel.modules.jsth.domain.MongoUserLocationDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoUserLocationRepository extends MongoRepository<MongoUserLocationDomain, String> {


    MongoUserLocationDomain findByOpenId(String openId);

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
