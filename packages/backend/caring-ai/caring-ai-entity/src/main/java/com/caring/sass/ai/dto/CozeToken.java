package com.caring.sass.ai.dto;

import lombok.Data;

@Data
public class CozeToken {


    private String token;

    private String botId;

    public CozeToken() {
    }

    public CozeToken(String token, String botId) {
        this.token = token;
        this.botId = botId;
    }
}
