package com.caring.sass.ai.entity.know;

import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
public class VolcengineToken {

    private static String id = "5071642817";

    private static String token = "8k3SJW_6Boix445CAfI4cXdJADbwCHkh";


    public static String buildToken() {
        return Base64.getEncoder().encodeToString((id + "caring" + token).getBytes(StandardCharsets.UTF_8));
    }


}
