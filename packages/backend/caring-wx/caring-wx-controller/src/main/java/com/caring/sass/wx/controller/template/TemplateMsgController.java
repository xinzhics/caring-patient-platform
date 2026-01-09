package com.caring.sass.wx.controller.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.dto.template.TemplateMsgPageDTO;
import com.caring.sass.wx.dto.template.TemplateMsgSaveDTO;
import com.caring.sass.wx.dto.template.TemplateMsgUpdateDTO;
import com.caring.sass.wx.entity.template.TemplateMsg;
import com.caring.sass.wx.service.template.TemplateMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 模板消息
 * </p>
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/templateMsg")
@Api(value = "TemplateMsg", tags = "模板消息")
public class TemplateMsgController extends SuperController<TemplateMsgService, Long, TemplateMsg, TemplateMsgPageDTO, TemplateMsgSaveDTO, TemplateMsgUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<TemplateMsg> templateMsgList = list.stream().map((map) -> {
            TemplateMsg templateMsg = TemplateMsg.builder().build();
            //TODO 请在这里完成转换
            return templateMsg;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(templateMsgList));
    }


    /**
     * @param indefiner 关键字枚举
     * @return com.caring.sass.base.R<com.caring.sass.wx.dto.template.TemplateMsgDto>
     * @Author yangShuai
     * @Description
     * @Date 2020/9/17 9:42
     */
    @ApiOperation("【模板消息】 - 通过关键字查询模板消息")
    @GetMapping("/findByIndefiner")
    public R<TemplateMsgDto> findByIndefiner(@RequestParam("indefiner") String indefiner) {
        TemplateMsgDto msgDto = baseService.getTemplateMsgDtoByIndefiner(indefiner);
        return R.success(msgDto);
    }

    @ApiOperation("【模板消息】 - 通过关键字查询模板消息(自定义租户)")
    @GetMapping("/findByIndefiner/{tenantCode}")
    public R<TemplateMsgDto> findByIndefiner(
            @PathVariable("tenantCode") String tenantCode,
            @RequestParam("indefiner") String indefiner) {
        BaseContextHandler.setTenant(tenantCode);
        TemplateMsgDto msgDto = baseService.getTemplateMsgDtoByIndefiner(indefiner);
        return R.success(msgDto);
    }

    @ApiOperation("【模板消息】 - 通过多个关键字查询模板消息(自定义租户)")
    @PostMapping("/findByIndefinerList/{tenantCode}")
    public R<List<TemplateMsg>> findByIndefinerList(@PathVariable("tenantCode") String tenantCode, @RequestBody List<String> indefiners) {
        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<TemplateMsg> queryWrapper = new LbqWrapper<>();
        if (CollUtil.isEmpty(indefiners)) {
            return R.success(new ArrayList<>());
        }
        queryWrapper.in(TemplateMsg::getIndefiner, indefiners);
        List<TemplateMsg> list = baseService.list(queryWrapper);
        return R.success(list);
    }

    @ApiOperation("【模板消息】 - 通过微信模板消息Id 到系统模板消息中")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "templateMsgId", value = "系统模板消息Id", required = true),
            @ApiImplicitParam(paramType = "query", name = "weiXinTemplateId", value = "微信公众号模板消息Id", required = true)})
    @GetMapping("/updateWeiXinTemplateId")
    public R<Boolean> updateWeiXinTemplateId(@RequestParam("templateMsgId") Long templateMsgId, @RequestParam("weiXinTemplateId") String weiXinTemplateId, @RequestParam("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        boolean templateId = baseService.updateWeiXinTemplateId(templateMsgId, weiXinTemplateId);
        if (templateId) {
            return R.success();
        } else {
            return R.fail("更新失败，未找到模板消息");
        }
    }

    @ApiOperation("【模板消息】 - 通过Id获取模板消息和属性")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "templateMsgId", value = "系统模板消息Id", required = true)})
    @GetMapping("/getOneById")
    public R<TemplateMsgDto> getOneById(@RequestParam("templateMsgId") Long templateMsgId) {
        TemplateMsgDto templateMsgDto = baseService.getOneById(templateMsgId);
        return R.success(templateMsgDto);
    }

    @ApiOperation("【模板消息】 - 通过租户code和Id获取模板消息和属性")
    @GetMapping("/getOneById/{tenantCode}")
    public R<TemplateMsgDto> getTenantOneById(
            @PathVariable("tenantCode") String tenantCode,
            @RequestParam("templateMsgId") Long templateMsgId) {
        BaseContextHandler.setTenant(tenantCode);
        TemplateMsgDto templateMsgDto = baseService.getOneById(templateMsgId);
        return R.success(templateMsgDto);
    }

    @ApiOperation("【模板消息】 - 新增一个模板消息和属性")
    @PostMapping("saveTemplateMsgAndFields")
    public R<TemplateMsg> saveTemplateMsgAndFields(@RequestBody TemplateMsgSaveDTO templateMsgSaveDTO) {
        TemplateMsg t = baseService.saveTemplateMsg(templateMsgSaveDTO);
        return R.success(t);
    }

    @ApiOperation("【模板消息】 - 新增或修改一个模板消息和属性并返回模板信息")
    @PostMapping("updateTemplateMsgAndFieldsResultMsg/{tenantCode}")
    public R<TemplateMsg> updateTemplateMsgAndFields(@RequestBody TemplateMsgUpdateDTO templateMsgUpdateDTO,
                                                 @PathVariable("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        TemplateMsg templateMsg = baseService.updateTemplateMsgResultMsg(templateMsgUpdateDTO);
        return R.success(templateMsg);
    }

    @ApiOperation("【模板消息】 - 修改一个模板消息和属性")
    @PostMapping("updateTemplateMsgAndFields/{tenantCode}")
    public R<Boolean> update(@RequestBody TemplateMsgUpdateDTO templateMsgUpdateDTO, @PathVariable("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        boolean templateMsg = baseService.updateTemplateMsg(templateMsgUpdateDTO);
        return templateMsg ? R.success() : R.fail("保存失败");
    }

    @GetMapping("initMsg")
    public R<Boolean> initMsg() {
        boolean templateMsg = baseService.initTemplateMsg();
        return templateMsg ? R.success() : R.fail("初始化失败");
    }


    @ApiOperation("查询所有的模板消息/(群发通知的模板除外)")
    @GetMapping("all/{tenantCode}")
    public R<List<TemplateMsgDto>> getMapping(@PathVariable("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        List<TemplateMsgDto> templateMsgDtos = baseService.findBusinessTemplateMessage(null, null, null);
        return R.success(templateMsgDtos);
    }

    @ApiOperation("删除一个模板")
    @DeleteMapping("delete/{id}/{tenantCode}")
    public R<Boolean> deleteTemplate(@PathVariable("id") Long id, @PathVariable("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        boolean remove = baseService.removeByIds(ids);
        return R.success(remove);
    }

    @ApiOperation("复制模板消息及模板属性")
    @PostMapping("copyTemplateMsgAndFields")
    public R<Map<Long, Long>> copyTemplateMsgAndFields(@RequestParam("fromTenantCode") @NotEmpty(message = "待复制的项目编码不能为空") String fromTenantCode,
                                                   @RequestParam("toTenantCode") @NotEmpty(message = "目标项目编码不能为空") String toTenantCode) {
        Map<Long, Long> ret = baseService.copyTemplateMsgAndFields(fromTenantCode, toTenantCode);
        return R.success(ret);
    }



    /**
     * 高频使用的接口
     * @param tenantCode
     * @return
     */
    @ApiOperation("医生评论提醒模版")
    @GetMapping("getDoctorCommentReminderMsg")
    public R<TemplateMsgDto> getDoctorCommentReminderMsg(@RequestParam(value = "tenantCode", required = false) String tenantCode) {
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        TemplateMsgDto msgDto = baseService.getTemplateMsgDtoByIndefiner(TemplateMessageIndefiner.DOCTOR_COMMENT_REMINDER);;
        if (Objects.isNull(msgDto) || StrUtil.isEmpty(msgDto.getTemplateId())) {
            // 需要去微信公众号添加一下这个模板。并将模板保存到本地的模板库。
            msgDto = baseService.initDoctorCommentReminderMsg();
        } else {
            // 本地存在服务工单模版，但是微信公众号可能出现人为删除的情况，这里增加一次校验任务。
            Boolean aBoolean = baseService.checkTemplateExist(msgDto);
            if (!aBoolean) {
                msgDto = baseService.initDoctorCommentReminderMsg();
            }
        }
        return R.success(msgDto);
    }



    /**
     * 高频使用的接口
     * TODO:: 需要进行redis优化。
     * @param tenantCode
     * @param templateMsgId
     * @param indefiner
     * @return
     */
    @ApiOperation("消息模板，不存在则使用类目模板")
    @GetMapping("getCommonCategoryServiceWorkOrderMsg")
    public R<TemplateMsgDto> getCommonCategoryServiceWorkOrderMsg(@RequestParam(value = "tenantCode", required = false) String tenantCode,
                                                      @RequestParam(value = "templateMsgId", required = false) Long templateMsgId,
                                                      @RequestParam(value = "indefiner", required = false) String indefiner) {
        if (Objects.isNull(templateMsgId) && StrUtil.isEmpty(indefiner)) {
            throw new BizException("模板ID和模板关键字不能都为空");
        }
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        TemplateMsgDto msgDto = null;
        if (Objects.nonNull(templateMsgId)) {
            msgDto = baseService.getOneById(templateMsgId);
        } else if (StrUtil.isNotEmpty(indefiner)) {
            msgDto = baseService.getTemplateMsgDtoByIndefiner(indefiner);
        }
        if (Objects.isNull(msgDto) || StrUtil.isEmpty(msgDto.getTemplateId())) {
            // 都没有找到模板消息。  查询约定的 服务工单 模板是否添加在本地。
            if (!TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER.equals(indefiner)) {
                msgDto = baseService.getTemplateMsgDtoByIndefiner(TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER);
            }
            try {
                if (Objects.isNull(msgDto) || StrUtil.isEmpty(msgDto.getTemplateId())) {
                    // 需要去微信公众号添加一下这个模板。并将模板保存到本地的模板库。
                    msgDto = baseService.initCommonCategoryServiceWorkOrderMsg();
                } else {
                    // 本地存在服务工单模版，但是微信公众号可能出现人为删除的情况，这里增加一次校验任务。
                    Boolean aBoolean = baseService.checkTemplateExist(msgDto);
                    if (!aBoolean) {
                        msgDto = baseService.initCommonCategoryServiceWorkOrderMsg();
                    }
                }
            } catch (Exception e) {
                return R.success(null);
            }
            indefiner = TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER;
        }
        if (TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER.equals(indefiner)) {
            msgDto.setCommonCategory(true);
        }
        return R.success(msgDto);
    }


}
