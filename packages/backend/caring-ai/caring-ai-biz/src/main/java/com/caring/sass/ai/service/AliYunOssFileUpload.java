package com.caring.sass.ai.service;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.caring.sass.ai.utils.FFmpegCoverGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Date;

@Component
@Slf4j
public class AliYunOssFileUpload {

    @Autowired
    AliYunAccessKey aliYunAccessKey;

    public String getImageUrl(String bucketName, String objectName, OSS ossClient) {

        String endpoint = aliYunAccessKey.getEndpoint();
        // 填写AccessKey ID和AccessKey Secret。
        String accessKeyId = aliYunAccessKey.getAccessKeyId();
        String accessKeySecret = aliYunAccessKey.getSecret();
        // 创建OSSClient实例。
        if (ossClient == null) {
            ossClient = new OSSClientBuilder()
                    .build(endpoint, accessKeyId, accessKeySecret);
        }
        String dir = System.getProperty("java.io.tmpdir");
        String path = "/saas/human/video/image/";
        File folder = new File(dir + path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethod.GET);
        Date expiration = new Date(new Date().getTime() + 1000 * 60 * 10 );
        String style = "video/snapshot,t_0,f_jpg,w_0,h_0";
        req.setExpiration(expiration);
        req.setProcess(style);
        URL generatePresignedUrl = ossClient.generatePresignedUrl(req);
        String imageUrl = generatePresignedUrl.getFile();
        imageUrl = "https://" + bucketName + ".oss-cn-beijing.aliyuncs.com" + imageUrl;
        // 下载imageUrl到本地。重新上传至oss。将其转成永久图片
        File image = null;
        String fileName = UUID.fastUUID().toString().replaceAll("-", "");
        try {
            String string = downLoadFromUrl(imageUrl, fileName + ".jpg", path);
            image = new File(string);
            if (image.exists()) {
                JSONObject updateFile = updateFile(fileName, "jpg", image, false);
                Object imageObjUrl = updateFile.get("fileUrl");
                return imageObjUrl.toString();
            }
        } catch (Exception e) {
            log.error("下载图片失败：" + e.getMessage());
        } finally {
            if (image != null) {
                image.delete();
            }
        }
        return null;
    }



    /**
     *
     * @param fileName
     * @param ext
     * @param file
     * @param needPresigned
     * @return {fileUrl：xxx, presignedUrl: xxx}
     * @throws Exception
     */
    public JSONObject updateFile(String fileName, String ext, File file, boolean needPresigned) throws Exception {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "https://oss-cn-beijing-internal.aliyuncs.com";
        String endpoint = aliYunAccessKey.getEndpoint();
        // 填写AccessKey ID和AccessKey Secret。
        String accessKeyId = aliYunAccessKey.getAccessKeyId();
        String accessKeySecret = aliYunAccessKey.getSecret();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = aliYunAccessKey.getBucketName();
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "human/video/" + fileName + "." + ext;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder()
                .build(endpoint, accessKeyId, accessKeySecret);

        JSONObject jsonObject = new JSONObject();
        String fileUrl;
        try {
            try {
                // 创建PutObjectRequest对象。
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file);
                // 上传文件。
                ossClient.putObject(putObjectRequest);
                fileUrl = "https://" + bucketName + ".oss-cn-beijing.aliyuncs.com/" + objectName;
                jsonObject.put("fileUrl", fileUrl);
            } catch (Exception e) {
                System.out.println("文件上传失败：" + e.getMessage());
                return jsonObject;
            }
            // 需要视频封面时
            if (needPresigned) {
                // 如果视频是 webm的视频。那么使用 FFmpeg 工具进行封面截图。
                if (ext.equals("webm")) {
                    String dir = System.getProperty("java.io.tmpdir");
                    String path = "/saas/human/video/image/";
                    File folder = new File(dir + path);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    String imagePath = Paths.get(dir, path, fileName + ".jpg").toString();
                    boolean codeCheck = FFmpegCoverGenerator.generateCover(file.getPath(), imagePath);
                    if (codeCheck) {
                        File image = new File(imagePath);
                        if (image.exists()) {
                            JSONObject updateFile = updateFile(fileName, "jpg", image, false);
                            Object imageObjUrl = updateFile.get("fileUrl");
                            if (imageObjUrl != null) {
                                jsonObject.put("presignedUrl", imageObjUrl.toString());
                            }
                            image.delete();
                        } else {
                            log.error("封面文件生成成功但文件不存在，请检查路径：{}", imagePath);
                        }
                    }
                } else {

                    String imageUrl = getImageUrl(bucketName, objectName, ossClient);
                    if (imageUrl != null) {
                        jsonObject.put("presignedUrl", imageUrl);
                    }
                }
            }
        } finally {
            if (ossClient != null) {
                // 关闭OSSClient。
                ossClient.shutdown();
            }
        }


        return jsonObject;

    }


    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public String downLoadFromUrl(String u, String fileName, String dir) throws IOException {
        URL url = new URL(u);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        InputStream inputStream = conn.getInputStream();
        byte[] getData = readInputStream(inputStream);
        File saveDir = new File(dir);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        String path = dir + fileName;
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }

        if (inputStream != null) {
            inputStream.close();
        }

        return path;
    }



    /**
     * 删除阿里云的文件资源
     * @param audioUrl
     */
    public void deleteByUrl(String audioUrl) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "https://oss-cn-beijing-internal.aliyuncs.com";
        String endpoint = aliYunAccessKey.getEndpoint();
        // 填写AccessKey ID和AccessKey Secret。
        String accessKeyId = aliYunAccessKey.getAccessKeyId();
        String accessKeySecret = aliYunAccessKey.getSecret();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "caring-saas-video";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = audioUrl.substring(audioUrl.lastIndexOf(".aliyuncs.com") + 14);

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder()
                .build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建PutObjectRequest对象。
            GenericRequest request = new GenericRequest(bucketName, objectName);
            // 上传文件。
            ossClient.deleteObject(request);
        }
        finally {
            if (ossClient != null) {
                // 关闭OSSClient。
                ossClient.shutdown();
            }
        }
    }
}
