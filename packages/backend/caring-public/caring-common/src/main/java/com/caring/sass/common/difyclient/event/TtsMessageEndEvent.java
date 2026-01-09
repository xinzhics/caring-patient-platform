package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * TTS 音频流结束事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TtsMessageEndEvent extends BaseMessageEvent {

    /**
     * 结束事件是没有音频的，所以这里是空字符串
     */
    @JsonProperty("audio")
    private String audio;
}
