package com.caring.sass.user.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.user.entity.ConsultationGroup;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author yangShuai
 * @Description 会诊
 * @Date 2020/10/13 10:51
 *
 * @return
 */
@Repository
public interface ConsultationGroupMapper extends SuperMapper<ConsultationGroup> {

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    ConsultationGroup selectByIdNoTenant(@Param("id") Long id);
}
