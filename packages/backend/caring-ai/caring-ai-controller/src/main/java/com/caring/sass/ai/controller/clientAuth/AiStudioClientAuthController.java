package com.caring.sass.ai.controller.clientAuth;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.article.service.ArticleUserVoiceService;
import com.caring.sass.ai.dto.article.ArticleUserVoicePageDTO;
import com.caring.sass.ai.dto.article.VoiceCloneStatus;
import com.caring.sass.ai.dto.userbiz.StudioAuthBo;
import com.caring.sass.ai.entity.article.ArticleUserVoice;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.oauth.api.OauthApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "AiStudioClientAuthController", tags = "ai工作室授权")
@RestController
@RequestMapping("/studio/anno")
public class AiStudioClientAuthController {

    private String studioToken = System.getenv().getOrDefault("AI_STUDIO_TOKEN", "");

    @Autowired
    OauthApi oauthApi;


    @Autowired
    ArticleUserVoiceService articleUserVoiceService;



    @PostMapping("/pageArticleUserVoice")
    @ApiOperation("名称分页查询所有可用音色")
    public R<IPage<ArticleUserVoice>> pageArticleUserVoice(@RequestBody PageParams<ArticleUserVoicePageDTO> params) {

        ArticleUserVoicePageDTO model = params.getModel();
        IPage<ArticleUserVoice> builtPage = params.buildPage();
        // 查询用户自己的音色，或者符合条件的 默认音色
        LbqWrapper<ArticleUserVoice> eq = new LbqWrapper<>();
        eq.eq(ArticleUserVoice::getHasClone, true);
        eq.eq(ArticleUserVoice::getMiniExpired, false);
        eq.eq(ArticleUserVoice::getCloneStatus, VoiceCloneStatus.SUCCESS);
        eq.eq(ArticleUserVoice::getDeleteStatus, false);
        eq.eq(ArticleUserVoice::getDefaultVoice, false);
        if (StringUtils.isNotEmptyString(model.getName())) {
            eq.like(ArticleUserVoice::getName, model.getName());
        }
        eq.orderByDesc(SuperEntity::getCreateTime);
        articleUserVoiceService.page(builtPage, eq);
        return R.success(builtPage);

    }





    @ApiOperation("授权")
    @PostMapping("/auth")
    public R<JSONObject> query(@RequestBody StudioAuthBo studioAuthBo) {

        if (studioAuthBo.getClientToken().equals(studioToken)) {
            R<JSONObject> tempToken = oauthApi.createTempToken(studioAuthBo.getUserId());
            return tempToken;
        }
        return R.fail("无效的认证");
    }


    @ApiOperation("刷新授权")
    @PostMapping("/refreshAuth")
    public R<JSONObject> refreshAuth(@RequestBody StudioAuthBo studioAuthBo) {

        if (studioAuthBo.getClientToken().equals(studioToken)) {

            String refreshToken = studioAuthBo.getRefreshToken();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", studioAuthBo.getUserId());
            jsonObject.put("refreshToken", refreshToken);
            return oauthApi.aiAuthRefreshToken(jsonObject);
        }
        return R.fail("无效的认证");
    }



}
