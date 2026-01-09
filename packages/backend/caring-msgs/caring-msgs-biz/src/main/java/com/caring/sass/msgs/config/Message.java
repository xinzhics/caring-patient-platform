package com.caring.sass.msgs.config;

import io.swagger.client.model.Msg;

public class Message extends Msg {
    public static final String MSG_TYPE_TXT = "txt";
    public static final String MSG_TYPE_IMG = "img";
    public static final String MSG_TYPE_AUDIO = "audio";
    public static final String MSG_TYPE_VIDEO = "video";
    public static final String MSG_TYPE_CMD = "cmd";
    public static final Integer TXT = Integer.valueOf(0);
    public static final Integer IMG = Integer.valueOf(1);
    public static final Integer AUDIO = Integer.valueOf(2);
    public static final Integer VIDEO = Integer.valueOf(3);
    public static final Integer CMD = Integer.valueOf(4);
    public static final String target_type_users = "users";
    public static final String target_type_chatgroups = "chatgroups";
    public static final String target_type_chatrooms = "chatrooms";
    private String id;
    private String projectId;
    private String file_length;
    private String type;
    private String fileurl;
    private String url;
    private String filename;
    private String secret;
    private String to;
    private String width;
    private String height;
    private String filetype;
    private String errorCode;
    private String JPushAppKey;
    protected String JPushMasterSecret;

    public String getJPushAppKey() {
        return this.JPushAppKey;
    }

    public void setJPushAppKey(String JPushAppKey) {
        this.JPushAppKey = JPushAppKey;
    }

    public String getJPushMasterSecret() {
        return this.JPushMasterSecret;
    }

    public void setJPushMasterSecret(String JPushMasterSecret) {
        this.JPushMasterSecret = JPushMasterSecret;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getFileurl() {
        return this.fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile_length() {
        return this.file_length;
    }

    public void setFile_length(String file_length) {
        this.file_length = file_length;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getFiletype() {
        return this.filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
