package com.ratel.modules.security.rest;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Api(tags = "系统：首页跳转")
public class IndexController {
    @RequestMapping("/")
    public String index()
    {
        return "forward:/index.html";
    }
}
