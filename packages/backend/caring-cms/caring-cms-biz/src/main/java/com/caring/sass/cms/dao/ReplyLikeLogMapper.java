package com.caring.sass.cms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.cms.entity.ReplyLikeLog;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 点赞记录  (不在区分所属的项目)
 * </p>
 *
 * @author leizhi
 * @date 2021-03-03
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ReplyLikeLogMapper extends SuperMapper<ReplyLikeLog> {

}
