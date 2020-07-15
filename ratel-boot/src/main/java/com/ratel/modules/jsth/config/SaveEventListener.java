package com.ratel.modules.jsth.config;

import com.ratel.framework.modules.system.domain.RatelUser;
import com.ratel.framework.utils.SecurityUtils;
import com.ratel.modules.jsth.domain.BaseMongoDocument;
import com.ratel.modules.jsth.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Slf4j
@Component
public class SaveEventListener extends AbstractMongoEventListener<Object> {

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Object> event) {
        try {
            BaseMongoDocument source = (BaseMongoDocument) event.getSource();
            RatelUser user = SecurityUtils.getRatelUserWithNoException();
            if (StringUtils.isEmpty(source.getId())) {
                source.setId(UUIDUtils.getUUID());
            }
            if (StringUtils.isEmpty(source.getCreatUserId())) {
                source.setCreatUserId(user.getUsername());
            }
            if (StringUtils.isEmpty(source.getCreateTime())) {
                source.setCreateTime(new Date());
            }
            if (StringUtils.isEmpty(source.getOpenId())) {
                source.setOpenId(user.getUsername());
            }
            if (StringUtils.isEmpty(source.getStatus())) {
                source.setStatus("1");
            }
        } catch (Exception e) {
            log.warn("SaveEventListener onBeforeConvert 监听类型转化错误", e);
        }
    }
}

