package com.ratel.modules.demo.service.vo;

import com.ratel.modules.system.domain.SysDept;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DemoDomainVo {
    private Integer age;
    private String username;
    private String nickName;
    private SysDept sysDept;

}
