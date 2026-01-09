package com.caring.sass.user.service.impl;


import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.user.dao.ImRecommendationHeatMapper;
import com.caring.sass.user.entity.ImRecommendationHeat;
import com.caring.sass.user.service.ImRecommendationHeatService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * im推荐功能热度
 * </p>
 *
 * @author 杨帅
 * @date 2024-01-16
 */
@Slf4j
@Service

public class ImRecommendationHeatServiceImpl extends SuperServiceImpl<ImRecommendationHeatMapper, ImRecommendationHeat> implements ImRecommendationHeatService {


    @Override
    public boolean save(ImRecommendationHeat model) {

        @Length(max = 20, message = "用户类型长度不能超过20") String userType = model.getUserType();
        Long userId = model.getUserId();
        @Length(max = 50, message = "功能类型长度不能超过50") String functionType = model.getFunctionType();
        Long functionId = model.getFunctionId();
        ImRecommendationHeat one = baseMapper.selectOne(Wraps.<ImRecommendationHeat>lbQ()
                .eq(ImRecommendationHeat::getUserType, userType)
                .eq(ImRecommendationHeat::getUserId, userId)
                .eq(ImRecommendationHeat::getFunctionType, functionType)
                .eq(ImRecommendationHeat::getFunctionId, functionId));
        if (Objects.nonNull(one)) {
            one.setFunctionHeat(one.getFunctionHeat() + 1);
            return super.updateById(model);
        } else {
            model.setFunctionHeat(1);
            return super.save(model);
        }

    }
}
