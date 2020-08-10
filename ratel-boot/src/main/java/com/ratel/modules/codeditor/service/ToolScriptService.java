package com.ratel.modules.codeditor.service;

import com.ratel.framework.exception.EntityExistException;
import com.ratel.framework.exception.EntityNotFoundException;
import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.FileUtil;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.framework.utils.ValidationUtil;
import com.ratel.modules.codeditor.domain.ToolScript;
import com.ratel.modules.codeditor.repository.ToolScriptRepository;
import com.ratel.modules.codeditor.service.dto.ToolScriptQueryCriteria;
import com.ratel.modules.system.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ToolScriptService extends BaseService<ToolScript, String> {

    @Autowired
    private ToolScriptRepository toolScriptRepository;

    public Page queryAll(ToolScriptQueryCriteria criteria, Pageable pageable) {
        Page<ToolScript> page = toolScriptRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return page;
    }

    public List<ToolScript> queryAll(ToolScriptQueryCriteria criteria) {
        List<ToolScript> scripts = toolScriptRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return scripts;
    }

    public void download(List<ToolScript> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ToolScript userDTO : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("脚本名", userDTO.getUsername());
            map.put("状态", userDTO.getEnabled() ? "启用" : "禁用");
            map.put("脚本组", userDTO.getToolScriptGroup().getName());
            map.put("创建日期", userDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    public ToolScript findById(String id) {
        ToolScript user = toolScriptRepository.findById(id).orElseGet(ToolScript::new);
        ValidationUtil.isNull(user.getId(), "User", "id", id);
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public ToolScript create(ToolScript resources) {
        if (toolScriptRepository.findByUsername(resources.getUsername()) != null) {
            throw new EntityExistException(SysUser.class, "username", resources.getUsername());
        }
//        if (sysUserRepository.findByEmail(resources.getEmail()) != null) {
//            throw new EntityExistException(SysUser.class, "email", resources.getEmail());
//        }
        return toolScriptRepository.save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(ToolScript resources) {
        ToolScript user = toolScriptRepository.findById(resources.getId()).orElseGet(ToolScript::new);
        ValidationUtil.isNull(user.getId(), "ToolScript", "id", resources.getId());
        ToolScript user1 = toolScriptRepository.findByUsername(user.getUsername());
        if (user1 != null && !user.getId().equals(user1.getId())) {
            throw new EntityExistException(SysUser.class, "username", resources.getUsername());
        }
        user.setUsername(resources.getUsername());
        user.setEnabled(resources.getEnabled());
        user.setToolScriptGroup(resources.getToolScriptGroup());
        user.setToolBaseScripts(resources.getToolBaseScripts());
        user.setContent(resources.getContent());
        toolScriptRepository.save(user);
    }


    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<String> ids) {
        for (String id : ids) {
            toolScriptRepository.deleteById(id);
        }
    }


    public ToolScript findByName(String userName) {
        ToolScript user = toolScriptRepository.findByUsername(userName);
        if (user == null) {
            throw new EntityNotFoundException(SysUser.class, "name", userName);
        } else {
            return user;
        }
    }

    public ToolScript findByUsername(String userName) {
        ToolScript user = toolScriptRepository.findByUsername(userName);
        return user;
    }
}




