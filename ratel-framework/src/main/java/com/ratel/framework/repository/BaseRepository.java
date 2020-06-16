package com.ratel.framework.repository;

import com.ratel.framework.service.dto.RatelQueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 基类的数据访问接口(继承了CrudRepository,PagingAndSortingRepository,JpaSpecificationExecutor的特性)
 *
 * @param <T>  实体对象
 * @param <ID> 实体主键类型
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * 封装自定义组合查询列表方法
     *
     * @param ratelQueryCriteria 查询条件
     * @return
     */
    List<T> findAll(RatelQueryCriteria ratelQueryCriteria);

    /**
     * 封装自定义组合查询列表方法 排序
     *
     * @param ratelQueryCriteria 查询条件
     * @param sort               排序条件
     * @return
     */
    List<T> findAll(RatelQueryCriteria ratelQueryCriteria, Sort sort);

    /**
     * 封装自定义组合查询列表方法 排序
     *
     * @param ratelQueryCriteria 查询条件
     * @param direction          排序方向
     * @param sortProperty       排序字段
     * @return
     */
    List<T> findAll(RatelQueryCriteria ratelQueryCriteria, Sort.Direction direction, String sortProperty);

    /**
     * 封装自定义组合查询列表方法 分页
     *
     * @param ratelQueryCriteria 查询条件
     * @param pageable           分页信息
     * @return
     */
    Page<T> findAll(RatelQueryCriteria ratelQueryCriteria, Pageable pageable);


    /**
     * @param ratelQueryCriteria 查询条件
     * @return
     */
    Optional<T> findOne(RatelQueryCriteria ratelQueryCriteria);

    /**
     * 查询统计
     *
     * @param ratelQueryCriteria
     * @return
     */
    long count(@Nullable RatelQueryCriteria ratelQueryCriteria);

    /**
     * 根据主键更新
     *
     * @param id        主键
     * @param t         实体
     * @param propertys 更新属性定义
     * @return
     */
    int updateById(ID id, T t, String... propertys);

    /**
     * @param id       主键
     * @param property 更新属性定义
     * @param value    更新值 于 属性顺序一致
     * @return
     */
    int updateById(ID id, String property, String value);

    /**
     * @param id        主键
     * @param propertys 更新属性定义
     * @param values    更新值 于 属性顺序一致
     * @return
     */
    int updateById(ID id, List<String> propertys, String... values);

    /**
     * @param ratelQueryCriteria 条件
     * @param propertys          更新属性定义
     * @param values             更新值 于 属性顺序一致
     * @return
     */
    int update(RatelQueryCriteria ratelQueryCriteria, List<String> propertys, String... values);

    /**
     * @param ratelQueryCriteria 条件
     * @param t                  实体
     * @param propertys          更新属性定义
     * @return
     */
    int update(RatelQueryCriteria ratelQueryCriteria, T t, String... propertys);
}
