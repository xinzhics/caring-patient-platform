package com.caring.sass.nursing.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.caring.sass.base.R;
import com.caring.sass.common.utils.ChineseToEnglishUtil;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.nursing.dao.drugs.SysDrugsCategoryLinkMapper;
import com.caring.sass.nursing.dao.drugs.SysDrugsCategoryMapper;
import com.caring.sass.nursing.dao.drugs.SysDrugsMapper;
import com.caring.sass.nursing.entity.drugs.SysDrugs;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategory;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategoryLink;
import org.apache.http.entity.ContentType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName DrugsImport 药品导入 读取表格和文件。
 * @Description
 * @Author yangShuai
 * @Date 2021/1/19 10:19
 * @Version 1.0
 */
@Component
public class DrugsImport {

    @Autowired
    FileUploadApi fileUploadApi;

    @Autowired
    SysDrugsMapper sysDrugsMapper;

    @Autowired
    SysDrugsCategoryMapper sysDrugsCategoryMapper;

    @Autowired
    SysDrugsCategoryLinkMapper sysDrugsCategoryLinkMapper;

    public Collection<SysDrugsCategory> allCategories = null;

    /**
     * 初始化类型
     */
    public void initCategory() {
        LbqWrapper<SysDrugsCategory> drugsCategoryLbqWrapper = new LbqWrapper<>();
        List<SysDrugsCategory> categories = sysDrugsCategoryMapper.selectList(drugsCategoryLbqWrapper);

        Map<Long, SysDrugsCategory> allPatient = new HashMap<>();
        List<SysDrugsCategory> allC = new ArrayList<>();
        categories.forEach(sysDrugsCategory -> {
            if (Objects.isNull(sysDrugsCategory.getParentId())) {
                allPatient.put(sysDrugsCategory.getId(), sysDrugsCategory);
            } else {
                allC.add(sysDrugsCategory);
            }
        });

        for (SysDrugsCategory category : allC) {
            SysDrugsCategory sysDrugsCategory = allPatient.get(category.getParentId());
            List<SysDrugsCategory> drugsCategories = sysDrugsCategory.getCategories();
            if (org.springframework.util.CollectionUtils.isEmpty(drugsCategories)) {
                drugsCategories = new ArrayList<>();
            }
            drugsCategories.add(category);
            sysDrugsCategory.setCategories(drugsCategories);
        }
        allCategories = allPatient.values();

    }

    /**
     * 下载药品的 压缩包
     * @param fileId
     * @param dir
     * @return
     */
    public String downDrugsZip(Long fileId, String dir) {
        com.caring.sass.file.entity.File query = new com.caring.sass.file.entity.File();
        query.setId(fileId);
        R<List<com.caring.sass.file.entity.File>> fileUploadApiFile = fileUploadApi.query(query);
        if (fileUploadApiFile != null && fileUploadApiFile.getIsSuccess()) {
            List<com.caring.sass.file.entity.File> fileList = fileUploadApiFile.getData();
            if (CollectionUtils.isNotEmpty(fileList)) {
                com.caring.sass.file.entity.File file = fileList.get(0);
                String fileUrl = file.getUrl();
                String fileName = "drugs" + fileId;
                try {
                    return FileUtils.downLoadFromUrl(fileUrl, fileName, dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String importDrugsFromZip(Long fileId, String folderName, String xlsxName) {
        String dir =  System.getProperty("user.dir") + "/drugs/";
        String filePath = downDrugsZip(fileId, dir);
        File zip = new File(filePath);
        File folder = new File(dir);
        try {
            FileUtils.unzip(folder, zip);
        } catch (Exception exception) {
            return "解压文件失败" + exception.getMessage();
        }
        try {
            zip.delete();
        } catch (Exception e) {

        }
        // 读取表格  dir + folderName + "/" + xlsxName
        String file = dir + folderName + "/" + xlsxName;
        String drugsImageFolderPath = dir + folderName + "/";

        XSSFWorkbook hssfWorkbook = null;
        try {
            hssfWorkbook = getHSSFWorkbook(file);
        } catch (IOException e) {
            return "获取药品表格失败";
        }
        try {
            uploadImageToCloudAndUpdateXlsx(hssfWorkbook, drugsImageFolderPath);
        } catch (IOException e) {
            return "上传药品图片失败";
        }
        // 初始化药品分类
        initCategory();
        XSSFSheet xssfSheet = hssfWorkbook.getSheetAt(0);
        readXssSheet(xssfSheet);

        try {
            if (hssfWorkbook != null) {
                hssfWorkbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File floder = new File( dir + folderName);
        floder.exists();
        return "上传成功";
    }

    /**
     * 读取表格药品
     * @param xssfSheet
     */
    public void readXssSheet(XSSFSheet xssfSheet) {

        int lastRowNum = xssfSheet.getLastRowNum();
        XSSFRow xssfRow;
        List<SysDrugs> sysDrugs = new ArrayList<>(lastRowNum + 10);
        List<SysDrugs> updateDrugs = new ArrayList<>(lastRowNum + 10);

        List<SysDrugsCategoryLink> sysDrugsCategoryLinks = new ArrayList<>(lastRowNum + 10);
        SysDrugs.SysDrugsBuilder builder;
        SysDrugsCategoryLink.SysDrugsCategoryLinkBuilder linkBuilder;
        XSSFCell cell;

        for (int i = 1; i <= lastRowNum; i++) {
            xssfRow = xssfSheet.getRow(i);
            builder = SysDrugs.builder();
            linkBuilder = SysDrugsCategoryLink.builder();
            cell = xssfRow.getCell(0);              // id
            Long parseLong = null;
            if (cell != null && StringUtils.isNotEmptyString(cell.getStringCellValue())) {
                parseLong = Long.parseLong(cell.getStringCellValue());
            }
            cell = xssfRow.getCell(1);              // 名称
            if (cell != null) {
                builder.name(cell.getStringCellValue());
                builder.pyszm(ChineseToEnglishUtil.getPinYinHeadChar(cell.getStringCellValue()));
            }

            cell = xssfRow.getCell(2);              // 通用名
            if (cell != null) {
                builder.genericName(cell.getStringCellValue());
            }

            cell = xssfRow.getCell(3);              // 一级分类
            String cellValue = cell.getStringCellValue().trim();
            SysDrugsCategory category = findPatientCategory(cellValue);
            if (category == null) {
                category = SysDrugsCategory.builder().label(cellValue).build();
                sysDrugsCategoryMapper.insert(category);
                initCategory();
            }

            cell = xssfRow.getCell(4);              // 二级分类
            String stringCellValue = cell.getStringCellValue().trim();
            SysDrugsCategory cCategory = findCCategory(category, stringCellValue);
            if (Objects.isNull(cCategory)) {
                cCategory = SysDrugsCategory.builder().label(stringCellValue).parentId(category.getId()).build();
                sysDrugsCategoryMapper.insert(cCategory);
                initCategory();
            }

            cell = xssfRow.getCell(5);          // 图标
            if (cell != null) {
                builder.icon(cell.getStringCellValue());
            }

            cell = xssfRow.getCell(6);          // 批准文号
            if (cell != null) {
                builder.gyzz(cell.getStringCellValue());
            }

            cell = xssfRow.getCell(7);          // 厂家
            if (cell != null) {
                builder.manufactor(cell.getStringCellValue());
            }

            cell = xssfRow.getCell(8);          // 规格
            if (cell != null) {
                builder.spec(cell.getStringCellValue());
            }

            cell = xssfRow.getCell(9);          // 用法用量
            if (cell != null) {
                builder.dosage(cell.getStringCellValue());
            }

            cell = xssfRow.getCell(10);          // 适用症
            if (cell != null) {
                builder.applicableDisease(cell.getStringCellValue());
            }

            cell = xssfRow.getCell(11);          // 禁忌症
            if (cell != null) {
                builder.taboo(cell.getStringCellValue());
            }

            cell = xssfRow.getCell(12);          // 不良反应
            if (cell != null) {
                builder.sideEffects(cell.getStringCellValue());
            }

            cell = xssfRow.getCell(13);          // 药物相互作用
            if (cell != null) {
                builder.interaction(cell.getStringCellValue());
            }

            cell = xssfRow.getCell(14);          // 单位
            if (cell != null) {
                builder.unit(cell.getStringCellValue());
            }

            cell = xssfRow.getCell(15);          // code

            if (cell == null || cell.getStringCellValue() == null) {
                if (parseLong != null) {
                    SysDrugs drugs = sysDrugsMapper.selectById(parseLong);
                    if (Objects.nonNull(drugs)) {
                        builder.code(drugs.getCode());
                    }
                }
            } else {
                builder.code(cell.getStringCellValue());
            }

            cell = xssfRow.getCell(16);          // 是否otc
            if (cell != null ) {
                String value = cell.getStringCellValue();
                if ("是".equals(value)) {
                    builder.isOtc(true);
                } else {
                    builder.isOtc(false);
                }
            }

            cell = xssfRow.getCell(17);          // 剂型
            if (cell != null) {
                builder.dosageForm(cell.getStringCellValue());
            }

            if (parseLong != null ) {
                updateDrugs.add(builder.build());
                sysDrugsCategoryLinks.add(linkBuilder.categoryId(cCategory.getId()).categoryParentId(category.getId()).drugsId(parseLong).build());
            } else {

                SysDrugsCategoryLink build = linkBuilder.categoryId(cCategory.getId()).categoryParentId(category.getId()).build();
                List<SysDrugsCategoryLink> builds = new ArrayList<>();
                builds.add(build);
                builder.sysDrugsCategoryLinkList(builds);
                sysDrugs.add(builder.build());
            }
        }

        // 保存药品
        saveDrgus(sysDrugs);
        updateDrugs(updateDrugs, sysDrugsCategoryLinks);
    }

    @Transactional
    public void saveDrgus(List<SysDrugs> sysDrugs) {
        List<SysDrugsCategoryLink> sysDrugsCategoryLinks = new ArrayList<>();
        for (SysDrugs drugs : sysDrugs) {
            List<SysDrugsCategoryLink> linkList = drugs.getSysDrugsCategoryLinkList();
            sysDrugsMapper.insert(drugs);
            if (CollectionUtils.isNotEmpty(linkList)) {
                for (SysDrugsCategoryLink link : linkList) {
                    link.setDrugsId(drugs.getId());
                }
                sysDrugsCategoryLinks.addAll(linkList);
            }
        }
        if (CollectionUtils.isNotEmpty(sysDrugsCategoryLinks)) {
            sysDrugsCategoryLinkMapper.insertBatchSomeColumn(sysDrugsCategoryLinks);
        }

    }

    @Transactional
    public void updateDrugs(List<SysDrugs> sysDrugs, List<SysDrugsCategoryLink> sysDrugsCategoryLinks) {
        if (CollectionUtils.isEmpty(sysDrugs)) {
            return;
        }
        List<Long> drugsIds = sysDrugs.stream().map(SysDrugs::getId).collect(Collectors.toList());
        LbqWrapper<SysDrugsCategoryLink> sysDrugsLbqWrapper = new LbqWrapper<>();
        sysDrugsLbqWrapper.in(SysDrugsCategoryLink::getDrugsId, drugsIds);
        sysDrugsCategoryLinkMapper.delete(sysDrugsLbqWrapper);

        for (SysDrugs sysDrug : sysDrugs) {
            sysDrugsMapper.updateAllById(sysDrug);
        }
        if (CollectionUtils.isNotEmpty(sysDrugsCategoryLinks)) {
            sysDrugsCategoryLinkMapper.insertBatchSomeColumn(sysDrugsCategoryLinks);
        }

    }

    /**
     * 查找父类
     * @param name
     * @return
     */
    public SysDrugsCategory findPatientCategory(String name) {

        for (SysDrugsCategory category : allCategories) {
            if (category.getLabel().contains(name)) {
                return category;
            }
        }
        return null;
    }

    /**
     * 查找子类
     * @param category
     * @param name
     * @return
     */
    public SysDrugsCategory findCCategory(SysDrugsCategory category, String name) {
        List<SysDrugsCategory> drugsCategoryList = category.getCategories();
        if (org.springframework.util.CollectionUtils.isEmpty(drugsCategoryList)) {
            return null;
        }
        for (SysDrugsCategory drugsCategory : drugsCategoryList) {
            if (drugsCategory.getLabel().contains(name)) {
                return drugsCategory;
            }
        }
        return null;
    }

    /**
     * 读取表格和图片。将图片进行压缩后上传到华为云，将图片路径写入到表格中防止，
     * 保存药品库时出现异常导致任务失败，图片重复上传。
     * @param workbook 表格
     * @param drugsImageFolderPath 图片文件的根路径
     * @throws IOException
     */
    public void uploadImageToCloudAndUpdateXlsx(XSSFWorkbook workbook, String drugsImageFolderPath) throws IOException {
        MockMultipartFile multipartFile;
        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 1; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            XSSFCell cell = row.getCell(5);
            XSSFCell updateImageStatus = row.getCell(18);
            if (cell == null) {
                continue;
            }
            if (updateImageStatus != null && updateImageStatus.getStringCellValue() != null) {
                continue;
            }
            String filename = cell.getStringCellValue();
            if (filename == null || "".equals(filename.trim())) {
                continue;
            }
            filename = filename.trim();
            File file = new File(drugsImageFolderPath + filename);
            FileInputStream inputStream;
            if (file.exists()) {
                inputStream =  new FileInputStream((drugsImageFolderPath + filename));
            } else {
                continue;
            }
            multipartFile = new MockMultipartFile(filename, filename, ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
            R<com.caring.sass.file.entity.File> upload = fileUploadApi.upload(1001L, multipartFile);
            if (upload != null && upload.getIsSuccess()) {
                com.caring.sass.file.entity.File data = upload.getData();
                if (data != null) {
                    cell.setCellValue(data.getUrl());
                    if (updateImageStatus == null) {
                        XSSFCell rowCell = row.createCell(19);
                        rowCell.setCellValue("上传成功");
                    }
                }
            }
        }
    }


    public XSSFWorkbook getHSSFWorkbook(String excelUrl) throws IOException {
        return new XSSFWorkbook(new FileInputStream(excelUrl));
    }

}
