package com.caring.sass.nursing.service.exoprt;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.Patient;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 导出医生的信息
 */
public class DoctorExportXls extends ExportXls {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public XSSFWorkbook getHSSWordBook(String sheetName) {
        return super.getHSSWordBook(sheetName);
    }


    @Override
    public void setSheetCellWidth(XSSFSheet sheet) {
        sheet.setColumnWidth(0, 6600);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 7000);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(5, 4000);
        sheet.setColumnWidth(6, 6000);
        sheet.setColumnWidth(7, 9000);
        sheet.setColumnWidth(8, 12000);
        sheet.setColumnWidth(9, 4000);
        sheet.setColumnWidth(10, 4000);
        sheet.setColumnWidth(11, 4000);
        sheet.setColumnWidth(12, 4000);
    }

    /**
     * 设置医生导出的表格
     * @param row
     * @param style
     * @param doctor
     */
    public void setDoctorValue(XSSFRow row, CellStyle style,  Doctor doctor) {

        int cellIdx = 0;
        XSSFCell cell;
        LocalDateTime createTime = doctor.getCreateTime();

        cell = row.createCell(cellIdx++);
        cell.setCellValue(createTime.toLocalDate().toString() + " " + createTime.toLocalTime().toString());
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue(doctor.getName() == null ? "" : doctor.getName());
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        cell.setCellValue(doctor.getMobile() == null ? "" : doctor.getMobile());
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        cell.setCellValue(doctor.getHospitalName() == null ? "" : doctor.getHospitalName());
        cell.setCellStyle(style);

        // 科室
        cell = row.createCell(cellIdx++);
        cell.setCellValue(doctor.getDeptartmentName() == null ? "" : doctor.getDeptartmentName());
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        cell.setCellValue(doctor.getTitle() == null ? "" : doctor.getTitle());
        cell.setCellStyle(style);

        String extraInfo = doctor.getExtraInfo();
        String doctorSpecialties = "";
        String doctorIntroduction = "";
        if (StrUtil.isNotEmpty(extraInfo)) {
            JSONObject object = JSON.parseObject(extraInfo);
            // 特长
            Object specialties = object.get("Specialties");
            if (specialties != null) {
                doctorSpecialties = specialties.toString();
            }

            // 详细介绍
            Object introduction = object.get("Introduction");
            if (introduction != null) {
                doctorIntroduction = introduction.toString();
            }
        }

        cell = row.createCell(cellIdx++);
        cell.setCellValue(doctorSpecialties);
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        cell.setCellValue(doctorIntroduction);
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        cell.setCellValue(doctor.getNursingName() == null ? "" : doctor.getNursingName());
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        if (doctor.getFirstLoginTime() == null) {
            cell.setCellValue("");
        } else {
            String format = doctor.getFirstLoginTime().format(formatter);
            cell.setCellValue(format);
        }
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        if (doctor.getLastLoginTime() == null) {
            cell.setCellValue("");
        } else {
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(doctor.getLastLoginTime()), ZoneId.systemDefault());
            String format = dateTime.format(formatter);
            cell.setCellValue(format);
        }
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx);
        cell.setCellValue(doctor.getTotalPatientCount() == null ? "" : doctor.getTotalPatientCount().toString());
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
        cell.setCellValue("联系电话");
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue("医院");
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        cell.setCellValue("科室名称");
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue("职称");
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        cell.setCellValue("专业特长");
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue("详细介绍");
        cell.setCellStyle(style);


        cell = row.createCell(cellIdx++);
        cell.setCellValue("医助");
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue("激活时间");
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx++);
        cell.setCellValue("最后登录时间");
        cell.setCellStyle(style);

        cell = row.createCell(cellIdx);
        cell.setCellValue("患者数");
        cell.setCellStyle(style);
    }

}
