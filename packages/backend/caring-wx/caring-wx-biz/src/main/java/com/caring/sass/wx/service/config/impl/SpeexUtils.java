package com.caring.sass.wx.service.config.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @ClassName SpeexUtils
 * @Description
 * @Author yangShuai
 * @Date 2021/7/20 14:34
 * @Version 1.0
 */
@Slf4j
public class SpeexUtils {


    public static String speex2wav(String speex, String wav) {
        String command = MessageFormat.format("speex2wav {0} {1}", speex, wav);
        return command;
    }

    public static void execCommand(String speex, String wav) {

        String command = speex2wav(speex, wav);
        Process process = null;
        Runtime runtime = Runtime.getRuntime();
        try {
            process = runtime.exec(command);
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("微信转换语音格式异常" + e.getMessage());
                process.destroy();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
