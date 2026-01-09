package com.caring.sass.file.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;

import com.caring.sass.file.entity.ScreenshotTask;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 视频截图任务
 * </p>
 *
 * @author 杨帅
 * @date 2023-04-06
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ScreenshotTaskMapper extends SuperMapper<ScreenshotTask> {

}
