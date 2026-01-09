package com.caring.sass.nursing.service.tag.impl;


import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.tag.AssociationMapper;
import com.caring.sass.nursing.entity.tag.Association;
import com.caring.sass.nursing.entity.tag.Tag;
import com.caring.sass.nursing.service.tag.AssociationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 业务关联标签记录表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Service

public class AssociationServiceImpl extends SuperServiceImpl<AssociationMapper, Association> implements AssociationService {



    @Override
    public boolean existsTag(Long tagId, String associationId) {
        if (Objects.isNull(tagId)) {
            return true;
        }
        LbqWrapper<Association> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(Association::getTagId, tagId);
        lbqWrapper.eq(Association::getAssociationId, associationId);
        Integer integer = baseMapper.selectCount(lbqWrapper);
        return integer > 0;
    }


    @Override
    public void clean(Long associationId, List<Tag> tagList) {
        LbqWrapper<Association> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(Association::getAssociationId, associationId);
        if (CollUtil.isNotEmpty(tagList)) {
            Set<Long> collect = tagList.stream().map(SuperEntity::getId).collect(Collectors.toSet());
            lbqWrapper.in(Association::getTagId, collect);
        }
        baseMapper.delete(lbqWrapper);
    }
}
