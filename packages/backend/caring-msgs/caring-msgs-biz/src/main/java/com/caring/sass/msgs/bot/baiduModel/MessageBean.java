package com.caring.sass.msgs.bot.baiduModel;

import java.util.List;

public class MessageBean {

    private String model;

    private boolean stream;

    private List<Message> messages;

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }


    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

}