package com.ratel.modules.jsth.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.framework.utils.StringUtils;
import com.ratel.modules.jsth.domain.MongoUserLocationDomain;
import com.ratel.modules.jsth.service.MongoUserLocationService;
import com.ratel.modules.security.domain.vo.AuthCredentials;
import com.ratel.modules.security.service.AuthService;
import com.ratel.modules.system.domain.SysUser;
import com.ratel.modules.system.service.SysUserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Api(tags = "微信登录")
@RestController
@RequestMapping("/api/wx/auth")
public class WxAuthController {

    @Autowired
    SysUserService sysUserService;

    @Autowired
    MongoUserLocationService mongoUserLocationService;

    @Autowired
    private AuthService authService;

    @PostMapping
    public ResponseEntity loginByWeixin(@RequestBody MongoUserLocationDomain resource, HttpServletRequest request) throws Exception {
        //得到用户的openId + sessionKey
        JSONObject jsonObject = getSessionKeyAndOpenId(resource.getCode());
        String openId = jsonObject.getString("openid");

        SysUser sysUser = sysUserService.findByUsername(openId);
        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setNickName(openId);
            sysUser.setUsername(openId);
            sysUser.setEnabled(true);
            sysUser.setDataDomain("jsth");
            sysUser = sysUserService.save(sysUser);
            resource.setUserId(sysUser.getId());
            resource.setOpenId(openId);
            mongoUserLocationService.saveLocation(resource);
        }
        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setAuthtype(AuthCredentials.QYWX);
        Map<String, Object> map = this.authService.getAuthUserMap(sysUser.getUsername(), authCredentials, request);
        return FormsHttpEntity.ok(map);
    }

    @PostMapping(value = "common")
    public ResponseEntity loginByWeixinCommon(@RequestBody MongoUserLocationDomain resource, HttpServletRequest request) throws Exception {
        //得到用户的openId + sessionKey
        JSONObject jsonObject = getSessionKeyAndOpenIdCommon(resource.getAppid(), resource.getSecret(), resource.getCode());
        String openId = jsonObject.getString("openid");

        SysUser sysUser = sysUserService.findByUsername(openId);
        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setNickName(openId);
            sysUser.setUsername(openId);
            sysUser.setEnabled(true);
            sysUser.setDataDomain("jsth");
            sysUser = sysUserService.save(sysUser);
            resource.setUserId(sysUser.getId());
            resource.setOpenId(openId);
            mongoUserLocationService.saveLocation(resource);
        }
        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setAuthtype(AuthCredentials.QYWX);
        Map<String, Object> map = this.authService.getAuthUserMap(sysUser.getUsername(), authCredentials, request);
        return FormsHttpEntity.ok(map);
    }

    @PostMapping(value = "qq")
    public ResponseEntity loginByQqCommon(@RequestBody MongoUserLocationDomain resource, HttpServletRequest request) throws Exception {
        //得到用户的openId + sessionKey
        JSONObject jsonObject = getQqSessionCommon(resource.getAppid(), resource.getSecret(), resource.getCode());
        String openId = jsonObject.getString("openid");

        SysUser sysUser = sysUserService.findByUsername(openId);
        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setNickName(openId);
            sysUser.setUsername(openId);
            sysUser.setEnabled(true);
            sysUser.setDataDomain("jsth");
            sysUser = sysUserService.save(sysUser);
            resource.setUserId(sysUser.getId());
            resource.setOpenId(openId);
            mongoUserLocationService.saveLocation(resource);
        }
        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setAuthtype(AuthCredentials.QYWX);
        Map<String, Object> map = this.authService.getAuthUserMap(sysUser.getUsername(), authCredentials, request);
        return FormsHttpEntity.ok(map);
    }

    @PostMapping(value = "toutiao")
    public ResponseEntity loginByToutiao(@RequestBody MongoUserLocationDomain resource, HttpServletRequest request) throws Exception {
        //得到用户的openId + sessionKey
        JSONObject jsonObject = getSessionKeyAndOpenIdToutiao(resource.getAppid(), resource.getSecret(), resource.getCode(), resource.getAnonymousCode());
        String openId = jsonObject.getString("openid");
        if (StringUtils.isBlank(openId)) {
            openId = jsonObject.getString("anonymous_openid");
        }
        SysUser sysUser = sysUserService.findByUsername(openId);
        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setNickName(openId);
            sysUser.setUsername(openId);
            sysUser.setEnabled(true);
            sysUser.setDataDomain("jsth");
            sysUser = sysUserService.save(sysUser);
            resource.setUserId(sysUser.getId());
            resource.setOpenId(openId);
            mongoUserLocationService.saveLocation(resource);
        }
        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setAuthtype(AuthCredentials.QYWX);
        Map<String, Object> map = this.authService.getAuthUserMap(sysUser.getUsername(), authCredentials, request);
        return FormsHttpEntity.ok(map);
    }


    /**
     * 获取微信小程序的session_key和openid
     *
     * @param code 微信前端login()方法返回的code
     * @return jsonObject
     * @author hengyang4
     */
    public JSONObject getSessionKeyAndOpenId(String code) throws Exception {
        //微信登录的code值
        String wxCode = code;
        //服务器端调用接口的url
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";
        //封装需要的参数信息
        Map<String, String> requestUrlParam = new HashMap<String, String>();
        //开发者设置中的appId
        requestUrlParam.put("appid", "wx0b59d637b6416073");
        //开发者设置中的appSecret
        requestUrlParam.put("secret", "36279f26f2f489688cba86f04a614327");
        //小程序调用wx.login返回的code
        requestUrlParam.put("js_code", wxCode);
        //默认参数
        requestUrlParam.put("grant_type", "authorization_code");

        JSONObject jsonObject = JSON.parseObject(sendPost(requestUrl, requestUrlParam));
        return jsonObject;
    }

    /**
     * 获取微信小程序的session_key和openid
     *
     * @param code 微信前端login()方法返回的code
     * @return jsonObject
     * @author hengyang4
     */
    public JSONObject getSessionKeyAndOpenIdCommon(String appid, String secret, String code) throws Exception {
        //微信登录的code值
        String wxCode = code;
        //服务器端调用接口的url
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";
        //封装需要的参数信息
        Map<String, String> requestUrlParam = new HashMap<String, String>();
        //开发者设置中的appId
        requestUrlParam.put("appid", appid);
        //开发者设置中的appSecret
        requestUrlParam.put("secret", secret);
        //小程序调用wx.login返回的code
        requestUrlParam.put("js_code", wxCode);
        //默认参数
        requestUrlParam.put("grant_type", "authorization_code");

        JSONObject jsonObject = JSON.parseObject(sendPost(requestUrl, requestUrlParam));
        return jsonObject;
    }


    /**
     * 获取微信小程序的session_key和openid
     *
     * @param code 微信前端login()方法返回的code
     * @return jsonObject
     * @author hengyang4
     */
    public JSONObject getQqSessionCommon(String appid, String secret, String code) throws Exception {

        //GET https://api.q.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
        //微信登录的code值
        String wxCode = code;
        //服务器端调用接口的url
        String requestUrl = "https://api.q.qq.com/sns/jscode2session";
        //封装需要的参数信息
        Map<String, String> requestUrlParam = new HashMap<String, String>();
        //开发者设置中的appId
        requestUrlParam.put("appid", appid);
        //开发者设置中的appSecret
        requestUrlParam.put("secret", secret);
        //小程序调用wx.login返回的code
        requestUrlParam.put("js_code", wxCode);
        //默认参数
        requestUrlParam.put("grant_type", "authorization_code");

        JSONObject jsonObject = JSON.parseObject(sendGet(requestUrl, requestUrlParam));
        return jsonObject;
    }


    /**
     * 获取微信小程序的session_key和openid
     *
     * @param code 微信前端login()方法返回的code
     * @return jsonObject
     * @author hengyang4
     */
    public JSONObject getSessionKeyAndOpenIdToutiao(String appid, String secret, String code, String anonymousCode) throws Exception {
        //服务器端调用接口的url
        String requestUrl = "https://developer.toutiao.com/api/apps/jscode2session";
        //封装需要的参数信息
        Map<String, String> requestUrlParam = new HashMap<String, String>();
        //开发者设置中的appId
        requestUrlParam.put("appid", appid);
        //开发者设置中的appSecret
        requestUrlParam.put("secret", secret);
        //小程序调用wx.login返回的code
        requestUrlParam.put("code", code);
        requestUrlParam.put("anonymous_code", anonymousCode);
        //默认参数
//        requestUrlParam.put("grant_type", "authorization_code");

        JSONObject jsonObject = JSON.parseObject(sendGet(requestUrl, requestUrlParam));
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


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @return 所代表远程资源的响应结果
     */
    public String sendGet(String url, Map<String, ?> paramMap) {
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
            URL realUrl = new URL(url + '?' + param);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            conn.connect();
//            out = new PrintWriter(conn.getOutputStream());
//            // 发送请求参数
//            out.print(param);
//            // flush输出流的缓冲
//            out.flush();
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

