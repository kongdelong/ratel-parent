package com.ratel.modules.system.service.mapstruct;

import com.ratel.framework.domain.BaseMapstruct;
import com.ratel.modules.system.domain.SysDict;
import com.ratel.modules.system.service.dto.DictDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictMapstruct extends BaseMapstruct<DictDto, SysDict> {

}
