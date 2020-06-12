package com.ratel.modules.system.service;

import com.ratel.framework.exception.EntityExistException;
import com.ratel.framework.exception.EntityNotFoundException;
import com.ratel.framework.modules.cache.RatelCacheProvider;
import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.FileUtil;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.framework.utils.SecurityUtils;
import com.ratel.framework.utils.ValidationUtil;
import com.ratel.modules.system.domain.SysRole;
import com.ratel.modules.system.domain.SysUser;
import com.ratel.modules.system.domain.SysUserAvatar;
import com.ratel.modules.system.repository.SysUserAvatarRepository;
import com.ratel.modules.system.repository.SysUserRepository;
import com.ratel.modules.system.service.dto.SysUserQueryCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@CacheConfig(cacheNames = "sysUser")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class SysUserService extends BaseService<SysUser, String> {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SysUserAvatarRepository sysUserAvatarRepository;

    @Autowired
    private RatelCacheProvider ratelCacheProvider;

    @Value("${ratel.file.avatar}")
    private String avatar;

    @Cacheable
    public Page queryAll(SysUserQueryCriteria criteria, Pageable pageable) {
        Page<SysUser> page = sysUserRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return page;
    }

    @Cacheable
    public List<SysUser> queryAll(SysUserQueryCriteria criteria) {
        List<SysUser> users = sysUserRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return users;
    }

    @Cacheable(key = "#p0")
    public SysUser findById(String id) {
        SysUser user = sysUserRepository.findById(id).orElseGet(SysUser::new);
        ValidationUtil.isNull(user.getId(), "User", "id", id);
        return user;
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public SysUser create(SysUser resources) {
        if (sysUserRepository.findByUsername(resources.getUsername()) != null) {
            throw new EntityExistException(SysUser.class, "username", resources.getUsername());
        }
        if (sysUserRepository.findByEmail(resources.getEmail()) != null) {
            throw new EntityExistException(SysUser.class, "email", resources.getEmail());
        }
        return sysUserRepository.save(resources);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(SysUser resources) {
        SysUser user = sysUserRepository.findById(resources.getId()).orElseGet(SysUser::new);
        ValidationUtil.isNull(user.getId(), "User", "id", resources.getId());
        SysUser user1 = sysUserRepository.findByUsername(user.getUsername());
        SysUser user2 = sysUserRepository.findByEmail(user.getEmail());

        if (user1 != null && !user.getId().equals(user1.getId())) {
            throw new EntityExistException(SysUser.class, "username", resources.getUsername());
        }

        if (user2 != null && !user.getId().equals(user2.getId())) {
            throw new EntityExistException(SysUser.class, "email", resources.getEmail());
        }

        // 如果用户的角色改变了，需要手动清理下缓存
        if (!resources.getSysRoles().equals(user.getSysRoles())) {
            String key = "role::loadPermissionByUser:" + user.getUsername();
            ratelCacheProvider.del(key);
            key = "role::findByUsers_Id:" + user.getId();
            ratelCacheProvider.del(key);
        }

        user.setUsername(resources.getUsername());
        user.setEmail(resources.getEmail());
        user.setEnabled(resources.getEnabled());
        user.setSysRoles(resources.getSysRoles());
        user.setSysDept(resources.getSysDept());
        user.setPhone(resources.getPhone());
        user.setNickName(resources.getNickName());
        user.setSex(resources.getSex());
        sysUserRepository.save(user);
    }


    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void updateCenter(SysUser resources) {
        SysUser user = sysUserRepository.findById(resources.getId()).orElseGet(SysUser::new);
        user.setNickName(resources.getNickName());
        user.setPhone(resources.getPhone());
        user.setSex(resources.getSex());
        sysUserRepository.save(user);
    }


    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<String> ids) {
        for (String id : ids) {
            sysUserRepository.deleteById(id);
        }
    }


    @Cacheable(key = "'loadUserByUsername:'+#p0")
    public SysUser findByName(String userName) {
        SysUser user;
        if (ValidationUtil.isEmail(userName)) {
            user = sysUserRepository.findByEmail(userName);
        } else {
            user = sysUserRepository.findByUsername(userName);
        }
        if (user == null) {
            throw new EntityNotFoundException(SysUser.class, "name", userName);
        } else {
            return user;
        }
    }


    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void updatePass(String username, String pass) {
        sysUserRepository.updatePass(username, pass, new Date());
    }


    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public SysUserAvatar updateAvatar(MultipartFile multipartFile) {
        SysUser user = sysUserRepository.findByUsername(SecurityUtils.getUsername());
        SysUserAvatar userAvatar = user.getSysUserAvatar();
//        String oldPath = "";
//        if (userAvatar != null) {
//            oldPath = userAvatar.getPath();
//        }
        File file = FileUtil.upload(multipartFile, avatar);
        assert file != null;
        userAvatar = sysUserAvatarRepository.save(new SysUserAvatar(userAvatar, file.getName(), file.getPath(), FileUtil.getSize(multipartFile.getSize())));
        user.setSysUserAvatar(userAvatar);
        sysUserRepository.save(user);
        return userAvatar;
//        if (StringUtils.isNotBlank(oldPath)) {
//            FileUtil.del(oldPath);
//        }
    }


    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(String username, String email) {
        sysUserRepository.updateEmail(username, email);
    }


    public void download(List<SysUser> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SysUser userDTO : queryAll) {
            List<String> roles = userDTO.getSysRoles().stream().map(SysRole::getName).collect(Collectors.toList());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", userDTO.getUsername());
            map.put("头像", userDTO.getSysUserAvatar());
            map.put("邮箱", userDTO.getEmail());
            map.put("状态", userDTO.getEnabled() ? "启用" : "禁用");
            map.put("手机号码", userDTO.getPhone());
            map.put("角色", roles);
            map.put("部门", userDTO.getSysDept().getName());
            map.put("最后修改密码的时间", userDTO.getLastPasswordResetTime());
            map.put("创建日期", userDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
