package com.caring.sass.nursing.service.drugs;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategory;
import com.caring.sass.nursing.entity.drugs.SysDrugsImage;

import java.util.List;

/**
 * 药品详细图片
 */
public interface SysDrugsImageService extends SuperService<SysDrugsImage> {


    boolean save(List<SysDrugsImage> model, Long drugId);

    void deleteByDrugsIds(List<Long> longs);
}
