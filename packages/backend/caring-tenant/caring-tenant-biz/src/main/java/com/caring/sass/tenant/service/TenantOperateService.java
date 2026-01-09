package com.caring.sass.tenant.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.tenant.dto.TenantDayRemindDto;
import com.caring.sass.tenant.entity.TenantOperate;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 项目运营配置
 * </p>
 *
 * @author 杨帅
 * @date 2023-05-10
 */
public interface TenantOperateService extends SuperService<TenantOperate> {

    /**
     * 超管端查询剩余时长30天的项目名称
     * @return
     */
    List<String> adminQuery30DayTenantName(Long userId);


    /**
     * 项目管理员登录提醒
     * @param userId
     * @return
     */
    TenantDayRemindDto userQueryTenantDay(Long userId);

    /**
     * 医助登录查询项目剩余时长
     * @param userId
     * @return
     */
    Integer nursingQueryTenantDay(Long userId);


    /**
     * 医生登录查询项目剩余时长
     * @param userId
     * @return
     */
    Integer doctorQueryTenantDay(Long userId);


    /**
     * 创建机构时，判断数量
     * @return
     */
    Integer createOrganNumberCheck();


    /**
     * 创建医生时，判断数量
     * @return
     */
    Integer createDoctorNumberCheck();

    /**
     * 创建医助时，判断数量
     * @return
     */
    Integer createNursingNumberCheck();

    /**
     * 定时任务。每日执行
     * 正常项目的剩余时长 -1
     */
    void tenantOperationalEvents();


}
