package com.caring.sass.nursing.service.information.impl;


import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.nursing.dao.information.PatientInformationFieldMapper;
import com.caring.sass.nursing.entity.information.PatientInformationField;
import com.caring.sass.nursing.service.information.PatientInformationFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 患者信息完整度字段
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Service

public class PatientInformationFieldServiceImpl extends SuperServiceImpl<PatientInformationFieldMapper, PatientInformationField> implements PatientInformationFieldService {
}
