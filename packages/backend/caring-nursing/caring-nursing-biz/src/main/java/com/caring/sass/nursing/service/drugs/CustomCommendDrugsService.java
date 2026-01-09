package com.caring.sass.nursing.service.drugs;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.drugs.CustomCommendDrugs;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 自定义推荐药品
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface CustomCommendDrugsService extends SuperService<CustomCommendDrugs> {

    Boolean copyRecommendDrugs(String fromTenantCode, String toTenantCode);

    void deleteByDrugsIds(List<Long> longs);
}
