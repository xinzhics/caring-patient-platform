package com.caring.sass.user.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.entity.DoctorAudit;

import java.util.List;

/**
 * @ClassName DoctorAuditService
 * @Description
 * @Author yangShuai
 * @Date 2022/2/22 14:07
 * @Version 1.0
 */
public interface DoctorAuditService  extends SuperService<DoctorAudit> {


    void desensitization(List<DoctorAudit> records);


}
