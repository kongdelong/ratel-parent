package com.ratel.modules.system.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;


@Getter
@Setter
public class DictDetailDto implements Serializable {

    private String id;

    private String label;

    private String value;

    private String sort;

    private DictSmallDto dict;

    private Timestamp createTime;
}
