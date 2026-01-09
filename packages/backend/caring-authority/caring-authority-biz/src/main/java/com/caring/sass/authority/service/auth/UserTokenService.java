package com.caring.sass.authority.service.auth;

import com.caring.sass.authority.dto.auth.DoctorToken;
import com.caring.sass.authority.entity.auth.UserToken;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * token
 * </p>
 *
 * @author caring
 * @date 2020-04-02
 */
public interface UserTokenService extends SuperService<UserToken> {

    /**
     * 保存医生token信息
     */
    boolean bindDoctorToken(DoctorToken model);

    /**
     * 重置用户登录
     *
     * @return
     */
    boolean reset();
}
