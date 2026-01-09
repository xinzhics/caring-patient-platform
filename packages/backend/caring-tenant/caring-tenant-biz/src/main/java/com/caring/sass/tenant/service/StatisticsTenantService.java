package com.caring.sass.tenant.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.tenant.dto.StatisticsTenantPageDTO;
import com.caring.sass.tenant.entity.StatisticsTenant;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 项目统计
 * </p>
 *
 * @author leizhi
 * @date 2021-11-05
 */
public interface StatisticsTenantService extends SuperService<StatisticsTenant> {

    void syncFromDb();

    void page(IPage<StatisticsTenant> iPage, StatisticsTenantPageDTO model);

    Map<String, Long> queryMemberNum();


}
