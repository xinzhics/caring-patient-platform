package com.caring.sass.nursing.controller.form;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dto.form.FormFieldsGroupPageDTO;
import com.caring.sass.nursing.dto.form.FormFieldsGroupSaveDTO;
import com.caring.sass.nursing.dto.form.FormFieldsGroupUpdateDTO;
import com.caring.sass.nursing.entity.form.FormFieldsGroup;
import com.caring.sass.nursing.service.form.FormFieldsGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Wrapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 表单题目的分组规则
 * </p>
 *
 * @author 杨帅
 * @date 2023-10-11
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/formFieldsGroup")
@Api(value = "FormFieldsGroup", tags = "表单题目的分组规则")
public class FormFieldsGroupController extends SuperController<FormFieldsGroupService, Long, FormFieldsGroup, FormFieldsGroupPageDTO, FormFieldsGroupSaveDTO, FormFieldsGroupUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<FormFieldsGroup> formFieldsGroupList = list.stream().map((map) -> {
            FormFieldsGroup formFieldsGroup = FormFieldsGroup.builder().build();
            //TODO 请在这里完成转换
            return formFieldsGroup;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(formFieldsGroupList));
    }


    @ApiOperation("删除分组")
    @PostMapping("deleteGroups")
    public R<Boolean> deleteGroups(@RequestParam("tenantCode") String tenantCode, @RequestBody List<Long> ids) {

        BaseContextHandler.setTenant(tenantCode);
        baseService.removeByIds(ids);
        return R.success(true);
    }


    @ApiOperation("查询表单的题目分组")
    @GetMapping("getFieldGroupList")
    public R<List<FormFieldsGroup>> getFieldGroupList(@RequestParam(required = false) String tenantCode, @RequestParam Long formId) {
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        List<FormFieldsGroup> groupList = baseService.list(Wraps.<FormFieldsGroup>lbQ()
                .eq(FormFieldsGroup::getFormId, formId)
                .orderByAsc(FormFieldsGroup::getGroupSort));
        return R.success(groupList);

    }



    @ApiOperation("创建一个临时组ID")
    @GetMapping("createTempGroupId")
    public R<FormFieldsGroup> createTempGroupId() {

        Long groupTempId = baseService.createGroupTempId();
        UUID uuid = UUID.randomUUID();
        String string = uuid.toString();
        FormFieldsGroup fieldsGroup = new FormFieldsGroup();
        fieldsGroup.setId(groupTempId);
        fieldsGroup.setFieldGroupUUId(string);
        return R.success(fieldsGroup);

    }

    @Override
    public R<Boolean> handlerSaveBatch(List<FormFieldsGroupSaveDTO> formFieldsGroupSaveDTOS) {
        if (CollUtil.isEmpty(formFieldsGroupSaveDTOS)) {
            return R.successDef();
        }
        FormFieldsGroupSaveDTO model = formFieldsGroupSaveDTOS.get(0);
        String tenantCode = model.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        return R.successDef();
    }


    @Override
    public R<FormFieldsGroup> handlerSave(FormFieldsGroupSaveDTO model) {
        String tenantCode = model.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        return R.successDef();
    }


    @Override
    public R<FormFieldsGroup> handlerUpdate(FormFieldsGroupUpdateDTO model) {
        String tenantCode = model.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        return R.successDef();
    }
}
