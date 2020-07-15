package com.ratel.modules.gen.service;

import com.ratel.framework.utils.StringUtils;
import com.ratel.modules.gen.domain.GenConfig;
import com.ratel.modules.gen.repository.GenConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@CacheConfig(cacheNames = "genConfig")
public class GenConfigService {

    @Autowired
    private GenConfigRepository genConfigRepository;


    @Cacheable(key = "#p0")
    public GenConfig find(String tableName) {
        GenConfig genConfig = genConfigRepository.findByTableName(tableName);
        if (genConfig == null) {
            return new GenConfig(tableName);
        }
        return genConfig;
    }

    @CachePut(key = "#p0")
    public GenConfig update(String tableName, GenConfig genConfig) {
        // 如果 api 路径为空，则自动生成路径
        if (StringUtils.isBlank(genConfig.getApiPath())) {
            String separator = File.separator;
            String[] paths;
            String symbol = "\\";
            if (symbol.equals(separator)) {
                paths = genConfig.getPath().split("\\\\");
            } else {
                paths = genConfig.getPath().split(File.separator);
            }
            StringBuilder api = new StringBuilder();
            for (String path : paths) {
                api.append(path);
                api.append(separator);
                if ("src".equals(path)) {
                    api.append("api");
                    break;
                }
            }
            genConfig.setApiPath(api.toString());
        }
        return genConfigRepository.save(genConfig);
    }
}
