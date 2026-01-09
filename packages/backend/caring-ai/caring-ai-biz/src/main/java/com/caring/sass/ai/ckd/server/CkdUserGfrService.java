package com.caring.sass.ai.ckd.server;

import com.caring.sass.ai.dto.ckd.CkdGfrTrend;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.ckd.CkdUserGfr;

/**
 * <p>
 * 业务接口
 * 用户GFR
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
public interface CkdUserGfrService extends SuperService<CkdUserGfr> {

    CkdGfrTrend getGfrTrend(String openId);

}
