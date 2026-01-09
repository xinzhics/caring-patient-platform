package com.caring.sass.nursing.service.tag.bind;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.tag.AttrMapper;
import com.caring.sass.nursing.dto.tag.AttrBindChangeDto;
import com.caring.sass.nursing.entity.tag.Attr;
import com.caring.sass.nursing.entity.tag.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName AddDrugHandle
 * @Description 添加用药触发
 * @Author yangShuai
 * @Date 2022/4/24 15:12
 * @Version 1.0
 */
@Component
public class DrugHandle extends AttrHandle {

    @Autowired
    AttrMapper attrMapper;

    @Autowired
    TagComponent tagComponent;


    @Override
    public void handle(AttrBindChangeDto attrBindChangeDto) {
        String tenantCode = attrBindChangeDto.getTenantCode();
        Long drugsId = attrBindChangeDto.getDrugsId();
        Long patientId = attrBindChangeDto.getPatientId();

        if (StrUtil.isEmpty(tenantCode)) {
            return;
        }
        if (Objects.isNull(drugsId) || Objects.isNull(patientId)) {
            return;
        }

        BaseContextHandler.setTenant(tenantCode);
        // 查询项目下 这个药品被什么 标签的属性绑定
        List<Attr> attrList = attrMapper.selectList(Wraps.<Attr>lbQ().eq(Attr::getAttrId, drugsId));

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
