package com.caring.sass.file.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aliyun.oss.ServiceException;
import com.caring.sass.file.properties.FileServerProperties;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.exception.ClientRequestException;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.http.HttpConfig;
import com.huaweicloud.sdk.core.utils.JsonUtils;
import com.huaweicloud.sdk.mpc.v1.MpcClient;
import com.huaweicloud.sdk.mpc.v1.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @className: CaringMpcClient
 * @author: 杨帅
 * @date: 2023/4/6
 */
@EnableConfigurationProperties(FileServerProperties.class)
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = FileServerProperties.PREFIX, name = "type", havingValue = "HW")
public class CaringMpcClient {

    @Autowired
    public FileServerProperties fileProperties;

    public static MpcClient mpcClient = null;


    /**
     * @title 初始化mpc客户端
     * @author 杨帅 
     * @updateTime 2023/4/6 13:51 
     * @throws 
     */
    public synchronized MpcClient initMpcClient() {
        
        if (mpcClient != null) {
            return mpcClient;
        }
        FileServerProperties.HW hw = fileProperties.getHw();


        HttpConfig httpConfig = HttpConfig.getDefaultHttpConfig().withIgnoreSSLVerification(true).withTimeout(3);
        //http代理设置，请根据实际情况设置
        //httpConfig.withProxyHost("xxxxxx").withProxyPort(xxxxxx).withProxyUsername("xxxxxx").
        //        withProxyPassword("xxxxxx");
        //根据实际填写ak,sk，华为云控制台账号名下“我的凭证”>访问密钥上创建和查看您的AK/SK
        String ak = hw.getAccessKey();
        String sk = hw.getSecurityKey();
        //根据实际填写项目ID，华为云控制台账号名下“我的凭证”>API凭证下查看您的项目ID
        String projectId = hw.getXProjectId();
        //根据实际填写所需endpoint，这里以华北-北京四为例
        String endpoint = "https://mpc.cn-north-4.myhuaweicloud.com";
        BasicCredentials auth = new BasicCredentials().withAk(ak).withSk(sk).withProjectId(projectId);
        mpcClient = MpcClient.newBuilder()
                .withHttpConfig(httpConfig)
                .withCredential(auth)
                .withEndpoint(endpoint)
                .build();
        return mpcClient;
    }

    /**
     * @param bucket 桶的名称
     * @author: 杨帅
     * @date:  2023/4/6
     * @Desc: 创建一个视频截图请求。默认截第一秒的图片
     */
    public CreateThumbnailsTaskResponse createScreenshotTask(String bucket, String object) {

        // ThumbnailPara {type: DOTS, dots: [1], output_filename: fileName, format: 1}
        // tar 1
        // sync 是否同步处理，同步处理是指不下载全部文件，快速定位到截图位置进行截图。 取值如下： 0：排队处理。1：同步处理，暂只支持按时间点截单张图。 默认值：0
        ObsObjInfo input = new ObsObjInfo().withBucket(bucket).withLocation("cn-north-4").withObject(object);

        ObsObjInfo output = new ObsObjInfo().withBucket(bucket).withLocation("cn-north-4").withObject("output");
        List<Integer> dots = new ArrayList<>();
        dots.add(0);
        ThumbnailPara thumbnailPara = new ThumbnailPara()
                .withType(ThumbnailPara.TypeEnum.DOTS)
                .withDots(dots);
        thumbnailPara.setFormat(1);
        CreateThumbnailsTaskRequest req = new CreateThumbnailsTaskRequest()
                .withBody(new CreateThumbReq().withInput(input).withOutput(output)
                        //设置截图类型,此处理按时间点截图
                        .withThumbnailPara(thumbnailPara)
                        .withSync(1));

        //发送截图请求
        try {
            CreateThumbnailsTaskResponse rsp = initMpcClient().createThumbnailsTask(req);
            System.out.println("CreateThumbnailsTaskResponse=" + JsonUtils.toJSON(rsp));
            return rsp;
        } catch (ClientRequestException | ConnectionException | RequestTimeoutException | ServiceException e) {
            System.out.println(e);
        }
        return null;
    }



}
