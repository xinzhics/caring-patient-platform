package com.caring.sass.cms.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.cms.constant.SiteJumpTypeEnum;
import com.caring.sass.cms.constant.SitePageModuleType;
import com.caring.sass.cms.constant.SitePlateType;
import com.caring.sass.cms.dao.SiteFolderPageModuleMapper;
import com.caring.sass.cms.dao.SiteJumpInformationMapper;
import com.caring.sass.cms.dao.SiteModulePlateMapper;
import com.caring.sass.cms.dao.SiteVideoWarehouseMapper;
import com.caring.sass.cms.dto.site.SiteVideoWarehouseUpdateDTO;
import com.caring.sass.cms.entity.SiteFolderPageModule;
import com.caring.sass.cms.entity.SiteJumpInformation;
import com.caring.sass.cms.entity.SiteModulePlate;
import com.caring.sass.cms.entity.SiteVideoWarehouse;
import com.caring.sass.cms.service.SiteVideoWarehouseService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.utils.ChineseToEnglishUtil;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.file.api.ScreenshotTaskApi;
import com.caring.sass.file.dto.ScreenshotTaskSaveDTO;
import com.caring.sass.file.entity.ScreenshotTask;
import com.caring.sass.utils.PingYinUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 建站视频库
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Service

public class SiteVideoWarehouseServiceImpl extends SuperServiceImpl<SiteVideoWarehouseMapper, SiteVideoWarehouse> implements SiteVideoWarehouseService {

    @Autowired
    SiteModulePlateMapper siteModulePlateMapper;

    @Autowired
    SiteFolderPageModuleMapper siteFolderPageModuleMapper;

    @Autowired
    SiteJumpInformationMapper jumpInformationMapper;

    @Autowired
    ScreenshotTaskApi screenshotTaskApi;

    @Override
    public boolean save(SiteVideoWarehouse model) {

        checkAndSetCover(model);
        if (StrUtil.isNotEmpty(model.getVideoTitle())) {
            String letter = ChineseToEnglishUtil.getPinYinFirstLetter(model.getVideoTitle());
            model.setTitleFirstLetter(letter);
        }
        model.setNumberViews(0);
        model.setMessageNum(0);
        model.setDeleteMark(CommonStatus.NO);
        return super.save(model);
    }

    /**
     * 判断封面为空时，使用obs获取封面
     * @param warehouse
     */
    public void checkAndSetCover(SiteVideoWarehouse warehouse) {
        if (StrUtil.isBlank(warehouse.getVideoCover())) {
            ScreenshotTaskSaveDTO taskSaveDTO = new ScreenshotTaskSaveDTO();
            taskSaveDTO.setBucket("caring");
            taskSaveDTO.setObject("build_video/" + warehouse.getObsFileName());
            taskSaveDTO.setFileName(warehouse.getObsFileName());
            R<ScreenshotTask> returnCover = screenshotTaskApi.createScreenshotTaskAndReturnCover(taskSaveDTO);
            if (returnCover.getIsSuccess()) {
                ScreenshotTask coverData = returnCover.getData();
                String coverUrl = coverData.getCoverUrl();
                warehouse.setVideoCover(coverUrl);
            }
        }
    }

    @Override
    public boolean updateById(SiteVideoWarehouse model) {
        if (StrUtil.isNotEmpty(model.getVideoTitle())) {
            String letter = ChineseToEnglishUtil.getPinYinFirstLetter(model.getVideoTitle());
            model.setTitleFirstLetter(letter);
        }
        checkAndSetCover(model);
        return super.updateById(model);
    }


    @Override
    public void synchronizeInformation(SiteVideoWarehouseUpdateDTO warehouse) {

        // 查询引用了这个视频的板块
        SiteModulePlate modulePlate = new SiteModulePlate();
        UpdateWrapper<SiteModulePlate> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("image_url", warehouse.getVideoCover());
        updateWrapper.set("video_url", warehouse.getVideoUrl());
        updateWrapper.set("cms_summary", warehouse.getVideoDesc());
        updateWrapper.set("plate_title", warehouse.getVideoTitle());
        updateWrapper.set("video_duration", warehouse.getVideoDuration());
        updateWrapper.set("video_file_size", warehouse.getVideoFileSize());
        updateWrapper.eq("plate_content_id", warehouse.getId());
        updateWrapper.eq("plate_type", SitePlateType.VIDEO);
        siteModulePlateMapper.update(modulePlate, updateWrapper);

        // 查询引用了 这个视频的 组件
        UpdateWrapper<SiteFolderPageModule> moduleUpdateWrapper = new UpdateWrapper<>();
        moduleUpdateWrapper.set("video_url", warehouse.getVideoUrl());
        moduleUpdateWrapper.set("video_cover", warehouse.getVideoCover());
        moduleUpdateWrapper.eq("module_type", SitePageModuleType.VIDEO);
        moduleUpdateWrapper.eq("ware_house_video_id", warehouse.getId());
        siteFolderPageModuleMapper.update(new SiteFolderPageModule(), moduleUpdateWrapper);

        UpdateWrapper<SiteJumpInformation> jumpInformationUpdateWrapper = new UpdateWrapper<>();
        jumpInformationUpdateWrapper.set("jump_video_url", warehouse.getVideoUrl());
        jumpInformationUpdateWrapper.set("jump_video_title", warehouse.getVideoTitle());
        jumpInformationUpdateWrapper.set("jump_video_cover", warehouse.getVideoCover());
        jumpInformationUpdateWrapper.eq("jump_type", SiteJumpTypeEnum.VIDEO);
        jumpInformationUpdateWrapper.eq("jump_video_id", warehouse.getId());
        jumpInformationMapper.update(new SiteJumpInformation(), jumpInformationUpdateWrapper);


    }


    @Override
    public boolean checkVideoUse(Long videoId) {

        Integer count = siteModulePlateMapper.selectCount(Wraps.<SiteModulePlate>lbQ()
                .eq(SiteModulePlate::getPlateContentId, videoId)
                .eq(SiteModulePlate::getPlateType, SitePlateType.VIDEO));
        if (count != null && count > 0) {
            return true;
        }
        count = siteFolderPageModuleMapper.selectCount(Wraps.<SiteFolderPageModule>lbQ()
                .eq(SiteFolderPageModule::getModuleType, SitePageModuleType.VIDEO)
                .eq(SiteFolderPageModule::getWareHouseVideoId, videoId));
        if (count != null && count > 0) {
            return true;
        }
        count = jumpInformationMapper.selectCount(Wraps.<SiteJumpInformation>lbQ()
                .eq(SiteJumpInformation::getJumpType, SiteJumpTypeEnum.VIDEO)
                .eq(SiteJumpInformation::getJumpVideoId, videoId));
        if (count != null && count > 0) {
            return true;
        }
        return false;
    }
}
