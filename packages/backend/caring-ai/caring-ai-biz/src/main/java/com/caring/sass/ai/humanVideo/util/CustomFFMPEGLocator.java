package com.caring.sass.ai.humanVideo.util;

import ws.schild.jave.process.ProcessLocator;
import ws.schild.jave.process.ProcessWrapper;

/**
 * 自定义ffmpeg执行文件的路径
 */
public class CustomFFMPEGLocator implements ProcessLocator {
    @Override
    public String getExecutablePath() {
        return "/usr/bin/ffmpeg";
    }

    @Override
    public ProcessWrapper createExecutor() {
        return new ProcessWrapper(getExecutablePath());
    }
}