package com.caring.sass.ai.podcast;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.entity.podcast.Podcast;
import com.caring.sass.ai.entity.podcast.PodcastInputType;
import com.caring.sass.ai.entity.podcast.PodcastSoundSet;
import com.caring.sass.ai.entity.podcast.PodcastStyle;
import com.caring.sass.ai.know.config.DifyApi;
import com.caring.sass.ai.know.config.DifyApiConfig;
import com.caring.sass.ai.know.util.DifyFlowControl;
import com.caring.sass.ai.podcast.dao.PodcastMapper;
import com.caring.sass.common.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PodcastDifyWordApi {


    @Autowired
    DifyApiConfig apiConfig;

    @Autowired
    DifyFlowControl difyFlowControl;

    @Autowired
    PodcastMapper podcastMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    /**
     * 上传文件到dify
     * @param apiUrl
     * @param file
     * @param userId
     * @param key
     * @return
     */
    public static String uploadFile(String apiUrl, File file, String userId, String key) {
        RestTemplate restTemplate = new RestTemplate();

        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + key);

        // 创建表单数据
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        body.add("user", userId);

        // 创建请求实体
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送 POST 请求
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

        // 输出响应结果
        System.out.println("PodcastDifyWordApi uploadFile Response Status Code: " + response.getStatusCode());
        String responseBody = response.getBody();
        System.out.println("PodcastDifyWordApi uploadFile Response Body: " + responseBody);
        JSONObject jsonObject = JSONObject.parseObject(responseBody);
        return jsonObject.getString("id");



    }

    /**
     * 使用sse方式。调用dify。
     * @param podcast
     * @throws Exception
     */
    public void callDifyCompletionMessagesStreaming(Podcast podcast, List<PodcastSoundSet> soundSetList, SseEmitter sseEmitter){
        String urlPath = apiConfig.getPodcastApiDomain() +  DifyApi.chatMessage.getPath();
        difyFlowControl.whenRedisValueIncrSuccess();

        // 使用 okHttp3 sse 方式请求接口
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        JSONObject jsonObject = new JSONObject();
        JSONObject inputs = new JSONObject();

        String botKey = "";
        if (podcast.getPodcastType() == null || podcast.getPodcastType().equals("article")) {
            botKey = apiConfig.getPodcast_bot_key();
        } else {
            botKey = apiConfig.getTextual_bot_key();
        }

        // 设置 输入的类型。
        inputs.put("input_type", podcast.getPodcastInputType().name);
        File file = null;
        String podcastInputContent = podcast.getPodcastInputContent();
        if (podcast.getPodcastInputType().equals(PodcastInputType.DOCUMENT)) {
            String osName = System.getProperty("os.name").toLowerCase();
            String dir;
            if (osName.contains("windows")) {
                dir =  "D:\\";
            } else {
                dir = System.getProperty("java.io.tmpdir");
            }
            String path = dir + "saas" + File.separator + "knowfile";
            try {
                String fileName = podcast.getPodcastInputFileName();
                if (podcastInputContent.contains("oss-cn-beijing.aliyuncs.com")) {
                    podcastInputContent = podcastInputContent.replace("oss-cn-beijing.aliyuncs.com", "oss-cn-beijing-internal.aliyuncs.com");
                }
                file = FileUtils.downLoadKnowFileFromUrl(podcastInputContent, fileName, path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!file.exists()) {
                log.error("File does not exist: " + file);
                return;
            }
            String apiFileUrl =  apiConfig.getPodcastApiDomain() +  DifyApi.filesUpload.getPath();
            String fileId = uploadFile(apiFileUrl, file, podcast.getUserId().toString(), botKey);
            JSONObject fileObj = new JSONObject();
            fileObj.put("type", "document");
            fileObj.put("transfer_method", "local_file");
            fileObj.put("upload_file_id", fileId);
            inputs.put("file", fileObj);
        } else if (podcast.getPodcastInputType().equals(PodcastInputType.ARTICLE_URL)) {

            inputs.put("input_content", podcastInputContent);
        } else {
            inputs.put("input_content", podcast.getPodcastInputTextContent());
        }
        inputs.put("guests_number", soundSetList.size());

        // 设置角色名称和头衔
        for (int i = 0; i < soundSetList.size(); i++) {
            String nameKey = "name" + (i + 1);
            String titleKey = "title" + (i + 1);
            inputs.put(nameKey , soundSetList.get(i).getRoleName());
            inputs.put(titleKey , soundSetList.get(i).getRoleTitle());
        }
        if (podcast.getPodcastStyle() == null) {
            log.error("callDifyCompletionMessagesStreaming podcastStyle is null, podcast id is {}", podcast.getId());
            inputs.put("podcastStyle" , PodcastStyle.DEFAULT_STYLE.name());
        } else {
            inputs.put("podcastStyle" , podcast.getPodcastStyle().name());
        }

        jsonObject.put("inputs", inputs);
        jsonObject.put("query", "开始");
        jsonObject.put("response_mode", "streaming");    // 使用阻塞模式。 3分钟的文本生成标题，应该不用很久
        jsonObject.put("user", podcast.getUserId()); // 替换为实际用户 ID

        EventSource.Factory factory = EventSources.createFactory(client);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String messageJson = JSONObject.toJSONString(jsonObject);
        RequestBody body = RequestBody.create(JSON, messageJson);
        log.error("Request Body: " + messageJson);
        Request request = new Request.Builder()
                .url(urlPath)
                .addHeader("Authorization", "Bearer " + botKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        // 直接吐出，不从redis中取
        Podcast2DifyBotSSEEventSourceListener sourceListener = new Podcast2DifyBotSSEEventSourceListener(podcastMapper, redisTemplate, podcast.getId().toString(), sseEmitter);
        factory.newEventSource(request, sourceListener);
        if (file != null && file.exists()) {
            file.delete();
        }
    }

}
