package com.caring.sass.nursing.service.form;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dto.blood.BloodPressDTO;
import com.caring.sass.nursing.dto.blood.BloodPressTrendResult;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.MonitorDateLineType;
import com.caring.sass.nursing.dto.form.MonitorDayParams;
import com.caring.sass.nursing.dto.form.MonitorLineChart;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.enumeration.FormEnum;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 表单填写结果表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface FormResultService extends SuperService<FormResult> {

    /**
     * 一条推送消息只有一个表单结果
     * @param messageId
     * @return
     */
    FormResult getFormResultByMessageId(Long messageId);

    /**
     * 使用 businessId 封装一个表单给前端
     * @param planId
     * @param messageId
     * @return
     */
    FormResult getFormResultByBusinessId(Long planId, Long messageId);

    FormResult getBasicOrHealthFormResult(Long patientId, FormEnum formEnum);

    List<FormResult> getBasicAndLastHealthFormResult(Long patientId);

    List<Map<String, Object>> patientBloodPressList(Long patientId);

    BloodPressTrendResult getPatientBloodPressTrandDatas(Long patientId,  boolean needNew);

    List<BloodPressDTO> getPatientBloodPress(Long patientId);

    /**
     * 监测计划 折线图数据
     * @param patientId
     * @param businessId
     * @param monitorDateLineType
     * @return
     */
    @Deprecated
    MonitorLineChart monitorLineChart(Long patientId, String businessId, MonitorDateLineType monitorDateLineType);


    /**
     * 监测计划 折线图数据
     * @param patientId
     * @param businessId
     * @param monitorDateLineType
     * @return
     */
    MonitorLineChart monitorLineChart(Long patientId, String businessId, MonitorDayParams monitorDateLineType);


    /**
     * 处理一下。多选题的 options 和 values 比对。 存在于values中的options需要设置select属性
     */
    void handleCheckBoxResult(List<FormField> formFields);

    /**
     * 分阶段保存患者的基本信息和疾病信息
     * @param formResult
     */
    void saveFormResultStage(FormResult formResult);

    /**
     * 逻辑删除表单结果（疾病信息）
     * @param formResultId
     */
    void updateForDeleteFormResult(Long formResultId);

    void checkFormResultSetScore(List<FormResult> formResults, Boolean needContent);

    boolean checkNeedDesensitization();

    /**
     * 脱敏表单字段中 姓名和手机号
     * @param fieldList
     */
    void desensitization(List<FormField> fieldList);
}
