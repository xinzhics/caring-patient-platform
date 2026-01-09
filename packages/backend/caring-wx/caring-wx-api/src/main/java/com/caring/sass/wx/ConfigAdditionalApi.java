package com.caring.sass.wx;

import com.caring.sass.base.R;
import com.caring.sass.base.api.SuperApi;
import com.caring.sass.wx.dto.config.ConfigPageDTO;
import com.caring.sass.wx.dto.config.ConfigSaveDTO;
import com.caring.sass.wx.dto.config.ConfigUpdateDTO;
import com.caring.sass.wx.dto.config.GeneralForm;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import com.caring.sass.wx.hystrix.ConfigAdditionalApiFallback;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 微信其他配置对外接口
 *
 * @ClassName com.caring.sass.wx.IConfigAdditionalService
 * @Description
 * @Author yangShuai
 * @Date 2020/9/16 10:02
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.oauth-server:caring-wx-server}", path = "/configAdditional",
        qualifier = "configAdditionalApi", fallback = ConfigAdditionalApiFallback.class)
public interface ConfigAdditionalApi {


    /**
     * 获取公众号路径配置  模板url 登录url 等配置
     */
    @PostMapping({"/getConfigAdditionalByWxAppId"})
    R<ConfigAdditional> getConfigAdditionalByWxAppId(@RequestBody GeneralForm paramGeneralForm);

    /**
     * 更新公众号路径配置  模板url 登录url 等配置
     */
    @PostMapping({"/updateConfigAdditional"})
    R updateConfigAdditional(@RequestBody ConfigAdditional configAdditional);

    /**
     * 是否开启关键字回复：1打开，2关闭
     *
     * @param status 1打开，2关闭
     */
    @PutMapping({"/switchKeyword"})
    R<Boolean> switchKeyword(@RequestParam(value = "status") Integer status);

    /**
     * 开启或关闭自动回复
     *
     * @param status 开启或关闭自动回复：1打开，2关闭
     */
    @PutMapping({"/switchAutomaticReply"})
    R<Boolean> switchAutomaticReply(@RequestParam(value = "status") Integer status);

    /**
     * 查询开关状态
     *
     * @param type 1关键字开关，2消息回复开关
     */
    @ApiOperation("查询开关状态")
    @GetMapping({"/switchStatus"})
    R<Integer> switchStatus(@RequestParam(value = "type") @NotNull(message = "开关类型不能为空") Integer type);
}
