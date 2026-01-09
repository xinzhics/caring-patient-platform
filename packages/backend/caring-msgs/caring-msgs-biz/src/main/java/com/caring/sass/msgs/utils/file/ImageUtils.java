//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.caring.sass.msgs.utils.file;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtils {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    public ImageUtils() {
    }

    public static int getImgWidth(String url) {
        if (StringUtils.isNotEmpty(url) && !url.startsWith("http://")) {
            url = "http://" + url;
        }

        InputStream is = getInputStreamByGet(url);
        int ret = -1;

        try {
            BufferedImage src = ImageIO.read(is);
            ret = src.getWidth((ImageObserver)null);
            is.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return ret;
    }

    public static int getImgHeight(String url) {
        if (StringUtils.isNotEmpty(url) && !url.startsWith("http://")) {
            url = "http://" + url;
        }

        InputStream is = getInputStreamByGet(url);
        int ret = -1;

        try {
            BufferedImage src = ImageIO.read(is);
            ret = src.getHeight((ImageObserver)null);
            is.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return ret;
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

    public static void deleteDirFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }

    }

    public static void main(String[] args) throws Exception {
        System.out.println("---------------------开始执行---------------------");
        Runtime runtime = Runtime.getRuntime();
        Process exec = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;
        boolean var12 = false;

        int i;
        label114: {
            try {
                var12 = true;
                String dir = "d:/mp4/build.bat";
                File bat = new File(dir);
                if (!bat.exists()) {
                    writeStr2File(dir, "d:\ncd mp4\npptvideo xxx.pptx 2222.wmv");
                }

                exec = runtime.exec(dir);
                bufrIn = new BufferedReader(new InputStreamReader(exec.getInputStream(), "UTF-8"));
                bufrError = new BufferedReader(new InputStreamReader(exec.getErrorStream(), "UTF-8"));
                String line = null;

                while((line = bufrIn.readLine()) != null) {
                    logger.info(line + "\n");
                }

                while((line = bufrError.readLine()) != null) {
                    logger.info(line + "\n");
                }

                exec.waitFor();
                System.out.println("___________________代码最后________________" + exec.exitValue());
                var12 = false;
                break label114;
            } catch (Exception var13) {
                logger.error("[执行命令出错]", var13);
                var13.printStackTrace();
                var12 = false;
            } finally {
                if (var12) {
                    i = exec.exitValue();
                    System.out.println("命令执行结果：" + i);
                    if (exec != null) {
                        exec.destroy();
                    }

                }
            }

            i = exec.exitValue();
            System.out.println("命令执行结果：" + i);
            if (exec != null) {
                exec.destroy();
            }

            return;
        }

        i = exec.exitValue();
        System.out.println("命令执行结果：" + i);
        if (exec != null) {
            exec.destroy();
        }

    }

    public static void writeStr2File(String dir, String content) {
        File file = new File(dir);
        if (!file.exists()) {
            FileOutputStream fileOutputStream = null;

            try {
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(content.getBytes("utf-8"));
                fileOutputStream.close();
            } catch (Exception var12) {
                var12.printStackTrace();
                throw new RuntimeException("写入内容出错了");
            } finally {
                try {
                    fileOutputStream.close();
                } catch (Exception var11) {
                    var11.printStackTrace();
                }

            }
        }

    }

    public static InputStream getInputStream(String path) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(path);
        return fileInputStream;
    }

    public static int getImgWidthForPath(String path) throws Exception {
        InputStream is = getInputStream(path);
        int ret = -1;

        try {
            BufferedImage src = ImageIO.read(is);
            ret = src.getWidth((ImageObserver)null);
            is.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return ret;
    }

    public static int getImgHeightForPath(String path) throws Exception {
        InputStream is = getInputStream(path);
        int ret = -1;

        try {
            BufferedImage src = ImageIO.read(is);
            ret = src.getHeight((ImageObserver)null);
            is.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return ret;
    }
}
