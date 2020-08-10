package com.ratel.modules.websocket.server.msg.impl;

import com.ratel.modules.websocket.server.msg.WebSocketCommandMsg;
import lombok.Data;

import java.util.List;

@Data
public class StopScriptCommandMsg implements WebSocketCommandMsg {
    private String command = "stopAll";
}
