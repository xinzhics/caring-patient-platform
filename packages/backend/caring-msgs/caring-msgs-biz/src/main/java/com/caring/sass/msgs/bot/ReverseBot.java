package com.caring.sass.msgs.bot;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.caring.sass.exception.BizException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 逆向工程机器人
 *
 * @author leizhi
 */
@Order(1)
@Component
public class ReverseBot implements AiBot {

    @Override
    public String chat(String greeting, String message, String user) {
        HttpResponse response = HttpRequest.get("http://121.36.11.64:5000/promt?info=" + message)
                .timeout(2 * 60 * 1000).execute();
        int status = response.getStatus();
        String body = response.body();
        if (status != HttpStatus.HTTP_OK || StrUtil.isEmpty(body)) {
            throw new BizException("ChatGPT逆向服务异常" + body);
        } else {
            return body;
        }
    }
}
