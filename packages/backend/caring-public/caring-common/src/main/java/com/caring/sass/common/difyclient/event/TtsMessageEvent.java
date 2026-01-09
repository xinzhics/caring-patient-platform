package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * TTS 音频流事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TtsMessageEvent extends BaseMessageEvent {

    /**
     * 语音合成之后的音频块使用 Base64 编码之后的文本内容
     */
    @JsonProperty("audio")
    private String audio;
}
