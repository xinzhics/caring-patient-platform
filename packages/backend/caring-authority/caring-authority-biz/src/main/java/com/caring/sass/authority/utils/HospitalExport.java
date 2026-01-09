package com.caring.sass.authority.utils;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.authority.entity.common.Hospital;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @ClassName HospitalExport
 * @Description
 * @Author yangShuai
 * @Date 2022/4/19 11:49
 * @Version 1.0
 */
public class HospitalExport {

    /**
     * 省
     */
    protected static Map<Long, String> provinceMap;

    /**
     * 市
     */
    protected static Map<Long, String> cityMap;


    public static void setProvinceMap(Map<Long, String> provinceMap) {
        HospitalExport.provinceMap = provinceMap;
    }

    public static void setCityMap(Map<Long, String> cityMap) {
        HospitalExport.cityMap = cityMap;
    }

    public static void clean() {
        provinceMap = null;
        cityMap = null;
    }

    /**
     * 设置表格的标题
     * @param sheet
     * @param userName
     */
    public static void setTitle(XSSFSheet sheet, String userName) {
        CellRangeAddress region0 = new CellRangeAddress(0, 0, 0, 5);
        sheet.addMergedRegion(region0);
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        String value = "导出时间：" + LocalDateTime.now().toString() + "  导出账号：" + userName;
        cell.setCellValue(value);
    }


    /**
     * 设置表格的内容
     * @param sheet
     * @param hospitals
     * @return
     */
    public static int setValues(XSSFSheet sheet, List<Hospital> hospitals, int index) {
        int lastRowNum = sheet.getLastRowNum();
        lastRowNum++;
        XSSFRow row;
        XSSFCell cell;
        for (Hospital hospital : hospitals) {
            index++;
            row = sheet.createRow(lastRowNum++);
            cell = row.createCell(0);
            cell.setCellValue(index);

            cell = row.createCell(1);
            cell.setCellValue(hospital.getHospitalName());

            cell = row.createCell(2);
            if (CollUtil.isNotEmpty(provinceMap)) {
                cell.setCellValue(provinceMap.get(hospital.getProvinceId()));
            }

            cell = row.createCell(3);
            if (CollUtil.isNotEmpty(cityMap)) {
                cell.setCellValue(cityMap.get(hospital.getCityId()));
            }

            cell = row.createCell(4);
            cell.setCellValue(hospital.getAddress());

            cell = row.createCell(5);
            cell.setCellValue(hospital.getHospitalLevel());

        }
        return index;
    }

}
