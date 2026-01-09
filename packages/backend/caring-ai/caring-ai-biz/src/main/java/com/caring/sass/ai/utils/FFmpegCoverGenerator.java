package com.caring.sass.ai.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FFmpegCoverGenerator {

    public static boolean generateCover(String inputPath, String outputPath) {
        try {
            // 构建 FFmpeg 命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "/usr/bin/ffmpeg",
                    "-i", inputPath,
                    "-ss", "0",             // 从第 0 秒开始截取
                    "-vframes", "1",         // 只取一帧
                    outputPath              // 输出路径
            );
            processBuilder.redirectErrorStream(true); // 合并错误流和标准流
            // 启动进程
            Process process = processBuilder.start();

            // 读取 FFmpeg 输出日志（可选）
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // 等待 FFmpeg 执行完成
            int exitCode = process.waitFor();
            System.out.println("FFmpeg 封面生成完成，退出码: " + exitCode);

            if (exitCode == 0) {
                System.out.println("封面生成成功！");
                return true;
            } else {
                System.err.println("封面生成失败，请检查输入文件或 FFmpeg 安装。");
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



}