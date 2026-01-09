package com.caring.sass.nursing.service.appointment.impl;


import com.caring.sass.nursing.dao.appointment.AppointConfigMapper;
import com.caring.sass.nursing.entity.appointment.AppointConfig;
import com.caring.sass.nursing.service.appointment.AppointConfigService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 记录医生的周预约配置
 * </p>
 *
 * @author leizhi
 * @date 2021-01-27
 */
@Slf4j
@Service

public class AppointConfigServiceImpl extends SuperServiceImpl<AppointConfigMapper, AppointConfig> implements AppointConfigService {



    @Override
    public boolean updateById(AppointConfig model) {
        return super.updateById(model);
    }
}
