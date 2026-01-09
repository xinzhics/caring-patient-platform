package com.caring.sass.ai.entity.textual;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

@Getter
@ApiModel("文献解读通知消息类型")
public enum TextualNoticeType {


    // 充值消息  (充值会员，充值能量豆)
    recharge("充值类消息"),
    // 能量豆消费消息
    Consumer_News("消费类消息"),
    // 会员即将到期消息
    Membership_expiration("会员到期消息"),
    // 数字人形象生成消息
    produceTaskComplete("生产任务完成消息");


    private String value;

    TextualNoticeType(String value) {
        this.value = value;
    }






}
