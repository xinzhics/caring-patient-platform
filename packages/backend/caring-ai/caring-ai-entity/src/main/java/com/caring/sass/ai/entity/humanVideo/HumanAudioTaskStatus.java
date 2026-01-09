package com.caring.sass.ai.entity.humanVideo;

import lombok.Getter;

@Getter
public enum HumanAudioTaskStatus {


    WAITING("WAITING", "等待中"),


    PROCESSING("PROCESSING", "制作中"),

    // 训练完成
    TRAINING_COMPLETED("TRAINING_COMPLETED", "训练完成"),

    // 训练失败
    TRAINING_FAILED("TRAINING_FAILED", "训练失败"),

    SUCCESS("SUCCESS", "制作成功");

    private String code;
    private String desc;

    HumanAudioTaskStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
