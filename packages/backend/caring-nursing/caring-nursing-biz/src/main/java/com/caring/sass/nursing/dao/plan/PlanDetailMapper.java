package com.caring.sass.nursing.dao.plan;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.plan.PlanDetail;

import com.caring.sass.tenant.entity.router.H5Router;
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
public interface PlanDetailMapper extends SuperMapper<PlanDetail> {


    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<PlanDetail> selectMenuLikeUrl(@Param(value = "shareUrl") String shareUrl);


}
