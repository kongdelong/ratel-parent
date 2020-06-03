package com.ratel.modules.security.domain.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AuthUser {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String code;

    private String uuid = "";

    private String clientType = "";

    @Override
    public String toString() {
        return "{username=" + username + ", password= ******}";
    }
}
