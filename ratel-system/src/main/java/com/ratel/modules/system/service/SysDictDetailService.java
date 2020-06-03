package com.ratel.modules.system.service;

import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.framework.utils.ValidationUtil;
import com.ratel.modules.system.domain.SysDictDetail;
import com.ratel.modules.system.repository.SysDictDetailRepository;
import com.ratel.modules.system.service.dto.SysDictDetailQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@CacheConfig(cacheNames = "sysDictDetail")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class SysDictDetailService extends BaseService<SysDictDetail, String> {

    @Autowired
    private SysDictDetailRepository sysDictDetailRepository;

    @Cacheable
    public Page queryAll(SysDictDetailQueryCriteria criteria, Pageable pageable) {
        return sysDictDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
    }

    @Cacheable(key = "#p0")
    public SysDictDetail findById(String id) {
        SysDictDetail sysDictDetail = sysDictDetailRepository.findById(id).orElseGet(SysDictDetail::new);
        ValidationUtil.isNull(sysDictDetail.getId(), "DictDetail", "id", id);
        return sysDictDetail;
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public SysDictDetail create(SysDictDetail resources) {
        return sysDictDetailRepository.save(resources);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDictDetail resources) {
        SysDictDetail sysDictDetail = sysDictDetailRepository.findById(resources.getId()).orElseGet(SysDictDetail::new);
        ValidationUtil.isNull(sysDictDetail.getId(), "DictDetail", "id", resources.getId());
        resources.setId(sysDictDetail.getId());
        sysDictDetailRepository.save(resources);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        sysDictDetailRepository.deleteById(id);
    }
}
