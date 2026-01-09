package com.caring.sass.nursing.service.tag.bind;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.drugs.PatientDrugsMapper;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dao.tag.AssociationMapper;
import com.caring.sass.nursing.dao.tag.AttrMapper;
import com.caring.sass.nursing.dao.tag.TagMapper;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.tag.AttrBindNeedData;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.tag.Association;
import com.caring.sass.nursing.entity.tag.Attr;
import com.caring.sass.nursing.entity.tag.Tag;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.service.form.FormResultService;
import com.caring.sass.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName TagComponent
 * @Description
 * @Author yangShuai
 * @Date 2022/4/25 13:17
 * @Version 1.0
 */
@Component
public class TagComponent {

    @Autowired
    TagMapper tagMapper;

    @Autowired
    AttrMapper attrMapper;

    @Autowired
    FormResultService formResultService;

    @Autowired
    PatientDrugsMapper patientDrugsMapper;

    @Autowired
    AssociationMapper associationMapper;

    /**
     * 通过 标签的ID 获取 标签和标签的属性
     * @param idList
     * @return
     */
    public List<Tag> getTagByTagIds(Collection<? extends Serializable> idList) {

        if (CollUtil.isEmpty(idList)) {
            return new ArrayList<>();
        }
        List<Tag> tagList = tagMapper.selectBatchIds(idList);
        if (CollUtil.isEmpty(tagList)) {
            return new ArrayList<>();
        }
        for (Tag tag : tagList) {
            List<Attr> attrList = attrMapper.selectList(Wraps.<Attr>lbQ().eq(Attr::getTagId, tag.getId()));
            tag.setTagAttr(attrList);
        }
        return tagList;

    }

    /**
     * 用户不存在此标签。 则绑定
     * @param patientId
     * @param tagId
     */
    public void noExistBindPatientTag(Long patientId, Long tagId) {

        Integer count = associationMapper.selectCount(Wraps.<Association>lbQ()
                .eq(Association::getAssociationId, patientId.toString())
                .eq(Association::getTagId, tagId));
        if (count != null && count > 0) {
            return;
        }
        Association build = Association.builder().associationId(patientId.toString())
                .tagId(tagId).build();
        associationMapper.insert(build);
    }

    /**
     * 清除患者的一个标签
     * @param patientId
     * @param tagId
     */
    public void cleanPatientTag(Long patientId, Long tagId) {
        associationMapper.delete(Wraps.<Association>lbQ()
                .eq(Association::getAssociationId, patientId.toString())
                .eq(Association::getTagId, tagId));
    }


    /**
     * 获取 标签绑定需要的 数据
     * @param tagAttr
     * @return
     */
    public AttrBindNeedData getTagBindNeedData(List<Attr> tagAttr, Long patientId) {

        if (CollUtil.isEmpty(tagAttr)) {
            return null;
        }
        /**
         * 基本信息
         */
        FormResult baseInfo = null;
        /**
         * 疾病信息
         */
        FormResult healthRecord = null;
        /**
         * 监测计划表单的字段
         */
        List<FormField> monitoringPlanResult = new ArrayList<>();

        /**
         * 用户的用药
         */
        List<PatientDrugs> patientDrugsList = new ArrayList<>();

        for (Attr attr : tagAttr) {
            String attrSource = attr.getAttrSource();
            if (StrUtil.isEmpty(attrSource)) {
                // 查询 基本信息， 和疾病信息
                if (Objects.isNull(baseInfo)) {
                    baseInfo = getBaseInfoOrHealthRecord(patientId, FormEnum.BASE_INFO);
                }
                if (Objects.isNull(healthRecord)) {
                    healthRecord = getBaseInfoOrHealthRecord(patientId, FormEnum.HEALTH_RECORD);
                }
            } else if (baseInfo == null && Attr.BASE_INFO.equals(attrSource)) {
                baseInfo = getBaseInfoOrHealthRecord(patientId, FormEnum.BASE_INFO);
            } else if (healthRecord == null && Attr.HEALTH_RECORD.equals(attrSource)) {
                healthRecord = getBaseInfoOrHealthRecord(patientId, FormEnum.HEALTH_RECORD);
            } else if (Attr.MONITORING_PLAN.equals(attrSource)) {
                // 需要查询 患者最近的 监测计划
                Long planId = attr.getPlanId();
                if (Objects.isNull(planId)) {
                    continue;
                }
                // 查询用户最新的监测计划表单
                List<FormResult> formResults = formResultService.list(Wraps.<FormResult>lbQ()
                        .eq(FormResult::getUserId, patientId)
                        .eq(FormResult::getBusinessId, planId)
                        .orderByDesc(SuperEntity::getCreateTime)
                        .last(" limit 0, 1"));
                if (CollUtil.isNotEmpty(formResults)) {
                    FormResult formResult = formResults.get(0);
                    FormField formField = getAttrNeedFormField(attr, formResult);
                    if (Objects.nonNull(formField)) {
                        monitoringPlanResult.add(formField);
                    }
                }
            } else if (Attr.DRUG.equals(attrSource)) {
                String attrId = attr.getAttrId();
                if (StrUtil.isNotEmpty(attrId)) {
                    // 查询患者的用药
                    List<PatientDrugs> patientDrugs = patientDrugsMapper.selectList(Wraps.<PatientDrugs>lbQ()
                            .eq(PatientDrugs::getPatientId, patientId)
                            .eq(PatientDrugs::getDrugsId, attrId));
                    if (CollUtil.isNotEmpty(patientDrugs)) {
                        patientDrugsList.addAll(patientDrugs);
                    }
                }
            }
        }
        return new AttrBindNeedData(baseInfo, healthRecord, monitoringPlanResult, patientDrugsList);

    }

    /**
     * 获取 标签属性 绑定的表单字段
     * @param attr
     * @param formResult
     * @return
     */
    public FormField getAttrNeedFormField(Attr attr, FormResult formResult) {
        String jsonContent = formResult.getJsonContent();
        List<FormField> formFields = JSON.parseArray(jsonContent, FormField.class);

        // 查找表单字段中 和 标签属性绑定的字段
        List<FormField> fieldList = formFields.stream()
                .filter(item -> item.getId().equals(attr.getAttrId())).collect(Collectors.toList());

        // 有一个 属性 只关联一个 表单字段
        if (CollUtil.isNotEmpty(fieldList)) {
            return fieldList.get(0);
        }
        return null;

    }

    public FormResult getBaseInfoOrHealthRecord(Long patientId, FormEnum category) {
        return formResultService.getBasicOrHealthFormResult(patientId, category);
    }


    public FormResult getFormResultById(Long id) {

        return formResultService.getById(id);

    }


}
