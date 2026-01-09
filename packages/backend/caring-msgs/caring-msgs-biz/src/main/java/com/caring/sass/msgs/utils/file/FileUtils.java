//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.caring.sass.msgs.utils.file;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;

public class FileUtils {
    public static final HashMap<String, String> fileTypes = new HashMap();
    public static final List<String> imageTypes = new ArrayList();

    public FileUtils() {
    }

    public static String getFileType(byte[] bytes) throws IOException {
        String hv = null;
        StringBuilder builder = new StringBuilder();
        if (bytes != null && bytes.length > 0) {
            for(int i = 0; i < 2; ++i) {
                hv = Integer.toHexString(bytes[i] & 255).toUpperCase();
                if (hv.length() < 2) {
                    builder.append(0);
                }

                builder.append(hv);
            }

            return (String)fileTypes.get(builder.toString());
        } else {
            return null;
        }
    }

    public static String downLoadFromUrl(String u, String fileName, String dir) throws IOException {
        URL url = new URL(u);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        InputStream inputStream = conn.getInputStream();
        byte[] getData = readInputStream(inputStream);
        File saveDir = new File(dir);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }

        String fileType = getFileType(getData);
        String path = dir + fileName + "." + fileType;
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }

        if (inputStream != null) {
            inputStream.close();
        }

        return path;
    }

    public static String downLoadFromFastDFS(String u, String fileName, String dir) throws IOException {
        URL url = new URL(u);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        InputStream inputStream = conn.getInputStream();
        byte[] getData = readInputStream(inputStream);
        File saveDir = new File(dir);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }

        String fileType = getExtName(u);
        String path = saveDir + File.separator + fileName + "." + fileType;
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }

        if (inputStream != null) {
            inputStream.close();
        }

        return path;
    }

    public static String getExtName(String url) {
        String substring = url.substring(url.lastIndexOf(".") + 1);
        return substring;
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }

        bos.close();
        return bos.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        byte[] imageStream = getImageStream("D:\\temp\\2.png");
        String fileType = getFileType(imageStream);
        System.out.println(fileType);
    }

    public static byte[] getImageStream(String url) {
        byte[] buffer = null;
        File file = new File(url);

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] b = new byte[1024];

            int n;
            while((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            fis.close();
            bos.close();
            buffer = bos.toByteArray();
            if (file.exists()) {
                file.delete();
            }
        } catch (FileNotFoundException var7) {
            var7.printStackTrace();
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        return buffer;
    }

    public static void addTextAndImage(File file, String str, String url) throws Exception {
        Image srcImg = ImageIO.read(file);
        int srcImgWidth = srcImg.getWidth((ImageObserver)null);
        int srcImgHeight = srcImg.getHeight((ImageObserver)null);
        Font font = new Font("Serif", 1, 10);
        BufferedImage bi = new BufferedImage(srcImgWidth, srcImgHeight, 1);
        Graphics2D g2 = (Graphics2D)bi.getGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, srcImgWidth, srcImgHeight);
        g2.setPaint(Color.RED);
        FontRenderContext context = g2.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(str, context);
        double x = ((double)srcImgWidth - bounds.getWidth()) / 2.0D;
        double y = ((double)srcImgHeight - bounds.getHeight()) / 2.0D;
        double ascent = -bounds.getY();
        double baseY = y + ascent;
        g2.drawString(str, (int)x, (int)baseY);

        try {
            ImageIO.write(bi, "jpg", file);
        } catch (IOException var20) {
            var20.printStackTrace();
        }

    }

    public int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }

    public static boolean deleteFile(String fileName) {
        File file = new File("d:/temp/百度.jpg");
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

    static {
        fileTypes.put("FFD8", "jpg");
        fileTypes.put("8950", "png");
        fileTypes.put("4749", "gif");
        fileTypes.put("4949", "tif");
        fileTypes.put("424D", "bmp");
        fileTypes.put("504B", "zip");
    }
}
