package com.caring.sass.nursing.entity.follow;

/**
 * 随访执行计划的时间范围
 */
public interface FollowContentTimeFrame {

    /**
     * 不需要设置
     */
    String NOT_NEED_SET = "not_need_set";
    /**
     * 未设置
     */
    String NOT_SET = "not_set";

    /**
     * 一个月
     */
    String ONE_MOUTH = "one_mouth";

    /**
     * 近三个月
     */
    String THREE_MONTHS = "three_months";

    /**
     * 近半年
     */
    String HALF_YEAR = "half_year";

    /**
     * 近一年
     */
    String ONE_YEAR = "one_year";

    /**
     * 近三年
     */
    String THREE_YEARS = "three_years";

}
