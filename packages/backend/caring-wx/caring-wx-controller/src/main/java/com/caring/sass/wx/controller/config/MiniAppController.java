package com.caring.sass.wx.controller.config;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.wx.dto.config.*;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.service.miniApp.MiniAppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.subscribemsg.PubTemplateKeyword;
import me.chanjar.weixin.common.bean.subscribemsg.TemplateInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * @className: MiniAppController
 * @author: 杨帅
 * @date: 2024/3/22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/miniapp")
@Api(value = "miniapp", tags = "小程序功能")
public class MiniAppController {


    @Autowired
    MiniAppService miniAppService;

    /**
     * 使用 appId 和jsCode 获取当前用户在小程序的openId
     * @param jsCode
     * @param appId
     * @return
     */
    @GetMapping("/anno/thirdPartyCode2Session")
    @ApiOperation("小程序登录获取用户openId和会话秘钥")
    @SysLog
    public R<WxMaJscode2SessionResult> miniAppThirdPartyCode2Session(@RequestParam String jsCode,
                                                                     @RequestParam String appId) {

        WxMaJscode2SessionResult wxMaJscode2SessionResult = miniAppService.miniAppThirdPartyCode2Session(jsCode, appId);
        return R.success(wxMaJscode2SessionResult);
    }

    @GetMapping("anno/codeRedirect")
    @ApiOperation("扫码重定向")
    public void codeRedirect(HttpServletResponse response, @RequestParam String appId) {

        String generatescheme = miniAppService.generatescheme(appId);
        System.out.println(generatescheme);
        try {
            response.sendRedirect(generatescheme);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("createQRCode")
    @ApiOperation("创建小程序二维码")
    public R<String> createQRCode(@RequestParam(name = "appId") String appId, @RequestBody MiniQrCode miniQrCode) {

        String codeUrl = miniAppService.createQRCode(appId, miniQrCode.getPath(), miniQrCode.getWidth());
        return R.success(codeUrl);
    }



    @GetMapping("/anno/getPhoneNumber")
    @ApiOperation("小程序获取用户手机号")
    @SysLog
    public R<WxMaPhoneNumberInfo> getPhoneNumber(@RequestParam String code,
                                                 @RequestParam String appId) {

        WxMaPhoneNumberInfo phoneNumber = miniAppService.getPhoneNumber(code, appId);
        return R.success(phoneNumber);
    }

    @PostMapping("/sendCustomMessage")
    @ApiOperation("发送小程序客服消息")
    public R<String> sendCustomMessage(@RequestBody WxMaKfMessageDto wxMaKfMessageDto) {

        miniAppService.sendCustomMessage(wxMaKfMessageDto);
        return R.success("success");

    }

    @PostMapping("/getTemplate/{appId}")
    @ApiOperation("获取模版内容")
    public R<List<PubTemplateKeyword>> getTemplate(@PathVariable String appId, @RequestParam String templateId) {

        List<PubTemplateKeyword> template = miniAppService.getTemplate(appId, templateId);
        return R.success(template);

    }


    @PostMapping("/getTemplates/{appId}")
    @ApiOperation("获取模版内容")
    public R<List<TemplateInfo>> getTemplates(@PathVariable String appId) {

        List<TemplateInfo> template = miniAppService.getTemplate(appId);
        return R.success(template);

    }


    @PostMapping("/sendMessage/{appId}")
    @ApiOperation("发送订阅消息")
    public R<String> sendMessage(@PathVariable String appId, @RequestBody WxMaSubscribeMessage wxMaSubscribeMessage) {

        miniAppService.sendMessage(appId, wxMaSubscribeMessage);
        return R.success("success");

    }

    @SysLog
    @ApiOperation("添加小程序")
    @PostMapping("addMiniApp")
    public R<Boolean> addMiniApp(@RequestBody @Validated MiniAppSaveDTO miniAppSaveDTO) {

        Config config = miniAppService.getByAppIdNoTenantCode(miniAppSaveDTO.getAppId());
        if (Objects.nonNull(config)) {
            return R.fail("小程序存在");
        }
        config = new Config();
        config.setAuthType(2);
        config.setThirdAuthorization(false);
        config.setName(miniAppSaveDTO.getName());
        config.setAppId(miniAppSaveDTO.getAppId());
        config.setAppSecret(miniAppSaveDTO.getAppSecret());
        BaseContextHandler.setTenant(miniAppSaveDTO.getTenantCode());
        config.setTenantCode(miniAppSaveDTO.getTenantCode());
        miniAppService.save(config);
        return R.success(true);
    }

    @ApiOperation("获取小程序的项目code")
    @GetMapping("anno/getMiniAppConfigTenantCode")
    public R<String> getMiniAppConfigTenantCode(@RequestParam String appId) {

        Config config = miniAppService.getByAppIdNoTenantCode(appId);
        if (Objects.nonNull(config)) {
            String tenantCode = config.getTenantCode();
            String encode = Base64.getEncoder().encodeToString(tenantCode.getBytes());
            return R.success(encode);
        } else {
            return R.success(null);
        }
    }


    @ApiOperation("系统当前小程序的列表")
    @PostMapping("page")
    public R<IPage<MiniAppPageList>> page(@RequestBody PageParams<ConfigPageDTO> params) {
        IPage<MiniAppPageList> buildPage = params.buildPage();
        ConfigPageDTO model = params.getModel();
        LambdaQueryWrapper<Config> wrapper = Wrappers.<Config>lambdaQuery();
        if (StrUtil.isNotEmpty(model.getAppId())) {
            wrapper.eq(Config::getAppId, model.getAppId());
        }
        if (StrUtil.isNotEmpty(model.getName())) {
            wrapper.like(Config::getName, model.getName());
        }
        wrapper.eq(Config::getAuthType, 2);

        wrapper.select(SuperEntity::getId, Config::getAppId, Config::getName, Config::getThirdAuthorization, Config::getTenantCode);

        IPage<Config> configIPage = miniAppService.pageNoTenantCode(buildPage, wrapper);
        List<Config> records = configIPage.getRecords();
        List<MiniAppPageList> miniAppPageLists = new ArrayList<>();
        for (Config record : records) {
            MiniAppPageList pageList = new MiniAppPageList();
            BeanUtils.copyProperties(record, pageList);
            miniAppPageLists.add(pageList);
        }
        buildPage.setPages(configIPage.getPages());
        buildPage.setRecords(miniAppPageLists);
        buildPage.setTotal(configIPage.getTotal());
        buildPage.setSize(configIPage.getSize());
        buildPage.setCurrent(configIPage.getCurrent());
        return R.success(buildPage);
    }

}
