package com.caring.sass.nursing.util;

import com.caring.sass.nursing.entity.plan.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 * 护理计划字典
 *
 * @author xinzh
 */
public class PlanDict {

    public static Map<Integer, Plan> PLANS = new HashMap<>();

    public static String CARE_PLAN = "care_plan";
    public static String MONITORING_DATA = "monitoring_data";

    static {
        PLANS.put(1, new Plan("血压监测", 0, 0, 7, 0, 1, MONITORING_DATA));
        PLANS.put(2, new Plan("血糖监测", 0, 0, 7, 0, 2, MONITORING_DATA));
        PLANS.put(3, new Plan("复查提醒", 0, 0, 30, 0, 3, CARE_PLAN));
        PLANS.put(4, new Plan("用药提醒", 0, 0, null, null, 4, CARE_PLAN));
        PLANS.put(5, new Plan("健康日志", 0, 0, 7, 0, 5, CARE_PLAN));
        PLANS.put(6, new Plan("学习计划", 0, 0, 1, 0, 6, CARE_PLAN));
    }

    public static Plan getPlan(Integer planType) {
        return PLANS.get(planType);
    }
}
