package com.caring.sass.ai.ckd.server;

import com.caring.sass.ai.dto.ckd.CkdFreeTimeDto;
import com.caring.sass.ai.dto.ckd.CkdUserMembershipUpdateDto;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.ckd.CkdUserInfo;
import org.hibernate.validator.constraints.Length;

/**
 * <p>
 * 业务接口
 * CKD用户信息
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
public interface CkdUserInfoService extends SuperService<CkdUserInfo> {

    CkdUserInfo getByOpenId(@Length(max = 40, message = "openId长度不能超过40") String openId);


    void updateFreeTime(CkdFreeTimeDto ckdFreeTimeDto);


    void setMembershipLevel(CkdUserMembershipUpdateDto membershipUpdateDto);


}
