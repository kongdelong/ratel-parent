package com.ratel.modules.codeditor.service.vo;

import lombok.Data;

import java.util.List;

@Data
public class RunScriptVo {
    List<String> sid;
    List<String> scriptGroupIds;
}
