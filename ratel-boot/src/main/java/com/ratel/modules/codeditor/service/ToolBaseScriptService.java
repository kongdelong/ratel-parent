package com.ratel.modules.codeditor.service;

import com.ratel.framework.exception.EntityExistException;
import com.ratel.framework.exception.EntityNotFoundException;
import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.FileUtil;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.framework.utils.ValidationUtil;
import com.ratel.modules.codeditor.domain.ToolBaseScript;
import com.ratel.modules.codeditor.domain.ToolScript;
import com.ratel.modules.codeditor.repository.ToolBaseScriptRepository;
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
public class ToolBaseScriptService extends BaseService<ToolScript, String> {

    @Autowired
    private ToolBaseScriptRepository toolBaseScriptRepository;

    public Page queryAll(ToolScriptQueryCriteria criteria, Pageable pageable) {
        Page<ToolBaseScript> page = toolBaseScriptRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return page;
    }

    public List<ToolBaseScript> queryAll(ToolScriptQueryCriteria criteria) {
        List<ToolBaseScript> scripts = toolBaseScriptRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return scripts;
    }

    public void download(List<ToolBaseScript> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ToolBaseScript userDTO : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("脚本名", userDTO.getUsername());
            map.put("状态", userDTO.getEnabled() ? "启用" : "禁用");
            map.put("创建日期", userDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    public ToolBaseScript findById(String id) {
        ToolBaseScript user = toolBaseScriptRepository.findById(id).orElseGet(ToolBaseScript::new);
        ValidationUtil.isNull(user.getId(), "User", "id", id);
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public ToolBaseScript create(ToolBaseScript resources) {
        if (toolBaseScriptRepository.findByUsername(resources.getUsername()) != null) {
            throw new EntityExistException(SysUser.class, "username", resources.getUsername());
        }
//        if (sysUserRepository.findByEmail(resources.getEmail()) != null) {
//            throw new EntityExistException(SysUser.class, "email", resources.getEmail());
//        }
        return toolBaseScriptRepository.save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(ToolBaseScript resources) {
        ToolBaseScript user = toolBaseScriptRepository.findById(resources.getId()).orElseGet(ToolBaseScript::new);
        ValidationUtil.isNull(user.getId(), "ToolScript", "id", resources.getId());
        ToolBaseScript user1 = toolBaseScriptRepository.findByUsername(user.getUsername());
        if (user1 != null && !user.getId().equals(user1.getId())) {
            throw new EntityExistException(SysUser.class, "username", resources.getUsername());
        }
        user.setUsername(resources.getUsername());
        user.setEnabled(resources.getEnabled());
        user.setContent(resources.getContent());
        toolBaseScriptRepository.save(user);
    }


    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<String> ids) {
        for (String id : ids) {
            toolBaseScriptRepository.deleteById(id);
        }
    }


    public ToolBaseScript findByName(String userName) {
        ToolBaseScript user = toolBaseScriptRepository.findByUsername(userName);
        if (user == null) {
            throw new EntityNotFoundException(SysUser.class, "name", userName);
        } else {
            return user;
        }
    }

    public ToolBaseScript findByUsername(String userName) {
        ToolBaseScript user = toolBaseScriptRepository.findByUsername(userName);
        return user;
    }
}




