package com.caring.sass.nursing.service.form;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.form.FormResultBackUp;

/**
 * @ClassName FormResultBackUpService
 * @Description
 * @Author yangShuai
 * @Date 2022/4/6 10:50
 * @Version 1.0
 */
public interface FormResultBackUpService extends SuperService<FormResultBackUp> {


    /**
     * 比对表单的结果。 如果结果有修改。则对结果进行备份
     * @param userType
     * @param userId
     * @param tenantCode
     * @param oldFormResult
     * @param newFormResult
     */
    void backUp(String userType, Long userId, String tenantCode, FormResult oldFormResult, FormResult newFormResult);



    void createHealthRecordByHistory();


}
