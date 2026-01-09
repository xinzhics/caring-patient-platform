package com.caring.sass.ai.utils.miniMax;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AudioSetting {
    private String format;
    private int sampleRate;
    private int bitrate;
    private int channels;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("format", format);
        map.put("sample_rate", sampleRate);
        map.put("bitrate", bitrate);
        map.put("channels", channels);
        return map;
    }
}
