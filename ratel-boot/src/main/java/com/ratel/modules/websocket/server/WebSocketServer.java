package com.ratel.modules.websocket.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ratel.framework.utils.SpringContextHolder;
import com.ratel.modules.codeditor.service.ToolDeviceService;
import com.ratel.modules.websocket.server.msg.WebSocketReceiveHelloMsg;
import com.ratel.modules.websocket.server.msg.WebSocketSendMsg;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/webSocket/{sid}")
@Slf4j
@Component
@Data
public class WebSocketServer {

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    private static ConcurrentHashMap<String, WebSocketServer> webSocketSetMap = new ConcurrentHashMap<String, WebSocketServer>();


    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收sid
     */
    private String sid = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        //如果存在就先删除一个，防止重复推送消息
        for (WebSocketServer webSocket : webSocketSet) {
            if (webSocket.sid.equals(sid)) {
                webSocketSetMap.remove(sid);
                webSocketSet.remove(webSocket);
            }
        }
        webSocketSetMap.put(sid, this);
        webSocketSet.add(this);
        this.sid = sid;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        log.warn("sid:" + sid + "，链接关闭");
        ToolDeviceService toolDeviceService = SpringContextHolder.getBean(ToolDeviceService.class);
        toolDeviceService.closeClient(sid);
        webSocketSet.remove(this);
    }

    //{"type":"bytes_command","md5":"2fff845bf17505dbd9da81bb4acadd69","data":{"id":"/Users/dongchuang/Documents/jsth-js","name":"/Users/dongchuang/Documents/jsth-js","command":"run_project"}}

//	{
//		"type": "command",
//			"data": {
//				"id": "/Users/dongchuang/Documents/jsth-js/main.js",
//				"name": "/Users/dongchuang/Documents/jsth-js/main.js",
//				"script": "let {\n    test\n} = require('./test')(runtime, this)\n\n\nlet config = {\n    baseUri: 'http://www.dsqhost.com',\n    wxPackage: 'com.tencent.mm',\n    dyPackage: 'com.ss.android.ugc.aweme',\n    dyAppname: '抖音短视频',\n    ttPackage: 'com.ss.android.article.news',\n    ttAppname: '今日头条',\n\n    perVideoWatchTime: 25, //每隔视频观看10秒\n    halfDeviceHeight: device.height / 2,\n    halfDeviceWidth: device.width / 2,\n    videoSwipeDistance: (device.height / 2) - 100, //视频下滑的长度 px\n\n    timeout: 6000,\n\n    sdFilePath: files.getSdcardPath(),\n    baseFilePath: files.getSdcardPath() + \"/jsth/\",\n    imageFilePath: files.getSdcardPath() + \"/jsth/image/\",\n    execFilePath: files.getSdcardPath() + \"/jsth/exec/\"\n}\n\ntry {\n    auto.waitFor()\n} catch (e) {\n    warnInfo('auto.waitFor()不可用')\n    auto()\n}\n\nif (!requestScreenCapture()) {\n    toast('请求图片失败');\n    exit();\n}\n\ninit()\n\n//let wsUri = URI.create(\"ws://localhost:8000/webSocket/999999\");\n// let ws = new RatelWebSocket(wsUri);\n\nobj = {\n    onOpen: function (webSocket, response) {\n        toastLog(\"onOpen\")\n    },\n    onMessage: function (webSocket, text) {\n        toastLog(text)\n    },\n    onMessage: function (webSocket, bytes) {\n\n    },\n    onClosing: function (webSocket, code, reason) {\n\n    },\n    onClosed: function (webSocket, code, reason) {\n\n    },\n    onFailure: function (webSocket, t, response) {\n\n    }\n}\n\n// var url = \"ws://192.168.199.210:8000/webSocket/999999\";\n// var listener = new RatelWebSocketListener(obj);\n\n// handler  = new RatelWebSocketHandler(url,listener)\n\n// toastLog(handler)\n\n// handler.setMsg(\"0\")\n\n// 监听他的各种事件\n// ws.on(\"open\", (res, ws) => {\n//     log(\"WebSocket已连接\");\n// }).on(\"failure\", (err, res, ws) => {\n//     log(\"WebSocket连接失败\");\n//     console.error(err);\n// }).on(\"closing\", (code, reason, ws) => {\n//     log(\"WebSocket关闭中\");\n// }).on(\"text\", (text, ws) => {\n//     console.info(\"收到文本消息: \", text);\n// }).on(\"binary\", (bytes, ws) => {\n//     console.info(\"收到二进制消息:\");\n//     console.info(\"hex: \", bytes.hex());\n//     console.info(\"base64: \", bytes.base64());\n//     console.info(\"md5: \", bytes.md5());\n//     console.info(\"size: \", bytes.size());\n//     console.info(\"bytes: \", bytes.toByteArray());\n// }).on(\"closed\", (code, reason, ws) => {\n//     log(\"WebSocket已关闭: code = %d, reason = %s\", code, reason);\n// });\n\n// 新建一个WebSocket\n// 指定web socket的事件回调在当前线程（好处是没有多线程问题要处理，坏处是不能阻塞当前线程，包括死循环）\n// 不加后面的参数则回调在IO线程\n// let ws = web.newWebSocket(\"wss://echo.websocket.org\", {\n//     eventThread: 'this'\n// });\n\n\n// engines.execScriptFile(config.execFilePath + \"process/TtProcess.js\", {\n//     path: [config.execFilePath]\n// });\n\nfunction execServerScriptFile(path) {\n    var serverScriptFile = download(path, \"1.js\");\n    engines.execScriptFile(serverScriptFile);\n}\n\n\n\n/**\n * 初始化\n */\nfunction init() {\n    files.create(config.baseFilePath);\n    files.create(config.imageFilePath);\n    files.create(config.execFilePath);\n\n    //execServerScriptFile(\"https://jsth.dsqhost.com/jsth/app/file/images/1.jpg\")\n\n\n    download(\"https://jsth.dsqhost.com/jsth/app/file/images/1.jpg\", \"1.jpg\")\n    download(\"https://jsth.dsqhost.com/jsth/app/file/images/2.jpg\", \"2.jpg\")\n    download(\"https://jsth.dsqhost.com/jsth/app/file/images/4.jpg\", \"4.jpg\")\n    download(\"https://jsth.dsqhost.com/jsth/app/file/images/5.jpg\", \"5.jpg\")\n}\n/**\n * 文件下载\n * @param {*} path \n * @param {*} name \n */\nfunction download(path, name) {\n    var xzfh = http.get(path, {})\n    if (xzfh.statusCode != 200) {\n        toast(\"文件：“+name+”，请求失败\");\n        return false\n    }\n    if (xzfh.headers['Content-Disposition']) {\n        name = extract(xzfh.headers['Content-Disposition'], 'filename=\"', '\"')\n    }\n    files.writeBytes(config.imageFilePath + name, xzfh.body.bytes());\n    return config.imageFilePath + name\n}",
//				"command": "run"
//			}
//	}

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来" + sid + "的信息:" + message);

        WebSocketReceiveHelloMsg receiveMsg = JSON.parseObject(message, WebSocketReceiveHelloMsg.class);
        if (receiveMsg.getType().equals("hello")) {
            ToolDeviceService toolDeviceService = SpringContextHolder.getBean(ToolDeviceService.class);
            receiveMsg.getData().setIp(getRemoteAddress(session).getHostString());
            toolDeviceService.checkClient(sid, receiveMsg.getData());
        }

        JSONObject jsonObject = JSONObject.parseObject(message);
        try {
            //{"type":"hello","data":{"server_version":2}}
            if ("hello".equals(jsonObject.get("type"))) {
                this.sendMessage("{\"type\":\"hello\",\"data\":{\"server_version\":2}}");
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error("sid:" + sid + "，消息发送失败");
        }
//        //群发消息
//        for (WebSocketServer item : webSocketSet) {
//            try {
//                item.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("sid:" + sid + "，链接发生错误");
        ToolDeviceService toolDeviceService = SpringContextHolder.getBean(ToolDeviceService.class);
        toolDeviceService.closeClient(sid);
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 关闭链接
     * @param sid
     * @throws IOException
     */
    public static void stopConn(@PathParam("sid") String sid) throws IOException {
        WebSocketServer item = webSocketSetMap.get(sid);
        item.getSession().close();
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(WebSocketSendMsg socketMsg, @PathParam("sid") String sid) throws IOException {
        String message = JSONObject.toJSONString(socketMsg);
        log.info("推送消息到" + sid + "，推送内容:" + message);
        for (WebSocketServer item : webSocketSet) {
            try {
                //这里可以设定只推送给这个sid的，为null则全部推送
                if (sid == null) {
                    item.sendMessage(message);
                } else if (item.sid.equals(sid)) {
                    item.sendMessage(message);
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static Boolean sendInfoToSid(WebSocketSendMsg socketMsg, @PathParam("sid") String sid) throws IOException {
        String message = JSONObject.toJSONString(socketMsg);
        log.info("推送消息到" + sid + "，推送内容:" + message);
        WebSocketServer item = webSocketSetMap.get(sid);
        if (item != null) {
            item.sendMessage(message);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketServer that = (WebSocketServer) o;
        return Objects.equals(session, that.session) &&
                Objects.equals(sid, that.sid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, sid);
    }

    public static InetSocketAddress getRemoteAddress(Session session) {
        if (session == null) {
            return null;
        }
        RemoteEndpoint.Async async = session.getAsyncRemote();

        //在Tomcat 8.0.x版本有效
//		InetSocketAddress addr = (InetSocketAddress) getFieldInstance(async,"base#sos#socketWrapper#socket#sc#remoteAddress");
        //在Tomcat 8.5以上版本有效
        InetSocketAddress addr = (InetSocketAddress) getFieldInstance(async,"base#socketWrapper#socket#sc#remoteAddress");
        return addr;
    }
    private static Object getFieldInstance(Object obj, String fieldPath) {
        String fields[] = fieldPath.split("#");
        for (String field : fields) {
            obj = getField(obj, obj.getClass(), field);
            if (obj == null) {
                return null;
            }
        }

        return obj;
    }

    private static Object getField(Object obj, Class<?> clazz, String fieldName) {
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field field;
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
            }
        }

        return null;
    }


}
