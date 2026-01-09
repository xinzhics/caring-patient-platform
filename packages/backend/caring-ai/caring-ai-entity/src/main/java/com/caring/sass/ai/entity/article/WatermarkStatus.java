package com.caring.sass.ai.entity.article;

public enum WatermarkStatus {


    /**
     * 用户只上传了形象，没有使用
     */
    NO_USE,

    /**
     * 用户需要使用形象，等待对形象水印处理。
     */
    WAITING,


    /**
     * 水印处理中
     */
    PROCESSING,


    /**
     * 水印处理成功。
     */
    SUCCESS,

    /**
     * 水印处理取消。
     */
    CANCELLED,

    /**
     * 水印处理失败。
     */
    FAILED;



}
