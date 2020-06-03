package com.ratel.modules.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.db.PageResult;
import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.FileUtil;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.framework.utils.ValidationUtil;
import com.ratel.modules.system.domain.SysDict;
import com.ratel.modules.system.domain.SysDictDetail;
import com.ratel.modules.system.mapper.SysDictMapper;
import com.ratel.modules.system.repository.SysDictRepository;
import com.ratel.modules.system.service.dto.SysDictQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@CacheConfig(cacheNames = "sysDict")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class SysDictService extends BaseService<SysDict, String> {

    @Autowired
    private SysDictRepository sysDictRepository;

    @Autowired
    private SysDictMapper sysDictMapper;

    @Cacheable
    public Page queryAll(SysDictQueryCriteria dict, Pageable pageable) {
        return sysDictRepository.findAll((root, query, cb) -> QueryHelp.getPredicate(root, dict, cb), pageable);
    }

    public List<SysDict> queryAll(SysDictQueryCriteria dict) {
        return sysDictRepository.findAll((root, query, cb) -> QueryHelp.getPredicate(root, dict, cb));
    }

    @Cacheable(key = "#p0")
    public SysDict findById(String id) {
        SysDict sysDict = sysDictRepository.findById(id).orElseGet(SysDict::new);
        ValidationUtil.isNull(sysDict.getId(), "Dict", "id", id);
        return sysDict;
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public SysDict create(SysDict resources) {
        return sysDictRepository.save(resources);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDict resources) {
        SysDict sysDict = sysDictRepository.findById(resources.getId()).orElseGet(SysDict::new);
        ValidationUtil.isNull(sysDict.getId(), "Dict", "id", resources.getId());
        resources.setId(sysDict.getId());
        sysDictRepository.save(resources);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        sysDictRepository.deleteById(id);
    }

    public void download(List<SysDict> sysDictDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SysDict sysDictDTO : sysDictDtos) {
            if (CollectionUtil.isNotEmpty(sysDictDTO.getSysDictDetails())) {
                for (SysDictDetail sysDictDetail : sysDictDTO.getSysDictDetails()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("字典名称", sysDictDTO.getName());
                    map.put("字典描述", sysDictDTO.getRemark());
                    map.put("字典标签", sysDictDetail.getLabel());
                    map.put("字典值", sysDictDetail.getValue());
                    map.put("创建日期", sysDictDetail.getCreateTime());
                    list.add(map);
                }
            } else {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("字典名称", sysDictDTO.getName());
                map.put("字典描述", sysDictDTO.getRemark());
                map.put("字典标签", null);
                map.put("字典值", null);
                map.put("创建日期", sysDictDTO.getCreateTime());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    public SysDict getOne(String id) {
        return sysDictMapper.getOne(id);
    }

    public PageResult findPage() {
        return sysDictMapper.findPage();
    }
}
