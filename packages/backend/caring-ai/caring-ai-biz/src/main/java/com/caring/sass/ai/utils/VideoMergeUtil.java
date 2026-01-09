package com.caring.sass.ai.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 视频合成
 */
@Slf4j
public class VideoMergeUtil {

    public static final String imagePath = File.separator + "saas" + File.separator + "tempfile" + File.separator;


    // FFmpeg 路径（请确保系统中可用）
    private static final String FFMPEG_PATH = "/usr/bin/ffmpeg";
    private static final String FFPROBE_PATH = "/usr/bin/ffprobe";

    public static String mergeVideo(String coverVideo, String video, String backCoverVideo, Long taskId) {
        if (video == null || video.trim().isEmpty()) {
            log.error("videoOverlay mergeVideo video is null or empty");
            return null;
        }
        if (coverVideo == null && backCoverVideo == null) {
            log.error("videoOverlay mergeVideo coverVideo and backCoverVideo is null");
            return null;
        }

        log.info("videoOverlay mergeVideo start, coverVideo: {}, video: {}, backCoverVideo: {}", coverVideo, video, backCoverVideo);

        String dir = System.getProperty("java.io.tmpdir");
        String outputDir = dir + imagePath + taskId + File.separator;
        Path outputDirPath = Paths.get(outputDir);
        try {
            Files.createDirectories(outputDirPath);
        } catch (IOException e) {
            log.error("无法创建输出目录: " + outputDir, e);
            return null;
        }

        String fileName = UUID.randomUUID().toString();
        String outputFile = outputDir + fileName + "_merged_output.mp4";

        List<String> inputs = new ArrayList<>();
        List<String> tempFiles = new ArrayList<>();

        // 添加输入
        if (coverVideo != null && !coverVideo.trim().isEmpty()) {
            File f = new File(coverVideo);
            if (!f.exists()) {
                log.error("coverVideo 文件不存在: " + coverVideo);
                return null;
            }
            inputs.add(coverVideo);
        }

        inputs.add(video);

        if (backCoverVideo != null && !backCoverVideo.trim().isEmpty()) {
            File f = new File(backCoverVideo);
            if (!f.exists()) {
                log.error("backCoverVideo 文件不存在: " + backCoverVideo);
                return null;
            }
            inputs.add(backCoverVideo);
        }

        // 获取主视频（video）的参数作为参考标准
        VideoAudioParams refParams = getVideoAudioParams(video);
        if (refParams == null) {
            log.error("无法获取主视频参数，合并失败");
            return null;
        }

        log.info("以主视频为标准: 分辨率={}x{}, SAR={}, FPS={}, 音频={}Hz, {}声道",
                refParams.width, refParams.height, refParams.sar, refParams.fps,
                refParams.sampleRate, refParams.channels);

        // 构建 filter_complex
        StringBuilder filter = new StringBuilder();
        List<String> mappedStreams = new ArrayList<>();

        for (int i = 0; i < inputs.size(); i++) {
            String input = inputs.get(i);

            // === 视频处理：缩放 + 设置 SAR + 帧率 ===
            filter.append(String.format(
                    "[%d:v]scale=%d:%d,setsar=%s,fps=%s[v%d]; ",
                    i, refParams.width, refParams.height, refParams.sar, refParams.fps, i
            ));

            // === 音频处理 ===
            VideoAudioParams inputParams = getVideoAudioParams(input);
            boolean hasAudio = inputParams != null && inputParams.hasAudio;

            if (hasAudio) {
                // 有音频：标准化
                filter.append(String.format(
                        "[%d:a]aformat=sample_rates=%d:channel_layouts=%s[a%d]; ",
                        i, refParams.sampleRate,
                        refParams.channels == 2 ? "stereo" : "mono",
                        i
                ));
            } else {
                // 无音频：生成静音，时长与视频一致
                String duration = getVideoDuration(input);
                if (duration == null) duration = "10"; // fallback

                filter.append(String.format(
                        "anullsrc=r=%d:cl=%s:d=%s[a%d]; ",
                        refParams.sampleRate,
                        refParams.channels == 2 ? "stereo" : "mono",
                        duration, i
                ));
            }

            mappedStreams.add("[v" + i + "]");
            mappedStreams.add("[a" + i + "]");
        }

        // 拼接所有流
        filter.append(String.join("", mappedStreams))
                .append(String.format("concat=n=%d:v=1:a=1[outv][outa]", inputs.size()));

        // 构建命令
        List<String> cmd = new ArrayList<>();
        cmd.add(FFMPEG_PATH);

        for (String input : inputs) {
            cmd.add("-i");
            cmd.add(input);
        }

        cmd.add("-filter_complex");
        cmd.add(filter.toString());

        cmd.add("-map");
        cmd.add("[outv]");
        cmd.add("-map");
        cmd.add("[outa]");

        cmd.add("-c:v");
        cmd.add("libx264");
        cmd.add("-c:a");
        cmd.add("aac");
        cmd.add("-strict");
        cmd.add("experimental");
        cmd.add(outputFile);

        // 执行命令
        try {
            log.info("执行 FFmpeg 命令: " + String.join(" ", cmd));
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("FFmpeg: " + line);
            }

            boolean finished = process.waitFor(20, TimeUnit.MINUTES);
            int exitCode = process.exitValue();

            if (finished && exitCode == 0) {
                log.info("视频合并成功: " + outputFile);
                cleanupTempFiles(tempFiles);
                return outputFile;
            } else {
                log.error("FFmpeg 执行失败，退出码: " + exitCode + ", 超时: " + !finished);
            }
        } catch (Exception e) {
            log.error("执行 FFmpeg 时发生异常", e);
        } finally {
            cleanupTempFiles(tempFiles);
        }

        return null;
    }

    // 清理临时文件
    private static void cleanupTempFiles(List<String> tempFiles) {
        for (String file : tempFiles) {
            try {
                Files.deleteIfExists(Paths.get(file));
            } catch (IOException e) {
                log.warn("清理临时文件失败: " + file, e);
            }
        }
    }

    // 获取视频时长（秒）
    private static String getVideoDuration(String videoPath) {
        return runCmdAndReadOutput(
                FFPROBE_PATH,
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "csv=p=0",
                videoPath
        );
    }

    // 获取视频和音频参数
    public static VideoAudioParams getVideoAudioParams(String videoPath) {
        String cmd = String.format(
                "%s -v error -select_streams v:0 -show_entries stream=width,height,sar,r_frame_rate,avg_frame_rate,codec_type,channels,sample_rate " +
                        "-of json \"%s\"",
                FFPROBE_PATH, videoPath
        );

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
            StringBuilder output = new StringBuilder();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            process.waitFor();

            return VideoAudioParams.parseFromFFprobeJson(output.toString());
        } catch (Exception e) {
            log.warn("获取视频参数失败: " + videoPath, e);
            return null;
        }
    }

    // 执行命令并读取第一行输出
    private static String runCmdAndReadOutput(String... cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.destroy();
            return line != null ? line.trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // 封装视频音频参数


    // 简单的有理数解析器

}