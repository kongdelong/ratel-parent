package com.ratel.modules.quartz.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.quartz.domain.QuartzJob;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuartzJobRepository extends BaseRepository<QuartzJob, Long> {

    /**
     * 查询启用的任务
     *
     * @return List
     */
    List<QuartzJob> findByIsPauseIsFalse();
}
