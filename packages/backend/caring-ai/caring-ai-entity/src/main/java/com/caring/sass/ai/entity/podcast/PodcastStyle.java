package com.caring.sass.ai.entity.podcast;

public enum PodcastStyle {

    DEFAULT_STYLE("默认风格"),
    COMMENTS_CURRENT_EVENTS("时事评论"),
    NEWSCAST("新闻播报"),
    INTERVIEW("访谈"),
    TALK_SHOW("脱口秀"),
    cross_talk("相声"),
    Literature_Interpretation("文献解读"),
    ;


    public final String name;


    PodcastStyle(String name) {
        this.name = name;
    }


}
