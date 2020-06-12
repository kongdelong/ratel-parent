package com.ratel.modules.security.domain.vo;

import lombok.Data;

@Data
public class AuthCredentials {

    public final static String PASSWORD = "password";//密码方式登录

    public final static String QYWX = "qywx";//密码方式登录

    private String authtype;// passowrd wx token
    private String password;
    private String token;


    public AuthCredentials() {

    }

    public AuthCredentials(String password) {
        this.authtype = "password";
        this.password = password;
    }

    public AuthCredentials(String authtype, String password) {
        this.authtype = authtype;
        this.password = password;
    }
}
