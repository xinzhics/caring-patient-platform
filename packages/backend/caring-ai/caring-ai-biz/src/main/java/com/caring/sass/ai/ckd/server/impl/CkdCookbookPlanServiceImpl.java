package com.caring.sass.ai.ckd.server.impl;



import com.caring.sass.ai.ckd.dao.CkdCookBookMapper;
import com.caring.sass.ai.ckd.dao.CkdCookbookPlanMapper;
import com.caring.sass.ai.ckd.server.CkdCookbookPlanService;
import com.caring.sass.ai.ckd.server.CkdUserInfoService;
import com.caring.sass.ai.dto.ckd.CkdMembershipLevel;
import com.caring.sass.ai.entity.CookPlanMealType;
import com.caring.sass.ai.entity.ckd.CkdCookBook;
import com.caring.sass.ai.entity.ckd.CkdCookbookPlan;
import com.caring.sass.ai.entity.ckd.CkdUserInfo;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 业务实现类
 * 用户食谱计划
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Slf4j
@Service

public class CkdCookbookPlanServiceImpl extends SuperServiceImpl<CkdCookbookPlanMapper, CkdCookbookPlan> implements CkdCookbookPlanService {

    @Autowired
    CkdUserInfoService ckdUserInfoService;

    @Autowired
    CkdCookBookMapper ckdCookBookMapper;


    private static final int MAX_TRY_COUNT = 80;


    public void setCookBook(CkdCookbookPlan plan) {
        Long breakfastId = plan.getBreakfastId();
        Long dinnerId = plan.getDinnerId();
        Long lunchId = plan.getLunchId();
        List<CkdCookBook> cookBooks = ckdCookBookMapper.selectList(Wraps.<CkdCookBook>lbQ()
                .select(SuperEntity::getId, CkdCookBook::getCarbohydrates, CkdCookBook::getType, CkdCookBook::getProtein,
                        CkdCookBook::getName, CkdCookBook::getDescribeInfo, CkdCookBook::getFat, CkdCookBook::getFileUrl,
                        CkdCookBook::getTotalHeatEnergy, CkdCookBook::getTaste)
                .in(SuperEntity::getId, breakfastId, dinnerId, lunchId));
        if (CollectionUtils.isEmpty(cookBooks)) {
            return;
        }
        for (CkdCookBook cookBook : cookBooks) {
            if (cookBook.getId().equals(breakfastId)) {
                plan.setBreakfast(cookBook);
            }
            if (cookBook.getId().equals(lunchId)) {
                plan.setLunch(cookBook);
            }
            if (cookBook.getId().equals(dinnerId)) {
                plan.setDinner(cookBook);
            }
        }
    }


    @Override
    public List<CkdCookbookPlan> findCookBookPlansInLastThreeDays(String openId) {

        // 查询今天 。明天。 后天 的食谱 。 不存在的就重新生成
        CkdUserInfo userInfo = ckdUserInfoService.getByOpenId(openId);
        if (userInfo.getSerumCreatinine() == null) {
            throw new BizException("请完善血肌酐信息");
        }
        List<CkdCookbookPlan> list = new ArrayList<>();

        CkdCookbookPlan today = baseMapper.selectOne(Wraps.<CkdCookbookPlan>lbQ().eq(CkdCookbookPlan::getOpenId, openId).eq(CkdCookbookPlan::getPresentDate, LocalDate.now()));
        if (today == null) {
            today = createCookBookPlan(userInfo, LocalDate.now());
        } else {
            setCookBook(today);
        }
        list.add(today);

        CkdCookbookPlan tomorrow = baseMapper.selectOne(Wraps.<CkdCookbookPlan>lbQ().eq(CkdCookbookPlan::getOpenId, openId).eq(CkdCookbookPlan::getPresentDate, LocalDate.now().plusDays(1)));
        if (tomorrow == null) {
            tomorrow = createCookBookPlan(userInfo, LocalDate.now().plusDays(1));
        } else {
            setCookBook(tomorrow);
        }
        list.add(tomorrow);

        CkdCookbookPlan afterTomorrow = baseMapper.selectOne(Wraps.<CkdCookbookPlan>lbQ().eq(CkdCookbookPlan::getOpenId, openId).eq(CkdCookbookPlan::getPresentDate, LocalDate.now().plusDays(2)));
        if (afterTomorrow == null) {
            afterTomorrow = createCookBookPlan(userInfo, LocalDate.now().plusDays(2));
        } else {
            setCookBook(afterTomorrow);
        }
        list.add(afterTomorrow);
        return list;
    }

    private Double getProteinValueForOtherTowCookBooks(CookPlanMealType type, CkdCookbookPlan plan) {
        if (CookPlanMealType.key_cookbook_breakfast.equals(type)) {
            CkdCookBook lc = ckdCookBookMapper.selectById(plan.getLunchId());
            CkdCookBook dr = ckdCookBookMapper.selectById(plan.getDinnerId());
            return lc.getProtein() + dr.getProtein();
        }
        if (CookPlanMealType.key_cookbook_lunch.equals(type)) {
            CkdCookBook bf = ckdCookBookMapper.selectById(plan.getBreakfastId());
            CkdCookBook dr = ckdCookBookMapper.selectById(plan.getDinnerId());
            return bf.getProtein() + dr.getProtein();
        }
        if (CookPlanMealType.key_cookbook_dinner.equals(type)) {
            CkdCookBook bf = ckdCookBookMapper.selectById(plan.getBreakfastId());
            CkdCookBook lc = ckdCookBookMapper.selectById(plan.getLunchId());
            return lc.getProtein() + bf.getProtein();
        }
        return 0d;
    }


    @Override
    public CkdCookbookPlan changeCookBookPlans(String openId, Long planId, CookPlanMealType type) {

        CkdUserInfo userInfo = ckdUserInfoService.getByOpenId(openId);
        if (userInfo.getSerumCreatinine() == null) {
            throw new BizException("请完善血肌酐信息");
        }
        Double proteinInTake = userInfo.getProteinInTake();
        CkdCookbookPlan cookbookPlan = baseMapper.selectById(planId);

        List<CkdCookbookPlan> cookBookPlanList = baseMapper.selectList(Wraps.<CkdCookbookPlan>lbQ()
                .eq(CkdCookbookPlan::getOpenId, userInfo.getOpenId())
                .ge(CkdCookbookPlan::getPresentDate, cookbookPlan.getPresentDate().plusDays(-1))
                .le(CkdCookbookPlan::getPresentDate, cookbookPlan.getPresentDate())
        );
        Set<Long> excludeCookbookSet = getExcludeCookbookSet(cookBookPlanList);

        Double otherTwoCookbookValues = getProteinValueForOtherTowCookBooks(type, cookbookPlan);
        List<CkdCookBook> cookBooks = ckdCookBookMapper.selectList(Wraps.<CkdCookBook>lbQ()
                .eq(CkdCookBook::getType, type.toString())
                .le(CkdCookBook::getProtein, proteinInTake - otherTwoCookbookValues + 3));

        Long foundCookbookId = selectRandomCookBook(excludeCookbookSet, cookBooks);
        if (foundCookbookId == null) {
            throw new BizException("没有合适的食谱");
        }
        if (CookPlanMealType.key_cookbook_breakfast.equals(type)) {
            cookbookPlan.setBreakfastId(foundCookbookId);
        } else if (CookPlanMealType.key_cookbook_lunch.equals(type)) {
            cookbookPlan.setLunchId(foundCookbookId);
        } else if (CookPlanMealType.key_cookbook_dinner.equals(type)) {
            cookbookPlan.setDinnerId(foundCookbookId);
        }
        baseMapper.updateById(cookbookPlan);
        setCookBook(cookbookPlan);
        return cookbookPlan;
    }

    private Long selectRandomCookBook(Set<Long> excludeCookbookSet,
                                      List<CkdCookBook> cookbookList) {
        int maxCount = cookbookList.size() < MAX_TRY_COUNT ? cookbookList.size() : MAX_TRY_COUNT;
        List<Integer> randomSeq = new ArrayList<>();
        try {
            while (maxCount >= 0) {
                Random random = new Random();
                int cookbookRandomNumber = random.nextInt(cookbookList.size());
                randomSeq.add(cookbookRandomNumber);
                CkdCookBook cookbook = cookbookList.get(cookbookRandomNumber);
                if (Objects.nonNull(cookbook.getProtein()) && !excludeCookbookSet.contains(cookbook.getId())) {
                    return cookbook.getId();
                } else {
                }
                --maxCount;
            }
            return null;
        } catch (Exception e) {
            log.error("selectRandomCookBook error,randomSeq:{}", randomSeq);
        }
        return null;
    }

    /**
     * 根据用户的信息。给用户生成食谱
     * @param userInfo
     * @param presentDate
     * @return
     */
    public CkdCookbookPlan createCookBookPlan(CkdUserInfo userInfo, LocalDate presentDate) {

        if (CkdMembershipLevel.LOSE_EFFICACY.equals(userInfo.getMembershipLevel())) {
            throw new BizException("请购买会员");
        }
        if (CkdMembershipLevel.FREE.equals(userInfo.getMembershipLevel())) {
            // 免费会员。
            if (userInfo.getExpirationDate() == null) {
                if (userInfo.getCreateTime().plusDays(7).isBefore(LocalDateTime.now())) {
                    throw new BizException("免费会员已到期，请购买会员！");
                }
            } else if (userInfo.getExpirationDate().isBefore(LocalDateTime.now())) {
                throw new BizException("免费会员已到期，请购买会员！");
            }
        }
        if (CkdMembershipLevel.TIME_LIMITED.equals(userInfo.getMembershipLevel()) &&
            userInfo.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new BizException("会员已到期，请续费！");
        }

        Double proteinInTake = userInfo.getProteinInTake();
        List<CkdCookbookPlan> cookBookPlanList = baseMapper.selectList(Wraps.<CkdCookbookPlan>lbQ()
                .eq(CkdCookbookPlan::getOpenId, userInfo.getOpenId())
                .ge(CkdCookbookPlan::getPresentDate, presentDate.plusDays(-1))
                .le(CkdCookbookPlan::getPresentDate, presentDate)
        );
        Set<Long> excludeCookbookSet = getExcludeCookbookSet(cookBookPlanList);
        CkdCookbookPlan bookPlan = createCookBookPlan(userInfo.getOpenId(), proteinInTake, presentDate, excludeCookbookSet);
        baseMapper.insert(bookPlan);
        return bookPlan;
    }


    private Set<Long> getExcludeCookbookSet(List<CkdCookbookPlan> cookBookPlanList) {
        Set<Long> excludeCookbookSet = new HashSet<>();
        if (CollectionUtils.isEmpty(cookBookPlanList)) {
            return excludeCookbookSet;
        }
        for (int i = 0; i < cookBookPlanList.size(); i++) {
            excludeCookbookSet.add(cookBookPlanList.get(i).getBreakfastId());
            excludeCookbookSet.add(cookBookPlanList.get(i).getLunchId());
            excludeCookbookSet.add(cookBookPlanList.get(i).getDinnerId());
        }
        return excludeCookbookSet;
    }


    private CkdCookBook selectRandomCookBook(Double lessThanProtein, Set<Long> excludeCookbookSet, List<CkdCookBook> cookbookList, String type) {
        int maxCount = cookbookList.size();
        while (maxCount >= 0) {
            Random random = new Random();
            int cookbookRandomNumber = random.nextInt(cookbookList.size());
            CkdCookBook cookbook = cookbookList.get(cookbookRandomNumber);
            if (Objects.nonNull(cookbook.getProtein())
                    && !excludeCookbookSet.contains(cookbook.getId())
                    && cookbook.getProtein() < lessThanProtein) {
                log.debug("{} cookbook is found: {}, {}", type, cookbook.getId(), cookbook.getProtein());
                return cookbook;
            }
            --maxCount;
        }
        return null;
    }


    private boolean inRange(Double totalProtein, Double proteinInTake) {
        return Objects.nonNull(totalProtein) && Objects.nonNull(proteinInTake)
                && totalProtein <= (proteinInTake);
    }

    /**
     * 推荐食谱生成算法。每次只生产三天的食谱
     *             参数说明
     *     personId： 患者ID
     *     proteinInTake： 建议蛋白质摄入量
     *     today： 开始生成食谱的日期
     *     excludeCookbookSet： 患者已用或准备使用的食谱id
     *     MAX_TRY_COUNT： 80 次
     *     breakfastList: 早餐的食谱列表
     *     lunchList： 午餐的食谱列表
     *     dinnerList： 晚餐的视频列表
     */
    public CkdCookbookPlan createCookBookPlan(String openId, Double proteinInTake, LocalDate today, Set<Long> excludeCookbookSet) {
        int maxCount = 0;
        Double useProteinInTake = proteinInTake + 3;
        CkdCookbookPlan cookBookPlan = null;
        List<CkdCookBook> breakfastList = ckdCookBookMapper.selectList(Wraps.<CkdCookBook>lbQ()
                .select(SuperEntity::getId, CkdCookBook::getCarbohydrates, CkdCookBook::getType, CkdCookBook::getProtein,
                        CkdCookBook::getName, CkdCookBook::getDescribeInfo, CkdCookBook::getFat, CkdCookBook::getFileUrl,
                        CkdCookBook::getTotalHeatEnergy, CkdCookBook::getTaste)
                .eq(CkdCookBook::getType, "key_cookbook_breakfast"));
        List<CkdCookBook> lunchList = ckdCookBookMapper.selectList(Wraps.<CkdCookBook>lbQ()
                .select(SuperEntity::getId, CkdCookBook::getCarbohydrates, CkdCookBook::getType, CkdCookBook::getProtein,
                        CkdCookBook::getName, CkdCookBook::getDescribeInfo, CkdCookBook::getFat, CkdCookBook::getFileUrl,
                        CkdCookBook::getTotalHeatEnergy, CkdCookBook::getTaste)
                .eq(CkdCookBook::getType, "key_cookbook_lunch"));
        List<CkdCookBook> dinnerList = ckdCookBookMapper.selectList(Wraps.<CkdCookBook>lbQ()
                .select(SuperEntity::getId, CkdCookBook::getCarbohydrates, CkdCookBook::getType, CkdCookBook::getProtein,
                        CkdCookBook::getName, CkdCookBook::getDescribeInfo, CkdCookBook::getFat, CkdCookBook::getFileUrl,
                        CkdCookBook::getTotalHeatEnergy, CkdCookBook::getTaste)
                .eq(CkdCookBook::getType, "key_cookbook_dinner"));
        while (maxCount <= MAX_TRY_COUNT) {
            if (maxCount == MAX_TRY_COUNT) {
                // 确保选中一个方案
                break;
            }
            //随机选取早餐
            CkdCookBook breakfast = selectRandomCookBook(proteinInTake,
                    excludeCookbookSet,
                    breakfastList,
                    "Breakfast");
            if (Objects.isNull(breakfast)) {
                ++maxCount;
                continue;
            }
            //随机选取午餐
            CkdCookBook lunch = selectRandomCookBook(useProteinInTake - breakfast.getProtein(),
                    excludeCookbookSet,
                    lunchList,
                    "Lunch");
            if (Objects.isNull(lunch)) {
                ++maxCount;
                continue;
            }
            //随机选取晚餐
            CkdCookBook dinner = selectRandomCookBook(useProteinInTake - lunch.getProtein() - breakfast.getProtein(),
                    excludeCookbookSet,
                    dinnerList,
                    "Dinner");
            if (Objects.isNull(dinner)) {
                ++maxCount;
                continue;
            }

            Double breakfastProtein = breakfast.getProtein();
            Double lunchProtein = lunch.getProtein();
            Double dinnerProtein = dinner.getProtein();
            Double totalProtein = breakfastProtein + lunchProtein + dinnerProtein;

            log.debug("发现推荐食谱: {}, {}, {}; total protein: {}",
                    breakfastProtein, lunchProtein, dinnerProtein, totalProtein);
            if (inRange(totalProtein, useProteinInTake)) {
                cookBookPlan = new CkdCookbookPlan();
                cookBookPlan.setBreakfastId(breakfast.getId());
                cookBookPlan.setBreakfast(breakfast);
                cookBookPlan.setLunchId(lunch.getId());
                cookBookPlan.setLunch(lunch);
                cookBookPlan.setDinnerId(dinner.getId());
                cookBookPlan.setDinner(dinner);
                cookBookPlan.setOpenId(openId);
                cookBookPlan.setPresentDate(today);
                cookBookPlan.setIsDuplicate(false);
                break;
            }

            ++maxCount;
        }
        return cookBookPlan;
    }


}
