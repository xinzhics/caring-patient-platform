package com.caring.sass.user.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.entity.MiniappInfo;

/**
 * <p>
 * 业务接口
 * 小程序用户openId关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-03-22
 */
public interface MiniappInfoService extends SuperService<MiniappInfo> {

    MiniappInfo selectByIdNoTenant(Long id);


}
