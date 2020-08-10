package com.ratel.modules.codeditor.service;

import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.FileUtil;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.framework.utils.ValidationUtil;
import com.ratel.modules.codeditor.domain.ToolScriptGroup;
import com.ratel.modules.codeditor.repository.ToolScriptGroupRepository;
import com.ratel.modules.codeditor.service.dto.ToolScriptGroupQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ToolScriptGroupService extends BaseService<ToolScriptGroup, String> {

    @Autowired
    private ToolScriptGroupRepository toolScriptGroupRepository;

    public List<ToolScriptGroup> queryAll(ToolScriptGroupQueryCriteria criteria) {
        return toolScriptGroupRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
    }

    public ToolScriptGroup findById(String id) {
        return toolScriptGroupRepository.findById(id).orElseGet(ToolScriptGroup::new);
    }

    public List<ToolScriptGroup> findByPid(String pid) {
        return toolScriptGroupRepository.findByPid(pid);
    }


    @Transactional(rollbackFor = Exception.class)
    public ToolScriptGroup create(ToolScriptGroup resources) {
        return toolScriptGroupRepository.save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(ToolScriptGroup resources) {
        if (resources.getId().equals(resources.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }
        ToolScriptGroup dept = toolScriptGroupRepository.findById(resources.getId()).orElseGet(ToolScriptGroup::new);
        ValidationUtil.isNull(dept.getId(), "ToolScriptGroup", "id", resources.getId());
        resources.setId(dept.getId());
        toolScriptGroupRepository.save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<ToolScriptGroup> deptDtos) {
        for (ToolScriptGroup deptDto : deptDtos) {
            toolScriptGroupRepository.deleteById(deptDto.getId());
        }
    }

    public void download(List<ToolScriptGroup> deptDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ToolScriptGroup deptDTO : deptDtos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("脚本组名称", deptDTO.getName());
            map.put("脚本组状态", deptDTO.getEnabled() ? "启用" : "停用");
            map.put("创建日期", deptDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    public Set<ToolScriptGroup> getDeleteDepts(List<ToolScriptGroup> menuList, Set<ToolScriptGroup> deptDtos) {
        for (ToolScriptGroup dept : menuList) {
            deptDtos.add(dept);
            List<ToolScriptGroup> depts = toolScriptGroupRepository.findByPid(dept.getId());
            if (depts != null && depts.size() != 0) {
                getDeleteDepts(depts, deptDtos);
            }
        }
        return deptDtos;
    }

}
