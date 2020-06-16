package com.ratel.framework.repository;

import com.ratel.framework.service.dto.RatelQueryCriteria;
import com.ratel.framework.utils.QueryHelp;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 自定义Query语言转Specification
 */
public class QueryToSpecification implements Specification {
    private RatelQueryCriteria ratelQueryCriteria;


    public QueryToSpecification(RatelQueryCriteria ratelQueryCriteria) {
        super();
        this.ratelQueryCriteria = ratelQueryCriteria;
    }

    /**
     * 【请在此输入描述文字】
     * <p>
     * (non-Javadoc)
     *
     * @see Specification#toPredicate(Root, CriteriaQuery, CriteriaBuilder)
     */
    @Override
    public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder cb) {
        return QueryHelp.getPredicate(root, this.ratelQueryCriteria, cb);
    }

}
