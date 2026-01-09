package com.caring.sass.wx.service.guide.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.wx.dao.guide.RegGuideMapper;
import com.caring.sass.wx.entity.guide.RegGuide;
import com.caring.sass.wx.service.guide.RegGuideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 微信注册引导
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class RegGuideServiceImpl extends SuperServiceImpl<RegGuideMapper, RegGuide> implements RegGuideService {


    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public Boolean copyRegGuide(String fromTenantCode, String toTenantCode) {
        String currentTenant = BaseContextHandler.getTenant();

        BaseContextHandler.setTenant(fromTenantCode);
        List<RegGuide> regGuides = baseMapper.selectList(Wrappers.emptyWrapper());
        if (CollUtil.isNotEmpty(regGuides)) {
            RegGuide regGuide = regGuides.get(0);
            regGuide.setId(null);
            regGuide.setTenantCode(toTenantCode);
            BaseContextHandler.setTenant(toTenantCode);
            int insert = baseMapper.insert(regGuide);
            if (insert > 0) {
                redisTemplate.opsForHash().put(SaasRedisBusinessKey.getRedisKeyWxUserDefaultRole(), toTenantCode, regGuide.getWxUserDefaultRole());
            }
        }
        BaseContextHandler.setTenant(currentTenant);
        return true;
    }

    @Override
    public List<RegGuide> getOpenUnregisteredReminder() {

        return baseMapper.getOpenUnregisteredReminder();
    }


    @Override
    public String setWxUserDefaultRole(String tenantCode, String userType) {
        BaseContextHandler.setTenant(tenantCode);
        RegGuide regGuide = baseMapper.selectOne(Wraps.<RegGuide>lbQ()
                .last(" limit 0, 1 ")
                .select(SuperEntity::getId, RegGuide::getWxUserDefaultRole));
        String userDefaultRole = regGuide.getWxUserDefaultRole();
        regGuide.setWxUserDefaultRole(userType);
        int update = baseMapper.updateById(regGuide);
        if (update > 0) {
            redisTemplate.opsForHash().put(SaasRedisBusinessKey.getRedisKeyWxUserDefaultRole(), tenantCode, userType);
            return userType;
        } else {
            redisTemplate.opsForHash().put(SaasRedisBusinessKey.getRedisKeyWxUserDefaultRole(), tenantCode, userDefaultRole);
            return userDefaultRole;
        }
    }

    /**
     * 获取 系统公众号的 默认角色
     * @param tenantCode
     * @return
     */
    @Override
    public String getWxUserDefaultRole(String tenantCode) {

        Object o = redisTemplate.opsForHash().get(SaasRedisBusinessKey.getRedisKeyWxUserDefaultRole(), tenantCode);
        if (Objects.isNull(o)) {
            BaseContextHandler.setTenant(tenantCode);
            RegGuide regGuide = baseMapper.selectOne(Wraps.<RegGuide>lbQ()
                    .last(" limit 0, 1 ")
                    .select(SuperEntity::getId, RegGuide::getWxUserDefaultRole));
            String userDefaultRole = regGuide.getWxUserDefaultRole();
            redisTemplate.opsForHash().put(SaasRedisBusinessKey.getRedisKeyWxUserDefaultRole(), tenantCode, userDefaultRole);
            return userDefaultRole;
        } else {
            return o.toString();
        }

    }



    @Override
    public void initRedisData() {

        Boolean hasKey = redisTemplate.hasKey(SaasRedisBusinessKey.getRedisKeyWxUserDefaultRole());
        if (Objects.nonNull(hasKey) && hasKey) {
            return;
        }

        List<RegGuide> guideList = baseMapper.selectWxUserDefaultRole();

        if (CollUtil.isEmpty(guideList)) {
            return;
        }
        guideList.forEach(item -> redisTemplate.opsForHash().put(SaasRedisBusinessKey.getRedisKeyWxUserDefaultRole(), item.getTenantCode(), item.getWxUserDefaultRole()));
    }

    @Override
    public void initGuide() {
        RegGuide regGuide = baseMapper.selectOne(Wraps.<RegGuide>lbQ().last("limit 0,1"));
        if (Objects.isNull(regGuide)) {
            regGuide = new RegGuide();
        }
        regGuide.setGuide("点击即注册，开启健康守护之旅");
        regGuide.setDescribe("填写基本信息，创建专属健康档案，让医生更了解您的健康情况");
        regGuide.setSuccessMsg("恭喜您注册成功，明天起将正式为您提供健康服务！");
        regGuide.setSuccessMsgType(1);

        regGuide.setEnableIntro(1);
        regGuide.setIntro("<p><img src=\"https://caring-deploy.obs.cn-north-4.myhuaweicloud.com/background.jpg\" width=\"100%\" /></p>");
        regGuide.setAgreement("<p>尊敬的用户：<br />欢迎使用${project.name}！<br />&nbsp;<br />您在使用${project.name}服务前，请仔细阅读本原则，一旦您开始使用，则意味着您将自愿遵守以下原则，并完全服从于${project.name}的统一管理。<br />&nbsp;<br />1、${project.name}平台的服务是一项慢病云呵护服务，针对已确诊并需要慢病照护的慢病患者。<br />&nbsp;<br />2、${project.name}的慢病照护服务为广大患者提供了一个便捷的渠道，可以通过微信服务号提交信息，平台医生/医助能够通过互联网看到这些信息，并给予相应指导，所有指导仅限参考，一切诊疗问题请线下咨询自己的医生。<br />&nbsp;<br />3、患者所有的病情信息均自行提供，需要确保录入或者上传信息及时、准确，平台医生/医助将完全根据患者提供的信息做出指导和建议，如果患者提供的信息有误，造成的任何后果自行承担。<br />&nbsp;<br />4、在照护服务过程中，所有需要定期体检的项目均是患者所患疾病所必须的，并由患者自行决定是否接受，平台医生/医助不会增加额外的非必要化验检查。<br />&nbsp;<br />5、患者提供的所有信息，其个人隐私内容不会向除患者本人和提供服务的平台医生/医助之外的任何第三方公开。<br />&nbsp;<br />6、${project.name}平台提供的慢病照护服务是常规医疗服务的有效补充，但不能代替到医院就诊。平台医生/医助的回复也可能有延迟，尤其是患者病情严重或者发生急性症状和化验结果严重异常的情况下，都需要尽快就医，不能依赖系统回复。<br />&nbsp;<br />7、患者提供的病情信息、提交的健康咨询问题，如果${project.name}平台认为其内容包含任何不基于事实、虚构、编造及无亲身经历的言论，带有威胁、淫秽、漫骂、非法及带有人身攻击之言论，含有色情、暴力、恐怖内容、含有违背伦理道德内容，涉及违法犯罪的内容，以及其他${project.name}平台认为不恰当的情况，${project.name}平台有权进行编辑、删除、直至取消该账户资格，并保留进行法律追究的权利。<br />&nbsp;<br />8、内容中出现的所有商标、服务标志、商号、商业外观、商业标识等所有权均归${project.name}平台所有。未经${project.name}平台或第三方的书面许可，禁止以任何方式使用。<br />&nbsp;<br />9、本免责声明以及其修改权、更新权及最终解释权均属${project.name}平台所有。</p>");
        regGuide.setDoctorAgreement("<p>算尊敬的用户：<br />欢迎使用${project.name}！<br />&nbsp;<br />您在使用${project.name}服务前，请仔细阅读本原则，一旦您开始使用，则意味着您将自愿遵守以下原则，并完全服从于${project.name}的统一管理。<br />&nbsp;<br />1、${project.name}平台的服务是一项慢病云呵护服务，针对已确诊并需要慢病照护的慢病患者。<br />&nbsp;<br />2、${project.name}的慢病照护服务为广大患者提供了一个便捷的渠道，可以通过微信服务号提交信息，平台医生/医助能够通过互联网看到这些信息，并给予相应指导，所有指导仅限参考，一切诊疗问题请线下咨询自己的医生。<br />&nbsp;<br />3、患者所有的病情信息均自行提供，需要确保录入或者上传信息及时、准确，平台医生/医助将完全根据患者提供的信息做出指导和建议，如果患者提供的信息有误，造成的任何后果自行承担。<br />&nbsp;<br />4、在照护服务过程中，所有需要定期体检的项目均是患者所患疾病所必须的，并由患者自行决定是否接受，平台医生/医助不会增加额外的非必要化验检查。<br />&nbsp;<br />5、患者提供的所有信息，其个人隐私内容不会向除患者本人和提供服务的平台医生/医助之外的任何第三方公开。<br />&nbsp;<br />6、${project.name}平台提供的慢病照护服务是常规医疗服务的有效补充，但不能代替到医院就诊。平台医生/医助的回复也可能有延迟，尤其是患者病情严重或者发生急性症状和化验结果严重异常的情况下，都需要尽快就医，不能依赖系统回复。<br />&nbsp;<br />7、患者提供的病情信息、提交的健康咨询问题，如果${project.name}平台认为其内容包含任何不基于事实、虚构、编造及无亲身经历的言论，带有威胁、淫秽、漫骂、非法及带有人身攻击之言论，含有色情、暴力、恐怖内容、含有违背伦理道德内容，涉及违法犯罪的内容，以及其他${project.name}平台认为不恰当的情况，${project.name}平台有权进行编辑、删除、直至取消该账户资格，并保留进行法律追究的权利。<br />&nbsp;<br />8、内容中出现的所有商标、服务标志、商号、商业外观、商业标识等所有权均归${project.name}平台所有。未经${project.name}平台或第三方的书面许可，禁止以任何方式使用。<br />&nbsp;<br />9、本免责声明以及其修改权、更新权及最终解释权均属${project.name}平台所有。</p>");

        regGuide.setHasShowDoctor(1);
        regGuide.setHasShowOrgName(1);
        regGuide.setFormHistoryRecord(1);
        regGuide.setTenantCode(BaseContextHandler.getTenant());
        baseMapper.insert(regGuide);
    }
}
