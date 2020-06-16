package com.ratel.modules.system.service.mapstruct;

import com.ratel.framework.domain.BaseMapstruct;
import com.ratel.modules.system.domain.SysDict;
import com.ratel.modules.system.service.dto.DictSmallDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictSmallMapstruct extends BaseMapstruct<DictSmallDto, SysDict> {

}
