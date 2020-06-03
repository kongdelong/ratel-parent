package com.ratel.framework.domain.auditable;

import java.util.Date;

public interface DefaultAuditable {

    void setCreateUserName(String userName);

    void setCreateUserId(String accountId);

    void setDataDomain(String dataDomain);

    String getDataDomain();

    void setCreateTime(Date createDate);

    Date getCreateTime();

    void setUpdateUserName(String userName);

    void setUpdateUserId(String accountId);

    void setUpdateTime(Date updateDate);

    Date getUpdateTime();

    void setDeptId(String deptId);

}
