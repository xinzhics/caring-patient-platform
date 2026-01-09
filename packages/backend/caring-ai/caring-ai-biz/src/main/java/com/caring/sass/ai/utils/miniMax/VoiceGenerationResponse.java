package com.caring.sass.ai.utils.miniMax;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * @author leizhi
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoiceGenerationResponse {
    @JsonProperty("status")
    @JsonInclude(Include.NON_NULL)
    private Integer status; // 当前音频流状态

    @JsonProperty("trace_id")
    @JsonInclude(Include.NON_NULL)
    private String traceId; // 会话ID

    @JsonProperty("data")
    @JsonInclude(Include.NON_NULL)
    private Data data; // 简化后的 Data 类型字段
    @JsonProperty("subtitle_file")
    @JsonInclude(Include.NON_NULL)
    private String subtitleFile; // 字幕文件下载链接

    @JsonProperty("extra_info")
    @JsonInclude(Include.NON_NULL)
    private Map<String, Object> extraInfo; // 额外信息

    @JsonProperty("base_resp")
    private BaseResponse baseResp; // 基础响应信息

    public Integer getStatus() {
        return status;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSubtitleFile() {
        return subtitleFile;
    }

    public Map<String, Object> getExtraInfo() {
        return extraInfo;
    }

    public BaseResponse getBaseResp() {
        return baseResp;
    }

    public Data getData() {
        return data;
    }

    // 静态方法用于解析 JSON 响应
    public static VoiceGenerationResponse fromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, VoiceGenerationResponse.class);
    }

    @Override
    public String toString() {
        return "VoiceGenerationResponse{" +
                "status=" + status +
                ", traceId='" + traceId + '\'' +
                ", subtitleFile='" + subtitleFile + '\'' +
                ", extraInfo=" + extraInfo +
                ", data=" + data +
                ", baseResp=" + baseResp +
                '}';
    }

    // 简化后的内部类：Data
    public static class Data {
        @JsonProperty("audio")
        private String audio; // hex编码的音频

        @JsonProperty("status")
        private Integer status; // 状态

        @JsonProperty("ced")
        private String ced;

        // Getters
        public String getAudio() {
            return audio;
        }

        public Integer getStatus() {
            return status;
        }

        public String getCed() {
            return ced;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "audio='" + audio + '\'' +
                    ", status=" + status +
                    '}';
        }
    }

    // 内部类：基础响应信息
    public static class BaseResponse {
        @JsonProperty("status_code")
        private Integer statusCode;

        @JsonProperty("status_msg")
        private String statusMsg;

        // Getters
        public Integer getStatusCode() {
            return statusCode;
        }

        public String getStatusMsg() {
            return statusMsg;
        }

        @Override
        public String toString() {
            return "BaseResponse{" +
                    "statusCode=" + statusCode +
                    ", statusMsg='" + statusMsg + '\'' +
                    '}';
        }
    }

}
