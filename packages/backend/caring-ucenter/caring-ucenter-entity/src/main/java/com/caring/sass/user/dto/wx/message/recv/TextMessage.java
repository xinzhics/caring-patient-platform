package com.caring.sass.user.dto.wx.message.recv;


import com.caring.sass.user.dto.wx.message.AbstractMessage;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 文本消息
 *
 * @author xinzh
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TextMessage extends AbstractMessage implements IMessage {

    private static final long serialVersionUID = -1L;

    private String content;
}

