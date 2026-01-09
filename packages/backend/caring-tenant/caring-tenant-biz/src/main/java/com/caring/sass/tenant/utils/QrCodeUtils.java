package com.caring.sass.tenant.utils;

import com.caring.sass.base.R;
import com.caring.sass.common.enums.DoctorLoginImage;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.ImageUtils;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.utils.SpringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

/**
 * @ClassName 医生激活码的合成
 * @Description
 * @Author yangShuai
 * @Date 2020/10/12 16:05
 * @Version 1.0
 */

public class QrCodeUtils {

    private static String TITLE = "激活码";
    private static String TITLE_ENGLISH = " Activation Code";

    private static String DESC1 = "请将此二维码出示给";
    private static String DESC1_ENGLISH = "Please show this QR code to the ";

    private static String DESC2 = "扫码激活";
    private static String DESC2_ENDLISH = "Scan code to activate %s";

    public static void main(String[] args) {
        String qrCodePath = "https://caing-test.obs.cn-north-4.myhuaweicloud.com:443/0112%2F2021%2F06%2Fd3176159-636c-4a35-9bf6-fff479d2fa7e.jpg";
        String logo = "https://caing-test.obs.cn-north-4.myhuaweicloud.com:443/admin%2F2022%2F09%2Fa38ba767-73fa-4b5a-a075-bc30d00aa536.png";
        String projectName = "凯琳云护";
        String dictItemName = "doctor";
        englishTenantDoctorLoginCode(qrCodePath, projectName, logo, dictItemName);
    }

    public static String englishTenantDoctorLoginCode(String qrCodePath, String projectName, String projectLogo, String dictItemName) {
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + "/saas/tenantDoctorLogin";
        File image = DoctorLoginImage.getInstance().getDoctorLoginImage();
        if (image == null) {
            return qrCodePath;
        }
        String title = dictItemName + TITLE_ENGLISH;
        File file = null;
        try {
            String qrCodeLocalPath = FileUtils.downLoadFromUrl(qrCodePath, UUID.randomUUID().toString().replace("-", ""), path);

            String projectLogoImage = null;
            if (!StringUtils.isEmpty(projectLogo)) {
                projectLogoImage = FileUtils.downLoadFromUrl(projectLogo, UUID.randomUUID().toString().replace("-", ""), path);
            }

            int imageWidth = 1440;
            BufferedImage background = ImageUtils.resizeImage(imageWidth, 2280, ImageIO.read(image));
            File qrCodeFile = new File(qrCodeLocalPath);
            BufferedImage qrCode = ImageUtils.resizeImage(900, 900, ImageIO.read(qrCodeFile));

            Graphics2D g = background.createGraphics();
            g.setColor(Color.white);
            Font fontUserName = new Font("黑体", 1, 110);
            g.setFont(fontUserName);
            FontMetrics userNameMetrics = g.getFontMetrics(fontUserName);
            int userNameWidth = userNameMetrics.stringWidth(title);
            int nameY = 620;
            int nameX = (imageWidth - userNameWidth) / 2;
            g.drawString(title, nameX, nameY);

            g.drawImage(qrCode, 270, 960, qrCode.getWidth(), qrCode.getHeight(), null);
            qrCodeFile.delete();
            if (!StringUtils.isEmpty(projectName)) {
                int projectNameX = 220;
                int projectNameY = 165;
                g.setFont(new Font("黑体", 1, 65));
                g.drawString(projectName, projectNameX, projectNameY);
            }

            // 将logo画到背景上
            if (!StringUtils.isEmpty(projectLogoImage)) {
                File file1 = new File(projectLogoImage);
                if (file1.exists()) {
                    BufferedImage logo = ImageUtils.resizeImage(90, 90, ImageIO.read(file1));
                    g.drawImage(logo, 95, 95, logo.getWidth(), logo.getHeight(), null);
                    file1.delete();
                }
            }

            String desc1 = DESC1_ENGLISH + dictItemName;
            String desc2 = String.format(DESC2_ENDLISH, dictItemName);
            int orgNameY = 2000;
            Font orgNameFont = new Font("黑体", 1, 55);
            g.setColor(new Color(153,153,153));
            g.setFont(orgNameFont);
            FontMetrics orgNameMetrics = g.getFontMetrics(orgNameFont);
            int orgNameWidth = orgNameMetrics.stringWidth(desc1);
            int orgNameX = (imageWidth - orgNameWidth) / 2;
            g.drawString(desc1, orgNameX, orgNameY);

            orgNameY = 2070;
            orgNameWidth = orgNameMetrics.stringWidth(desc2);
            orgNameX = (imageWidth - orgNameWidth) / 2;
            g.drawString(desc2, orgNameX, orgNameY);
            g.dispose();

            String imgPath = path + "/" + UUID.randomUUID().toString().replace("-", "") + ".jpg";
            file = new File(imgPath);
            System.out.println(imgPath);
            ImageIO.write(background, "jpg", file);
            FileUploadApi fileUploadApi =  SpringUtils.getBean(FileUploadApi.class);
            MockMultipartFile mockMultipartFile = FileUtils.fileToFileItem(file);
            R<com.caring.sass.file.entity.File> fileR = fileUploadApi.upload(0l, mockMultipartFile);
            if (fileR.getIsSuccess()) {
                com.caring.sass.file.entity.File data = fileR.getData();
                return data.getUrl();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                file.delete();
            }
        }
        return null;
    }

    /**
     * 将 二维码和 图片背景合成。
     * @param qrCodePath 医生的激活码
     * @param projectName 项目的名称
     * @param projectLogo 项目的logo
     * @param dictItemName 字典的名字
     * @return
     */
    public static String tenantDoctorLoginCode(String qrCodePath, String projectName, String projectLogo, String dictItemName) {
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + "/saas/tenantDoctorLogin";
        File image = DoctorLoginImage.getInstance().getDoctorLoginImage();
        if (image == null) {
            return qrCodePath;
        }
        String title = dictItemName + TITLE;
        File file = null;
        try {
            String qrCodeLocalPath = FileUtils.downLoadFromUrl(qrCodePath, UUID.randomUUID().toString().replace("-", ""), path);

            String projectLogoImage = null;
            if (!StringUtils.isEmpty(projectLogo)) {
                projectLogoImage = FileUtils.downLoadFromUrl(projectLogo, UUID.randomUUID().toString().replace("-", ""), path);
            }

            int imageWidth = 1440;
            BufferedImage background = ImageUtils.resizeImage(imageWidth, 2280, ImageIO.read(image));
            File qrCodeFile = new File(qrCodeLocalPath);
            BufferedImage qrCode = ImageUtils.resizeImage(900, 900, ImageIO.read(qrCodeFile));

            Graphics2D g = background.createGraphics();
            g.setColor(Color.white);
            Font fontUserName = new Font("黑体", 1, 110);
            g.setFont(fontUserName);
            FontMetrics userNameMetrics = g.getFontMetrics(fontUserName);
            int userNameWidth = userNameMetrics.stringWidth(title);
            int nameY = 620;
            int nameX = (imageWidth - userNameWidth) / 2;
            g.drawString(title, nameX, nameY);

            g.drawImage(qrCode, 270, 960, qrCode.getWidth(), qrCode.getHeight(), null);
            qrCodeFile.delete();
            if (!StringUtils.isEmpty(projectName)) {
                int projectNameX = 220;
                int projectNameY = 165;
                g.setFont(new Font("黑体", 1, 65));
                g.drawString(projectName, projectNameX, projectNameY);
            }

            // 将logo画到背景上
            if (!StringUtils.isEmpty(projectLogoImage)) {
                File file1 = new File(projectLogoImage);
                if (file1.exists()) {
                    BufferedImage logo = ImageUtils.resizeImage(90, 90, ImageIO.read(file1));
                    g.drawImage(logo, 95, 95, logo.getWidth(), logo.getHeight(), null);
                    file1.delete();
                }
            }

            String desc1 = DESC1 + dictItemName;
            String desc2 = DESC2 + dictItemName + "端";
            int orgNameY = 2000;
            Font orgNameFont = new Font("黑体", 1, 55);
            g.setColor(new Color(153,153,153));
            g.setFont(orgNameFont);
            FontMetrics orgNameMetrics = g.getFontMetrics(orgNameFont);
            int orgNameWidth = orgNameMetrics.stringWidth(desc1);
            int orgNameX = (imageWidth - orgNameWidth) / 2;
            g.drawString(desc1, orgNameX, orgNameY);

            orgNameY = 2070;
            orgNameWidth = orgNameMetrics.stringWidth(desc2);
            orgNameX = (imageWidth - orgNameWidth) / 2;
            g.drawString(desc2, orgNameX, orgNameY);
            g.dispose();

            String imgPath = path + "/" + UUID.randomUUID().toString().replace("-", "") + ".jpg";
            file = new File(imgPath);
            System.out.println(imgPath);
            ImageIO.write(background, "jpg", file);
            FileUploadApi fileUploadApi =  SpringUtils.getBean(FileUploadApi.class);
            MockMultipartFile mockMultipartFile = FileUtils.fileToFileItem(file);
            R<com.caring.sass.file.entity.File> fileR = fileUploadApi.upload(0l, mockMultipartFile);
            if (fileR.getIsSuccess()) {
                com.caring.sass.file.entity.File data = fileR.getData();
                return data.getUrl();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                file.delete();
            }
        }
        return null;
    }


}
