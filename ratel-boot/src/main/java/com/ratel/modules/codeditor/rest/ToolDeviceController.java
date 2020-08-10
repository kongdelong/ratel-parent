package com.ratel.modules.codeditor.rest;

import com.ratel.framework.http.FormsHttpEntity;
import com.ratel.modules.codeditor.domain.ToolDevice;
import com.ratel.modules.codeditor.domain.ToolScript;
import com.ratel.modules.codeditor.service.ToolDeviceService;
import com.ratel.modules.codeditor.service.dto.ToolDeviceQueryCriteria;
import com.ratel.modules.codeditor.service.vo.RunScriptVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(tags = "系统：设备")
@RestController
@RequestMapping("/api/tool/device")
public class ToolDeviceController {

    @Autowired
    private ToolDeviceService toolDeviceService;

    @GetMapping(value = "/download")
    @PreAuthorize("@ratel.check('device:list')")
    public void download(HttpServletResponse response, ToolDeviceQueryCriteria criteria) throws IOException {
        toolDeviceService.download(toolDeviceService.queryAll(criteria), response);
    }

    @GetMapping
    @PreAuthorize("@ratel.check('device:list')")
    public ResponseEntity<Object> list(ToolDeviceQueryCriteria criteria, Pageable pageable) {
        return FormsHttpEntity.ok(toolDeviceService.queryAll(criteria, pageable));
    }



    @PostMapping(value = "/updateTag")
    @PreAuthorize("@ratel.check('device:run')")
    public ResponseEntity<Object> updateTag(@RequestBody ToolDevice toolDevice) throws IOException {
        toolDeviceService.updateTag(toolDevice.getId(),toolDevice.getTag());
        return FormsHttpEntity.ok();
    }


    @PostMapping(value = "/runScript")
    @PreAuthorize("@ratel.check('device:run')")
    public ResponseEntity<Object> runScript(@RequestBody RunScriptVo runScriptVo) throws IOException {
        toolDeviceService.runScript(runScriptVo.getSid(),runScriptVo.getScriptGroupIds());
        return FormsHttpEntity.ok();
    }

    @PostMapping(value = "/stopScrip")
    @PreAuthorize("@ratel.check('device:run')")
    public ResponseEntity<Object> stopScrip(@RequestBody ToolDevice toolDevice) throws IOException {
        toolDeviceService.stopScrip(toolDevice.getSid());
        return FormsHttpEntity.ok();
    }

    @PostMapping(value = "/showScreen")
    @PreAuthorize("@ratel.check('device:run')")
    public ResponseEntity<Object> showScreen(@RequestBody ToolDevice toolDevice) throws IOException {
        toolDeviceService.showScreen(toolDevice.getSid());
        return FormsHttpEntity.ok();
    }

    @PostMapping(value = "/saveScript")
    @PreAuthorize("@ratel.check('device:run')")
    public ResponseEntity<Object> saveScript(@RequestBody RunScriptVo runScriptVo) throws IOException {
        toolDeviceService.saveScript(runScriptVo.getSid(),runScriptVo.getScriptGroupIds());
        return FormsHttpEntity.ok();
    }

    @PostMapping(value = "/deleteScript")
    @PreAuthorize("@ratel.check('device:run')")
    public ResponseEntity<Object> deleteScript(@RequestBody ToolDevice toolDevice) throws IOException {
        toolDeviceService.deleteScript(toolDevice.getSid());
        return FormsHttpEntity.ok();
    }

    @PostMapping(value = "/stopConn")
    @PreAuthorize("@ratel.check('device:run')")
    public ResponseEntity<Object> stopConn(@RequestBody ToolDevice toolDevice) throws IOException {
        toolDeviceService.stopConn(toolDevice.getSid());
        return FormsHttpEntity.ok();
    }

    @PostMapping(value = "/runScriptLocal")
    @PreAuthorize("@ratel.check('device:run')")
    public ResponseEntity<Object> runScriptLocal(@RequestBody RunScriptVo runScriptVo) throws IOException {
        toolDeviceService.runScriptLocal(runScriptVo.getSid(),runScriptVo.getScriptGroupIds());
        return FormsHttpEntity.ok();
    }

}
