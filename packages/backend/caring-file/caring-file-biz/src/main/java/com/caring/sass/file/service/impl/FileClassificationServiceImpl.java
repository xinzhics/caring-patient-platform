package com.caring.sass.file.service.impl;


import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.file.constant.ClassificationBelongEnum;
import com.caring.sass.file.dao.FileClassificationMapper;
import com.caring.sass.file.entity.FileClassification;
import com.caring.sass.file.entity.FileMy;
import com.caring.sass.file.entity.FilePublicImage;
import com.caring.sass.file.service.FileClassificationService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.file.service.FileMyService;
import com.caring.sass.file.service.FilePublicImageService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * <p>
 * 业务实现类
 * 图片分组
 * </p>
 *
 * @author 杨帅
 * @date 2022-08-29
 */
@Slf4j
@Service

public class FileClassificationServiceImpl extends SuperServiceImpl<FileClassificationMapper, FileClassification> implements FileClassificationService {


    @Autowired
    FileMyService fileMyService;

    @Autowired
    FilePublicImageService filePublicImageService;


    @Override
    public boolean removeById(Serializable id) {

        FileClassification classification = baseMapper.selectById(id);
        String classificationBelong = classification.getClassificationBelong();
        if (ClassificationBelongEnum.MY_IMAGE.toString().equals(classificationBelong)) {
            fileMyService.remove(Wraps.<FileMy>lbQ().eq(FileMy::getFileClassificationId, id));
        } else if (ClassificationBelongEnum.PUBLIC_IMAGE.toString().equals(classificationBelong)){
            filePublicImageService.remove(Wraps.<FilePublicImage>lbQ().eq(FilePublicImage::getFileClassificationId, id));
        }
        baseMapper.deleteById(id);
        return true;

    }



}
