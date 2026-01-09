package com.caring.sass.user.service.impl;


import com.caring.sass.user.dao.ReferralMapper;
import com.caring.sass.user.entity.Referral;
import com.caring.sass.user.service.ReferralService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 患者转诊
 * </p>
 *
 * @author leizhi
 * @date 2021-08-27
 */
@Slf4j
@Service

public class ReferralServiceImpl extends SuperServiceImpl<ReferralMapper, Referral> implements ReferralService {
}
