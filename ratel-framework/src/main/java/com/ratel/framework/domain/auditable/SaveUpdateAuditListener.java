package com.ratel.framework.domain.auditable;

import com.ratel.framework.GlobalConstant;
import com.ratel.framework.modules.system.domain.RatelUser;
import com.ratel.framework.utils.SecurityUtils;
import com.ratel.framework.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * 审计记录记录创建和修改信息
 *
 * @see AuditingEntityListener
 */
@Slf4j
public class SaveUpdateAuditListener {

    @PrePersist
    public void touchForCreate(Object target) {
        if (!(target instanceof DefaultAuditable)) {
            return;
        }
        DefaultAuditable auditable = (DefaultAuditable) target;
        if (auditable.getCreateTime() == null) {
            auditable.setCreateTime(new Date());
            //设置初始更新时间为创建时间，便于有些业务按照更新时间降序排列的需求
            auditable.setUpdateTime(new Date());
        }

        try {
            RatelUser user = (RatelUser) SecurityUtils.getUserDetails();
            if (StringUtils.isBlank(auditable.getCreateUserId())) {
                auditable.setCreateUserId(user.getId());
                auditable.setCreateUserName(user.getNickName());
                auditable.setDeptId(user.getDeptId());
                auditable.setUpdateUserId(user.getId());
                auditable.setUpdateUserName(user.getNickName());
            }
        } catch (Exception e) {
            log.warn("insert - 审计记录记录创建和修改信息修改失败");
        }
        auditable.setDataDomain(GlobalConstant.DEFAULT_VALUE);
    }

    @PreUpdate
    public void touchForUpdate(Object target) {
        if (!(target instanceof DefaultAuditable)) {
            return;
        }
        DefaultAuditable auditable = (DefaultAuditable) target;
        auditable.setUpdateTime(new Date());
        try {
            RatelUser user = (RatelUser) SecurityUtils.getUserDetails();
            if (StringUtils.isBlank(auditable.getUpdateUserId())) {
                auditable.setUpdateUserId(user.getId());
                auditable.setUpdateUserName(user.getNickName());
            }
        } catch (Exception e) {
            log.warn("upate - 审计记录记录创建和修改信息修改失败");
        }
    }
}
