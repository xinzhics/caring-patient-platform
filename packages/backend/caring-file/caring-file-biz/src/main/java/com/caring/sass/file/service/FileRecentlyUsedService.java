package com.caring.sass.file.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.file.entity.FileRecentlyUsed;

import java.io.File;

/**
 * <p>
 * 业务接口
 * 最近使用图片
 * </p>
 *
 * @author 杨帅
 * @date 2022-08-29
 */
public interface FileRecentlyUsedService extends SuperService<FileRecentlyUsed> {


    File downloadFile(String url);


}
