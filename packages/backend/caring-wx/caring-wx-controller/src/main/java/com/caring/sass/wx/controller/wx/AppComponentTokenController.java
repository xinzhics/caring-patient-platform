package com.caring.sass.wx.controller.wx;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.wx.config.WxMpServiceHolder;
import com.caring.sass.wx.dao.config.ConfigMapper;
import com.caring.sass.wx.dao.config.PreAuthCodeMapper;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.config.PreAuthCode;
import com.caring.sass.wx.service.config.AppComponentTokenService;
import com.caring.sass.wx.service.thirdParty.ComponentAppService;
import com.caring.sass.wx.wxUtil.AddSHA1;
import com.caring.sass.wx.wxUtil.AesException;
import com.caring.sass.wx.wxUtil.WXBizMsgCrypt;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jeewx.api.core.exception.WexinReqException;
import org.jeewx.api.third.JwThirdAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 微信入口
 * @Version(2.0)
 */
@Slf4j
@Api(value = "AppComponentToken", tags = "微信第三方平台")
@RestController
@RequestMapping("/appComponentToken/anno")
public class AppComponentTokenController {

    @Autowired
    AppComponentTokenService appComponentTokenService;

    @Autowired
    ComponentAppService componentAppService;

    @Autowired
    WxMpMessageRouter wxMpMessageRouter;

    @Autowired
    PreAuthCodeMapper preAuthCodeMapper;

    @Autowired
    ConfigMapper configMapper;

    /**
     * @Author yangShuai
     * @Description 获取微信解密加密接口
     * @Date 2020/12/23 15:00
     *
     * @return com.caring.wechatrs.weUtil.WXBizMsgCrypt
     */
    public static WXBizMsgCrypt getWxBizMsgCrypt() throws AesException {
        return new WXBizMsgCrypt(ApplicationProperties.getThirdPlatformToken(),
                ApplicationProperties.getThirdPlatformEncodingAesKey(),
                ApplicationProperties.getThirdPlatformComponentAppId());
    }


    /**
     * @Author yangShuai
     * @Description
     * @Date 2020/12/23 15:01
     *  用于接收取消授权通知、授权成功通知、授权更新通知，也用于接收验证票据（component_verify_ticket），
     *
     *  按 saas 逻辑，现在 应该 收到 授权成功通知后，应该创建公众号对应的 项目。
     *
     *  component_verify_ticket 是验证平台方的重要凭据，请妥善保存。
     * 服务方在获取 component_access_token 时需要提供最新推送的 component_verify_ticket 以供验证身份合法性
     */
    @RequestMapping(value = "/portal", produces = "text/plain;charset=utf-8")
    public void authGet(HttpServletRequest request, HttpServletResponse response,
                        @RequestBody String requestBody,
                        @RequestParam(name = "signature", required = false) String signature,
                        @RequestParam(name = "timestamp", required = false) String timestamp,
                        @RequestParam(name = "nonce", required = false) String nonce,
                        @RequestParam(name = "msg_signature", required = false) String msg_signature,
                        @RequestParam(name = "echostr", required = false) String echostr,
                        @RequestParam(name = "encrypt_type", required = false) String encrypt_type) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String format = simpleDateFormat.format(new Date());
        log.info("微信第三方平台---------微信推送Ticket消息10分钟一次-----------"+ format);
        log.info("微信第三方平台-[{},{},{},{},{},{}]", signature, timestamp, nonce, msg_signature, echostr, encrypt_type);

        try {
            processAuthorizeEvent(request, requestBody, nonce, timestamp, signature, msg_signature);
        } catch (AesException e) {
            e.printStackTrace();
        }
        output(response, "success"); // 输出响应的内容
        log.error("接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature, timestamp, nonce, echostr);

    }


    /**
     *
     * 用于代授权的公众号或小程序的接收平台推送的消息与事件
     *
     * @return java.lang.String
     */
    @PostMapping(value = "/{appId}/callback", produces = "application/xml; charset=UTF-8")
    public String appPost(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable("appId") String appId,
            @RequestBody String requestBody,
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam(name = "encrypt_type", required = false) String encrypt_type,
            @RequestParam(name = "msg_signature", required = false) String msgSignature) {

        log.error("appPost接收到认证消息：[{}, {}, {}, {}, {}]", signature, timestamp, nonce, encrypt_type, msgSignature);
        log.error("appPost接收到提交的body：[{}]", requestBody);

        WXBizMsgCrypt pc = null;
        try {
            pc = getWxBizMsgCrypt();
            String result2 = pc.decryptMsg(msgSignature, timestamp, nonce, requestBody);
            log.error("接收到来自微信服务器提交的body解密之后：[{}]", result2);
            String networkCheck = checkWeixinAllNetworkCheck(request, response, appId, requestBody, nonce, timestamp, msgSignature);
            if (!StringUtils.isEmpty(networkCheck)) {
                return networkCheck;
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (AesException  e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";

    }



    /**
     * 返回第三方平台授权接口
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     */
    @RequestMapping(value = "/getAuthUrl/{tenantCode}")
    public String goAuthor(@PathVariable("tenantCode") String tenantCode) {
        return  ApplicationDomainUtil.apiUrl() + "/api/wx/appComponentToken/anno/goAuthor/" + tenantCode;
    }


    /**
     * 预授权码
     * 重定向到微信的授权页面。展示授权二维码供用户扫码授权。
     * 授权后，第三方平台拥有公众号的部分权限。可以使用授权码对公众号的粉丝处理事件
     *
     * 修改授权的账号类型为全部。包括小程序和公众号
     */
    @RequestMapping(value = "/goAuthor/{tenantCode}")
    public void goAuthor(@PathVariable("tenantCode") String tenantCode,
                         Integer auth_type,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        try {
            String componentAppId = ApplicationProperties.getThirdPlatformComponentAppId();
            String componentAccessToken = appComponentTokenService.getComponentAccessToken(componentAppId);

            //预授权码
            String preAuthCode = JwThirdAPI.getPreAuthCode(componentAppId, componentAccessToken);
            PreAuthCode authCode = new PreAuthCode();
            authCode.setPreAuthCode(preAuthCode);
            authCode.setTenantCode(tenantCode);
            if (Objects.isNull(auth_type)) {
                auth_type = 1;
            }
            authCode.setAuthType(auth_type);
            preAuthCodeMapper.insert(authCode);
            String url = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid="+componentAppId
                    +"&pre_auth_code="+preAuthCode+"&auth_type="+ auth_type +"&redirect_uri="
                    +ApplicationDomainUtil.webAdminDomain()+"/wxThirdAuthCallback?tenantCode=" + tenantCode;
            response.sendRedirect(url);
        } catch (WexinReqException e) {
            e.printStackTrace();
        }
    }


    /**
     * 工具类：回复微信服务器"文本消息"
     * @param response
     * @param returnvaleue
     */
    public void output(HttpServletResponse response, String returnvaleue) {
        try {
            PrintWriter pw = response.getWriter();
            pw.write(returnvaleue);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 处理授权事件的推送
     *
     * @param request
     * @throws IOException
     * @throws AesException
     */
    public void processAuthorizeEvent(HttpServletRequest request,
                                      String requestBody,
                                      String nonce,
                                      String timestamp,
                                      String signature,
                                      String msgSignature) throws AesException {
        if (StringUtils.isEmpty(msgSignature)) {
            return; // 微信推送给第三方开放平台的消息一定是加过密的，无消息加密无法解密消息
        }
        boolean isValid = checkSignature(ApplicationProperties.getThirdPlatformToken(), signature, timestamp, nonce);
        if (isValid) {
            String xml;
            WXBizMsgCrypt pc = getWxBizMsgCrypt();
            xml = pc.decryptMsg(msgSignature, timestamp, nonce, requestBody);
            log.info("第三方平台全网发布-----------------------解密后 Xml="+xml);
            processAuthorizationEvent(xml);
        }
    }


    void processAuthorizationEvent(String xml){
        Document doc;
        try {
            doc = DocumentHelper.parseText(xml);
            componentAppService.parsingXmlInfoType(doc);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }



    /**
     * 判断是否加密
     */
    public static boolean checkSignature(String token,String signature,String timestamp,String nonce){
        log.info("###token:"+token+";signature:"+signature+";timestamp:"+timestamp+"nonce:"+nonce);
        boolean flag = false;
        if(signature!=null && !signature.equals("") && timestamp!=null && !timestamp.equals("") && nonce!=null && !nonce.equals("")){
            String sha1 = "";
            String[] ss = new String[] { token, timestamp, nonce };
            Arrays.sort(ss);
            for (String s : ss) {
                sha1 += s;
            }
            sha1 = AddSHA1.SHA1(sha1);

            if (sha1.equals(signature)){
                flag = true;
            }
        }
        return flag;
    }


    /**
     * 处理 公众号的 事件和消息
     */
    public String checkWeixinAllNetworkCheck(HttpServletRequest request, HttpServletResponse response, String appId, String xml, String nonce, String timestamp, String msgSignature )
            throws DocumentException, AesException, IOException {
        WXBizMsgCrypt pc = getWxBizMsgCrypt();
        xml = pc.decryptMsg(msgSignature, timestamp, nonce, xml);

        Document doc = DocumentHelper.parseText(xml);
        Element rootElt = doc.getRootElement();
        String msgType = rootElt.elementText("MsgType");
        String toUserName = rootElt.elementText("ToUserName");  // 原始的公众号Id
        String fromUserName = rootElt.elementText("FromUserName");
        // 将xml转化为 工具包需要的实体
        WxMpXmlMessage wxMpXmlMessage = WxMpXmlMessage.fromXml(xml);
        if("event".equals(msgType)){
            // 包括了 关注， 取关， 扫码
        	 // 使用 官方提供的全网发布检测时 提供的测试公众号
            String event = rootElt.elementText("Event");
            if ("wx9252c5e0bb1836fc".equals(appId) || "wx8e1097c5bc82cde9".equals(appId)) {
                replyEventMessage(request,response,event,toUserName,fromUserName);
            } else {
                WxMpXmlOutMessage outMessage = configRoute(appId, wxMpXmlMessage);
                if (Objects.nonNull(outMessage)) {
                    return pc.encryptMsg(outMessage.toXml(), timestamp, nonce);
                }
            }
        } else if("text".equals(msgType)){
            String content = rootElt.elementText("Content");
            if("TESTCOMPONENT_MSG_TYPE_TEXT".equals(content)){
                String returnContent = content+"_callback";
                replyTextMessage(request,response,returnContent,toUserName,fromUserName);
            }else if(StringUtils.startsWithIgnoreCase(content, "QUERY_AUTH_CODE")){
                output(response, "");
                // 接下来客服API再回复一次消息
                replyApiTextMessage(request,response,content.split(":")[1],fromUserName);
            } else {
                WxMpXmlOutMessage outMessage = configRoute(appId, wxMpXmlMessage);
                if (Objects.nonNull(outMessage)) {
                    return pc.encryptMsg(outMessage.toXml(), timestamp, nonce);
                }
            }
        }  else {
            WxMpXmlOutMessage outMessage = configRoute(appId, wxMpXmlMessage);
            if (Objects.nonNull(outMessage)) {
                return pc.encryptMsg(outMessage.toXml(), timestamp, nonce);
            }
        }
        return "success";

    }

    /**
     * 根据appId查询项目code。设置线程租户
     * @param appId
     * @param wxMpXmlMessage
     * @return
     */
    public WxMpXmlOutMessage configRoute(String appId, WxMpXmlMessage wxMpXmlMessage) {
        List<Config> configs = configMapper.selectByAppIdWithoutTenant(appId);
        if (CollUtil.isNotEmpty(configs)) {
            Config config = configs.get(0);
            if (Objects.nonNull(config.getBecomeSilent()) && config.getBecomeSilent().equals(1)) {
                return null;
            }
            BaseContextHandler.setTenant(config.getTenantCode());
            // event 有如下可能 1 关注/取消关注事件 2 扫描带参数二维码事件 3 上报地理位置事件 4 自定义菜单事件 5 点击菜单拉取消息时的事件推送 6 点击菜单跳转链接时的事件推送
            WxMpService wxMpService = WxMpServiceHolder.getWxMpService(config.getAppId());
            return route(wxMpXmlMessage, wxMpService);
        }
        return null;
    }

    public void replyEventMessage(HttpServletRequest request, HttpServletResponse response, String event, String toUserName, String fromUserName) throws DocumentException, IOException {
        String content = event + "from_callback";
        log.info("---全网发布接入检测------step.4-------事件回复消息  content="+content + "   toUserName="+toUserName+"   fromUserName="+fromUserName);
        replyTextMessage(request,response,content,toUserName,fromUserName);
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message, WxMpService wxMpService) {
        try {
            return wxMpMessageRouter.route(message, new HashMap(2), wxMpService);
        } catch (Exception e) {
            log.error("路由消息时出现异常！", e);
        }

        return null;
    }

    /**
     * 回复微信服务器"文本消息"
     */
    public void replyTextMessage(HttpServletRequest request, HttpServletResponse response, String content, String toUserName, String fromUserName) {
        Long createTime = Calendar.getInstance().getTimeInMillis() / 1000;
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        sb.append("<ToUserName><![CDATA["+fromUserName+"]]></ToUserName>");
        sb.append("<FromUserName><![CDATA["+toUserName+"]]></FromUserName>");
        sb.append("<CreateTime>"+createTime+"</CreateTime>");
        sb.append("<MsgType><![CDATA[text]]></MsgType>");
        sb.append("<Content><![CDATA["+content+"]]></Content>");
        sb.append("</xml>");
        String replyMsg = sb.toString();

        String returnvaleue = "";
        try {
            WXBizMsgCrypt pc = getWxBizMsgCrypt();
            returnvaleue = pc.encryptMsg(replyMsg, createTime.toString(), "easemob");
            log.info("------------------加密后的返回内容 returnvaleue： "+returnvaleue);
        } catch (AesException e) {
            e.printStackTrace();
        }
        output(response, returnvaleue);
    }

    /**
     * @Author yangShuai
     * @Description 发布上线时， 微信对系统进行消息测试
     * @Date 2021/1/7 9:26
     *
     */
    public void replyApiTextMessage(HttpServletRequest request, HttpServletResponse response, String authCode, String fromUserName) {
        String authorizationCode = authCode;
        // 得到微信授权成功的消息后，应该立刻进行处理！！相关信息只会在首次授权的时候推送过来
        log.info("------step.1----使用客服消息接口回复粉丝----逻辑开始-------------------------");
        try {
            String componentAppId = ApplicationProperties.getThirdPlatformComponentAppId();
            String componentAccessToken = appComponentTokenService.getComponentAccessToken(componentAppId);

            log.info("------step.2----使用客服消息接口回复粉丝------- component_access_token = "+componentAccessToken + "---------authorization_code = "+authorizationCode);
            JSONObject authorizationInfoJson = JwThirdAPI.getApiQueryAuthInfo(componentAppId, authorizationCode, componentAccessToken);

            log.info("------step.3----使用客服消息接口回复粉丝-------------- 获取authorizationInfoJson = "+authorizationInfoJson);
            JSONObject infoJson = authorizationInfoJson.getJSONObject("authorization_info");
            String authorizerAccessToken = infoJson.getString("authorizer_access_token");


            Map<String,Object> obj = new HashMap<>(10);
            Map<String,Object> msgMap = new HashMap<>(10);
            String msg = authCode + "_from_api";
            msgMap.put("content", msg);

            obj.put("touser", fromUserName);
            obj.put("msgtype", "text");
            obj.put("text", msgMap);
            JwThirdAPI.sendMessage(obj, authorizerAccessToken);
        } catch (WexinReqException e) {
            e.printStackTrace();
        }

    }


}
