package com.caring.sass.ai.entity.humanVideo;

import lombok.Getter;

@Getter
public enum HumanVideoMakeWay {


    PHOTO("photo", "照片"),

    VIDEO("video", "视频");

    private String code;

    private String name;

    HumanVideoMakeWay(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
