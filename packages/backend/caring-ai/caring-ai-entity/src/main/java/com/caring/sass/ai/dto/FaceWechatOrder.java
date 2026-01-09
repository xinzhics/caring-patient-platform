package com.caring.sass.ai.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class FaceWechatOrder {

    @NotNull
    Long userId;

    private List<Long> typeIds;

    public FaceWechatOrder() {
    }
}
