package com.caring.sass.ai.controller.article;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.article.service.ArticleUserVoiceTaskService;
import com.caring.sass.ai.dto.article.ArticleUserVoiceTaskPageDTO;
import com.caring.sass.ai.dto.article.ArticleUserVoiceTaskSaveDTO;
import com.caring.sass.ai.dto.article.ArticleUserVoiceTaskUpdateDTO;
import com.caring.sass.ai.entity.article.ArticlePodcastJoin;
import com.caring.sass.ai.entity.article.ArticleUserVoiceTask;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.ai.humanVideo.task.VideoCreationRequest;
import com.caring.sass.ai.service.AliYunOssFileUpload;
import com.caring.sass.ai.utils.ImageDrawUtils;
import com.caring.sass.ai.utils.ImageFrameVideoUtils;
import com.caring.sass.ai.utils.VideoOverlay;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


/**
 * <p>
 * 前端控制器
 * 声音管理
 * </p>
 *
 * @author leizhi
 * @date 2025-02-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/articleUserVoiceTask")
@Api(value = "ArticleUserVoiceTask", tags = "科普创作数字人合成任务")
public class ArticleUserVoiceTaskController extends SuperController<ArticleUserVoiceTaskService, Long, ArticleUserVoiceTask, ArticleUserVoiceTaskPageDTO, ArticleUserVoiceTaskSaveDTO, ArticleUserVoiceTaskUpdateDTO> {


    /**
     * 提交数字人生成任务
     */
    @ApiOperation(value = "提交数字人生成任务")
    @PostMapping("submitVideo")
    public R<Boolean> submitVideoTask(@RequestBody VideoCreationRequest creationRequest) {
        baseService.submitVideoTask(creationRequest);
        return R.success();
    }

    @ApiOperation("发布视频在医生数字分身平台")
    @PutMapping("/showVoiceInAIKnowledge")
    public R<Boolean> showVoiceInAIKnowledge(@RequestBody List<Long> ids) {
        baseService.update(new ArticleUserVoiceTask(), new UpdateWrapper<ArticleUserVoiceTask>()
                .in("id", ids)
                .set("show_ai_knowledge", true));
        return R.success(true);
    }


    @ApiOperation("隐藏视频在医生数字分身平台")
    @PutMapping("/hideVoiceInAIKnowledge/{id}")
    public R<Boolean> hidePodcastInAIKnowledge(@PathVariable("id") Long id) {
        baseService.update(new ArticleUserVoiceTask(), new UpdateWrapper<ArticleUserVoiceTask>()
                .eq("id", id)
                .set("show_ai_knowledge", false));
        return R.success(true);
    }

    @ApiOperation("无权限查看用户数据")
    @PostMapping("anno")
    public R<IPage<ArticleUserVoiceTask>> annoPage(@RequestBody PageParams<ArticleUserVoiceTaskPageDTO> params) {
        return super.page(params);
    }

    @ApiOperation("百度的回调")
    @PostMapping("anno/callback")
    public R<String> callback(@RequestParam(name = "requestId", required = false) String requestId,
                              @RequestParam(name = "type", required = false) String type,
                              @RequestBody String data) {

        // {"type":"VIDEO_GENERATE_IMAGE","data":{"taskId":"img-rdgr3ih33br19m6e",
        // "videoUrl":"https://digital-human-pipeline-output.cdn.bcebos.com/vis/video/v-rdghttn9cykxgavg80.mp4",
        // "failedCode":0,"duration":4888,"subtitleFileUrl":"","status":"SUCCESS"}}
        log.error("baidu callback: requestId: {}, type: {}, data: {}", requestId, type, data);
        if (StrUtil.isNotEmpty(data)) {
            JSONObject jsonObject = JSONObject.parseObject(data);
            String string = jsonObject.getString("data");
            if (StrUtil.isNotEmpty(string)) {
                JSONObject dataResult = JSONObject.parseObject(string);
                String status = dataResult.getString("status");
                if (StrUtil.equals(status, "SUCCESS")) {
                    String taskId = dataResult.getString("taskId");
                    String videoUrl = dataResult.getString("videoUrl");
                    baseService.updateSuccess(taskId, videoUrl);
                } else if (StrUtil.equals(status, "FAILED")) {
                    String taskId = dataResult.getString("taskId");
                    baseService.updateFailed(taskId, string);
                }
            }
        }

        return R.success("SUCCESS");

    }


//    @Autowired
//    AliYunOssFileUpload aliYunOssFileUpload;
//
//    @ApiOperation("测试合成视频")
//    @GetMapping("testMergeVideo")
//    public R<ArticleUserVoiceTask> testMergeVideo(@RequestParam Long taskId) {
//
//        String dir = System.getProperty("java.io.tmpdir");
//        String path = "/saas/article/human/video";
//        File folder = new File(dir + path);
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
//        ArticleUserVoiceTask task = baseService.getById(taskId);
//        String generateAudioUrl = task.getGenerateAudioUrl();
////        AliYunOssFileUpload aliYunOssFileUpload = SpringUtil.getBean(AliYunOssFileUpload.class);
//        generateAudioUrl = generateAudioUrl.replace("oss-cn-beijing.aliyuncs.com", "oss-cn-beijing-internal.aliyuncs.com");
//
//        log.error("testMergeVideo generateAudioUrl start: {}", generateAudioUrl);
//        String loadFromUrl;
//        try {
//            loadFromUrl = aliYunOssFileUpload.downLoadFromUrl(generateAudioUrl, task.getId() + ".mp4", dir + path + File.separator);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        if (StrUtil.isBlank(loadFromUrl)) {
//            return R.success(null);
//        }
//        log.error("testMergeVideo downLoadFromUrl success: {}", loadFromUrl);
//        File file = new File(loadFromUrl);
//        // 进行 封面 封底制作。
//        try {
//            // 制作封面视频
//            String coverVideoPath = dir + path + File.separator + task.getId() + "cover_video.mp4";
//            String coverImage = null;
//            if (task.getHumanVideoCover() != null) {
//                try {
//                    log.error("testMergeVideo createCoverImage start: {}", task.getTaskName());
//                    coverImage = ImageDrawUtils.createCoverImage(task.getHumanVideoCover(), task.getTaskName());
//                } catch (IOException e) {
//                    log.error("Error creating cover image: {}", e.getMessage(), e);
//                }
//                log.error("testMergeVideo createCoverImage result: {}", coverImage);
//                if (coverImage != null) {
//                    try {
//
//                        log.error("testMergeVideo createShortFrameVideo start: {}", coverImage);
//                        coverVideoPath = ImageFrameVideoUtils.createShortFrameVideo(coverImage, coverVideoPath);
//                        log.error("testMergeVideo createShortFrameVideo result: {}", coverVideoPath);
//                    } catch (Exception e) {
//                        log.error("Error creating cover video: {}", e.getMessage(), e);
//                    }
//                    // 把视频上传阿里云
//                    JSONObject jsonObject = aliYunOssFileUpload.updateFile(UUID.randomUUID().toString(), "mp4", new File(coverVideoPath), false);
//                    task.setCoverVideo(jsonObject.getString("fileUrl"));
//                }
//            }
//            int width;
//            int height;
//            // 获取当前视频的宽高
//            if (StrUtil.isNotBlank(coverImage)) {
//                BufferedImage read = ImageIO.read(new File(coverImage));
//                width = read.getWidth();
//                height = read.getHeight();
//            } else {
//                throw new BizException("封面不存在");
//            }
//
//
//            log.error("testMergeVideo createBackVideo start: {}", task.getTaskName());
//            String backVideo = VideoOverlay.createBackVideo(width, height, task.getId());
//
//            log.error("testMergeVideo createBackVideo result: {}", backVideo);
//            if (backVideo != null && StrUtil.isNotBlank(backVideo)) {
//                JSONObject jsonObject = aliYunOssFileUpload.updateFile(UUID.randomUUID().toString(), "mp4", new File(backVideo), false);
//                task.setBackCoverVideo(jsonObject.getString("fileUrl"));
//            }
//
//            // 视频合成。将封面。原视频。封底进行合成。
//            String mergedVideo = VideoOverlay.mergeVideo(coverVideoPath, file.getAbsolutePath(), backVideo, task.getId());
//            if (StrUtil.isNotBlank(mergedVideo)) {
//                JSONObject jsonObject = aliYunOssFileUpload.updateFile(UUID.randomUUID().toString(), "mp4", new File(mergedVideo), true);
//                Object fileUrl = jsonObject.get("fileUrl");
//                Object presignedUrl = jsonObject.get("presignedUrl");
//                task.setFinalVideoResult(fileUrl.toString());
//                if (Objects.nonNull(presignedUrl)) {
//                    task.setHumanVideoCover(presignedUrl.toString());
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            task.setTaskStatus(HumanVideoTaskStatus.SUCCESS);
//            if (file != null && file.exists()) {
//                file.delete();
//            }
//        }
//        baseService.updateById(task);
//        return R.success(task);
//    }


}
