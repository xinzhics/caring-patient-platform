package com.caring.sass.nursing.service.tag.bind;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.constant.TagAttrFieldType;
import com.caring.sass.nursing.dao.tag.AttrMapper;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.tag.AttrBindChangeDto;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.tag.Attr;
import com.caring.sass.nursing.entity.tag.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName FormBaseInfoHandle
 * @Description
 * @Author yangShuai
 * @Date 2022/4/24 14:44
 * @Version 1.0
 */
@Component
public class FormResultHandle extends AttrHandle {

    @Autowired
    TagComponent tagComponent;

    @Autowired
    AttrMapper attrMapper;

    @Override
    public void handle(AttrBindChangeDto attrBindChangeDto) {

        String tenantCode = attrBindChangeDto.getTenantCode();
        Long formResultId = attrBindChangeDto.getFormResultId();
        Long patientId = attrBindChangeDto.getPatientId();

        BaseContextHandler.setTenant(tenantCode);
        if (StrUtil.isEmpty(tenantCode)) {
            return;
        }
        if (Objects.isNull(formResultId) || Objects.isNull(patientId)) {
            return;
        }
        FormResult formResult = tagComponent.getFormResultById(formResultId);
        if (Objects.isNull(formResult)) {
            return;
        }
        String jsonContent = formResult.getJsonContent();
        if (StrUtil.isEmpty(jsonContent)) {
            return;
        }
        List<FormField> fields = JSON.parseArray(jsonContent, FormField.class);
        List<String> fieldIds = fields.stream()
                .filter(item ->
                        TagAttrFieldType.checkExistType(item.getWidgetType(), null) ||
                                TagAttrFieldType.checkExistType(item.getExactType(), null))
                .map(FormField::getId)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(fieldIds)) {
            return;
        }
        List<Attr> attrList = attrMapper.selectList(Wraps.<Attr>lbQ().in(Attr::getAttrId, fieldIds));
        if (CollUtil.isEmpty(attrList)) {
            return;
        }
        // 根据标签属性， 获取 对应的标签
        Set<Long> tagIds = attrList.stream().map(Attr::getTagId).collect(Collectors.toSet());
        List<Tag> tagList = tagComponent.getTagByTagIds(tagIds);
        if (CollUtil.isEmpty(tagList)) {
            return;
        }

        bindPatientTags(tagList, tagComponent, patientId);

    }


}
