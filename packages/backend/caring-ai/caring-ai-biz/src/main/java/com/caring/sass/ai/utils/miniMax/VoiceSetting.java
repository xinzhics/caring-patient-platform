package com.caring.sass.ai.utils.miniMax;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class VoiceSetting {
    private String voiceId;
    private int speed;
    private int volume;
    private int pitch;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("voice_id", voiceId);
        map.put("speed", speed);
        map.put("volume", volume);
        map.put("pitch", pitch);
        return map;
    }

}
