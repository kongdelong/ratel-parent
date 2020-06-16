package com.ratel.framework.service;

import com.ratel.framework.domain.AbstractPersistableEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class BaseService<T extends AbstractPersistableEntity, ID extends Serializable> {

    protected Class<T> entityClass;

    protected String entityName;

    protected SimpleJpaRepository<T, ID> jpaRepository;

    @PersistenceContext
    protected EntityManager entityManager;

    @PostConstruct
    public void init() {
        // 通过反射取得Entity的Class.
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        //获取实体类对应的JQL实体名称
        Entity annoEntity = entityClass.getAnnotation(Entity.class);
        this.entityName = annoEntity.name();
        if (StringUtils.isBlank(entityName)) {
            this.entityName = entityClass.getSimpleName();
        }

        //构造对应的JPA操作接口对象
        this.jpaRepository = new SimpleJpaRepository(entityClass, entityManager);
    }

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be {@literal null}.
     * @return the saved entity will never be {@literal null}.
     */
    @Transactional
    public T save(T entity) {
        return jpaRepository.save(entity);
    }

    /**
     * Saves all given entities.
     *
     * @param entities must not be {@literal null}.
     * @return the saved entities will never be {@literal null}.
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    @Transactional
    public Iterable<T> saveAll(Iterable<T> entities) {
        Assert.notNull(entities, "The given Iterable of entities not be null!");
        List<T> result = new ArrayList();
        for (T entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    /**
     * 基于主键查询单一数据对象
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public T findOne(ID id) {
        return jpaRepository.findById(id).orElse(null);
    }

    /**
     * 基于主键查询单一数据对象
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<T> findOptionalOne(ID id) {
        return jpaRepository.findById(id);
    }

    /**
     * 基于主键集合查询集合数据对象，如果可变参数为空则查询全部数据
     *
     * @param ids 主键集合
     * @return never {@literal null}.
     */
    @Transactional(readOnly = true)
    public List<T> findAll(final ID... ids) {
        if (ArrayUtils.isEmpty(ids)) {
            return jpaRepository.findAll();
        } else {
            return jpaRepository.findAll((root, query, builder) -> root.get("id").in(ids));
        }
    }

    /**
     * 数据删除操作
     *
     * @param entity 待操作数据
     */
    @Transactional
    public void delete(T entity) {
        jpaRepository.delete(entity);
    }

    @Transactional
    public void deleteById(ID id) {
        jpaRepository.deleteById(id);
    }

    @Transactional
    public void deleteBatch(final ID... ids) {
        if (ArrayUtils.isEmpty(ids)) {
            jpaRepository.deleteAllInBatch();
        } else {
            for (ID id : ids) {
                jpaRepository.deleteById(id);
            }
        }
    }

    /**
     * 批量数据删除操作 其实现只是简单循环集合每个元素调用 {@link #delete(AbstractPersistableEntity)}
     * 因此并无实际的Batch批量处理，如果需要数据库底层批量支持请自行实现
     *
     * @param entities 待批量操作数据集合
     * @return
     */
    @Transactional
    public void deleteAll(Iterable<T> entities) {
        Assert.notNull(entities, "The given Iterable of entities not be null!");
        for (T entity : entities) {
            delete(entity);
        }
    }

    @Transactional
    public void deleteAllById(ID[] ids) {
        Assert.notNull(ids, "The given Iterable of ids not be null!");
        for (ID id : ids) {
            deleteById(id);
        }
    }

    /**
     * count总记录数据
     *
     * @return
     */
    @Transactional(readOnly = true)
    public long count() {
        return jpaRepository.count();
    }

    /**
     * 基于Native SQL和分页(不含排序，排序直接在native sql中定义)对象查询数据集合
     *
     * @param pageable 分页(不含排序，排序直接在native sql中定义)对象
     * @param sql      Native SQL(自行组装好动态条件和排序的原生SQL语句，不含order by部分)
     * @return Map结构的集合分页对象
     */
    @Transactional(readOnly = true)
    public Page<Map> findByPageNativeSQL(Pageable pageable, String sql) {
        return findByPageNativeSQL(pageable, sql, null);
    }

    /**
     * 基于Native SQL和分页(不含排序，排序直接在native sql中定义)对象查询数据集合
     *
     * @param pageable 分页(不含排序，排序直接在native sql中定义)对象
     * @param sql      Native SQL(自行组装好动态条件和排序的原生SQL语句，不含order by部分)
     * @param orderby  order by部分
     * @return Map结构的集合分页对象
     */
    @Transactional(readOnly = true)
    public Page<Map> findByPageNativeSQL(Pageable pageable, String sql, String orderby) {
        Query query = null;
        if (StringUtils.isNotBlank(orderby)) {
            query = entityManager.createNativeQuery(sql + " " + orderby);
        } else {
            query = entityManager.createNativeQuery(sql);
        }
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        Query queryCount = entityManager.createNativeQuery("select count(*) from (" + sql + ") cnt");
        query.setFirstResult(Long.valueOf(pageable.getOffset()).intValue());
        query.setMaxResults(pageable.getPageSize());
        Object count = queryCount.getSingleResult();
        return new PageImpl(query.getResultList(), pageable, Long.valueOf(count.toString()));
    }


    public void detach(Object entity) {
        entityManager.detach(entity);
    }


    public void updateDomain(String id, String prop, String value) {

    }
}
