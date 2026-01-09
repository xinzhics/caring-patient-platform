package com.caring.sass.ai.entity.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public enum ConsumptionType {

    NEW_USER_REGISTER("新用户注册"),
    // 购买能量豆包
    ENERGY_RECHARGE("购买能量豆包"),

    // 会员充值赠送
    MEMBERSHIP_RECHARGE("会员充值赠送"),

    // 图文创作
    IMAGE_TEXT_CREATION("深度科普文章"),

    XIAOHONGSHU_COPY_CREATION("社交媒体文案"),

    VIDEO_BROADCAST_CREATION("短视频口播稿"),

    // 播客制作
    POADCAST_CREATION("创作播客"),

    podcastQuickListeningEssence("AI播客-速听精华"),

    podcastDeepDiscovery("AI播客-深度发现"),

    PODCAST_REFUND("播客退费"),

    // 数字人形象制作
    HUMAN_VIDEO_CREATION("数字人形象创作"),

    // 自定义音色克隆
    CUSTOM_VOICE_CREATION("音色克隆"),

    CUSTOM_VOICE_CREATION_REFUND("音色克隆退费"),

    // 视频创作
    VIDEO_CREATION("视频创作"),

    VIDEO_CREATION_REFUND("视频创作退费"),
    ;

    private String desc;

    ConsumptionType(String desc) {
        this.desc = desc;
    }


}
