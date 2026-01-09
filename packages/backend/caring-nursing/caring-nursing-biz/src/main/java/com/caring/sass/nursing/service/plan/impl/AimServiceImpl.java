package com.caring.sass.nursing.service.plan.impl;


import com.caring.sass.nursing.dao.plan.AimMapper;
import com.caring.sass.nursing.entity.plan.Aim;
import com.caring.sass.nursing.service.plan.AimService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 护理目标
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Service

public class AimServiceImpl extends SuperServiceImpl<AimMapper, Aim> implements AimService {
}
