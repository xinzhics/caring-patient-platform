package com.caring.sass.ai.entity.podcast;

public enum PodcastTaskStatus {

    /**
     * 输入文本
     */
    INPUT_TEXT,

    /**
     * 设置声音
     */
    SET_SOUND,

    /**
     * 创建对话
     */
    CREATE_DIALOGUE,


    CREATE_DIALOGUE_SUCCESS,


    CREATE_DIALOGUE_ERROR,


    /**
     * 对话未确认
     */
    DIALOGUE_NOT_CONFIRMED,

    /**
     * 创建音频
     */
    CREATE_AUDIO,


    CREATE_ERROR,

    /**
     * 合并音频
     */
    Merge_AUDIO,


    /**
     * 任务结束
     */
    TASK_FINISH;


}
