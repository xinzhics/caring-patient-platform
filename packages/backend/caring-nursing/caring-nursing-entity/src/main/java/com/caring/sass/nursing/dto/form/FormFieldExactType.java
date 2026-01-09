package com.caring.sass.nursing.dto.form;

/**
 * 表单字段确切类型
 *
 * @author xinzh
 */
public interface FormFieldExactType {
    /** 评分单选 */
    String SCORING_SINGLE_CHOICE = "SCORING_SINGLE_CHOICE";
    /** 身高*/
    String HEIGHT = "Height";
    /** 体重*/
    String WEIGHT = "Weight";
    /** BMI*/
    String BMI = "BMI";
    /** 血肌酐*/
    String SCR = "SCR";
    /** 肾小球过滤*/
    String GFR = "GFR";
    /** 肌酐清除率*/
    String CCR = "CCR";
    /** 病程，肾病分期*/
    String COURSE_OF_DISEASE = "CourseOfDisease";
    /** 年龄*/
    String AGE = "Age";
    /** 性别*/
    String GENDER = "Gender";
    /** 出生年月日*/
    String BIRTHDAY = "Birthday";
    /** 姓名*/
    String NAME = "Name";
    /** 手机*/
    String MOBILE = "Mobile";
    /** 邮箱*/
    String EMAIL = "Email";
    /** 座机电话*/
    String PHONE = "Phone";
    /** 地址*/
    String ADDRESS = "Address";
    /** 诊断类型*/
    String DIAGNOSE = "Diagnose";
    /** 头像*/
    String AVATAR = "Avatar";
    /**
     * 医疗字段 监测指标
     */
    String MONITORING_INDICATORS = "monitoringIndicators";

    String CHECK_TIME = "FormResultCheckTime";

    /**  医院  */
    String HOSPITAL = "hospital";

    /**
     * 单选题
     * 监测事件
     */
    String MonitoringEvents = "MonitoringEvents";

    /**
     * 单选题
     * 随访阶段
     */
    String FollowUpStage = "FollowUpStage";

    /**
     * 随访开启日期
     */
    String FollowStartDate = "followStartDate";

}
