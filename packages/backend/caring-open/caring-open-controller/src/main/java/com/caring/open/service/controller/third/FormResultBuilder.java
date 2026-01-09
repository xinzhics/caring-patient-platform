package com.caring.open.service.controller.third;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.caring.open.service.entity.third.DateReportReq;
import com.caring.sass.base.BaseEnum;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.enumeration.FormEnum;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xinz
 */
public class FormResultBuilder {

    /**
     * 表单提交的json
     */
    public static String formJson = "{\"businessId\":\"1613023633469341696\",\"fieldList\":[{\"defineChooseDate\":2,\"enableSmsCheck\":false,\"exactType\":\"FormResultCheckTime\",\"hasOtherOption\":2,\"id\":\"ec40d1796c9946399e58e6abc11d83cc\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"测量日期\",\"labelDesc\":\"\",\"minValue\":\"1945-01-01\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"请选择日期\",\"required\":false,\"values\":[{\"attrValue\":\"2023-01-13\",\"attrText\":\"2023-01-13\"}],\"widgetType\":\"Date\"},{\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"errorWhenEmpty\":\"\",\"hasOtherOption\":2,\"id\":\"06e78228d8d34cec9e87badfa994a8ec\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"上次用餐时间\",\"labelDesc\":\"\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"请选择时间\",\"required\":true,\"values\":[{\"attrValue\":\"12:00\",\"attrText\":\"12:00\"}],\"widgetType\":\"Time\"},{\"canDecimal\":1,\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"exactType\":\"monitoringIndicators\",\"firstWriteAsReferenceValue\":false,\"hasOtherOption\":2,\"hasTrendReference\":false,\"id\":\"a42725f5d6344fa182d197e31ca6a087\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"血糖\",\"labelDesc\":\"\",\"maxValue\":\"20\",\"minValue\":\"0\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"\",\"precision\":1,\"required\":true,\"rightUnit\":\"mmol/L\",\"showReferenceTargetValue\":false,\"showTrend\":1,\"trendReferenceExactValue\":\"0\",\"trendReferenceMax\":\"0\",\"trendReferenceMin\":\"0\",\"values\":[{\"attrValue\":\"6\"}],\"widgetType\":\"Number\"},{\"canDecimal\":1,\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"exactType\":\"monitoringIndicators\",\"firstWriteAsReferenceValue\":false,\"hasOtherOption\":2,\"hasTrendReference\":false,\"id\":\"ac07e93695bf48f9845057fc8dd0dfac\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"血氧\",\"labelDesc\":\"\",\"maxValue\":\"100\",\"minValue\":\"0\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"\",\"precision\":2,\"required\":true,\"rightUnit\":\"%\",\"showReferenceTargetValue\":false,\"showTrend\":1,\"trendReferenceExactValue\":\"0\",\"trendReferenceMax\":\"0\",\"trendReferenceMin\":\"0\",\"values\":[{\"attrValue\":\"96\"}],\"widgetType\":\"Number\"},{\"canDecimal\":2,\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"exactType\":\"monitoringIndicators\",\"firstWriteAsReferenceValue\":false,\"hasOtherOption\":2,\"hasTrendReference\":false,\"id\":\"6c99e1ff5aac4f709fa6220f2d08ec06\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"心率\",\"labelDesc\":\"\",\"maxValue\":\"130\",\"minValue\":\"0\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"\",\"required\":true,\"rightUnit\":\"BPM\",\"showReferenceTargetValue\":false,\"showTrend\":1,\"trendReferenceExactValue\":\"0\",\"trendReferenceMax\":\"0\",\"trendReferenceMin\":\"0\",\"values\":[{\"attrValue\":\"75\"}],\"widgetType\":\"Number\"}],\"formId\":\"1613024303689760768\",\"jsonContent\":\"[{\\\"defineChooseDate\\\":2,\\\"enableSmsCheck\\\":false,\\\"exactType\\\":\\\"FormResultCheckTime\\\",\\\"hasOtherOption\\\":2,\\\"id\\\":\\\"ec40d1796c9946399e58e6abc11d83cc\\\",\\\"isUpdatable\\\":1,\\\"itemAfterHasEnter\\\":2,\\\"itemAfterHasEnterRequired\\\":2,\\\"itemAfterHasEnterSize\\\":120,\\\"label\\\":\\\"测量日期\\\",\\\"labelDesc\\\":\\\"\\\",\\\"minValue\\\":\\\"1945-01-01\\\",\\\"modifyTags\\\":false,\\\"otherEnterRequired\\\":2,\\\"otherValueSize\\\":120,\\\"placeholder\\\":\\\"请选择日期\\\",\\\"required\\\":false,\\\"values\\\":[{\\\"attrValue\\\":\\\"2023-01-13\\\",\\\"attrText\\\":\\\"2023-01-13\\\"}],\\\"widgetType\\\":\\\"Date\\\"},{\\\"cancelRegistrationProcessAppear\\\":false,\\\"defineChooseDate\\\":2,\\\"enableSmsCheck\\\":false,\\\"errorWhenEmpty\\\":\\\"\\\",\\\"hasOtherOption\\\":2,\\\"id\\\":\\\"06e78228d8d34cec9e87badfa994a8ec\\\",\\\"isUpdatable\\\":1,\\\"itemAfterHasEnter\\\":2,\\\"itemAfterHasEnterRequired\\\":2,\\\"itemAfterHasEnterSize\\\":120,\\\"label\\\":\\\"上次用餐时间\\\",\\\"labelDesc\\\":\\\"\\\",\\\"modifyTags\\\":false,\\\"otherEnterRequired\\\":2,\\\"otherValueSize\\\":120,\\\"placeholder\\\":\\\"请选择时间\\\",\\\"required\\\":true,\\\"values\\\":[{\\\"attrValue\\\":\\\"12:00\\\",\\\"attrText\\\":\\\"12:00\\\"}],\\\"widgetType\\\":\\\"Time\\\"},{\\\"canDecimal\\\":1,\\\"cancelRegistrationProcessAppear\\\":false,\\\"defineChooseDate\\\":2,\\\"enableSmsCheck\\\":false,\\\"exactType\\\":\\\"monitoringIndicators\\\",\\\"firstWriteAsReferenceValue\\\":false,\\\"hasOtherOption\\\":2,\\\"hasTrendReference\\\":false,\\\"id\\\":\\\"a42725f5d6344fa182d197e31ca6a087\\\",\\\"isUpdatable\\\":1,\\\"itemAfterHasEnter\\\":2,\\\"itemAfterHasEnterRequired\\\":2,\\\"itemAfterHasEnterSize\\\":120,\\\"label\\\":\\\"血糖\\\",\\\"labelDesc\\\":\\\"\\\",\\\"maxValue\\\":\\\"20\\\",\\\"minValue\\\":\\\"0\\\",\\\"modifyTags\\\":false,\\\"otherEnterRequired\\\":2,\\\"otherValueSize\\\":120,\\\"placeholder\\\":\\\"\\\",\\\"precision\\\":1,\\\"required\\\":true,\\\"rightUnit\\\":\\\"mmol/L\\\",\\\"showReferenceTargetValue\\\":false,\\\"showTrend\\\":1,\\\"trendReferenceExactValue\\\":\\\"0\\\",\\\"trendReferenceMax\\\":\\\"0\\\",\\\"trendReferenceMin\\\":\\\"0\\\",\\\"values\\\":[{\\\"attrValue\\\":\\\"6\\\"}],\\\"widgetType\\\":\\\"Number\\\"},{\\\"canDecimal\\\":1,\\\"cancelRegistrationProcessAppear\\\":false,\\\"defineChooseDate\\\":2,\\\"enableSmsCheck\\\":false,\\\"exactType\\\":\\\"monitoringIndicators\\\",\\\"firstWriteAsReferenceValue\\\":false,\\\"hasOtherOption\\\":2,\\\"hasTrendReference\\\":false,\\\"id\\\":\\\"ac07e93695bf48f9845057fc8dd0dfac\\\",\\\"isUpdatable\\\":1,\\\"itemAfterHasEnter\\\":2,\\\"itemAfterHasEnterRequired\\\":2,\\\"itemAfterHasEnterSize\\\":120,\\\"label\\\":\\\"血氧\\\",\\\"labelDesc\\\":\\\"\\\",\\\"maxValue\\\":\\\"100\\\",\\\"minValue\\\":\\\"0\\\",\\\"modifyTags\\\":false,\\\"otherEnterRequired\\\":2,\\\"otherValueSize\\\":120,\\\"placeholder\\\":\\\"\\\",\\\"precision\\\":2,\\\"required\\\":true,\\\"rightUnit\\\":\\\"%\\\",\\\"showReferenceTargetValue\\\":false,\\\"showTrend\\\":1,\\\"trendReferenceExactValue\\\":\\\"0\\\",\\\"trendReferenceMax\\\":\\\"0\\\",\\\"trendReferenceMin\\\":\\\"0\\\",\\\"values\\\":[{\\\"attrValue\\\":\\\"96\\\"}],\\\"widgetType\\\":\\\"Number\\\"},{\\\"canDecimal\\\":2,\\\"cancelRegistrationProcessAppear\\\":false,\\\"defineChooseDate\\\":2,\\\"enableSmsCheck\\\":false,\\\"exactType\\\":\\\"monitoringIndicators\\\",\\\"firstWriteAsReferenceValue\\\":false,\\\"hasOtherOption\\\":2,\\\"hasTrendReference\\\":false,\\\"id\\\":\\\"6c99e1ff5aac4f709fa6220f2d08ec06\\\",\\\"isUpdatable\\\":1,\\\"itemAfterHasEnter\\\":2,\\\"itemAfterHasEnterRequired\\\":2,\\\"itemAfterHasEnterSize\\\":120,\\\"label\\\":\\\"心率\\\",\\\"labelDesc\\\":\\\"\\\",\\\"maxValue\\\":\\\"130\\\",\\\"minValue\\\":\\\"0\\\",\\\"modifyTags\\\":false,\\\"otherEnterRequired\\\":2,\\\"otherValueSize\\\":120,\\\"placeholder\\\":\\\"\\\",\\\"required\\\":true,\\\"rightUnit\\\":\\\"BPM\\\",\\\"showReferenceTargetValue\\\":false,\\\"showTrend\\\":1,\\\"trendReferenceExactValue\\\":\\\"0\\\",\\\"trendReferenceMax\\\":\\\"0\\\",\\\"trendReferenceMin\\\":\\\"0\\\",\\\"values\\\":[{\\\"attrValue\\\":\\\"75\\\"}],\\\"widgetType\\\":\\\"Number\\\"}]\",\"messageId\":\"\",\"name\":\"血糖监测\",\"userId\":\"1613021426049089536\"}";

    @Getter
    public static class ValueClass {
        private String attrText;
        private String attrValue;

        public ValueClass(String attrText, String attrValue) {
            this.attrText = attrText;
            this.attrValue = attrValue;
        }

        public String toJson() {
            ArrayList<ValueClass> javaObject = new ArrayList<>();
            javaObject.add(this);
            return JSON.toJSON(javaObject).toString();
        }
    }


    /**
     * 这里数据结构先写死
     *
     * @param dateReport 血糖仪上报数据
     * @return 表单
     */
    public static FormResult build(DateReportReq dateReport, Long userId) {
        FormResult formResult = JSON.parseObject(formJson, FormResult.class);
        formResult.setCategory(FormEnum.NURSING_PLAN);
        formResult.setUserId(userId);
        List<FormField> resultFields = JSON.parseArray(formResult.getJsonContent(), FormField.class);

        for (FormField resultField : resultFields) {
            String label = resultField.getLabel();
            switch (label) {
                case "测量日期":
                    String today = DateUtil.today();
                    resultField.setValues(new ValueClass(today, today).toJson());
                    break;
                case "上次用餐时间":
                    resultField.setValues(new ValueClass(dateReport.getMealTime(), dateReport.getMealTime()).toJson());
                    break;
                case "血糖":
                    resultField.setValues("[{\"attrValue\":\"" + dateReport.getGlucose() + "\"}]");
                    break;
                case "血氧":
                    resultField.setValues("[{\"attrValue\":\"" + dateReport.getSpo2() + "\"}]");
                    break;
                case "心率":
                    resultField.setValues("[{\"attrValue\":\"" + dateReport.getRate() + "\"}]");
                    break;
                default:
                    break;
            }
        }
        formResult.setJsonContent(JSON.toJSONString(resultFields));
        formResult.setCategory(FormEnum.NURSING_PLAN);
        return formResult;
    }


}
