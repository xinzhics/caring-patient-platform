package com.caring.sass.tenant.service.impl;


import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.tenant.dao.StepMapper;
import com.caring.sass.tenant.entity.Step;
import com.caring.sass.tenant.service.StepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 项目配置步骤信息
 * </p>
 *
 * @author leizhi
 * @date 2020-11-03
 */
@Slf4j
@Service
public class StepServiceImpl extends SuperServiceImpl<StepMapper, Step> implements StepService {
}
