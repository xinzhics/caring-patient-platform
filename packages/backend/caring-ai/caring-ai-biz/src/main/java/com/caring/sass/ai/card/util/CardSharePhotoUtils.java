package com.caring.sass.ai.card.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.caring.sass.ai.know.util.DocuKnowImage;
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

public class CardSharePhotoUtils {


    public static final String imagePath = "saas" + File.separator + "cardShareCode" + File.separator;

    public static void main(String[] args) {
        String fileUrl = "https://caing-test.obs.cn-north-4.myhuaweicloud.com/0000%2F2024%2F11%2Fa075ca53-44a2-4dba-8f42-50cf5e065739.png";
        String mergedPhoto = mergePhoto(fileUrl, "祝戎飞", "https://caring.obs.cn-north-4.myhuaweicloud.com/business_card/zhurongfei_avatar.png",
                "中南大学湘雅附属第一医院", "过敏反应科", "主任医师");
        System.out.println(mergedPhoto);

    }


    /**
     * 合成图片
     */
    public static String mergePhoto(String fileUrl, String doctorName, String doctorAvatar, String hospitalName, String departmentName, String titleName) {
        // 生成二维码
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + imagePath;
        File qrcode;
        try {
            qrcode = FileUtils.downLoadFromFile(fileUrl, UUID.randomUUID().toString().replace("-", ""), path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedImage qrCodeBufferedImage;
        try {
            qrCodeBufferedImage = ImageIO.read(qrcode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String string = cardSharePhoto(qrCodeBufferedImage, doctorName, doctorAvatar, hospitalName, departmentName, titleName);
        if (qrcode.exists()) {
            qrcode.delete();
        }
        return string;

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
     * @return
     */
    private static String cardSharePhoto(BufferedImage bufferedImage, String doctorName, String doctorAvatar, String hospitalName, String departmentName, String titleName) {
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + imagePath;
        File saveDir = new File(path);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File image = CardShareBgImage.getInstance().getCardShareBgImage();
        if (image == null) {
            return null;
        }
        File file = null;
        try {
            String font = "Source Han Sans SC,Source Han Sans SC ExtraLight:style=ExtraLight,Regular";
            int imageWidth = 452;
            int n = 4;
            BufferedImage background = ImageUtils.resizeImageTransparent(imageWidth * n, 615 * n, ImageIO.read(image));


            Graphics2D g = background.createGraphics();

            // 确保透明通道被正确处理
            g.setComposite(AlphaComposite.SrcOver);

            // 科室
            g.setColor(new Color(30, 52,70));
            Font sourceFont = new Font(font, Font.PLAIN, 18 * n);
            g.setFont(sourceFont);
            g.drawString(departmentName, 40  * n, 92 * n);

            // 把 医生的名字写到背景图上
            g.setColor(new Color(29, 46,56));
            sourceFont = new Font("Source Han Sans SC,Source Han Sans SC Medium:style=Medium", Font.BOLD, 44 * n);
            g.setFont(sourceFont);
            g.drawString(doctorName, 40 * n, 148 * n);

            // 医院 + 职称
            String text = "";
            if (StrUtil.isNotBlank(hospitalName)) {
                text += hospitalName;
                if (StrUtil.isNotBlank(titleName)) {
                    text += " " + titleName;
                }
            } else if (StrUtil.isNotBlank(titleName)) {
                text += titleName;
            }
            g.setColor(new Color(105, 105,105));
            sourceFont = new Font(font, Font.PLAIN, 12 * n);
            g.setFont(sourceFont);

            if (text.length() > 17) {
                g.drawString(text.substring(0,16), 40 * n, 183 * n);
                g.drawString(text.substring(16), 40 * n, 208 * n);
            } else {
                g.drawString(text, 40 * n, 183 * n);
            }


            // 将二维码合成到背景上
            g.drawImage(bufferedImage, 140 * n, 288 * n, 172 * n, 172 * n, null);

            if (!StringUtils.isEmpty(doctorAvatar)) {
                String doctorAvatarFile = FileUtils.downLoadFromUrl(doctorAvatar, UUID.randomUUID().toString().replace("-", ""), path);
                // 将头像画到背景上
                if (!StringUtils.isEmpty(doctorAvatarFile)) {
                    File file1 = new File(doctorAvatarFile);
                    // 将头像裁剪成圆形
                    BufferedImage logo = handleAvatar(file1, 111 * n, 111 * n);
                    g.drawImage(logo, 301 * n, 70 * n, logo.getWidth(), logo.getHeight(), null);
                    file1.delete();
                }
            }

            g.dispose();

            String imgPath = path + UUID.randomUUID().toString().replace("-", "") + ".png";
            file = new File(imgPath);
            System.out.println(imgPath);
            ImageIO.write(background, "png", file);
            return imgPath;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
