package com.caring.sass.nursing.dto.statistics;

/**
 * @ClassName StatisticsShowStatus
 * @Description
 * @Author yangShuai
 * @Date 2022/4/19 15:55
 * @Version 1.0
 */
public interface StatisticsStatus {

    /**
     * 记录项目下最大
     */
    String redisOrderKey = "statisticsTaskOrder";

    /**
     * 项目统计 统计图表 顺序存储
     * 记录项目下 每个 顺序绑定的 图表类型和ID
     */
    String statisticsTaskOrderKey = "statisticsTaskOrderKey:";

    /**
     * 显示
     */
    String SHOW = "show";

    /**
     * 隐藏
     */
    String HIDE = "hide";

    /**
     * 任务配置第一步
     */
    String STEP_FIRST = "step_first";

    /**
     * 任务配置第二步
     */
    String STEP_SECOND = "step_second";

    /**
     * 任务配置完成
     */
    String STEP_FINISH = "step_finish";

    /**
     * 用户概要
     */
    String USER_PROFILE = "user_profile";

    /**
     * 诊断类型
     */
    String DIAGNOSIS_TYPE = "diagnosis_type";

    /**
     * 复诊率
     */
    String RETURN_RATE = "return_rate";


    /**
     * 自定义
     */
    String CUSTOMIZE = "customize";

    /**
     * 达标率
     */
    String COMPLIANCE_RATE = "compliance_rate";

    /**
     * 基准值
     */
    String BASE_LINE_VALUE = "base_line_value";

    /**
     * 主统计图
     */
    String MASTER_CHART = "master_chart";


}
