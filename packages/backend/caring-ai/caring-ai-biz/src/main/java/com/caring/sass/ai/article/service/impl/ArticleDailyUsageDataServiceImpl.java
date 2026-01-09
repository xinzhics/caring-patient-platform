package com.caring.sass.ai.article.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.ai.article.dao.ArticleDailyUsageDataMapper;
import com.caring.sass.ai.article.dao.ArticleEventLogMapper;
import com.caring.sass.ai.article.service.ArticleDailyUsageDataService;
import com.caring.sass.ai.article.service.ArticleTaskService;
import com.caring.sass.ai.article.service.ArticleUserService;
import com.caring.sass.ai.article.service.ArticleUserVoiceTaskService;
import com.caring.sass.ai.dto.article.EventCountDTO;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.ai.entity.podcast.Podcast;
import com.caring.sass.ai.entity.podcast.PodcastTaskStatus;
import com.caring.sass.ai.podcast.service.PodcastService;
import com.caring.sass.ai.service.AliYunOssFileUpload;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.SassDateUtis;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * AI创作日使用数据累计
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-02
 */
@Slf4j
@Service

public class ArticleDailyUsageDataServiceImpl extends SuperServiceImpl<ArticleDailyUsageDataMapper, ArticleDailyUsageData> implements ArticleDailyUsageDataService {


    @Autowired
    ArticleUserService articleUserService;


    @Autowired
    ArticleTaskService articleTaskService;

    @Autowired
    PodcastService podcastService;

    @Autowired
    ArticleUserVoiceTaskService articleUserVoiceTaskService;

    @Autowired
    AliYunOssFileUpload aliYunOssFileUpload;

    @Autowired
    ArticleEventLogMapper articleEventLogMapper;

    public ArticleDailyUsageDataServiceImpl() {
    }

    /**
     * 昨日数据汇总
     *
     * 定时任务执行
     * 凌晨 1点开始执行。 处理昨日生成的所有数据
     *
     */
    @Override
    public void handleYesterdayData(LocalDateTime now) {

        ArticleDailyUsageData usageData = new ArticleDailyUsageData();
        LocalDate yesterday = now.toLocalDate().plusDays(-1);
        usageData.setCreateDay(yesterday.toString());
        usageData.setCreateMonth(yesterday.toString().substring(0, 7));
        usageData.setWeekNumber(SassDateUtis.getWeekNumberFromBaseDate(yesterday));
        usageData.setRegisterNumber(0L);
        usageData.setActiveNumber(0L);
        usageData.setProduceFirstCompleteUserNumber(0L);
        usageData.setProduceCompleteUserNumber(0L);
        usageData.setProduceStartUserNumber(0L);
        usageData.setProduceCompleteTotalNumber(0L);
        usageData.setProduceErrorTotalNumber(0L);
        usageData.setProduceUnfinishedTotalNumber(0L);
        usageData.setArticleCompleteNumber(0L);
        usageData.setPodcastCompleteNumber(0L);
        usageData.setVideoCompleteNumber(0L);
        usageData.setDay7Active(0);
        usageData.setDay30Active(0);

        usageData.setArticleOriginalScienceNumber(0L);
        usageData.setArticleOriginalSocialNumber(0L);
        usageData.setArticleOriginalVideoNumber(0L);
        usageData.setArticleRewriteScienceNumber(0L);
        usageData.setArticleRewriteSocialNumber(0L);
        usageData.setArticleRewriteVideoNumber(0L);

        baseMapper.insert(usageData);

        LocalDateTime createTime = usageData.getCreateTime();
        createTime = createTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = createTime.plusDays(1);


        // 昨日 注册用户数量统计
        yesterdayRegisterNumberCount(usageData, createTime, endTime);

        // 昨日活跃用户数量统计
        yesterdayActiveNumberCount(usageData, createTime, endTime);

        // 昨日注册用户完成首次创作用户数量
        firstCompleteUserNumber(usageData, createTime, endTime);

        // 完成创作的用户数量
        // 由于活跃的要求是 完成创作。所以 活跃用户数 等于 完成创作的用户数量
        usageData.setProduceCompleteUserNumber(usageData.getActiveNumber());

        // 开始创作的用户数量
        startProduceUserNumber(usageData, createTime, endTime);

        // 累计创作数量
        produceCompleteTotalNumber(usageData, createTime, endTime);

        // 累计失败的数量
        produceErrorTotalNumber(usageData, createTime, endTime);

        // 累计未完成的数量
        produceUnfinishedTotalNumber(usageData, createTime, endTime);

        // 文章完成创作的数量
        int count = articleTaskService.count(Wrappers.<ArticleTask>lambdaQuery()
                .in(ArticleTask::getTaskStatus, 1, 2)
                .between(SuperEntity::getCreateTime, createTime, endTime));
        usageData.setArticleCompleteNumber((long) count);

        // 完成的播客数量
        int counted = podcastService.count(Wraps.<Podcast>lbQ()
                .notIn(Podcast::getTaskStatus, PodcastTaskStatus.TASK_FINISH)
                .between(SuperEntity::getCreateTime, createTime, endTime));
        usageData.setPodcastCompleteNumber((long) counted);

        int number = articleUserVoiceTaskService.count(Wraps.<ArticleUserVoiceTask>lbQ()
                .notIn(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.SUCCESS)
                .between(SuperEntity::getCreateTime, createTime, endTime));
        usageData.setVideoCompleteNumber((long) number);

        // 原创科普文章创作
        count = articleTaskService.count(Wrappers.<ArticleTask>lambdaQuery()
                .eq(ArticleTask::getTaskType, 1)
                .eq(ArticleTask::getCreativeApproach, 1)
                .between(SuperEntity::getCreateTime, createTime, endTime));
        usageData.setArticleOriginalScienceNumber((long) count);

        // 仿写科普文章创作
        count = articleTaskService.count(Wrappers.<ArticleTask>lambdaQuery()
                .eq(ArticleTask::getTaskType, 1)
                .eq(ArticleTask::getCreativeApproach, 2)
                .between(SuperEntity::getCreateTime, createTime, endTime));
        usageData.setArticleRewriteScienceNumber((long) count);

        // 原创社交媒体文案
        count = articleTaskService.count(Wrappers.<ArticleTask>lambdaQuery()
                .eq(ArticleTask::getTaskType, 2)
                .eq(ArticleTask::getCreativeApproach, 1)
                .between(SuperEntity::getCreateTime, createTime, endTime));
        usageData.setArticleOriginalSocialNumber((long) count);
        // 仿写社交媒体文案
        count = articleTaskService.count(Wrappers.<ArticleTask>lambdaQuery()
                .eq(ArticleTask::getTaskType, 2)
                .eq(ArticleTask::getCreativeApproach, 2)
                .between(SuperEntity::getCreateTime, createTime, endTime));
        usageData.setArticleRewriteSocialNumber((long) count);
        // 原创短视频口播

        count = articleTaskService.count(Wrappers.<ArticleTask>lambdaQuery()
                .eq(ArticleTask::getTaskType, 3)
                .eq(ArticleTask::getCreativeApproach, 1)
                .between(SuperEntity::getCreateTime, createTime, endTime));
        usageData.setArticleOriginalVideoNumber((long) count);
        // 仿写短视频口播
        count = articleTaskService.count(Wrappers.<ArticleTask>lambdaQuery()
                .eq(ArticleTask::getTaskType, 3)
                .eq(ArticleTask::getCreativeApproach, 2)
                .between(SuperEntity::getCreateTime, createTime, endTime));
        usageData.setArticleRewriteVideoNumber((long) count);

        // 数字人创作数量
        count = articleUserVoiceTaskService.count(Wraps.<ArticleUserVoiceTask>lbQ()
                .between(SuperEntity::getCreateTime, createTime, endTime));
        usageData.setArticleOriginalDigitalPersonNumber((long) count);

        // 播客数量
        count = podcastService.count(Wraps.<Podcast>lbQ()
                .eq(Podcast::getPodcastType, "article")
                .between(SuperEntity::getCreateTime, createTime, endTime));
        usageData.setPodcastNumber((long) count);

        // 7日留存率 (检查8天前注册的用户， 昨天是否有完成创作的)
        dayActive(8, usageData, now);
        // 30日留存率（检查31天前注册的用户，昨天是否有完成创作）
        dayActive(31, usageData, now);

        baseMapper.updateById(usageData);

    }



    private void dayActive( int day, ArticleDailyUsageData usageData, LocalDateTime now) {
        LocalDateTime createTime = now.toLocalDate().plusDays(-day).atStartOfDay();
        LocalDateTime endTime = createTime.plusDays(1);
        List<ArticleUser> articleUsers = articleUserService.list(Wrappers.<ArticleUser>lambdaQuery()
                .between(ArticleUser::getCreateTime, createTime, endTime));

        // 查询昨天 他们是否 来系统进行完成了创作
        if (articleUsers.isEmpty()) {
            return;
        }

        List<Long> ids = articleUsers.stream().map(SuperEntity::getId).collect(Collectors.toList());
        List<ArticleTask> list = articleTaskService.list(Wraps.<ArticleTask>lbQ()
                .in(SuperEntity::getCreateUser, ids)
                .gt(ArticleTask::getTaskStatus, 0)
                .between(SuperEntity::getCreateTime, createTime, endTime));

        Set<Long> completeUserIds = list.stream().map(SuperEntity::getCreateUser).collect(Collectors.toSet());

        ids.removeAll(completeUserIds);

        if (!ids.isEmpty()) {
            List<Podcast> podcasts = podcastService.list(Wraps.<Podcast>lbQ()
                    .in(Podcast::getUserId, ids)
                    .gt(Podcast::getTaskStatus, PodcastTaskStatus.TASK_FINISH)
                    .between(SuperEntity::getCreateTime, createTime, endTime));
            if (!podcasts.isEmpty()) {
                completeUserIds.addAll(podcasts.stream().map(Podcast::getUserId).collect(Collectors.toSet()));
                ids.removeAll(completeUserIds);
            }
        }

        if (!ids.isEmpty()) {
            List<ArticleUserVoiceTask> voiceTasks = articleUserVoiceTaskService.list(Wraps.<ArticleUserVoiceTask>lbQ()
                    .in(ArticleUserVoiceTask::getUserId, ids)
                    .gt(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.SUCCESS)
                    .between(SuperEntity::getCreateTime, createTime, endTime));
            if (!voiceTasks.isEmpty()) {
                completeUserIds.addAll(voiceTasks.stream().map(ArticleUserVoiceTask::getUserId).collect(Collectors.toSet()));
                ids.removeAll(completeUserIds);
            }
        }

        if (completeUserIds.isEmpty()) {
            return;
        }

        if (day == 8) {
            UpdateWrapper<ArticleUser> updateWrapper = new UpdateWrapper<ArticleUser>();
            updateWrapper.in("id", completeUserIds);
            updateWrapper.set("day_7_active", true);
            articleUserService.update(updateWrapper);

            usageData.setDay7Active(completeUserIds.size() / articleUsers.size() * 100);

        } else {
            UpdateWrapper<ArticleUser> updateWrapper = new UpdateWrapper<ArticleUser>();
            updateWrapper.in("id", completeUserIds);
            updateWrapper.set("day_30_active", true);
            articleUserService.update(updateWrapper);

            usageData.setDay30Active(completeUserIds.size() / articleUsers.size() * 100);
        }





    }


    /**
     * 统计昨天新创建多少用户
     * @param usageData
     */
    private void yesterdayRegisterNumberCount(ArticleDailyUsageData usageData, LocalDateTime createTime, LocalDateTime endTime) {

        int count = articleUserService.count(Wrappers.<ArticleUser>lambdaQuery()
                .between(ArticleUser::getCreateTime, createTime, endTime));
        usageData.setRegisterNumber((long) count);
        baseMapper.updateById(usageData);
    }



    /**
     * 没有完成的任务
     * @param usageData
     * @param createTime
     * @param endTime
     */
    private void produceUnfinishedTotalNumber(ArticleDailyUsageData usageData, LocalDateTime createTime, LocalDateTime endTime) {


//        int count = articleTaskService.count(Wrappers.<ArticleTask>lambdaQuery()
//                .lt(ArticleTask::getStep, 5)
//                .between(SuperEntity::getCreateTime, createTime, endTime));
//
//
//        int counted = podcastService.count(Wraps.<Podcast>lbQ()
//                .notIn(Podcast::getTaskStatus, PodcastTaskStatus.CREATE_ERROR, PodcastTaskStatus.CREATE_DIALOGUE_ERROR, PodcastTaskStatus.TASK_FINISH)
//                .between(SuperEntity::getCreateTime, createTime, endTime));
//
//        int number = articleUserVoiceTaskService.count(Wraps.<ArticleUserVoiceTask>lbQ()
//                .notIn(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.FAIL, HumanVideoTaskStatus.SUCCESS)
//                .between(SuperEntity::getCreateTime, createTime, endTime));

        int i = baseMapper.produceUnfinishedTotalNumber(createTime, endTime);
        usageData.setProduceUnfinishedTotalNumber( (long) i);
    }

    /**
     * 出现异常的创作数量
     * @param usageData
     * @param createTime
     * @param endTime
     */
    private void produceErrorTotalNumber(ArticleDailyUsageData usageData, LocalDateTime createTime, LocalDateTime endTime) {



        int counted = podcastService.count(Wraps.<Podcast>lbQ()
                .in(Podcast::getTaskStatus, PodcastTaskStatus.CREATE_ERROR, PodcastTaskStatus.CREATE_DIALOGUE_ERROR)
                .between(SuperEntity::getCreateTime, createTime, endTime));

        int number = articleUserVoiceTaskService.count(Wraps.<ArticleUserVoiceTask>lbQ()
                        .eq(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.FAIL)
                .between(SuperEntity::getCreateTime, createTime, endTime));

        int i = counted + number;
        usageData.setProduceErrorTotalNumber( (long) i);

    }

    /**
     * 累计多少创作
     * @param usageData
     * @param createTime
     * @param endTime
     */
    private void produceCompleteTotalNumber(ArticleDailyUsageData usageData, LocalDateTime createTime, LocalDateTime endTime) {
//
//        int count = articleTaskService.count(Wrappers.<ArticleTask>lambdaQuery()
//                .between(SuperEntity::getCreateTime, createTime, endTime));
//
//        int counted = podcastService.count(Wraps.<Podcast>lbQ()
//                .between(SuperEntity::getCreateTime, createTime, endTime));
//
//        int number = articleUserVoiceTaskService.count(Wraps.<ArticleUserVoiceTask>lbQ()
//                .between(SuperEntity::getCreateTime, createTime, endTime));


        int i = baseMapper.countAllProduce(createTime, endTime);
        usageData.setProduceCompleteTotalNumber((long) i);

    }

    /**
     * 计算 有多少人 昨天开启了创作 无论创作是否完成
     * @param usageData
     * @param createTime
     * @param endTime
     */
    private void startProduceUserNumber(ArticleDailyUsageData usageData, LocalDateTime createTime, LocalDateTime endTime) {

//        List<ArticleTask> taskList = articleTaskService.list(Wrappers.<ArticleTask>lambdaQuery()
//                .select(SuperEntity::getId, SuperEntity::getCreateUser)
//                .groupBy(SuperEntity::getCreateUser)
//                .between(SuperEntity::getCreateTime, createTime, endTime));
//        Set<Long> completeUserIds = taskList.stream().map(SuperEntity::getCreateUser).collect(Collectors.toSet());
//
//        List<Podcast> podcasts = podcastService.list(Wraps.<Podcast>lbQ()
//                .select(SuperEntity::getId, Podcast::getUserId)
//                .groupBy(Podcast::getUserId)
//                .between(SuperEntity::getCreateTime, createTime, endTime));
//        if (!podcasts.isEmpty()) {
//            completeUserIds.addAll(podcasts.stream().map(Podcast::getUserId).collect(Collectors.toSet()));
//        }
//
//        List<ArticleUserVoiceTask> voiceTasks = articleUserVoiceTaskService.list(Wraps.<ArticleUserVoiceTask>lbQ()
//                .between(SuperEntity::getCreateTime, createTime, endTime));
//        if (!voiceTasks.isEmpty()) {
//            completeUserIds.addAll(voiceTasks.stream().map(ArticleUserVoiceTask::getUserId).collect(Collectors.toSet()));
//        }

        int i = baseMapper.countAllProduceUserNumber(createTime, endTime);

        usageData.setProduceStartUserNumber((long) i);

    }

    /**
     * 昨天活跃用户统计
     * @param usageData
     * @param createTime
     * @param endTime
     */
    private void yesterdayActiveNumberCount(ArticleDailyUsageData usageData, LocalDateTime createTime, LocalDateTime endTime) {

//        List<ArticleTask> list = articleTaskService.list(Wraps.<ArticleTask>lbQ()
//                .gt(ArticleTask::getTaskStatus, 0)
//                .between(SuperEntity::getCreateTime, createTime, endTime));
//
//        Set<Long> completeUserIds = list.stream().map(SuperEntity::getCreateUser).collect(Collectors.toSet());
//
//        List<Podcast> podcasts = podcastService.list(Wraps.<Podcast>lbQ()
//                .gt(Podcast::getTaskStatus, PodcastTaskStatus.TASK_FINISH)
//                .between(SuperEntity::getCreateTime, createTime, endTime));
//        completeUserIds.addAll(podcasts.stream().map(Podcast::getUserId).collect(Collectors.toSet()));
//
//        List<ArticleUserVoiceTask> voiceTasks = articleUserVoiceTaskService.list(Wraps.<ArticleUserVoiceTask>lbQ()
//                .gt(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.SUCCESS)
//                .between(SuperEntity::getCreateTime, createTime, endTime));
//        completeUserIds.addAll(voiceTasks.stream().map(ArticleUserVoiceTask::getUserId).collect(Collectors.toSet()));

        int i = baseMapper.countActiveUsers(createTime, endTime);
        usageData.setActiveNumber((long) i);
    }

    /**
     * 昨天完成首次创作用户数量
     * @param usageData
     * @param createTime
     * @param endTime
     */
    private void firstCompleteUserNumber(ArticleDailyUsageData usageData, LocalDateTime createTime, LocalDateTime endTime) {
        // 查询昨天注册的用户
        List<ArticleUser> articleUsers = articleUserService.list(Wrappers.<ArticleUser>lambdaQuery()
                .between(ArticleUser::getCreateTime, createTime, endTime));

        if (articleUsers.isEmpty()) {
            usageData.setProduceFirstCompleteUserNumber(0L);
        }
        List<Long> ids = articleUsers.stream().map(SuperEntity::getId).collect(Collectors.toList());
        List<ArticleTask> list = articleTaskService.list(Wraps.<ArticleTask>lbQ()
                .in(SuperEntity::getCreateUser, ids)
                .gt(ArticleTask::getTaskStatus, 0)
                .between(SuperEntity::getCreateTime, createTime, endTime));

        Set<Long> completeUserIds = list.stream().map(SuperEntity::getCreateUser).collect(Collectors.toSet());
        ids.removeAll(completeUserIds);

        if (!ids.isEmpty()) {
            List<Podcast> podcasts = podcastService.list(Wraps.<Podcast>lbQ()
                    .in(Podcast::getUserId, ids)
                    .gt(Podcast::getTaskStatus, PodcastTaskStatus.TASK_FINISH)
                    .between(SuperEntity::getCreateTime, createTime, endTime));
            completeUserIds = podcasts.stream().map(Podcast::getUserId).collect(Collectors.toSet());
            ids.removeAll(completeUserIds);
        }

        if (!ids.isEmpty()) {
            List<ArticleUserVoiceTask> voiceTasks = articleUserVoiceTaskService.list(Wraps.<ArticleUserVoiceTask>lbQ()
                    .in(ArticleUserVoiceTask::getUserId, ids)
                    .gt(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.SUCCESS)
                    .between(SuperEntity::getCreateTime, createTime, endTime));
            completeUserIds = voiceTasks.stream().map(ArticleUserVoiceTask::getUserId).collect(Collectors.toSet());
            ids.removeAll(completeUserIds);

        }
        int i = articleUsers.size() - ids.size();
        usageData.setProduceFirstCompleteUserNumber((long) i);


    }




//    private static final String templatePath = "https://caring-saas-video.oss-cn-beijing.aliyuncs.com/article/article_use_data_template.xlsx";
    private static final String templatePath = "https://caring-saas-video.oss-cn-beijing-internal.aliyuncs.com/article/article_use_data_template.xlsx";
    private static final String REGISTER_NUMBER = "register_number";
    private static final String ACTIVE_NUMBER = "active_number";

    // 导出逻辑
    // 下载模版表格模版到本地
    // 统计 汇总 近三个月数据
    @Override
    public String exportTemplate() {

        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + File.separator + "article";
        File file = null;
        try {
            file = FileUtils.downLoadKnowFileFromUrl(templatePath, System.currentTimeMillis() +"template.xlsx", path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!file.exists()) {
            throw new BizException("下载模版表格失败");
        }

        LocalDate minDate = LocalDate.of(2025, 6, 1);
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.plusDays(-90);
        if (startDate.isBefore(minDate)) {
            startDate = minDate;
        }
        LocalDate endDate = now.plusDays(-1);


        List<ArticleDailyUsageData> usageData = baseMapper.selectList(Wraps.<ArticleDailyUsageData>lbQ()
                .orderByDesc(ArticleDailyUsageData::getCreateDay)
                .ge(ArticleDailyUsageData::getCreateDay, startDate.toString())
                .le(ArticleDailyUsageData::getCreateDay, endDate.toString()));
        // 使用临时文件来避免读写冲突
        File tempFile = new File(path +  File.separator + System.currentTimeMillis() + "temp.xlsx");
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis);
            FileOutputStream fos = new FileOutputStream(tempFile)) {
            Sheet sheetAt = workbook.getSheetAt(0);
            // 根据表格模版。填充其中的数据。
            // 填充 新注册用户数

            // 新注册用户数 （日累计）
            writeRegisterNumberDay(sheetAt, usageData, REGISTER_NUMBER);

            // 新注册用户数（周累计）
            writeRegisterNumberWeek(sheetAt, usageData, REGISTER_NUMBER);

            // 新注册用户数（月累计）
            writeRegisterNumberMonth(sheetAt, usageData, REGISTER_NUMBER);

            // 转化率(首次创作用户数/当天注册用户数)
            writeConversionRate(sheetAt, usageData);

            // 日活
            writeRegisterNumberDay(sheetAt, usageData, ACTIVE_NUMBER);
            // 周活
            writeRegisterNumberWeek(sheetAt, usageData, ACTIVE_NUMBER);
            // 月活
            writeRegisterNumberMonth(sheetAt, usageData, ACTIVE_NUMBER);

            // 7日留存
            writeRetainNumberDay(sheetAt, usageData, 7);

            // 30日留存
            writeRetainNumberDay(sheetAt, usageData, 30);

            // 周平均创作次数
            weekAverageCreationNumber(sheetAt, usageData);

            // 周文章的产出
            // 周视频产出
            // 周播客产出
            weekProduce(sheetAt, usageData);

            // 统计各周，各事件 （周累计）
            writeWeekEventNumber(sheetAt, usageData);

            // 每周 原创科普文章 等统计
            setWeekDateNumber(sheetAt, usageData);

            // 使用功能分布
            writeFunctionDistribution(sheetAt, usageData);

            workbook.write(fos);

            JSONObject jsonObject = aliYunOssFileUpload.updateFile("articleData" + System.currentTimeMillis(), "xlsx", tempFile, false);
            String fileUrl = jsonObject.getString("fileUrl");
            return fileUrl;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            file.delete();
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }


    }


    /**
     * 使用功能分布
     */
    private void writeFunctionDistribution(Sheet sheetAt, List<ArticleDailyUsageData> usageData) {

        Map<String, Long> functionErrorNumberMap = new LinkedHashMap<>();
        Map<String, Long> unfinishedTotalNumberMap = new LinkedHashMap<>();
        Map<String, Long> completeTotalNumberMap = new LinkedHashMap<>();
        Map<String, List<String>> weekDatesMap = new HashMap<>();

        List<String> months = new ArrayList<>();
        for (ArticleDailyUsageData usageDatum : usageData) {
            if (!months.contains(usageDatum.getCreateMonth())) {
                months.add(usageDatum.getCreateMonth());
            }
            weekDatesMap.computeIfAbsent(usageDatum.getCreateMonth(), k -> new ArrayList<>())
                    .add(usageDatum.getCreateDay());
            // 记录每周包含的日期
            functionErrorNumberMap.merge(usageDatum.getCreateMonth(), usageDatum.getProduceErrorTotalNumber(), Long::sum);
            unfinishedTotalNumberMap.merge(usageDatum.getCreateMonth(), usageDatum.getProduceUnfinishedTotalNumber(), Long::sum);
            completeTotalNumberMap.merge(usageDatum.getCreateMonth(), usageDatum.getProduceCompleteTotalNumber(), Long::sum);
        }

        Row row = sheetAt.getRow(114);
        if (row == null) {
            row = sheetAt.createRow(114);
        }
        Row row115 = sheetAt.getRow(115);
        if (row115 == null) {
            row115 = sheetAt.createRow(115);
        }
        Row row116 = sheetAt.getRow(116);
        if (row116 == null) {
            row116 = sheetAt.createRow(116);
        }

        Row row117 = sheetAt.getRow(117);
        if (row117 == null) {
            row117 = sheetAt.createRow(117);
        }
        Row row118 = sheetAt.getRow(118);
        if (row118 == null) {
            row118 = sheetAt.createRow(118);
        }

        int i = 1;
        for (String month : months) {
            Cell cell = row.createCell(i);
            List<String> strings = weekDatesMap.get(month);
            String startDay = strings.get(0);
            String endDay = strings.get(strings.size() - 1);
            if (strings.size() > 1) {
                cell.setCellValue(startDay + "至" + strings.get(strings.size() - 1));
            } else {
                cell.setCellValue(startDay);
            }

            cell.setCellValue(month);



            Cell row115Cell = row115.createCell(i);
            Long count = functionErrorNumberMap.get(month);
            Long unfinishedTotal = unfinishedTotalNumberMap.get(month);
            Long completeTotalNumber = completeTotalNumberMap.get(month);
            row115Cell.setCellValue(count != null && completeTotalNumber != null && completeTotalNumber > 0 ? (double) count / completeTotalNumber * 100 : 0); // 程序异常导致的失败 / 总发起数量
            Cell row116Cell = row116.createCell(i);
            row116Cell.setCellValue(unfinishedTotal != null && completeTotalNumber != null && completeTotalNumber > 0 ? (double) unfinishedTotal / completeTotalNumber * 100 : 0); // 未完成的 / 总发起数量

            LocalDateTime createTime = LocalDateTime.of(LocalDate.parse(endDay), LocalTime.MAX);
            LocalDateTime endTime = LocalDateTime.of(LocalDate.parse(startDay), LocalTime.MIN);

            int counted = podcastService.count(Wraps.<Podcast>lbQ()
                    .in(Podcast::getTaskStatus, PodcastTaskStatus.CREATE_ERROR, PodcastTaskStatus.CREATE_DIALOGUE_ERROR)
                    .between(SuperEntity::getCreateTime, createTime, endTime));

            if (counted == 0) {
                row117.createCell(i).setCellValue(0);
            } else {
                int countedAll = podcastService.count(Wraps.<Podcast>lbQ()
                        .between(SuperEntity::getCreateTime, createTime, endTime));
                row117.createCell(i).setCellValue(counted * 100 / countedAll);
            }


            int number = articleUserVoiceTaskService.count(Wraps.<ArticleUserVoiceTask>lbQ()
                    .eq(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.FAIL)
                    .between(SuperEntity::getCreateTime, createTime, endTime));
            if (number == 0) {
                row118.createCell(i).setCellValue(0);
            } else {
                int numberAll = articleUserVoiceTaskService.count(Wraps.<ArticleUserVoiceTask>lbQ()
                        .eq(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.FAIL)
                        .between(SuperEntity::getCreateTime, createTime, endTime));
                row118.createCell(i).setCellValue(number * 100 / numberAll);
            }
            i++;
        }



    }


    /**
     * 设置 每周 原创科普文章 等统计
     * @param sheetAt
     * @param usageData
     */
    private void setWeekDateNumber(Sheet sheetAt, List<ArticleDailyUsageData> usageData) {
        // 根据表格模版。填充其中的数据。
        Map<Integer, List<String>> weekDatesMap = new HashMap<>();
        Map<Integer, Long> articleOriginalScienceNumberMap = new LinkedHashMap<>();
        Map<Integer, Long> articleRewriteScienceNumberMap = new LinkedHashMap<>();
        Map<Integer, Long> articleOriginalSocialNumberMap = new LinkedHashMap<>();
        Map<Integer, Long> articleRewriteSocialNumberMap = new LinkedHashMap<>();
        Map<Integer, Long> articleOriginalVideoNumberMap = new LinkedHashMap<>();
        Map<Integer, Long> articleRewriteVideoNumberMap = new LinkedHashMap<>();
        Map<Integer, Long> articleOriginalDigitalPersonNumberMap = new LinkedHashMap<>();
        Map<Integer, Long> podcastNumberMap = new LinkedHashMap<>();


        List<Integer> weeks = new ArrayList<>();
        for (ArticleDailyUsageData usageDatum : usageData) {
            if (!weeks.contains(usageDatum.getWeekNumber())) {
                weeks.add(usageDatum.getWeekNumber());
            }
            // 记录每周包含的日期
            weekDatesMap.computeIfAbsent(usageDatum.getWeekNumber(), k -> new ArrayList<>())
                    .add(usageDatum.getCreateDay());

            articleOriginalScienceNumberMap.merge(usageDatum.getWeekNumber(), usageDatum.getArticleOriginalScienceNumber(), Long::sum);
            articleRewriteScienceNumberMap.merge(usageDatum.getWeekNumber(), usageDatum.getArticleRewriteScienceNumber(), Long::sum);
            articleOriginalSocialNumberMap.merge(usageDatum.getWeekNumber(), usageDatum.getArticleOriginalSocialNumber(), Long::sum);
            articleRewriteSocialNumberMap.merge(usageDatum.getWeekNumber(), usageDatum.getArticleRewriteSocialNumber(), Long::sum);
            articleOriginalVideoNumberMap.merge(usageDatum.getWeekNumber(), usageDatum.getArticleOriginalVideoNumber(), Long::sum);
            articleRewriteVideoNumberMap.merge(usageDatum.getWeekNumber(), usageDatum.getArticleRewriteVideoNumber(), Long::sum);
            articleOriginalDigitalPersonNumberMap.merge(usageDatum.getWeekNumber(), usageDatum.getArticleOriginalDigitalPersonNumber(), Long::sum);
            podcastNumberMap.merge(usageDatum.getWeekNumber(), usageDatum.getPodcastNumber(), Long::sum);
        }
        Row row = sheetAt.getRow(104);
        if (row == null) {
            row = sheetAt.createRow(104);
        }
        // 填充 每周 原创科普文章 等统计
        int i = 1;
        for (Integer week : weeks) {
            List<String> weekDates = weekDatesMap.get(week);
            if (CollUtil.isEmpty(weekDates)) {
                continue;
            }
            LocalDate startTime = LocalDate.parse(weekDates.get(0));
            LocalDate endTime = LocalDate.parse(weekDates.get(weekDates.size() - 1));

            Cell cell = row.createCell(i);
            if (weekDates.size() > 1) {
                cell.setCellValue(startTime.toString() + "至" + endTime.toString());
            } else {
                cell.setCellValue(startTime.toString());
            }
            // 原创科普文章
            Row row1 = sheetAt.getRow(105);
            if (row1 == null) {
                row1 = sheetAt.createRow(105);
            }
            cell = row1.createCell(i);
            cell.setCellValue(articleOriginalScienceNumberMap.get(week));
            // 重写科普文章
            Row row2 = sheetAt.getRow(106);
            if (row2 == null) {
                row2 = sheetAt.createRow(106);
            }
            cell = row2.createCell(i);
            cell.setCellValue(articleRewriteScienceNumberMap.get(week));
            // 原创社交文章
            Row row3 = sheetAt.getRow(107);
            if (row3 == null) {
                row3 = sheetAt.createRow(107);
            }
            cell = row3.createCell(i);
            cell.setCellValue(articleOriginalSocialNumberMap.get(week));
            // 重写社交文章
            Row row4 = sheetAt.getRow(108);
            if (row4 == null) {
                row4 = sheetAt.createRow(108);
            }
            cell = row4.createCell(i);
            cell.setCellValue(articleRewriteSocialNumberMap.get(week));
            // 原创视频文章
            Row row5 = sheetAt.getRow(109);
            if (row5 == null) {
                row5 = sheetAt.createRow(109);
            }
            cell = row5.createCell(i);
            cell.setCellValue(articleOriginalVideoNumberMap.get(week));
            // 重写视频文章
            Row row6 = sheetAt.getRow(110);
            if (row6 == null) {
                row6 = sheetAt.createRow(110);
            }
            cell = row6.createCell(i);
            cell.setCellValue(articleRewriteVideoNumberMap.get(week));
            // 数字人
            Row row7 = sheetAt.getRow(111);
            if (row7 == null) {
                row7 = sheetAt.createRow(111);
            }
            cell = row7.createCell(i);
            cell.setCellValue(articleOriginalDigitalPersonNumberMap.get(week));
            // 播客
            Row row8 = sheetAt.getRow(112);
            if (row8 == null) {
                row8 = sheetAt.createRow(112);
            }
            cell = row8.createCell(i);
            cell.setCellValue(podcastNumberMap.get(week));

            i++;
        }
    }


    /**
     * 统计各周，各事件 （周累计）
     * @param sheetAt
     * @param usageData
     */
    private void writeWeekEventNumber(Sheet sheetAt, List<ArticleDailyUsageData> usageData) {
        // 根据表格模版。填充其中的数据。
        Map<Integer, List<String>> weekDatesMap = new HashMap<>();
        List<Integer> weeks = new ArrayList<>();
        for (ArticleDailyUsageData usageDatum : usageData) {
            if (!weeks.contains(usageDatum.getWeekNumber())) {
                weeks.add(usageDatum.getWeekNumber());
            }
            // 记录每周包含的日期
            weekDatesMap.computeIfAbsent(usageDatum.getWeekNumber(), k -> new ArrayList<>())
                    .add(usageDatum.getCreateDay());
        }

        int i = 1;
        for (Integer week : weeks) {
            List<String> weekDates = weekDatesMap.get(week);
            if (CollUtil.isEmpty(weekDates)) {
                continue;
            }
            LocalDate endTime = LocalDate.parse(weekDates.get(0));
            LocalDate startTime = LocalDate.parse(weekDates.get(weekDates.size() - 1));
            List<EventCountDTO> eventCountDTOS = articleEventLogMapper.countEventsByType(startTime.toString(), endTime.toString());
            Map<String, Long> eventCountMap = eventCountDTOS.stream().collect(Collectors.toMap(EventCountDTO::getEventType, EventCountDTO::getCount));
            Row row = sheetAt.getRow(33);
            if (row == null) {
                row = sheetAt.createRow(33);
            }
            Cell cell = row.createCell(i);
            if (weekDates.size() > 1) {
                cell.setCellValue(weekDates.get(0) + "至" + weekDates.get(weekDates.size() - 1));
            } else {
                cell.setCellValue(weekDates.get(0));
            }

            writeWeekEventNumber(sheetAt, eventCountMap, i);
            i++;
        }
    }

    /**
     * 按 事件枚举。给表格中 写入事件数量
     * @param sheetAt
     * @param eventCountMap
     * @param cellIdx
     */
    private void writeWeekEventNumber(Sheet sheetAt, Map<String, Long> eventCountMap, int cellIdx) {
        // 根据表格模版。填充其中的数据。
        for (ArticleEventLogType value : ArticleEventLogType.values()) {
            Integer rowIdx = value.getRowIdx();
            Row row = sheetAt.getRow(rowIdx);
            if (row == null) {
                row = sheetAt.createRow(rowIdx);
            }
            Cell cell = row.createCell(cellIdx);
            String code = value.getCode();
            Long count = eventCountMap.get(code);
            cell.setCellValue(count == null ? 0 : count);
        }


    }



    /**
     * 周文章的产出
     * 周视频产出
     * 周播客产出
     * @param sheetAt
     * @param usageData
     */
    private void weekProduce(Sheet sheetAt, List<ArticleDailyUsageData> usageData) {
        // 根据表格模版。填充其中的数据。
        // 填充 周文章的产出
        Row row1 = sheetAt.getRow(27);
        Row row2 = sheetAt.getRow(28); // 周文章
        Row row3 = sheetAt.getRow(29); // 周视频
        Row row4 = sheetAt.getRow(30); // 周博客
        int i = 1;

        Map<Integer, Long> weekArticleNumberMap = new LinkedHashMap<>();
        Map<Integer, Long> weekPodcastNumberMap = new LinkedHashMap<>();
        Map<Integer, Long> weekVoiceNumberMap = new LinkedHashMap<>();
        Map<Integer, List<String>> weekDatesMap = new HashMap<>();
        List<Integer> weeks = new ArrayList<>();
        for (ArticleDailyUsageData usageDatum : usageData) {
            if (!weeks.contains(usageDatum.getWeekNumber())) {
                weeks.add(usageDatum.getWeekNumber());
            }
            // 累加每周创作数
            weekArticleNumberMap.merge(usageDatum.getWeekNumber(), usageDatum.getArticleCompleteNumber(), Long::sum);
            weekPodcastNumberMap.merge(usageDatum.getWeekNumber(), usageDatum.getPodcastCompleteNumber(), Long::sum);
            weekVoiceNumberMap.merge(usageDatum.getWeekNumber(), usageDatum.getVideoCompleteNumber(), Long::sum);
            // 记录每周包含的日期
            weekDatesMap.computeIfAbsent(usageDatum.getWeekNumber(), k -> new ArrayList<>())
                    .add(usageDatum.getCreateDay());
        }
        for (Integer week : weeks) {
            Cell cell1 = row1.createCell(i);
            Cell cell2 = row2.createCell(i);
            Cell cell3 = row3.createCell(i);
            Cell cell4 = row4.createCell(i);
            i++;
            List<String> dates = weekDatesMap.get(week);
            String dateRange;
            if (dates.size() > 1) {
                // 取每周的第一天和最后一天作为范围
                dateRange = dates.get(0) + "至" + dates.get(dates.size() - 1);
            } else {
                dateRange = dates.get(0);
            }
            cell1.setCellValue(dateRange);
            cell2.setCellValue(weekArticleNumberMap.get(week)); // 文章
            cell3.setCellValue(weekVoiceNumberMap.get(week));   // 视频
            cell4.setCellValue(weekPodcastNumberMap.get(week)); // 播客
        }


    }


    /**
     * 周平均创作次数
     * @param sheetAt
     * @param usageData
     */
    private void weekAverageCreationNumber(Sheet sheetAt, List<ArticleDailyUsageData> usageData) {
        // 根据表格模版。填充其中的数据。
        // 填充 周平均创作次数
        Row row1 = sheetAt.getRow(23);   // 周平均创作次数 日期行
        Row row2 = sheetAt.getRow(24);   // 周平均创作次数 数量行
        int i = 1;
        Map<Integer, Long> weekContentNumberMap = new LinkedHashMap<>();
        Map<Integer, List<String>> weekDatesMap = new HashMap<>();
        List<Integer> weeks = new ArrayList<>();
        for (ArticleDailyUsageData usageDatum : usageData) {
            if (!weeks.contains(usageDatum.getWeekNumber())) {
                weeks.add(usageDatum.getWeekNumber());
            }
            // 累加每周创作数
            weekContentNumberMap.merge(usageDatum.getWeekNumber(), usageDatum.getProduceCompleteTotalNumber(), Long::sum);
            // 记录每周包含的日期
            weekDatesMap.computeIfAbsent(usageDatum.getWeekNumber(), k -> new ArrayList<>())
                    .add(usageDatum.getCreateDay());
        }
        // 根据日期。统计这一周有多少用户创作了内容
        for (Integer week : weeks) {
            List<String> dates = weekDatesMap.get(week);
            long contentNumber = weekContentNumberMap.get(week);
            Cell cell1 = row1.createCell(i);
            Cell cell2 = row2.createCell(i);
            i++;
            String dateRange;
            if (dates.size() > 1) {
                // 取每周的第一天和最后一天作为范围
                dateRange = dates.get(0) + "至" + dates.get(dates.size() - 1);
            } else {
                dateRange = dates.get(0);
            }
            cell1.setCellValue(dateRange);
            if (contentNumber == 0) {
                cell2.setCellValue(0);
            } else {
                LocalDate endDay = LocalDate.parse(dates.get(0));
                LocalDate localDate = LocalDate.parse(dates.get(dates.size() - 1));
                int activeUsers = baseMapper.countActiveUsers(LocalDateTime.of(localDate, LocalTime.MIN), LocalDateTime.of(endDay, LocalTime.MAX));
                if (activeUsers == 0) {
                    cell2.setCellValue(0);
                } else {
                    cell2.setCellValue((double) contentNumber / activeUsers);
                }
            }



        }


    }



    /**
     * 新注册用户数 月累计 或 活跃用户数
     * @param sheetAt
     * @param usageData
     * @param type 类型  registerNumber 或 activeNumber
     */
    private void writeRegisterNumberMonth(Sheet sheetAt, List<ArticleDailyUsageData> usageData, String type) {
        // 根据表格模版。填充其中的数据。
        // 填充 新注册用户数
        Row row1 = sheetAt.getRow(type.equals(REGISTER_NUMBER) ? 5 :  15);   // 新用户注册 月累计 日期行
        Row row2 = sheetAt.getRow(type.equals(REGISTER_NUMBER) ? 6 :  16);   // 新用户注册 月累计 数量行
        int i = 1;


        List<String> monthes = new ArrayList<>();
        Map<String, Long> monthUserNumberMap = new HashMap<>();
        for (ArticleDailyUsageData usageDatum : usageData) {
            if (!monthes.contains(usageDatum.getCreateMonth())) {
                monthes.add(usageDatum.getCreateMonth());
            }
            monthUserNumberMap.merge(usageDatum.getCreateMonth(), type.equals(REGISTER_NUMBER) ? usageDatum.getRegisterNumber() : usageDatum.getActiveNumber(), Long::sum);
        }
        for (String month : monthes) {
            Cell cell1 = row1.createCell(i);
            Cell cell2 = row2.createCell(i);
            i++;
            cell1.setCellValue(month);
            cell2.setCellValue(monthUserNumberMap.get(month));
        }


    }

    /**
     * 新注册用户数 （周累计） 或 活跃用户数
     * @param sheetAt
     * @param usageData
     * @param type 类型  registerNumber 或 activeNumber
     */
    private void writeRegisterNumberWeek(Sheet sheetAt, List<ArticleDailyUsageData> usageData, String type) {
        // 根据表格模版。填充其中的数据。
        // 填充 新注册用户数
        Row row1 = sheetAt.getRow(type.equals(REGISTER_NUMBER) ? 3 :  13);   // 新用户注册 周累计 日期行
        Row row2 = sheetAt.getRow(type.equals(REGISTER_NUMBER) ? 4 :  14);   // 新用户注册 周累计 数量行
        int i = 1;

        List<Integer> weeks = new ArrayList<>();
        // 按周分组统计
        Map<Integer, Long> weekUserNumberMap = new LinkedHashMap<>();
        Map<Integer, List<String>> weekDatesMap = new HashMap<>();
        for (ArticleDailyUsageData usageDatum : usageData) {
            if (!weeks.contains(usageDatum.getWeekNumber())) {
                weeks.add(usageDatum.getWeekNumber());
            }
            // 累加每周用户数
            weekUserNumberMap.merge(usageDatum.getWeekNumber(), type.equals(REGISTER_NUMBER) ? usageDatum.getRegisterNumber() : usageDatum.getActiveNumber(), Long::sum);
            // 记录每周包含的日期
            weekDatesMap.computeIfAbsent(usageDatum.getWeekNumber(), k -> new ArrayList<>())
                    .add(usageDatum.getCreateDay());
        }

        for (Integer week : weeks) {

            Cell cell1 = row1.createCell(i);
            Cell cell2 = row2.createCell(i);
            i++;

            List<String> dates = weekDatesMap.get(week);
            String dateRange;
            if (dates.size() > 1) {
                // 取每周的第一天和最后一天作为范围
                dateRange = dates.get(0) + "至" + dates.get(dates.size() - 1);
            } else {
                dateRange = dates.get(0);
            }
            cell1.setCellValue(dateRange);
            cell2.setCellValue(weekUserNumberMap.get(week));
        }

    }


    /**
     * 7 日 留存率  和 30 日留存率
     * @param sheetAt
     * @param usageData
     * @param type 类型  registerNumber 或 activeNumber
     */
    private void writeRetainNumberDay(Sheet sheetAt, List<ArticleDailyUsageData> usageData, Integer type) {
        // 根据表格模版。填充其中的数据。
        // 填充 新注册用户数

        Row row1 = sheetAt.getRow(type.equals(7) ? 19 :  21);   // 新用户注册日期行
        Row row2 = sheetAt.getRow(type.equals(7) ? 20 :  22);   // 新用户注册数量行
        int i = 1;
        for (ArticleDailyUsageData usageDatum : usageData) {
            Cell cell1 = row1.createCell(i);
            Cell cell2 = row2.createCell(i);
            i++;
            cell1.setCellValue(usageDatum.getCreateDay());
            cell2.setCellValue(type.equals(7) ? usageDatum.getDay7Active() : usageDatum.getDay30Active());
        }
    }



    /**
     * 新注册用户数 （日累计） 或 活跃用户数
     * @param sheetAt
     * @param usageData
     * @param type 类型  registerNumber 或 activeNumber
     */
    private void writeRegisterNumberDay(Sheet sheetAt, List<ArticleDailyUsageData> usageData, String type) {
        // 根据表格模版。填充其中的数据。
        // 填充 新注册用户数

        Row row1 = sheetAt.getRow(type.equals(REGISTER_NUMBER) ? 1 :  11);   // 新用户注册日期行
        Row row2 = sheetAt.getRow(type.equals(REGISTER_NUMBER) ? 2 :  12);   // 新用户注册数量行
        int i = 1;
        for (ArticleDailyUsageData usageDatum : usageData) {
            Cell cell1 = row1.createCell(i);
            Cell cell2 = row2.createCell(i);
            i++;
            cell1.setCellValue(usageDatum.getCreateDay());
            cell2.setCellValue(type.equals(REGISTER_NUMBER) ? usageDatum.getRegisterNumber() : usageDatum.getActiveNumber());
        }
    }


    /**
     * 转化率：注册当天完成首次创作用户数/当天注册用户数）
     * @param sheetAt
     * @param usageData
     */
    private void writeConversionRate(Sheet sheetAt, List<ArticleDailyUsageData> usageData) {
        // 根据表格模版。填充其中的数据。
        // 填充 转化率
        Row row1 = sheetAt.getRow(8);   // 转化率 日期行
        Row row2 = sheetAt.getRow(9);   // 转化率 数量行
        int i = 1;
        for (ArticleDailyUsageData usageDatum : usageData) {
            Cell cell1 = row1.createCell(i);
            Cell cell2 = row2.createCell(i);
            i++;
            cell1.setCellValue(usageDatum.getCreateDay());
            if (usageDatum.getRegisterNumber() == 0) {
                cell2.setCellValue(0);
                continue;
            }
            cell2.setCellValue(usageDatum.getProduceFirstCompleteUserNumber() / (double) usageDatum.getRegisterNumber() * 100);
        }
    }


























}
