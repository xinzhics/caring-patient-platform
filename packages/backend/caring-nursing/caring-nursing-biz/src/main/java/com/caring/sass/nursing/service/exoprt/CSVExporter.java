package com.caring.sass.nursing.service.exoprt;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVExporter {
    public static void exportToCSV(List<String[]> data, String fileName) throws IOException {
        try (OutputStream outputStream = new java.io.FileOutputStream(fileName)) {
            for (String[] row : data) {  
                outputStream.write(String.join(",", row).getBytes(StandardCharsets.UTF_8));
                outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));  
            }
        }
    }

    public static void exportShaiXuan() throws IOException {
        List<String[]> data = new ArrayList<>();
        String shaiXuanHao = "S01-001";
        String doctorName = "杨青";
        String fangShi = "荨麻疹活动评分—筛选";
        String nowDate = LocalDate.now().toString();
        String now1Date = LocalDate.now().plusDays(1).toString();
        String now2Date = LocalDate.now().plusDays(2).toString();
        String liangBiao = "荨麻疹活动性评分（UAS）自我记录及评价";
        data.add(new String[]{"序号", "筛选号(可能未填)", "医生名称", "随访期", "评估时间", "量表", "项目", "结果"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao, "风团数量/24h", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao, "瘙痒程度", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao, "总分", "2"});

        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao, "风团数量/24h", "1"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao, "瘙痒程度", "1"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao, "总分", "2"});

        data.add(new String[]{"3", shaiXuanHao, doctorName, fangShi, now2Date, liangBiao, "风团数量/24h", "1"});
        data.add(new String[]{"3", shaiXuanHao, doctorName, fangShi, now2Date, liangBiao, "瘙痒程度", "1"});
        data.add(new String[]{"3", shaiXuanHao, doctorName, fangShi, now2Date, liangBiao, "总分", "2"});

        CSVExporter.exportToCSV(data, "./荨麻疹活动评分—筛选.csv");
    }

    public static void exportZhiLiao() throws IOException {
        List<String[]> data = new ArrayList<>();
        String shaiXuanHao = "S01-001";
        String doctorName = "杨青";
        String fangShi = "荨麻疹活动评分—治疗";
        String nowDate = LocalDate.now().toString();
        String now1Date = LocalDate.now().plusDays(1).toString();
        String now2Date = LocalDate.now().plusDays(2).toString();
        String liangBiao = "荨麻疹活动性评分（UAS）自我记录及评价";
        data.add(new String[]{"序号", "筛选号(可能未填)", "医生名称", "随访期", "评估时间", "量表", "项目", "结果"});

        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao, "风团数量/24h", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao, "瘙痒程度", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao, "总分", "2"});

        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao, "风团数量/24h", "1"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao, "瘙痒程度", "1"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao, "总分", "2"});

        data.add(new String[]{"3", shaiXuanHao, doctorName, fangShi, now2Date, liangBiao, "风团数量/24h", "1"});
        data.add(new String[]{"3", shaiXuanHao, doctorName, fangShi, now2Date, liangBiao, "瘙痒程度", "1"});
        data.add(new String[]{"3", shaiXuanHao, doctorName, fangShi, now2Date, liangBiao, "总分", "2"});

        CSVExporter.exportToCSV(data, "./荨麻疹活动评分—治疗.csv");
    }

    public static void exportSuiFang() throws IOException {
        List<String[]> data = new ArrayList<>();
        String shaiXuanHao = "S01-001";
        String doctorName = "杨青";
        String fangShi = "荨麻疹活动评分—随访";
        String nowDate = LocalDate.now().toString();
        String now1Date = LocalDate.now().plusDays(1).toString();
        String liangBiao = "荨麻疹活动性评分（UAS）自我记录及评价";
        String liangBiao1 = "慢性荨麻疹生活质量问卷（CU-Q2oL ）";
        data.add(new String[]{"序号", "筛选号(可能未填)", "医生名称", "访视", "评估时间", "量表", "项目", "结果"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao, "风团数量/24h", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao, "瘙痒程度", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao, "总分", "2"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "1.瘙痒", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "2.风团", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "3.眼睛肿胀", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "4.口唇肿胀", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "5.工作", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "6.运动", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "7.睡眠", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "8.闲暇", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "9.社交", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "10.饮食", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "11.是否入睡困难", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "12.是否会夜间醒来", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "13.是否晚上睡眠不佳而白天困乏", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "14.是否注意力难以集中", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "15.是否感觉紧张", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "16.是否情绪低落", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "17.是否需要限制饮食", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "18.是否身体出现的荨麻疹症状而感到困扰？", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "19.在公共场所是否感到尴尬", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "20.是否可以使用化妆品", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "21.是否限制了您对服装类型的选择", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "22.是否因荨麻疹限制了体育运动", "1"});
        data.add(new String[]{"1", shaiXuanHao, doctorName, fangShi, nowDate, liangBiao1, "23.是否受荨麻疹治疗的不良反应影响", "1"});

        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao, "风团数量/24h", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao, "瘙痒程度", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao, "总分", "4"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "1.瘙痒", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "2.风团", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "3.眼睛肿胀", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "4.口唇肿胀", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "5.工作", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "6.运动", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "7.睡眠", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "8.闲暇", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "9.社交", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "10.饮食", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "11.是否入睡困难", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "12.是否会夜间醒来", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "13.是否晚上睡眠不佳而白天困乏", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "14.是否注意力难以集中", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "15.是否感觉紧张", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "16.是否情绪低落", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "17.是否需要限制饮食", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "18.是否身体出现的荨麻疹症状而感到困扰？", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "19.在公共场所是否感到尴尬", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "20.是否可以使用化妆品", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "21.是否限制了您对服装类型的选择", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "22.是否因荨麻疹限制了体育运动", "2"});
        data.add(new String[]{"2", shaiXuanHao, doctorName, fangShi, now1Date, liangBiao1, "23.是否受荨麻疹治疗的不良反应影响", "2"});

        CSVExporter.exportToCSV(data, "./荨麻疹活动评分—随访.csv");
    }

    public static void main(String[] args) throws IOException {

        exportSuiFang();
        exportShaiXuan();
        exportZhiLiao();

    }
}