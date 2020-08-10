package com.ratel.modules.websocket.server.msg.impl;

import com.ratel.modules.websocket.server.msg.WebSocketCommandMsg;
import lombok.Data;

import java.util.List;

@Data
public class MultScriptCommandMsg implements WebSocketCommandMsg {
    private String id;
    private String name;
    private String script;
    private List<MultScriptCommandMsg> scripts;
    private String command = "runScripts";
}
