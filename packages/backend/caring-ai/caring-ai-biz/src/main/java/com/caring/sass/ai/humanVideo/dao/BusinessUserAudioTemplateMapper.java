package com.caring.sass.ai.humanVideo.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.humanVideo.BusinessUserAudioTemplate;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 用户提交的录音素材
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-14
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface BusinessUserAudioTemplateMapper extends SuperMapper<BusinessUserAudioTemplate> {

}
