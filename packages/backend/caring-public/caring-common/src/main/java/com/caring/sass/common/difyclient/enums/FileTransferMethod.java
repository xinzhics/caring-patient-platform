package com.caring.sass.common.difyclient.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件传输方式
 */
@Getter
@AllArgsConstructor
public enum FileTransferMethod {
    /**
     * 远程 URL
     */
    REMOTE_URL("remote_url"),

    /**
     * 本地文件
     */
    LOCAL_FILE("local_file");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
