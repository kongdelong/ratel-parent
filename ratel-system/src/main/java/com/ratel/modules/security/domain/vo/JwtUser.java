package com.ratel.modules.security.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ratel.framework.modules.system.domain.RatelUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class JwtUser implements RatelUser {

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

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Collection getRoles() {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }
}
