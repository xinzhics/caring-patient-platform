package com.caring.sass.ai.controller;

import com.alibaba.fastjson.JSON;
import com.caring.sass.ai.entity.card.BusinessCardDiagramTask;
import com.caring.sass.ai.service.AliYunAccessKey;
import com.caring.sass.ai.service.AliYunOssFileUpload;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.GenderType;
import com.caring.sass.context.BaseContextHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/fileupload")
@Api(value = "AliyunOssAuth", tags = "视频上传参数")
public class AliyunOssAuthController {

    @Autowired
    AliYunAccessKey aliYunAccessKey;

    @Autowired
    AliYunOssFileUpload aliYunOssFileUpload;

    @ApiOperation("获取阿里云的参数")
    @GetMapping("getAuth")
    public R<String> getAliYunOssAuth() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessKeyId", aliYunAccessKey.getAccessKeyId());
        jsonObject.put("accessKeySecret", aliYunAccessKey.getSecret());

        // 对 json 进行 加盐，加密
        String string = jsonObject.toString();
        // 进行base64加密
        Base64.Encoder encoder = Base64.getEncoder();
        String encodeToString = encoder.encodeToString(string.getBytes());
        // 加盐
        String salt = "caring-aliyun";
        // 加盐后再次加密
        String encodeToString1 = encoder.encodeToString((encodeToString + salt).getBytes());
        return R.success(encodeToString1);

    }


    @ApiOperation("获取视频封面")
    @PostMapping("/getImageUrl")
    public R<String> getImageUrl(@RequestBody com.alibaba.fastjson.JSONObject jsonObject) {

        // https://caring-saas-video.oss-cn-beijing.aliyuncs.com/video/1746678867623-5%E6%9C%888%E6%97%A5%20(3).mp4
        // 提取其中的 桶名称，caring-saas-video  对象名称 video/1746678867623-5%E6%9C%888%E6%97%A5%20(3).mp4
        String string = jsonObject.getString("url");
        URL url = null;
        try {
            url = new URL(string);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String host = url.getHost(); // 例如：caring-saas-video.oss-cn-beijing.aliyuncs.com

        // 提取 bucket 名称（host 的第一部分）
        String bucketName = host.split("\\.")[0];

        // objectName 是 URL 的路径部分（去掉开头的 '/'）
        String objectName = url.getPath().substring(1); // 去掉开头的 '/'
        // 上传到阿里云OSS
        String imageUrl = aliYunOssFileUpload.getImageUrl(bucketName, objectName, null);
        return R.success(imageUrl);

    }



    @ApiOperation(value = "上传打招呼视频到阿里云oss")
    @RequestMapping(value = "/uploadHelloVideo", method = RequestMethod.POST)
    public R<com.alibaba.fastjson.JSONObject> createTask(@RequestParam(value = "file") MultipartFile file) {
        // 参数校验
        if (file == null || file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }

        File tempFile = null;
        try {
            // 使用系统临时目录创建临时文件
            String originalFilename = file.getOriginalFilename();

            // 获取文件类型
            String fileType = "mp4"; // 默认类型
            if (originalFilename != null && originalFilename.contains(".")) {
                int lastDotIndex = originalFilename.lastIndexOf(".");
                if (lastDotIndex > 0 && lastDotIndex < originalFilename.length() - 1) {
                    fileType = originalFilename.substring(lastDotIndex + 1).toLowerCase();
                }
            }
            String dir = System.getProperty("java.io.tmpdir");
            String path = "/saas/article/human/video";
            File folder = new File(dir + path);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String uniqueFileName = UUID.randomUUID().toString().replaceAll("-", "");
            path = new StringBuilder().append(dir + path).append(uniqueFileName).append(".").append(fileType).toString();
            tempFile = new File(path);
            // 保存文件到临时位置
            file.transferTo(tempFile);

            // 生成唯一文件名

            // 上传到阿里云OSS
            com.alibaba.fastjson.JSONObject jsonObject = aliYunOssFileUpload.updateFile(
                    uniqueFileName, fileType, tempFile, true);

            return R.success(jsonObject);
        } catch (IOException e) {
            log.error("文件上传失败：保存临时文件时出错", e);
            return R.fail("文件上传失败");
        } catch (Exception e) {
            log.error("文件上传失败：上传到OSS时出错", e);
            return R.fail("文件上传失败");
        } finally {
            // 清理临时文件
            if (tempFile != null && tempFile.exists()) {
                if (!tempFile.delete()) {
                    log.warn("无法删除临时文件: {}", tempFile.getAbsolutePath());
                }
            }
        }
    }








}
