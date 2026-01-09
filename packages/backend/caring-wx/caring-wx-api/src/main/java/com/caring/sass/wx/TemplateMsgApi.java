package com.caring.sass.wx;

import com.caring.sass.base.R;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.dto.template.TemplateMsgSaveDTO;
import com.caring.sass.wx.dto.template.TemplateMsgUpdateDTO;
import com.caring.sass.wx.entity.template.TemplateMsg;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName TemplateMsgApi
 * @Description
 * @Author yangShuai
 * @Date 2020/9/29 11:25
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.oauth-server:caring-wx-server}", path = "/templateMsg",
        qualifier = "TemplateMsgApi"/*, fallback = TemplateMsgApiFallback.class*/)
public interface TemplateMsgApi {

    @GetMapping("initMsg")
    R<Boolean> initMsg();

    @GetMapping("/getOneById")
    R<TemplateMsgDto> getOneById(@RequestParam("templateMsgId") Long templateMsgId);

    // 功能配置专用。 查询发送微信的模板不可使用。
    @ApiOperation("【模板消息】 - 通过租户code和Id获取模板消息和属性")
    @GetMapping("/getOneById/{tenantCode}")
    R<TemplateMsgDto> getTenantOneById(@PathVariable("tenantCode") String tenantCode, @RequestParam("templateMsgId") Long templateMsgId);

    @ApiOperation("【模板消息】 - 通过关键字查询模板消息(自定义租户)")
    @GetMapping("/findByIndefiner/{tenantCode}")
    R<TemplateMsgDto> findByIndefiner(@PathVariable("tenantCode") String tenantCode, @RequestParam("indefiner") String indefiner);

    @ApiOperation("【模板消息】 - 新增一个模板消息和属性")
    @PostMapping("saveTemplateMsgAndFields")
    R<TemplateMsg> saveTemplateMsgAndFields(@RequestBody TemplateMsgSaveDTO templateMsgSaveDTO);

    @ApiOperation("【模板消息】 - 新增或修改一个模板消息和属性并返回模板信息")
    @PostMapping("updateTemplateMsgAndFieldsResultMsg/{tenantCode}")
    R<TemplateMsg> updateTemplateMsgAndFields(@RequestBody TemplateMsgUpdateDTO templateMsgUpdateDTO,
                                                     @PathVariable("tenantCode") String tenantCode);
    /**
     * 复制模板消息及模板属性
     *
     * @param fromTenantCode 待复制的项目编码
     * @param toTenantCode   目标项目编码
     */
    @PostMapping("copyTemplateMsgAndFields")
    R<Map<Long, Long>> copyTemplateMsgAndFields(@RequestParam("fromTenantCode") String fromTenantCode,
                                                @RequestParam("toTenantCode") String toTenantCode);

    @ApiOperation("删除一个模板")
    @DeleteMapping("delete/{id}/{tenantCode}")
    R<Boolean> deleteTemplate(@PathVariable("id") Long id, @PathVariable("tenantCode") String tenantCode);

    // 初始化功能配置使用。
    @ApiOperation("【模板消息】 - 通过多个关键字查询模板消息(自定义租户)")
    @PostMapping("/findByIndefinerList/{tenantCode}")
    R<List<TemplateMsg>> findByIndefinerList(@PathVariable("tenantCode") String tenantCode, @RequestBody List<String> indefiners);


    @ApiOperation("消息模板，不存在则使用类目模板")
    @GetMapping("getCommonCategoryServiceWorkOrderMsg")
    R<TemplateMsgDto> getCommonCategoryServiceWorkOrderMsg(@RequestParam(value = "tenantCode", required = false) String tenantCode,
                                                                  @RequestParam(value = "templateMsgId", required = false) Long templateMsgId,
                                                                  @RequestParam(value = "indefiner", required = false) String indefiner);


    @ApiOperation("医生评论提醒模版")
    @GetMapping("getDoctorCommentReminderMsg")
    R<TemplateMsgDto> getDoctorCommentReminderMsg(@RequestParam(value = "tenantCode", required = false) String tenantCode);

}
