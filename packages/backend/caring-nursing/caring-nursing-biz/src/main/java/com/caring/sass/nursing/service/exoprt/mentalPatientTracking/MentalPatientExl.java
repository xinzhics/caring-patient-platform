package com.caring.sass.nursing.service.exoprt.mentalPatientTracking;

import cn.hutool.core.collection.CollUtil;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;

public class MentalPatientExl {

    XSSFWorkbook workbook;

    XSSFSheet sheet;


    List<String> titles;

    private CellStyle titleStyle;

    /**
     * 没有背景色
     */
    private CellStyle cellValueStyle1;

    /**
     * 有背景色
     */
    private CellStyle cellValueStyle2;


    public CellStyle getCellValueStyle1() {
        return cellValueStyle1;
    }

    public CellStyle getCellValueStyle2() {
        return cellValueStyle2;
    }

    public MentalPatientExl() {

        titles = new ArrayList<>();
        titles.add("序列");
        titles.add("省份");
        titles.add("城市");
        titles.add("医院");
        titles.add("医院类型（专科/综合）");
        titles.add("处方医生");
        titles.add("患者姓名");
        titles.add("性别");
        titles.add("年龄");
        titles.add("临床诊断");
        titles.add("患病时长");
        titles.add("既往用药");
        titles.add("既往用药剂量");
        titles.add("疗效评价");
        titles.add("本次处方迈达原因");
        titles.add("处方时间（日/月）");
        titles.add("注射时口服阿立哌唑剂量（片剂或口服液）");
        titles.add("开始口服阿立哌唑时间");
        titles.add("口服阿立哌唑品牌");
        titles.add("注射时间");
        titles.add("联合用药:是/否");
        titles.add("联合用药的名称");
        titles.add("联合用药的剂量");
        titles.add("联合用药原因");
        titles.add("AOM处方：线上/线下");
        titles.add("针头配置：医院/药店/线上");
        titles.add("注射地点：医院/社区/上门服务");
        titles.add("使用反馈：注射是否顺滑/操作是否便利/注射部位是否有疼痛感或结节？");
        titles.add("用药后跟踪1个月");
        titles.add("用药后跟踪2个月");
        titles.add("用药后跟踪3个月");
        titles.add("用药后跟踪4个月");
        titles.add("用药后跟踪5个月");
        titles.add("用药后跟踪6个月");
        titles.add("停药时间");
        titles.add("停药原因");
        titles.add("备注");

        getXSSFWorkbook();
    }

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    /**
     * 精神病患者 exl 工具
     * 获取一个 表格。
     *
     * @return
     */
    private XSSFWorkbook getXSSFWorkbook() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet();
        titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);    // 设置居中
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);    // 设置居中
        titleStyle.setWrapText(true);
        titleStyle.setBorderTop(BorderStyle.THIN);  // 设置细线
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderLeft(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);

        cellValueStyle1 = workbook.createCellStyle();
        cellValueStyle1.setAlignment(HorizontalAlignment.CENTER);    // 设置居中
        cellValueStyle1.setVerticalAlignment(VerticalAlignment.CENTER);    // 设置居中
        cellValueStyle1.setWrapText(true);

        cellValueStyle2 = workbook.createCellStyle();
        cellValueStyle2.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex()); // 设置颜色
        cellValueStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);    // 设置颜色填充方式
        cellValueStyle2.setAlignment(HorizontalAlignment.CENTER);    // 设置居中
        cellValueStyle2.setVerticalAlignment(VerticalAlignment.CENTER);    // 设置居中
        cellValueStyle2.setWrapText(true);
        createTitle(sheet);
        return workbook;
    }


    /**
     * 创建标题
     * @param sheet
     * @return
     */
    private boolean createTitle(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) 500);
        // 一般信息 0 -5
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("一般信息");
        cell.setCellStyle(titleStyle);
        for (int i = 1; i <= 5; i++) {
            row.createCell(i);
            cell.setCellStyle(titleStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        cell = row.createCell(6);
        cell.setCellValue("患者信息");
        cell.setCellStyle(titleStyle);
        for (int i = 7; i <= 18; i++) {
            row.createCell(i);
            cell.setCellStyle(titleStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 6, 18));

        cell = row.createCell(19);
        cell.setCellValue("注射记录");
        cell.setCellStyle(titleStyle);
        for (int i = 20; i <= 27; i++) {
            row.createCell(i);
            cell.setCellStyle(titleStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 19, 27));

        cell = row.createCell(28);
        cell.setCellValue("长期用药跟踪信息");
        cell.setCellStyle(titleStyle);
        for (int i = 29; i <= 36; i++) {
            row.createCell(i);
            cell.setCellStyle(titleStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 28, 36));

        XSSFRow row1 = sheet.createRow(1);
        int i = 0;
        XSSFCell cell1;
        for (String title : titles) {
            cell1 = row1.createCell(i++);
            cell1.setCellValue(title);
            cell1.setCellStyle(titleStyle);
        }
        return true;
    }


    /**
     * 写入患者数据
     * @param rowIndex 开始的行
     * @param patientBaseEntity 患者基本信息
     * @param diseaseExcelEntities 疾病信息
     * @param injectionExcelEntities 注射信息
     * @param mentalMedicationTrackings 用户跟踪信息
     * @return
     */
    public int setValue(Integer rowIndex, Integer serialNumber, CellStyle cellStyle,
                        MentalPatientBaseEntity patientBaseEntity,
                        List<MentalDiseaseExcelEntity> diseaseExcelEntities,
                        List<MentalInjectionExcelEntity> injectionExcelEntities,
                        List<MentalMedicationTracking> mentalMedicationTrackings) {
        if (injectionExcelEntities == null) {
            injectionExcelEntities = new ArrayList<>();
        }
        if (mentalMedicationTrackings == null) {
            mentalMedicationTrackings = new ArrayList<>();
        }
        if (diseaseExcelEntities == null) {
            diseaseExcelEntities = new ArrayList<>();
        }

        int thisPatientRowStartIndex = rowIndex;    // 计算患者的信息站了多少行。后面合并基本信息
        int thisPatientRowEndIndex = rowIndex;
        int patientInfoIndex = 0;
        XSSFCell cell;
        XSSFRow row;
        row = sheet.createRow(rowIndex++);
        cell = row.createCell(patientInfoIndex++);
        cell.setCellValue(serialNumber);    // 序号
        cell.setCellStyle(cellStyle);

        cell = row.createCell(patientInfoIndex++);
        cell.setCellValue(patientBaseEntity.getProvince());    // 省份
        cell.setCellStyle(cellStyle);


        cell = row.createCell(patientInfoIndex++);
        cell.setCellValue(patientBaseEntity.getCity());    // 城市
        cell.setCellStyle(cellStyle);

        cell = row.createCell(patientInfoIndex++);
        cell.setCellValue(patientBaseEntity.getHospital());    // 医院
        cell.setCellStyle(cellStyle);

        cell = row.createCell(patientInfoIndex++);
        cell.setCellValue(patientBaseEntity.getHospitalType());    // 医院类型(专科/综合)
        cell.setCellStyle(cellStyle);

        cell = row.createCell(patientInfoIndex++);
        cell.setCellValue(patientBaseEntity.getDoctor());    // 处方医生
        cell.setCellStyle(cellStyle);

        cell = row.createCell(patientInfoIndex++);
        cell.setCellValue(patientBaseEntity.getPatientName());    // 患者姓名
        cell.setCellStyle(cellStyle);

        cell = row.createCell(patientInfoIndex++);
        cell.setCellValue(patientBaseEntity.getPatientSex());    // 性别
        cell.setCellStyle(cellStyle);

        cell = row.createCell(patientInfoIndex);
        cell.setCellValue(patientBaseEntity.getPatientAge());    // 年龄
        cell.setCellStyle(cellStyle);
        boolean formFirstRow = true;
        while (CollUtil.isNotEmpty(diseaseExcelEntities) || CollUtil.isNotEmpty(injectionExcelEntities) || CollUtil.isNotEmpty(mentalMedicationTrackings)) {
            if (!formFirstRow) {
                row = sheet.createRow(rowIndex++);
                thisPatientRowEndIndex++;
            }
            formFirstRow = false;
            if (!diseaseExcelEntities.isEmpty()) {
                MentalDiseaseExcelEntity excelEntity = diseaseExcelEntities.get(0);
                // 给行中写入疾病信息
                setMentalDiseaseExcel(excelEntity, row, cellStyle);
                diseaseExcelEntities.remove(0);
            }
            if (!injectionExcelEntities.isEmpty()) {
                MentalInjectionExcelEntity injectionExcelEntity = injectionExcelEntities.get(0);
                setMentalInjectionExcel(injectionExcelEntity, row, cellStyle);
                injectionExcelEntities.remove(0);
            }
            if (!mentalMedicationTrackings.isEmpty()) {
                MentalMedicationTracking medicationTracking = mentalMedicationTrackings.get(0);
                setMedicationTracking(medicationTracking, row, cellStyle);
                mentalMedicationTrackings.remove(0);
            }
        }

        mergeCell(thisPatientRowStartIndex, thisPatientRowEndIndex);
        return rowIndex;

    }


    /**
     * 合并基本信息列
     * @param patientStartRowNumber 开始的行数
     * @param thisPatientRowEndIndex 结束的行数
     */
    public void mergeCell(int patientStartRowNumber, int thisPatientRowEndIndex) {

        if (patientStartRowNumber == thisPatientRowEndIndex) {
            return;
        }
        for (int i = 0; i <= 8; i++) {
            sheet.addMergedRegion(new CellRangeAddress(patientStartRowNumber, thisPatientRowEndIndex , i, i));
        }

    }

    /**
     * 写入用药跟踪
     * @param excelEntity
     * @param row
     */
    private void setMedicationTracking(MentalMedicationTracking excelEntity, XSSFRow row, CellStyle cellStyle) {
        int index = 28;
        XSSFCell cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getYONG_YAO_1_YUE_VALUE());    //用药后跟踪1个月
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getYONG_YAO_2_YUE_VALUE());    //用药后跟踪2个月
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getYONG_YAO_3_YUE_VALUE());    //用药后跟踪2个月
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getYONG_YAO_4_YUE_VALUE());    //用药后跟踪3个月
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getYONG_YAO_5_YUE_VALUE());    //用药后跟踪4个月
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getYONG_YAO_6_YUE_VALUE());    //用药后跟踪5个月
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getTING_YAO_SHI_JIAN_VALUE());    //停药时间
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getTING_YAO_YUAN_YIN_VALUE());    //停药原因
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index);
        cell.setCellValue(excelEntity.getBEI_ZHU_VALUE());    //备注
        cell.setCellStyle(cellStyle);

    }

    /**
     * 给行中写入注射信息
     * @param excelEntity
     * @param row
     */
    private void setMentalInjectionExcel(MentalInjectionExcelEntity excelEntity, XSSFRow row, CellStyle cellStyle) {
        int index = 19;
        XSSFCell cell;

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getZHU_SHE_RI_QI_VALUE());    // 注射日期
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getLIAN_HE_YONG_YAO_VALUE());    // 联合用药:是/否
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getLIAN_HE_YONG_YAO_MING_CHEN_VALUE());    // 联合用药的名称
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getLIAN_HE_YONG_YAO_JI_LIANG_VALUE());    // 联合用药的剂量
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getLIAN_HE_YONG_YAO_YUAN_YIN_VALUE());    // 联合用药原因
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getAOM_CHU_FANG_VALUE());    // AOM处方：线上/线下
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getZHEN_TOU_PEI_ZHI_VALUE());    // 针头配置：医院/药店/线上
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index++);
        cell.setCellValue(excelEntity.getZHU_SHE_DI_DIAN_VALUE());    // 注射地点：医院/社区/上门服务
        cell.setCellStyle(cellStyle);

        cell = row.createCell(index);
        cell.setCellValue(excelEntity.getSHI_YONG_FAN_KUI_VALUE());    // 使用反馈：注射是否顺滑/操作是否便利/注射部位是否有疼痛感或结节？
        cell.setCellStyle(cellStyle);

    }


    /**
     * 给行中写入 疾病信息
     * @param excelEntity
     * @param row
     */
    private void setMentalDiseaseExcel(MentalDiseaseExcelEntity excelEntity, XSSFRow row, CellStyle cellStyle) {
        int diseaseIndex = 9;
        XSSFCell cell = row.createCell(diseaseIndex++);
        cell.setCellValue(excelEntity.getLin_chang_zhen_duan_value());    // 临床诊断
        cell.setCellStyle(cellStyle);

        cell = row.createCell(diseaseIndex++);
        cell.setCellValue(excelEntity.getHuan_bing_shi_chang_value());    // 患病时长
        cell.setCellStyle(cellStyle);

        cell = row.createCell(diseaseIndex++);
        cell.setCellValue(excelEntity.getJi_wang_yong_yao_value());    // 既往用药
        cell.setCellStyle(cellStyle);

        cell = row.createCell(diseaseIndex++);
        cell.setCellValue(excelEntity.getJi_wang_yong_yao_ji_liang_value());    // 既往用药剂量
        cell.setCellStyle(cellStyle);

        cell = row.createCell(diseaseIndex++);
        cell.setCellValue(excelEntity.getLiao_xiao_ping_jia_value());    // 疗效评价
        cell.setCellStyle(cellStyle);

        cell = row.createCell(diseaseIndex++);
        cell.setCellValue(excelEntity.getBen_ci_chu_fang_mai_da_yuan_yin_value());    // 本次处方迈达原因
        cell.setCellStyle(cellStyle);

        cell = row.createCell(diseaseIndex++);
        cell.setCellValue(excelEntity.getChu_fang_shi_jian_value());    // 处方时间（日/月）
        cell.setCellStyle(cellStyle);

        cell = row.createCell(diseaseIndex++);
        cell.setCellValue(excelEntity.getZhu_she_shi_kou_fu_a_li_pai_zuo_liang_value());    // 注射时口服阿立哌唑剂量（片剂或口服液）
        cell.setCellStyle(cellStyle);

        cell = row.createCell(diseaseIndex++);
        cell.setCellValue(excelEntity.getStart_kou_fu_a_li_pai_zuo_shi_jian_value());    // 开始口服阿立哌唑时间
        cell.setCellStyle(cellStyle);

        cell = row.createCell(diseaseIndex);
        cell.setCellValue(excelEntity.getKou_fu_a_li_pai_zuo_pin_pai_value());    // 口服阿立哌唑品牌
        cell.setCellStyle(cellStyle);
    }


}
