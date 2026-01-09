package com.caring.sass.ai.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FaceWechatOrder {

    @NotNull
    Long userId;

    public FaceWechatOrder() {
    }
}
