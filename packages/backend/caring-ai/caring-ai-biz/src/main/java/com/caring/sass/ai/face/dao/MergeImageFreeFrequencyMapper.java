package com.caring.sass.ai.face.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.face.MergeImageFreeFrequency;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 融合图片免费次数
 * </p>
 *
 * @author 杨帅
 * @date 2024-06-21
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface MergeImageFreeFrequencyMapper extends SuperMapper<MergeImageFreeFrequency> {

}
