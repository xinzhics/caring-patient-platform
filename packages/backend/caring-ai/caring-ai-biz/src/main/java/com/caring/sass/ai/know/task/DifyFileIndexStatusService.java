package com.caring.sass.ai.know.task;


import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.entity.know.KnowledgeDailyCollection;
import com.caring.sass.ai.entity.know.KnowledgeFile;
import com.caring.sass.ai.entity.know.KnowledgeFileStatus;
import com.caring.sass.ai.know.service.KnowledgeDailyCollectionService;
import com.caring.sass.ai.know.service.KnowledgeFileLabelService;
import com.caring.sass.ai.know.service.KnowledgeFileService;
import com.caring.sass.ai.know.util.DifyKnowApi;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.database.mybatis.conditions.Wraps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DifyFileIndexStatusService {


    @Autowired
    KnowledgeDailyCollectionService knowledgeDailyCollectionService;

    @Autowired
    KnowledgeFileService knowledgeFileService;

    @Autowired
    KnowledgeFileLabelService knowledgeFileLabelService;

    @Autowired
    DifyKnowApi difyKnowApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private void serverStartInitTask() {
        List<KnowledgeDailyCollection> collections = knowledgeDailyCollectionService.list(Wraps.<KnowledgeDailyCollection>lbQ()
                .orderByAsc(SuperEntity::getCreateTime)
                .isNotNull(KnowledgeDailyCollection::getKnowDifyId)
                .in(KnowledgeDailyCollection::getDifyFileStatus, KnowledgeFileStatus.ANALYSIS, KnowledgeFileStatus.WAITING));

        if (!collections.isEmpty()) {
            collections.forEach(collection -> {
                JSONObject jsonObject = difyKnowApi.getFileIndexingStatus(collection.getKnowDifyId(), collection.getDifyBatch());
                if (jsonObject != null) {
                    String status = jsonObject.getString("indexing_status");
                    if ("waiting".equals(status)) {

                    } else if ("indexing".equals(status) || "parsing".equals(status)) {
                        collection.setDifyFileStatus(KnowledgeFileStatus.ANALYSIS);
                        String objectString = jsonObject.getString("completed_segments");
                        collection.setDifyFileIndexProgress(objectString);
                        knowledgeDailyCollectionService.updateById(collection);
                    } else if ("completed".equals(status)) {
                        // 索引建立完毕了。修改为分析关键词状态。
                        collection.setDifyFileIndexProgress("100");
                        collection.setDifyFileStatus(KnowledgeFileStatus.ANALYSIS_KEYWORD);
                        knowledgeDailyCollectionService.updateById(collection);

                        knowledgeFileLabelService.asyncAnalyzeTags(collection);

                    } else {
                        System.out.println("serverStartInitTaskList getFileIndexingStatus " + status);
                    }
                }
            });
        }



        List<KnowledgeFile> knowledgeFiles = knowledgeFileService.list(Wraps.<KnowledgeFile>lbQ()
                .orderByAsc(SuperEntity::getCreateTime)
                .isNotNull(KnowledgeFile::getDifyFileId)
                .in(KnowledgeFile::getDifyFileStatus, KnowledgeFileStatus.ANALYSIS, KnowledgeFileStatus.WAITING));

        if (knowledgeFiles.isEmpty()) {
            return;
        }
        knowledgeFiles.forEach(collection -> {
            JSONObject jsonObject = difyKnowApi.getFileIndexingStatus(collection.getKnowDifyId(), collection.getDifyBatch());
            if (jsonObject != null) {
                String status = jsonObject.getString("indexing_status");
                if ("waiting".equals(status)) {

                } else if ("indexing".equals(status) || "parsing".equals(status)) {
                    collection.setDifyFileStatus(KnowledgeFileStatus.ANALYSIS);
                    String objectString = jsonObject.getString("completed_segments");
                    collection.setDifyFileIndexProgress(objectString);
                    knowledgeFileService.updateById(collection);
                } else if ("completed".equals(status)) {
                    // 索引建立完毕了。修改为分析关键词状态。
                    collection.setDifyFileIndexProgress("100");
                    collection.setDifyFileStatus(KnowledgeFileStatus.ANALYSIS_KEYWORD);
                    knowledgeFileService.updateById(collection);
                    knowledgeFileLabelService.asyncAnalyzeTags(collection);
                } else if ("error".equals(status)) {
                    // 增加尝试机制，一个文件最多尝试3次。 3次后依然无法索引完毕的，标记为失败。
                    if (collection.getTryCount() > 3) {
                        collection.setDifyFileStatus(KnowledgeFileStatus.FAIL);
                        knowledgeFileService.updateById(collection);
                    } else {
                        collection.setTryCount(collection.getTryCount() + 1);
                        difyKnowApi.retryIndex(collection);
                        knowledgeFileService.updateById(collection);
                    }
                    System.out.println("serverStartInitTaskList getFileIndexingStatus " + status);
                }
            }
        });
    }

    /**
     * 定时任务。每10秒启动一次。
     *
     * 查询数据库中的日常收集。处于等待状态的资料。
     * 遍历，查询dify文档的索引进度。
     * 如果索引进度为100%，则更新状态为已完成。
     *
     */
    public void serverStartInitTaskList() {

        Boolean isLock = redisTemplate.opsForValue().setIfAbsent("dify_file_index_status_lock", "1", 5, TimeUnit.MINUTES);
        if (!isLock) {
            return;
        }
        try {
            serverStartInitTask();
        } finally {
            redisTemplate.delete("dify_file_index_status_lock");
        }

    }


}
