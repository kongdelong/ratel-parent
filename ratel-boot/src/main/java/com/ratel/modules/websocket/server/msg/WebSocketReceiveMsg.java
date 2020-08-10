package com.ratel.modules.websocket.server.msg;

import lombok.Data;

import java.util.Map;

@Data
public class WebSocketReceiveMsg {
    private String type;
    private Map<String, String> data;
}
