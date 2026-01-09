package com.caring.sass.common.enums;

import com.caring.sass.common.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 医生名片的背景
 */
public class DoctorLoginImage {

    private static DoctorLoginImage doctorLoginImage;

    private String fileImageUrl = "https://caring.obs.cn-north-4.myhuaweicloud.com/patientSubscribe/doctorLoginBg.png";

    private static File image = null;

    public static synchronized DoctorLoginImage getInstance() {
        if (doctorLoginImage == null) {
            doctorLoginImage = new DoctorLoginImage();
        }
        return doctorLoginImage;
    }

    public DoctorLoginImage() {
        String dir = System.getProperty("java.io.tmpdir");
        String filePath = "/saas/qrcode/";
        File folder = new File(dir + filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = "doctorLoginImage";
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
    }

    public File getDoctorLoginImage() {
        return image;
    }
}
