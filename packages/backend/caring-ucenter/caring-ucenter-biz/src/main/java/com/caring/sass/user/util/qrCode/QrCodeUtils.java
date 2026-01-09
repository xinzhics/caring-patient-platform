package com.caring.sass.user.util.qrCode;

import com.caring.sass.base.R;
import com.caring.sass.common.enums.PatientSubscribeImage;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.ImageUtils;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.utils.SpringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

/**
 * @ClassName QCodeUtils
 * @Description
 * @Author yangShuai
 * @Date 2020/10/12 16:05
 * @Version 1.0
 */

public class QrCodeUtils {

    static String doctorAvator = "https://caring.obs.cn-north-4.myhuaweicloud.com/patientSubscribe/doctor_avator.png";
    static String doctor_default_avator = "https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/doctor_default_avator.png";

    static String fontName = "Noto Sans CJK";

    public static void main(String[] args) {
        String qrCodePath = "https://caing-test.obs.cn-north-4.myhuaweicloud.com:443/0112%2F2022%2F04%2F66e07ed9-8fb0-46c2-9e84-c58a6c88faad.jpg";
        String avatar = "https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/doctor_default_avator.png";
        String username = "霍测萍";
        String projectName = "宜宾市第二人民医院介入医学中心";
        String hospitalName = "华中科技大学同济医学院附属协和医院（西区）";
        String projectLogo = "https://caing-test.obs.cn-north-4.myhuaweicloud.com:443/admin%2F2022%2F09%2Fa38ba767-73fa-4b5a-a075-bc30d00aa536.png";
        String doctorTitle = "主任医生";
//        patientSubscribeCode(qrCodePath, avatar, username, projectName, hospitalName, projectLogo);
        patientSubscribeCode_3(qrCodePath, avatar, username, projectName, hospitalName, doctorTitle);

    }

    public static String patientSubscribeCode_3(String qrCodePath, String avatar, String username, String projectName, String hospitalName, String doctorTitle) {
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + "/saas/businessCardQrCode";
        File image = PatientSubscribeImage.getInstance().getPatientSubscribeImage3();
        if (image == null) {
            return qrCodePath;
        }
        if (StringUtils.isEmpty(avatar) || Doctor.defaultAvtar.equals(avatar) || avatar.equals(doctor_default_avator)) {
            avatar = doctorAvator;
        }
        File file = null;
        try {
            String qrCodeLocalPath = FileUtils.downLoadFromUrl(qrCodePath, UUID.randomUUID().toString().replace("-", ""), path);

            String avatarImg = null;
            if (!StringUtils.isEmpty(avatar)) {
                avatarImg = FileUtils.downLoadFromUrl(avatar, UUID.randomUUID().toString().replace("-", ""), path);
            }
            int imageWidth = 1575;
            BufferedImage background = ImageUtils.resizeImage(imageWidth, 2100, ImageIO.read(image));
            File qrCodeFile = new File(qrCodeLocalPath);
            BufferedImage qrCode = ImageUtils.resizeImage(347, 347, ImageIO.read(qrCodeFile));

            Graphics2D g = background.createGraphics();
            Color color = new Color(46, 107, 215);
            g.setColor(color);
            Font fontUserName = new Font(fontName, Font.BOLD, 104);
            g.setFont(fontUserName);
            FontMetrics userNameMetrics = g.getFontMetrics(fontUserName);
            int userNameWidth = userNameMetrics.stringWidth(username);
            int nameY = 1285;
            int nameX = (imageWidth - userNameWidth) / 2;
            g.drawString(username, nameX, nameY);

            g.drawImage(qrCode, 319, 1481, qrCode.getWidth(), qrCode.getHeight(), null);
            qrCodeFile.delete();
            // 绘制 项目名称
            if (!StringUtils.isEmpty(projectName)) {

                if (projectName.length() <= 8) {
                    Font projecFont = new Font("Alimama ShuHeiTi:style=Bold", Font.BOLD, 131);
                    FontMetrics projectMetrics = g.getFontMetrics(projecFont);
                    int projectNameWidth = projectMetrics.stringWidth(projectName);

                    int projectNameX = (imageWidth - projectNameWidth) / 2;
                    int projectNameY = 270;
                    g.setFont(projecFont);
                    g.setColor(Color.white);
                    g.drawString(projectName, projectNameX, projectNameY);
                } else if (projectName.length() <= 15) {
                    Font projecFont = new Font("Alimama ShuHeiTi:style=Bold", Font.BOLD, 85);
                    FontMetrics projectMetrics = g.getFontMetrics(projecFont);
                    int projectNameWidth = projectMetrics.stringWidth(projectName);

                    int projectNameX = (imageWidth - projectNameWidth) / 2;
                    int projectNameY = 270;
                    g.setFont(projecFont);
                    g.setColor(Color.white);
                    g.drawString(projectName, projectNameX, projectNameY);
                } else {
                    Font projecFont = new Font("Alimama ShuHeiTi:style=Bold", Font.BOLD, 60);
                    FontMetrics projectMetrics = g.getFontMetrics(projecFont);
                    int projectNameWidth = projectMetrics.stringWidth(projectName);

                    int projectNameX = (imageWidth - projectNameWidth) / 2;
                    int projectNameY = 270;
                    g.setFont(projecFont);
                    g.setColor(Color.white);
                    g.drawString(projectName, projectNameX, projectNameY);
                }

            }

            StringBuilder hospitalNameAndTitle = new StringBuilder();
            if (!StringUtils.isEmpty(hospitalName) && !StringUtils.isEmpty(doctorTitle)) {
                hospitalNameAndTitle.append(hospitalName);
                hospitalNameAndTitle.append(" ").append(doctorTitle);
            } else if (!StringUtils.isEmpty(hospitalName)) {
                hospitalNameAndTitle.append(hospitalName);
            } else if (!StringUtils.isEmpty(doctorTitle)) {
                hospitalNameAndTitle.append(doctorTitle);
            }
            String hospitalNameAndTitleString = hospitalNameAndTitle.toString();
            if (!StringUtils.isEmpty(hospitalNameAndTitleString)) {
                g.setColor(color);
                if (hospitalNameAndTitleString.length() <= 21) {
                    int orgNameY = 1365;
                    Font orgNameFont = new Font(fontName, Font.BOLD, 50);
                    g.setFont(orgNameFont);
                    FontMetrics orgNameMetrics = g.getFontMetrics(orgNameFont);
                    int orgNameWidth = orgNameMetrics.stringWidth(hospitalNameAndTitleString);
                    int orgNameX = (imageWidth - orgNameWidth) / 2;
                    g.drawString(hospitalNameAndTitleString, orgNameX, orgNameY);
                } else {
                    // 如果医院名称在21个字以内，则直接医院一行。职称一行
                    if (StringUtils.isEmpty(hospitalName) || hospitalName.length() <= 21) {
                        int orgNameY = 1360;
                        Font orgNameFont = new Font(fontName, Font.BOLD, 45);
                        FontMetrics orgNameMetrics = g.getFontMetrics(orgNameFont);
                        if (!StringUtils.isEmpty(hospitalName)) {
                            g.setFont(orgNameFont);
                            g.setColor(color);
                            int orgNameWidth = orgNameMetrics.stringWidth(hospitalName);
                            int orgNameX = (imageWidth - orgNameWidth) / 2;
                            g.drawString(hospitalName, orgNameX, orgNameY);
                            orgNameY += 50;
                        }
                        if (!StringUtils.isEmpty(doctorTitle)) {
                            orgNameFont = new Font(fontName, Font.BOLD, 45);
                            g.setFont(orgNameFont);
                            g.setColor(color);
                            orgNameMetrics = g.getFontMetrics(orgNameFont);
                            int orgNameWidth = orgNameMetrics.stringWidth(doctorTitle);
                            int orgNameX = (imageWidth - orgNameWidth) / 2;
                            g.drawString(doctorTitle, orgNameX, orgNameY);
                        }
                    } else {
                        // 如果医院名称在21个字以外，则将医院和职称名称拆分成两行
                        String hospitalName1 = hospitalNameAndTitleString.substring(0, 21);
                        int orgNameY = 1360;
                        Font orgNameFont = new Font(fontName, Font.BOLD, 45);
                        g.setFont(orgNameFont);
                        g.setColor(color);
                        FontMetrics orgNameMetrics = g.getFontMetrics(orgNameFont);
                        int orgNameWidth = orgNameMetrics.stringWidth(hospitalName1);
                        int orgNameX = (imageWidth - orgNameWidth) / 2;
                        g.drawString(hospitalName1, orgNameX, orgNameY);

                        String hospitalName2 = hospitalNameAndTitleString.substring(21);
                        orgNameY = 1410;
                        orgNameFont = new Font(fontName, Font.BOLD, 45);
                        g.setFont(orgNameFont);
                        g.setColor(color);
                        orgNameMetrics = g.getFontMetrics(orgNameFont);
                        orgNameWidth = orgNameMetrics.stringWidth(hospitalName2);
                        orgNameX = (imageWidth - orgNameWidth) / 2;
                        g.drawString(hospitalName2, orgNameX, orgNameY);
                    }
                }
            }

            if (!StringUtils.isEmpty(avatarImg)) {
                File avatarImgFile = new File(avatarImg);
                if (avatarImgFile.exists()) {
                    BufferedImage bufferedImage = ImageUtils.transferImgForRoundImage(ImageIO.read(avatarImgFile));
                    BufferedImage doctorAvatar = ImageUtils.resizeImageTransparent(532, 532, bufferedImage);
                    g.drawImage(doctorAvatar, 514, 483, doctorAvatar.getWidth(), doctorAvatar.getHeight(), null);
                    avatarImgFile.delete();
                }
            }

            // Health Science Platform
            String HealthSciencePlatform = "Health Science Platform";
            Font projecFont1 = new Font("Alimama ShuHeiTi", Font.PLAIN, 45);
            FontMetrics projectMetrics1 = g.getFontMetrics(projecFont1);
            int projectNameWidth = projectMetrics1.stringWidth(HealthSciencePlatform);
            int projectNameX = (imageWidth - projectNameWidth) / 2;
            int projectNameY = 380;
            g.setFont(projecFont1);
            g.setColor(new Color(57, 96, 245));
            g.drawString(HealthSciencePlatform, projectNameX, projectNameY);

            // Protect your health, we accompany you
            String protectYourHealth = "Protect your health, we accompany you";
            Font protectYourHealth1 = new Font("Alimama ShuHeiTi", Font.PLAIN, 38);
            FontMetrics protectYourHealthMetrics1 = g.getFontMetrics(protectYourHealth1);
            int protectYourHealthWidth = protectYourHealthMetrics1.stringWidth(protectYourHealth);
            int protectYourHealthWidthX = (imageWidth - protectYourHealthWidth) / 2;
            int protectYourHealthWidthY = 1948;
            g.setFont(protectYourHealth1);
            g.setColor(new Color(46, 107, 215));
            g.drawString(protectYourHealth, protectYourHealthWidthX, protectYourHealthWidthY);

            // Use WeChat Scan
            String useWeChatScan = "Use WeChat Scan";
            Font useWeChatScanFont = new Font("Alimama ShuHeiTi", Font.BOLD, 55);
            g.setFont(useWeChatScanFont);
            g.setColor(Color.white);
            g.drawString(useWeChatScan, 760, 1481 + 70);

            // Follow the official account to join
            String followOfficialAccount = "Follow the official";
            g.setFont(useWeChatScanFont);
            g.setColor(Color.white);
            g.drawString(followOfficialAccount, 760, 1481 + 150);

            //  account to join
            String accountJoin = "account to join";
            g.setFont(useWeChatScanFont);
            g.setColor(Color.white);
            g.drawString(accountJoin, 760, 1481 + 230);

            String RememberCompleteTheRegistration = "Remember to complete the registration";
            Font RememberCompleteTheRegistrationFont = new Font("Alimama ShuHeiTi", Font.PLAIN, 30);
            g.setFont(RememberCompleteTheRegistrationFont);
            g.setColor(Color.white);
            g.drawString(RememberCompleteTheRegistration, 760, 1481 + 300);

            g.dispose();

            String imgPath = path + "/" + UUID.randomUUID().toString().replace("-", "") + ".jpg";
            System.out.println(imgPath);
            file = new File(imgPath);
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
     * @Author yangShuai
     * @Description 将 二维码和 图片背景合成。
     * @Date 2020/10/12 16:13
     *
     * @return java.lang.String
     */
    public static String patientSubscribeCode_2(String qrCodePath, String avatar, String username, String projectName, String hospitalName, String doctorTitle) {
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + "/saas/businessCardQrCode";
        File image = PatientSubscribeImage.getInstance().getPatientSubscribeImage2();
        if (image == null) {
            return qrCodePath;
        }
        if (StringUtils.isEmpty(avatar) || Doctor.defaultAvtar.equals(avatar) || avatar.equals(doctor_default_avator)) {
            avatar = doctorAvator;
        }
        File file = null;
        try {
            String qrCodeLocalPath = FileUtils.downLoadFromUrl(qrCodePath, UUID.randomUUID().toString().replace("-", ""), path);

            String avatarImg = null;
            if (!StringUtils.isEmpty(avatar)) {
                avatarImg = FileUtils.downLoadFromUrl(avatar, UUID.randomUUID().toString().replace("-", ""), path);
            }
            int imageWidth = 1575;
            BufferedImage background = ImageUtils.resizeImage(imageWidth, 2100, ImageIO.read(image));
            File qrCodeFile = new File(qrCodeLocalPath);
            BufferedImage qrCode = ImageUtils.resizeImage(347, 347, ImageIO.read(qrCodeFile));

            Graphics2D g = background.createGraphics();
            Color color = new Color(46, 107, 215);
            g.setColor(color);
            Font fontUserName = new Font(fontName, Font.BOLD, 104);
            g.setFont(fontUserName);
            FontMetrics userNameMetrics = g.getFontMetrics(fontUserName);
            int userNameWidth = userNameMetrics.stringWidth(username);
            int nameY = 1285;
            int nameX = (imageWidth - userNameWidth) / 2;
            g.drawString(username, nameX, nameY);

            g.drawImage(qrCode, 319, 1481, qrCode.getWidth(), qrCode.getHeight(), null);
            qrCodeFile.delete();
            // 绘制 项目名称
            if (!StringUtils.isEmpty(projectName)) {

                if (projectName.length() <= 8) {
                    Font projecFont = new Font("Alimama ShuHeiTi:style=Bold", Font.BOLD, 131);
                    FontMetrics projectMetrics = g.getFontMetrics(projecFont);
                    int projectNameWidth = projectMetrics.stringWidth(projectName);

                    int projectNameX = (imageWidth - projectNameWidth) / 2;
                    int projectNameY = 270;
                    g.setFont(projecFont);
                    g.setColor(Color.white);
                    g.drawString(projectName, projectNameX, projectNameY);
                } else if (projectName.length() <= 15) {
                    Font projecFont = new Font("Alimama ShuHeiTi:style=Bold", Font.BOLD, 85);
                    FontMetrics projectMetrics = g.getFontMetrics(projecFont);
                    int projectNameWidth = projectMetrics.stringWidth(projectName);

                    int projectNameX = (imageWidth - projectNameWidth) / 2;
                    int projectNameY = 270;
                    g.setFont(projecFont);
                    g.setColor(Color.white);
                    g.drawString(projectName, projectNameX, projectNameY);
                } else {
                    Font projecFont = new Font("Alimama ShuHeiTi:style=Bold", Font.BOLD, 60);
                    FontMetrics projectMetrics = g.getFontMetrics(projecFont);
                    int projectNameWidth = projectMetrics.stringWidth(projectName);

                    int projectNameX = (imageWidth - projectNameWidth) / 2;
                    int projectNameY = 270;
                    g.setFont(projecFont);
                    g.setColor(Color.white);
                    g.drawString(projectName, projectNameX, projectNameY);
                }

            }


            StringBuilder hospitalNameAndTitle = new StringBuilder();
            if (!StringUtils.isEmpty(hospitalName) && !StringUtils.isEmpty(doctorTitle)) {
                hospitalNameAndTitle.append(hospitalName);
                hospitalNameAndTitle.append(" ").append(doctorTitle);
            } else if (!StringUtils.isEmpty(hospitalName)) {
                hospitalNameAndTitle.append(hospitalName);
            } else if (!StringUtils.isEmpty(doctorTitle)) {
                hospitalNameAndTitle.append(doctorTitle);
            }
            String hospitalNameAndTitleString = hospitalNameAndTitle.toString();
            if (!StringUtils.isEmpty(hospitalNameAndTitleString)) {
                g.setColor(color);
                if (hospitalNameAndTitleString.length() <= 21) {
                    int orgNameY = 1365;
                    Font orgNameFont = new Font(fontName, Font.BOLD, 50);
                    g.setFont(orgNameFont);
                    FontMetrics orgNameMetrics = g.getFontMetrics(orgNameFont);
                    int orgNameWidth = orgNameMetrics.stringWidth(hospitalNameAndTitleString);
                    int orgNameX = (imageWidth - orgNameWidth) / 2;
                    g.drawString(hospitalNameAndTitleString, orgNameX, orgNameY);
                } else {
                    // 如果医院名称在21个字以内，则直接医院一行。职称一行
                    if (StringUtils.isEmpty(hospitalName) || hospitalName.length() <= 21) {
                        int orgNameY = 1360;
                        Font orgNameFont = new Font(fontName, Font.BOLD, 45);
                        FontMetrics orgNameMetrics = g.getFontMetrics(orgNameFont);
                        if (!StringUtils.isEmpty(hospitalName)) {
                            g.setFont(orgNameFont);
                            g.setColor(color);
                            int orgNameWidth = orgNameMetrics.stringWidth(hospitalName);
                            int orgNameX = (imageWidth - orgNameWidth) / 2;
                            g.drawString(hospitalName, orgNameX, orgNameY);
                            orgNameY += 50;
                        }
                        if (!StringUtils.isEmpty(doctorTitle)) {
                            orgNameFont = new Font(fontName, Font.BOLD, 45);
                            g.setFont(orgNameFont);
                            g.setColor(color);
                            orgNameMetrics = g.getFontMetrics(orgNameFont);
                            int orgNameWidth = orgNameMetrics.stringWidth(doctorTitle);
                            int orgNameX = (imageWidth - orgNameWidth) / 2;
                            g.drawString(doctorTitle, orgNameX, orgNameY);
                        }
                    } else {
                        // 如果医院名称在21个字以外，则将医院和职称名称拆分成两行
                        String hospitalName1 = hospitalNameAndTitleString.substring(0, 21);
                        int orgNameY = 1360;
                        Font orgNameFont = new Font(fontName, Font.BOLD, 45);
                        g.setFont(orgNameFont);
                        g.setColor(color);
                        FontMetrics orgNameMetrics = g.getFontMetrics(orgNameFont);
                        int orgNameWidth = orgNameMetrics.stringWidth(hospitalName1);
                        int orgNameX = (imageWidth - orgNameWidth) / 2;
                        g.drawString(hospitalName1, orgNameX, orgNameY);

                        String hospitalName2 = hospitalNameAndTitleString.substring(21);
                        orgNameY = 1410;
                        orgNameFont = new Font(fontName, Font.BOLD, 45);
                        g.setFont(orgNameFont);
                        g.setColor(color);
                        orgNameMetrics = g.getFontMetrics(orgNameFont);
                        orgNameWidth = orgNameMetrics.stringWidth(hospitalName2);
                        orgNameX = (imageWidth - orgNameWidth) / 2;
                        g.drawString(hospitalName2, orgNameX, orgNameY);
                    }
                }
            }

            if (!StringUtils.isEmpty(avatarImg)) {
                File avatarImgFile = new File(avatarImg);
                if (avatarImgFile.exists()) {
                    BufferedImage bufferedImage = ImageUtils.transferImgForRoundImage(ImageIO.read(avatarImgFile));
                    BufferedImage doctorAvatar = ImageUtils.resizeImageTransparent(532, 532, bufferedImage);
                    g.drawImage(doctorAvatar, 514, 483, doctorAvatar.getWidth(), doctorAvatar.getHeight(), null);
                    avatarImgFile.delete();
                }
            }
            g.dispose();

            String imgPath = path + "/" + UUID.randomUUID().toString().replace("-", "") + ".jpg";
            System.out.println(imgPath);
            file = new File(imgPath);
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
     * @Author yangShuai
     * @Description 将 二维码和 图片背景合成。 TODO： 做集群时，此服务背景图片路径需要进行优化
     * @Date 2020/10/12 16:13
     *
     * @return java.lang.String
     */
    public static String patientSubscribeCode(String qrCodePath, String avatar, String username, String projectName, String hospitalName, String projectLogo) {
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + "/saas/businessCardQrCode";
        File image = PatientSubscribeImage.getInstance().getPatientSubscribeImage();
        if (image == null) {
            return qrCodePath;
        }
        File file = null;
        try {
            String qrCodeLocalPath = FileUtils.downLoadFromUrl(qrCodePath, UUID.randomUUID().toString().replace("-", ""), path);

            String projectLogoImage = null;
            if (!StringUtils.isEmpty(projectLogo)) {
                projectLogoImage = FileUtils.downLoadFromUrl(projectLogo, UUID.randomUUID().toString().replace("-", ""), path);
            }
            String avatarImg = null;
            if (!StringUtils.isEmpty(avatar)) {
                avatarImg = FileUtils.downLoadFromUrl(avatar, UUID.randomUUID().toString().replace("-", ""), path);
            }
            int imageWidth = 1440;
            BufferedImage background = ImageUtils.resizeImage(imageWidth, 2280, ImageIO.read(image));
            File qrCodeFile = new File(qrCodeLocalPath);
            BufferedImage qrCode = ImageUtils.resizeImage(900, 900, ImageIO.read(qrCodeFile));

            Graphics2D g = background.createGraphics();
            g.setColor(Color.white);
            Font fontUserName = new Font("黑体", 1, 90);
            g.setFont(fontUserName);
            FontMetrics userNameMetrics = g.getFontMetrics(fontUserName);
            int userNameWidth = userNameMetrics.stringWidth(username);
            int nameY = 620;
            int nameX = (imageWidth - userNameWidth) / 2;
            g.drawString(username, nameX, nameY);

            g.drawImage(qrCode, 270, 960, qrCode.getWidth(), qrCode.getHeight(), null);
            qrCodeFile.delete();
            if (!StringUtils.isEmpty(projectName)) {
                int projectNameX = 220;
                int projectNameY = 165;
                g.setFont(new Font("黑体", 1, 65));
                g.drawString(projectName, projectNameX, projectNameY);
            }

            if (!StringUtils.isEmpty(hospitalName)) {
                if (hospitalName.length() <= 17) {
                    int orgNameY = 720;
                    Font orgNameFont = new Font("黑体", 1, 68);
                    g.setFont(orgNameFont);
                    FontMetrics orgNameMetrics = g.getFontMetrics(orgNameFont);
                    int orgNameWidth = orgNameMetrics.stringWidth(hospitalName);
                    int orgNameX = (imageWidth - orgNameWidth) / 2;
                    g.drawString(hospitalName, orgNameX, orgNameY);
                } else {
                    String hospitalName1 = hospitalName.substring(0, 17);
                    int orgNameY = 700;
                    Font orgNameFont = new Font("黑体", 1, 60);
                    g.setFont(orgNameFont);
                    FontMetrics orgNameMetrics = g.getFontMetrics(orgNameFont);
                    int orgNameWidth = orgNameMetrics.stringWidth(hospitalName1);
                    int orgNameX = (imageWidth - orgNameWidth) / 2;
                    g.drawString(hospitalName1, orgNameX, orgNameY);

                    String hospitalName2 = hospitalName.substring(17);
                    orgNameY = 770;
                    orgNameFont = new Font("黑体", 1, 60);
                    g.setFont(orgNameFont);
                    orgNameMetrics = g.getFontMetrics(orgNameFont);
                    orgNameWidth = orgNameMetrics.stringWidth(hospitalName2);
                    orgNameX = (imageWidth - orgNameWidth) / 2;
                    g.drawString(hospitalName2, orgNameX, orgNameY);
                }
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

            if (!StringUtils.isEmpty(avatarImg)) {
                File avatarImgFile = new File(avatarImg);
                if (avatarImgFile.exists()) {
                    BufferedImage bufferedImage = ImageUtils.transferImgForRoundImage(ImageIO.read(avatarImgFile));
                    BufferedImage doctorAvatar = ImageUtils.resizeImageTransparent(280, 280, bufferedImage);
                    g.drawImage(doctorAvatar, 577, 200, doctorAvatar.getWidth(), doctorAvatar.getHeight(), null);
                    avatarImgFile.delete();
                }
            }
            g.dispose();

            String imgPath = path + "/" + UUID.randomUUID().toString().replace("-", "") + ".jpg";
            System.out.println(imgPath);
            file = new File(imgPath);
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
