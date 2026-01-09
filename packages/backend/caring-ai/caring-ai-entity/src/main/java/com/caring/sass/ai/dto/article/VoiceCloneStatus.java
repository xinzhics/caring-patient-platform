package com.caring.sass.ai.dto.article;

import lombok.Getter;

@Getter
public enum VoiceCloneStatus {

    WAITING_TO_EXTRACT_AUDIO("waiting_to_extract_audio", "等待提取音频"),

    WAITING("waiting", "等待中"),

    SUCCESS("success", "成功"),

    FAILED("failed", "失败");

    private String code;
    private String name;

    VoiceCloneStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }



}
