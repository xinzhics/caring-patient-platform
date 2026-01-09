package com.caring.sass.ai.hwfile;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.utils.StrPool;
import com.obs.services.ObsClient;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.caring.sass.utils.DateUtils.DEFAULT_MONTH_FORMAT_SLASH;

/**
 * 华为obs
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = HwOssAutoConfigure.PREFIX)
public class HwOssAutoConfigure {

    public static final String PREFIX = "file.hw";

    private String endPoint;
    private String accessKey;
    private String securityKey;
    private String bucketName;
    private String xProjectId;


    public String uploadFile(MultipartFile multipartFile, String obsFileName) throws Exception {
        ObsClient obsClient = new ObsClient(this.getAccessKey(), this.getSecurityKey(), this.getEndPoint());
        String bucketName = this.getBucketName();
        if (!obsClient.headBucket(bucketName)) {
            obsClient.createBucket(bucketName);
        }

        //生成文件名
        String fileName = StrUtil.join(StrPool.EMPTY, obsFileName, StrPool.DOT, FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
        //日期文件夹
        String tenant = BaseContextHandler.getTenant();
        String relativePath = "";
        if (StrUtil.isNotBlank(tenant)) {
            relativePath = tenant + StrPool.SLASH + LocalDate.now().format(DateTimeFormatter.ofPattern(DEFAULT_MONTH_FORMAT_SLASH));
        } else {
            relativePath = LocalDate.now().format(DateTimeFormatter.ofPattern(DEFAULT_MONTH_FORMAT_SLASH));
        }
        // 服务器存放的绝对路径
        String relativeFileName = relativePath + StrPool.SLASH + fileName;

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, relativeFileName, multipartFile.getInputStream());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        putObjectRequest.setMetadata(metadata);
        PutObjectResult result = obsClient.putObject(putObjectRequest);
        obsClient.setObjectAcl(bucketName, relativeFileName, AccessControlList.REST_CANNED_PUBLIC_READ);
        log.info("result={}", JSONObject.toJSONString(result));

        String url = result.getObjectUrl();
        obsClient.close();
        return url;
    }


}
