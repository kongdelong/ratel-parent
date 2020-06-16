package com.ratel.framework.utils;

import cn.hutool.json.JSONObject;
import com.ratel.framework.config.RatelProperties;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.modules.system.domain.RatelUser;
import com.ratel.framework.modules.system.domain.RatelUserImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * 获取当前登录的用户
 */
public class SecurityUtils {

    public static UserDetails getUserDetails() {
        UserDetails userDetails;
        try {
            userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            RatelProperties ratelProperties = SpringContextHolder.getBean("ratelProperties");
            if (ratelProperties.getSecurity()) {
                throw new BadRequestException(HttpStatus.UNAUTHORIZED, "登录状态过期");
            } else {
                RatelUserImpl ratelUser = new RatelUserImpl();
                ratelUser.setId(ratelProperties.getUserId());
                ratelUser.setUsername(ratelProperties.getUsername());
                ratelUser.setDeptId(ratelProperties.getDeptId());
                ratelUser.setDeptName(ratelProperties.getDeptName());
                Set<String> set = new HashSet<>();
                Collections.addAll(set, ratelProperties.getRoles().split(","));
                ratelUser.setRoles(set);
                userDetails = ratelUser;
            }
        }
        return userDetails;
    }


    public static RatelUser getRatelUserWithNoException() {
        RatelUser ratelUser;
        try {
            ratelUser = (RatelUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            ratelUser = new RatelUser() {
                @Override
                public String getId() {
                    return null;
                }

                @Override
                public void setId(String id) {

                }

                @Override
                public String getUsername() {
                    return null;
                }

                @Override
                public void setUsername(String username) {

                }

                @Override
                public String getNickName() {
                    return null;
                }

                @Override
                public void setNickName(String nickName) {

                }

                @Override
                public String getPassword() {
                    return null;
                }

                @Override
                public void setPassword(String password) {

                }

                @Override
                public String getDeptId() {
                    return null;
                }

                @Override
                public void setDeptId(String id) {

                }

                @Override
                public Collection getRoles() {
                    return null;
                }

                @Override
                public String getSex() {
                    return null;
                }

                @Override
                public void setSex(String sex) {

                }

                @Override
                public String getAvatar() {
                    return null;
                }

                @Override
                public void setAvatar(String avatar) {

                }

                @Override
                public String getEmail() {
                    return null;
                }

                @Override
                public void setEmail(String email) {

                }

                @Override
                public String getPhone() {
                    return null;
                }

                @Override
                public void setPhone(String phone) {

                }

                @Override
                public String getDeptName() {
                    return null;
                }

                @Override
                public void setDeptName(String deptName) {

                }

                @Override
                public Collection<GrantedAuthority> getAuthorities() {
                    return null;
                }

                @Override
                public void setAuthorities(Collection<GrantedAuthority> authorities) {

                }

                @Override
                public Boolean getEnabled() {
                    return null;
                }

                @Override
                public void setEnabled(Boolean enabled) {

                }

                @Override
                public Date getCreateTime() {
                    return null;
                }

                @Override
                public void setCreateTime(Date createTime) {

                }

                @Override
                public String getDataDomain() {
                    return null;
                }

                @Override
                public void setDataDomain(String dataDomain) {

                }

                @Override
                public Date getLastPasswordResetDate() {
                    return null;
                }

                @Override
                public void setLastPasswordResetDate(Date lastPasswordResetDate) {

                }

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
            };
        }
        return ratelUser;
    }

    /**
     * 获取系统用户名称
     *
     * @return 系统用户名称
     */
    public static String getUsername() {
        Object obj = getUserDetails();
        return new JSONObject(obj).get("username", String.class);
    }
}
