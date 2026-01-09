package com.caring.sass.tenant.controller.config;


import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.R;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.api.AttrApi;
import com.caring.sass.nursing.api.FormApi;
import com.caring.sass.nursing.api.TagApi;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.tag.TagUpsertDTO;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.tag.Attr;
import com.caring.sass.nursing.entity.tag.Tag;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.util.FormUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目基础配置
 *
 * @author leizhi
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/tagConfig")
@Api(value = "TagConfig", tags = "项目标签配置")
public class TagConfigController {

    private final TagApi tagApi;
    private final AttrApi attrApi;
    private final FormApi formApi;

    public TagConfigController(TagApi tagApi, AttrApi attrApi, FormApi formApi) {
        this.tagApi = tagApi;
        this.attrApi = attrApi;
        this.formApi = formApi;
    }

    /**
     * 查询项目标签
     */
    @ApiOperation("查询项目标签")
    @GetMapping("list/{code}")
    public R<List<Tag>> list(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        Tag tag = Tag.builder().build();
        return tagApi.query(tag);
    }


    /**
     * 查询标签及标签属性
     */
    @ApiOperation("查询标签及标签属性")
    @GetMapping("getTagWithAttr/{code}")
    public R<TagUpsertDTO> getTagWithAttr(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                          @RequestParam(value = "id") @NotNull(message = "id不能为空") Long id) {
        BaseContextHandler.setTenant(code);
        return tagApi.getTagWithAttr(id);
    }

    /**
     * 配置标签
     */
    @ApiOperation("配置标签,主键存在更新，不存在插入")
    @PostMapping("tag/{code}")
    public R<Boolean> saveTag(@RequestBody @Validated TagUpsertDTO tagUpsertDTO, @PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        return tagApi.upsert(tagUpsertDTO);
    }

    /**
     * 删除标签
     */
    @ApiOperation("删除标签")
    @DeleteMapping("tag/{code}")
    public R<Boolean> delTag(@RequestParam("ids[]") List<Long> ids, @PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        tagApi.delete(ids);
        return R.success();
    }

    /**
     * 查询标签所有属性
     */
    @ApiOperation("查询标签所有属性")
    @GetMapping("listAttr/{code}")
    public R<List<Attr>> listAttr(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                  @RequestParam("id") @NotNull(message = "标签id不能为空") Long id) {
        BaseContextHandler.setTenant(code);
        R<List<Attr>> list = attrApi.query(Attr.builder().tagId(id).build());
        return R.success(list.getData());
    }

    /**
     * 查询标签属性选项
     */
    @ApiOperation("查询标签属性选项")
    @GetMapping("listAttrOptions/{code}")
    @Deprecated
    public R<List<FormField>> listAttrOptions(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        List<FormField> fieldList = new ArrayList<>();
        List<Form> baseForms = formApi.query(Form.builder().category(FormEnum.BASE_INFO).build()).getData();
        List<Form> healthForms = formApi.query(Form.builder().category(FormEnum.HEALTH_RECORD).build()).getData();
        if (CollUtil.isNotEmpty(baseForms)) {
            fieldList.addAll(FormUtil.str2FormField(baseForms.get(0).getFieldsJson()));
        }
        if (CollUtil.isNotEmpty(healthForms)) {
            fieldList.addAll(FormUtil.str2FormField(healthForms.get(0).getFieldsJson()));
        }
        return R.success(fieldList);
    }
}
