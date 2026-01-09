package com.caring.sass.ai.article.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.article.ArticleUserNotice;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 科普用户系统通知
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-26
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ArticleUserNoticeMapper extends SuperMapper<ArticleUserNotice> {

}
