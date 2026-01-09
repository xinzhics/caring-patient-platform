package com.caring.sass.nursing.service.plan;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.form.CopyPlanDTO;
import com.caring.sass.nursing.entity.plan.Plan;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 护理计划（随访服务）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
public interface PlanService extends SuperService<Plan> {

    /**
     * @return boolean
     * @Author yangShuai
     * @Description 初始化 护理计划
     * @Date 2020/9/21 17:16
     */
    boolean initPlan();


    Plan getOne(Long planId);

    /**
     * 通过计划类型查询计划列表
     * @param planType
     * @return
     */
    List<Plan> getPlanAndDetailsByPlanType(Integer planType, String userRole, Integer status);

    /**
     * 通过计划类型查询计划列表
     * @param planType
     * @return
     */
    List<Plan> getPlanAndDetailsByPlanType(Integer planType, String userRole, Integer status, List<Long> planIds);

    /**
     * @return com.caring.sass.nursing.entity.plan.Plan
     * @Author yangShuai
     * @Description 重置护理计划
     * @Date 2020/10/24 16:47
     */
    Boolean resetPlan(Long planId);

    boolean updateStatus(Long planId, Integer status);

    /**
     * 更新一个护理计划
     *
     * @param plan
     * @return
     */
    Boolean updatePlan(Plan plan);

    /**
     * 按项目删除护理计划
     */
    Boolean deleteByTenant();

    Map<Long,Long> copyPlan(CopyPlanDTO copyPlanDTO);

    /**
     * 护理计划
     * @param isAdminTemplate
     * @return
     */
    List<Plan> getPlan(Integer isAdminTemplate, String followUpPlanType);

    /**
     * 护理计划
     * @param isAdminTemplate
     * @param status
     * @return
     */
    List<Plan> getPlan(Integer isAdminTemplate, Integer status, String followUpPlanType);


    void checkPlanAll(Plan plan);

    /**
     * 根据 系统模板plan 创建一个项目计划模板
     * @param plan
     */
    void copySystemTemplatePlan(Plan plan);

    /**
     * 将计划保存为系统模板
     * @param plan
     */
    void saveSystemTemplatePlan(Plan plan);

    /**
     * 根据租户code获取随访记录
     * */
    List<Plan> exportFollowPlan();

    Long getPLanIdByPlanType(Integer planType);

    /**
     * 获取护理计划和护理计划的详细配置
     * @param planId
     */
    Plan getPlanAndDetails(Long planId);


    List<Plan> getPlanAndDetails(List<Long> planIds);

    /**
     * 检查文件夹分享的链接。是否被随访计划使用
     * @param url
     * @return
     */
    List<String> checkFolderShareUrlExist(String url);


    void queryAllTenantNoBindPlanPatient();


}
