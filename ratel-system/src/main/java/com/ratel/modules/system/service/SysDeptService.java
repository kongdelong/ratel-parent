package com.ratel.modules.system.service;

import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.FileUtil;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.framework.utils.ValidationUtil;
import com.ratel.modules.system.domain.SysDept;
import com.ratel.modules.system.repository.SysDeptRepository;
import com.ratel.modules.system.service.dto.SysDeptQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "sysDept")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class SysDeptService extends BaseService<SysDept, Long> {

    @Autowired
    private SysDeptRepository sysDeptRepository;

    @Cacheable
    public List<SysDept> queryAll(SysDeptQueryCriteria criteria) {
        return sysDeptRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
    }

    @Cacheable(key = "#p0")
    public SysDept findById(String id) {
        return sysDeptRepository.findById(id).orElseGet(SysDept::new);
    }

    @Cacheable
    public List<SysDept> findByPid(String pid) {
        return sysDeptRepository.findByPid(pid);
    }

    public Set<SysDept> findByRoleIds(String id) {
        return sysDeptRepository.findBySysRoles_Id(id);
    }

    @Cacheable
    public Object buildTree(List<SysDept> deptDtos) {
        Set<SysDept> trees = new LinkedHashSet<>();
        Set<SysDept> depts = new LinkedHashSet<>();
        List<String> deptNames = deptDtos.stream().map(SysDept::getName).collect(Collectors.toList());
        boolean isChild;
        for (SysDept deptDTO : deptDtos) {
            isChild = false;
            if ("0".equals(deptDTO.getPid().toString())) {
                trees.add(deptDTO);
            }
            for (SysDept it : deptDtos) {
                if (it.getPid().equals(deptDTO.getId())) {
                    isChild = true;
                    if (deptDTO.getChildren() == null) {
                        deptDTO.setChildren(new ArrayList<>());
                    }
                    deptDTO.getChildren().add(it);
                }
            }
            if (isChild) {
                depts.add(deptDTO);
            } else if (!deptNames.contains(sysDeptRepository.findNameById(deptDTO.getPid()))) {
                depts.add(deptDTO);
            }
        }

        if (CollectionUtils.isEmpty(trees)) {
            trees = depts;
        }

        Integer totalElements = deptDtos.size();

        Map<String, Object> map = new HashMap<>(2);
        map.put("totalElements", totalElements);
        map.put("content", CollectionUtils.isEmpty(trees) ? deptDtos : trees);
        return map;
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public SysDept create(SysDept resources) {
        return sysDeptRepository.save(resources);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDept resources) {
        if (resources.getId().equals(resources.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }
        SysDept dept = sysDeptRepository.findById(resources.getId()).orElseGet(SysDept::new);
        ValidationUtil.isNull(dept.getId(), "Dept", "id", resources.getId());
        resources.setId(dept.getId());
        sysDeptRepository.save(resources);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<SysDept> deptDtos) {
        for (SysDept deptDto : deptDtos) {
            sysDeptRepository.deleteById(deptDto.getId());
        }
    }

    public void download(List<SysDept> deptDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SysDept deptDTO : deptDtos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("部门名称", deptDTO.getName());
            map.put("部门状态", deptDTO.getEnabled() ? "启用" : "停用");
            map.put("创建日期", deptDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    public Set<SysDept> getDeleteDepts(List<SysDept> menuList, Set<SysDept> deptDtos) {
        for (SysDept dept : menuList) {
            deptDtos.add(dept);
            List<SysDept> depts = sysDeptRepository.findByPid(dept.getId());
            if (depts != null && depts.size() != 0) {
                getDeleteDepts(depts, deptDtos);
            }
        }
        return deptDtos;
    }
}
