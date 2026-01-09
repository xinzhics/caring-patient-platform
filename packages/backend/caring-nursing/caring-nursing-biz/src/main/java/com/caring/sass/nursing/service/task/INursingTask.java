package com.caring.sass.nursing.service.task;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xinzh
 */
public interface INursingTask {

    // 获取符合推送条件的项目

    // 获取项目下的推送护理计划

    // 获取护理计划被患者订阅的列表

    // 获取护理计划的详情，如果详情为空，不进行处理

    // 查询患者信息

    // 患者正常且入组

    // 查询患者和项目的标签是否匹配




    // 血压、血糖、复查提醒、健康日志
    //  根据推送详情、判断消息类型，0图文、1模板消息、2文字
    // 根据 完成入组时间、推送周期、提前天数、频率、有效时间、计算是否推送
    // 判断当前时间、分钟数是否匹配，匹配则推送
    // 推送图文消息、推送微信模板消息、文字则推送客服消息


    // 学习计划 只关注图文

    // 用药提醒


    // 发送微信


//    public List<Tenant> getAllNormalTenant() {
//        R<List<Tenant>> listR = tenantApi.getAllNormalTenant();
//        if (listR.getIsSuccess() && listR.getData() != null) {
//            return listR.getData();
//        }
//        return new ArrayList<>();
//    }

}
