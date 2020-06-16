package com.ratel.framework.modules.system.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

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

    public Collection getRoles();

    public String getSex();

    public void setSex(String sex);

    public String getAvatar();

    public void setAvatar(String avatar);

    public String getEmail();

    public void setEmail(String email);

    public String getPhone();

    public void setPhone(String phone);

    public String getDeptName();

    public void setDeptName(String deptName);

    @Override
    public Collection<GrantedAuthority> getAuthorities();

    public void setAuthorities(Collection<GrantedAuthority> authorities);

    public Boolean getEnabled();

    public void setEnabled(Boolean enabled);

    public Date getCreateTime();

    public void setCreateTime(Date createTime);

    public String getDataDomain();

    public void setDataDomain(String dataDomain);

    public Date getLastPasswordResetDate();

    public void setLastPasswordResetDate(Date lastPasswordResetDate);

}
