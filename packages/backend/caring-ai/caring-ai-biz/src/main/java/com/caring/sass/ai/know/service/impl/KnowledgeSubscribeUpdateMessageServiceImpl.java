package com.caring.sass.ai.know.service.impl;


import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.dto.know.KnowMemberType;
import com.caring.sass.ai.dto.know.KnowledgeSubscribeUpdateMessageSaveDTO;
import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.know.dao.KnowledgeSubscribeUpdateMessageMapper;
import com.caring.sass.ai.know.dao.KnowledgeUserMapper;
import com.caring.sass.ai.know.dao.KnowledgeUserOrderMapper;
import com.caring.sass.ai.know.dao.KnowledgeUserSubscribeMapper;
import com.caring.sass.ai.know.service.KnowledgeSubscribeUpdateMessageService;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 博主订阅设置修改记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-08-05
 */
@Slf4j
@Service

public class KnowledgeSubscribeUpdateMessageServiceImpl extends SuperServiceImpl<KnowledgeSubscribeUpdateMessageMapper, KnowledgeSubscribeUpdateMessage> implements KnowledgeSubscribeUpdateMessageService {

    @Autowired
    KnowledgeUserMapper knowledgeUserMapper;

    @Autowired
    KnowledgeUserOrderMapper knowledgeUserOrderMapper;

    @Autowired
    KnowledgeUserSubscribeMapper knowledgeUserSubscribeMapper;

    public void stopFreeOrder(String domain) {

        // 停止免费订阅的订单 会员
        List<KnowledgeUserOrder> userOrders = knowledgeUserOrderMapper.selectList(Wraps.<KnowledgeUserOrder>lbQ()
                .eq(KnowledgeUserOrder::getUserDomain, domain)
                .eq(KnowledgeUserOrder::getGoodsType, KnowMemberType.Free));
        if (!userOrders.isEmpty()) {
            for (KnowledgeUserOrder userOrder : userOrders) {
                userOrder.setMembershipExpiration(LocalDateTime.now());
                userOrder.setDeductibleAmount(0);
                knowledgeUserOrderMapper.updateById(userOrder);
            }
            knowledgeUserSubscribeMapper.delete(Wraps.<KnowledgeUserSubscribe>lbQ()
                    .eq(KnowledgeUserSubscribe::getMembershipLevel, MembershipLevel.FREE_VERSION)
                    .eq(KnowledgeUserSubscribe::getUserDomain, domain));
        }


    }

    /**
     * 修改博主的订阅设置
     * @param knowledgeUser
     */
    @Override
    public void updateSubscribeInfo(KnowledgeSubscribeUpdateMessageSaveDTO knowledgeUser) {

        KnowledgeUser user = knowledgeUserMapper.selectById(knowledgeUser.getKnowledgeUserId());
        if (user == null) {
            throw new BizException("博主不存在");
        }
        StringBuilder stringBuilder = new StringBuilder();
        Boolean subscribeSwitch = user.getSubscribeSwitch();
        if (subscribeSwitch && !knowledgeUser.getSubscribeSwitch()) {
            stringBuilder.append("\n关闭了订阅设置");
        } else if (!subscribeSwitch && knowledgeUser.getSubscribeSwitch()) {
            stringBuilder.append("\n开启了订阅设置");
            // 博主开启订阅设置。需要吧免费订阅的会员终止掉免费试用机会
            stopFreeOrder(user.getUserDomain());
        }
        if (!user.getSubscribeUserName().equals(knowledgeUser.getSubscribeUserName())) {
            stringBuilder.append("\n会员名称由")
                    .append(user.getSubscribeUserName())
                    .append("修改为")
                    .append(knowledgeUser.getSubscribeUserName());
        }
        if (knowledgeUser.getOpenMonthlyPayment() && !user.getOpenMonthlyPayment()) {
            stringBuilder.append("\n按月付费已开启");
        }
        if (!knowledgeUser.getOpenMonthlyPayment() && user.getOpenMonthlyPayment()) {
            stringBuilder.append("\n按月付费已关闭");
        }
        if (knowledgeUser.getOpenAnnualPayment() && !user.getOpenAnnualPayment()) {
            stringBuilder.append("\n按年付费已开启");
        }
        if (!knowledgeUser.getOpenAnnualPayment() && user.getOpenAnnualPayment()) {
            stringBuilder.append("\n按年付费已关闭");
        }
        if (knowledgeUser.getOpenArticleData() && !user.getOpenArticleData()) {
            stringBuilder.append("\n科普患教权限修改为开放");
        }
        if (!knowledgeUser.getOpenArticleData() && user.getOpenArticleData()) {
            stringBuilder.append("\n科普患教权限修改为不开放");
        }
        if (knowledgeUser.getOpenTextual() && !user.getOpenTextual()) {
            stringBuilder.append("\n文献解读权限修改为开放");
        }
        if (!knowledgeUser.getOpenTextual() && user.getOpenTextual()) {
            stringBuilder.append("\n文献解读权限修改为不开放");
        }
        if (knowledgeUser.getOpenAcademicMaterials() && !user.getOpenAcademicMaterials()) {
            stringBuilder.append("\n学术材料权限修改为开放");
        }
        if (!knowledgeUser.getOpenAcademicMaterials() && user.getOpenAcademicMaterials()) {
            stringBuilder.append("\n学术材料权限修改为不开放");
        }
        if (knowledgeUser.getOpenPersonalAchievements() && !user.getOpenPersonalAchievements()) {
            stringBuilder.append("\n个人成果权限修改为开放");
        }
        if (!knowledgeUser.getOpenPersonalAchievements() && user.getOpenPersonalAchievements()) {
            stringBuilder.append("\n个人成果权限修改为不开放");
        }
        if (knowledgeUser.getOpenCaseDatabase() && !user.getOpenCaseDatabase()) {
            stringBuilder.append("\n疾病分享权限修改为开放");
        }
        if (!knowledgeUser.getOpenCaseDatabase() && user.getOpenCaseDatabase()) {
            stringBuilder.append("\n疾病分享权限修改为不开放");
        }
        if (knowledgeUser.getOpenDailyCollection() && !user.getOpenDailyCollection()) {
            stringBuilder.append("\n日常收集权限修改为开放");
        }
        if (!knowledgeUser.getOpenDailyCollection() && user.getOpenDailyCollection()) {
            stringBuilder.append("\n日常收集权限修改为不开放");
        }
        if (!user.getBasicMembershipPrice().equals(knowledgeUser.getBasicMembershipPrice())) {
            stringBuilder.append("\n按月付费价格由￥")
                    .append(user.getBasicMembershipPrice() / 100D)
                    .append("修改为￥")
                    .append(knowledgeUser.getBasicMembershipPrice() / 100D);
        }
        if (!user.getProfessionalVersionPrice().equals(knowledgeUser.getProfessionalVersionPrice())) {
            stringBuilder.append("\n按年付费价格由￥")
                    .append(user.getProfessionalVersionPrice() / 100D)
                    .append("修改为￥")
                    .append(knowledgeUser.getProfessionalVersionPrice() / 100D);
        }
        String string = stringBuilder.toString();
        if (StrUtil.isBlank(string)) {
            return;
        }
        if (string.startsWith("\n")) {
            string = string.substring(1);
        }

        BeanUtils.copyProperties(knowledgeUser, user);
        user.setSubscribeLastUpdateTime(LocalDateTime.now());
        knowledgeUserMapper.updateById(user);
        KnowledgeSubscribeUpdateMessage updateMessage = new KnowledgeSubscribeUpdateMessage();
        updateMessage.setKnowledgeUserId(user.getId());
        updateMessage.setUpdateMessage(string);
        baseMapper.insert(updateMessage);


    }


}
