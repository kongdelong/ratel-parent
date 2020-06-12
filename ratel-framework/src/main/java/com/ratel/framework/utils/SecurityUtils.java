package com.ratel.framework.utils;

import cn.hutool.json.JSONObject;
import com.ratel.framework.config.RatelProperties;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.framework.modules.system.domain.RatelUser;
import com.ratel.framework.modules.system.domain.RatelUserImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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
                RatelUser ratelUser = new RatelUserImpl();
                ratelUser.setUsername(ratelProperties.getUsername());
                userDetails = ratelUser;
            }
        }
        return userDetails;
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
