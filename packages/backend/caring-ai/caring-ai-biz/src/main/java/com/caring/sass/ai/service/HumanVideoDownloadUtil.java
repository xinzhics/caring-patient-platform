package com.caring.sass.ai.service;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.entity.article.ArticleUserVoiceTask;
import com.caring.sass.ai.entity.humanVideo.BusinessDigitalHumanVideoTask;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class HumanVideoDownloadUtil {


    @Autowired
    AliYunOssFileUpload aliYunOssFileUpload;


    public File downLoadFromFile(String u, String fileName, String dir) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(3000);
            conn.setReadTimeout(600000); // 读取超时时间，这里设置为10分钟
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            inputStream = conn.getInputStream();

            File saveDir = new File(dir);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            String fileType = u.substring(u.lastIndexOf(".") + 1);
            if (fileType.equals("null")) {
                fileType = "mp4";
            }
            String path = new StringBuilder().append(dir).append(fileName).append(".").append(fileType).toString();
            File file = new File(path);
            outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096]; // 使用4KB的缓冲区
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("download file success. file type: " + fileType);

            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return null;
    }


    /**
     * 下载百度合成好的视频
     * @param videoUrl
     * @param task
     */
    public void downloadVideo(String videoUrl, ArticleUserVoiceTask task) {

        String dir = System.getProperty("java.io.tmpdir");
        String path = "/saas/article/human/video";
        File folder = new File(dir + path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file;
        try {
            file = downLoadFromFile(videoUrl, task.getId().toString(), dir + path);
        } catch (IOException e) {
            task.setTaskStatus(HumanVideoTaskStatus.FAIL);
            task.setErrorMessage("下载数字人视频文件失败");
            return;
        }
        String fileType = "mp4";
        if (file != null) {
            String filePath = file.getPath();
            int lastDotIndex = filePath.lastIndexOf(".");
            if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
                fileType = filePath.substring(lastDotIndex + 1);
            }
        }

        try {
            JSONObject jsonObject = aliYunOssFileUpload.updateFile(UUID.randomUUID().toString(), fileType, file, true);
            Object fileUrl = jsonObject.get("fileUrl");
            Object presignedUrl = jsonObject.get("presignedUrl");
            task.setTaskStatus(HumanVideoTaskStatus.SUCCESS);
            if (Objects.nonNull(fileUrl)) {
                task.setGenerateAudioUrl(fileUrl.toString());
                if (Objects.nonNull(presignedUrl)) {
                    task.setHumanVideoCover(presignedUrl.toString());
                }
            } else {
                task.setTaskStatus(HumanVideoTaskStatus.FAIL);
                task.setErrorMessage("上传数字人到阿里云obs失败");
            }
        } catch (Exception e) {
            task.setTaskStatus(HumanVideoTaskStatus.FAIL);
            task.setErrorMessage("上传数字人到阿里云obs失败");
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }

    }

    /**
     * 下载科普名片数字人
     * @param videoUrl
     * @param task
     */
    public void downloadVideo(String videoUrl, BusinessDigitalHumanVideoTask task) {

        String dir = System.getProperty("java.io.tmpdir");
        String path = "/saas/business/card/video";
        File folder = new File(dir + path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file;
        try {
            file = downLoadFromFile(videoUrl, task.getId().toString(), dir + path);
        } catch (Exception e) {
            task.setTaskStatus(HumanVideoTaskStatus.FAIL);
            task.setTaskMessage("下载数字人视频文件失败");
            return;
        }
        String fileType = "mp4";
        if (file != null) {
            String filePath = file.getPath();
            int lastDotIndex = filePath.lastIndexOf(".");
            if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
                fileType = filePath.substring(lastDotIndex + 1);
            }
        }
        try {
            JSONObject jsonObject = aliYunOssFileUpload.updateFile(UUID.randomUUID().toString(), fileType, file, true);
            Object fileUrl = jsonObject.get("fileUrl");
            Object presignedUrl = jsonObject.get("presignedUrl");
            if (Objects.nonNull(fileUrl)) {
                task.setHumanVideoUrl(fileUrl.toString());
                task.setTaskStatus(HumanVideoTaskStatus.SUCCESS);
                task.setEndTime(LocalDateTime.now());
                if (Objects.nonNull(presignedUrl)) {
                    task.setHumanVideoCover(presignedUrl.toString());
                }
            } else {
                task.setTaskStatus(HumanVideoTaskStatus.FAIL);
                task.setTaskMessage("上传阿里云oss失败");
            }
        } catch (Exception e) {
            task.setTaskStatus(HumanVideoTaskStatus.FAIL);
            task.setTaskMessage("上传阿里云oss失败");
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }

    }


    /**
     * 下载海螺试听音频
     *
     * @param videoUrl 海螺丽试听链接
     * @param fileName 文件名
     */
    public String downloadVoiceAndUploadAliOss(String videoUrl, String fileName) {
        String dir = System.getProperty("java.io.tmpdir");
        String path = "/saas/business/card/voice";
        File folder = new File(dir + path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = null;
        try {
            file = downLoadFromFile(videoUrl, fileName, dir + path);
            JSONObject jsonObject = aliYunOssFileUpload.updateFile(fileName, "mp3", file, false);
            return jsonObject.getString("fileUrl");
        } catch (Exception e) {
            return null;
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }




}
