package com.caring.sass.nursing.service.exoprt;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.common.utils.SensitiveInfoUtils;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.form.FormFieldExactType;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.user.entity.Patient;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 患者 基本信息 疾病信息 的导出
 *
 */
public class PatientFormExportXls extends ExportXls {


    @Override
    public XSSFWorkbook getHSSWordBook(String sheetName) {
        return super.getHSSWordBook(sheetName);
    }

    /**
     * 设置表格的数据
     * @param patient
     * @param baseInfo
     * @param healthFile
     */
    public void setValue(XSSFRow row,  Patient patient, FormResult baseInfo, FormResult healthFile, boolean desensitization) {
        int cellIdx = 0;
        XSSFCell cell;

        // 插入患者ID
        cell = row.createCell(cellIdx++);
        cell.setCellValue(patient.getId().toString());

        LocalDateTime createTime = patient.getCreateTime();
        cell = row.createCell(cellIdx++);
        cell.setCellValue(createTime.toLocalDate().toString() + " " + createTime.toLocalTime().toString());

        cell = row.createCell(cellIdx++);
        cell.setCellValue(patient.getName());

        cell = row.createCell(cellIdx++);
        cell.setCellValue(patient.getDoctorName());


        cell = row.createCell(cellIdx++);
        cell.setCellValue(patient.getServiceAdvisorName());

        // 写入基本信息的字段
        if (Objects.nonNull(baseInfo)) {
            String jsonContent = baseInfo.getJsonContent();
            List<FormFieldDto> formFieldDtos = JSON.parseArray(jsonContent, FormFieldDto.class);

            for (FormFieldDto formField : formFieldDtos) {

                String exactType = formField.getExactType();

                if (exportFieldCheck(formField.getWidgetType())) {
                    cell = row.createCell(cellIdx++);
                    // 当导出的字段中有姓名和手机号时。 进行加密
                    if (desensitization && (FormFieldExactType.MOBILE.equals(exactType) ||
                            FormFieldExactType.NAME.equals(exactType))) {
                        String values = formField.getValues();
                        JSONArray array = JSONArray.parseArray(values);
                        if (array.isEmpty()) {
                            cell.setCellValue("");
                            continue;
                        }
                        JSONObject jsonObject = array.getJSONObject(0);
                        String value = jsonObject.getString("attrValue");

                        if (FormFieldExactType.NAME.equals(exactType)) {
                            value = SensitiveInfoUtils.desensitizeName(value);
                        }
                        if (FormFieldExactType.MOBILE.equals(exactType)) {
                            value = SensitiveInfoUtils.desensitizePhone(value);
                        }
                        cell.setCellValue(value);
                    } else {
                        cell.setCellValue(getFieldValue(formField));
                    }
                }

            }
        }

        /**
         * 写入疾病信息的字段
         */
        if (Objects.nonNull(healthFile)) {
            String jsonContent = healthFile.getJsonContent();
            List<FormFieldDto> formFieldDtos = JSON.parseArray(jsonContent, FormFieldDto.class);

            for (FormFieldDto formField : formFieldDtos) {
                if (exportFieldCheck(formField.getWidgetType())) {
                    cell = row.createCell(cellIdx++);
                    cell.setCellValue(getFieldValue(formField));
                }
            }
        }


    }

    public Integer setFieldName(XSSFWorkbook workbook, Form baseInfo, Form healthFile) {
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = createRow(sheet,3);
        CellStyle style = getTitleStyle(0);
        int cellIdx = 0;
        XSSFCell cell;
        Integer lastCell = 0;

        cell = row.createCell(cellIdx++);
        cell.setCellValue("患者ID");
        cell.setCellStyle(style);

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

        if (Objects.nonNull(baseInfo)) {
            String fieldsJson = baseInfo.getFieldsJson();
            List<FormField> formFields = JSON.parseArray(fieldsJson, FormField.class);
            for (FormField formField : formFields) {
                if (exportFieldCheck(formField.getWidgetType())) {
                    cell = row.createCell(cellIdx++);
                    lastCell++;
                    cell.setCellValue(formField.getLabel());
                    cell.setCellStyle(style);
                }
            }
        }
        style = getTitleStyle(1);
        if (Objects.nonNull(healthFile)) {
            String fieldsJson = healthFile.getFieldsJson();
            List<FormField> formFields = JSON.parseArray(fieldsJson, FormField.class);
            for (FormField formField : formFields) {
                if (exportFieldCheck(formField.getWidgetType())) {
                    cell = row.createCell(cellIdx++);
                    lastCell++;
                    cell.setCellValue(formField.getLabel());
                    cell.setCellStyle(style);
                }
            }
        }
        return lastCell;
    }

    public void setCellStyle(XSSFRow row, CellStyle style, Integer lastCell) {

        for (int j = 0; j <= lastCell; j++) {
            XSSFCell cell = row.getCell(j);
            if (cell == null) {
                cell = row.createCell(j);
            }
            cell.setCellStyle(style);
        }

    }

    /**
     * 设置表头。设置第三行的表单项
     * @param workbook
     * @param title1
     * @param title2
     * @param baseInfo
     * @param healthFile
     */
    public void setTitle(XSSFWorkbook workbook, String title1, String title2, Form baseInfo, Form healthFile) {

        setFirstOneRowTitle(workbook, title1);
        setSecondOneRowTitle(workbook, title2);
        CellStyle style = getTitleStyle(0);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = createRow(sheet,2);
        int lastCol = 0;
        int firstCol = 0;
        XSSFCell cell = row.createCell(lastCol++);
        if (baseInfo != null) {
            cell.setCellValue(baseInfo.getName());
            cell.setCellStyle(style);
        } else {
            cell.setCellValue("基本信息");
            cell.setCellStyle(style);
        }
        cell = row.createCell(lastCol++);
        cell.setCellStyle(style);
        cell = row.createCell(lastCol++);
        cell.setCellStyle(style);
        cell = row.createCell(lastCol++);
        cell.setCellStyle(style);
        cell = row.createCell(lastCol++);
        cell.setCellStyle(style);
        if (baseInfo != null) {
            cell.setCellValue(baseInfo.getName());
            cell.setCellStyle(style);
            String fieldsJson = baseInfo.getFieldsJson();
            List<FormField> formFields = JSON.parseArray(fieldsJson, FormField.class);
            for (FormField formField : formFields) {
                if (exportFieldCheck(formField.getWidgetType())) {
                    cell = row.createCell(lastCol++);
                    cell.setCellStyle(style);
                }
            }
            if (firstCol < lastCol -1) {
                CellRangeAddress region0 = new CellRangeAddress(2, 2, firstCol, lastCol -1);
                sheet.addMergedRegion(region0);
            }
        } else {
            CellRangeAddress region0 = new CellRangeAddress(2, 2, firstCol, lastCol -1);
            sheet.addMergedRegion(region0);
        }
        firstCol = lastCol;
        style = getTitleStyle(1);
        if (Objects.nonNull(healthFile)) {
            cell = row.createCell(lastCol++);
            cell.setCellValue(healthFile.getName());
            cell.setCellStyle(style);
            String fieldsJson = healthFile.getFieldsJson();
            List<FormField> formFields = JSON.parseArray(fieldsJson, FormField.class);
            if (CollUtil.isNotEmpty(formFields) && formFields.size() > 1) {
                int i = 0;
                for (FormField formField : formFields) {
                    i++;
                    if (exportFieldCheck(formField.getWidgetType())) {
                        if (i < formFields.size()) {
                            cell = row.createCell(lastCol++);
                            cell.setCellStyle(style);
                        }
                    }
                }
                CellRangeAddress region0 = new CellRangeAddress(2, 2, firstCol, lastCol - 1);
                sheet.addMergedRegion(region0);
            }
        }

    }

}
