package com.caring.sass.wx.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.dto.template.TemplateMsgSaveDTO;
import com.caring.sass.wx.dto.template.TemplateMsgUpdateDTO;
import com.caring.sass.wx.entity.template.TemplateMsg;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Component
public class TemplateMsgApiFallback implements TemplateMsgApi {

    @Override
    public R<Boolean> initMsg() {
        return R.timeout();
    }

    @Override
    public R<TemplateMsgDto> getOneById(Long templateMsgId) {
        return R.timeout();
    }

    @Override
    public R<TemplateMsgDto> getTenantOneById(String tenantCode, Long templateMsgId) {
        return R.timeout();
    }

    @Override
    public R<TemplateMsgDto> findByIndefiner(String tenantCode, String indefiner) {
        return R.timeout();
    }

    @Override
    public R<TemplateMsg> saveTemplateMsgAndFields(TemplateMsgSaveDTO templateMsgSaveDTO) {
        return R.timeout();
    }

    @Override
    public R<TemplateMsg> updateTemplateMsgAndFields(TemplateMsgUpdateDTO templateMsgUpdateDTO, String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<Map<Long, Long>> copyTemplateMsgAndFields(String fromTenantCode, String toTenantCode) {
        return R.timeout();
    }

    @Override
    public R<Boolean> deleteTemplate(Long id, String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<List<TemplateMsg>> findByIndefinerList(String tenantCode, List<String> indefiners) {
        return R.timeout();
    }

    @Override
    public R<TemplateMsgDto> getCommonCategoryServiceWorkOrderMsg(String tenantCode, Long templateMsgId, String indefiner) {
        return R.timeout();
    }

    @Override
    public R<TemplateMsgDto> getDoctorCommentReminderMsg(String tenantCode) {
        return R.timeout();
    }
}
