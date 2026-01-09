package com.caring.sass.ai.ckd.server.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.ckd.dao.CkdCookBookMapper;
import com.caring.sass.ai.ckd.dao.CkdCookbookPlanMapper;
import com.caring.sass.ai.ckd.dao.CkdUserInfoMapper;
import com.caring.sass.ai.ckd.dao.CkdUserShareMapper;
import com.caring.sass.ai.ckd.server.CkdUserGfrService;
import com.caring.sass.ai.ckd.server.CkdUserInfoService;
import com.caring.sass.ai.dto.ckd.CkdFreeTimeDto;
import com.caring.sass.ai.dto.ckd.CkdMembershipLevel;
import com.caring.sass.ai.dto.ckd.CkdUserMembershipUpdateDto;
import com.caring.sass.ai.entity.ckd.CkdCookbookPlan;
import com.caring.sass.ai.entity.ckd.CkdUserGfr;
import com.caring.sass.ai.entity.ckd.CkdUserInfo;
import com.caring.sass.ai.entity.ckd.CkdUserShare;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * CKD用户信息
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Slf4j
@Service

public class CkdUserInfoServiceImpl extends SuperServiceImpl<CkdUserInfoMapper, CkdUserInfo> implements CkdUserInfoService {


    @Autowired
    CkdUserGfrService ckdUserGfrService;

    @Autowired
    CkdUserShareMapper ckdUserShareMapper;

    /**
     * 查询openId对应的用户信息，
     * 如果用户不存在，就创建一个，并返回。
     * 如果用户会员到期，则修改会员等级为过期
     * @param openId
     * @return
     */
    @Override
    public CkdUserInfo getByOpenId(String openId) {
        CkdUserInfo userInfo = baseMapper.selectOne(Wraps.<CkdUserInfo>lbQ().eq(CkdUserInfo::getOpenId, openId).last(" limit 1 "));
        if (userInfo == null) {
            userInfo = new CkdUserInfo();
            userInfo.setOpenId(openId);
            userInfo.setExpirationDate(LocalDateTime.now().plusDays(7));
            userInfo.setMembershipLevel(CkdMembershipLevel.FREE);
            baseMapper.insert(userInfo);
            return userInfo;
        } else {
            CkdMembershipLevel membershipLevel = userInfo.getMembershipLevel();
            if (membershipLevel.equals(CkdMembershipLevel.PERMANENT) || membershipLevel.equals(CkdMembershipLevel.LOSE_EFFICACY)) {
                return userInfo;
            }
            // 限时会员，查询用户会员是否 到期。
            if (membershipLevel.equals(CkdMembershipLevel.TIME_LIMITED)) {
                if (userInfo.getExpirationDate().isBefore(LocalDateTime.now())) {
                    userInfo.setMembershipLevel(CkdMembershipLevel.LOSE_EFFICACY);
                    baseMapper.updateById(userInfo);
                }
            }
            // 免费会员 免费7天。
            if (membershipLevel.equals(CkdMembershipLevel.FREE)) {
                if (userInfo.getExpirationDate() == null) {
                    // 没有过期时间，就初始化一个。目前不存在此情况
                    userInfo.setExpirationDate(userInfo.getCreateTime().plusDays(7));
                }
                if (userInfo.getExpirationDate().isBefore(LocalDateTime.now())) {
                    userInfo.setMembershipLevel(CkdMembershipLevel.LOSE_EFFICACY);
                    baseMapper.updateById(userInfo);
                }
            }
        }
        return userInfo;
    }

    // 计算年龄
    public int getAge(LocalDate date) {
        if (date == null) {
            return 0; // 如果传入的日期为空，返回0岁
        }

        int age;
        LocalDate now = LocalDate.now(); // 当前日期
        if (date.isAfter(now)) { // 如果传入的日期在当前日期之后，返回0岁
            age = 0;
        } else {
            age = now.getYear() - date.getYear();
            if (now.getMonthValue() < date.getMonthValue() ||
                    (now.getMonthValue() == date.getMonthValue() && now.getDayOfMonth() < date.getDayOfMonth())) {
                age -= 1;
            }
        }
        return age;
    }


    /**
     * 2. GFR算法：
     * //男： GFR = 186 * Math.pow((scr / 88.4), -1.154) * Math.pow(age, -0.203)
     * //女： GFR = 186 * Math.pow((scr / 88.4), -1.154) * Math.pow(age, -0.203) * (0.742);
     * @param model
     * @return
     */
    public Double getGrf(CkdUserInfo model) {

        Double scr = model.getSerumCreatinine();
        if (scr == null) {
            return null;
        }
        Double GFR ;
        DecimalFormat df = new DecimalFormat("#.00");
        if (model.getGender().equals(CkdUserInfo.male)) {
            GFR = 186 * Math.pow((scr / 88.4), -1.154) * Math.pow(getAge(model.getDateOfBirth()), -0.203);
        } else {
            GFR = 186 * Math.pow((scr / 88.4), -1.154) * Math.pow(getAge(model.getDateOfBirth()), -0.203) * (0.742);
        }
        return Double.parseDouble(df.format(GFR));
    }

    /**
     * 计算蛋白质摄入量
     * @param model
     * @return
     */
    public Double getProteinInTake(CkdUserInfo model) {
        Double gfr = model.getGfr();
        Double height = model.getUserHeight();
        double standardWeight = height - 105;

        if (gfr == null) {
            return null;
        }
        double proteinInTake = 0d;
        if (gfr >= 60) {
            proteinInTake = 0.8f * standardWeight;
        } else if (gfr >= 0) {
            proteinInTake = 0.6f * standardWeight;
        }
        BigDecimal bDec = new BigDecimal(proteinInTake);
        proteinInTake = bDec.setScale(1, RoundingMode.HALF_UP).doubleValue();
        model.setProteinInTake(proteinInTake);
        return proteinInTake;
    }

    /**
     * 计算bmi
     * @param height
     * @param weight
     * @return
     */
    public double calculateBMI(double height, double weight) {
        double bmi = weight / Math.pow((height / 100), 2);
        double v = Math.round(bmi * 10.0) / 10.0;
        DecimalFormat df = new DecimalFormat("#.00");
        return Double.parseDouble(df.format(v));
    }


    public Double getBmiIndex(CkdUserInfo model) {
        if (model.getUserHeight() == null || model.getUserWeight() == null) {
            return null;
        }
        return calculateBMI(model.getUserHeight(), model.getUserWeight());
    }

    String KEY_DISEASE_STAGING_CKD1 = "key_disease_staging_CKD1";
    String KEY_DISEASE_STAGING_CKD2 = "key_disease_staging_CKD2";
    String KEY_DISEASE_STAGING_CKD3 = "key_disease_staging_CKD3";
    String KEY_DISEASE_STAGING_CKD4 = "key_disease_staging_CKD4";
    String KEY_DISEASE_STAGING_CKD5 = "key_disease_staging_CKD5";

    /**
     * @Description 根据gfr 判断是肾病几期
     * @Author 韩凯
     * @Date 2019/12/27 13:49
     */
    public String evalStagingCKD(double GFR) {
        BigDecimal bigDecimal = new BigDecimal(GFR);
        GFR = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (GFR >= 90) {
            return KEY_DISEASE_STAGING_CKD1;
        }
        if (GFR >= 60) {
            return KEY_DISEASE_STAGING_CKD2;
        }
        if (GFR >= 30) {
            return KEY_DISEASE_STAGING_CKD3;
        }
        if (GFR >= 15) {
            return KEY_DISEASE_STAGING_CKD4;
        }
        if (0 < GFR) {
            return KEY_DISEASE_STAGING_CKD5;
        }
        return null;
    }


    @Autowired
    CkdCookbookPlanMapper ckdCookBookPlanMapper;


    @Override
    public boolean updateById(CkdUserInfo model) {

        Double oldGfr = model.getGfr();
        Double newGrf = getGrf(model);


        Double bmiIndex = getBmiIndex(model);
        if (newGrf != null) {
            model.setGfr(newGrf);
            // 计算蛋白质摄入量
            getProteinInTake(model);

            // 计算肾病分期
            String string = evalStagingCKD(newGrf);
            model.setKidneyDiseaseStaging(string);

            // 当用户的 gfr 变化时， 记录一下
            if (!newGrf.equals(oldGfr)) {
                CkdUserGfr userGfr = new CkdUserGfr();
                userGfr.setGfr(newGrf);
                userGfr.setOpenId(model.getOpenId());
                ckdUserGfrService.save(userGfr);

                // 清除用户的食谱计划。重新生产
                ckdCookBookPlanMapper.delete(Wraps.<CkdCookbookPlan>lbQ()
                        .eq(CkdCookbookPlan::getOpenId, model.getOpenId())
                        .ge(CkdCookbookPlan::getPresentDate, LocalDate.now()));

            }
        }
        /**高尿酸标签**/
        if (Objects.nonNull(model.getUricAcid())) {
            if (model.getUricAcid() > 420) {
                model.setHighUricAcidLabel(true);
            }
        }

        /**高血脂标签**/
        if (Objects.nonNull(model.getTcOfBloodLipid())) {
            if (model.getTcOfBloodLipid() >= 5.7) {
                model.setHyperlipidemiaLabel(true);
            }
        }

        /**高磷标签**/
        if (Objects.nonNull(model.getPOfelectrolyte())) {
            if (model.getPOfelectrolyte() > 1.45) {
                model.setHighPhosphorusLabel(true);
            }
        }

        /** 高钾标签**/
        if (Objects.nonNull(model.getKOfelectrolyte())) {
            if (model.getKOfelectrolyte() > 5.6) {
                model.setHighPotassiumLabel(true);
            }
        }

        /**超重/肥胖标签**/
        if (Objects.nonNull(bmiIndex)) {
            if (bmiIndex >= 25) {
                model.setFatLabel(true);
            }
        }

        /**高龄标签**/
        if (Objects.nonNull(model.getDateOfBirth())) {
            if (getAge(model.getDateOfBirth()) >= 60) {
                model.setAdvancedAgeLabel(true);
            }
        }

        // 高血压标签
        if (model.getHypertension() != null && model.getHypertension()) {
            model.setHighBloodPressureLabel(true);
        }

        if (Objects.nonNull(model.getUserHeight())) {
            Double height = model.getUserHeight();
            double standardWeight = height - 105;
            if (StringUtils.equals(model.getKidneyDiseaseStaging(), KEY_DISEASE_STAGING_CKD1) ||
                    StringUtils.equals(model.getKidneyDiseaseStaging(), KEY_DISEASE_STAGING_CKD2)) {
                model.setProteinInTakeEveryday(standardWeight * 0.8f);
            } else {
                model.setProteinInTakeEveryday(standardWeight * 0.6f);
            }
            if (Objects.equals(model.getFatLabel(), true) &&
                    Objects.equals(model.getAdvancedAgeLabel(), true)) {
                model.setInTakeEnergyEveryday(standardWeight * 30);
            } else {
                model.setInTakeEnergyEveryday(standardWeight * 35);
            }
        }


        return super.updateById(model);
    }

    /**
     * 更新用户的粉丝拥有的免费时间
     * @param ckdFreeTimeDto
     */
    @Override
    public void updateFreeTime(CkdFreeTimeDto ckdFreeTimeDto) {

        Long userId = ckdFreeTimeDto.getUserId();
        Integer freeDay = ckdFreeTimeDto.getFreeDay();
        CkdUserInfo userInfo = new CkdUserInfo();
        UpdateWrapper<CkdUserInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("create_user", userId);
        updateWrapper.set("free_duration_fans", freeDay);
        baseMapper.update(userInfo, updateWrapper);

    }

    /**
     * 给用户覆盖设置会员
     * @param membershipUpdateDto
     */
    @Override
    public void setMembershipLevel(CkdUserMembershipUpdateDto membershipUpdateDto) {
        Long ckdUserId = membershipUpdateDto.getCkdUserId();
        CkdUserInfo userInfo;
        CkdMembershipLevel dtoMembershipLevel = membershipUpdateDto.getMembershipLevel();
        if (dtoMembershipLevel == null) {
            throw new BizException("参数不能为空");
        }
        LocalDateTime expirationDate = membershipUpdateDto.getExpirationDate();
        if (expirationDate == null) {
            throw new BizException("参数不能为空");
        }
        if (Objects.nonNull(ckdUserId)) {
            userInfo = baseMapper.selectById(ckdUserId);
        } else {
            String userName = membershipUpdateDto.getUserName();
            if (StrUtil.isBlank(userName)) {
                throw new BizException("用户名称不能为空");
            }
            Integer count = baseMapper.selectCount(Wraps.<CkdUserInfo>lbQ().eq(CkdUserInfo::getNickname, userName));
            if (count == null || count > 1 || count == 0) {
                throw new BizException("用户不存在或用户名称重复");
            }
            List<CkdUserInfo> ckdUserInfos = baseMapper.selectList(Wraps.<CkdUserInfo>lbQ().eq(CkdUserInfo::getNickname, userName));
            if (ckdUserInfos.isEmpty()) {
                throw new BizException("用户不存在");
            }
            if (ckdUserInfos.size() > 1) {
                throw new BizException("用户名称重复");
            }
            userInfo = ckdUserInfos.get(0);
        }
        CkdMembershipLevel membershipLevel = userInfo.getMembershipLevel();
        if (CkdMembershipLevel.PERMANENT.equals(membershipLevel)) {
            throw new BizException("用户已经时永久会员。不可修改");
        } else {
            userInfo.setMembershipLevel(dtoMembershipLevel);
            userInfo.setExpirationDate(membershipUpdateDto.getExpirationDate());
            baseMapper.updateById(userInfo);
        }
    }
}
