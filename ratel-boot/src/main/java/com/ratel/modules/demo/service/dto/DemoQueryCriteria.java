package com.ratel.modules.demo.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ratel.framework.annotation.search.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoQueryCriteria {

    /**
     * 多字段模糊搜索，仅支持String类型字段，多个用逗号隔开, 如@Query(blurry = "email,username")
     */
    @Query(blurry = "email,username,nickName")
    private String blurry;

    /**
     * 精确
     */
    @Query
    private String id;

    @Query(type = Query.Type.EQUAL, propName = "id")
    private Long userId;

    /**
     * 模糊
     */
    @Query(type = Query.Type.INNER_LIKE)
    private String username;

    @Query(propName = "id", type = Query.Type.IN, joinName = "dept")
    private Set<Long> deptIds;

    /**
     * 不等于
     */
    @Query(type = Query.Type.NOT_EQUAL, propName = "id")
    private Set<Long> ids;

    /**
     * 不为空
     */
    @Query(type = Query.Type.NOT_NULL, propName = "id")
    private Set<Long> notInids;

    /**
     * 大于等于
     */
    @Query(type = Query.Type.GREATER_THAN, propName = "id")
    private Long greaterThan;

    /**
     * 小于等于
     */
    @Query(type = Query.Type.LESS_THAN, propName = "id")
    private Long lessThan;

    /**
     * BETWEEN
     */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;

}
