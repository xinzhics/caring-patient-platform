package com.caring.sass.nursing.service.form;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.enumeration.FormEnum;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 自定义表单表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface FormService extends SuperService<Form> {


    /**
     * 通过分类或者关联的计划ID查询表单
     * 优先从redis的缓存中去获取
     * @param formEnum
     * @param businessId
     * @return
     */
    Form getFormByRedis(FormEnum formEnum, String businessId);

    Integer queryFormDailySubmissionLimit(Long formId);

    /**
     * 复制表单
     */
    Boolean copyForm(String fromTenantCode, String toTenantCode, Map<Long, Long> planIdMaps);

    /**
     * 查看标签管理使用的表单字段
     *
     * @param category
     * @return
     */
    List<FormField> getTagAttrField(FormEnum category);


    /**
     * 获取监测计划 中的监测指标字段
     * @param businessId
     * @return
     */
    List<FormField> getTagAttrMonitoringIndicatorsField(String businessId);

    /**
     * 查询监控指标
     * @param planId
     * @param planType
     * @param category
     * @return
     */
    Form getMonitoringIntervalFields(Long planId, Integer planType, FormEnum category);

    /**
     * 删除护理计划下 表单相关的所有业务数据
     * @param id
     */
    void removePlanForm(Serializable id);

    /**
     * 查询项目入组的方式
     * @return
     */
    @Deprecated
    Form getIntoTheGroupOneQuestionPage();


    /**
     * 根据业务ID修改表单的名称
     * @param businessId
     * @param name
     */
    void updateFormName(String businessId, String name);
}
