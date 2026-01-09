package com.caring.sass.ai.face.service;

import com.caring.sass.ai.entity.face.MergeImageFreeFrequency;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 融合图片免费次数
 * </p>
 *
 * @author 杨帅
 * @date 2024-06-21
 */
public interface MergeImageFreeFrequencyService extends SuperService<MergeImageFreeFrequency> {

    MergeImageFreeFrequency queryOrInitUserFreeFrequency(Long userId);


    /**
     * 检查用户是否有限免次数， 如果有，则次数减少1
     * @param userId
     * @return
     */
    boolean hasFreeMergeImage(Long userId);

}
