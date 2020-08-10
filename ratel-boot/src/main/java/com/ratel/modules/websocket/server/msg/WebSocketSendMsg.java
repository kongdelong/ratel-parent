package com.ratel.modules.websocket.server.msg;

import lombok.Data;

@Data
public class WebSocketSendMsg {
    private String type;
    private WebSocketCommandMsg data;

    public WebSocketSendMsg(String type, WebSocketCommandMsg webSocketCommandMsg) {
        this.type = type;
        this.data = webSocketCommandMsg;
    }
}
