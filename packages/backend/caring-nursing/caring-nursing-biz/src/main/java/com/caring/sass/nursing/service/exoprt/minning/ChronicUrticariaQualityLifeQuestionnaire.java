package com.caring.sass.nursing.service.exoprt.minning;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @className: ChronicUrticariaQualityLifeQuestionnaire
 * @author: 杨帅
 * @date: 2023/11/23
 *
 * 慢性荨麻疹生活质量问卷
 */
@Data
@ApiModel("慢性荨麻疹生活质量问卷")
public class ChronicUrticariaQualityLifeQuestionnaire {

    @ApiModelProperty("评估日期")
    private LocalDate aaluationDate;

    @ApiModelProperty("评估日期和时间")
    private LocalDateTime aaluationDateTime;

    @ApiModelProperty("自我评价")
    private UrticariaActivityScoreSelfEvaluation scoreSelfEvaluation;

    @ApiModelProperty("有随访")
    private Boolean hasFollowUp = false;

    /**
     * 瘙痒
     */
    private Integer itch;

    /**
     * 风团
     */
    private Integer wheal;

    /**
     * 眼睛肿胀
     */
    private Integer swollenEyes;


    /**
     * 口唇肿胀
     */
    private Integer lipSwelling;


    /**
     * 工作
     */
    private Integer work;

    /**
     * 运动
     */
    private Integer motion;

    /**
     * 睡眠
     */
    private Integer sleep;

    /**
     * 闲暇
     */
    private Integer leisure;

    /**
     * 社交
     */
    private Integer socialize;

    /**
     * 饮食
     */
    private Integer diet;

    /**
     * 入睡困难
     */
    private Integer difficultyFallingAsleep;

    /**
     * 夜间醒来。
     */
    private Integer wakeUpAtNight;

    /**
     * 是否晚上睡眠不佳而白天困乏
     */
    private Integer daytimeFatigue;

    /**
     * 注意力难以集中
     */
    private Integer difficultyConcentrating;

    /**
     * 感觉紧张
     */
    private Integer feelNervous;

    /**
     * 情绪低落
     */
    private Integer depression;

    /**
     * 限制饮食
     */
    private Integer restrictedDiet;

    /**
     * 荨麻疹症状而感到困扰
     */
    private Integer feelingTroubledUrticaria;

    /**
     * 公共场所感到尴尬
     */
    private Integer feelingEmbarrassedPublicPlaces;

    /**
     * 使用化妆品
     */
    private Integer usingCosmetics;

    /**
     * 限制了您对服装类型的选择
     */
    private Integer restrictClothingChoices;

    /**
     * 限制体育运动
     */
    private Integer restrictSportsActivities;

    /**
     * 荨麻疹不良反应影响
     */
    private Integer adverseEffectsUrticaria;


}
