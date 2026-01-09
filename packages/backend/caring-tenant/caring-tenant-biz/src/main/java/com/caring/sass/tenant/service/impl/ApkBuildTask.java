package com.caring.sass.tenant.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.system.SystemUtil;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.ImageUtils;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.tenant.entity.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * 1、检查源码文件是否存在->初始化工作目录
 * 2、查找源码文件
 * 3、解压文件
 * 4、生成脚本文件
 * 5、生成build_config.gradle文件
 * 6、生成图片到源文件夹
 * 7、执行脚本文件
 * 8、返回打包结果
 *
 * @author xinzh
 */
@Slf4j
public class ApkBuildTask implements Callable<ApkBuildR> {

    /**
     * 源码文件名
     */
    public static final String SOURCE_FULL_NAME = "SaasApp.zip";
    public static final String SOURCE_NAME = "SaasApp";
    public static final String BUILD_SUCCESS_FLAG = "BUILD ASSEMBLE_RELEASE SUCCESS";

    public static final String PACKAGE_NAME = "com.caringsaas.";

    /**
     * 项目子域名
     */
    private String domain;

    /**
     * 项目code
     */
    private String code;

    private AppConfig appConfig;

    /**
     * app打包文件夹
     */
    private static File appPackageFolder;

    private static File tmpDir;

    /**
     * 项目打包所在目录
     */
    private static File workFolder;

    public ApkBuildTask(String domain, String code, AppConfig appConfig) {
        this.domain = domain;
        this.code = code;
        this.appConfig = appConfig;
    }

    @Override
    public ApkBuildR call() {
        try {
            // 1、检查源码文件是否存在->初始化工作目录
            init();
            // 2、查找源码文件
            File sourceZip = getSourceFile();
            // 3、解压文件
            File sourceFile = ZipUtil.unzip(sourceZip, tmpDir);
            // 4、生成脚本文件
            File bashFile = createBashFile(workFolder.getPath());
            // 5、生成build_config.gradle文件
            File gradleFile = createGradleFile(domain, Base64.encode(code), appConfig);
            // 6、生成图片到源文件夹
            writeAppImage(appConfig.getAppLaunchImage(), appConfig.getAppIcon(), appConfig.getAppApplicationName());
            // 7、执行脚本文件
            boolean success = execBuild(bashFile.getAbsolutePath(), workFolder.getAbsolutePath());
            if (!success) {
                return new ApkBuildR().setTenantDomain(domain).setWorkDir(workFolder).setSuccess(false).setMsg("执行打包脚本失败");
            }
            // 8、获取打包apk文件
            File release = getApkFile(workFolder, appConfig);
            if (!release.exists()) {
                return new ApkBuildR().setTenantDomain(domain).setWorkDir(workFolder).setSuccess(false).setMsg(release.getAbsolutePath() + ":打包文件不存在");
            }
            return new ApkBuildR().setTenantDomain(domain).setWorkDir(workFolder).setSuccess(true).setMsg("成功").setApkFile(release);
        } catch (Exception e) {
            log.error("打包失败", e);
            return new ApkBuildR().setTenantDomain(domain).setWorkDir(workFolder).setSuccess(false).setMsg(e.getMessage());
        }
    }

    private void init() {
        File sourceFolder = getSourceFile();
        if (!sourceFolder.exists()) {
            throw new RuntimeException(sourceFolder.getPath() + "源码不存在");
        }

        appPackageFolder = new File(ApplicationProperties.getTempFolderLocation(), "appPackage");
        if (!appPackageFolder.exists()) {
            appPackageFolder.mkdirs();
        }

        tmpDir = new File(appPackageFolder, code + "_" + RandomUtil.randomString(4));
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        workFolder = new File(tmpDir, SOURCE_NAME);
        if (!workFolder.exists()) {
            workFolder.mkdirs();
        }
    }

    private static File getSourceFile() {
        return new File(ApplicationProperties.getApkSourceCodeFolder() + SystemUtil.getOsInfo().getFileSeparator() + SOURCE_FULL_NAME);
    }

    private static File createGradleFile(String domain, String code, AppConfig appConfig) {
        File gradle = new File(workFolder, "build_config.gradle");
        if (gradle.exists()) {
            FileUtil.del(gradle);
        }
        String[] str = new String[]{"ext {", "    signature = [", "            storeFile : \"key.keystore\",", "            keyAlias : \"waiter\",", "            storePassword : \"password\",", "            keyPassword: \"password\"", "    ]",
                "    appConfig = [", "            domain : \"" + domain + "\",",
                "            applicationId: \"" + PACKAGE_NAME + domain + "\",",
                "            tenant: \"" + code + "\",",
                "            versionCode : " + appConfig.getAppVersionCode() + ",",
                "            versionName : \"" + appConfig.getAppVersionName() + "\",",
                "            appName : \"" + appConfig.getAppApplicationName() + "\",",
                "            hmsAppId : \"" + (StringUtils.isEmpty(appConfig.getAppHuaweiAppid()) ? "" : appConfig.getAppHuaweiAppid()) + "\",",
                "            miAppId : \"" + (StringUtils.isEmpty(appConfig.getMiAppId()) ? "" : appConfig.getMiAppId()) + "\",",
                "            miAppKey : \"" + (StringUtils.isEmpty(appConfig.getMiAppKey()) ? "" : appConfig.getMiAppKey()) + "\",",
                "            appUserCall : \"" + (StringUtils.isEmpty(appConfig.getAppUserCall()) ? "" : appConfig.getAppUserCall()) + "\",",
                "            wxUserCall : \"" + (StringUtils.isEmpty(appConfig.getWxUserCall()) ? "" : appConfig.getWxUserCall()) + "\",",
                "            jpushAppkey : \"" + appConfig.getJpushAppkey() + "\"", "    ]", "}"};
        FileUtil.writeLines(ListUtil.toList(str), gradle, "utf-8");
        return gradle;
    }

    public void writeAppImage(String appLaunchImage, String appIcon, String appName) throws Exception {
        File launchFile = new File(workFolder, "app/src/main/res/mipmap/launch.png");
        File mdpiStandard = new File(workFolder, "app/src/main/res/mipmap/ic_launcher.png");
        File mdpi = new File(workFolder, "app/src/main/res/mipmap-mdpi/ic_launcher.png");
        File hdpi = new File(workFolder, "app/src/main/res/mipmap-hdpi/ic_launcher.png");
        File xhdpi = new File(workFolder, "app/src/main/res/mipmap-xhdpi/ic_launcher.png");
        File xxhdpi = new File(workFolder, "app/src/main/res/mipmap-xxhdpi/ic_launcher.png");
        File xxxhdpi = new File(workFolder, "app/src/main/res/mipmap-xxxhdpi/ic_launcher.png");
        File icon = new File(workFolder, "icon.png");
        if (launchFile.exists()) {
            launchFile.delete();
        }

        // ImageUtils.resizeImage(appLaunchImage, launchFile, workFolder, 720, 1080);  不压缩启动图
        FileUtils.downLoadFromFile(appLaunchImage, "/launch", new File(workFolder, "app/src/main/res/mipmap/").getAbsolutePath());
        if (icon.exists()) {
            icon.delete();
        }

        ImageUtils.resizeImage(appIcon, mdpi, workFolder, 48, 48);
        ImageUtils.resizeImage(appIcon, hdpi, workFolder, 72, 72);
        ImageUtils.resizeImage(appIcon, xhdpi, workFolder, 96, 96);
        ImageUtils.resizeImage(appIcon, xxhdpi, workFolder, 144, 144);
        ImageUtils.resizeImage(appIcon, xxxhdpi, workFolder, 192, 192);
        ImageUtils.resizeImage(appIcon, mdpiStandard, workFolder, 192, 192);
        boolean defaultLaunchImage = Objects.equals(appLaunchImage, ApplicationProperties.getApkLaunchImage());
        if (defaultLaunchImage) {
            mergeLaunchImg(launchFile, xxxhdpi, appName);
        }
    }

    private static File createBashFile(String workFolder) {
        List<String> cmdList = new ArrayList();
        String androidHome = getAndroidHome();
        File exec = null;
        if (SystemUtil.getOsInfo().isWindows()) {
            exec = new File(workFolder, "build.bat");
            if (exec.exists()) {
                FileUtil.del(exec);
            }

            if (StrUtil.isNotEmpty(androidHome)) {
                cmdList.add("set ANDROID_HOME=" + androidHome);
            }
            cmdList.add("cd /d %1");
            cmdList.add("gradle clean assembleRelease");
            FileUtil.writeLines(cmdList, exec, "utf-8");
        } else {
            // 仅支持windows或linux内核
            exec = new File(workFolder, "build.sh");
            if (exec.exists()) {
                FileUtil.del(exec);
            }
            cmdList.add("#!/bin/sh");
            if (StrUtil.isNotBlank(androidHome)) {
                cmdList.add("export ANDROID_HOME=" + androidHome);
            } else {
                cmdList.add("export ANDROID_HOME=/usr/local/android");
            }

            cmdList.add("cd $1");
            cmdList.add("gradle clean assembleRelease && echo '" + BUILD_SUCCESS_FLAG + "'");
            FileUtil.writeLines(cmdList, exec, "utf-8");
            RuntimeUtil.exec("chmod -R a+x " + exec.getAbsolutePath());

        }
        return exec;
    }

    private static String getAndroidHome() {
        String androidHome = System.getProperty("ANDROID_HOME");
        if (StringUtils.isEmpty(androidHome)) {
            androidHome = System.getenv("ANDROID_HOME");
        }
        return androidHome;
    }

    private static File getApkFile(File workFolder, AppConfig appConfig) {
        String child = String.format("app/build/outputs/apk/release/%s_%s_%s.apk", appConfig.getAppVersionName(), appConfig.getAppApplicationName(), appConfig.getAppVersionCode());
//        return new File(workFolder, "app/build/outputs/apk/release/" + appConfig.getAppApplicationName() + "_" + appConfig.getAppVersionName() + "_" + appConfig.getAppVersionCode() + ".apk");
        return new File(workFolder, child);
    }

    /**
     * 在背景图片上 写入 项目名称
     */
    private static void mergeLaunchImg(File backgroundImage, File iconImage, String text) {
        try {
            BufferedImage background = ImageIO.read(backgroundImage);
            BufferedImage icon = ImageIO.read(iconImage);
            Graphics2D g = background.createGraphics();
            g.drawImage(icon, 276, 225, 170, 170, (ImageObserver) null);
            Font font = new Font("宋体", 1, 40);
            Color color = new Color(41, 94, 167);
            g.setColor(color);
            g.setFont(font);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            FontMetrics fontMetrics = g.getFontMetrics(font);
            int centerX = background.getWidth() / 2;
            int textWidth = fontMetrics.stringWidth(text);
            g.drawString(text, centerX - textWidth / 2, 450);
            g.dispose();
            ImageIO.write(background, "png", backgroundImage);
        } catch (Exception var12) {
            log.error("", var12);
        }
    }

    public boolean execBuild(String exe, String dirName) {
        String sr = exe + " " + dirName;
        log.info("执行打包脚本[{}]", sr);
        String ret = RuntimeUtil.execForStr(sr);
        if (StrUtil.isNotBlank(ret) && ret.contains(BUILD_SUCCESS_FLAG)) {
            return true;
        }
        log.error("打包结果为：[{}]", ret);
        return false;
    }
}
