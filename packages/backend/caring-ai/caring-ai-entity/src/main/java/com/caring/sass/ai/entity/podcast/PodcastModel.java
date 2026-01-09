package com.caring.sass.ai.entity.podcast;

import lombok.Getter;

@Getter
public enum PodcastModel {


    // 速听精华
    QUICK_LISTENING_ESSENCE("QUICK_LISTENING_ESSENCE", "速听精华"),

    DEEP_DISCOVERY("DEEP_DISCOVERY", "深度发现");

    private String code;

    private String model;

    PodcastModel(String code, String model) {
        this.code = code;
        this.model = model;
    }
}
