package com.caring.sass.ai.textual;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.config.CozeConfig;
import com.caring.sass.ai.entity.podcast.PodcastInputType;
import com.caring.sass.ai.entity.podcast.PodcastStyle;
import com.caring.sass.ai.entity.textual.TextualInterpretationPptTask;
import com.caring.sass.ai.know.config.DifyApi;
import com.caring.sass.ai.service.AliYunOssFileUpload;
import com.caring.sass.ai.textual.dao.TextualInterpretationPptTaskMapper;
import com.caring.sass.ai.textual.service.TextualInterpretationPptTaskService;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.base.R;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.api.FileUploadApi;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PptCreateTask {

    TextualInterpretationPptTaskMapper textualInterpretationPptTaskMapper;

    FileUploadApi fileUploadApi;


    public PptCreateTask(TextualInterpretationPptTaskMapper textualInterpretationPptTaskMapper, FileUploadApi fileUploadApi) {
        this.fileUploadApi = fileUploadApi;
        this.textualInterpretationPptTaskMapper = textualInterpretationPptTaskMapper;
    }


    public static void main(String[] args) {


        String url = "https://saas.api.yoo-ai.com/apps/ppt-result?id=rTWY9cnvP3frQnpXsPPpa7cbFgXZ6qgv";

        // 使用 GET 请求。 header 设置Authorization
        // 获取任务的结果
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Connection", "Keep-alive")
                .addHeader("Accept", "*/*")
                .addHeader("Authorization", "Bearer " + token)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            System.out.println(responseBody);
            JSONObject jsonObject = JSONObject.parseObject(responseBody, JSONObject.class);
            String string = jsonObject.getString("msg");
            if ("success".equals(string)) {
            JSONObject data = jsonObject.getJSONObject("data");
            String status = data.getString("status");
            if (status.equals("2") || Integer.parseInt(status) == 2) {   // 已完成
                System.out.println("已完成");
            }
            if (status.equals("3") || Integer.parseInt(status) == 3) {   // 已完成
                System.out.println("已失败");
            }
            }

        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }



    }



    // 查询数据库 的任务状态  优先查询最早提交的进行中的任务
    public void queryTaskStatus() {

        redisTemplate.opsForValue().setIfAbsent("textual_ppt_task_lock", "1", 5, TimeUnit.MINUTES);
        List<TextualInterpretationPptTask> list = textualInterpretationPptTaskMapper.selectList(Wraps.<TextualInterpretationPptTask>lbQ()
                        .last(" limit 0, 20")
                .orderByAsc(TextualInterpretationPptTask::getLastQueryStatusTime)
                .eq(TextualInterpretationPptTask::getPptTaskStatus, "1")
                .eq(TextualInterpretationPptTask::getTaskStatus, 0));

        if (CollUtil.isEmpty(list)) {
                // 没有任务了
            redisTemplate.delete("textual_ppt_task_lock");
            return;
        }

        try {
            for (TextualInterpretationPptTask pptTask : list) {

                String url = "https://saas.api.yoo-ai.com/apps/ppt-result?id=" + pptTask.getPptTaskId();

                // 使用 GET 请求。 header 设置Authorization
                // 获取任务的结果
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Connection", "Keep-alive")
                        .addHeader("Accept", "*/*")
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String responseBody = response.body().string();
                    JSONObject jsonObject = JSONObject.parseObject(responseBody, JSONObject.class);
                    pptTask.setPptDataResult(responseBody);
                    pptTask.setLastQueryStatusTime(LocalDateTime.now());
                    String string = jsonObject.getString("msg");
                    if ("success".equals(string)) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String status = data.getString("status");
                        if (status.equals("2") || Integer.parseInt(status) == 2) {   // 已完成
                            pptTask.setPptTaskStatus("2");
                        }
                        if (status.equals("3") || Integer.parseInt(status) == 3) {   // 已完成
                            pptTask.setPptTaskStatus("3");
                            if (StrUtil.isNotBlank(pptTask.getUid())) {
                                SseEmitter emitter = SseEmitterSession.get(pptTask.getUid());
                                if (emitter != null) {
                                    try {
                                        textualInterpretationPptTaskMapper.updateById(pptTask);
                                        emitter.send(SseEmitter.event().id(pptTask.getUid()).name("PPT任务失败").data(""));
                                        emitter.send(SseEmitter.event().id(pptTask.getUid()).name("[DONE]").data(""));
                                        emitter.complete();
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    }
                    textualInterpretationPptTaskMapper.updateById(pptTask);

                } catch (IOException e) {
                    // 处理异常
                    e.printStackTrace();
                }

            }
        } finally {
            redisTemplate.delete("textual_ppt_task_lock");
        }


    }

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public void downloadPptFile() {

        Boolean absent = redisTemplate.opsForValue().setIfAbsent("ppt_download_file_if_absent", "1", 5, TimeUnit.MINUTES);
        if (absent != null && !absent) {
            return;
        }
        List<TextualInterpretationPptTask> list = textualInterpretationPptTaskMapper.selectList(Wraps.<TextualInterpretationPptTask>lbQ()
                .last(" limit 0, 20")
                .orderByAsc(TextualInterpretationPptTask::getLastQueryStatusTime)
                .eq(TextualInterpretationPptTask::getPptTaskStatus, "2")
                .eq(TextualInterpretationPptTask::getTaskStatus, 0));

        if (CollUtil.isEmpty(list)) {
            redisTemplate.delete("ppt_download_file_if_absent");
            return;
        }

        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + "saas" + File.separator + "ppt_file";
        try {

            // 下载ppt文件到本地。上传到我们的oss
            for (TextualInterpretationPptTask pptTask : list) {

                String url = "https://saas.api.yoo-ai.com/apps/ppt-download?id=" + pptTask.getPptTaskId();

                // 使用 GET 请求。 header 设置Authorization
                // 获取任务的结果
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Connection", "Keep-alive")
                        .addHeader("Accept", "*/*")
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String responseBody = response.body().string();
                    JSONObject jsonObject = JSONObject.parseObject(responseBody, JSONObject.class);
                    pptTask.setPptDataResult(responseBody);
                    pptTask.setLastQueryStatusTime(LocalDateTime.now());
                    textualInterpretationPptTaskMapper.updateById(pptTask);
                    String string = jsonObject.getString("msg");
                    if ("success".equals(string)) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String download_url = data.getString("download_url");
                        if (download_url != null) {
                            String submitFileName;
                            String ext;
                            if (download_url.contains(".pptx")) {
                                submitFileName = "ppt_" + new Date().getTime();
                                ext = "pptx";
                            } else {
                                ext = "ppt";
                                submitFileName = "ppt_" + new Date().getTime();
                            }
                            File file = FileUtils.downLoadKnowFileFromUrl(download_url, submitFileName + "." + ext, path);

                            try {
                                JSONObject updateFile = aliYunOssFileUpload.updateFile(submitFileName, ext, file, false);
                                String fileUrl = updateFile.getString("fileUrl");
                                pptTask.setPptUrl(fileUrl);
                            } catch (Exception e) {
                                log.error("downloadPptFile aliYunOssFileUpload updateFile error", e);
                            }
                            pptTask.setTaskStatus(1);

                            textualInterpretationPptTaskMapper.updateById(pptTask);
                            if (StrUtil.isNotBlank(pptTask.getUid())) {
                                SseEmitter emitter = SseEmitterSession.get(pptTask.getUid());
                                if (emitter != null) {
                                    try {
                                        emitter.send(SseEmitter.event().id(pptTask.getUid()).name("PPT任务已完成").data(""));
                                        emitter.send(SseEmitter.event().id(pptTask.getUid()).name("[DONE]").data(""));
                                        emitter.complete();
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    // 处理异常
                    e.printStackTrace();
                }
            }
        } finally {
            redisTemplate.delete("ppt_download_file_if_absent");
        }

    }

    @Autowired
    AliYunOssFileUpload aliYunOssFileUpload;

    private static String token = "";

    /**
     * 创建一个ppt任务
     * @param pptTask
     * @return
     */
    public String createPpt(TextualInterpretationPptTask pptTask, String userName) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        json.put("text", pptTask.getContent());
        json.put("complex", 1); // PPT复杂度（1-简单 2-中等 3-复杂）
        json.put("font_name", "黑体"); // 字体（黑体、宋体、仿宋、幼圆、楷书、隶书）
        json.put("user_name", userName);
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url("https://saas.api.yoo-ai.com/apps/ppt-create")
                .addHeader("Content-Type", "application/json")
                .addHeader("Connection", "Keep-alive")
                .addHeader("Accept", "*/*")
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // 获取响应体内容
            String responseBody = response.body() != null ? response.body().string() : null;

            // 解析响应
            if (responseBody == null) {
                throw new IOException("Empty response body");
            }
            JSONObject jsonObject = JSONObject.parseObject(responseBody, JSONObject.class);
            String string = jsonObject.getString("msg");
            log.error("create ppt error: taskId {}，responseBody：{}", pptTask.getId(), responseBody);
            if ("success".equals(string)) {
                JSONObject data = jsonObject.getJSONObject("data");
                return data.getString("id");
            } else {
                throw new BizException("创建ppt失败");
            }
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }
        return null;
    }
}
