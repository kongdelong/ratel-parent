package com.ratel.framework.modules.system.domain;

import org.springframework.security.core.userdetails.UserDetails;

public interface RatelUser extends UserDetails {

    String getId();

    void setId(String id);

    String getUsername();

    void setUsername(String username);

    String getNickName();

    void setNickName(String nickName);

    String getPassword();

    void setPassword(String password);

    String getDeptId();

    void setDeptId(String id);

}
