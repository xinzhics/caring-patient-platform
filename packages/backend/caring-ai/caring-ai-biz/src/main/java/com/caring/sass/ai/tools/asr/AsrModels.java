package com.caring.sass.ai.tools.asr;

import com.google.gson.annotations.SerializedName;

public class AsrModels {

    public static class ApiResponse {
        @SerializedName("request_id")
        private String requestId;
        private Output output;

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public Output getOutput() {
            return output;
        }

        public void setOutput(Output output) {
            this.output = output;
        }
    }

    public static class Output {
        @SerializedName("task_id")
        private String taskId;
        @SerializedName("task_status")
        private String taskStatus;
        private String message;
        private Result result;

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getTaskStatus() {
            return taskStatus;
        }

        public void setTaskStatus(String taskStatus) {
            this.taskStatus = taskStatus;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }
    }

    public static class Result {
        private String text;
        private String content;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class SubmitRequest {
        private String model;
        private Input input;
        private Parameters parameters;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Input getInput() {
            return input;
        }

        public void setInput(Input input) {
            this.input = input;
        }

        public Parameters getParameters() {
            return parameters;
        }

        public void setParameters(Parameters parameters) {
            this.parameters = parameters;
        }
    }

    public static class Input {
        @SerializedName("file_url")
        private String fileUrl;

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }

    public static class Parameters {
        @SerializedName("channel_id")
        private Integer[] channelId;
        @SerializedName("enable_itn")
        private Boolean enableItn;
        private String language;

        public Integer[] getChannelId() {
            return channelId;
        }

        public void setChannelId(Integer[] channelId) {
            this.channelId = channelId;
        }

        public Boolean getEnableItn() {
            return enableItn;
        }

        public void setEnableItn(Boolean enableItn) {
            this.enableItn = enableItn;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}