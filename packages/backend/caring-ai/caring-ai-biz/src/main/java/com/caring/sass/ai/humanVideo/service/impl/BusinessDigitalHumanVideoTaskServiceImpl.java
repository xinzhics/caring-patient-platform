package com.caring.sass.ai.humanVideo.service.impl;

import com.caring.sass.ai.card.service.BusinessCardHumanLimitService;
import com.caring.sass.ai.dto.humanVideo.BaiduVideoDTO;
import com.caring.sass.ai.humanVideo.util.CustomFFMPEGLocator;
import com.caring.sass.ai.service.AliYunOssFileUpload;
import com.caring.sass.ai.service.HumanVideoDownloadUtil;
import org.springframework.data.redis.core.RedisTemplate;
import ws.schild.jave.*;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.card.service.BusinessCardService;
import com.caring.sass.ai.dto.humanVideo.BusinessDigitalHumanVideoTaskSaveDTO;
import com.caring.sass.ai.dto.humanVideo.BusinessDigitalHumanVideoTaskUpdateDTO;
import com.caring.sass.ai.entity.humanVideo.*;
import com.caring.sass.ai.humanVideo.dao.BusinessDigitalHumanVideoTaskMapper;
import com.caring.sass.ai.humanVideo.dao.BusinessUserAudioTemplateMapper;
import com.caring.sass.ai.humanVideo.service.BusinessDigitalHumanVideoTaskService;
import com.caring.sass.ai.humanVideo.task.BaiduDigitalHumanAPI;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.api.FileUploadApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.process.ProcessLocator;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 数字人视频制作任务
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-14
 */
@Slf4j
@Service
public class BusinessDigitalHumanVideoTaskServiceImpl extends SuperServiceImpl<BusinessDigitalHumanVideoTaskMapper, BusinessDigitalHumanVideoTask> implements BusinessDigitalHumanVideoTaskService {

    @Autowired
    BusinessCardService businessCardService;

    @Autowired
    BusinessUserAudioTemplateMapper audioTemplateMapper;

    @Autowired
    FileUploadApi fileUploadApi;

    @Autowired
    BaiduDigitalHumanAPI digitalHumanAPI;

    @Autowired
    BusinessCardHumanLimitService humanLimitService;
    /**
     * 查询用户上次未完成资料填写的任务
     *
     * 如果没有任务，检查用户是否有名片。且名片被填充了数字人视频（定制的数据）
     * @param userId
     * @return
     */
    @Override
    public BusinessDigitalHumanVideoTask getNotStartTask(Long userId) {

        BusinessDigitalHumanVideoTask videoTask = baseMapper.selectOne(Wraps.<BusinessDigitalHumanVideoTask>lbQ()
                .eq(BusinessDigitalHumanVideoTask::getUserId, userId)
                .eq(BusinessDigitalHumanVideoTask::getTaskStatus, HumanVideoTaskStatus.SUBMIT_ING)
                .last(" limit 0,1 "));
        return videoTask;
    }


    /**
     * 提交任意其他资料
     * @param updateDTO
     * @return
     */
    @Override
    public BusinessDigitalHumanVideoTask submitTaskOtherData(BusinessDigitalHumanVideoTaskUpdateDTO updateDTO) {

        Long id = updateDTO.getId();
        if (Objects.isNull(id)) {
            throw new BizException("请选择制作方式");
        }
        BusinessDigitalHumanVideoTask videoTask = baseMapper.selectById(id);
        if (Objects.isNull(videoTask)) {
            throw new BizException("请选择制作方式");
        }

        boolean needGetAudioUrl = false;
        // 任务里面没有上传模版视频。 新提交的参数中存在模版视频。
        if (videoTask.getMakeWay().equals(HumanVideoMakeWay.VIDEO)) {
            if (StrUtil.isBlank(videoTask.getTemplateVideoUrl()) && StrUtil.isNotEmpty(updateDTO.getTemplateVideoUrl())) {
                needGetAudioUrl = true;

                // 用户修改了模版视频。 重新提取视频中的音频
            } else if (updateDTO.getTemplateVideoUrl() != null && !updateDTO.getTemplateVideoUrl().equals(videoTask.getTemplateVideoUrl())) {
                needGetAudioUrl = true;
            }
        }
        BeanUtils.copyProperties(updateDTO, videoTask);

        if (needGetAudioUrl) {
            // 提取视频中的音频
            getAudioUrl(videoTask);
        }
        baseMapper.updateById(videoTask);
        return videoTask;
    }


    /**
     * 获取视频中音频的 url。
     * 获取视频的 宽高分辨率
     *
     * @param videoTask
     */
    public void getAudioUrl(BusinessDigitalHumanVideoTask videoTask) {
        if (StrUtil.isBlank(videoTask.getTemplateVideoUrl())) {
            return;
        }
        // 输入视频文件路径
        File source = downLoadVideoFile(videoTask.getTemplateVideoUrl());
        String dir = System.getProperty("java.io.tmpdir");
        String path = "/saas/business/card/audio";
        // 输出音频文件路径
        UUID fastUUID = UUID.fastUUID();
        String fileName = fastUUID.toString().replace("-", "");
        File target = new File(dir + path + fileName +".mp3");

        // 创建音频属性
        AudioAttributes audio = new AudioAttributes();
        // 设置输出格式为mp3
        audio.setCodec("libmp3lame");
        // 设置比特率
        audio.setBitRate(128000);
        // 设置声道数
        audio.setChannels(2);
        // 设置采样率
        audio.setSamplingRate(44100);
        ProcessLocator locator = new CustomFFMPEGLocator();
        MultimediaObject multimediaObject = new MultimediaObject(source, locator);

        // 创建编码属性，并设置音频属性
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("mp3");
        attrs.setAudioAttributes(audio);
        // 创建Encoder对象
        try {
            // 执行转换操作，从视频中提取音频
            MultimediaInfo info = multimediaObject.getInfo();
            // 打印视频分辨率

            videoTask.setVideoWidth(info.getVideo().getSize().getWidth());
            videoTask.setVideoHeight(info.getVideo().getSize().getHeight());
        } catch (EncoderException e) {
            e.printStackTrace();
        }

        try {
            Encoder encoder = new Encoder(locator);
            encoder.encode(multimediaObject, target, attrs);
//            MockMultipartFile multipartFile = FileUtils.fileToFileItem(target);
//            R<com.caring.sass.file.entity.File> upload = fileUploadApi.upload(1000L, multipartFile);
//            if (upload.getIsSuccess()) {
//                videoTask.setAudioUrl(upload.getData().getUrl());
//            }
            JSONObject jsonObject = aliYunOssFileUpload.updateFile(fileName, "mp3", target, false);
            videoTask.setAudioUrl(jsonObject.getString("fileUrl"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                source.delete();
                target.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Autowired
    AliYunOssFileUpload aliYunOssFileUpload;


    /**
     * 管理端 直接提交一个制作数字人任务。
     *
     * 支持管理端 配置是否要创建音色
     *
     * @param updateDTO
     * @return
     */
    @Override
    public BusinessDigitalHumanVideoTask adminSubmitStartTask(BusinessDigitalHumanVideoTaskUpdateDTO updateDTO) {

        BusinessDigitalHumanVideoTask videoTask = new BusinessDigitalHumanVideoTask();
        BeanUtils.copyProperties(updateDTO, videoTask);
        videoTask.setTaskSource(HumanTaskSource.WEB_ADMIN);
        if (HumanVideoMakeWay.VIDEO.equals(videoTask.getMakeWay())) {
            // 检查参数是否有未提交的
            if (StrUtil.isEmpty(videoTask.getAudioUrl())) {
                throw new BizException("请上传录音文件");
            }
            if (StrUtil.isEmpty(videoTask.getVideoTextContent())) {
                throw new BizException("请填写视频内容");
            }
            if (StrUtil.isEmpty(videoTask.getTemplateVideoUrl())) {
                throw new BizException("请上传模版视频");
            }
            if (StrUtil.isEmpty(updateDTO.getAudioUrl())) {
                getAudioUrl(videoTask);
            }
        } else if (HumanVideoMakeWay.PHOTO.equals( videoTask.getMakeWay())) {
            if (StrUtil.isEmpty(videoTask.getPhotoHumanUrl())) {
                throw new BizException("请上传数字人形象");
            }
            if (StrUtil.isEmpty(videoTask.getAudioUrl())) {
                throw new BizException("请上传录音文件");
            }
            if (StrUtil.isEmpty(videoTask.getVideoTextContent())) {
                throw new BizException("请填写视频内容");
            }
        } else {
            throw new BizException("请选择制作方式");
        }

        // 是否创建音色任务
        if (updateDTO.getCreateTimbre() != null && updateDTO.getCreateTimbre()) {
            videoTask.setTaskStatus(HumanVideoTaskStatus.NOT_START);
        } else {
            videoTask.setTaskStatus(HumanVideoTaskStatus.WAIT_VIDEO);
        }
        if (videoTask.getId() == null) {
            baseMapper.insert(videoTask);
        } else {
            baseMapper.updateById(videoTask);
        }

        if (videoTask.getTaskStatus().equals(HumanVideoTaskStatus.NOT_START)) {
            // 创建音色制作任务
            String audioUrl = videoTask.getAudioUrl();
            BusinessUserAudioTemplate audioTemplate = new BusinessUserAudioTemplate();
            audioTemplate.setTaskStatus(HumanAudioTaskStatus.WAITING);
            audioTemplate.setVideoTaskId(videoTask.getId());
            audioTemplate.setFileUrl(audioUrl);
            audioTemplateMapper.insert(audioTemplate);
        }
        return videoTask;


    }


    /**
     * 下载模版视频到本地
     */
    public File downLoadVideoFile(String filePath) {

        String dir = System.getProperty("java.io.tmpdir");
        String path = "/saas/business/card/video";
        File folder = new File(dir + path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        UUID fastUUID = UUID.fastUUID();
        String fileName = fastUUID.toString().replace("-", "");
        File file = null;
        try {
            file = FileUtils.downLoadFromFile(filePath, fileName, dir + path);
        } catch (IOException e) {
            log.error("download file error: {}", e.getMessage());
            return null;
        }
        if (Objects.isNull(file)) {
            log.error("download file error: file not exist");
            return null;
        }
        return file;
    }


    /**
     * 最终提交资料。 开始制作任务
     * @param updateDTO
     * @return
     */
    @Override
    public BusinessDigitalHumanVideoTask submitEndStartTask(BusinessDigitalHumanVideoTaskUpdateDTO updateDTO) {

        Long id = updateDTO.getId();
        if (Objects.isNull(id)) {
            throw new BizException("请选择制作方式");
        }
        BusinessDigitalHumanVideoTask videoTask = baseMapper.selectById(id);
        if (Objects.isNull(videoTask)) {
            throw new BizException("请选择制作方式");
        }
        BeanUtils.copyProperties(updateDTO, videoTask);
        baseMapper.updateById(videoTask);
        videoTask = baseMapper.selectById(id);
        videoTask.setTaskStatus(HumanVideoTaskStatus.NOT_START);

        if (HumanVideoMakeWay.VIDEO.equals( videoTask.getMakeWay())) {
            // 检查参数是否有未提交的

            if (StrUtil.isEmpty(videoTask.getAudioUrl())) {
                throw new BizException("请上传录音文件");
            }
            if (StrUtil.isEmpty(videoTask.getVideoTextContent())) {
                throw new BizException("请填写视频内容");
            }
            if (StrUtil.isEmpty(videoTask.getTemplateVideoUrl())) {
                throw new BizException("请上传模版视频");
            }
            if (StrUtil.isEmpty(videoTask.getAuthVideoUrl())) {
                throw new BizException("请上传授权视频");
            }
        } else if (HumanVideoMakeWay.PHOTO.equals( videoTask.getMakeWay())) {
            if (StrUtil.isEmpty(videoTask.getPhotoHumanUrl())) {
                throw new BizException("请上传数字人形象");
            }
            if (StrUtil.isEmpty(videoTask.getAudioUrl())) {
                throw new BizException("请上传录音文件");
            }
            if (StrUtil.isEmpty(videoTask.getVideoTextContent())) {
                throw new BizException("请填写视频内容");
            }
        } else {
            throw new BizException("请选择制作方式");
        }
        if (videoTask.getCreateTimbre() == null) {
            videoTask.setCreateTimbre(true);
        }
        videoTask.setStartTime(LocalDateTime.now());
        baseMapper.updateById(videoTask);

        // 创建音色制作任务
        audioTemplateMapper.delete(Wraps.<BusinessUserAudioTemplate>lbQ().eq(BusinessUserAudioTemplate::getVideoTaskId, videoTask.getId()));
        String audioUrl = videoTask.getAudioUrl();
        BusinessUserAudioTemplate audioTemplate = new BusinessUserAudioTemplate();
        audioTemplate.setTaskStatus(HumanAudioTaskStatus.WAITING);
        audioTemplate.setVideoTaskId(videoTask.getId());
        audioTemplate.setFileUrl(audioUrl);
        audioTemplateMapper.insert(audioTemplate);
        return videoTask;
    }


    /**
     * 更新数字人制作方式
     * @param taskUpdateDTO
     * @return
     */
    @Override
    public BusinessDigitalHumanVideoTask submitDigitalHumanVideoTask(BusinessDigitalHumanVideoTaskUpdateDTO taskUpdateDTO) {

        Long id = taskUpdateDTO.getId();
        if (Objects.isNull(id)) {

            // 用户发起制作数字人。扣除用户的制作额度。
            // 扣除失败，则直接拒绝制作。
            boolean isSuccess = humanLimitService.deductLimit(taskUpdateDTO.getUserId());
            if (!isSuccess) {
                throw new BizException("已经没有制作数字人的次数啦！");
            }
            BusinessDigitalHumanVideoTask businessDigitalHumanVideoTask = new BusinessDigitalHumanVideoTask();
            businessDigitalHumanVideoTask.setBusinessCardId(taskUpdateDTO.getBusinessCardId());
            businessDigitalHumanVideoTask.setUserId(taskUpdateDTO.getUserId());
            businessDigitalHumanVideoTask.setTaskStatus(HumanVideoTaskStatus.SUBMIT_ING);
            businessDigitalHumanVideoTask.setTaskName("问候视频");
            businessDigitalHumanVideoTask.setMakeWay(taskUpdateDTO.getMakeWay());
            businessDigitalHumanVideoTask.setTaskSource(HumanTaskSource.MINI_APP);
            baseMapper.insert(businessDigitalHumanVideoTask);
            return businessDigitalHumanVideoTask;
        } else {
            BusinessDigitalHumanVideoTask videoTask = baseMapper.selectById(id);
            videoTask.setMakeWay(taskUpdateDTO.getMakeWay());
            baseMapper.updateById(videoTask);
            return videoTask;
        }
    }


    @Override
    public void updateFailed(String taskId, String errorMessage) {

        BusinessDigitalHumanVideoTask videoTask = baseMapper.selectOne(Wraps.<BusinessDigitalHumanVideoTask>lbQ()
                .eq(BusinessDigitalHumanVideoTask::getBaiduTaskId, taskId)
                .last(" limit 0,1 "));
        if (Objects.nonNull(videoTask)) {
            videoTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
            videoTask.setTaskMessage(errorMessage);
            baseMapper.updateById(videoTask);
        }
    }

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    HumanVideoDownloadUtil humanVideoDownloadUtil;

    @Override
    public void updateSuccess(String taskId, String videoUrl) {
        BusinessDigitalHumanVideoTask videoTask = baseMapper.selectOne(Wraps.<BusinessDigitalHumanVideoTask>lbQ()
                .eq(BusinessDigitalHumanVideoTask::getBaiduTaskId, taskId)
                .last(" limit 0,1 "));
        if (Objects.nonNull(videoTask)) {
            videoTask.setTaskStatus(HumanVideoTaskStatus.WAIT_DOWNLOAD_VIDEO);
            videoTask.setBaiduVideoUrl(videoUrl);
            // 下载视频。 上传阿里云oss.
//          humanVideoDownloadUtil.downloadVideo(videoUrl, videoTask);
            BaiduVideoDTO videoDTO = new BaiduVideoDTO();
            videoDTO.setBusinessId(videoTask.getId().toString());
            videoDTO.setBusinessClassName(BusinessDigitalHumanVideoTask.class.getSimpleName());
            redisTemplate.opsForList().leftPush("video_download_list_handle", videoDTO.toJSONString());
            baseMapper.updateById(videoTask);

        }



    }

    @Override
    public JSONObject verifyImage(BusinessDigitalHumanVideoTaskSaveDTO updateDTO) {

        return digitalHumanAPI.verifyImage(updateDTO.getPhotoHumanUrl());
    }
}
