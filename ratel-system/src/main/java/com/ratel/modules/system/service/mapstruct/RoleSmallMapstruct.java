package com.ratel.modules.system.service.mapstruct;

import com.ratel.framework.domain.BaseMapstruct;
import com.ratel.modules.system.domain.SysRole;
import com.ratel.modules.system.service.dto.RoleSmallDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleSmallMapstruct extends BaseMapstruct<RoleSmallDto, SysRole> {

}
