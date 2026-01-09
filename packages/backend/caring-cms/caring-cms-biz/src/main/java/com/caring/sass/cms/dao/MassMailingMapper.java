package com.caring.sass.cms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.cms.entity.MassMailing;
import org.springframework.stereotype.Repository;

/**
 * @ClassName ArticleNewsMapper
 * @Description
 * @Author yangShuai
 * @Date 2021/11/17 16:29
 * @Version 1.0
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface MassMailingMapper extends SuperMapper<MassMailing> {

}