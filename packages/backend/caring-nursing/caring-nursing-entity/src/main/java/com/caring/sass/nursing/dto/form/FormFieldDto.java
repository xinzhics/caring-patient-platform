package com.caring.sass.nursing.dto.form;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.common.utils.JsonUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName FormFieldDto
 * @Description
 * @Author yangShuai
 * @Date 2020/11/2 15:17
 * @Version 1.0
 */

public class FormFieldDto extends FormField {

    // APP 端 取值的 属性
    public JSONArray getResultValues() {
        if (this.values != null && this.values.length() > 0) {
            JSONArray jsonArray = JSONArray.parseArray(this.values);
            if (jsonArray.size() == 0) {
                String str = "[{attrValue: ''}]";
                return JSONArray.parseArray(str);
            }
            return jsonArray;
        } else {
            String str = "[{attrValue: ''}]";
            return JSONArray.parseArray(str);
        }
    }

    public static boolean isNumeric(String str) {
        try {
            new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String parseValue() {
        try {
            if (this.values != null) {
                JSONArray ja = JsonUtils.String2JSONArray(this.values);
                if ((ja != null) && (ja.size() > 0)) {
                    StringBuffer sb = new StringBuffer();
                    int size = ja.size();
                    for (int i = 0; i < size; i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        if (jo == null) {
                            continue;
                        }
                        if (sb.length() > 0)
                            sb.append("、").append(jo.getString("attrValue"));
                        else {
                            sb.append(jo.getString("attrValue"));
                        }
                    }
                    return sb.toString();
                }
            } else {
                return this.value;
            }
        } catch (Exception localException) {
        }
        return null;
    }

    public String parseResultValues() {
        if (StringUtils.isEmpty(this.values))
            return "";
        JSONArray array = JSONArray.parseArray(this.values);

        List resultValues = new ArrayList();
        Iterator localIterator;
        if (CollUtil.isNotEmpty(array)) {
            for (localIterator = array.iterator(); localIterator.hasNext(); ) {
                Object o = localIterator.next();
                JSONObject jsonObject = JSONObject.parseObject(o.toString());
                if (Objects.nonNull(jsonObject.get("valueText"))) {
                    resultValues.add(jsonObject.get("valueText").toString());
                } else {
                    Object attrValue = jsonObject.get("attrValue");
                    if (Objects.nonNull(attrValue)) {
                        resultValues.add(attrValue.toString());
                    }
                }
            }
        }
        return String.join("、", resultValues);
    }

    public static List<String> parseResultValues(String values) {
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

    public List<String> parseResultValuesOrIds() {
        if (StringUtils.isEmpty(this.values)) {
            return null;
        }
        JSONArray array = JSONArray.parseArray(this.values);

        List resultValues = new ArrayList();
        Iterator localIterator;
        if (CollUtil.isNotEmpty(array)) {
            for (localIterator = array.iterator(); localIterator.hasNext(); ) {
                Object o = localIterator.next();
                JSONObject jsonObject = JSONObject.parseObject(o.toString());
                Object attrValue = jsonObject.get("attrValue");
                if (Objects.nonNull(attrValue) && attrValue.toString().length() > 0) {
                    resultValues.add(attrValue.toString());
                }
            }
        }
        return resultValues;
    }


}
