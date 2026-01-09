package com.caring.sass.ai.entity.textual;

import lombok.Getter;

@Getter
public enum TextualConsumptionType {

    NEW_USER_REGISTER("新用户注册"),
    // 购买能量豆包
    ENERGY_RECHARGE("购买能量豆包"),

    // 会员充值赠送
    MEMBERSHIP_RECHARGE("会员充值赠送"),

    // 播客解读
    PODCAST_INTERPRETATION("播客解读"),

    // 图文创作
    IMAGE_TEXT_CREATION("文字解读"),

    IMAGE_TEXT_CREATION_RESTART("文字解读重新生成"),

    PPT_OUTLINE_CREATION("重新生成ppt大纲"),

    // 播客制作
    POADCAST_CREATION("语音解读"),
    PODCAST_REFUND("语音解读退费"),

    // 自定义音色克隆
    CUSTOM_VOICE_CREATION("音色克隆"),
    CUSTOM_VOICE_CREATION_REFUND("音色克隆退费"),

    // 视频创作
    PPT_CREATION("生成课件"),
    ;

    private String desc;

    TextualConsumptionType(String desc) {
        this.desc = desc;
    }


}
