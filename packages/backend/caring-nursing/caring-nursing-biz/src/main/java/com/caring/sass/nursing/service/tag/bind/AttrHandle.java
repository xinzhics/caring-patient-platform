package com.caring.sass.nursing.service.tag.bind;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormFieldExactType;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.dto.tag.AttrBindChangeDto;
import com.caring.sass.nursing.dto.tag.AttrBindNeedData;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.tag.Attr;
import com.caring.sass.nursing.entity.tag.Tag;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName AttrHandle
 * @Description
 * @Author yangShuai
 * @Date 2022/4/24 14:46
 * @Version 1.0
 */
@Slf4j
public abstract class AttrHandle {

    abstract void handle(AttrBindChangeDto attrBindChangeDto);

    /**
     * 绑定患者标签

     * @param tagList
     * @param tagComponent
     * @param patientId
     */
    void bindPatientTags(List<Tag> tagList, TagComponent tagComponent, Long patientId) {
        for (Tag tag : tagList) {
            bindPatientTag(tag, tagComponent, patientId);
        }
    }

    /**
     * 绑定患者标签
     * 每一个标签。都可能设置了 基本信息组件。 疾病信息组件。 监测计划组件， 和用药信息
     * 需要判断标签中的 每一个属性配置的值，用户都拥有，此时 用户才拥有此 标签
     * 根据标签的属性 。准备标签绑定需要的 用户数据
     * @param tag
     * @param tagComponent
     * @param patientId
     */
    void bindPatientTag(Tag tag, TagComponent tagComponent, Long patientId) {
        List<Attr> tagTagAttr = tag.getTagAttr();
        if (CollUtil.isEmpty(tagTagAttr)) {
            return;
        }
        AttrBindNeedData attrBindNeedData = tagComponent.getTagBindNeedData(tagTagAttr, patientId);
        boolean bind = checkTagBind(tagTagAttr, attrBindNeedData);
        if (bind) {
            tagComponent.noExistBindPatientTag(patientId, tag.getId());
        } else {
            tagComponent.cleanPatientTag(patientId, tag.getId());
        }
    }


    /**
     * 检查 标签 是否可以根据这些条件绑定
     * @param attrs         标签的属性 有可能是过去设置的标签。这是标签关联的表单可能 基本信息，也可能是疾病信息
     * @return
     */
    private boolean checkTagBind(List<Attr> attrs, AttrBindNeedData attrBindNeedData) {

        if(CollUtil.isEmpty(attrs)) {
            return false;
        }

        FormResult baseInfoForm = attrBindNeedData.getBaseInfo();
        FormResult healthRecodeForm = attrBindNeedData.getHealthRecord();
        List<FormField> formFieldList = attrBindNeedData.getMonitoringPlanResult();
        List<PatientDrugs> patientDrugs = attrBindNeedData.getPatientDrugsList();

        boolean checkStatus = true;
        for (Attr attr : attrs) {
            String attrSource = attr.getAttrSource();
            if (StringUtils.isEmpty(attr.getAttrSource())) {
                // 标签是旧的标签业务。
                // 使用旧的标签业务逻辑去处理。
                List<FormField> formFields = getFormFields(attr, baseInfoForm);
                if (CollUtil.isNotEmpty(formFields)) {
                    checkStatus = checkAttrFormField(attr, formFields);
                }
                formFields = getFormFields(attr, healthRecodeForm);
                if (CollUtil.isNotEmpty(formFields)) {
                    checkStatus = checkAttrFormField(attr, formFields);
                }
            } else if (Attr.BASE_INFO.equals(attrSource)) {
                checkStatus = checkAttrBaseInfo(attr, baseInfoForm);
            } else if (Attr.HEALTH_RECORD.equals(attrSource)) {
                checkStatus = checkAttrBaseInfo(attr, healthRecodeForm);
            } else if (Attr.MONITORING_PLAN.equals(attrSource)) {
                checkStatus = checkAttrFormField(attr, formFieldList);
            } else if (Attr.DRUG.equals(attrSource)) {
                checkStatus = checkAttrDrugs(attr, patientDrugs);
            } else {
                checkStatus = false;
            }

            // 一旦发现有一个条件不满足。直接退出循环
            if (!checkStatus) {
                break;
            }
        }


        return checkStatus;

    }

    /**
     * 检查 属性关联的 用药。判断用药状态是否符合
     * @param attr
     * @param patientDrugs
     * @return
     */
    private boolean checkAttrDrugs(Attr attr, List<PatientDrugs> patientDrugs) {

        if (Objects.isNull(attr)) {
            return false;
        }
        if (CollUtil.isEmpty(patientDrugs)) {
            return false;
        }
        String attrId = attr.getAttrId();
        String attrValue = attr.getAttrValue();
        if (StrUtil.isEmpty(attrId) || StrUtil.isEmpty(attrValue)) {
            return false;
        }

        boolean checkAttrDrugs = false;
        for (PatientDrugs drug : patientDrugs) {
            if (drug.getDrugsId().toString().equals(attrId)) {
                // 0  1  2
                Integer status = drug.getStatus();
                if (Objects.nonNull(status)) {
                    // 正在用药
                    if ("0".equals(attrValue)) {
                        // 判断是否 是正在用药
                        if (status.equals(0)) {
                            checkAttrDrugs = true;
                            break;
                        }
                    } else {
                        // 判断用户是否 停止用药， 可能是 1 或者 2
                        if (status.equals(1) || status.equals(2)) {
                            checkAttrDrugs = true;
                            break;
                        }
                    }

                }
            }
        }
        return checkAttrDrugs;
    }

    /**
     * 获取 表单的 字段
     * @param formResult
     * @return
     */
    private List<FormField> getFormFields(Attr attr, FormResult formResult) {

        if (Objects.isNull(attr) || Objects.isNull(formResult)) {
            return null;
        }

        if (StrUtil.isEmpty(attr.getAttrId())) {
            return null;
        }
        String jsonContent = formResult.getJsonContent();
        if (StringUtils.isEmpty(jsonContent)) {
            return null;
        }
        List<FormField> formFields = JSON.parseArray(jsonContent, FormField.class);

        List<FormField> fieldResults = new ArrayList<>();

        for (FormField formField : formFields) {
            String attrId = attr.getAttrId();
            if (attrId.equals(formField.getId())) {
                fieldResults.add(formField);
                break;
            }
        }
        return fieldResults;
    }

    /**
     * 检查基本信息
     * @param attr          标签的属性
     * @param formResult    基本信息或者疾病信息
     * @return
     */
    private  boolean checkAttrBaseInfo(Attr attr, FormResult formResult) {
        if (Objects.isNull(formResult) || Objects.isNull(attr)) {
            return false;
        }

        List<FormField> formFields = getFormFields(attr, formResult);
        if (formFields == null) {
            return false;
        }

        return checkAttrFormField(attr, formFields);

    }



    /**
     * 检查 表单字段 中的结果是否符合 标签属性的要求
     * @param attr
     * @param formFields
     * @return
     */
    private boolean checkAttrFormField(Attr attr, List<FormField> formFields) {

        if (CollUtil.isEmpty(formFields) || Objects.isNull(attr)) {
            return false;
        }
        FormField formField = getCustomFormFieldByAttrId(formFields, attr);
        if (Objects.isNull(formField)) {
            return false;
        }
        return checkAttrAttrValue(attr, formField);
    }


    /**
     * @return java.lang.Boolean
     * @Author yangShuai
     * @Description 检查表单的值和标签属性绑定的值是否符合
     * @Date 2020/10/28 16:18
     */
    private Boolean checkAttrAttrValue(Attr attr, FormField formField) {
        String attrValue = attr.getAttrValue();
        String attrValueMax = attr.getAttrValueMax();
        String attrValueMin = attr.getAttrValueMin();
        if (StringUtils.isEmpty(attrValue) && StringUtils.isEmpty(attrValueMax) &&  StringUtils.isEmpty(attrValueMin)) {
            log.info("attr的attrValue字段为空，用户标签失败，请检查该项目标签的配置是否正确");
            return false;
        }
        String widgetType = attr.getWidgetType();
        if (null == widgetType) {
            log.info("attr的widgetType字段为空，用户标签失败，请检查该项目标签的配置是否正确");
            return false;
        }
        if (formField == null) {
            log.info("CustomFormField为空，该用户可能并未填写个人信息或健康日志，无法对其进行标签比对</span>");
            return false;
        }
        String values = formField.getValues();
        List<String> valuesOrIds = parseResultValues(values);
        if (CollUtil.isEmpty(valuesOrIds)) {
            log.info("CustomFormField.getValues() = {}，转换为id数组时，数组为空，无法继续做对比，请仔细检查核对是否有误", values);
            return false;
        }

        String max;
        if (widgetType.equals(FormWidgetType.RADIO)
                || widgetType.equals(FormWidgetType.CHECK_BOX)
                || widgetType.equals(FormWidgetType.DROPDOWN_SELECT)
                || widgetType.equals(FormFieldExactType.HOSPITAL)) {
            for (String valuesOrId : valuesOrIds) {
                max = valuesOrId;
                if (Objects.equals(attrValue, max)) {
                    log.info("该属性要求的值为{},患者输入的值为{}，两者相匹配，因此该患者确实具备了此标签", attrValue, max);
                    return true;
                }
                log.info("该属性要求的值为{},患者输入的值为{}，两者不匹配，因此该患者不具备了此标签", attrValue, max);
            }
            return false;
        }
        log.info("已检查完所有radio、checkbox、DropdownSelect、，没有一个与用户输入值匹配，下面将对Number、Time、Date、DateTime类型的输入值做检查 ...");
        if (widgetType.equals(FormWidgetType.TIME)) {
            String value = valuesOrIds.get(0);
            max = attr.getAttrValueMax();
            String min = attr.getAttrValueMin();
            boolean result = compare(min, max, value);
            if (result) {
                log.info("该属性要求的值为min={},max={}，患者输入的值为{}，两者相匹配，因此该患者确实具备了此标签", min, max, value);
            } else {
                log.info("该属性要求的值为min={},max={}，患者输入的值为{}，两者不匹配，因此该患者不具备了此标签", min, max, value);
            }
            return result;
        } else if (widgetType.equals(FormWidgetType.DATE)) {
            String value = valuesOrIds.get(0);
            // 将日期中的 - 或者 / 去掉。
            // 将最大值和最小值的 - 或者 / 去掉。
            // 然后比对
            max = attr.getAttrValueMax();
            String min = attr.getAttrValueMin();
            return compareDate(min, max, value);
        } else if (widgetType.equals(FormWidgetType.ADDRESS)) {
            // 两个都是 省， 市， 区的json。那就先直接比对试试吧。
            String value = valuesOrIds.get(0);
            return compareAddress(attrValue, value);
        } else if (widgetType.equals(FormWidgetType.NUMBER)
                || widgetType.equals(FormFieldExactType.HEIGHT)
                || widgetType.equals(FormFieldExactType.WEIGHT)
                || widgetType.equals(FormFieldExactType.BMI)
                || widgetType.equals(FormFieldExactType.MONITORING_INDICATORS)) {
            // 这些字段。结果都是数据类型。
            String value = valuesOrIds.get(0);
            max = attr.getAttrValueMax();
            String min = attr.getAttrValueMin();
            return compareNumber(min, max, value);
        }
        String value = valuesOrIds.get(0);
        max = attr.getAttrValueMax();
        String min = attr.getAttrValueMin();
        boolean result = compare(min, max, value);
        if (result) {
            log.info("该属性要求的值为min={},max={}，患者输入的值为{}，两者相匹配，因此该患者确实具备了此标签", min, max, value);
        } else {
            log.info("该属性要求的值为min={},max={}，患者输入的值为{}，两者不匹配，因此该患者不具备了此标签", min, max, value);
        }
        return result;
    }


    private static boolean compareAddress(String attrValue, String value) {
        if (StrUtil.isEmpty(value)) {
            return false;
        }
        JSONArray valueArray = JSON.parseArray(value);
        JSONArray attrValueArray = JSON.parseArray(attrValue);
        if (valueArray.size() == attrValueArray.size()) {
            return attrValue.equals(value);
        }
        boolean compare = false;
        for (int i = 0; i < attrValueArray.size(); i++) {
            if (i < valueArray.size()) {
                Object o = attrValueArray.get(i);
                Object o1 = valueArray.get(i);
                if (o.toString().equals(o1.toString())) {
                    compare = true;
                }
            }
        }

        return compare;

    }

    private static boolean compareNumber(String min, String max, String target) {
        if (StrUtil.isEmpty(target)) {
            return false;
        }
        target = target.replace("-", "");
        boolean minNotEmpty = StrUtil.isNotEmpty(min);
        boolean maxNotEmpty = StrUtil.isNotEmpty(max);
        if (minNotEmpty && maxNotEmpty) {
            BigDecimal minDecimal = new BigDecimal(min);
            BigDecimal maxDecimal = new BigDecimal(max);
            BigDecimal targetDecimal = new BigDecimal(target);
            if (minDecimal.compareTo(targetDecimal) <= 0 && targetDecimal.compareTo(maxDecimal) <= 0) {
                return true;
            }
        } else if (minNotEmpty) {
            BigDecimal minDecimal = new BigDecimal(min);
            BigDecimal targetDecimal = new BigDecimal(target);
            if (minDecimal.compareTo(targetDecimal) <= 0) {
                return true;
            }
        } else {
            BigDecimal maxDecimal = new BigDecimal(max);
            BigDecimal targetDecimal = new BigDecimal(target);
            if (targetDecimal.compareTo(maxDecimal) <= 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean compareDate(String min, String max, String target) {
        min = min.replace("-", "");
        min = min.replace("/", "");
        max = max.replace("-", "");
        max = max.replace("/", "");
        target = target.replace("-", "");
        target = target.replace("/", "");
        return compare(min, max, target);
    }


    private static boolean compare(String min, String max, String target) {
        boolean notEmptystartTime = StrUtil.isNotEmpty(min);
        boolean notEmptyendTime = StrUtil.isNotEmpty(max);
        if (StrUtil.isEmpty(target)) {
            return false;
        }

        if ((notEmptyendTime) && (notEmptystartTime)) {
            return (compare(target, min)) && (compare(max, target));
        }

        if ((!notEmptyendTime) && (notEmptystartTime)) {
            return compare(target, min);
        }

        if (notEmptyendTime) {
            return compare(max, target);
        }
        return false;
    }

    public static void main(String[] args) {

        List<String> strings = parseResultValues("[{\"attrValue\":[\"河北省\",\"邢台市\",\"内丘县\"]}]");
        String s = strings.get(0);
        boolean address = compareAddress("[\"河北省\"]", s);
        System.out.println(address);
    }

    private static boolean compare(String a, String b) {
        return a.compareTo(b) > 0;
    }

    /**
     * 格式化 表单 字段的结果
     * @param values
     * @return
     */
    private static List<String> parseResultValues(String values) {
        if (StringUtils.isEmpty(values)) {
            return null;
        }
        JSONArray array = JSON.parseArray(values);

        List<String> resultValues = new ArrayList(array.size());
        Iterator localIterator;
        if (CollUtil.isNotEmpty(array)) {
            for (localIterator = array.iterator(); localIterator.hasNext(); ) {
                Object o = localIterator.next();
                JSONObject jsonObject = JSON.parseObject(o.toString());
                Object attrValue = jsonObject.get("attrValue");
                if (Objects.nonNull(attrValue)) {
                    resultValues.add(attrValue.toString());
                }
            }
        }
        return resultValues;
    }

    /**
     * @return com.caring.sass.nursing.dto.form.FormField
     * @Author yangShuai
     * @Description 获取表单中 被标签属性关联的组件
     * @Date 2020/10/28 16:18
     */
    private FormField getCustomFormFieldByAttrId(List<FormField> allFields, Attr attr) {
        if (Objects.nonNull(attr) && StringUtils.isNotEmptyString(attr.getAttrId())) {
            for (FormField allField : allFields) {
                if (allField.getId().equals(attr.getAttrId())) {
                    return allField;
                }
            }
        }
        return null;
    }


}
