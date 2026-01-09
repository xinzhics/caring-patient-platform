package com.caring.sass.ai.podcast.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.podcast.PodcastAudioTask;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 播客音频任务
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-12
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface PodcastAudioTaskMapper extends SuperMapper<PodcastAudioTask> {

}
