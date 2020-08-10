package com.ratel.modules.websocket.server.msg;

import com.ratel.modules.codeditor.domain.ToolDevice;
import lombok.Data;

@Data
public class WebSocketReceiveHelloMsg {
    private String type;
    private ToolDevice data;
}
