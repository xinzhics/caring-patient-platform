package com.caring.sass.tenant.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.system.SystemUtil;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.tenant.dto.DrawableEnum;
import com.caring.sass.tenant.entity.AppConfig;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * uni-app 医助打包
 * <p>
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
public class ApkAssistantBuildTask implements Callable<ApkBuildR> {

    /**
     * 源码文件名
     */
    public static final String SOURCE_FULL_NAME = "assistantApp.zip";
    public static final String SOURCE_NAME = "assistantApp";
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

    private static File uniLaunchFolder;

    public ApkAssistantBuildTask() {
    }

    public ApkAssistantBuildTask(String domain, String code, AppConfig appConfig) {
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
            // 6.替换基座文件
            replaceBaseFiles(workFolder.getPath(), appConfig.getDcloudAppid(), Base64.encode(code), appConfig.getUniAppVersionName(), domain);
            // 7、生成图片到源文件夹
            writeAppImage(appConfig.getAppLaunchImage(), appConfig.getAppIcon(), appConfig.getAppApplicationName());
            // 8、执行脚本文件
            boolean success = execBuild(bashFile.getAbsolutePath(), workFolder.getAbsolutePath());
            if (!success) {
                return new ApkBuildR().setTenantDomain(domain).setWorkDir(workFolder).setSuccess(false).setMsg("执行打包脚本失败");
            }
            // 9、获取打包apk文件
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
                "            dcloudAppkey: \"" + appConfig.getDcloudAppkey() + "\",",
                "            versionCode : " + appConfig.getAppVersionCode() + ",",
                "            versionName : \"" + appConfig.getUniAppVersionName() + "\",",
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


    private static void drawImage(File icon, File bg, File result) {
        BufferedImage image = null;
        BufferedImage iconImage = null;
        try {
            image = ImageIO.read(bg);
            iconImage = ImageIO.read(icon);
            Graphics2D graphics = image.createGraphics();
            int x = (image.getWidth() - iconImage.getWidth()) / 2;
            int y = (image.getHeight() - iconImage.getHeight()) / 2;
            graphics.drawImage(iconImage, x, y, iconImage.getWidth(), iconImage.getHeight(), null);
            ImageIO.write(image, "png", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final File androidManifestDir = new File(Paths.get("C:\\Users\\Administrator\\Desktop\\assistantApp", "app", "src", "main").toUri());

        String androidManifestUrl = Paths.get(androidManifestDir.toString(),  "AndroidManifest.xml").toString();
        String androidManifest = FileUtil.readString(androidManifestUrl, StandardCharsets.UTF_8);
        String androidAuthorities = "com.caringsaas.kailing.dc.fileprovider";
        String newAndroidAuthorities = "com.caringsaas." + "aaaaaa" + ".dc.fileprovider";
        androidManifest = StrUtil.replace(androidManifest, androidAuthorities, newAndroidAuthorities);
        FileUtil.writeString(androidManifest, androidManifestUrl, StandardCharsets.UTF_8);

    }

    public void writeAppImage(String appLaunchImage, String appIcon, String appName) throws Exception {
        //安卓资源路径 app/src/main/res
        String resourcePath = Joiner.on(SystemUtil.getOsInfo().getFileSeparator()).join(workFolder, "app", "src", "main", "res");
        File drawableDir = new File(resourcePath, "drawable");
        if (!drawableDir.exists()) {
            drawableDir.mkdirs();
        }
        // 下载静态资源到改文件夹
        File launchFile = FileUtils.downLoadFromFile(appLaunchImage, "/launch", drawableDir.getPath());
        File iconFile = FileUtils.downLoadFromFile(appIcon, "/icon", drawableDir.getPath());
        // 默认使用icon作为通知图标
        File pushFile = new File(drawableDir.getPath(), "push.png");
        File splashBgFile = new File(drawableDir.getPath(), "splash_bg.png");
        File splashFile = new File(drawableDir.getPath(), "splash.9.png");
        File tempIcon = new File(drawableDir.getPath(), "temp_icon.png");
        BufferedImage iconImage = ImageIO.read(iconFile);
        ImageIO.write(iconImage, "png", pushFile);
        ImgUtil.scale(iconFile, tempIcon, 192, 192, null);

        drawImage(tempIcon, splashBgFile, splashFile);

        for (DrawableEnum drawableEnum : DrawableEnum.values()) {
            File dir = new File(resourcePath, drawableEnum.getFoldName());
            dir = FileUtil.mkdir(dir);
            File icon = new File(dir, "icon.png");
            File push = new File(dir, "push.png");
            ImgUtil.scale(iconFile, icon, drawableEnum.getIconWidth(), drawableEnum.getIconHeight(), null);
            ImgUtil.scale(pushFile, push, drawableEnum.getPushWidth(), drawableEnum.getPushHeight(), null);
        }

        File launch = new File(uniLaunchFolder, "launch.png");
        // 根据 splashFile 生成 launch 下的 三种尺寸的图片
//         launch480x762.png
        // launch720x1242.png
        // launch1080x1882.png
        File launch480x762 = new File(uniLaunchFolder, "launch480x762.png");
        File launch720x1242 = new File(uniLaunchFolder, "launch720x1242.png");
        File launch1080x1882 = new File(uniLaunchFolder, "launch1080x1882.png");

        ImgUtil.scale(launchFile, launch480x762, 480, 762, null);
        ImgUtil.scale(launchFile, launch720x1242, 720, 1242, null);
        ImgUtil.scale(launchFile, launch1080x1882, 1080, 1882, null);

        BufferedImage image = ImageIO.read(launchFile);
        ImageIO.write(image, "png", launch);

    }

    /**
     * 替换基座文件
     * 找到基座下的 launch 并记录目录
     *
     * @param workFolder
     */
    private static void replaceBaseFiles(String workFolder, String dcloudAppid, String tenantCode, String appVersionName, String domain) {
        // 替换文件assistantApp\app\src\main\assets\data\dcloud_control.xml中 默认的__UNI__DA034C0的值为 dcloudAppid
        File dcloudControlXmlPath = Paths.get(workFolder, "app", "src", "main", "assets", "data", "dcloud_control.xml").toFile();
        if (FileUtil.exist(dcloudControlXmlPath)) {
            String fileContent = FileUtil.readString(dcloudControlXmlPath, StandardCharsets.UTF_8);
            String replaceFileContent = StrUtil.replace(fileContent, ApplicationProperties.getBaseUniAppId(), dcloudAppid);
            FileUtil.writeString(replaceFileContent, dcloudControlXmlPath, StandardCharsets.UTF_8);
        }

        // 2. 替换文件assistantApp\app\src\main\assets\apps\__UNI__DA034C0\www\app-service.js中的MDExMg==值为相应的租户编码
        final File appServiceJsDir = new File(Paths.get(workFolder, "app", "src", "main", "assets", "apps").toUri());
        final File androidManifestDir = new File(Paths.get(workFolder, "app", "src", "main").toUri());

        if (!appServiceJsDir.exists()) {
            log.error("基座{}目录不存在", appServiceJsDir.getPath());
            return;
        }
        if (!androidManifestDir.exists()) {
            log.error("安卓配置文件{}目录不存在", androidManifestDir.getPath());
            return;
        }
        final String[] fileList = appServiceJsDir.list();
        if (fileList == null) {
            log.error("基座{}目录暂无文件", appServiceJsDir.getPath());
        }

        // 开始替换 AndroidManifest.xml 中的包名
        String androidManifestUrl = Paths.get(androidManifestDir.toString(),  "AndroidManifest.xml").toString();
        String androidManifest = FileUtil.readString(androidManifestUrl, StandardCharsets.UTF_8);
        String androidAuthorities = "com.caringsaas.kailing.dc.fileprovider";
        String newAndroidAuthorities = "com.caringsaas." + domain + ".dc.fileprovider";
        androidManifest = StrUtil.replace(androidManifest, androidAuthorities, newAndroidAuthorities);
        FileUtil.writeString(androidManifest, androidManifestUrl, StandardCharsets.UTF_8);
        // 替换结束

        String regexp = "^__UNI\\_\\S*";
        String appServiceJsFileUrl = "", manifestJson = "", uniLaunchFolderPath = "";
        for (String uniDir : fileList) {
            if (uniDir.matches(regexp)) {
                // 目录替换
                Path oldUniPath = Paths.get(workFolder, "app", "src", "main", "assets", "apps", uniDir);
                Path newUniPath = Paths.get(workFolder, "app", "src", "main", "assets", "apps", dcloudAppid);
                new File(oldUniPath.toUri()).renameTo(new File(newUniPath.toUri()));

                appServiceJsFileUrl = Paths.get(newUniPath.toString(), "www", "app-service.js").toString();
                manifestJson = Paths.get(newUniPath.toString(), "www", "manifest.json").toString();
                uniLaunchFolderPath = Paths.get(newUniPath.toString(), "www", "static", "launch").toString();
                break;
            }
        }
        String content = FileUtil.readString(appServiceJsFileUrl, StandardCharsets.UTF_8);
        content = StrUtil.replace(content, "MDExMg==", tenantCode);
        // 替换caringVersion 为真实的版本号
        content = StrUtil.replace(content, "caringVersion", appVersionName);
        FileUtil.writeString(content, appServiceJsFileUrl, StandardCharsets.UTF_8);
        FileUtil.writeString(StrUtil.replace(FileUtil.readString(manifestJson, StandardCharsets.UTF_8), ApplicationProperties.getBaseUniAppId(), dcloudAppid), manifestJson, StandardCharsets.UTF_8);
        if (StrUtil.isNotEmpty(uniLaunchFolderPath)) {
            uniLaunchFolder = new File(uniLaunchFolderPath);
            if (!uniLaunchFolder.exists()) {
                uniLaunchFolder.mkdirs();
            }
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
            cmdList.add("gradle assembleRelease");
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
            cmdList.add("gradle assembleRelease && echo '" + BUILD_SUCCESS_FLAG + "'");
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
        String child = "app/build/outputs/apk/release/app-release.apk";
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
