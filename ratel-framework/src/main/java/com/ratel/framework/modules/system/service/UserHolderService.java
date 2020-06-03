package com.ratel.framework.modules.system.service;

import com.ratel.framework.modules.system.domain.RatelUser;

public interface UserHolderService {
    RatelUser getAuthUserDetails();
}
