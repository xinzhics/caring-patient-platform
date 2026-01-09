package com.caring.sass.tenant.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.dao.TenantConfigurationMarkMapper;
import com.caring.sass.tenant.dao.TenantConfigurationScheduleMapper;
import com.caring.sass.tenant.dao.TenantMapper;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.entity.TenantConfigurationMark;
import com.caring.sass.tenant.entity.TenantConfigurationSchedule;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import com.caring.sass.tenant.service.TenantConfigurationScheduleService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 项目配置进度表
 * </p>
 *
 * @author 杨帅
 * @date 2023-07-18
 */
@Slf4j
@Service

public class TenantConfigurationScheduleServiceImpl extends SuperServiceImpl<TenantConfigurationScheduleMapper, TenantConfigurationSchedule> implements TenantConfigurationScheduleService {


    @Autowired
    TenantMapper tenantMapper;

    @Autowired
    TenantConfigurationMarkMapper markMapper;

    /**
     * 上线之后。对正在 运行的 正常项目 状态为已设置。
     * 禁用和部署中的项目为 未设置。
     */
    @Override
    public void onLinkInit() {

        // 上线之后 初始化 项目的配置进度。 标记。
        List<Tenant> tenants = tenantMapper.selectList(Wraps.<Tenant>lbQ().select(SuperEntity::getId, Tenant::getCode, Tenant::getStatus));
        if (CollUtil.isNotEmpty(tenants)) {
            TenantConfigurationSchedule schedule;
            TenantConfigurationMark mark;
            for (Tenant tenant : tenants) {
                String code = tenant.getCode();
                BaseContextHandler.setTenant(code);
                TenantStatusEnum status = tenant.getStatus();
                schedule = new TenantConfigurationSchedule();
                mark = new TenantConfigurationMark();

                if (TenantStatusEnum.NORMAL.eq(status)) {
                    TenantConfigurationSchedule.init(schedule, 1);
                    TenantConfigurationMark.init(mark, 1);
                } else {
                    TenantConfigurationSchedule.init(schedule, 0);
                    TenantConfigurationMark.init(mark, 0);
                }
                baseMapper.insert(schedule);
                markMapper.insert(mark);
            }
        }
    }

    @Override
    public void copy(String formCode, String toCode) {
        BaseContextHandler.setTenant(formCode);
        TenantConfigurationMark mark = markMapper.selectOne(Wraps.<TenantConfigurationMark>lbQ().last("limit 0,1"));
        BaseContextHandler.setTenant(toCode);
        TenantConfigurationSchedule schedule = new TenantConfigurationSchedule();
        TenantConfigurationSchedule.init(schedule, 0);
        // 界面配置没有复制
        baseMapper.insert(schedule);
        if (Objects.nonNull(mark)) {
            mark.setId(null);
            // 安卓变更日志状态
            mark.setAndroidChangeLogStatus(0);
            // uni变更日志状态
            mark.setUniChangeLogStatus(0);
            // uni配置状态
            mark.setUniConfigurationStatus(0);
            // 推送配置状态
            mark.setPushConfigurationStatus(0);
            // 运营支持状态
            mark.setOperationalSupportStatus(0);
            // 患者推荐设置状态
            mark.setRecommendConfigStatus(0);
            // 角色设置状态
            mark.setSystemRoleStatus(0);
            // 随访管理设置的状态
            mark.setFollowManageStatus(0);
            // 微信菜单设置的状态
            mark.setMenuSettingStatus(0);
            // 关键字回复。
            mark.setKeywordReplyStatus(0);
            // 收到消息后回复
            mark.setReceivedMessageReply(0);
            markMapper.insert(mark);
        }
        BaseContextHandler.setTenant(formCode);

    }

}
