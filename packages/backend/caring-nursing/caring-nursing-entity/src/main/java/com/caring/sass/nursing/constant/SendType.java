package com.caring.sass.nursing.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 发送方式
 *
 * @author Evan Zhai
 */
@Getter
@AllArgsConstructor
public enum SendType {

    SINGLE_SHOT(1,"单发"),
    MASS_MESSAGE(2,"群发"),
    ;

    @JsonValue
    private final Integer type;

    private final String desc;
}
