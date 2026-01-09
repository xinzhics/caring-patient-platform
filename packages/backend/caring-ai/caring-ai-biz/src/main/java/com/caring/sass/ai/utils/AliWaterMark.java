package com.caring.sass.ai.utils;


import cn.hutool.core.codec.Base64Encoder;
import com.aliyun.mts20140618.models.QueryJobListResponse;
import com.aliyun.mts20140618.models.QueryJobListResponseBody;
import com.aliyun.mts20140618.models.SubmitJobsResponse;
import com.aliyun.mts20140618.models.SubmitJobsResponseBody;
import com.aliyun.tea.TeaException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.service.AliYunAccessKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AliWaterMark {


    @Autowired
    AliYunAccessKey aliYunAccessKey;

    /**
     * <b>description</b> :
     * <p>使用AK&amp;SK初始化账号Client</p>
     * @return Client
     *
     * @throws Exception
     */
    public com.aliyun.mts20140618.Client createClient() throws Exception {

        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(aliYunAccessKey.getAccessKeyId())
                .setAccessKeySecret(aliYunAccessKey.getSecret());
        config.endpoint = "mts.cn-beijing.aliyuncs.com";
        return new com.aliyun.mts20140618.Client(config);
    }

    public static void main(String[] args) {
        String fileUrl = "video/{FileName}water.mp4";

        try {
            fileUrl = URLEncoder.encode(fileUrl, "UTF-8");
        } catch (Exception e) {
            log.error("URLEncoder.encode error, error: {}", e.getMessage());
        }
        System.out.println(fileUrl);
    }

    /**
     * 提交文字水印转码作业
     * @param fileUrl 视频文件url
     * @param waterContent 水印内容
     * @return
     * @throws Exception
     */
    public SubmitJobsResponseBody submitTextWaterMarkJobs(String fileUrl, String waterContent, String referer, Integer width, Integer height) {
        com.aliyun.mts20140618.Client client;
        try {
            client = createClient();
        } catch (Exception e) {
            log.error("createClient error, error: {}", e.getMessage());
            return null;
        }
        // 从 obs 文件地址中，截取出来 obs的 桶，Location ，Object
        // https://caring-saas-video.oss-cn-beijing.aliyuncs.com/video/1762764593795.mp4
        String Bucket = aliYunAccessKey.getBucketName();
        String location = "oss-cn-beijing";
        String[] fileUrlParts = fileUrl.split(".aliyuncs.com/");
        String object = fileUrlParts[1];

        //构建水印输出配置
        JSONArray waterMarks = new JSONArray();  //水印数组大小上限为4，即同一路输出最多支持4个水印
        //文字水印
        JSONObject textWaterMarks = new JSONObject();
        textWaterMarks.put("WaterMarkTemplateId","717f4077286e43a5ba87c44e3f24feae");
        textWaterMarks.put("Type","Text");
        com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();
        String base64WaterContent = Base64Encoder.encode(waterContent);
        JSONObject TextWaterMark = new JSONObject();
        TextWaterMark.put("Content", base64WaterContent);
        TextWaterMark.put("FontName", "SimSun");
        TextWaterMark.put("FontSize", "20"); // (4,120)。
        TextWaterMark.put("FontColor", "White"); // (4,120)。
        TextWaterMark.put("FontAlpha", "0.3"); // 透明度 (0,1]。
        TextWaterMark.put("Top", height - 66);
        TextWaterMark.put("Left", width - 90);
        textWaterMarks.put("TextWaterMark", TextWaterMark);
        waterMarks.add(textWaterMarks);


        Map<String, Object> input = new HashMap<>();
        input.put("Bucket", Bucket);
        input.put("Location", location);
        input.put("Object", object);
        input.put("Referer", referer);

        // 3. 构建单个 Output
        Map<String, Object> output = new HashMap<>();
        output.put("OutputObject", "video%2F%7BFileName%7Dwater.mp4");
        output.put("TemplateId", aliYunAccessKey.getTemplateId());
        output.put("WaterMarks", waterMarks);

        try {
            String inputJson = mapper.writeValueAsString(input);
            String outputJson = mapper.writeValueAsString(Arrays.asList(output));
            System.out.println(inputJson);
            System.out.println(outputJson);
            com.aliyun.mts20140618.models.SubmitJobsRequest submitJobsRequest = new com.aliyun.mts20140618.models.SubmitJobsRequest()
                    //作业输入
    //                .setInput("{\"Bucket\":\"exampleBucket\",\"Location\":\"oss-cn-shanghai\",\"Object\":\"example.flv\",\"Referer\": \"用户自行在OSS控制台设置的OSS防盗链参数\"}")
                    .setInput(inputJson)
                    //作业输出配置
    //                .setOutputs("[{\"OutputObject\":\"exampleOutput.mp4\",\"TemplateId\":\"6181666213ab41b9bc21da8ff5ff****\",\"WaterMarks\":" + waterMarks.toJSONString() + ",\"UserData\":\"testid-001\"}]")
                    .setOutputs(outputJson)
                    //输出文件所在的OSS Bucket
                    .setOutputBucket(Bucket)
                    //输出文件所在的 OSS Bucket 的地域（OSS Region）
                    .setOutputLocation(location)
                    //管道ID
                    .setPipelineId(aliYunAccessKey.getPipelineId());
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

            // 复制代码运行请自行打印 API 的返回值
            SubmitJobsResponse submitJobsResponse = client.submitJobsWithOptions(submitJobsRequest, runtime);
            return submitJobsResponse.getBody();
        } catch (TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            log.error("submitTextWaterMarkJobs error, fileUrl: {}, waterContent: {}, referer: {}, error: {}", fileUrl, waterContent, referer, error.getMessage());
//            System.out.println(error.getMessage());
            // 诊断地址
            log.error("submitTextWaterMarkJobs error, Recommend: {}, ", error.getData().get("Recommend"));
//            System.out.println(error.getData().get("Recommend"));
//            com.aliyun.teautil.Common.assertAsString(error.message);
            log.error("submitTextWaterMarkJobs error message: {}, ", error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
        return null;
    }


    /**
     * 查询作业列表
     * @param jobId 一次最多10个任务
     * @return
     * @throws Exception
     */
    public QueryJobListResponseBody queryJobList(List<String> jobId) {

        com.aliyun.mts20140618.Client client;
        try {
            client = createClient();
        } catch (Exception e) {
            log.error("queryJobList error, jobId: {}, error: {}", String.join(",", jobId), e.getMessage());
            return null;
        }
        com.aliyun.mts20140618.models.QueryJobListRequest queryJobListRequest = new com.aliyun.mts20140618.models.QueryJobListRequest();
        queryJobListRequest.setJobIds(String.join(",", jobId));
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            QueryJobListResponse queryJobListResponse = client.queryJobListWithOptions(queryJobListRequest, runtime);
            QueryJobListResponseBody responseBody = queryJobListResponse.getBody();
            return responseBody;
        } catch (TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            log.error("queryJobList error, jobId: {}, error: {}", String.join(",", jobId), error.getMessage());
//            System.out.println(error.getMessage());
        } catch (Exception e) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            log.error("queryJobList error, jobId: {}, error: {}", String.join(",", jobId), e.getMessage());
        }
        return null;
    }

}
