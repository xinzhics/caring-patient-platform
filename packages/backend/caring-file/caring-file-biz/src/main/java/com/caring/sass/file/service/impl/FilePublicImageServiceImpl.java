package com.caring.sass.file.service.impl;


import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.file.dao.FilePublicImageMapper;
import com.caring.sass.file.entity.FileMy;
import com.caring.sass.file.entity.FilePublicImage;
import com.caring.sass.file.service.FilePublicImageService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 业务实现类
 * 公共图片
 * </p>
 *
 * @author 杨帅
 * @date 2022-08-29
 */
@Slf4j
@Service

public class FilePublicImageServiceImpl extends SuperServiceImpl<FilePublicImageMapper, FilePublicImage> implements FilePublicImageService {


    @Override
    public boolean save(FilePublicImage model) {

        @NotNull(message = "文件id不能为空") Long fileId = model.getFileId();
        FilePublicImage filePublicImage = baseMapper.selectOne(Wraps.<FilePublicImage>lbQ().eq(FilePublicImage::getFileId, fileId).last(" limit 0,1 "));
        if (filePublicImage != null) {
            model.setId(filePublicImage.getId());
            return super.updateById(model);
        } else {
            return super.save(model);
        }
    }
}
