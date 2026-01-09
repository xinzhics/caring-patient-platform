package com.caring.sass.nursing.service.drugs.impl;

import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.nursing.dao.drugs.PatientDrugsHistoryLogMapper;
import com.caring.sass.nursing.entity.drugs.PatientDrugsHistoryLog;
import com.caring.sass.nursing.service.drugs.PatientDrugsHistoryLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 患者药箱的操作记录
 */
@Slf4j
@Service
public class PatientDrugsHistoryLogServiceImpl extends SuperServiceImpl<PatientDrugsHistoryLogMapper, PatientDrugsHistoryLog> implements PatientDrugsHistoryLogService {


}
