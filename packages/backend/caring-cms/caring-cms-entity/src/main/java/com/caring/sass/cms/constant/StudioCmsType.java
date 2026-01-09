package com.caring.sass.cms.constant;

public enum StudioCmsType {


    CMS_TYPE_TEXT("text", "文章"),

    CMS_TYPE_VOICE("voice", "声音"),

    CMS_TYPE_VIDEO("video", "视频");

    /**
     * 类型
     */
    private final String code;

    private final String info;

    StudioCmsType(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
