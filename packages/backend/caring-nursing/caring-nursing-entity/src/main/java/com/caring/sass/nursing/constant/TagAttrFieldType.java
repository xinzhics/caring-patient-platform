package com.caring.sass.nursing.constant;

import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.nursing.dto.form.FormFieldExactType;
import com.caring.sass.nursing.dto.form.FormWidgetType;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName TagAttrFieldType
 * @Description
 * @Author yangShuai
 * @Date 2022/4/22 14:08
 * @Version 1.0
 */
public class TagAttrFieldType {

    /**
     * 字段的 扩展字段
     */
    public static final String EXACT_TYPE = "exactType";

    /**
     * 组件类型
     */
    private static Set<String> fieldWidgetTypes = new HashSet<>();

    /**
     * 组件的额外类型
     */
    private static Set<String> fieldExactTypes = new HashSet<>();


    static {
        fieldWidgetTypes.add(FormWidgetType.RADIO);
        fieldWidgetTypes.add(FormWidgetType.CHECK_BOX);
        fieldWidgetTypes.add(FormWidgetType.DROPDOWN_SELECT);
        fieldWidgetTypes.add(FormWidgetType.ADDRESS);

        fieldWidgetTypes.add(FormWidgetType.NUMBER);
        fieldWidgetTypes.add(FormWidgetType.TIME);
        fieldWidgetTypes.add(FormWidgetType.DATE);

        fieldExactTypes.add(FormFieldExactType.HEIGHT);
        fieldExactTypes.add(FormFieldExactType.WEIGHT);
        fieldExactTypes.add(FormFieldExactType.BMI);
        fieldExactTypes.add(FormFieldExactType.HOSPITAL);
        fieldExactTypes.add(FormFieldExactType.MONITORING_INDICATORS);
    }

    /**
     * 判断 类型是否 是标签属性需要的类型
     * @param type
     * @param fieldField
     * @return
     */
    public static boolean checkExistType(String type, String fieldField) {
        if (StringUtils.isNotEmptyString(fieldField)) {
            if (EXACT_TYPE.equals(fieldField)) {
                if (fieldExactTypes.contains(type)) {
                    return true;
                }
            }
        } else {
            if (fieldWidgetTypes.contains(type) || fieldExactTypes.contains(type)) {
                return true;
            }
        }

        return false;
    }







}
