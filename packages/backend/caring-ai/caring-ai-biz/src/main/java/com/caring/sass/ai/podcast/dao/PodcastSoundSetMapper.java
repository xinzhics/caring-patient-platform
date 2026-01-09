package com.caring.sass.ai.podcast.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.ai.entity.podcast.PodcastSoundSet;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 播客声音设置
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-12
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface PodcastSoundSetMapper extends SuperMapper<PodcastSoundSet> {

}
