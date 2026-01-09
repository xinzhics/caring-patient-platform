package com.caring.sass.ai.entity.humanVideo;

import lombok.Getter;

/**
 * 数字人视频制作任务状态
 */
@Getter
public enum HumanVideoTaskStatus {


    SUBMIT_ING("SUBMIT_ING", "提交资料中"),

    NOT_START("NOT_START", "未开始"),

    // 等待火山形象
    WAIT_IMAGE("WAIT_IMAGE", "等待火山形象"),

    MAKE_IMAGE("MAKE_IMAGE", "正在做火山形象"),

    WAIT_AUDIO("WAIT_AUDIO", "等待做音频"),

    MAKE_AUDIO("MAKE_AUDIO", "正在做音频"),

    WAIT_VIDEO("WAIT_VIDEO", "等待做视频"),

    MAKE_VIDEO("MAKE_VIDEO", "制作视频"),

    WAIT_DOWNLOAD_VIDEO("WAIT_DOWNLOAD_VIDEO", "等待下载视频"),

    SUCCESS("SUCCESS", "制作成功"),

    FAIL("FAIL", "制作失败");


    private String code;


    private String desc;


    HumanVideoTaskStatus(String code, String desc)
    {
        this.code = code;
        this.desc = desc;
    }


}
