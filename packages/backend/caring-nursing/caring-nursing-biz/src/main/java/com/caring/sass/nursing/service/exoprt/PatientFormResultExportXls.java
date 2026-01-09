package com.caring.sass.nursing.service.exoprt;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.form.FormFieldExactType;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.entity.form.*;
import com.caring.sass.nursing.enumeration.FormResultCountScoreEnum;
import com.caring.sass.user.entity.Patient;
import io.swagger.models.auth.In;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 患者随访记录 的导出表格
 *
 */
public class PatientFormResultExportXls extends ExportXls {


    @Override
    public XSSFWorkbook getHSSWordBook(String sheetName) {
        return super.getHSSWordBook(sheetName);
    }

    /**
     * 设置字段名称
     * @param workbook
     * @param formList
     * @return
     */
    public  Integer setFieldName(XSSFWorkbook workbook, List<Form> formList, Form baseinfoForm,
                                 Map<String, Integer> planFormFieldIdx,
                                 Map<Long, FormScoreRule> scoreRuleMap,  Map<Long, List<FormFieldsGroup>> formFieldGroupMap) {
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = createRow(sheet,3);
        int cellIdx = 0;
        XSSFCell cell;
        int formIdx = 0;
        Integer lastCell = 0;
        CellStyle style = getTitleStyle(formIdx);
        cell = row.createCell(cellIdx++);
        cell.setCellValue("序号");
        cell.setCellStyle(style);
        lastCell++;
        cell = row.createCell(cellIdx++);
        cell.setCellValue("患者ID");
        cell.setCellStyle(style);
        lastCell++;

        cell = row.createCell(cellIdx++);
        cell.setCellValue("创建时间");
        cell.setCellStyle(style);
        lastCell++;

        cell = row.createCell(cellIdx++);
        cell.setCellValue("姓名");
        cell.setCellStyle(style);
        lastCell++;

        cell = row.createCell(cellIdx++);
        cell.setCellValue("所属医生");
        cell.setCellStyle(style);
        lastCell++;

        cell = row.createCell(cellIdx++);
        cell.setCellValue("所属医助");
        cell.setCellStyle(style);
        lastCell++;

        if (Objects.nonNull(baseinfoForm)) {
            String formFieldsJson = baseinfoForm.getFieldsJson();
            List<FormField> formFields = JSON.parseArray(formFieldsJson, FormField.class);
            for (FormField formField : formFields) {
                if (exportFieldCheck(formField.getWidgetType())) {
                    cell = row.createCell(cellIdx++);
                    cell.setCellValue(formField.getLabel());
                    cell.setCellStyle(style);
                }
            }
        }

        for (Form form : formList) {
            if (formIdx == 0) {
                formIdx = 1;
            } else {
                formIdx = 0;
            }
            planFormFieldIdx.put(form.getBusinessId(), cellIdx);
            style = getTitleStyle(formIdx);
            cell = row.createCell(cellIdx++);
            cell.setCellValue("NO.");
            cell.setCellStyle(style);
            lastCell++;

            String fieldsJson = form.getFieldsJson();
            List<FormField> formFields = JSON.parseArray(fieldsJson, FormField.class);
            if (Objects.nonNull(form.getScoreQuestionnaire()) && form.getScoreQuestionnaire() == 1) {
                FormScoreRule scoreRule = scoreRuleMap.get(form.getId());
                List<FormFieldsGroup> fieldsGroups = formFieldGroupMap.get(form.getId());
                for (FormField formField : formFields) {
                    if (exportFieldCheck(formField.getWidgetType())) {
                        cell = row.createCell(cellIdx++);
                        cell.setCellValue(formField.getLabel());
                        cell.setCellStyle(style);
                        // 如果是个评分单选。要多设置一个。
                        if (FormFieldExactType.SCORING_SINGLE_CHOICE.equals(formField.getExactType())) {
                            CellRangeAddress region0 = new CellRangeAddress(3, 3, cellIdx - 1, cellIdx);
                            sheet.addMergedRegion(region0);
                            cell = row.createCell(cellIdx++);
                            cell.setCellStyle(style);
                        }
                    }
                }
                if (Objects.nonNull(scoreRule)) {
                    if (Objects.nonNull(scoreRule.getShowResultSum()) && scoreRule.getShowResultSum() == 1) {
                        cell = row.createCell(cellIdx++);
                        cell.setCellValue("总分");
                        cell.setCellStyle(style);
                    }
                    if (Objects.nonNull(scoreRule.getShowAverage()) && scoreRule.getShowAverage() == 1) {
                        cell = row.createCell(cellIdx++);
                        cell.setCellValue("平均分");
                        cell.setCellStyle(style);
                    }
                    if (Objects.nonNull(scoreRule.getShowGroupSum()) && scoreRule.getShowGroupSum() == 1) {
                        for (FormFieldsGroup group : fieldsGroups) {
                            cell = row.createCell(cellIdx++);
                            cell.setCellValue(group.getGroupName());
                            cell.setCellStyle(style);
                        }
                    }
                }
            } else {
                for (FormField formField : formFields) {
                    if (exportFieldCheck(formField.getWidgetType())) {
                        cell = row.createCell(cellIdx++);
                        cell.setCellValue(formField.getLabel());
                        cell.setCellStyle(style);
                        lastCell++;
                    }
                }
            }
        }

        return lastCell;

    }

    /**
     * 设置表头。设置第三行的表单项
     * @param workbook
     * @param title1
     * @param title2
     * @param formList
     */
    public void setTitle(XSSFWorkbook workbook, String title1, String title2, Form baseInfoForm, List<Form> formList, Map<Long,
            FormScoreRule> scoreRuleMap, Map<Long, List<FormFieldsGroup>> formFieldGroupMap) {

        setFirstOneRowTitle(workbook, title1);
        setSecondOneRowTitle(workbook, title2);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = createRow(sheet,2);
        int lastCol = 0;
        int firstCol = 0;
        int formIdx = 0;
        CellStyle style = getTitleStyle(formIdx);
        XSSFCell cell = row.createCell(lastCol++);
        cell.setCellValue("基本信息");
        cell.setCellStyle(style);
        // 序号
        cell = row.createCell(lastCol++);
        cell.setCellStyle(style);
        // ID
        cell = row.createCell(lastCol++);
        cell.setCellStyle(style);
        // 创建时间
        cell = row.createCell(lastCol++);
        cell.setCellStyle(style);
        // 姓名
        cell = row.createCell(lastCol++);
        cell.setCellStyle(style);
        // 所属医生
        cell = row.createCell(lastCol++);
        cell.setCellStyle(style);

        List<FormField> formFields;
        // 使用基本信息表单 创建一个新的表头并合并
        String formFieldsJson = baseInfoForm.getFieldsJson();
        if (StrUtil.isNotEmpty(formFieldsJson)) {
            formFields = JSON.parseArray(formFieldsJson, FormField.class);
            // 其他的基本信息
            for (FormField field : formFields) {
                if (exportFieldCheck(field.getWidgetType())) {
                    cell = row.createCell(lastCol++);
                    cell.setCellStyle(style);
                }
            }
        }

        formIdx = 1;
        CellRangeAddress region0 = new CellRangeAddress(2, 2, firstCol, lastCol -1);
        sheet.addMergedRegion(region0);
        for (Form form : formList) {
            style = getTitleStyle(formIdx);
            if (formIdx == 0) {
                formIdx = 1;
            } else {
                formIdx = 0;
            }
            firstCol = lastCol;
            cell = row.createCell(lastCol++);
            cell.setCellValue(form.getName());
            cell.setCellStyle(style);
            String fieldsJson = form.getFieldsJson();
            formFields = JSON.parseArray(fieldsJson, FormField.class);
            if (formFields.isEmpty()) {
                continue;
            } else {
                if (Objects.nonNull(form.getScoreQuestionnaire()) && form.getScoreQuestionnaire() == 1) {
                    FormScoreRule scoreRule = scoreRuleMap.get(form.getId());
                    List<FormFieldsGroup> fieldsGroups = formFieldGroupMap.get(form.getId());
                    for (FormField formField : formFields) {
                        if (exportFieldCheck(formField.getWidgetType())) {
                            cell = row.createCell(lastCol++);
                            cell.setCellStyle(style);
                            // 如果是个评分单选。要多设置一个。
                            if (FormFieldExactType.SCORING_SINGLE_CHOICE.equals(formField.getExactType())) {
                                cell = row.createCell(lastCol++);
                                cell.setCellStyle(style);
                            }
                        }
                    }
                    if (Objects.nonNull(scoreRule)) {
                        if (Objects.nonNull(scoreRule.getShowResultSum()) && scoreRule.getShowResultSum() == 1) {
                            cell = row.createCell(lastCol++);
                            cell.setCellStyle(style);
                        }
                        if (Objects.nonNull(scoreRule.getShowAverage()) && scoreRule.getShowAverage() == 1) {
                            cell = row.createCell(lastCol++);
                            cell.setCellStyle(style);
                        }
                        if (Objects.nonNull(scoreRule.getShowGroupSum()) && scoreRule.getShowGroupSum() == 1) {
                            for (FormFieldsGroup group : fieldsGroups) {
                                cell = row.createCell(lastCol++);
                                cell.setCellStyle(style);
                            }
                        }
                    }
                } else {
                    for (FormField formField : formFields) {
                        if (exportFieldCheck(formField.getWidgetType())) {
                            cell = row.createCell(lastCol++);
                            cell.setCellStyle(style);
                        }
                    }
                }
            }
            if (lastCol - 1 >= firstCol) {
                region0 = new CellRangeAddress(2, 2, firstCol, lastCol - 1);
                sheet.addMergedRegion(region0);
            }
        }

    }


    /**
     * 设置表单的 字段。
     * 返回 最大 last row
     * @return
     */
    public void setFormField(XSSFSheet sheet, int startRowIdx, CellStyle style, int startCellIdx, int formResultIdx,
                             FormResult formResult,
                             FormScoreRule formScoreRule,
                             List<FormFieldsGroup> formFieldsGroups,
                             FormResultScore resultScore) {
        XSSFRow row = sheet.getRow(startRowIdx);
        if (Objects.isNull(row)) {
            row = createRow(sheet, startRowIdx);
        }
        XSSFCell cell = row.createCell(startCellIdx++);
        cell.setCellValue(formResultIdx);
        cell.setCellStyle(style);
        String jsonContent = formResult.getJsonContent();
        List<FormFieldDto> fieldDtos = JSON.parseArray(jsonContent, FormFieldDto.class);
        if (Objects.nonNull(formResult.getScoreQuestionnaire()) && formResult.getScoreQuestionnaire() == 1) {
            for (FormFieldDto fieldDto : fieldDtos) {
                if (exportFieldCheck(fieldDto.getWidgetType())) {
                    cell = row.createCell(startCellIdx++);
                    cell.setCellStyle(style);
                    cell.setCellValue(getFieldValue(fieldDto));
                    if (FormFieldExactType.SCORING_SINGLE_CHOICE.equals(fieldDto.getExactType())) {
                        cell = row.createCell(startCellIdx++);
                        cell.setCellStyle(style);
                        Float valueScore = getFieldValueScore(fieldDto);
                        if (Objects.nonNull(valueScore)) {
                            cell.setCellValue(valueScore);
                        }
                    }
                }
            }
            if (Objects.nonNull(formScoreRule)) {
                if (Objects.nonNull(formScoreRule.getShowResultSum()) && formScoreRule.getShowResultSum() == 1) {
                    cell = row.createCell(startCellIdx++);
                    String formResultCountWay = formScoreRule.getFormResultCountWay();
                    if (FormResultCountScoreEnum.sum_score.type.equals(formResultCountWay)) {
                        cell.setCellValue(resultScore.getFormResultSumScore());
                    } else if (FormResultCountScoreEnum.average_score.type.equals(formResultCountWay)) {
                        if (Objects.nonNull(resultScore.getFormResultAverageScore())) {
                            BigDecimal decimal = BigDecimal.valueOf(resultScore.getFormResultAverageScore());
                            BigDecimal bigDecimal = decimal.setScale(1, RoundingMode.HALF_UP);
                            cell.setCellValue(bigDecimal.toString());
                        }
                    } else if (FormResultCountScoreEnum.sum_average_score.type.equals(formResultCountWay)) {
                        if (Objects.nonNull(resultScore.getFormResultSumAverageScore())) {
                            BigDecimal decimal = BigDecimal.valueOf(resultScore.getFormResultSumAverageScore());
                            BigDecimal bigDecimal = decimal.setScale(1, RoundingMode.HALF_UP);
                            cell.setCellValue(bigDecimal.toString());
                        }
                    }
                    cell.setCellStyle(style);
                }
                if (Objects.nonNull(formScoreRule.getShowAverage()) && formScoreRule.getShowAverage() == 1) {
                    cell = row.createCell(startCellIdx++);
                    if (Objects.nonNull(resultScore.getFormResultAverageScore())) {
                        BigDecimal decimal = BigDecimal.valueOf(resultScore.getFormResultAverageScore());
                        BigDecimal bigDecimal = decimal.setScale(1, RoundingMode.HALF_UP);
                        cell.setCellValue(bigDecimal.toString());
                    }
                    cell.setCellStyle(style);
                }
                if (Objects.nonNull(formScoreRule.getShowGroupSum()) && formScoreRule.getShowGroupSum() == 1) {
                    String fieldGroupSumInfo = resultScore.getFormFieldGroupSumInfo();
                    Map<String, String> scoreGroupMap = new HashMap<>();
                    if (StrUtil.isNotEmpty(fieldGroupSumInfo)) {
                        List<JSONObject> jsonObjects = JSONArray.parseArray(fieldGroupSumInfo, JSONObject.class);
                        for (JSONObject jsonObject : jsonObjects) {
                            String uuid = jsonObject.getString("uuid");
                            Object score = jsonObject.get("score");
                            scoreGroupMap.put(uuid, score.toString());
                        }
                    }
                    for (FormFieldsGroup group : formFieldsGroups) {
                        cell = row.createCell(startCellIdx++);
                        String s = scoreGroupMap.get(group.getFieldGroupUUId());
                        cell.setCellValue(s);
                        cell.setCellStyle(style);
                    }
                }
            }
        } else {
            for (FormFieldDto fieldDto : fieldDtos) {
                if (exportFieldCheck(fieldDto.getWidgetType())) {
                    cell = row.createCell(startCellIdx++);
                    cell.setCellStyle(style);
                    cell.setCellValue(getFieldValue(fieldDto));
                }
            }
        }
    }


    /**
     * 写入患者信息
     * @param row
     * @param style
     * @param patientIdx
     * @param patient
     */
    public void setPatientValue(XSSFRow row, CellStyle style, int patientIdx, Patient patient, FormResult baseInfoFormResult) {
        int cellIdx = 0;
        XSSFCell cell;
        LocalDateTime createTime = patient.getCreateTime();
        cell = row.createCell(cellIdx++);
        cell.setCellValue(patientIdx);
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue(patient.getId().toString());
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue(createTime.toLocalDate().toString() + " " + createTime.toLocalTime().toString());
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue(patient.getName());
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue(patient.getDoctorName());
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue(patient.getServiceAdvisorName());
        cell.setCellStyle(style);

        if (Objects.nonNull(baseInfoFormResult)) {
            String jsonContent = baseInfoFormResult.getJsonContent();
            List<FormFieldDto> formFieldDtos = JSONArray.parseArray(jsonContent, FormFieldDto.class);
            for (FormFieldDto fieldDto : formFieldDtos) {
                if (exportFieldCheck(fieldDto.getWidgetType())) {
                    cell = row.createCell(cellIdx++);
                    cell.setCellStyle(style);
                    cell.setCellValue(getFieldValue(fieldDto));
                }
            }
        }

    }

    public void mergeCell(XSSFSheet sheet, int patientStartRowNumber, int patientUseRowNumber) {

        int lastRow = patientStartRowNumber + patientUseRowNumber - 1;
        if (patientStartRowNumber == lastRow) {
            return;
        }
        CellRangeAddress region0 = new CellRangeAddress(patientStartRowNumber, lastRow, 0, 0);
        sheet.addMergedRegion(region0);

        CellRangeAddress region1 = new CellRangeAddress(patientStartRowNumber, lastRow, 1, 1);
        sheet.addMergedRegion(region1);

        CellRangeAddress region2 = new CellRangeAddress(patientStartRowNumber, lastRow, 2, 2);
        sheet.addMergedRegion(region2);

        CellRangeAddress region3 = new CellRangeAddress(patientStartRowNumber, lastRow, 3, 3);
        sheet.addMergedRegion(region3);

        CellRangeAddress region4 = new CellRangeAddress(patientStartRowNumber, lastRow, 4, 4);
        sheet.addMergedRegion(region4);

    }

    public void setCellStyle(XSSFSheet sheet, int patientStartRowNumber, int patientUseRowNumber, CellStyle style, Integer lastCell) {

        int lastRow = patientStartRowNumber + patientUseRowNumber;
        for (int i = patientStartRowNumber; i < lastRow; i++) {
            XSSFRow row = sheet.getRow(i);
            for (int j = 0; j < lastCell; j++) {
                XSSFCell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                cell.setCellStyle(style);
            }
        }


    }
}
