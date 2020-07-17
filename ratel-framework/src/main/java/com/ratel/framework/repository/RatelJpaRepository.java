package com.ratel.framework.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ratel.framework.domain.BaseNativeEntity;
import com.ratel.framework.domain.BaseUuidEntity;
import com.ratel.framework.modules.system.domain.RatelUser;
import com.ratel.framework.service.dto.RatelQueryCriteria;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.framework.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
@NoRepositoryBean
public class RatelJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private EntityManager entityManager;
    private JpaEntityInformation<T, ?> jpaEntityInformation;
    JPAQueryFactory jpaQueryFactory;

    public RatelJpaRepository(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
        this.jpaEntityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }


    @Override
    public JPAQueryFactory getJPAQueryFactory() {
        return this.jpaQueryFactory;
    }

    /**
     * 自定义查询条件转换实现
     * <p>
     * (non-Javadoc)
     */
    private Specification<T> getConditonByQuery(RatelQueryCriteria ratelQueryCriteria) {
        return new QueryToSpecification(ratelQueryCriteria);
    }

    /**
     * 封装自定义组合查询列表方法
     * <p>
     * (non-Javadoc)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll(RatelQueryCriteria ratelQueryCriteria) {
        return findAll(getConditonByQuery(ratelQueryCriteria));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll(RatelQueryCriteria ratelQueryCriteria, Sort sort) {
        return findAll(getConditonByQuery(ratelQueryCriteria), sort);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll(RatelQueryCriteria ratelQueryCriteria, Sort.Direction direction, String sortProperty) {
        Sort sort = Sort.by(direction, sortProperty);
        return findAll(getConditonByQuery(ratelQueryCriteria), sort);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<T> findAll(RatelQueryCriteria ratelQueryCriteria, Pageable pageable) {
        return findAll(getConditonByQuery(ratelQueryCriteria), pageable);
    }


    @Override
    public Optional<T> findOne(RatelQueryCriteria ratelQueryCriteria) {
        return findOne(getConditonByQuery(ratelQueryCriteria));
    }

    @Override
    public long count(RatelQueryCriteria ratelQueryCriteria) {
        return count(getConditonByQuery(ratelQueryCriteria));
    }

    /**
     * 根据唯一主键更新相关数据
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int updateById(ID id, T t, String... updateFileds) {
        CriteriaBuilder cb = entityManager.getEntityManagerFactory().getCriteriaBuilder();
        CriteriaUpdate<T> update = (CriteriaUpdate<T>) cb.createCriteriaUpdate(t.getClass());
        Root<T> root = update.from((Class<T>) t.getClass());
        for (String fieldName : updateFileds) {
            try {
                Object o = PropertyUtils.getProperty(t, fieldName);
                update.set(fieldName, o);
            } catch (Exception e) {
                log.error("update error:" + e);
            }
        }
        //定位主键信息
        Iterable<String> idAttributeNames = jpaEntityInformation.getIdAttributeNames();
        for (String key : idAttributeNames) {
            if (key != null && key != "") {
                update.where(cb.equal(root.get(key), id));
                break;
            }
        }
        return entityManager.createQuery(update).executeUpdate();
    }

    /**
     * 根据唯一主键更新相关数据
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int updateById(ID id, String property, String value) {
        CriteriaBuilder cb = entityManager.getEntityManagerFactory().getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate(this.getDomainClass());
        Root<T> root = update.from(this.getDomainClass());
        update.set(property, value);
        processCommonUpdateInfo(update);
        //定位主键信息
        Iterable<String> idAttributeNames = jpaEntityInformation.getIdAttributeNames();
        for (String key : idAttributeNames) {
            if (key != null && key != "") {
                update.where(cb.equal(root.get(key), id));
                break;
            }
        }
        return entityManager.createQuery(update).executeUpdate();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int updateById(ID id, List<String> propertys, String... values) {
        CriteriaBuilder cb = entityManager.getEntityManagerFactory().getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate(this.getDomainClass());
        Root<T> root = update.from(this.getDomainClass());
        for (int i = 0; i < values.length; i++) {
            update.set(propertys.get(i), values[i]);
        }
        processCommonUpdateInfo(update);
        //定位主键信息
        Iterable<String> idAttributeNames = jpaEntityInformation.getIdAttributeNames();
        for (String key : idAttributeNames) {
            if (key != null && key != "") {
                update.where(cb.equal(root.get(key), id));
                break;
            }
        }
        //update.where(cb.equal(root.get("id"), id));
        return entityManager.createQuery(update).executeUpdate();
    }

    private void processCommonUpdateInfo(CriteriaUpdate<T> update) {
        if (this.getDomainClass().getSuperclass() == BaseNativeEntity.class
                || this.getDomainClass().getSuperclass() == BaseUuidEntity.class) {
            RatelUser user = SecurityUtils.getRatelUserWithNoException();
            update.set("updateTime", new Date());
            update.set("updateUserId", user.getId());
            update.set("updateUserName", user.getNickName());
        }
    }

    /**
     * 自定义更新update方法
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int update(RatelQueryCriteria ratelQueryCriteria, List<String> propertys, String... values) {
        CriteriaBuilder cb = entityManager.getEntityManagerFactory().getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate(this.getDomainClass());
        Root<T> root = update.from(this.getDomainClass());
        for (int i = 0; i < values.length; i++) {
            update.set(propertys.get(i), values[i]);
        }
        processCommonUpdateInfo(update);
        update.where(QueryHelp.getPredicate(root, ratelQueryCriteria, cb));
        return entityManager.createQuery(update).executeUpdate();
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int update(RatelQueryCriteria ratelQueryCriteria, T t, String... propertys) {
        CriteriaBuilder cb = entityManager.getEntityManagerFactory().getCriteriaBuilder();
        CriteriaUpdate<T> update = (CriteriaUpdate<T>) cb.createCriteriaUpdate(t.getClass());
        Root<T> root = update.from((Class<T>) t.getClass());
        for (String fieldName : propertys) {
            try {
                Object o = PropertyUtils.getProperty(t, fieldName);
                update.set(fieldName, o);
            } catch (Exception e) {
                log.error("update error:" + e);
            }
        }
        processCommonUpdateInfo(update);
        update.where(QueryHelp.getPredicate(root, ratelQueryCriteria, cb));
        return entityManager.createQuery(update).executeUpdate();
    }
}
