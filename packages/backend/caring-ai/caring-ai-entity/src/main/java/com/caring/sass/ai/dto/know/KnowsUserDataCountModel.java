package com.caring.sass.ai.dto.know;

import lombok.Data;

@Data
public class KnowsUserDataCountModel {

    /**
     * 专业资料
     */
    private Integer academicMaterials;

    /**
     * 个人成就
     */
    private Integer personalAchievements;

    /**
     * 病例库
     */
    private Integer caseDatabase;

    /**
     * 日常收集
     */
    private Integer dailyCollection;


    /**
     * 科普创作视频
     */
    private Integer articleVideo;

    /**
     * 科普创作 文章
     */
    private Integer articleText;

    /**
     * 科普创作 播客
     */
    private Integer articlePodcast;

    /**
     * 融媒体
     */
    private Integer articleConvergenceMedia;


    /**
     * 文献解读 文字解读
     */
    private Integer textualText;

    /**
     * 文献解读 语音解读
     */
    private Integer textualAudio;


    /**
     * 文献解读 PPT
     */
    private Integer textualPpt;


}
