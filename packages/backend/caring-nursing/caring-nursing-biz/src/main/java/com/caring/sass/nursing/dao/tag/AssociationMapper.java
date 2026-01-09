package com.caring.sass.nursing.dao.tag;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.dto.tag.TagPatientCountResult;
import com.caring.sass.nursing.entity.tag.Association;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 业务关联标签记录表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Repository
public interface AssociationMapper extends SuperMapper<Association> {


    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<TagPatientCountResult> selectTagCountPatientNumber(@Param("serviceAdvisorId") Long serviceAdvisorId, @Param("doctorId") Long doctorId, @Param("doctorIds") List<Long> doctorIds);


}
