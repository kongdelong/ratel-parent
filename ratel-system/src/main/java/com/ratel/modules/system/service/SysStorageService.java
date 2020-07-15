package com.ratel.modules.system.service;

import cn.hutool.core.util.ObjectUtil;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.FileUtil;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.framework.utils.StringUtils;
import com.ratel.framework.utils.ValidationUtil;
import com.ratel.modules.system.domain.SysStorage;
import com.ratel.modules.system.repository.SysStorageRepository;
import com.ratel.modules.system.service.dto.SysStorageQueryCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@CacheConfig(cacheNames = "sysStorage")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class SysStorageService extends BaseService<SysStorage, String> {

    @Autowired
    private SysStorageRepository sysStorageRepository;

    @Value("${ratel.file.path}")
    private String path;

    @Value("${ratel.file.maxSize}")
    private long maxSize;

    @Cacheable
    public Object queryAll(SysStorageQueryCriteria criteria, Pageable pageable) {
        return sysStorageRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
    }

    @Cacheable
    public List<SysStorage> queryAll(SysStorageQueryCriteria criteria) {
        return sysStorageRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
    }

    @Cacheable(key = "#p0")
    public SysStorage findById(String id) {
        SysStorage localStorage = sysStorageRepository.findById(id).orElseGet(SysStorage::new);
        ValidationUtil.isNull(localStorage.getId(), "SysStorage", "id", id);
        return localStorage;
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public SysStorage create(String name, MultipartFile multipartFile) {
        FileUtil.checkSize(maxSize, multipartFile.getSize());
        String suffix = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        String type = FileUtil.getFileType(suffix);
        File file = FileUtil.upload(multipartFile, path + type + File.separator);
        if (ObjectUtil.isNull(file)) {
            throw new BadRequestException("上传失败");
        }
        try {
            name = StringUtils.isBlank(name) ? FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename()) : name;
            SysStorage localStorage = new SysStorage(
                    file.getName(),
                    name,
                    suffix,
                    file.getPath(),
                    "",
                    type,
                    FileUtil.getSize(multipartFile.getSize()),
                    "file/" + file.getPath().replace(path, "").replace(File.separator, "/"),
                    ""
            );
            return sysStorageRepository.save(localStorage);
        } catch (Exception e) {
            FileUtil.del(file);
            throw e;
        }
    }


    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public SysStorage createImage(String name, MultipartFile multipartFile) {
        FileUtil.checkSize(maxSize, multipartFile.getSize());
        String suffix = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        String type = FileUtil.getFileType(suffix);
        File file = FileUtil.upload(multipartFile, path + type + File.separator);
        if (ObjectUtil.isNull(file)) {
            throw new BadRequestException("上传失败");
        }
        String fileSmall = FileUtil.resize(file, multipartFile.getSize());
        if (ObjectUtil.isNull(fileSmall)) {
            log.warn("生成缩略图失败" + file.getPath());
        }
        try {
            name = StringUtils.isBlank(name) ? FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename()) : name;
            SysStorage localStorage = new SysStorage(
                    file.getName(),
                    name,
                    suffix,
                    file.getPath(),
                    fileSmall,
                    type,
                    FileUtil.getSize(multipartFile.getSize()),
                    "file/" + file.getPath().replace(path, "").replace(File.separator, "/"),
                    "file/" + fileSmall.replace(path, "").replace(File.separator, "/")
            );
            return sysStorageRepository.save(localStorage);
        } catch (Exception e) {
            FileUtil.del(file);
            throw e;
        }
    }


    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(SysStorage resources) {
        SysStorage localStorage = sysStorageRepository.findById(resources.getId()).orElseGet(SysStorage::new);
        ValidationUtil.isNull(localStorage.getId(), "LocalStorage", "id", resources.getId());
        localStorage.copy(resources);
        sysStorageRepository.save(localStorage);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(String[] ids) {
        for (String id : ids) {
            SysStorage storage = sysStorageRepository.findById(id).orElseGet(SysStorage::new);
            FileUtil.del(storage.getPath());
            FileUtil.del(storage.getSmallPath());
            sysStorageRepository.delete(storage);
        }
    }

    public void download(List<SysStorage> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SysStorage localStorageDTO : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("文件名", localStorageDTO.getRealName());
            map.put("备注名", localStorageDTO.getName());
            map.put("文件类型", localStorageDTO.getType());
            map.put("文件大小", localStorageDTO.getSize());
            map.put("操作人", localStorageDTO.getCreateUserName());
            map.put("创建日期", localStorageDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
