//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.caring.sass.msgs.utils.im.comm;

import com.caring.sass.msgs.config.IMConfigWrapper;
import io.swagger.client.ApiException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    public ResponseHandler() {
    }

    public Object handle(EasemobAPI easemobAPI) {
        Object result = null;

        try {
            result = easemobAPI.invokeEasemobAPI();
            return result;
        } catch (ApiException var4) {
            int code = var4.getCode();
            if (code == 401) {
                String responseBody = var4.getResponseBody();
                if (responseBody.contains("unauthorized")) {
                    IMConfigWrapper.initAccessToken(IMConfigWrapper.getKey());
                    // token 刷新后。尝试重新执行任务
                    try {
                        return easemobAPI.invokeEasemobAPI();
                    } catch (ApiException var5) {
                        logger.error("刷新token后im始终异常。异常code：{}, 异常说明 {}", var5.getCode(), var5.getResponseBody());
                    }
                }
                if (responseBody.contains("auth_bad_access_token")) {
                    logger.error("发送请求时使用的token错误。或者无效token");
                }
            }

            throw new RuntimeException(var4);
        }
    }

    public Object retry(EasemobAPI easemobAPI) {
        Object result = null;
        long time = 5L;

        for(int i = 0; i < 3; ++i) {
            try {
                TimeUnit.SECONDS.sleep(time);
                logger.info("Reconnection is in progress..." + i);
                result = easemobAPI.invokeEasemobAPI();
                if (result != null) {
                    return result;
                }
            } catch (ApiException var7) {
                time *= 3L;
            } catch (InterruptedException var8) {
                logger.error(var8.getMessage());
            }
        }

        return result;
    }
}
