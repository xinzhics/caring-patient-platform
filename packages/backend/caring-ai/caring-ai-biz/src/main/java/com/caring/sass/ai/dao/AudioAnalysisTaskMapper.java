package com.caring.sass.ai.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.ai.entity.AudioAnalysisTask;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 音频解析任务表
 * </p>
 *
 * @author leizhi
 * @date 2025-12-12
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface AudioAnalysisTaskMapper extends SuperMapper<AudioAnalysisTask> {

}
