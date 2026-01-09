package com.caring.sass.tenant.service.impl;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.Serializable;

/**
 * apk打包结果
 *
 * @author xinzh
 */
@Data
@Accessors(chain = true)
public class ApkBuildR implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 消息
     */
    private String msg;

    /**
     * 生成apk文件
     */
    private File apkFile;

    /**
     * 项目域名
     */
    private String tenantDomain;

    /**
     * 工作目录，上传apk后，删除
     */
    private File workDir;
}
