package com.caring.sass.ai.entity.podcast;

public enum PodcastInputType {


    ARTICLE_URL("网页"),


    DOCUMENT("文件"),


    TEXT("文本");

    public final String name;

    PodcastInputType(String name) {
        this.name = name;
    }

}
