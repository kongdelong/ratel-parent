package com.ratel.modules.system.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.system.domain.SysUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface SysUserRepository extends BaseRepository<SysUser, String> {

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return /
     */
    SysUser findByUsername(String username);

    /**
     * 根据邮箱查询
     *
     * @param email 邮箱
     * @return /
     */
    SysUser findByEmail(String email);

    /**
     * 修改密码
     *
     * @param username              用户名
     * @param pass                  密码
     * @param lastPasswordResetTime /
     */
    @Modifying
    @Query(value = "update sys_user set password = ?2 , last_password_reset_time = ?3 where username = ?1", nativeQuery = true)
    void updatePass(String username, String pass, Date lastPasswordResetTime);

    /**
     * 修改邮箱
     *
     * @param username 用户名
     * @param email    邮箱
     */
    @Modifying
    @Query(value = "update sys_user set email = ?2 where username = ?1", nativeQuery = true)
    void updateEmail(String username, String email);

}
