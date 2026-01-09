package com.caring.sass.file.service.impl;


import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.file.dao.FileRecentlyUsedMapper;
import com.caring.sass.file.entity.FilePublicImage;
import com.caring.sass.file.entity.FileRecentlyUsed;
import com.caring.sass.file.service.FileRecentlyUsedService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * <p>
 * 业务实现类
 * 最近使用图片
 * </p>
 *
 * @author 杨帅
 * @date 2022-08-29
 */
@Slf4j
@Service

public class FileRecentlyUsedServiceImpl extends SuperServiceImpl<FileRecentlyUsedMapper, FileRecentlyUsed> implements FileRecentlyUsedService {


    @Override
    public File downloadFile(String url) {
        UUID uuid = UUID.randomUUID();
        String string = uuid.toString();
        String replaceAll = string.replaceAll("-", "");
        try {
            return FileUtils.downLoadArticleFromUrl(url, replaceAll+".png");
        } catch (IOException e) {
            log.error("download image error, file url = {}", url);
        }
        return null;
    }
}
