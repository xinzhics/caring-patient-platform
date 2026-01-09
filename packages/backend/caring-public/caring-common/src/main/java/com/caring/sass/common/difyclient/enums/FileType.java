package com.caring.sass.common.difyclient.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 文件类型
 */
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum FileType {
    /**
     * 文档类型
     */
    DOCUMENT("document", new String[]{"TXT", "MD", "MARKDOWN", "PDF", "HTML", "XLSX", "XLS", "DOCX", "CSV", "EML", "MSG", "PPTX", "PPT", "XML", "EPUB"}),

    /**
     * 图片类型
     */
    IMAGE("image", new String[]{"JPG", "JPEG", "PNG", "GIF", "WEBP", "SVG"}),

    /**
     * 音频类型
     */
    AUDIO("audio", new String[]{"MP3", "M4A", "WAV", "WEBM", "AMR"}),

    /**
     * 视频类型
     */
    VIDEO("video", new String[]{"MP4", "MOV", "MPEG", "MPGA"}),

    /**
     * 自定义类型
     */
    CUSTOM("custom");

    private final String value;

    @Getter
    private String[] fileExtensions;

    @JsonValue
    public String getValue() {
        return value;
    }

    public static FileType getByFileExtension(String fileExtension) {
        if (fileExtension == null) {
            return CUSTOM;
        }
        if (fileExtension.contains(".")) {
            fileExtension = fileExtension.substring(fileExtension.lastIndexOf(".") + 1);
        }
        for (FileType fileType : values()) {
            if (fileType.getFileExtensions() != null) {
                for (String extension : fileType.getFileExtensions()) {
                    if (extension.equalsIgnoreCase(fileExtension)) {
                        return fileType;
                    }
                }
            }
        }
        return CUSTOM;
    }
}
