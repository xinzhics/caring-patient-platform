package com.caring.sass.user.service.impl;


import com.caring.sass.user.dao.DoctorCustomGroupPatientMapper;
import com.caring.sass.user.entity.DoctorCustomGroupPatient;
import com.caring.sass.user.service.DoctorCustomGroupPatientService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 医生的自定义小组患者
 * </p>
 *
 * @author yangshuai
 * @date 2022-08-11
 */
@Slf4j
@Service

public class DoctorCustomGroupPatientServiceImpl extends SuperServiceImpl<DoctorCustomGroupPatientMapper, DoctorCustomGroupPatient> implements DoctorCustomGroupPatientService {
}
