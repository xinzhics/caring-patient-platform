package com.caring.sass.nursing.service.drugs.impl;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.ChineseToEnglishUtil;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.drugs.SysDrugsCategoryMapper;
import com.caring.sass.nursing.dao.drugs.SysDrugsMapper;
import com.caring.sass.nursing.entity.drugs.SysDrugs;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategory;
import com.caring.sass.nursing.service.drugs.SysDrugsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 药品
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class SysDrugsServiceImpl extends SuperServiceImpl<SysDrugsMapper, SysDrugs> implements SysDrugsService {

    @Autowired
    SysDrugsCategoryMapper sysDrugsCategoryMapper;

    @Override
    public boolean save(SysDrugs model) {
        if (model.getSticky() == null) {
            model.setSticky(-1);
        }
        if (StringUtils.isNotEmptyString(model.getName())) {
            model.setPyszm(ChineseToEnglishUtil.getPinYinHeadChar(model.getName()));
        }
        return super.save(model);
    }

    @Override
    public boolean updateById(SysDrugs model) {
        if (model.getSticky() != null) {
            model.setStickyTime(LocalDateTime.now());
        }

        if (model.getSticky() == null) {
            model.setSticky(-1);
        }
        return super.updateById(model);
    }
}
