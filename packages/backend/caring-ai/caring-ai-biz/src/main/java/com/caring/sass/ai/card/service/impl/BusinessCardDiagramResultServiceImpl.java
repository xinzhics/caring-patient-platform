package com.caring.sass.ai.card.service.impl;



import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.card.dao.BusinessCardDiagramMapper;
import com.caring.sass.ai.card.dao.BusinessCardDiagramResultMapper;
import com.caring.sass.ai.card.dao.BusinessCardDiagramTaskMapper;
import com.caring.sass.ai.card.service.BusinessCardDiagramResultService;
import com.caring.sass.ai.card.service.BusinessCardMemberVersionService;
import com.caring.sass.ai.card.util.CozeBackgroundWorkFlow;
import com.caring.sass.ai.ckd.server.CkdUserCozeTokenService;
import com.caring.sass.ai.config.FaceConfig;
import com.caring.sass.ai.dto.ckd.CkdUserCozeTokenSaveDTO;
import com.caring.sass.ai.entity.card.*;
import com.caring.sass.ai.entity.ckd.CkdUserCozeToken;
import com.caring.sass.ai.humanVideo.task.BaiduDigitalHumanAPI;
import com.caring.sass.ai.utils.ImageThumbnails;
import com.caring.sass.ai.utils.VolcengineApi2;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.GenderType;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.exception.code.ExceptionCode;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.file.entity.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 医生名片头像合成结果
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-10
 */
@Slf4j
@Service

public class BusinessCardDiagramResultServiceImpl extends SuperServiceImpl<BusinessCardDiagramResultMapper, BusinessCardDiagramResult> implements BusinessCardDiagramResultService {

    @Autowired
    FileUploadApi fileUploadApi;

    @Autowired
    BusinessCardDiagramMapper businessCardDiagramMapper;

    @Autowired
    FaceConfig faceConfig;

    @Autowired
    VolcengineApi2 volcengineApi;

    @Autowired
    BaiduDigitalHumanAPI digitalHumanAPI;

    @Autowired
    BusinessCardDiagramTaskMapper cardDiagramTaskMapper;

    @Autowired
    BusinessCardMemberVersionService businessCardMemberVersionService;

    @Autowired
    CkdUserCozeTokenService ckdUserCozeTokenService;
    @Override
    public void updateHistory(Long userId, String humanPoster) {

        UpdateWrapper<BusinessCardDiagramResult> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("history_", true);
        updateWrapper.eq("create_user", userId);
        baseMapper.update(new BusinessCardDiagramResult(), updateWrapper);

        BusinessCardDiagramResult diagramResult = baseMapper.selectOne(Wraps.<BusinessCardDiagramResult>lbQ()
                .eq(BusinessCardDiagramResult::getImageObsUrl, humanPoster)
                .last(" limit 0,1"));
        if (Objects.nonNull(diagramResult)) {
            diagramResult.setUseding(true);
            baseMapper.updateById(diagramResult);
        }

    }

    /**
     * 创建 头像合成 任务
     * @param file
     * @param gender
     * @param userId
     * @return
     */
    @Override
    public BusinessCardDiagramTask createTask(MultipartFile file, GenderType gender, Long userId) {

        // 查询会员版本
        BusinessCardMemberVersion memberVersion = businessCardMemberVersionService.queryUserVersion(userId);

        // 基础版会员。只能创建男女性别各一次, 再上次的名片没有被使用之前，不能再创建
        if (memberVersion.getMemberVersion().equals(BusinessCardMemberVersionEnum.BASIC_EDITION)) {
            Integer count = baseMapper.selectCount(Wraps.<BusinessCardDiagramResult>lbQ()
                    .eq(BusinessCardDiagramResult::getHistory_, false)
                    .eq(BusinessCardDiagramResult::getGender, gender)
                    .eq(SuperEntity::getCreateUser, userId));
            if (count > 0) {
                throw new BizException("基础版会员，只能创建男女性别各一次");
            }
        }


        String base64Image = ImageThumbnails.getImageBase64(file);
        MockMultipartFile multipartFile = FileUtils.imageBase64ToMultipartFile(base64Image);
        // 获取检测到的人脸数量
        R<File> fileR = fileUploadApi.upload(1L, multipartFile);
        if (!fileR.getIsSuccess() || fileR.getData() == null) {
            throw new BizException(ExceptionCode.SYSTEM_BUSY.getMsg());
        }
        File data = fileR.getData();
        String url = data.getUrl();
        JSONObject jsonObject = digitalHumanAPI.verifyImage(url);
        if (!jsonObject.get("code").toString().equals("0")) {
            throw new BizException(jsonObject.getString("message"));
        }

        // 不在使用模版去生成、
        List<BusinessCardDiagram> cardDiagrams = businessCardDiagramMapper.selectList(Wraps.<BusinessCardDiagram>lbQ()
                .eq(BusinessCardDiagram::getGender, gender)
                .last(" limit 0, 3 ")
                .orderByDesc(BusinessCardDiagram::getOrder));
        if (cardDiagrams.isEmpty()) {
            throw new BizException("模版未找到");
        }

        BusinessCardDiagramTask diagramTask = new BusinessCardDiagramTask();
        diagramTask.setGender(gender);
        diagramTask.setUserId(userId);
        diagramTask.setImageObsUrl(url);
        cardDiagramTaskMapper.insert(diagramTask);

        SaasGlobalThreadPool.execute(() -> createTaskMergeResult(diagramTask.getId()));

        return diagramTask;

    }
    /**
     * 查询照片数字人生成结果
     * @param taskId
     * @return
     */
    @Override
    public List<BusinessCardDiagramResult> queryTaskMergeResult(Long taskId) {
        return baseMapper.selectList(Wraps.<BusinessCardDiagramResult>lbQ()
                .eq(BusinessCardDiagramResult::getTaskId, taskId));

    }
    /**
     * 创建照片数字人生成
     * @param taskId
     * @return
     */
    public void createTaskMergeResult(Long taskId) {

        BusinessCardDiagramTask diagramTask = cardDiagramTaskMapper.selectById(taskId);
        GenderType gender = diagramTask.getGender();
        String url = diagramTask.getImageObsUrl();
        Long userId = diagramTask.getUserId();
        List<BusinessCardDiagram> cardDiagrams = businessCardDiagramMapper.selectList(Wraps.<BusinessCardDiagram>lbQ()
                .eq(BusinessCardDiagram::getGender, gender)
                .last(" limit 0, 3 ")
                .orderByDesc(BusinessCardDiagram::getOrder));
        if (cardDiagrams.isEmpty()) {
            throw new BizException("模版未找到");
        }
//        List<String> req_key = new ArrayList<>();
//        // 美漫风格
////        req_key.add("img2img_photoverse_american_comics");
//        // 商务证件照
//        req_key.add("img2img_photoverse_executive_ID_photo");
//        // 3d人偶
//        req_key.add("img2img_photoverse_3d_weird");

        List<BusinessCardDiagramResult> list = new ArrayList<>();
        try {
            for (BusinessCardDiagram diagram : cardDiagrams) {
                // 调用人脸融合接口，传入模板图和融合图的base64编码，获取融合结果
//                String string = volcengineApi.singleImagePhotography(url, reqKey);
                String string = volcengineApi.mergeImage(url,
                        diagram.getImageObsUrl(),
                        faceConfig.getSource_similarity(),
                        faceConfig.getGpen(),
                        faceConfig.getReqKey());
                if (StrUtil.isNotEmpty(string)) {
                    MockMultipartFile  multipartFile = FileUtils.imageBase64ToMultipartFile("data:image/jpeg;base64," + string);
                    if (Objects.nonNull(multipartFile)) {
                        R<File> uploaded = fileUploadApi.upload(2L, multipartFile);
                        if (uploaded.getIsSuccess()) {
                            File fileInfo = uploaded.getData();
                            BusinessCardDiagramResult build = BusinessCardDiagramResult.builder()
                                    .gender(gender)
                                    .history_(false)
                                    .taskId(taskId)
                                    .originalDrawing(CommonStatus.NO)
                                    .imageObsUrl(fileInfo.getUrl())
                                    .build();
                            build.setCreateUser(userId);
                            list.add(build);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(" {}", e.getMessage());
        }
        if (!list.isEmpty()) {
            BusinessCardDiagramResult build = BusinessCardDiagramResult.builder()
                    .gender(gender)
                    .history_(false)
                    .taskId(taskId)
                    .originalDrawing(CommonStatus.YES)
                    .imageObsUrl(url)
                    .build();
            build.setCreateUser(userId);
            list.add(build);
            baseMapper.insertBatchSomeColumn(list);
        }

    }

    /**
     * 将用户上传的照片和模版合成并返回结果
     * @param file
     * @param userId
     * @param gender
     * @return
     */
    @Override
    public List<BusinessCardDiagramResult> saveAndMerge(MultipartFile file, Long userId, GenderType gender) {

        String base64Image = ImageThumbnails.getImageBase64(file);
        MockMultipartFile multipartFile = FileUtils.imageBase64ToMultipartFile(base64Image);
        // 获取检测到的人脸数量
        R<File> fileR = fileUploadApi.upload(1L, multipartFile);
        if (!fileR.getIsSuccess() || fileR.getData() == null) {
            throw new BizException(ExceptionCode.SYSTEM_BUSY.getMsg());
        }
        File data = fileR.getData();
        String url = data.getUrl();

        List<BusinessCardDiagram> cardDiagrams = businessCardDiagramMapper.selectList(Wraps.<BusinessCardDiagram>lbQ()
                .eq(BusinessCardDiagram::getGender, gender)
                .last(" limit 0, 3 ")
                .orderByDesc(BusinessCardDiagram::getOrder));
        if (cardDiagrams.isEmpty()) {
            throw new BizException("模版未找到");
        }


        List<BusinessCardDiagramResult> list = new ArrayList<>();
        try {
            for (BusinessCardDiagram diagram : cardDiagrams) {
                // 调用人脸融合接口，传入模板图和融合图的base64编码，获取融合结果
                String string = volcengineApi.mergeImage(url,
                        diagram.getImageObsUrl(),
                        faceConfig.getSource_similarity(),
                        faceConfig.getGpen(),
                        faceConfig.getReqKey());
                if (StrUtil.isNotEmpty(string)) {
                    multipartFile = FileUtils.imageBase64ToMultipartFile("data:image/jpeg;base64," + string);
                    if (Objects.nonNull(multipartFile)) {
                        R<File> uploaded = fileUploadApi.upload(2L, multipartFile);
                        if (uploaded.getIsSuccess()) {
                            File fileInfo = uploaded.getData();
                            BusinessCardDiagramResult build = BusinessCardDiagramResult.builder()
                                    .gender(gender)
                                    .history_(false)
                                    .originalDrawing(CommonStatus.NO)
                                    .imageObsUrl(fileInfo.getUrl())
                                    .build();
                            build.setCreateUser(userId);
                            list.add(build);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(" {}", e.getMessage());
        }
        if (CollUtil.isEmpty(list)) {
            log.error("Volcano engine interface abnormality, no AI avatar generated");
            throw new BizException(ExceptionCode.SYSTEM_BUSY.getMsg());
        }
        // todo 这里需要核实是否需要换背景，这里调用后，接口返回可能超过一分钟
       /* try{
            String userIdStr = Convert.toStr(userId);
            CkdUserCozeToken cozeToken = ckdUserCozeTokenService
                    .queryOrCreateToken(new CkdUserCozeTokenSaveDTO()
                            .setOpenId(userIdStr));
            String cozeUrl = CozeBackgroundWorkFlow.runWorkflow(url, cozeToken.getAccessToken(), userIdStr);
            if (StrUtil.isNotEmpty(cozeUrl)) {
                url = cozeUrl;
            }
        }catch (Exception e){
            log.error("CozeBackgroundWorkFlow run is error", e);
        }*/

        BusinessCardDiagramResult build = BusinessCardDiagramResult.builder()
                .gender(gender)
                .history_(false)
                .originalDrawing(CommonStatus.YES)
                .imageObsUrl(url)
                .build();
        build.setCreateUser(userId);
        list.add(build);
        baseMapper.insertBatchSomeColumn(list);
        return list;
    }
}
