package com.caring.sass.ai.humanVideo.task;

import lombok.Data;

import java.util.UUID;

@Data
public class TtsRequest {

    public TtsRequest(String text, String appId, String token, String cluster, String userId, String speakerId) {
        app = new App(appId, token, cluster);
        user = new User(userId);
        audio = new Audio();
        audio.setVoice_type(speakerId);
        request = new Request();
        request.setText(text);
    }

    private App app;
    private User user;
    private Audio audio;
    private Request request;


    @Data
    public class App {
        private String appid;
        private String token; // 目前未生效，填写默认值：access_token
        private String cluster;

        public App(String appid, String token, String cluster) {
            this.appid = appid;
            this.token = token;
            this.cluster = cluster;
        }

    }

    @Data
    public class User {
        private String uid; // 目前未生效，填写一个默认值就可以

        public User(String uid) {
            this.uid = uid;
        }
    }

    @Data
    public class Audio {
        private String voice_type;
        private String encoding = "wav";
        private int rate = 24000;
        private float speed_ratio = 1.0f;
    }

    @Data
    public class Request {
        private String reqid = UUID.randomUUID().toString();
        private String text;
        private String text_type = "plain";
        private String operation = "query";

    }
}