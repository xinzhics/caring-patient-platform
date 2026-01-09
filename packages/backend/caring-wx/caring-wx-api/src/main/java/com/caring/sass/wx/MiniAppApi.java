package com.caring.sass.wx;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.caring.sass.base.R;
import com.caring.sass.common.enums.NotificationTarget;
import com.caring.sass.wx.dto.config.*;
import com.caring.sass.wx.dto.enums.TagsEnum;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.config.WeiXinUserInfo;
import com.caring.sass.wx.hystrix.WeiXinApiFallback;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.mp.bean.WxMpMassOpenIdsMessage;
import me.chanjar.weixin.mp.bean.WxMpMassTagMessage;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.tag.WxUserTag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * @ClassName com.caring.sass.wx.IWeiXinService
 * @Description 对外服务接口
 * @Author yangShuai
 * @Date 2020/9/16 9:49
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.oauth-server:caring-wx-server}", path = "/miniapp",
        qualifier = "MiniAppApi")
public interface MiniAppApi {


    @PostMapping("/sendCustomMessage")
    @ApiOperation("发送小程序客服消息")
    R<String> sendCustomMessage(@RequestBody WxMaKfMessageDto wxMaKfMessageDto);


    @PostMapping("/sendMessage/{appId}")
    @ApiOperation("发送订阅消息")
    R<String> sendMessage(@PathVariable(name = "appId") String appId, @RequestBody WxMaSubscribeMessage wxMaSubscribeMessage);

    @ApiOperation("创建小程序二维码")
    @PostMapping("createQRCode")
    R<String> createQRCode(@RequestParam(name = "appId") String appId, @RequestBody MiniQrCode miniQrCode);
}
