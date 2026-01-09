package com.caring.sass.cms.reptile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @className: Base64Util
 * @author: 杨帅
 * @date: 2023/12/5
 */
public class Base64Util {

    public static String encode(BufferedImage bufferedImage, String imageType) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, imageType, outputStream);
        return encode(outputStream);

    }

    public static String encode(ByteArrayOutputStream outputStream) {
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }


}
