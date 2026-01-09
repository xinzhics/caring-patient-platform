import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.ImageUtils;
import com.caring.sass.tenant.dto.DrawableEnum;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

@Slf4j
public class TestApkImg {
    private static File workFolder = new File("D:\\work");

    public static void main(String[] args) throws Exception {
        writeAppImage("https://caing-test.obs.cn-north-4.myhuaweicloud.com:443/admin%2F2022%2F11%2F46cf3e68-b688-4b36-bbbf-66df727d142e.png",
                "https://caing-test.obs.cn-north-4.myhuaweicloud.com:443/admin%2F2022%2F11%2F93b73c3e-a209-4702-9c55-3fd531c728a6.png",
                "卡柠医助");
    }

    public static void writeAppImage(String appLaunchImage, String appIcon, String appName) throws Exception {

        //安卓资源路径 app/src/main/res
        String resourcePath = Joiner.on(SystemUtil.getOsInfo().getFileSeparator()).join(workFolder, "app", "src", "main", "res");
        File drawableDir = new File(resourcePath, "drawable");
        if (!drawableDir.exists()) {
            drawableDir.mkdirs();
        }
        // 下载静态资源到改文件夹
        File splashFile = FileUtils.downLoadFromFile(appLaunchImage, "/splash", drawableDir.getPath());
        File iconFile = FileUtils.downLoadFromFile(appIcon, "/icon", drawableDir.getPath());
        // 默认使用icon作为通知图标
        File pushFile = FileUtils.downLoadFromFile(appIcon, "/push", drawableDir.getPath());

        // 生成适配不通屏幕尺寸的图标
        for (DrawableEnum drawableEnum : DrawableEnum.values()) {
            File dir = new File(resourcePath, drawableEnum.getFoldName());
            dir = FileUtil.mkdir(dir);
            File icon = new File(dir, "icon.png");
            File push = new File(dir, "push.png");
            File splash = new File(dir, "splash.png");
            ImgUtil.scale(iconFile, icon, drawableEnum.getIconWidth(), drawableEnum.getIconHeight(), null);
            ImgUtil.scale(pushFile, push, drawableEnum.getPushWidth(), drawableEnum.getPushHeight(), null);
            ImgUtil.scale(splashFile, splash, drawableEnum.getSplashWidth(), drawableEnum.getSplashHeight(), null);
        }

//
//        File launchFile = new File(workFolder, "app/src/main/res/drawable/icon.png");
//        File hdpi = new File(workFolder, "app/src/main/res/drawable-hdpi/icon.png");
//        File mdpiStandard = new File(workFolder, "app/src/main/res/drawable-ldpi/icon.png");
//        File mdpi = new File(workFolder, "app/src/main/res/drawable-mdpi/icon.png");
//        File xhdpi = new File(workFolder, "app/src/main/res/drawable-xhdpi/icon.png");
//        File xxhdpi = new File(workFolder, "app/src/main/res/drawable-xxhdpi/icon.png");
//        File xxxhdpi = new File(workFolder, "app/src/main/res/drawable-xxxhdpi/icon.png");
//        File icon = new File(workFolder, "icon.png");
//        if (launchFile.exists()) {
//            launchFile.delete();
//        }
//        FileUtil.mkdir(new File("D:\\work\\app\\src\\main\\res"));
//
//        // ImageUtils.resizeImage(appLaunchImage, launchFile, workFolder, 720, 1080);  不压缩启动图
//        FileUtils.downLoadFromFile(appLaunchImage, "/launch", new File(workFolder, "app/src/main/res/drawable/").getAbsolutePath());
//        if (icon.exists()) {
//            icon.delete();
//        }
//
//        ImageUtils.resizeImage(appIcon, mdpi, workFolder, 48, 48);
//        ImageUtils.resizeImage(appIcon, hdpi, workFolder, 72, 72);
//        ImageUtils.resizeImage(appIcon, xhdpi, workFolder, 96, 96);
//        ImageUtils.resizeImage(appIcon, xxhdpi, workFolder, 144, 144);
//        ImageUtils.resizeImage(appIcon, xxxhdpi, workFolder, 192, 192);
//        ImageUtils.resizeImage(appIcon, mdpiStandard, workFolder, 192, 192);
////        boolean defaultLaunchImage = Objects.equals(appLaunchImage, ApplicationProperties.getApkLaunchImage());
//        if (true) {
//            mergeLaunchImg(launchFile, xxxhdpi, appName);
//        }
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


}
