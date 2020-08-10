package com.ratel.modules.demo.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/api/demo")
@Slf4j
public class DemoController {

    @GetMapping(value = "/code")
    public String code(@RequestParam(required = false) String code,
                       @RequestParam(required = false) String error,
                       HttpServletRequest request,
                       HttpServletResponse response) throws Exception {

        JSONObject object = getSessionKeyAndOpenId(code);
        request.getSession().setAttribute("token", object);
        response.sendRedirect("http://localhost:9000/code.html");
        return null;
    }


    @GetMapping(value = "/token")
    public ResponseEntity<Object> token(HttpServletRequest request) throws Exception {
        JSONObject object = (JSONObject) request.getSession().getAttribute("token");
        return ResponseEntity.ok(object);
    }

    public JSONObject getSessionKeyAndOpenId(String code) throws Exception {
        //服务器端调用接口的url
        String requestUrl = "http://localhost:8000/oauth/token";
        //封装需要的参数信息
        Map<String, String> requestUrlParam = new HashMap<String, String>();
        //开发者设置中的client_id
        requestUrlParam.put("client_id", "SampleClientId");
        //开发者设置中的client_secret
        requestUrlParam.put("client_secret", "36279f26f2f489688cba86f04a614327");
        //小程序调用wx.login返回的code
        requestUrlParam.put("code", code);
        //默认参数
        requestUrlParam.put("grant_type", "authorization_code");

        JSONObject jsonObject = JSON.parseObject(sendPost(requestUrl, requestUrlParam));
        return jsonObject;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @return 所代表远程资源的响应结果
     */
    public String sendPost(String url, Map<String, ?> paramMap) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        String param = "";
        Iterator<String> it = paramMap.keySet().iterator();

        while (it.hasNext()) {
            String key = it.next();
            param += key + "=" + paramMap.get(key) + "&";
        }

        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

}
