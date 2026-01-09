package com.caring.sass.ai.utils;

import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.ImageUtils;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 视频封底制作
 */
@Slf4j
public class VideoOverlay {
    static String background = "https://caring-saas-video.oss-cn-beijing-internal.aliyuncs.com/human/03c51ea4d2eb1ffb71f5bd99f35b76dc.jpg";
//    static String background = "https://caring-saas-video.oss-cn-beijing.aliyuncs.com/human/03c51ea4d2eb1ffb71f5bd99f35b76dc.jpg";
    static String inputVideo = "https://caring-saas-video.oss-cn-beijing-internal.aliyuncs.com/human/03c51ea4d2eb1ffb71f5bd99f35b76dc.mp4";
//    static String inputVideo = "https://caring-saas-video.oss-cn-beijing.aliyuncs.com/human/03c51ea4d2eb1ffb71f5bd99f35b76dc.mp4";

    static String inputVideo16_9 = "https://caring-saas-video.oss-cn-beijing-internal.aliyuncs.com/human/article_video_back_cover_16_9.mp4";
    static String inputVideo9_16 = "https://caring-saas-video.oss-cn-beijing-internal.aliyuncs.com/human/article_video_back_cover_9_16.mp4";
    static String inputVideo1_1 = "https://caring-saas-video.oss-cn-beijing-internal.aliyuncs.com/human/article_video_back_cover_1_1.mp4";

    public static final String imagePath = File.separator + "saas" + File.separator + "tempfile" + File.separator;


    /**
     * 调整封底素材的尺寸
     * @param width
     * @param height
     * @param taskId
     * @return
     */
    public static String createBackVideo(int width, int height, Long taskId, double fps) {
        String tempInputVideo = null;

        try {
            String dir = System.getProperty("java.io.tmpdir");
            dir = dir + imagePath + taskId + File.separator;
            File saveDir = new File(dir);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            String downloadUrl = null;
            if (width == height) {
                downloadUrl = inputVideo1_1;
            } else if (width > height) {
                downloadUrl = inputVideo16_9;
            } else {
                downloadUrl = inputVideo9_16;
            }
            String fileName = UUID.randomUUID().toString();
            try {
                tempInputVideo = downLoadFromUrl(downloadUrl, fileName + "_inputVideo.mp4", dir);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            log.error("createBackVideo tempInputVideo downLoadFromUrl success: {}", tempInputVideo);

            String outputVideo = dir + fileName + "_outputVideo.mp4";

            // 构建 FFmpeg 命令（强制缩放，可能变形）
            ProcessBuilder pb = new ProcessBuilder(
                    "/usr/bin/ffmpeg",
                    "-i", tempInputVideo,
                    "-vf", "scale=" + width + ":" + height + ",fps=" + fps,
                    "-c:a", "copy",
                    "-y",
                    outputVideo
            );

            pb.redirectErrorStream(true); // 合并 stdout 和 stderr
            try {
                Process process = pb.start();

                // 读取 FFmpeg 输出日志（可选）
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("视频转换成功：" + outputVideo);
                    return outputVideo;
                } else {
                    System.out.println("视频转换失败，退出码：" + exitCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (tempInputVideo != null && new File(tempInputVideo).exists()) {
                    new File(tempInputVideo).delete();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }




    /**
     * 合并视频：coverVideo + video + backCoverVideo
     * 支持 coverVideo 无音频的情况
     *
     * @param coverVideo      开场视频（可为 null，可无音频）
     * @param video           主视频（必须有，必须有音视频）
     * @param backCoverVideo  结尾视频（可为 null，可无音频）
     * @param taskId          任务 ID，用于生成唯一文件名
     * @return 合并后的视频文件路径，失败返回 null
     */
    public static String mergeVideo(String coverVideo, String video, String backCoverVideo, Long taskId) {
        if (video == null || video.trim().isEmpty()) {
            log.error("videoOverlay mergeVideo video is null or empty");
            return null;
        }
        if (coverVideo == null && backCoverVideo == null) {
            log.error("videoOverlay mergeVideo coverVideo and backCoverVideo is null");
            return null;
        }

        log.error("videoOverlay mergeVideo start, coverVideo: {}, video: {}, backCoverVideo: {}", coverVideo, video, backCoverVideo);
        String dir = System.getProperty("java.io.tmpdir");
        String outputDir = dir + imagePath + taskId + File.separator;
        Path outputDirPath = Paths.get(outputDir);
        try {
            Files.createDirectories(outputDirPath); // 确保输出目录存在
        } catch (IOException e) {
            log.error("无法创建输出目录: " + outputDir, e);
            return null;
        }

        String fileName = UUID.randomUUID().toString();

        String outputFile = outputDir + fileName + "_merged_output.mp4";

        List<String> inputs = new ArrayList<>();
        List<String> tempFiles = new ArrayList<>(); // 记录临时生成的文件，便于清理

        // 1. 处理 coverVideo：如果存在且无音频，添加静音音轨
        String finalCoverVideo = coverVideo;
        if (coverVideo != null && !coverVideo.trim().isEmpty()) {
            File coverFile = new File(coverVideo);
            if (!coverFile.exists()) {
                log.error("coverVideo 文件不存在: " + coverVideo);
                return null;
            }
            // 判断是否有音频流（简化：假设无音频，实际可用 ffprobe 检测）
            // 这里我们直接为 coverVideo 添加静音音轨以确保安全
            String silentCover = outputDir + fileName + "_silent_cover.mp4";
            if (addSilentAudioTrack(coverVideo, silentCover)) {
                finalCoverVideo = silentCover;
                tempFiles.add(silentCover);
            } else {
                log.warn("为 coverVideo 添加静音音轨失败，尝试继续...");
                // 仍使用原文件，可能失败
            }
        }

        // 2. 构建输入列表
        if (finalCoverVideo != null) {
            inputs.add(finalCoverVideo);
        }

        inputs.add(video);
        if (backCoverVideo != null && !backCoverVideo.trim().isEmpty()) {
            inputs.add(backCoverVideo);
        }

        // 3. 构建 FFmpeg 命令
//        List<String> cmd = new ArrayList<>();
//        cmd.add("/usr/bin/ffmpeg"); // 确保路径正确，或使用 "ffmpeg"
//
//        // 添加所有输入
//        for (String input : inputs) {
//            cmd.add("-i");
//            cmd.add(input);
//        }
//
//        // 构建 filter_complex: [0:v][0:a][1:v][1:a]... concat=n=x:v=1:a=1
//        StringBuilder filter = new StringBuilder();
//        for (int i = 0; i < inputs.size(); i++) {
//            filter.append(String.format("[%d:v][%d:a]", i, i));
//        }
//        filter.append(String.format("concat=n=%d:v=1:a=1[outv][outa]", inputs.size()));
//
//        cmd.add("-filter_complex");
//        cmd.add(filter.toString());
//
//        cmd.add("-map");
//        cmd.add("[outv]");
//        cmd.add("-map");
//        cmd.add("[outa]");
//
//        // 编码设置（可选）
//        cmd.add("-c:v");
//        cmd.add("libx264");
//        cmd.add("-c:a");
//        cmd.add("aac");
//        cmd.add("-strict");
//        cmd.add("experimental");


        List<String> cmd = new ArrayList<>();
        cmd.add("/usr/bin/ffmpeg");

// 添加所有输入
        for (String input : inputs) {
            cmd.add("-i");
            cmd.add(input);
        }

// 构建 filter_complex
        StringBuilder filter = new StringBuilder();

// 用于保存标准化后的流标签
        List<String> concatInputs = new ArrayList<>();

        for (int i = 0; i < inputs.size(); i++) {
            String videoLabel = String.format("v%d", i);
            // 对每个视频流设置 SAR=1:1，并打上标签
            filter.append(String.format("[%d:v]setsar=sar=1:1[%s];", i, videoLabel));
            concatInputs.add(String.format("[%s][%d:a]", videoLabel, i)); // [v0][0:a], [v1][1:a], ...
        }

// 拼接所有标准化后的流
        filter.append(String.join("", concatInputs))
                .append(String.format("concat=n=%d:v=1:a=1[outv][outa]", inputs.size()));

        cmd.add("-filter_complex");
        cmd.add(filter.toString());

// 映射输出
        cmd.add("-map");
        cmd.add("[outv]");
        cmd.add("-map");
        cmd.add("[outa]");

// 编码设置
        cmd.add("-c:v");
        cmd.add("libx264");
        cmd.add("-c:a");
        cmd.add("aac");
        cmd.add("-strict");
        cmd.add("experimental"); // 可选，aac 编码有时需要


        cmd.add(outputFile);

        // 4. 执行 FFmpeg 命令
        try {
            log.info("执行 FFmpeg 命令: " + String.join(" ", cmd));
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true); // 合并 stdout 和 stderr
            Process process = pb.start();

            // 读取输出日志
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("FFmpeg: " + line);
            }

            // 等待完成
            boolean finished = process.waitFor(20, TimeUnit.MINUTES); // 防止卡死
            int exitCode = process.exitValue();

            if (finished && exitCode == 0) {
                log.info("视频合并成功: " + outputFile);
                // 清理临时文件
                for (String tempFile : tempFiles) {
                    try {
                        Files.deleteIfExists(Paths.get(tempFile));
                    } catch (IOException e) {
                        log.warn("清理临时文件失败: " + tempFile, e);
                    }
                }
                return outputFile;
            } else {
                log.error("FFmpeg 执行失败，退出码: " + exitCode + ", 未在规定时间内完成: " + !finished);
            }

        } catch (IOException | InterruptedException e) {
            log.error("执行 FFmpeg 时发生异常", e);
        }

        // 清理临时文件（出错时也尝试清理）
        for (String tempFile : tempFiles) {
            try {
                Files.deleteIfExists(Paths.get(tempFile));
            } catch (IOException e) {
                log.warn("清理临时文件失败: " + tempFile, e);
            }
        }

        return null;
    }


    /**
     * 为视频添加静音音轨
     *
     * @param inputVideo  输入视频路径
     * @param outputVideo 输出带静音音轨的视频
     * @return 是否成功
     */
    private static boolean addSilentAudioTrack(String inputVideo, String outputVideo) {
        List<String> cmd = new ArrayList<>();
        cmd.add("/usr/bin/ffmpeg");
        cmd.add("-i");
        cmd.add(inputVideo);
        cmd.add("-f");
        cmd.add("lavfi");
        cmd.add("-i");
        cmd.add("anullsrc=channel_layout=stereo:sample_rate=44100");
        cmd.add("-shortest");  // 让音频长度匹配视频
        cmd.add("-c:v");
        cmd.add("copy");  // 视频流直接复制
        cmd.add("-c:a");
        cmd.add("aac");   // 音频编码为 AAC
        cmd.add(outputVideo);

        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("addSilentAudio: " + line);
            }

            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            log.error("为视频添加静音音轨失败: " + inputVideo, e);
            return false;
        }
    }

    public static String downLoadFromUrl(String u, String fileName, String dir) throws IOException {
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
}