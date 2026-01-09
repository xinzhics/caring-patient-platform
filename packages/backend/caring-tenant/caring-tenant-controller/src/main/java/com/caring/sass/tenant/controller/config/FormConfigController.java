package com.caring.sass.tenant.controller.config;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.api.FormApi;
import com.caring.sass.nursing.dto.form.FormSaveDTO;
import com.caring.sass.nursing.dto.form.FormUpdateDTO;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.enumeration.FormEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author xinzh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/formConfig")
@Api(value = "formConfig", tags = "项目表单配置")
public class FormConfigController {

    public final FormApi formApi;

    public FormConfigController(FormApi formApi) {
        this.formApi = formApi;
    }

    /**
     * 新增表单
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @ApiOperation(value = "新增表单")
    @PostMapping(value = "/{code}")
    public R<Form> save(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                        @RequestBody @Validated FormSaveDTO saveDTO) {
        BaseContextHandler.setTenant(code);
        // 相同类型的表单是否需要校验？
        return formApi.save(saveDTO);
    }

    /**
     * 修改表单
     *
     * @param updateDTO 修改参数
     */
    @ApiOperation(value = "修改表单")
    @PutMapping(value = "/{code}")
    public R<Form> channelUpdate(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                 @RequestBody @Validated FormUpdateDTO updateDTO) {
        BaseContextHandler.setTenant(code);
        return formApi.update(updateDTO);
    }

    /**
     * 删除表单
     *
     * @param ids 需要删除的主键
     */
    @ApiOperation(value = "删除表单")
    @DeleteMapping(value = "/{code}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids[]", value = "主键id", dataType = "array", paramType = "query"),
    })
    public R<Boolean> channelDelete(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                    @RequestParam("ids[]") List<Long> ids) {
        BaseContextHandler.setTenant(code);
        return formApi.delete(ids);
    }

    /**
     * 查询项目所有表单
     */
    @ApiOperation("查询项目所有表单")
    @GetMapping("listForm/{code}")
    public R<List<Form>> listForm(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        return formApi.query(Form.builder().build());
    }

    /**
     * 查询表单
     *
     * @return 实体
     */
    @ApiOperation(value = "查询表单")
    @GetMapping(value = "/{code}")
    public R<Form> query(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                        @RequestParam("category") FormEnum category, @RequestParam(value = "bizId", required = false) String bizId) {
        BaseContextHandler.setTenant(code);
        return formApi.getFormByCategoryAndBizId(category, bizId);
    }

}
