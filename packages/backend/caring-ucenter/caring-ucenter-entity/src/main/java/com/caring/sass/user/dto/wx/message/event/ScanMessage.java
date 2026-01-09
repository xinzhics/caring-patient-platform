package com.caring.sass.user.dto.wx.message.event;


import com.caring.sass.user.dto.wx.message.AbstractMessage;
import com.caring.sass.user.dto.wx.message.recv.IMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ScanMessage extends AbstractMessage implements IMessage {

    private static final long serialVersionUID = -1L;

    private String eventKey;
    private String ticket;

    public String getEvent() {
        return "SCAN";
    }
}
