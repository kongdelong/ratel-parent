package com.ratel.framework.domain.auditable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @ClassName: JpaAuditorAware
 * @Description:根据你需要返回的类型修改这个T，比如我需要返回的是字符串，就是String。需要注意的是，类需要加上@Component以便spring扫描到，否则不起作用。
 * @author: 郭秀志 jbcode@126.com
 * @date: 2020/5/1 8:54
 * @Copyright:
 */
//@Component
@Slf4j
public class JpaAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String userId = null;//SecurityUtils.getCurrentUserId();
        if (userId != null) {
            return Optional.of(userId);
        } else {
            return Optional.of("bank_router-task");
        }

    }
}
