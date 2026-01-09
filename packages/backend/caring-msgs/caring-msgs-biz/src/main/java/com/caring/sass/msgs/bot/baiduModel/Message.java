package com.caring.sass.msgs.bot.baiduModel;

import java.util.List;

public class Message {

    private String version;
    private long created;
    private String role;
    private List<Content> content;

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getCreated() {
        return created;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public List<Content> getContent() {
        return content;
    }

}
