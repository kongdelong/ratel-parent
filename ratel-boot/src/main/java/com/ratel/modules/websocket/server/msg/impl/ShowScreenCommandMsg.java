package com.ratel.modules.websocket.server.msg.impl;

import com.ratel.modules.websocket.server.msg.WebSocketCommandMsg;
import lombok.Data;

@Data
public class ShowScreenCommandMsg implements WebSocketCommandMsg {
    private String command = "showScreen";
}
