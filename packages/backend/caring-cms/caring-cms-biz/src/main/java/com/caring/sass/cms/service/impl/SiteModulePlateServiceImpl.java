package com.caring.sass.cms.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.cms.constant.SitePlateType;
import com.caring.sass.cms.dao.SiteJumpInformationMapper;
import com.caring.sass.cms.dao.SiteModulePlateMapper;
import com.caring.sass.cms.entity.*;
import com.caring.sass.cms.service.*;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 建站组件的板块表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Service

public class SiteModulePlateServiceImpl extends SuperServiceImpl<SiteModulePlateMapper, SiteModulePlate> implements SiteModulePlateService {

    @Autowired
    SiteJumpInformationService siteJumpInformationService;

    @Autowired
    ChannelContentService channelContentService;

    @Autowired
    ContentCollectService contentCollectService;

    @Autowired
    SiteVideoWarehouseService siteVideoWarehouseService;

    @Autowired
    SiteJumpInformationMapper jumpInformationMapper;


    @Override
    public void saveList(Long folderId, Long pageId, Long moduleId,  List<SiteModulePlate> labelPlates, Snowflake snowflake) {
        if (CollUtil.isEmpty(labelPlates)) {
            return;
        }

        SiteJumpInformation information;
        List<SiteJumpInformation> jumps = new ArrayList<>();
        for (SiteModulePlate labelPlate : labelPlates) {
            long plateId = snowflake.nextId();
            labelPlate.setId(plateId);
            labelPlate.setFolderId(folderId);
            labelPlate.setId(plateId);
            labelPlate.setPageId(pageId);
            labelPlate.setModuleId(moduleId);
            information = labelPlate.getJumpInformation();

            if (Objects.nonNull(information)) {
                information.setFolderId(folderId);
                information.setPageId(pageId);
                information.setModuleId(moduleId);
                information.setModulePlateId(plateId);
                jumps.add(information);
            }
        }
        baseMapper.insertBatchSomeColumn(labelPlates);

        siteJumpInformationService.saveBatch(jumps);

    }


    @Override
    public void setPlateJump(List<SiteModulePlate> siteModulePlates) {
        if (CollUtil.isEmpty(siteModulePlates)) {
            return;
        }
        List<Long> plateIds = siteModulePlates.stream().map(SuperEntity::getId).collect(Collectors.toList());
        List<SiteJumpInformation> jumpInformations = jumpInformationMapper.selectList(Wraps.<SiteJumpInformation>lbQ()
                .in(SiteJumpInformation::getModulePlateId, plateIds));

        Map<Long, SiteJumpInformation> informationMap = new HashMap<>();
        if (CollUtil.isNotEmpty(jumpInformations)) {
            informationMap = jumpInformations.stream().collect(Collectors.toMap(SiteJumpInformation::getModulePlateId, item -> item, (o1, o2) -> o2));
        }
        for (SiteModulePlate modulePlate : siteModulePlates) {
            SiteJumpInformation information = informationMap.get(modulePlate.getId());
            modulePlate.setJumpInformation(information);
        }
    }


    /**
     * 设置文章。视频的 点击量。评论数
     */
    @Override
    public void setPlatesAllInfo(List<SiteModulePlate> siteModulePlates) {

        if (CollUtil.isEmpty(siteModulePlates)) {
            return;
        }
        Map<SitePlateType, List<SiteModulePlate>> plateTypeListMap = siteModulePlates.stream().collect(Collectors.groupingBy(SiteModulePlate::getPlateType));
        List<SiteModulePlate> modulePlates = plateTypeListMap.get(SitePlateType.ARTICLE);
        if (CollUtil.isNotEmpty(modulePlates)) {
            Set<Long> cmsIds = modulePlates.stream().map(SiteModulePlate::getPlateContentId).collect(Collectors.toSet());
            // 查询cms 对应的 文章信息。 收藏数量。 评论数量
            if (CollUtil.isNotEmpty(cmsIds)) {
                List<ChannelContent> contentList = channelContentService.list(Wraps.<ChannelContent>lbQ().in(SuperEntity::getId, cmsIds)
                        .select(SuperEntity::getId, ChannelContent::getTitle, ChannelContent::getSummary, SuperEntity::getCreateTime,
                                Entity::getUpdateTime, ChannelContent::getHitCount, ChannelContent::getMessageNum));
                QueryWrapper<ContentCollect> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("content_id", "count(*) as countNumber");
                queryWrapper.in("content_id", cmsIds);
                queryWrapper.eq("collect_status", CommonStatus.YES);
                queryWrapper.groupBy("content_id");
                List<Map<String, Object>> listMaps = contentCollectService.listMaps(queryWrapper);
                Map<Long, Integer> cmsCollectMap = new HashMap<>();
                if (CollUtil.isNotEmpty(listMaps)) {
                    for (Map<String, Object> listMap : listMaps) {
                        Object content_id = listMap.get("content_id");
                        Object countNumber = listMap.get("countNumber");
                        if (Objects.isNull(content_id) || Objects.isNull(countNumber)) {
                            continue;
                        }
                        cmsCollectMap.put(Long.parseLong(content_id.toString()), Integer.parseInt(countNumber.toString()));
                    }
                }
                Map<Long, ChannelContent> cmsIdMap = new HashMap<>();
                if (CollUtil.isNotEmpty(contentList)) {
                    cmsIdMap = contentList.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
                }
                for (SiteModulePlate plate : modulePlates) {
                    Long cmsId = plate.getPlateContentId();
                    ChannelContent content = cmsIdMap.get(cmsId);
                    Integer collectNumber = cmsCollectMap.get(cmsId);
                    if (Objects.nonNull(content)) {
                        plate.setCmsSummary(content.getSummary());
                        plate.setPlateTitle(content.getTitle());
                        plate.setCollectNumber(collectNumber);
                        plate.setShowTime(content.getUpdateTime() != null ? content.getUpdateTime() : content.getCreateTime() != null ? content.getCreateTime() : LocalDateTime.now());
                        plate.setClickNumber(content.getHitCount());
                        plate.setCommentNumber(content.getMessageNum());
                    }
                }
            }
        }

        List<SiteModulePlate> videoPlateLists = plateTypeListMap.get(SitePlateType.VIDEO);
        if (CollUtil.isNotEmpty(videoPlateLists)) {
            Set<Long> videoIds = videoPlateLists.stream().map(SiteModulePlate::getPlateContentId).collect(Collectors.toSet());
            if (CollUtil.isNotEmpty(videoIds)) {
                List<SiteVideoWarehouse> warehouses = siteVideoWarehouseService.list(Wraps.<SiteVideoWarehouse>lbQ()
                        .select(SuperEntity::getId, SiteVideoWarehouse::getMessageNum, SiteVideoWarehouse::getNumberViews,
                                SiteVideoWarehouse::getVideoDesc, SiteVideoWarehouse::getVideoTitle,
                                SuperEntity::getCreateTime, Entity::getUpdateTime)
                        .in(SuperEntity::getId, videoIds));
                if (CollUtil.isNotEmpty(warehouses)) {
                    Map<Long, SiteVideoWarehouse> warehouseMap = warehouses.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
                    videoPlateLists.forEach(item -> {
                        SiteVideoWarehouse warehouse = warehouseMap.get(item.getPlateContentId());
                        if (Objects.nonNull(warehouse)) {
                            item.setPlateTitle(warehouse.getVideoTitle());
                            item.setCmsSummary(warehouse.getVideoDesc());
                            item.setShowTime(warehouse.getUpdateTime() != null ? warehouse.getUpdateTime() : warehouse.getCreateTime() != null ? warehouse.getCreateTime() : LocalDateTime.now());
                            if (Objects.nonNull(warehouse.getNumberViews())) {
                                item.setClickNumber(Long.parseLong(warehouse.getNumberViews().toString()));
                            }
                            item.setCommentNumber(warehouse.getMessageNum());
                        }
                    });
                }
            }

        }

    }


    /**
     * 更新cms内容后。更新cms的标题和摘要。
     * @param model
     * @param tenant
     */
    @Override
    public void updateCmsInfo(ChannelContent model, String tenant) {
        if (BizConstant.SUPER_TENANT.equals(tenant)) {
            return;
        }
        SiteModulePlate plate = new SiteModulePlate();
        UpdateWrapper<SiteModulePlate> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("plate_type", SitePlateType.ARTICLE);
        updateWrapper.eq("plate_content_id", model.getId());
        updateWrapper.set("cms_summary", model.getSummary());
        updateWrapper.set("plate_title", model.getTitle());

        baseMapper.update(plate, updateWrapper);

    }
}
