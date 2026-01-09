package com.caring.sass.cms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.cms.entity.ContentReply;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 内容留言 （不在区分所属的项目）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-09
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ContentReplyMapper extends SuperMapper<ContentReply> {

}
