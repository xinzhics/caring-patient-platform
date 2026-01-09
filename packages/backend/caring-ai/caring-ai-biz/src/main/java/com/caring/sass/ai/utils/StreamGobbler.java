package com.caring.sass.ai.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

// 辅助类：用于异步读取并处理输出流
public class StreamGobbler extends Thread {
    private final InputStream inputStream;
    private final java.util.function.Consumer<String> consumer;

    public StreamGobbler(InputStream inputStream, java.util.function.Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                consumer.accept(line);
            }
        } catch (IOException ex) {
            consumer.accept("流读取异常: " + ex.getMessage());
        }
    }
}