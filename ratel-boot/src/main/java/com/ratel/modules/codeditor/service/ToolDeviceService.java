package com.ratel.modules.codeditor.service;

import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.FileUtil;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.modules.codeditor.domain.ToolBaseScript;
import com.ratel.modules.codeditor.domain.ToolDevice;
import com.ratel.modules.codeditor.domain.ToolScript;
import com.ratel.modules.codeditor.repository.ToolDeviceRepository;
import com.ratel.modules.codeditor.repository.ToolScriptGroupRepository;
import com.ratel.modules.codeditor.repository.ToolScriptRepository;
import com.ratel.modules.codeditor.service.dto.ToolDeviceQueryCriteria;
import com.ratel.modules.websocket.server.WebSocketServer;
import com.ratel.modules.websocket.server.msg.WebSocketSendMsg;
import com.ratel.modules.websocket.server.msg.impl.DeleteScriptCommandMsg;
import com.ratel.modules.websocket.server.msg.impl.MultScriptCommandMsg;
import com.ratel.modules.websocket.server.msg.impl.ShowScreenCommandMsg;
import com.ratel.modules.websocket.server.msg.impl.StopScriptCommandMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ToolDeviceService extends BaseService<ToolDevice, String> {

    @Autowired
    private ToolDeviceRepository toolDeviceRepository;

    @Autowired
    private ToolScriptGroupRepository toolScriptGroupRepository;

    @Autowired
    private ToolScriptRepository toolScriptRepository;

    public Page<ToolDevice> queryAll(ToolDeviceQueryCriteria criteria, Pageable pageable) {
        return toolDeviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
    }

    public List<ToolDevice> queryAll(ToolDeviceQueryCriteria criteria) {
        return toolDeviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
    }

    public ToolDevice findById(String id) {
        return toolDeviceRepository.findById(id).orElseGet(ToolDevice::new);
    }

    @Transactional(rollbackFor = Exception.class)
    public ToolDevice create(ToolDevice resources) {
        return toolDeviceRepository.save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTag(String id, String tag) {
        toolDeviceRepository.updateTag(id, tag);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<String> ids) {
        for (String id : ids) {
            toolDeviceRepository.deleteById(id);
        }
    }

    public void download(List<ToolDevice> deptDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ToolDevice deptDTO : deptDtos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("设备SID", deptDTO.getSid());
            map.put("设备名", deptDTO.getDeviceName());
            map.put("创建日期", deptDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Transactional(rollbackFor = Exception.class)
    public void checkClient(String sid, ToolDevice data) {
        ToolDevice toolDevice = toolDeviceRepository.findBySid(sid);
        if (toolDevice == null) {
            data.setSid(sid);
            toolDeviceRepository.save(data);
        } else {
            toolDevice.setEnable("1");
            toolDevice.setIp(data.getIp());
            toolDevice.setDeviceName(data.getDeviceName());
            toolDevice.setAppVersion(data.getAppVersion());
            toolDevice.setAppVersionCode(data.getAppVersionCode());
            toolDeviceRepository.save(toolDevice);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void closeClient(String sid) {
        ToolDevice toolDevice = toolDeviceRepository.findBySid(sid);
        toolDevice.setEnable("0");
        toolDeviceRepository.save(toolDevice);
    }

    @Transactional(rollbackFor = Exception.class)
    public void runScript(List<String> sids, List<String> scriptGroupIds) throws IOException {
        for (String sid : sids) {
            List<ToolScript> scripts = toolScriptRepository.findByToolScriptGroupIn(scriptGroupIds);
            List<MultScriptCommandMsg> scriptStr = new ArrayList<>();
            for (ToolScript script : scripts) {
                MultScriptCommandMsg msg = new MultScriptCommandMsg();
                msg.setName(script.getUsername());
                msg.setId(script.getId());
                String str = "";
                for (ToolBaseScript tbs : script.getToolBaseScripts()) {
                    str = str + tbs.getContent();
                }
                str = str + script.getContent();
                msg.setScript(str);
                scriptStr.add(msg);
            }
            MultScriptCommandMsg webSocketCommandMsg = new MultScriptCommandMsg();
            webSocketCommandMsg.setId(UUID.randomUUID().toString());
            webSocketCommandMsg.setName(UUID.randomUUID().toString());
            webSocketCommandMsg.setScripts(scriptStr);
            if (!WebSocketServer.sendInfoToSid(new WebSocketSendMsg("command", webSocketCommandMsg), sid)) {
                toolDeviceRepository.setSidOffline(sid);
            }
        }
    }

    public void showScreen(String sid) throws IOException {
        ShowScreenCommandMsg msg = new ShowScreenCommandMsg();
        WebSocketServer.sendInfo(new WebSocketSendMsg("command", msg), sid);
    }

    public void stopScrip(String sid) throws IOException {
        StopScriptCommandMsg msg = new StopScriptCommandMsg();
        WebSocketServer.sendInfo(new WebSocketSendMsg("command", msg), sid);
    }

    public void saveScript(List<String> sids, List<String> scriptGroupIds) throws IOException {
        for (String sid : sids) {
            List<ToolScript> scripts = toolScriptRepository.findByToolScriptGroupIn(scriptGroupIds);
            List<MultScriptCommandMsg> scriptStr = new ArrayList<>();
            for (ToolScript script : scripts) {
                MultScriptCommandMsg msg = new MultScriptCommandMsg();
                msg.setName(script.getUsername());
                msg.setId(script.getId());
                String str = "";
                for (ToolBaseScript tbs : script.getToolBaseScripts()) {
                    str = str + tbs.getContent();
                }
                str = str + script.getContent();
                msg.setScript(str);
                scriptStr.add(msg);
            }
            MultScriptCommandMsg webSocketCommandMsg = new MultScriptCommandMsg();
            webSocketCommandMsg.setCommand("saveScripts");
            webSocketCommandMsg.setId(UUID.randomUUID().toString());
            webSocketCommandMsg.setName(UUID.randomUUID().toString());
            webSocketCommandMsg.setScripts(scriptStr);
            WebSocketServer.sendInfo(new WebSocketSendMsg("command", webSocketCommandMsg), sid);
        }
    }

    public void deleteScript(String sid) throws IOException {
        DeleteScriptCommandMsg msg = new DeleteScriptCommandMsg();
        WebSocketServer.sendInfo(new WebSocketSendMsg("command", msg), sid);
    }

    public void stopConn(String sid) throws IOException {
        WebSocketServer.stopConn(sid);
    }

    @Transactional(rollbackFor = Exception.class)
    public void runScriptLocal(List<String> sids, List<String> scriptGroupIds) throws IOException {
        for (String sid : sids) {
            List<ToolScript> scripts = toolScriptRepository.findByToolScriptGroupIn(scriptGroupIds);
            List<MultScriptCommandMsg> scriptStr = new ArrayList<>();
            for (ToolScript script : scripts) {
                MultScriptCommandMsg msg = new MultScriptCommandMsg();
                msg.setCommand("runScriptsLocal");
                msg.setName(script.getUsername());
                msg.setId(script.getId());
                scriptStr.add(msg);
            }
            MultScriptCommandMsg webSocketCommandMsg = new MultScriptCommandMsg();
            webSocketCommandMsg.setCommand("runScriptsLocal");
            webSocketCommandMsg.setScripts(scriptStr);
            webSocketCommandMsg.setId(UUID.randomUUID().toString());
            webSocketCommandMsg.setName(UUID.randomUUID().toString());
            if (!WebSocketServer.sendInfoToSid(new WebSocketSendMsg("command", webSocketCommandMsg), sid)) {
                toolDeviceRepository.setSidOffline(sid);
            }
        }
    }

}
