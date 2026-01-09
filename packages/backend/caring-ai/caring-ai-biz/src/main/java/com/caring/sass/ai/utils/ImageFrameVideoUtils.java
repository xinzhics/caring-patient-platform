package com.caring.sass.ai.utils;

import java.io.*;

/**
 * 将一个图片转成 视频
 */
public class ImageFrameVideoUtils {




    // 方案1：降低帧率来缩短显示时间
    public static String createShortFrameVideo(String imagePath, String outputVideo, double fps) throws Exception {

        return createShortFrameVideo(imagePath, outputVideo, 1, fps);
        // 这样生成的视频中，单帧显示时间约为1秒
    }


    /**
     * 使用 FFmpeg 命令将图片转换为指定时长的单帧视频
     * @param imagePath 输入图片的路径
     * @param outputVideo 输出视频的路径
     * @param durationSeconds 视频时长（秒）
     * @throws InterruptedException
     */
    public static String createShortFrameVideo(String imagePath, String outputVideo, int durationSeconds, double fps)
            throws IOException, InterruptedException {

        // FFmpeg 命令
        ProcessBuilder pb = new ProcessBuilder(
                "/usr/bin/ffmpeg",
                "-loop", "1",
                "-i", imagePath,
                "-c:v", "libx264",
                "-t", String.valueOf(durationSeconds),
                "-pix_fmt", "yuv420p",
                "-vf", "fps=" + fps,
                "-y",  // 自动覆盖输出文件
                outputVideo
        );

        // 可选：打印执行命令，便于调试
        System.out.println("执行命令: " + String.join(" ", pb.command()));

        // 启动进程
        Process process = pb.start();

        // 🔥 关键：必须读取 stdout 和 stderr，防止缓冲区阻塞
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), System.err::println);

        // 启动两个线程读取输出流
        outputGobbler.start();
        errorGobbler.start();

        // 等待进程结束
        int exitCode = process.waitFor();

        // 等待输出流读取完成
        outputGobbler.join();
        errorGobbler.join();

        if (exitCode != 0) {
            throw new IOException("FFmpeg 命令执行失败，退出码: " + exitCode);
        }

        // 检查输出文件是否存在
        File outFile = new File(outputVideo);
        if (!outFile.exists() || outFile.length() == 0) {
            throw new IOException("视频文件生成失败或为空: " + outputVideo);
        }

        return outputVideo;
    }








}
