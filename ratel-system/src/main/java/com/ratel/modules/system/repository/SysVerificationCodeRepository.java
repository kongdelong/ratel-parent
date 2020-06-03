package com.ratel.modules.system.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.system.domain.SysVerificationCode;
import org.springframework.stereotype.Repository;

@Repository
public interface SysVerificationCodeRepository extends BaseRepository<SysVerificationCode, Long> {

    /**
     * 获取有效的验证码
     *
     * @param scenes 业务场景，如重置密码，重置邮箱等等
     * @param type   类型
     * @param value  值
     * @return VerificationCode
     */
    SysVerificationCode findByScenesAndTypeAndValueAndStatusIsTrue(String scenes, String type, String value);
}
