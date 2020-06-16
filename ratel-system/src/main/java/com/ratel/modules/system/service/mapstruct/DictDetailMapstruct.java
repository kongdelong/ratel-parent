package com.ratel.modules.system.service.mapstruct;

import com.ratel.framework.domain.BaseMapstruct;
import com.ratel.modules.system.domain.SysDictDetail;
import com.ratel.modules.system.service.dto.DictDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", uses = {DictSmallMapstruct.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictDetailMapstruct extends BaseMapstruct<DictDetailDto, SysDictDetail> {

}
