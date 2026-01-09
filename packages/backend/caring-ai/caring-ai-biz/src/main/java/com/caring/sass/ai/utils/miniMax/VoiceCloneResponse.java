package com.caring.sass.ai.utils.miniMax;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 声音克隆参数接受
 *
 * @author leizhi
 */
public class VoiceCloneResponse {

    @JsonProperty("input_sensitive")
    private boolean inputSensitive;

    @JsonProperty("input_sensitive_type")
    private int inputSensitiveType;

    @JsonProperty("demo_audio")
    private String demoAudio;

    @JsonProperty("base_resp")
    private BaseResp baseResp;

    // Getters and Setters
    public boolean isInputSensitive() {
        return inputSensitive;
    }

    public void setInputSensitive(boolean inputSensitive) {
        this.inputSensitive = inputSensitive;
    }

    public int getInputSensitiveType() {
        return inputSensitiveType;
    }

    public void setInputSensitiveType(int inputSensitiveType) {
        this.inputSensitiveType = inputSensitiveType;
    }

    public String getDemoAudio() {
        return demoAudio;
    }

    public void setDemoAudio(String demoAudio) {
        this.demoAudio = demoAudio;
    }

    public BaseResp getBaseResp() {
        return baseResp;
    }

    public void setBaseResp(BaseResp baseResp) {
        this.baseResp = baseResp;
    }

    public static class BaseResp {
        @JsonProperty("status_code")
        private int statusCode;

        @JsonProperty("status_msg")
        private String statusMsg;

        // Getters and Setters
        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusMsg() {
            return statusMsg;
        }

        public void setStatusMsg(String statusMsg) {
            this.statusMsg = statusMsg;
        }
    }

    @Override
    public String toString() {
        return "VoiceCloneResponse{" +
                "inputSensitive=" + inputSensitive +
                ", inputSensitiveType=" + inputSensitiveType +
                ", demoAudio='" + demoAudio + '\'' +
                ", baseResp=" + baseResp +
                '}';
    }
}
