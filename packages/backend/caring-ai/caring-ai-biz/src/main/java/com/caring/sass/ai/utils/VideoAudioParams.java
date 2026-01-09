package com.caring.sass.ai.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class VideoAudioParams {
    int width, height;
    double fps = 25.0;  // 设置默认帧率
    String sar = "1:1";
    int sampleRate = 44100;
    int channels = 2;
    boolean hasAudio = false;  // 默认无音频

    static VideoAudioParams parseFromFFprobeJson(String json) {
        try {
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(json, JsonObject.class);
            JsonArray streams = root.getAsJsonArray("streams");

            VideoAudioParams p = new VideoAudioParams();
            boolean foundVideo = false, foundAudio = false;

            for (JsonElement element : streams) {
                JsonObject stream = element.getAsJsonObject();
                String codecType = stream.get("codec_type").getAsString();

                if ("video".equals(codecType) && !foundVideo) {
                    p.width = stream.get("width").getAsInt();
                    p.height = stream.get("height").getAsInt();

                    // 处理帧率
                    if (stream.has("r_frame_rate")) {
                        p.fps = Rational.parse(stream.get("r_frame_rate").getAsString());
                    }
                    // 设置默认帧率
                    if (p.fps <= 0) {
                        p.fps = 25.0;
                    }

                    // 处理SAR
                    if (stream.has("sample_aspect_ratio")) {
                        String sar = stream.get("sample_aspect_ratio").getAsString();
                        if (!sar.equals("0:1") && !sar.equals("N/A") && !sar.isEmpty()) {
                            p.sar = sar;
                        }
                    }

                    foundVideo = true;
                } else if ("audio".equals(codecType) && !foundAudio) {
                    if (stream.has("sample_rate")) {
                        p.sampleRate = stream.get("sample_rate").getAsInt();
                    }
                    if (stream.has("channels")) {
                        p.channels = stream.get("channels").getAsInt();
                    }
                    p.hasAudio = true;
                    foundAudio = true;
                }
            }

            // 如果没有找到音频流，使用默认值但标记为无音频
            if (!foundAudio) {
                p.hasAudio = false;
            }

            return foundVideo ? p : null;
        } catch (Exception e) {
            log.warn("解析 ffprobe JSON 失败", e);
            return null;
        }
    }

}

