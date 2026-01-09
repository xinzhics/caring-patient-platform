package com.caring.sass.user.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.user.dao.CommonMsgTemplateContentMapper;
import com.caring.sass.user.entity.CommonMsgTemplateContent;
import com.caring.sass.user.entity.CommonMsgTemplateType;
import com.caring.sass.user.service.CommonMsgTemplateContentService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.user.service.CommonMsgTemplateTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * <p>
 * 业务实现类
 * 常用语模板分类
 * </p>
 *
 * @author 杨帅
 * @date 2023-05-08
 */
@Slf4j
@Service

public class CommonMsgTemplateContentServiceImpl extends SuperServiceImpl<CommonMsgTemplateContentMapper, CommonMsgTemplateContent> implements CommonMsgTemplateContentService {

    @Autowired
    CommonMsgTemplateTypeService commonMsgTemplateTypeService;

    @Override
    public boolean updateById(CommonMsgTemplateContent model) {
        CommonMsgTemplateContent content = super.getById(model.getId());
        if (Objects.isNull(content)) {
            return false;
        }
        model.setCreateTime(content.getCreateTime());
        model.setCreateUser(content.getCreateUser());
        return super.updateAllById(model);
    }

    /**
     * 导入常用语
     * @param inputStream
     * @param userType
     */
    @Override
    public void importMsgTemplate(InputStream inputStream,  String userType) {
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (workbook == null) {
            return;
        }
        Sheet sheet = workbook.getSheetAt(0);
        int i = 0;
        String typeName = null;
        String title = null;
        String content = null;
        Map<String, Long> typeIdMap = new HashMap<>();
        List<CommonMsgTemplateContent> contentList = new ArrayList<>();
        CommonMsgTemplateContent templateContent;
        for (Row row: sheet) {
            if (i == 0) {
                i++;
                continue;
            }
            typeName = null;
            title = null;
            content = null;
            Cell cell0 = row.getCell(0);
            if (cell0 != null) {
                typeName = cell0.getStringCellValue();
            }
            Cell cell1 = row.getCell(1);
            if (cell1 != null) {
                title = cell1.getStringCellValue();
            }
            Cell cell2 = row.getCell(2);
            if (cell2 != null) {
                content = cell2.getStringCellValue();
            }
            if (StrUtil.isEmpty(content)) {
                Cell cell = row.createCell(3);
                cell.setCellValue("失败， 内容不能为空");
                continue;
            } else {
                if (content.length() > 20000) {
                    Cell cell = row.createCell(3);
                    cell.setCellValue("失败， 内容长度不能超过20000");
                    continue;
                }
            }
            if (StrUtil.isEmpty(title)) {
                Cell cell = row.createCell(3);
                cell.setCellValue("失败，标题不能为空");
                continue;
            } else {
                if (title.length() > 200) {
                    Cell cell = row.createCell(3);
                    cell.setCellValue("失败， 标题长度不能超过200");
                    continue;
                }
            }
            templateContent = new CommonMsgTemplateContent();

            if (StrUtil.isNotEmpty(typeName)) {
                if (!typeIdMap.containsKey(typeName)) {
                    CommonMsgTemplateType templateType = commonMsgTemplateTypeService.getOne(Wraps.<CommonMsgTemplateType>lbQ()
                            .eq(CommonMsgTemplateType::getUserType, userType)
                            .eq(CommonMsgTemplateType::getTypeName, typeName).last(" limit 0,1 "));
                    if (Objects.nonNull(templateType)) {
                        typeIdMap.put(typeName, templateType.getId());
                    } else {
                        templateType = new CommonMsgTemplateType().setTypeName(typeName).setUserType(userType).setTypeSort(0);
                        commonMsgTemplateTypeService.save(templateType);
                        typeIdMap.put(typeName, templateType.getId());
                    }
                }
                templateContent.setTemplateTypeId(typeIdMap.get(typeName));
            }
            templateContent.setTemplateTitle(title);
            templateContent.setTemplateContent(content);
            templateContent.setUserType(userType);
            templateContent.setTemplateRelease(1);
            contentList.add(templateContent);
        }
        if (CollUtil.isNotEmpty(contentList)) {
            baseMapper.insertBatchSomeColumn(contentList);
        }
    }

}
