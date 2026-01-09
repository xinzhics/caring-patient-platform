package com.caring.sass.ai.controller;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.dto.CozeRequest;
import com.caring.sass.ai.service.CozeServer;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(value = "/coze", tags = "扣子")
@RestController
@RequestMapping("/coze")
public class CozeController {

    @Autowired
    CozeServer cozeServer;

    @ApiOperation("调用扣子api")
    @PostMapping("/anno/query")
    public R<JSONObject> sseChat(@RequestBody @Validated CozeRequest cozeRequest) {

        JSONObject jsonObject = cozeServer.queryCoze(cozeRequest);

        return R.success(jsonObject);
    }

}
