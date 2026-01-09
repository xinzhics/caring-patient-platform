package com.caring.sass.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.tenant.entity.Sequence;
import com.caring.sass.tenant.enumeration.SequenceEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author xinzh
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface SequenceMapper extends SuperMapper<Sequence> {

    Long selectSequenceVar(@Param("seqType") SequenceEnum seqType);
}
