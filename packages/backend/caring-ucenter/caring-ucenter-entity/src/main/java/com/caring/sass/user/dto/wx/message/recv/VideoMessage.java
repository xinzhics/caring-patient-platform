package com.caring.sass.user.dto.wx.message.recv;


import com.caring.sass.user.dto.wx.message.AbstractMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author xinzh
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class VideoMessage extends AbstractMessage implements IMessage {

    private static final long serialVersionUID = -1L;

    private String mediaId;
    private String thumbMediaId;

    public String getMediaId() {
        return this.mediaId;
    }

}
