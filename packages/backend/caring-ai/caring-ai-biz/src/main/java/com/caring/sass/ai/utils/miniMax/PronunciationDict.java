package com.caring.sass.ai.utils.miniMax;

import java.util.HashMap;
import java.util.Map;

public class PronunciationDict {
    private String tone;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("tone", tone);
        return map;
    }
}
