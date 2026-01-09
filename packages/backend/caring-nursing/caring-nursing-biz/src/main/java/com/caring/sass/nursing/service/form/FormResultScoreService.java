package com.caring.sass.nursing.service.form;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.entity.form.FormFieldsGroup;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.form.FormResultScore;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 表单结果的成绩计算
 * </p>
 *
 * @author 杨帅
 * @date 2023-10-11
 */
public interface FormResultScoreService extends SuperService<FormResultScore> {

    /**
     * 计算表单结果的得分
     * @param formResult
     * @return
     */
    boolean countFormResult(FormResult formResult);


    FormResultScore queryFormResultScore(Long formResultId);

    List<FormResultScore> queryFormResultScore(List<Long> formResultIds);


    void countFormResult(List<FormFieldDto> resultFields, Long id, Long userId, Long formId, List<FormFieldsGroup> fieldsGroups);

}
