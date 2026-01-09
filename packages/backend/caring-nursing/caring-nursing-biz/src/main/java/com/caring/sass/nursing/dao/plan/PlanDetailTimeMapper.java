package com.caring.sass.nursing.dao.plan;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.plan.PlanDetail;
import com.caring.sass.nursing.entity.plan.PlanDetailTime;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 护理计划详情
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Repository
public interface PlanDetailTimeMapper extends SuperMapper<PlanDetailTime> {

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<PlanDetailTime> selectMenuLikeUrl(@Param(value = "shareUrl") String shareUrl);


}
