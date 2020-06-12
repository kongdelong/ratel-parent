package com.ratel.framework.modules.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;

@Data
public class RatelUserImpl implements RatelUser {

    private String id;

    private String username;

    private String nickName;

    private String sex;

    @JsonIgnore
    private String password;

    private String avatar;

    private String email;

    private String phone;

    private String dept;

    private String deptId;

    @JsonIgnore
    private Collection<GrantedAuthority> authorities;

    private Boolean enabled;

    private Date createTime;

    private String dataDomain;

    @JsonIgnore
    private Date lastPasswordResetDate;

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
