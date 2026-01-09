package com.caring.sass.wx.constant;

public final class WeiXinConstants {
    public enum RecvMessage {
        text, image, link, location, voice, video, event, CLICK;
    }

    public enum FileType {
        jpeg, mp3, amr, mp4;
    }

    public enum MediaType {
        text, image, voice, video, cmd, thumb, file;
    }

    public enum MaterialType {
        news, voice, image, video, thumb
    }

    public enum EventMessage {
        subscribe,
        unsubscribe,
        SCAN,
        LOCATION,
        CLICK,
        VIEW,
        TEMPLATESENDJOBFINISH;
    }
}

