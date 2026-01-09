package com.caring.sass.ai.service;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.config.CozeConfig;
import com.caring.sass.ai.dto.CozeRequest;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CozeServer {

    @Autowired
    CozeConfig cozeConfig;


    public JSONObject queryCoze(CozeRequest cozeRequest) {

        cozeConfig.checkCanQueryCoze(cozeConfig.getAuthorization());
        // 使用 okHttp3 sse 方式请求接口
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        json.put("bot_id", cozeRequest.getBot_id());
        json.put("user", cozeRequest.getUser());
        json.put("query", cozeRequest.getQuery());
        json.put("stream", false);
        String domain = cozeConfig.getDomain();
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(domain + CozeConfig.API)
                .addHeader("Content-Type", "application/json")
                .addHeader("Connection", "Keep-alive")
                .addHeader("Accept", "*/*")
                .addHeader("Authorization", cozeConfig.getAuthorization())
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

            return JSONObject.parseObject(responseBody, JSONObject.class);
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }
        return null;
    }

    public String updateFile(String fileUrl, String accessToken) {

        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + "saas" + File.separator + "knowfile";
        String substringed = fileUrl.substring(fileUrl.lastIndexOf(".") + 1);
        String fileName = UUID.randomUUID().toString().replace("-", "");
        File file;

        // 检查路径是否是 阿里云oss路径。如果是阿里云oss路径，将路径转换成内网路径。进行下载
        if (fileUrl.contains("oss-cn-beijing.aliyuncs.com")) {
            fileUrl = fileUrl.replace("oss-cn-beijing.aliyuncs.com", "oss-cn-beijing-internal.aliyuncs.com");
        }

        try {
            file = FileUtils.downLoadKnowFileFromUrl(fileUrl, fileName + "." + substringed, path);
        } catch (IOException e) {
            log.error("downLoadKnowFileFromUrl error", e);
            throw new BizException("下载文件失败");
        }
        RestTemplate restTemplate = new RestTemplate();
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + accessToken);
        // 创建请求实体
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送 POST 请求
        ResponseEntity<String> response = restTemplate.postForEntity("https://api.coze.cn/v1/files/upload", requestEntity, String.class);

        // 输出响应结果
        System.out.println("coze uploadFile Response Status Code: " + response.getStatusCode());
        String responseBody = response.getBody();
        System.out.println("coze uploadFile Response Body: " + responseBody);
        JSONObject jsonObject = JSONObject.parseObject(responseBody);
        JSONObject data = jsonObject.getJSONObject("data");
        if (data == null) {
            return null;
        }
        return data.getString("id");

    }
}
