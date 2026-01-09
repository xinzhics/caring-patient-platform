package com.caring.sass.nursing.service.exoprt;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.form.FormOptions;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExportXls {

    /**
     * 蓝色背景的标题
     */
    private CellStyle titleCellStyle1;

    /**
     * 绿色背景的标题
     */
    private CellStyle titleCellStyle2;

    /**
     * 没有背景色
     */
    private CellStyle cellValueStyle1;

    /**
     * 有背景色
     */
    private CellStyle cellValueStyle2;

    protected void initStyle(XSSFWorkbook workbook ){
        XSSFFont font2 = workbook.createFont();
        font2.setBold(true);
        font2.setFontHeightInPoints((short)12);
        font2.setFontName("DengXian");

        titleCellStyle1 = workbook.createCellStyle();
        titleCellStyle1.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex()); // 设置颜色
        titleCellStyle1.setVerticalAlignment(VerticalAlignment.CENTER);    // 设置居中
        titleCellStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    // 设置颜色填充方式
        titleCellStyle1.setAlignment(HorizontalAlignment.CENTER);    // 设置居中

        titleCellStyle1.setBorderTop(BorderStyle.THIN);  // 设置细线
        titleCellStyle1.setBorderBottom(BorderStyle.THIN);
        titleCellStyle1.setBorderLeft(BorderStyle.THIN);
        titleCellStyle1.setBorderRight(BorderStyle.THIN);
        titleCellStyle1.setFont(font2);


        titleCellStyle2 = workbook.createCellStyle();
        titleCellStyle2.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex()); // 设置颜色
        titleCellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);    // 设置颜色填充方式
        titleCellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);    // 设置居中
        titleCellStyle2.setAlignment(HorizontalAlignment.CENTER);    // 设置居中

        titleCellStyle2.setBorderTop(BorderStyle.THIN);  // 设置细线
        titleCellStyle2.setBorderBottom(BorderStyle.THIN);
        titleCellStyle2.setBorderLeft(BorderStyle.THIN);
        titleCellStyle2.setBorderRight(BorderStyle.THIN);
        titleCellStyle2.setFont(font2);


        cellValueStyle1 = workbook.createCellStyle();
        cellValueStyle1.setAlignment(HorizontalAlignment.CENTER);    // 设置居中
        cellValueStyle1.setVerticalAlignment(VerticalAlignment.CENTER);    // 设置居中
        cellValueStyle1.setWrapText(true);
        cellValueStyle1.setBorderTop(BorderStyle.THIN);  // 设置细线
        cellValueStyle1.setBorderBottom(BorderStyle.THIN);
        cellValueStyle1.setBorderLeft(BorderStyle.THIN);
        cellValueStyle1.setBorderRight(BorderStyle.THIN);


        cellValueStyle2 = workbook.createCellStyle();
        cellValueStyle2.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex()); // 设置颜色
        cellValueStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);    // 设置颜色填充方式
        cellValueStyle2.setAlignment(HorizontalAlignment.CENTER);    // 设置居中
        cellValueStyle2.setVerticalAlignment(VerticalAlignment.CENTER);    // 设置居中
        cellValueStyle2.setWrapText(true);
        cellValueStyle2.setBorderTop(BorderStyle.THIN);  // 设置细线
        cellValueStyle2.setBorderBottom(BorderStyle.THIN);
        cellValueStyle2.setBorderLeft(BorderStyle.THIN);
        cellValueStyle2.setBorderRight(BorderStyle.THIN);
    }

    /**
     *
     * 创建一个表格
     * @return
     */
    public XSSFWorkbook getHSSWordBook(String sheetName) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.createSheet(sheetName);
        initStyle(workbook);
        return workbook;
    }


    public boolean exportFieldCheck(String widgetType) {

        if (FormWidgetType.FULL_NAME.equals(widgetType) ||
                FormWidgetType.DESC.equals(widgetType) ||
                FormWidgetType.PAGE.equals(widgetType) ||
                FormWidgetType.SPLIT_LINE.equals(widgetType)) {
            return false;
        }
        return true;

    }

    public void setSheetCellWidth(XSSFSheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        XSSFRow row = sheet.getRow(lastRowNum);
        short lastCellNum = row.getLastCellNum();
        for (int i = 0; i < lastCellNum; i++) {
            sheet.setColumnWidth(i, 3300);
        }
    }


    /**
     * 创建一个行， 并设置高度
     * @param sheet
     * @param startRowIdx
     * @return
     */
    public XSSFRow createRow(XSSFSheet sheet, int startRowIdx) {
        XSSFRow row = sheet.createRow(startRowIdx);
        row.setHeight((short) 600);
        return row;
    }

    /**
     * 解析 获取 表单字段的结果。
     * @param formField
     * @return
     */
    public String getFieldValue(FormFieldDto formField) {

        return ExportFormFieldResultUtil.getFieldValue(formField);
    }

    /**
     * 解析 获取 表单字段的结果的成绩。
     * @param formField
     * @return
     */
    public Float getFieldValueScore(FormFieldDto formField) {
        List<String> resultValues = FormFieldDto.parseResultValues(formField.getValues());
        if (CollUtil.isEmpty(resultValues)) {
            return null;
        }
        List<FormOptions> options = formField.getOptions();
        if (CollUtil.isEmpty(options)) {
            return null;
        }
        Map<String, Float> floatMap = options.stream()
                .filter(item -> Objects.nonNull(item.getScore()))
                .collect(Collectors.toMap(FormOptions::getId, FormOptions::getScore));
        Float score = null;
        for (String value : resultValues) {
            score = floatMap.get(value);
            if (Objects.nonNull(score)) {
                break;
            }
        }
        return score;

    }

    /**
     * 获取标题的背景颜色 样式
     * @param formIdx
     * @return
     */
    public CellStyle getTitleStyle(int formIdx) {
        if (formIdx == 0) {
            return titleCellStyle1;
        } else {
            return titleCellStyle2;
        }
    }

    /**
     * 获取字段的背景颜色
     * @param patientIdx
     * @return
     */
    public CellStyle getCellStyle(int patientIdx) {

        if (patientIdx == 0) {
            return cellValueStyle1;
        } else {
            return cellValueStyle2;
        }
    }


    /**
     * 设置第一行的 大标题
     * @param workbook
     */
    public void setFirstOneRowTitle(XSSFWorkbook workbook, String title) {

        setRowTitle(workbook, title, 0, 30, true);
    }

    /**
     * 设置第二行的大标题
     * @param workbook
     */
    public void setSecondOneRowTitle(XSSFWorkbook workbook, String title) {

        setRowTitle(workbook, title, 1, 30, true);
    }

    public void setRowTitle(XSSFWorkbook workbook, String title, int rowNum, int lastCol, boolean fontBold) {

        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.createRow(rowNum);
        XSSFCellStyle style = workbook.createCellStyle();
        row.setHeight((short)500);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont font = workbook.createFont();
        font.setFontName("DengXian");
        font.setFontHeightInPoints((short)16);
        CellRangeAddress region0 = new CellRangeAddress(rowNum, rowNum, 0, lastCol);
        sheet.addMergedRegion(region0);
        font.setBold(fontBold);
        style.setFont(font);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(style);

    }

}
