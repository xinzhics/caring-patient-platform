package com.caring.sass.ai.know.util;

import com.caring.sass.common.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 医生知识库的背景
 */
public class DocuKnowImage {

    private static DocuKnowImage docuKnowImage;

    private static String fileImageUrl = "https://caring.obs.cn-north-4.myhuaweicloud.com/docuknow/docu_know_paper_bg.png";

    private static File image = null;

    public static synchronized DocuKnowImage getInstance() {
        if (docuKnowImage == null) {
            docuKnowImage = new DocuKnowImage();
        }
        return docuKnowImage;
    }

    public DocuKnowImage() {
        String dir = System.getProperty("java.io.tmpdir");
        String filePath = "/saas/qrcode/";
        File folder = new File(dir + filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = "docu_know_paper_bg";
        String fileType = ".png";
        String backgroundPath = dir + filePath + fileName + fileType;
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

    public File getDocuKnowImage() {
        return image;
    }
}
