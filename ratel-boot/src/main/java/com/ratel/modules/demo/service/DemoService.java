package com.ratel.modules.demo.service;

import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.modules.demo.domain.DemoDomain;
import com.ratel.modules.demo.repository.DemoRepository;
import com.ratel.modules.demo.service.dto.DemoQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DemoService extends BaseService<DemoDomain, Long> {

    @Autowired
    private DemoRepository demoRepository;


    public List<DemoDomain> queryAll(DemoQueryCriteria criteria) {
        return demoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
    }

    public Page queryAll(DemoQueryCriteria criteria, Pageable pageable) {
        Page<DemoDomain> page = demoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return page;
    }

    public DemoDomain getDemoDomain(DemoDomain demoDomain) {
        return demoRepository.getOne(demoDomain.getId());
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            demoRepository.updateStatus(ids[i]);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNickName(DemoDomain demoDomain) {
        demoRepository.updateNickName(demoDomain);
    }
}
