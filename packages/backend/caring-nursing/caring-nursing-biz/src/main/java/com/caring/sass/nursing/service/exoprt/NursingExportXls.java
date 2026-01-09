package com.caring.sass.nursing.service.exoprt;

import com.caring.sass.user.entity.NursingStaff;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDateTime;

/**
 * 导出医助的信息
 */
public class NursingExportXls extends ExportXls {

    @Override
    public XSSFWorkbook getHSSWordBook(String sheetName) {
        return super.getHSSWordBook(sheetName);
    }


    @Override
    public void setSheetCellWidth(XSSFSheet sheet) {
        sheet.setColumnWidth(0, 6600);
        sheet.setColumnWidth(1, 6600);
        sheet.setColumnWidth(2, 6600);
        sheet.setColumnWidth(3, 6600);
        sheet.setColumnWidth(4, 6600);
        sheet.setColumnWidth(5, 4000);
    }

    /**
     * 设置医助导出的表格
     * @param row
     * @param style
     * @param nursingStaff
     */
    public void setNursingValue(XSSFRow row, CellStyle style, NursingStaff nursingStaff) {

        int cellIdx = 0;
        XSSFCell cell;
        LocalDateTime createTime = nursingStaff.getCreateTime();

        cell = row.createCell(cellIdx++);
        cell.setCellValue(createTime.toLocalDate().toString() + " " + createTime.toLocalTime().toString());
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue(nursingStaff.getName() == null ? "" : nursingStaff.getName());
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        cell.setCellValue(nursingStaff.getMobile() == null ? "" : nursingStaff.getMobile());
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        cell.setCellValue(nursingStaff.getOrganName() == null ? "" : nursingStaff.getOrganName());
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue(nursingStaff.getPatientCount() == null ? "" : nursingStaff.getPatientCount().toString());
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx);
        cell.setCellValue(nursingStaff.getDoctorCount() == null ? "" : nursingStaff.getDoctorCount().toString());
        cell.setCellStyle(style);

    }

    /**
     * 设置医生的表格的表头
     * @param workbook
     */
    public void setFieldName(XSSFWorkbook workbook, String title1, String title2) {

        setFirstOneRowTitle(workbook, title1);
        setSecondOneRowTitle(workbook, title2);

        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = createRow(sheet,2);
        CellStyle style = getTitleStyle(0);
        int cellIdx = 0;
        XSSFCell cell;
        cell = row.createCell(cellIdx++);
        cell.setCellValue("创建时间");
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue("姓名");
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue("电话");
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue("机构");
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        cell.setCellValue("患者数");
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx);
        cell.setCellValue("医生数");
        cell.setCellStyle(style);


    }

}
