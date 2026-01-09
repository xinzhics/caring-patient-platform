package com.caring.sass.ai.know.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.entity.know.KnowledgeFile;
import com.caring.sass.ai.know.config.DifyApi;
import com.caring.sass.ai.know.config.DifyApiConfig;
import com.caring.sass.ai.know.model.DifyFilemodel;
import com.caring.sass.ai.know.model.DocumentText;
import com.caring.sass.common.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DifyKnowApi {

    @Autowired
    DifyApiConfig apiConfig;

    @Autowired
    DifyFlowControl difyFlowControl;

    /**
     * 给医生创建四个知识库。分别为 xxx专业学术资料， xxx个人成果， xxx病例库， xxx日常收集
     * curl --location --request POST 'http://dify.caringopen.cn/v1/datasets' \
     * --header 'Authorization: Bearer {api_key}' \
     * --header 'Content-Type: application/json' \
     * --data-raw '{"name": "name"}'
     */
    public String initKnowledge(String knowledgeName){

         return createKnowledgeBase(knowledgeName);
    }

    /**
     * 创建知识库
     * @param name
     * @return
     */
    private String createKnowledgeBase(String name) {
        String urlPath = apiConfig.getApiDomain() +  DifyApi.createDatasets.getPath();
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.createDatasets.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getAPIKey());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            String jsonInputString = jsonObject.toString();
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            StringBuilder response = new StringBuilder(); // 用于存储响应内容
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                log.debug(("知识库 '" + name + "' 创建成功"));
                // 读取响应内容
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                JSONObject result = JSON.parseObject(response.toString());
                return result.getString("id");
            } else {
                log.error("createKnowledgeBase '" + name + "' error: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * curl --location --request DELETE 'http://dify.caringopen.cn/v1/datasets/{dataset_id}/documents/{document_id}' \
     * --header 'Authorization: Bearer {api_key}'
     * @param datasetId 知识库ID
     * @param documentId 文档ID
     */
    public boolean deleteKnowledgeFile(String datasetId, String documentId) {
        if (StrUtil.isEmpty(documentId) || StrUtil.isEmpty(datasetId)) {
            return true;
        }
        String urlPath = apiConfig.getApiDomain() +  DifyApi.deleteDocuments.getPath();
        urlPath = urlPath.replace("{dataset_id}", datasetId);
        urlPath = urlPath.replace("{document_id}", documentId);
        if (StrUtil.isEmpty(documentId)) {
            return true;
        }
        if (StrUtil.isEmpty(datasetId)) {
            return true;
        }
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.deleteDocuments.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getAPIKey());
            conn.setDoOutput(true);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                log.debug("deleteKnowledgeFile '" + documentId + "' 删除成功");
                return true;
            } else {
                log.error("deleteKnowledgeFile '" + documentId + "' error: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;


    }


    /**
     * curl --location --request DELETE 'http://dify.caringopen.cn/v1/datasets/{dataset_id}' \
     * --header 'Authorization: Bearer {api_key}'
     *
     * 删除知识库
     * @param dataset_id
     */
    public void deleteKnowledge(String dataset_id) {
        if (StrUtil.isEmpty(dataset_id)) {
            return;
        }
        String urlPath = apiConfig.getApiDomain() +  DifyApi.deleteDatasets.getPath();
        urlPath = urlPath.replace("{dataset_id}", dataset_id);
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.deleteDatasets.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getAPIKey());
            conn.setDoOutput(true);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                log.debug("deleteKnowledge '" + dataset_id + "' 删除成功");
            } else {
                log.error("deleteKnowledge '" + dataset_id + "' 删除失败，响应代码: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过文本创建文档
     * curl --location --request POST 'http://dify.caringopen.cn/v1/datasets/{dataset_id}/document/create_by_text' \
     * --header 'Authorization: Bearer {api_key}' \
     * --header 'Content-Type: application/json' \
     * --data-raw '{"name": "text","text": "text","indexing_technique": "high_quality","process_rule": {"mode": "automatic"}}'
     * @param datasetId
     * @param name
     * @param text
     * @return
     */
    public DifyFilemodel createByText(String datasetId, String name, String text) {
        String urlPath = apiConfig.getApiDomain() +  DifyApi.createDocumentByText.getPath();
        urlPath = urlPath.replace("{dataset_id}", datasetId);
        difyFlowControl.whenRedisValueIncrSuccess();
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.createDocumentByText.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getAPIKey());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("text", text);
            // 索引方式
            jsonObject.put("indexing_technique", apiConfig.getTextIndexingTechnique());

            // 处理规则 - 自动
            JSONObject processRule = new JSONObject();
            processRule.put("mode", apiConfig.getTextProcessRule());
            jsonObject.put("process_rule", processRule);
            String jsonInputString = jsonObject.toString();
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            StringBuilder response = new StringBuilder(); // 用于存储响应内容
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                DifyFilemodel filemodel = JSON.parseObject(response.toString(), DifyFilemodel.class);
                return filemodel;
            } else {
                log.error("createByText '" + name + "' 创建失败，响应代码: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * curl --location --request GET 'http://dify.caringopen.cn/v1/datasets/{dataset_id}/documents/{batch}/indexing-status' \
     * --header 'Authorization: Bearer {api_key}'
     */
    public JSONObject getFileIndexingStatus(String dataset_id, String batch) {

        String urlPath = apiConfig.getApiDomain() +  DifyApi.getDocumentsIndexingStatus.getPath();
        urlPath = urlPath.replace("{dataset_id}", dataset_id);
        urlPath = urlPath.replace("{batch}", batch);
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getAPIKey());
            conn.setDoOutput(true);

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                JSONObject result = JSON.parseObject(response.toString());
                JSONArray jsonArray = result.getJSONArray("data");
                if (CollUtil.isEmpty(jsonArray)) {
                    return null;
                }
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 通过文本更新文档
     * curl --location --request POST 'http://dify.caringopen.cn/v1/datasets/{dataset_id}/documents/{document_id}/update_by_text' \
     * --header 'Authorization: Bearer {api_key}' \
     * --header 'Content-Type: application/json' \
     * --data-raw '{"name": "name","text": "text"}'
     * @param knowDifyId
     * @param documentId
     * @param textTitle
     * @param textContent
     * @return
     */
    public DifyFilemodel updateByText(String knowDifyId, String documentId,  String textTitle, String textContent) {

        String urlPath = apiConfig.getApiDomain() +  DifyApi.updateDocumentsByText.getPath();
        urlPath = urlPath.replace("{dataset_id}", knowDifyId);
        urlPath = urlPath.replace("{document_id}", documentId);
        difyFlowControl.whenRedisValueIncrSuccess();
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.updateDocumentsByText.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getAPIKey());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", textTitle);
            jsonObject.put("text", textContent);
            String jsonInputString = jsonObject.toString();
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            StringBuilder response = new StringBuilder(); // 用于存储响应内容
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                DifyFilemodel filemodel = JSON.parseObject(response.toString(), DifyFilemodel.class);
                return filemodel;
            } else {
                log.error("updateByText '" + textTitle + "' 修改失败，响应代码: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 根据文件连接下载文件
     * 上传文件到 dify
     *
     * curl --location --request POST 'http://dify.caringopen.cn/v1/datasets/{dataset_id}/document/create_by_file' \
     * --header 'Authorization: Bearer {api_key}' \
     * --form 'data="{"indexing_technique":"economy","process_rule":{"rules":{"pre_processing_rules":[{"id":"remove_extra_spaces","enabled":true},{"id":"remove_urls_emails","enabled":true}],"segmentation":{"separator":"###","max_tokens":500}},"mode":"custom"}}";type=text/plain' \
     * --form 'file=@"/path/to/file"'
     * @param knowledgeFile
     */
    public DifyFilemodel createByFile(KnowledgeFile knowledgeFile) {

        String fileName = knowledgeFile.getFileName();
        String fileUrl = knowledgeFile.getFileUrl();
        String knowDifyId = knowledgeFile.getKnowDifyId();
        String osName = System.getProperty("os.name").toLowerCase();
        String dir;
        if (osName.contains("windows")) {
            dir =  "D:\\";
        } else {
            dir = System.getProperty("java.io.tmpdir");
        }
        String path = dir + "saas" + File.separator + "knowfile";
        File file = null;
        try {
            file = FileUtils.downLoadKnowFileFromUrl(fileUrl, fileName, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!file.exists()) {
            return null;
        }

        // 将文件上传到 dify
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        JSONObject data = new JSONObject();
        data.put("indexing_technique", apiConfig.getTextIndexingTechnique());

        String rules = "{\"rules\":{\"pre_processing_rules\":[{\"id\":\"remove_extra_spaces\",\"enabled\":true},{\"id\":\"remove_urls_emails\",\"enabled\":true}],\"segmentation\":{\"separator\":\"###\",\"max_tokens\":500}},\"mode\":\"custom\"}";
        JSONObject rulesJson = JSON.parseObject(rules);
        data.put("process_rule", rulesJson);


        body.add("data", data);
        body.add("file", new FileSystemResource(file));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + apiConfig.getAPIKey());

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        String url = apiConfig.getApiDomain() + DifyApi.createDocumentByFile.getPath();
        url = url.replace("{dataset_id}", knowDifyId);
        difyFlowControl.whenRedisValueIncrSuccess();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    request,
                    String.class
            );
            HttpStatus statusCode = response.getStatusCode();
            if (statusCode.equals(HttpStatus.OK)) {
                System.out.println(statusCode);
                String responseBody = response.getBody();
                System.out.println(responseBody);
                return JSON.parseObject(responseBody, DifyFilemodel.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to upload file to dify", e);
            // throw new RuntimeException("Failed to upload file to dify", e);
            return null;
        } finally {
            file.delete();
        }

    }


    /**
     * curl --location --request GET 'http://dify.caringopen.cn/v1/datasets/{dataset_id}/documents/{batch}/indexing-status' \
     * --header 'Authorization: Bearer {api_key}'
     */
    public List<DocumentText> getFileSegments(String dataset_id, String document_id) {

        String urlPath = apiConfig.getApiDomain() +  DifyApi.segments.getPath();
        urlPath = urlPath.replace("{dataset_id}", dataset_id);
        urlPath = urlPath.replace("{document_id}", document_id);
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.segments.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getAPIKey());
            conn.setDoOutput(true);

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                JSONObject result = JSON.parseObject(response.toString());
                String data = result.getString("data");
                if (StrUtil.isEmpty(data)) {
                    return null;
                }
                return JSON.parseArray(data, DocumentText.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 对索引失败的文件，发起索引重试
     * @param collection
     * @return
     */
    public boolean retryIndex(KnowledgeFile collection) {

        String urlPath = apiConfig.getApiDomain() +  DifyApi.retryDocumentIndex.getPath();
        urlPath = urlPath.replace("{dataset_id}", collection.getKnowDifyId());
        urlPath = urlPath.replace("{document_id}", collection.getDifyFileId());
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getAPIKey());
            conn.setDoOutput(true);

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                log.error(" retryIndex error {} ", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    /**
     * 知识库文档元数据赋值
     * @param datasetId
     */
    public String datasetsDocumentsMetadata(String datasetId, String documentId, List<Map<String, String>> list) {

        String urlPath = apiConfig.getApiDomain() +  DifyApi.datasetsDocumentsMetadata.getPath();
        urlPath = urlPath.replace("{dataset_id}", datasetId);
        difyFlowControl.whenRedisValueIncrSuccess();
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.datasetsDocumentsMetadata.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getAPIKey());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONArray operationData = new JSONArray();
            JSONObject operation = new JSONObject();
            operation.put("document_id", documentId);
            operation.put("metadata_list", list);
            operationData.add(operation);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operation_data", operationData);
            String jsonInputString = jsonObject.toString();
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            StringBuilder response = new StringBuilder(); // 用于存储响应内容
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                log.error("datasetsDocumentsMetadata error {} ", response);
            } else {
                log.error("documentId '" + documentId + "' 创建失败，响应代码: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 创建元数据
     * @param datasetId
     */
    public String datasetsMetadata(String datasetId, String metadataName) {


        String urlPath = apiConfig.getApiDomain() +  DifyApi.datasetsMetadata.getPath();
        urlPath = urlPath.replace("{dataset_id}", datasetId);
        difyFlowControl.whenRedisValueIncrSuccess();
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.datasetsMetadata.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getAPIKey());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "string");
            jsonObject.put("name", metadataName);

            String jsonInputString = jsonObject.toString();
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            StringBuilder response = new StringBuilder(); // 用于存储响应内容
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                // 读取响应内容
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                String responseString = response.toString();
                JSONObject parsedObject = JSON.parseObject(responseString);
                String id = parsedObject.getString("id");
                return id;
            } else {
                log.error("metadataName '" + metadataName + "' 创建失败，响应代码: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
