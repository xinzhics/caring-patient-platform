package com.caring.sass.msgs.utils;

import com.caring.sass.common.constant.MediaType;

public class MediaTypeUtils {



    /**
     * 根据枚举类型。返回消息提醒时的提醒内容
     * @param chatType
     * @param content
     * @return
     */
    public static String getImContentByType(MediaType chatType, String content) {
        switch (chatType) {
            case text: {
                break;
            }
            case cms: {
                content = I18nUtils.getMessage("chatCms"); //"您收到一篇文章"
                break;
            }
            case remind: {
                content = I18nUtils.getMessage("chatRemind"); // "您收到一条提醒";
                break;
            }
            case file: {
                content = I18nUtils.getMessage("chatFile"); // "您收到一个文件";
                break;
            }
            case image: {
                content = I18nUtils.getMessage("chatImage"); // "您收到一张图片";
                break;
            }
            case voice: {
                content = I18nUtils.getMessage("chatVoice"); // "您收到一条语音";
                break;
            }
            case video: {
                content = I18nUtils.getMessage("chatVideo"); // "您收到一个视频消息";
                break;
            }
            default: {
                content = I18nUtils.getMessage("chatOther"); // "您收到一条消息";
            }
        }
        return content;
    }
    
}
