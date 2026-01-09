package com.caring.sass.ai.entity.humanVideo;

/**
 * 一个因素同时 只能被一个 数字人制作任务使用。
 * 因此只有 空闲状态的 音色才能被使用。
 */
public enum VolcengineTimbreStatus {

    // 空闲
    FREE,

    // 训练中
    DURING_TRAINING,

    // 过期失效
    LOSE_EFFICACY

}
