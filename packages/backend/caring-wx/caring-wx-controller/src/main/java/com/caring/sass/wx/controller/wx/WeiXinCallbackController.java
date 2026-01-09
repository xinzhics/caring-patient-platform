package com.caring.sass.wx.controller.wx;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.wx.config.WxMpServiceHolder;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.service.config.ConfigService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author xinzh
 */
@Slf4j
@RestController
@RequestMapping({"/{code}/callback"})
public class WeiXinCallbackController {

    private final ConfigService configService;

    private final WxMpMessageRouter messageRouter;

    public WeiXinCallbackController(ConfigService configService, WxMpMessageRouter messageRouter) {
        this.configService = configService;
        this.messageRouter = messageRouter;
    }

    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@PathVariable String code,
                          @RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) {

        log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature,
                timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }
        Config config = getAppId(code);
        String appId = config.getAppId();
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        if (wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return "非法请求";
    }


    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable String code,
                       @RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        log.info("\n接收微信请求：[openid=[{}], [signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                openid, signature, encType, msgSignature, timestamp, nonce, requestBody);
        Config config = getAppId(code);
        // 当公众号已经完成第三方授权， 但是服务器配置没有取消时，对此处的消息拦截
        if (config.getThirdAuthorization() != null && config.getThirdAuthorization()) {
            return "success";
        }
        String appId = config.getAppId();
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        String out = null;
        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = route(inMessage);
            if (outMessage == null) {
                return "";
            }

            out = outMessage.toXml();
        } else if ("aes".equalsIgnoreCase(encType)) {
            // aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody, wxService.getWxMpConfigStorage(),
                    timestamp, nonce, msgSignature);
            log.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
            WxMpXmlOutMessage outMessage = route(inMessage);
            if (outMessage == null) {
                return "";
            }

            out = outMessage.toEncryptedXml(wxService.getWxMpConfigStorage());
        }

        log.debug("\n组装回复信息：{}", out);
        return out;
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return messageRouter.route(message);
        } catch (Exception e) {
            log.error("路由消息时出现异常！", e);
        }

        return null;
    }

    private Config getAppId(String code) {
        BaseContextHandler.setTenant(code);
        Config config = configService.getConfigByTenant();
        if (config == null) {
            throw new RuntimeException("微信公众号未配置");
        }
        return config;
    }
}
