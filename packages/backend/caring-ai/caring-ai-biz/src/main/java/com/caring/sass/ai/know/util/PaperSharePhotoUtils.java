package com.caring.sass.ai.know.util;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.ImageUtils;
import com.google.zxing.BarcodeFormat;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PaperSharePhotoUtils {

    public static final String imagePath = File.separator + "saas" + File.separator + " docuKnowsSharePhoto" + File.separator;

    public static void main(String[] args) {
        String wechatAppId = System.getenv().getOrDefault("WECHAT_APP_ID", "");
        String wechatComponentAppId = System.getenv().getOrDefault("WECHAT_COMPONENT_APP_ID", "");
        String domain = System.getenv().getOrDefault("DOMAIN", "domain");
        String redirectSaasUrl = System.getenv().getOrDefault("REDIRECT_SAAS_URL", "http://docuknowsmobile.domain");
        
        String url = String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=http%%3A%%2F%%2Fdomain%%2Fapi%%2Fwx%%2FwxUserAuth%%2Fanno%%2FthirdRedirectCodeOpenId%%3FwxAppId%%3D%s%%26redirectSaasUrl%%3D%s%%3Fdomain%%3D%s&response_type=code&scope=snsapi_base&state=1&component_appid=%s#wechat_redirect", 
            wechatAppId, wechatAppId, redirectSaasUrl, domain, wechatComponentAppId);

        try {
            // 生成二维码
            BufferedImage bufferedImage = generateQRCode(url);

            // 将二维码绘制到另一张图片上
            String string = docuKnowsSharePhoto(bufferedImage, "祝戎飞",
                    "https://caring.obs.cn-north-4.myhuaweicloud.com/business_card/zhurongfei_avatar.png",
                    "https://yangshuai.domain");
            System.out.println(string);
            System.out.println("二维码已生成并绘制到图片上！:" + string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 合成图片
     * @param url
     * @param doctorName
     * @param doctorAvatar
     * @param webUrl
     * @return
     */
    public static String mergePhoto(String url, String doctorName, String doctorAvatar, String webUrl) {
        // 生成二维码
        BufferedImage bufferedImage = generateQRCode(url);

        return docuKnowsSharePhoto(bufferedImage, doctorName, doctorAvatar, webUrl);

    }


    /**
     * 生成二维码
     * @param url
     * @throws IOException
     */
    private static BufferedImage generateQRCode(String url) {

        QrConfig config = new QrConfig();
        config.setBackColor(null);
        config.setWidth(260);
        config.setHeight(260);
        config.setMargin(4);
        BarcodeFormat barcodeFormat = BarcodeFormat.QR_CODE;
        BufferedImage bufferedImage = QrCodeUtil.generate(url, barcodeFormat, config);


        // 创建一个新的 BufferedImage 来保存裁剪后的圆形图片
        int size = bufferedImage.getWidth();
        BufferedImage roundedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        // 获取 Graphics2D 对象
        Graphics2D g2d = roundedImage.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 创建一个圆角矩形的剪切区域
        int cornerRadius = 28; // 圆角半径
        RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(0, 0, size, size, cornerRadius, cornerRadius);
        g2d.setClip(roundRect);

        // 绘制原图到圆角矩形区域内
        g2d.drawImage(bufferedImage, 0, 0, size, size, null);

        // 释放资源
        g2d.dispose();

        return roundedImage;
    }

    /**
     * 将图片从方的 剪成圆的
     * @param file1
     * @return
     * @throws IOException
     */
    private static BufferedImage handleAvatar(File file1, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(file1);
        BufferedImage image = ImageUtils.resizeImageTransparent(width, height, originalImage);

        // 创建一个新的 BufferedImage 来保存裁剪后的圆形图片
        int size = Math.min(image.getWidth(), image.getHeight());
        BufferedImage circleImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        // 获取 Graphics2D 对象
        Graphics2D g2d = circleImage.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 创建一个圆形的剪切区域
        g2d.setClip(new Ellipse2D.Float(0, 0, size, size));


        // 绘制原图到圆形区域内
        g2d.drawImage(image, 0, 0, size, size, null);

        // 释放资源
        g2d.dispose();

        return circleImage;
    }

    /**
     * 将二维码。 医生头像 医生姓名，知识库网址。 画到背景图上。
     * @param bufferedImage 二维码
     * @param doctorName 医生名称
     * @param doctorAvatar 医生的头像
     * @param webUrl PC网站地址
     * @return
     */
    private static String docuKnowsSharePhoto(BufferedImage bufferedImage, String doctorName, String doctorAvatar, String webUrl) {
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + imagePath;
        File saveDir = new File(path);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File image = DocuKnowImage.getInstance().getDocuKnowImage();
        if (image == null) {
            return null;
        }
        File file = null;
        try {

            int imageWidth = 1200;
            BufferedImage background = ImageUtils.resizeImage(imageWidth, 1846, ImageIO.read(image));
            Graphics2D g = background.createGraphics();

            // 把 医生的名字写到背景图上
            g.setColor(new Color(0, 101,204));
            Font fontDoctorName = new Font("Alimama ShuHeiTi:style=Bold", Font.BOLD, 152);
            g.setFont(fontDoctorName);
            g.drawString(doctorName, 322, 324);

            // 将二维码合成到背景上
            g.drawImage(bufferedImage, 892, 1511, 235, 235, null);


            if (!StringUtils.isEmpty(doctorAvatar)) {
                String doctorAvatarFile = FileUtils.downLoadFromUrl(doctorAvatar, UUID.randomUUID().toString().replace("-", ""), path);
                // 将头像画到背景上
                if (!StringUtils.isEmpty(doctorAvatarFile)) {
                    File file1 = new File(doctorAvatarFile);
                    // 将头像裁剪成圆形
                    BufferedImage logo= handleAvatar(file1, 169, 169);
                    g.drawImage(logo, 125, 183, logo.getWidth(), logo.getHeight(), null);
                    file1.delete();
                }
            }

            // 把网址写到背景图上
            Font webUrlFont = new Font("Alibaba PuHuiTi", Font.PLAIN, 32);
            g.setColor(new Color(0, 101,204));
            g.setFont(webUrlFont);
            g.drawString(webUrl, 181, 1702);
            g.dispose();

            String imgPath = path + File.separator + UUID.randomUUID().toString().replace("-", "") + ".png";
            file = new File(imgPath);
            ImageIO.write(background, "png", file);
            return imgPath;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
