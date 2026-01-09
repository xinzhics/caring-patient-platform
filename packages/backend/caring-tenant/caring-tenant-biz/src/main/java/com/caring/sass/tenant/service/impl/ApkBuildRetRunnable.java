package com.caring.sass.tenant.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.tenant.dao.AppConfigMapper;
import com.caring.sass.tenant.entity.AppConfig;
import com.caring.sass.tenant.entity.AppVersion;
import com.caring.sass.tenant.service.AppVersionService;
import com.caring.sass.tenant.service.batchBuild.BatchBuildRedisMessageConsumption;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.hibernate.validator.constraints.Length;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.FutureTask;

/**
 * app打包结果处理任务
 *
 * @author xinzh
 */
@Slf4j
public class ApkBuildRetRunnable implements Runnable {

    private final FileUploadApi fileUploadApi;

    private final AppConfigMapper appConfigMapper;

    private final AppVersionService appVersionService;

    private FutureTask<ApkBuildR> futureTask;

    private BatchBuildRedisMessageConsumption batchApkBuild;

    private Long batchApkTaskId;
    /**
     * 项目code
     */
    private String code;


    /**
     * 项目配置
     */
    private AppConfig appConfig;

    private Boolean thirdAuthorization;

    private String apkScanDownload;

    public ApkBuildRetRunnable(FileUploadApi fileUploadApi, BatchBuildRedisMessageConsumption batchApkBuild, AppConfigMapper appConfigMapper,
                               AppVersionService appVersionService, FutureTask<ApkBuildR> futureTask,
                               String code, AppConfig appConfig, Long batchApkTaskId, Boolean thirdAuthorization, String apkScanDownload) {
        this.fileUploadApi = fileUploadApi;
        this.batchApkBuild = batchApkBuild;
        this.appConfigMapper = appConfigMapper;
        this.appVersionService = appVersionService;
        this.futureTask = futureTask;
        this.code = code;
        this.appConfig = appConfig;
        this.batchApkTaskId = batchApkTaskId;
        this.thirdAuthorization = thirdAuthorization;
        this.apkScanDownload = apkScanDownload;
    }

    @Override
    public void run() {
        ApkBuildR r = null;
        try {
            log.info("进行打包结果处理...");
            // 阻塞等待任务处理完成
            r = futureTask.get();
            log.info("获取到打包结果，进行处理...");
            BaseContextHandler.setTenant(code);

            if (!r.isSuccess()) {
                // 打包失败
                log.error("打包失败，原因为:{}",r.getMsg());
                buildFailed(appConfig.getId(), batchApkTaskId, code, r.getMsg());
                return;
            }
            File apk = r.getApkFile();
            uploadApk(apk, appConfig.getId(), r.getWorkDir(), r.getTenantDomain(), appConfig.getAppVersionName(), appConfig.getAppApplicationName());
        } catch (Exception e) {
            // 处理打包结果异常
            log.error("处理打包结果异常", e);
            buildFailed(appConfig.getId(), batchApkTaskId, code, "处理打包结果异常");
        } finally {
            if (r != null && r.getWorkDir().exists()) {
                r.getWorkDir().delete();

            }
        }
    }

    public void updateVersion(AppConfig appConfig) {
        appConfig = appConfigMapper.selectById(appConfig.getId());
        AppVersion dto = new AppVersion();
        dto.setAppName(appConfig.getAppApplicationName());
        dto.setBoundleId(appConfig.getAppApplicationId());
        dto.setEnable(1);
        dto.setPlatform(1);
        dto.setBoundleId(appConfig.getAppApplicationId());

        dto.setQrcodeImageUrl(appConfig.getApkUrl());
        dto.setUpgradeDesc("本次升级修正了上一版本中出现的BUG，进一步增强了APP的稳定性。");
        dto.setUrl(appConfig.getUploadUrl());
        dto.setVersionCode(appConfig.getAppVersionCode());
        dto.setVersionName(StringUtils.isEmpty(appConfig.getAppVersionName()) ? String.valueOf(appConfig.getAppVersionCode()) : appConfig.getAppVersionName());
        appVersionService.save(dto);
    }

    public void uploadApk(File releaseApk, Long configId, File wordDir, String tenantDomain, String appVersionName, String appApplicationName) {
        log.info("打包完成，进行上传处理...");
        if (releaseApk.exists()) {
            com.caring.sass.file.entity.File data = null;
            try {
                MockMultipartFile multipartFile = FileUtils.fileToFileItem(releaseApk);
                String fileName = "";
                // 原生的
                if (apkScanDownload.equals(H5Router.APK_SCAN_DOWNLOAD)) {
                    fileName = appApplicationName + appConfig.getAppVersionName();
                } else {
                    fileName = appApplicationName + "uni." + appConfig.getUniAppVersionName();
                }
                R<com.caring.sass.file.entity.File> fileR = fileUploadApi.uploadAppFile(0L, multipartFile, fileName);
                if (fileR.getIsSuccess()) {
                    data = fileR.getData();
                }
            } catch (Exception e) {
                releaseApk.delete();
                log.error("已经生成APK文件，但上传失败", e);
            }
            if (apkScanDownload == null) {
                apkScanDownload = H5Router.APK_SCAN_DOWNLOAD;
            }
            if (data != null && !StringUtils.isEmpty(data.getUrl())) {
                String downloadUrl = ApplicationDomainUtil.wxPatientBizUrl(tenantDomain, thirdAuthorization, apkScanDownload, "apkUrl=" + data.getUrl(), "tenantCode=" + BaseContextHandler.getTenant());
                String qrCode = getQrCode(downloadUrl, wordDir);
                AppConfig ap = new AppConfig();
                ap.setId(configId);
                if (apkScanDownload.equals(H5Router.APK_SCAN_DOWNLOAD)) {
                    ap.setApkUrl(qrCode);
                    ap.setUploadUrl(data.getUrl());
                    ap.setPackageStatus(AppConfig.PACKAGING_SUCCESS);
                    ap.setAppVersionName(appConfig.getAppVersionName());
                } else {
                    ap.setUniApkUrl(qrCode);
                    ap.setUniApkDownloadUrl(data.getUrl());
                    ap.setUniPackageStatus(AppConfig.PACKAGING_SUCCESS);
                    ap.setUniAppVersionName(appConfig.getUniAppVersionName());
                }
                buildSuccess(ap, batchApkTaskId, code);
            } else {
                buildFailed(configId, batchApkTaskId, code, "已经生成APK文件，但上传失败");
                log.error("已经生成APK文件，但上传失败");
            }
        }
    }

    private void buildSuccess(AppConfig ap, Long batchApkTaskId, String code) {

        appConfigMapper.updateById(ap);
        updateVersion(ap);

        batchApkBuild.childTaskMessage(batchApkTaskId, code, true, null);

    }

    private void buildFailed(Long id, Long batchApkTaskId, String code, String message) {
        AppConfig ap = appConfigMapper.selectById(id);
        if (apkScanDownload.equals(H5Router.APK_SCAN_DOWNLOAD)) {
            ap.setPackageStatus(AppConfig.PACKAGING_FAIL);
        } else {
            ap.setUniPackageStatus(AppConfig.PACKAGING_FAIL);
        }
        appConfigMapper.updateById(ap);

        batchApkBuild.childTaskMessage(batchApkTaskId, code, false, message);
    }

    public String getQrCode(String content, File wordDir) {
        log.info("生成二维码中...");
        String tmpIconPath;
        try {
            tmpIconPath = FileUtils.downLoadFromUrl(appConfig.getAppIcon(), "icon.png", wordDir.getPath());
            byte[] out2 = QrCodeUtil.generatePng(content, QrConfig.create().setImg(tmpIconPath).setWidth(300).setHeight(300));
            String qrName = "qrcode_" + RandomUtil.randomString(6);
            MockMultipartFile mockMultipartFile = new MockMultipartFile(qrName, qrName + ".png", ContentType.APPLICATION_OCTET_STREAM.toString(), out2);
            log.info("生成二维码完成，开始上传中...");
            R<com.caring.sass.file.entity.File> r = fileUploadApi.upload(0L, mockMultipartFile);
            log.info("生成二维码完成，并上传成功");
            return r.getData().getUrl();
        } catch (IOException e) {
            log.error("下载项目icon失败", e);
        }
        return "";
    }
}
