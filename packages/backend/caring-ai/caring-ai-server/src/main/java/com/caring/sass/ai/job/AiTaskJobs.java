package com.caring.sass.ai.job;


import com.caring.sass.ai.article.service.ArticleDailyUsageDataService;
import com.caring.sass.ai.article.service.ArticleUserVideoTemplateService;
import com.caring.sass.ai.article.service.ArticleUserVoiceService;
import com.caring.sass.ai.article.service.ArticleVolcengineVoiceTaskService;
import com.caring.sass.ai.article.task.ArticleHumanAudioThread;
import com.caring.sass.ai.article.task.ArticleVideoTemplateWaterTask;
import com.caring.sass.ai.article.task.TaskFailRefundServer;
import com.caring.sass.ai.call.service.CallRecordService;
import com.caring.sass.ai.card.task.CardEveryDayInitStatisticsData;
import com.caring.sass.ai.humanVideo.task.BaiduVideoDownloadHandle;
import com.caring.sass.ai.humanVideo.task.VoiceTaskComponent;
import com.caring.sass.ai.know.task.DifyFileIndexStatusService;
import com.caring.sass.ai.podcast.service.PodcastAudioTaskService;
import com.caring.sass.ai.podcast.task.PodcastAudioThread;
import com.caring.sass.ai.service.ArticleVideoMergeCoverService;
import com.caring.sass.ai.textual.PptCreateTask;
import com.caring.sass.ai.textual.service.TextualInterpretationUserVoiceService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class AiTaskJobs {



    @Autowired
    VoiceTaskComponent voiceTaskComponent;


    @Autowired
    BaiduVideoDownloadHandle baiduVideoDownloadHandle;

    @Autowired
    ArticleHumanAudioThread articleHumanAudioThread;

    @Autowired
    DifyFileIndexStatusService difyFileIndexStatusService;

    @Autowired
    CardEveryDayInitStatisticsData cardEveryDayInitStatisticsData;

    @Autowired
    ArticleUserVoiceService articleUserVoiceService;

    @Autowired
    TextualInterpretationUserVoiceService texualInterpretationUserVoiceService;

    @Autowired
    PptCreateTask pptCreateTask;

    @Autowired
    PodcastAudioThread podcastAudioThread;

    @Autowired
    ArticleDailyUsageDataService dailyUsageDataService;

    @Autowired
    ArticleUserVideoTemplateService articleUserVideoTemplateService;

    @Autowired
    TaskFailRefundServer taskFailRefundServer;

    @Autowired
    PodcastAudioTaskService podcastAudioTaskService;

    /**
     * 播客音频合并任务。
     * 30秒执行一次
     */
    @XxlJob("PODCAST_AUDIO_MERGE_TASK")
    public ReturnT<String> podcastAudioMergeTask(String param) {
        log.error("PODCAST_AUDIO_MERGE_TASK start");
        podcastAudioTaskService.startMergeAudioTask();
        return ReturnT.SUCCESS;
    }



    /**
     * 科普创作任务失败退费
     */
    @XxlJob("TASK_FAIL_REFUND_SERVER")
    public ReturnT<String> TaskFailRefundServer(String param) {
        log.error("TASK_FAIL_REFUND_SERVER start");
        taskFailRefundServer.refund();
        return ReturnT.SUCCESS;
    }


    /**
     * 定时从视频中提取声音
     * @param param
     * @return
     */
    @XxlJob("OBTAIN_AUDIO_FROM_THE_VIDEO")
    public ReturnT<String> obtainAudioFromTheVideo(String param) {
        log.error("AI_HUMAN_AUDIO_TASK start");
        articleUserVideoTemplateService.xxjobObtainAudioFromTheVideo();
        return ReturnT.SUCCESS;
    }


    /**
     * 30秒检查一下等待提交给海螺克隆的声音
     */
    @XxlJob("VOICE_CLONE_TASK_STATUS")
    public ReturnT<String> VOICE_CLONE_TASK_STATUS(String param) {
        log.error("VOICE_CLONE_TASK_STATUS start");
        articleUserVoiceService.checkCloningVoiceTaskStatus();
        return ReturnT.SUCCESS;
    }



    /**
     * 30秒刷新一下ppt数据库
     */
    @XxlJob("PPT_QUERY_TASK_STATUS")
    public ReturnT<String> PPT_QUERY_TASK_STATUS(String param) {
        log.error("PPT_QUERY_TASK_STATUS start");
        pptCreateTask.queryTaskStatus();
        return ReturnT.SUCCESS;
    }

    /**
     * 30秒执行一次。
     * 下载ppt文件
     * @param param
     * @return
     */
    @XxlJob("PPT_DOWNLOAD_PPT_FILE")
    public ReturnT<String> PPT_DOWNLOAD_PPT_FILE(String param) {
        log.error("PPT_DOWNLOAD_PPT_FILE start");
        pptCreateTask.downloadPptFile();
        return ReturnT.SUCCESS;
    }

    /**
     * 每天凌晨。处理昨日的数据
     */
    @XxlJob("DAILY_USAGE_DATA_HANDLE")
    public ReturnT<String> DAILY_USAGE_DATA_HANDLE(String param) {
        log.error("DAILY_USAGE_DATA_HANDLE start");
        LocalDateTime now = LocalDateTime.now();
        try {
            dailyUsageDataService.handleYesterdayData(now);
        } catch (Exception e) {
            log.error("DAILY_USAGE_DATA_HANDLE error", e);
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 每5天同步一次可用声音库
     * m_ai_article_user_voice
     * 如您希望永久保留某复刻音色，
     * 请于168小时（7天）内在任意T2A语音合成接口中调用该音色（不包含本接口内的试听行为）；否则，该音色将被删除。
     */
    @XxlJob("SyncCloningVoice")
    public ReturnT<String> SyncCloningVoice(String param) {
        log.error("SyncCloningVoice start");
        articleUserVoiceService.syncCloningVoiceId();
        texualInterpretationUserVoiceService.syncCloningVoiceId();
        return ReturnT.SUCCESS;
    }


    /**
     * 每日凌晨生产日统计数据
     */
    @XxlJob("CardEveryDayInitStatisticsData")
    public ReturnT<String> CardEveryDayInitStatisticsData(String param) {
        log.error("CardEveryDayInitStatisticsData start");
        cardEveryDayInitStatisticsData.initData();
        return ReturnT.SUCCESS;
    }

    /**
     * 定时任务。每10秒启动一次。
     *
     * 查询数据库中的日常收集。处于等待状态的资料。
     * 遍历，查询dify文档的索引进度。
     * 如果索引进度为100%，则更新状态为已完成。
     *
     */
    @XxlJob("DIFY_FILE_INDEX_STATUS")
    public ReturnT<String> serverStartInitTaskList(String param) {
        log.error("DIFY_FILE_INDEX_STATUS start");
        difyFileIndexStatusService.serverStartInitTaskList();
        return ReturnT.SUCCESS;
    }

    /**
     * 科普创作 数字人任务。
     *
     * 每分钟一次
     */
    @XxlJob("ARTICLE_HUMAN_AUDIO_THREAD")
    public ReturnT<String> articleHumanAudioThread(String param) {
        log.error("ARTICLE_HUMAN_AUDIO_THREAD start");
        articleHumanAudioThread.run();
        return ReturnT.SUCCESS;
    }

    /**
     * 科普创作 数字人任务 超时切换备用方案。
     *
     * 每分钟一次
     */
    @XxlJob("ARTICLE_HUMAN_AUDIO_CHECK_TIME_OUT")
    public ReturnT<String> articleHumanAudioCheckTimeOut(String param) {
        log.error("ARTICLE_HUMAN_AUDIO_CHECK_TIME_OUT start");
        articleHumanAudioThread.checkTimeOut();
        return ReturnT.SUCCESS;
    }


    /**
     * 下载百度视频。
     *
     * 每分钟一次
     */
    @XxlJob("VIDEO_DOWNLOAD_LIST_HANDLE")
    public ReturnT<String> videoDownloadListHandle(String param) {
        log.error("VIDEO_DOWNLOAD_LIST_HANDLE start");
        baiduVideoDownloadHandle.downloadVideoTask();
        return ReturnT.SUCCESS;
    }

    /**
     * 训练音色任务
     *
     * 5分钟一次
     */
    @XxlJob("AI_HUMAN_TRAINING_TIMBRE")
    public ReturnT<String> trainingTimbre(String param) {
        log.error("AI_HUMAN_TRAINING_TIMBRE start");
        voiceTaskComponent.trainingTimbre();
        return ReturnT.SUCCESS;
    }


    /**
     * 查询音色训练状态
     *
     * 5分钟一次
     */
    @XxlJob("AI_HUMAN_TRAINING_TIMBRE_STATUS")
    public ReturnT<String> trainingTimbreStatus(String param) {
        log.error("AI_HUMAN_TRAINING_TIMBRE_STATUS start");
        voiceTaskComponent.trainingTimbreStatus();
        return ReturnT.SUCCESS;
    }


    /**
     * 发起语音合成
     *
     * 5分钟一次
     */
    @XxlJob("AI_HUMAN_AUDIO_SYNTHESIS_TASK")
    public ReturnT<String> audioSynthesisTask(String param) {
        log.error("AI_HUMAN_AUDIO_SYNTHESIS_TASK start");
        voiceTaskComponent.audioSynthesisTask();
        return ReturnT.SUCCESS;
    }


    /**
     * 提交百度数字人
     *
     * 5分钟一次
     */
    @XxlJob("AI_HUMAN_SUBMIT_VIDEO_TASK")
    public ReturnT<String> submitVideoTask(String param) {
        log.error("AI_HUMAN_SUBMIT_VIDEO_TASK start");
        voiceTaskComponent.submitVideoTask();
        return ReturnT.SUCCESS;
    }



    /**
     * 科普名片数字人 查询数字人制作状态
     *
     * 5分钟一次
     */
    @XxlJob("AI_HUMAN_QUERY_VIDEO_STATUS")
    public ReturnT<String> queryCreateVideoStatus(String param) {
        log.error("AI_HUMAN_QUERY_VIDEO_STATUS start");
        voiceTaskComponent.queryCreateVideoStatus();
        return ReturnT.SUCCESS;

    }

    @Autowired
    ArticleVolcengineVoiceTaskService articleVolcengineVoiceTaskService;

    /**
     * 制作火山形象任务
     * @param param
     * @return
     */
    @XxlJob("VOLCENGINE_CREATE_IMAGE_TASK")
    public ReturnT<String> handleCreateImageTask(String param) {
        articleVolcengineVoiceTaskService.handleCreateImageTask();
        return ReturnT.SUCCESS;
    }

    /**
     * 查询火山形象任务
     * @param param
     * @return
     */
    @XxlJob("VOLCENGINE_MAKE_IMAGE_TASK")
    public ReturnT<String> handleMakeImageTask(String param) {
        articleVolcengineVoiceTaskService.handleMakeImageTask();
        return ReturnT.SUCCESS;
    }

    /**
     * 将等待火山做的视频任务提交
     * @param param
     * @return
     */
    @XxlJob("VOLCENGINE_WAIT_MAKE_VIDEO_TASK")
    public ReturnT<String> handleWaitMakeVideoTask(String param) {
        articleVolcengineVoiceTaskService.handleWaitMakeVideoTask();
        return ReturnT.SUCCESS;
    }


    /**
     * 查询火山视频任务制作状态
     * @param param
     * @return
     */
    @XxlJob("VOLCENGINE_MAKING_VIDEO_TASK")
    public ReturnT<String> handleMakingVideoTask(String param) {
        articleVolcengineVoiceTaskService.handleMakingVideoTask();
        return ReturnT.SUCCESS;
    }

    @Autowired
    ArticleVideoMergeCoverService articleVideoMergeCoverService;

    /**
     * 科普创作视频封面合成。
     */
    @XxlJob("ARTICLE_VIDEO_MERGE_COVER_TASK")
    public ReturnT<String> articleVideoMergeCoverTask(String param) {
        log.error("articleVideoMergeCoverTask start");
        articleVideoMergeCoverService.syncArticleVideoMergeCoverTask();
        return ReturnT.SUCCESS;
    }


    /**
     * 播客声音合成。
     */
    @XxlJob("PODCAST_AUDIO_TASK")
    public ReturnT<String> podcastAudioTask(String param) {
        log.error("podcastAudioTask start");
        podcastAudioThread.run();
        return ReturnT.SUCCESS;
    }

    @Autowired
    CallRecordService callRecordService;

    /**
     * 结束超时的通话
     * @param param
     * @return
     */
    @XxlJob("KNOWLEDGE_END_CALL")
    public ReturnT<String> endCall(String param) {
        log.error("endCall start");
        callRecordService.endTimeOutCall();
        return ReturnT.SUCCESS;
    }


    @Autowired
    ArticleVideoTemplateWaterTask articleVideoTemplateWaterTask;

    /**
     * 提交转码任务，带水印
     * @return
     */
    @XxlJob("ARTICLE_VIDEO_SUBMIT_WATER_MARK_TASK")
    public ReturnT<String> submitWaterMarkTask(String param) {

        articleVideoTemplateWaterTask.submitWaterMarkTask();
        return ReturnT.SUCCESS;
    }

    /**
     * 查询转码任务状态
     * @return
     */
    @XxlJob("ARTICLE_VIDEO_QUERY_WATER_MARK_TASK")
    public ReturnT<String> queryWaterMarkTask(String param) {

        articleVideoTemplateWaterTask.queryWaterMarkTask();
        return ReturnT.SUCCESS;
    }




}
