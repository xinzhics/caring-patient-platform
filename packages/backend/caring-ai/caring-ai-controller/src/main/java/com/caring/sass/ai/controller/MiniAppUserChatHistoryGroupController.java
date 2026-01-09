package com.caring.sass.ai.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.chat.server.MiniAppUserChatHistoryGroupService;
import com.caring.sass.ai.entity.chat.MiniAppUserChatHistoryGroup;
import com.caring.sass.ai.entity.dto.MiniAppChatHistoryGroupDTO;
import com.caring.sass.ai.entity.dto.MiniAppUserChatHistoryGroupPageDTO;
import com.caring.sass.ai.entity.dto.MiniAppUserChatHistoryGroupSaveDTO;
import com.caring.sass.ai.entity.dto.MiniAppUserChatHistoryGroupUpdateDTO;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 前端控制器
 * 敏智用户历史对话
 * </p>
 *
 * @author 杨帅
 * @date 2024-03-28
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/miniAppUserChatHistoryGroup")
@Api(value = "MiniAppUserChatHistoryGroupController", tags = "敏智用户历史对话")
public class MiniAppUserChatHistoryGroupController extends SuperController<MiniAppUserChatHistoryGroupService, Long, MiniAppUserChatHistoryGroup, MiniAppUserChatHistoryGroupPageDTO, MiniAppUserChatHistoryGroupSaveDTO, MiniAppUserChatHistoryGroupUpdateDTO> {

    @PostMapping("customPage")
    @ApiOperation("分页加载历史对话")
    public R<MiniAppChatHistoryGroupDTO> customPage(@RequestBody @Validated PageParams<MiniAppUserChatHistoryGroupPageDTO> params) {


        IPage<MiniAppUserChatHistoryGroup> page = params.buildPage();
        MiniAppUserChatHistoryGroupPageDTO paramsModel = params.getModel();
        LbqWrapper<MiniAppUserChatHistoryGroup> wrapper = Wraps.<MiniAppUserChatHistoryGroup>lbQ()
                .eq(MiniAppUserChatHistoryGroup::getSenderId, paramsModel.getSenderId())
                .eq(MiniAppUserChatHistoryGroup::getCurrentUserType, paramsModel.getCurrentUserType());
        if (StrUtil.isNotEmpty(paramsModel.getContentTitle())) {
            wrapper.like(MiniAppUserChatHistoryGroup::getContentTitle, paramsModel.getContentTitle());
        }
        wrapper.orderByDesc(SuperEntity::getCreateTime);
        baseService.page(page, wrapper);
        List<MiniAppUserChatHistoryGroup> records = page.getRecords();
        Map<String, List<MiniAppUserChatHistoryGroup>> historyGroup = baseService.customPage(records);
        MiniAppChatHistoryGroupDTO groupDTO = new MiniAppChatHistoryGroupDTO();
        groupDTO.setHistoryGroup(historyGroup);
        groupDTO.setCurrent(page.getCurrent());
        groupDTO.setPages(page.getPages());
        groupDTO.setTotal(page.getTotal());
        groupDTO.setSize(page.getSize());
        return R.success(groupDTO);
    }


}
