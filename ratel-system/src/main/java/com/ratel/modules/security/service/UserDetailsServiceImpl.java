package com.ratel.modules.security.service;

import com.ratel.framework.GlobalConstant;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.modules.security.domain.vo.JwtUser;
import com.ratel.modules.system.domain.SysUser;
import com.ratel.modules.system.service.SysRoleService;
import com.ratel.modules.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        SysUser user = sysUserService.findByName(username);
        if (user == null) {
            throw new BadRequestException("账号不存在");
        } else {
            if (!user.getEnabled()) {
                throw new BadRequestException("账号未激活");
            }
            if (!GlobalConstant.STATUS_VALUE.equals(user.getEnable())) {
                throw new BadRequestException("账号状态异常");
            }
            return createJwtUser(user);
        }
    }

    private UserDetails createJwtUser(SysUser user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getNickName(),
                user.getSex(),
                user.getPassword(),
                user.getSysUserAvatar() != null ? user.getSysUserAvatar().getPath() : "",
                user.getEmail(),
                user.getPhone(),
                user.getSysDept() != null ? user.getSysDept().getName() : "",
                user.getSysDept() != null ? user.getSysDept().getId() : "",
                sysRoleService.mapToGrantedAuthorities(user),
//                sysRoleService.getUserReleNames(user),
//                sysRoleService.getUserRelePermissions(user),
                user.getEnabled(),
                user.getCreateTime(),
                "system",
                user.getLastPasswordResetTime()
        );
    }
}
