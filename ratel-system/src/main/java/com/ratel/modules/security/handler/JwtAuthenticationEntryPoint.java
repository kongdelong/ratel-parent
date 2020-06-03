package com.ratel.modules.security.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 当用户尝试访问安全的REST资源而不提供任何凭据时，将调用此方法发送401 响应
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print(JSONObject.toJSONString(new ErrorMsg(authException == null ? "Unauthorized" : authException.getMessage())));
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException == null ? "Unauthorized" : authException.getMessage());
    }
}

@Data
class ErrorMsg {
    ErrorMsg(String msg) {
        this.msg = msg;
    }
    String msg;
}
