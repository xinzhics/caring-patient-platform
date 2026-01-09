package com.caring.sass.tenant.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.NamedThreadFactory;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.tenant.dao.AppConfigMapper;
import com.caring.sass.tenant.dao.SequenceMapper;
import com.caring.sass.tenant.entity.AppConfig;
import com.caring.sass.tenant.enumeration.SequenceEnum;
import com.caring.sass.tenant.service.AppVersionService;
import com.caring.sass.tenant.service.batchBuild.BatchBuildRedisMessageConsumption;
import com.caring.sass.utils.BizAssert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author xinzh
 */
@Slf4j
@Component
public class ApkBuildService {

    private final BatchBuildRedisMessageConsumption batchApkBuild;

    private final FileUploadApi fileUploadApi;

    private final AppConfigMapper appConfigMapper;

    private final AppVersionService appVersionService;

    private final SequenceMapper sequenceMapper;


    /**
     * 打包线程池，由于打包频次较低，默认2个
     */
    private static final NamedThreadFactory PACKAGE_NAMED_THREAD_FACTORY = new NamedThreadFactory("apk-build-", false);

    private static final ExecutorService PACKAGE_EXECUTOR = new ThreadPoolExecutor(2, 5,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(10), PACKAGE_NAMED_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());

    /**
     * 打包结果处理线程池
     */
    private static final ExecutorService PACKAGE_RET_HANDLE_EXECUTOR = new ThreadPoolExecutor(2, 5,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(10), new NamedThreadFactory("apk-ret-", false), new ThreadPoolExecutor.AbortPolicy());


    public ApkBuildService(FileUploadApi fileUploadApi, BatchBuildRedisMessageConsumption batchApkBuild, AppConfigMapper appConfigMapper,
                           AppVersionService appVersionService, SequenceMapper sequenceMapper) {
        this.fileUploadApi = fileUploadApi;
        this.appConfigMapper = appConfigMapper;
        this.batchApkBuild = batchApkBuild;
        this.appVersionService = appVersionService;
        this.sequenceMapper = sequenceMapper;
    }

    /**
     *
     * @param domain
     * @param code
     * @param appConfig
     * @param batchApkTaskId 当一个打包任务结束时， 将 此ID发送到批量 BatchApkBuild 任务中去，判断是否还有下一个需要打包的项目
     */
    public void doBuild(String domain, String code, AppConfig appConfig, Long batchApkTaskId, Boolean thirdAuthorization) {
        BizAssert.notEmpty(domain, "项目域名不能为空");
        BizAssert.notEmpty(code, "项目code不能为空");
        BizAssert.notNull(appConfig, "app配置不能为空");
        BizAssert.notEmpty(appConfig.getAppApplicationId(), "应用id不能为空");
        BizAssert.notEmpty(appConfig.getAppVersionName(), "app配置显示版本号不能为空");
        BizAssert.notNull(appConfig.getAppVersionCode(), "app版本号不能为空");
        Long appVersion = sequenceMapper.selectSequenceVar(SequenceEnum.APP_VERSION);
        AppConfig tmp = AppConfig
                .builder().build()
                .setPackageStatus(AppConfig.PACKAGING)
                .setAppVersionCode(Convert.toInt(appVersion));
        tmp.setId(appConfig.getId());
        appConfigMapper.updateById(tmp);

        FutureTask<ApkBuildR> f = new FutureTask<>(new ApkBuildTask(domain, code, appConfig));
//         打包uniapp安卓工程
//        FutureTask<ApkBuildR> f = new FutureTask<>(new ApkAssistantBuildTask(domain, code, appConfig));
        PACKAGE_EXECUTOR.execute(f);
        try {
            PACKAGE_RET_HANDLE_EXECUTOR.submit(new ApkBuildRetRunnable(fileUploadApi, batchApkBuild, appConfigMapper, appVersionService, f, code, appConfig, batchApkTaskId, thirdAuthorization, H5Router.APK_SCAN_DOWNLOAD));
        } catch (Exception e) {
            log.error("打包异常", e);
        }
    }


    public void doUniBuild(String domain, String code, AppConfig appConfig, Long batchApkTaskId, Boolean thirdAuthorization) {
        BizAssert.notEmpty(domain, "项目域名不能为空");
        BizAssert.notEmpty(code, "项目code不能为空");
        BizAssert.notNull(appConfig, "app配置不能为空");
        BizAssert.notEmpty(appConfig.getAppApplicationId(), "应用id不能为空");
        BizAssert.notEmpty(appConfig.getUniAppVersionName(), "app配置显示版本号不能为空");
        BizAssert.notNull(appConfig.getAppVersionCode(), "app版本号不能为空");
        Long appVersion = sequenceMapper.selectSequenceVar(SequenceEnum.APP_VERSION);
        AppConfig tmp = AppConfig
                .builder().build()
                .setUniPackageStatus(AppConfig.PACKAGING)
                .setAppVersionCode(Convert.toInt(appVersion));
        tmp.setId(appConfig.getId());
        appConfigMapper.updateById(tmp);

//        FutureTask<ApkBuildR> f = new FutureTask<>(new ApkBuildTask(domain, code, appConfig));
//         打包uniapp安卓工程
        FutureTask<ApkBuildR> f = new FutureTask<>(new ApkAssistantBuildTask(domain, code, appConfig));
        PACKAGE_EXECUTOR.execute(f);
        try {
            PACKAGE_RET_HANDLE_EXECUTOR.submit(new ApkBuildRetRunnable(fileUploadApi, batchApkBuild, appConfigMapper, appVersionService, f, code, appConfig, batchApkTaskId, thirdAuthorization, H5Router.UNI_APK_SCAN_DOWNLOAD));
        } catch (Exception e) {
            log.error("打包异常", e);
        }
    }

}
