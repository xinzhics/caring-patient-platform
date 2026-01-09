package com.caring.sass.ai.face.service.impl;



import com.caring.sass.ai.config.FaceConfig;
import com.caring.sass.ai.entity.face.MergeImageFreeFrequency;
import com.caring.sass.ai.face.dao.MergeImageFreeFrequencyMapper;
import com.caring.sass.ai.face.service.MergeImageFreeFrequencyService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 融合图片免费次数
 * </p>
 *
 * @author 杨帅
 * @date 2024-06-21
 */
@Slf4j
@Service

public class MergeImageFreeFrequencyServiceImpl extends SuperServiceImpl<MergeImageFreeFrequencyMapper, MergeImageFreeFrequency> implements MergeImageFreeFrequencyService {


    @Autowired
    FaceConfig faceConfig;

    /**
     * 查询用户还剩余的免费合成图片次数
     * 新用户首次查询 有 N 次免费机会
     * @param userId
     * @return
     */
    @Override
    public MergeImageFreeFrequency queryOrInitUserFreeFrequency(Long userId) {
        MergeImageFreeFrequency freeFrequency = baseMapper.selectOne(Wraps.<MergeImageFreeFrequency>lbQ()
                .eq(MergeImageFreeFrequency::getUserId, userId));
        if (freeFrequency == null) {
            freeFrequency = new MergeImageFreeFrequency();
            freeFrequency.setUserId(userId);
            if (faceConfig.getNew_user_free() <= 0) {
                freeFrequency.setFreeMergeTotal(1);
            } else {
                freeFrequency.setFreeMergeTotal(faceConfig.getNew_user_free());
            }
            baseMapper.insert(freeFrequency);
        }
        return freeFrequency;
    }

    /**
     * 用户首次免费。后续需要付费
     * @param userId
     * @return
     */
    @Override
    public boolean hasFreeMergeImage(Long userId) {

        MergeImageFreeFrequency freeFrequency = queryOrInitUserFreeFrequency(userId);

        Integer freeMergeTotal = freeFrequency.getFreeMergeTotal();
        if (freeMergeTotal.equals(0)) {
            return false;
        }
        freeFrequency.setFreeMergeTotal(freeMergeTotal -1);
        baseMapper.updateById(freeFrequency);
        return true;

    }

}
