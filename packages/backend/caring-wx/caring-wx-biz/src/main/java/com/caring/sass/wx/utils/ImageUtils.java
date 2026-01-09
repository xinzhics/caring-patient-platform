//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.caring.sass.wx.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtils {
    public ImageUtils() {
    }

    public static Rectangle getImageRect(InputStream in) throws Exception {
        Rectangle r = new Rectangle();
        r.width = -1;
        r.height = -1;
        BufferedImage src = ImageIO.read(in);
        r.width = src.getWidth((ImageObserver)null);
        r.height = src.getHeight((ImageObserver)null);
        return r;
    }

    public static File multipartFileToFile(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String[] filename = originalFilename.split(".");
        File file = null;
        try {
            file = File.createTempFile(filename[0], filename[1]);
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static Rectangle getImageRect(File file) {
        Rectangle r = new Rectangle();
        r.width = -1;
        r.height = -1;

        try {
            InputStream in = new FileInputStream(file);
            Throwable var3 = null;

            try {
                r = getImageRect((InputStream)in);
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (in != null) {
                    if (var3 != null) {
                        try {
                            in.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        in.close();
                    }
                }

            }
        } catch (Exception var15) {
        }

        return r;
    }

    public static Rectangle getImageRect(String url) {
        if (!StringUtils.isEmpty(url) && !url.startsWith("http://")) {
            url = "http://" + url;
        }

        Rectangle r = new Rectangle();
        r.width = -1;
        r.height = -1;
        InputStream is = null;

        Rectangle var4;
        try {
            is = getInputStreamByGet(url);
            Rectangle var3 = getImageRect(is);
            return var3;
        } catch (Exception var14) {
            var4 = r;
        } finally {
            try {
                is.close();
            } catch (Exception var13) {
            }

        }

        return var4;
    }

    public static InputStream getInputStreamByGet(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                return inputStream;
            }
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return null;
    }
}
