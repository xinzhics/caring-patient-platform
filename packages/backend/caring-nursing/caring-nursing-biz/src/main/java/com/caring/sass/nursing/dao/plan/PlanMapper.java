package com.caring.sass.nursing.dao.plan;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.plan.Plan;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * 护理计划（随访服务）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Repository
public interface PlanMapper extends SuperMapper<Plan> {

    /**
     * 查询每天需要推送的用药信息
     */
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<Map<Object, Object>> queryGenerateDrugsPush();


}
