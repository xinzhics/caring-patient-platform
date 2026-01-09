package com.caring.sass.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtils {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

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

    /**
     * 设置图片的尺寸
     * @param x
     * @param y
     * @param bfi
     * @return
     */
    public static BufferedImage resizeImage(int x, int y, BufferedImage bfi) {
        BufferedImage bufferedImage = new BufferedImage(x, y, 1);
        bufferedImage.getGraphics().drawImage(bfi.getScaledInstance(x, y, 4), 0, 0, null);
        return bufferedImage;
    }


    /**
     * 设置图片的尺寸，并保持背景的透明
     * @param x
     * @param y
     * @param bfi
     * @return
     */
    public static BufferedImage resizeImageTransparent(int x, int y, BufferedImage bfi) {
        BufferedImage bufferedImage = new BufferedImage(x, y, 1);
        Graphics2D g = bufferedImage.createGraphics();
        bufferedImage = g.getDeviceConfiguration().createCompatibleImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                Transparency.TRANSLUCENT);
        g = bufferedImage.createGraphics();
        g.drawImage(bfi.getScaledInstance(x, y, 4), 0, 0, null);
        return bufferedImage;
    }


    /**
     * 将图片处理为圆形图片
     * 传入的图片必须是正方形的才会生成圆形 如果是长方形的比例则会变成椭圆的
     *
     * @param avatarBufferedImage 医生头像的 buffer
     * @return
     */
    public static BufferedImage transferImgForRoundImage(BufferedImage avatarBufferedImage){
        BufferedImage resultImg = null;

        resultImg = new BufferedImage(avatarBufferedImage.getWidth(), avatarBufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resultImg.createGraphics();
        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, avatarBufferedImage.getWidth(), avatarBufferedImage.getHeight());
        // 使用 setRenderingHint 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        resultImg = g.getDeviceConfiguration().createCompatibleImage(avatarBufferedImage.getWidth(), avatarBufferedImage.getHeight(),
                Transparency.TRANSLUCENT);
        g = resultImg.createGraphics();
        // 使用 setRenderingHint 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(shape);
        g.drawImage(avatarBufferedImage, 0, 0, null);
        g.dispose();
        return resultImg;
    }

    public static Rectangle getImageRect(File file) {
        Rectangle r = new Rectangle();
        r.width = -1;
        r.height = -1;

        try {
            InputStream in = new FileInputStream(file);
            Throwable var3 = null;

            try {
                r = getImageRect(in);
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

    public static int getImgWidth(String url) {
        if (!StringUtils.isEmpty(url) && !url.startsWith("http://")) {
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
        if (!StringUtils.isEmpty(url) && !url.startsWith("http://")) {
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

    public static void resizeImage(String srcImgPath, File file, File folder, int width, int height) throws IOException {
        String url = FileUtils.downLoadFromUrl(srcImgPath, "temp", folder.getAbsolutePath());
        File srcFile = new File(url);
        String ext = FileUtils.getExtName(srcFile);
        Image srcImg = ImageIO.read(srcFile);
        BufferedImage buffImg = new BufferedImage(width, height, 1);
        buffImg.getGraphics().drawImage(srcImg.getScaledInstance(width, height, 4), 0, 0, (ImageObserver)null);
        deleteDirFile(url);
        boolean write = ImageIO.write(buffImg, ext, file);
        if (write) {
            logger.info("下载图片成功：[{}]", srcImgPath);
        } else {
            logger.error("下载图片失败：[{}]", srcImgPath);
        }
        ext = null;
        srcImg = null;
        srcFile = null;
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
        boolean var13 = false;

        int i;
        label114: {
            try {
                var13 = true;
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
                i = exec.exitValue();
                System.out.println("___________________代码最后________________");
                var13 = false;
                break label114;
            } catch (Exception var14) {
                logger.error("[执行命令出错]", var14);
                var14.printStackTrace();
                var13 = false;
            } finally {
                if (var13) {
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
}
