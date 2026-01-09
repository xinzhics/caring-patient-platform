package com.caring.sass.ai.controller.knows;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.dto.know.KnowledgeSubscribeUpdateMessagePageDTO;
import com.caring.sass.ai.dto.know.KnowledgeSubscribeUpdateMessageSaveDTO;
import com.caring.sass.ai.dto.know.KnowledgeSubscribeUpdateMessageUpdateDTO;
import com.caring.sass.ai.entity.know.KnowledgeSubscribeUpdateMessage;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import com.caring.sass.ai.know.service.KnowledgeSubscribeUpdateMessageService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 博主订阅设置修改记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-08-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledgeSubscribeUpdateMessage")
@Api(value = "KnowledgeSubscribeUpdateMessage", tags = "博主订阅设置修改记录")
public class KnowledgeSubscribeUpdateMessageController{

    @Autowired
    KnowledgeSubscribeUpdateMessageService knowledgeSubscribeUpdateMessageService;


    @ApiOperation("博主订阅修改记录")
    @PostMapping("/page")
    public R<IPage<KnowledgeSubscribeUpdateMessage>> page(@RequestBody @Validated PageParams<KnowledgeSubscribeUpdateMessagePageDTO> params) {

        IPage<KnowledgeSubscribeUpdateMessage> buildPage = params.buildPage();
        LbqWrapper<KnowledgeSubscribeUpdateMessage> wrapper = Wraps.<KnowledgeSubscribeUpdateMessage>lbQ()
                .eq(KnowledgeSubscribeUpdateMessage::getKnowledgeUserId, params.getModel().getKnowledgeUserId());
        knowledgeSubscribeUpdateMessageService.page(buildPage, wrapper);
        return R.success(buildPage);
    }




    @ApiOperation("修改博主的订阅设置")
    @PutMapping("subscribeInfo")
    public R<Boolean> updateSubscribeInfo(@RequestBody @Validated KnowledgeSubscribeUpdateMessageSaveDTO knowledgeUser) {

        knowledgeSubscribeUpdateMessageService.updateSubscribeInfo(knowledgeUser);
        return R.success(true);
    }

}
