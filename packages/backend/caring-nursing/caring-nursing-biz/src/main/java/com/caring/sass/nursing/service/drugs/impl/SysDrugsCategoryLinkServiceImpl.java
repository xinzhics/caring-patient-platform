package com.caring.sass.nursing.service.drugs.impl;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.drugs.SysDrugsCategoryLinkMapper;
import com.caring.sass.nursing.dao.drugs.SysDrugsCategoryMapper;
import com.caring.sass.nursing.dao.drugs.SysDrugsMapper;
import com.caring.sass.nursing.entity.drugs.SysDrugs;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategory;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategoryLink;
import com.caring.sass.nursing.service.drugs.SysDrugsCategoryLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 药品类别关联表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class SysDrugsCategoryLinkServiceImpl extends SuperServiceImpl<SysDrugsCategoryLinkMapper, SysDrugsCategoryLink> implements SysDrugsCategoryLinkService {

    @Autowired
    SysDrugsCategoryMapper sysDrugsCategoryMapper;

    @Autowired
    SysDrugsMapper sysDrugsMapper;


    /**
     * @Author yangShuai
     * @Description 给药品填充上 药品类别信息
     * @Date 2021/3/2 15:57
     *
     * @param records
     * @return void
     */
    @Override
    public void getCategoryLink(List<SysDrugs> records) {
        if (CollectionUtils.isEmpty(records))
            return;
        List<Long> collect = records.stream().map(SysDrugs::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect))
            return;
        List<SysDrugsCategoryLink> drugsCategoryLinks = baseMapper.selectList(new LbqWrapper<SysDrugsCategoryLink>().in(SysDrugsCategoryLink::getDrugsId, collect));
        if (CollectionUtils.isEmpty(drugsCategoryLinks))
            return;
        collect = drugsCategoryLinks.stream().map(SysDrugsCategoryLink::getCategoryId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(drugsCategoryLinks))
            return;
        List<SysDrugsCategory> categoryList = sysDrugsCategoryMapper.selectList(new LbqWrapper<SysDrugsCategory>().in(SysDrugsCategory::getId, collect));
        if (CollectionUtils.isEmpty(collect))
            return;
        Map<Long, String> categoryMap = categoryList.stream().collect(Collectors.toMap(SysDrugsCategory::getId, SysDrugsCategory::getLabel, (o1, o2) -> o2));

        List<Long> parentId = categoryList.stream().map(SysDrugsCategory::getParentId).collect(Collectors.toList());
        Map<Long, String> parentMap = null;
        if (CollectionUtils.isNotEmpty(parentId)) {
            List<SysDrugsCategory> parentList = sysDrugsCategoryMapper.selectList(new LbqWrapper<SysDrugsCategory>().in(SysDrugsCategory::getId, parentId));
            parentMap = parentList.stream().collect(Collectors.toMap(SysDrugsCategory::getId, SysDrugsCategory::getLabel, (o1, o2) -> o2));
        }
        Map<Long, List<SysDrugsCategoryLink>> map = new HashMap<>(records.size());

        for (SysDrugsCategoryLink categoryLink : drugsCategoryLinks) {
            List<SysDrugsCategoryLink> linkList = map.get(categoryLink.getDrugsId());
            if (CollectionUtils.isEmpty(linkList)) {
                linkList = new ArrayList<>();
            }
            categoryLink.setCategoryName(categoryMap.get(categoryLink.getCategoryId()));
            if (categoryLink.getCategoryParentId() != null && parentMap != null) {
                categoryLink.setParentCategoryName(parentMap.get(categoryLink.getCategoryParentId()));
                categoryLink.setName(categoryLink.getParentCategoryName() + "/" + categoryLink.getCategoryName());
            } else {
                categoryLink.setName(categoryLink.getCategoryName());
            }
            linkList.add(categoryLink);
            map.put(categoryLink.getDrugsId(), linkList);
        }

        for (SysDrugs record : records) {
            List<SysDrugsCategoryLink> categoryLinks = map.get(record.getId());
            if (CollectionUtils.isNotEmpty(categoryLinks)) {
                List<String> stringList = categoryLinks.stream().map(SysDrugsCategoryLink::getName).collect(Collectors.toList());
                record.setSysDrugsCategoryLinkList(categoryLinks);
                record.setCategoryName(String.join(",", stringList));
            }
        }

    }



    /**
     * @Author yangShuai
     * @Description 更新药品的分类
     * @Date 2021/3/2 15:56
     *
     * @param categoryLinkList
     * @param drugId
     * @return void
     */
    @Override
    public void save(List<SysDrugsCategoryLink> categoryLinkList, Long drugId) {
        if (CollectionUtils.isEmpty(categoryLinkList))
            return;
        LbqWrapper<SysDrugsCategoryLink> linkLbqWrapper = new LbqWrapper<>();
        linkLbqWrapper.eq(SysDrugsCategoryLink::getDrugsId, drugId);
        baseMapper.delete(linkLbqWrapper);
        for (SysDrugsCategoryLink link : categoryLinkList) {
            SysDrugsCategory category = sysDrugsCategoryMapper.selectById(link.getCategoryId());
            link.setCategoryParentId(category.getParentId());
            link.setDrugsId(drugId);
        }
        baseMapper.insertBatchSomeColumn(categoryLinkList);
    }


    /**
     * @Author yangShuai
     * @Description 通过分类查询药品的id集合
     * @Date 2021/3/2 15:56
     *
     * @param childIds
     * @return java.util.List<com.caring.sass.nursing.entity.drugs.SysDrugsCategoryLink>
     */
    @Override
    public List<SysDrugsCategoryLink> selectDrugsIdByCategoryIds(List<Long> childIds) {


        return baseMapper.selectList(new LbqWrapper<SysDrugsCategoryLink>()
                .select(SysDrugsCategoryLink::getDrugsId)
                .in(SysDrugsCategoryLink::getCategoryId, childIds));

    }

    /**
     * @Author yangShuai
     * @Description 删除药品的 分类
     * @Date 2021/3/2 15:55
     *
     * @param longs
     * @return void
     */
    @Override
    public void deleteByDrugsIds(List<Long> longs) {

        if (CollectionUtils.isNotEmpty(longs))
            baseMapper.delete(new LbqWrapper<SysDrugsCategoryLink>().in(SysDrugsCategoryLink::getDrugsId, longs));

    }
}
