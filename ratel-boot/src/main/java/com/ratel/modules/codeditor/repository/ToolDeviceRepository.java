package com.ratel.modules.codeditor.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.codeditor.domain.ToolDevice;
import com.ratel.modules.codeditor.domain.ToolScript;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ToolDeviceRepository extends BaseRepository<ToolDevice, String> {

    ToolDevice findBySid(String sid);

    @Modifying
    @Query(value = "update tool_device set tag = ?2 where id=?1", nativeQuery = true)
    void updateTag(String id, String tag);

    @Modifying
    @Query(value = "update tool_device set enable = '0' where sid=?1", nativeQuery = true)
    void setSidOffline(String sid);
}
