package com.ratel.framework.domain.validation;

import javax.validation.GroupSequence;

/**
 * @ClassName: Group
 * @Description: 分组排序
 **/
@GroupSequence({SaveGroup.class, UpdateGroup.class})
public interface Group {

}
