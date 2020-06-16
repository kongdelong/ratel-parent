package com.ratel.modules.system.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter
public class DictDto implements Serializable {

    private String id;

    private String name;

    private String remark;

    private List<DictDetailDto> sysDictDetails;

    private Timestamp createTime;
}
