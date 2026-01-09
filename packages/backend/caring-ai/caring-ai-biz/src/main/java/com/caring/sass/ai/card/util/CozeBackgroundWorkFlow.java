package com.caring.sass.ai.card.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
/**
 * coze图片背景替换
 *
 * @author leizhi
 */
public class CozeBackgroundWorkFlow {

//    workflow_id：7495575540305870900
//    curl 请求示例
//    curl -X POST 'https://api.coze.cn/v1/workflow/run' \
//            -H "Authorization: Bearer ${COZE_API_KEY:your-coze-api-key-here}" \
//            -H "Content-Type: application/json" \
//            -d '{
//            "parameters": {
//        "url": "https://caing-test.obs.cn-north-4.myhuaweicloud.com:443/0346%2F2025%2F04%2F9e4e1678-db1b-4036-a8d2-0616f7f9c45f.jpg"
//    },
//            "workflow_id": "7495575540305870900"
//}'

    public static String runWorkflow(String imageUrl, String token, String userId) {
        JSONObject params = new JSONObject();
        params.set("parameters", new JSONObject()
                .set("url", imageUrl)
                .set("user_id", userId));
        params.set("workflow_id", "7495575540305870900");

        HttpResponse response = HttpRequest.post("https://api.coze.cn/v1/workflow/run")
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(params.toString())
                .execute();

        if (response.isOk()) {
            JSONObject cozeData = new JSONObject(response.body());
            return cozeData.getJSONObject("data").getStr("output");
        }
        return null;
    }

}
