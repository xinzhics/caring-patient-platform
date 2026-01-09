package com.caring.sass.ai.entity.podcast;

public enum PodcastAudioTaskStatus {

    /**
     * 等待提交到火山
     */
    WAIT_SUBMIT,

    /**
     * 音频制作中
     */
    CREATE_AUDIO_ING,

    /**
     * 制作音频失败
     */
    CREATE_AUDIO_ERROR,

    /**
     * 音频下载成功
     */
    DOWNLOAD_AUDIO_SUCCESS;




}
