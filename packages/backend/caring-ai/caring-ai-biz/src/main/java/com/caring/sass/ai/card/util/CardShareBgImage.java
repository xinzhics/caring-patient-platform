package com.caring.sass.ai.card.util;

import com.caring.sass.common.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 医生知识库的背景
 */
public class CardShareBgImage {

    private static CardShareBgImage cardShareBgImage;

    private static final String fileImageUrl = "https://caring.obs.cn-north-4.myhuaweicloud.com/business_card/card_share_bg.png";

    private static File image = null;

    public static synchronized CardShareBgImage getInstance() {
        if (cardShareBgImage == null) {
            cardShareBgImage = new CardShareBgImage();
        }
        return cardShareBgImage;
    }

    public CardShareBgImage() {
        String dir = System.getProperty("java.io.tmpdir");
        String filePath = "/saas/cardShareCode/";
        File folder = new File(dir + filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = "card_share_bg";
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

    public File getCardShareBgImage() {
        return image;
    }
}
