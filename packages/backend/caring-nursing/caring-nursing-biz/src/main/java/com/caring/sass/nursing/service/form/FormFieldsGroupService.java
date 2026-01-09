package com.caring.sass.nursing.service.form;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.form.FormFieldsGroup;

/**
 * <p>
 * 业务接口
 * 表单题目的分组规则
 * </p>
 *
 * @author 杨帅
 * @date 2023-10-11
 */
public interface FormFieldsGroupService extends SuperService<FormFieldsGroup> {

    Long createGroupTempId();
}
