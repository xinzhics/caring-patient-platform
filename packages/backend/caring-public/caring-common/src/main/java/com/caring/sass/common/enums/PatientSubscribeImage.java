package com.caring.sass.common.enums;

import com.caring.sass.common.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 医生名片的背景
 */
public class PatientSubscribeImage {

    private static PatientSubscribeImage patientSubscribeImage;

    private String fileImageUrl2 = "https://caring.obs.cn-north-4.myhuaweicloud.com/patientSubscribe/docker_bussiess_bg_image.png";
    private String fileImageUrl3 = "https://caring.obs.cn-north-4.myhuaweicloud.com/patientSubscribe/doctor_qrcodebg.png";
    private String fileImageUrl = "https://caring.obs.cn-north-4.myhuaweicloud.com/patientSubscribe/bgImg.png";

    private static File image = null;
    private static File image2 = null;
    private static File image3 = null;

    public static synchronized PatientSubscribeImage getInstance() {
        if (patientSubscribeImage == null) {
            patientSubscribeImage = new PatientSubscribeImage();
        }
        return patientSubscribeImage;
    }

    public PatientSubscribeImage() {
        String dir = System.getProperty("java.io.tmpdir");
        String filePath = "/saas/qrcode/";
        File folder = new File(dir + filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = "patient_subscribe";
        String fileType = ".png";
        String backgroundPath = dir + filePath + fileName + fileType;
        System.out.println(backgroundPath);
        image = new File(backgroundPath);
        if (!image.exists()) {
            try {
                FileUtils.downLoadFromFile(fileImageUrl, fileName, dir + filePath);
            } catch (IOException e) {
                return;
            }
            image = new File(backgroundPath);
        }

        String fileName2 = "patient_subscribe_2";
        String fileType2 = ".png";
        String backgroundPath2 = dir + filePath + fileName2 + fileType2;
        System.out.println(backgroundPath2);
        image2 = new File(backgroundPath2);
        if (!image2.exists()) {
            try {
                FileUtils.downLoadFromFile(fileImageUrl2, fileName2, dir + filePath);
            } catch (IOException e) {
                return;
            }
            image2 = new File(backgroundPath2);
        }

        String fileName3 = "doctor_qrcodebg";
        String fileType3 = ".png";
        String backgroundPath3 = dir + filePath + fileName3 + fileType3;
        System.out.println(backgroundPath3);
        image3 = new File(backgroundPath3);
        if (!image3.exists()) {
            try {
                FileUtils.downLoadFromFile(fileImageUrl3, fileName3, dir + filePath);
            } catch (IOException e) {
                return;
            }
            image3 = new File(backgroundPath3);
        }
    }


    public File getPatientSubscribeImage() {
        return image;
    }
    public File getPatientSubscribeImage2() {
        return image2;
    }
    public File getPatientSubscribeImage3() {
        return image3;
    }
}
