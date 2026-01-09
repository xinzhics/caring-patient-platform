package com.caring.sass.ai.entity.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum BusinessCardMemberVersionEnum {


    BASIC_EDITION("基础版"),

    MEMBERSHIP_VERSION("会员版");

    private String desc;

}
