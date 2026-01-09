package com.caring.sass.nursing.service.drugs;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.drugs.SysDrugs;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategoryLink;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 药品和类别关联表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface SysDrugsCategoryLinkService extends SuperService<SysDrugsCategoryLink> {

    void getCategoryLink(List<SysDrugs> records);

    void save(List<SysDrugsCategoryLink> categoryLinkList, Long drugId);


    List<SysDrugsCategoryLink> selectDrugsIdByCategoryIds(List<Long> childIds);


    void deleteByDrugsIds(List<Long> longs);


}
