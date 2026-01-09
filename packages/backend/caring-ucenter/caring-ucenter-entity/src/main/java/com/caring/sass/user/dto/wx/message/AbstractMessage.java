package com.caring.sass.user.dto.wx.message;


import com.caring.sass.user.dto.wx.message.recv.IMessage;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author xinzh
 */
@Getter
@Setter
@Accessors(chain = true)
public abstract class AbstractMessage implements IMessage, Serializable {
    protected long msgId;
    protected String toUserName;
    protected String fromUserName;
    protected long createTime;
    protected String msgType;
    protected String wxAppId;
    protected Long tenantId;
}
