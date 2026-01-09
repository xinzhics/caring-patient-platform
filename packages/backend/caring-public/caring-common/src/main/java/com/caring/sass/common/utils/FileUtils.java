package com.caring.sass.common.utils;

import org.apache.http.entity.ContentType;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {
    public static final HashMap<String, String> fileTypes = new HashMap();
    public static final List<String> imageTypes = new ArrayList();

    public FileUtils() {
    }

    public static InputStream getInputStreamFromUrl(String u)
            throws IOException {
        URL url = new URL(u);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(3000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        return conn.getInputStream();
    }



    public static File downLoadFromFile(String u, String fileName, String dir) throws IOException {
        FileOutputStream fos = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(3000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            inputStream = conn.getInputStream();
            byte[] getData = readInputStream(inputStream);

            File saveDir = new File(dir);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            String fileType = getFileType(getData);
            if (fileType == null && u.contains(".")) {
                fileType = u.substring(u.lastIndexOf(".") + 1);
            }
            String path = new StringBuilder().append(dir).append(fileName).append(".").append(fileType).toString();
            File file = new File(path);
            fos = new FileOutputStream(file);
            fos.write(getData);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return null;
    }


    public static File catImage(File file) {
        // 图片路径
        String dir = System.getProperty("java.io.tmpdir");
        String filePath = "/saas/qrcodeTemp/";
        File folder = new File(dir + filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String outputImagePath = dir + filePath + UUID.randomUUID().toString() + ".png";

        // 裁剪区域的坐标和尺寸
        int x = 15;  // 起始 x 坐标
        int y = 15;  // 起始 y 坐标
        int width = 252;  // 裁剪宽度
        int height = 252; // 裁剪高度

        try {
            // 读取原始图片
            BufferedImage originalImage = null;
            try {
                originalImage = ImageIO.read(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 检查裁剪区域是否超出图片边界
            if (x + width > originalImage.getWidth() || y + height > originalImage.getHeight()) {
                System.out.println("裁剪区域超出图片边界");
                return file;
            }

            // 裁剪图片
            BufferedImage croppedImage = originalImage.getSubimage(x, y, width, height);

            // 保存裁剪后的图片
            File outputFile = new File(outputImagePath);
            ImageIO.write(croppedImage, "png", outputFile);
            file.delete();
            return outputFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }



    /**
     * 对base64字符串转成MultipartFile。要求是jpg图片格式
     * @param base64
     * @return
     */
    public static MockMultipartFile imageBase64ToMultipartFile(String base64) {
        String[] strings = base64.split(",");
        String type = strings[0].split(";")[0].split("/")[1];
        String ext = type.equals("jpeg") ? "jpg" : type;
        String data = strings[1];
        byte[] decoded = Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8));
        ByteArrayInputStream inputStream;
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        String fileName = uuidString + "." + ext;
        try {
            inputStream = new ByteArrayInputStream(decoded);
            try (ByteArrayInputStream finalInputStream = inputStream) {
                return new MockMultipartFile(fileName, fileName,
                        ContentType.APPLICATION_OCTET_STREAM.toString(), finalInputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MockMultipartFile fileToFileItem(File newfile) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(newfile);
            return new MockMultipartFile(newfile.getName(), newfile.getName(),
                    ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    public static void removeFile(File path) {
        if (path.exists()) {
            if (path.isFile()) {
                path.delete();
            } else {
                File[] files = path.listFiles();

                for(int i = 0; i < files.length; ++i) {
                    removeFile(files[i]);
                }

                path.delete();
            }
        }
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


    public static String getExtName(File file) {
        String absolutePath = file.getAbsolutePath();
        String substring = absolutePath.substring(absolutePath.lastIndexOf(".") + 1);
        return substring;
    }

    public static File downLoadArticleFromUrl(String fileUrl, String fileName) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        InputStream inputStream = conn.getInputStream();
        byte[] getData = readInputStream(inputStream);
        String dir =  System.getProperty("user.dir") + "/material/";
        File saveDir = new File(dir);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        String path = dir + fileName;
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        return file;
    }

    public static File downLoadKnowFileFromUrl(String u, String submitFileName, String dir) throws IOException {
        URL url = new URL(u);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        File saveDir = new File(dir);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        String path = dir + File.separator + submitFileName;
        File file = new File(path);

        try (InputStream inputStream = conn.getInputStream();
             FileOutputStream fos = new FileOutputStream(file)) {

            byte[] getData = readInputStream(inputStream);
            fos.write(getData);
        } catch (IOException e) {
            throw new IOException("Error downloading file: " + e.getMessage(), e);
        }

        return file;
    }
    public static final String imagePath = "saas" + File.separator + "tempfile" + File.separator;
    public static String downLoadFromUrl(String u, String fileName, String dir) throws IOException {
        if (dir == null) {
            dir = System.getProperty("java.io.tmpdir");
            dir = dir + imagePath;
            File saveDir = new File(dir);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
        }
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
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }

        bos.close();
        return bos.toByteArray();
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

    public static void unzip(File folder, File zipToBeUnzip) throws Exception {
        if (!zipToBeUnzip.exists()) {
            throw new RuntimeException("压缩包不存在");
        } else {
            ZipFile zipFile = new ZipFile(zipToBeUnzip,  Charset.forName("GBK"));
            Throwable var4 = null;

            try {
                Enumeration<?> entries = zipFile.entries();
                ZipEntry entry = null;

                while(entries.hasMoreElements()) {
                    entry = (ZipEntry)entries.nextElement();
                    if (entry.isDirectory()) {
                        (new File(folder, entry.getName())).mkdirs();
                    } else {
                        File targetFile = new File(folder, entry.getName());
                        if (!targetFile.getParentFile().exists()) {
                            targetFile.getParentFile().mkdirs();
                        }

                        targetFile.createNewFile();
                        FileOutputStream fos = new FileOutputStream(targetFile);
                        Throwable var9 = null;

                        try {
                            InputStream is = zipFile.getInputStream(entry);
                            Throwable var11 = null;

                            try {
                                byte[] buf = new byte[10240];

                                int len;
                                while((len = is.read(buf)) != -1) {
                                    fos.write(buf, 0, len);
                                }
                            } catch (Throwable var56) {
                                var11 = var56;
                                throw var56;
                            } finally {
                                if (is != null) {
                                    if (var11 != null) {
                                        try {
                                            is.close();
                                        } catch (Throwable var55) {
                                            var11.addSuppressed(var55);
                                        }
                                    } else {
                                        is.close();
                                    }
                                }

                            }
                        } catch (Throwable var58) {
                            var9 = var58;
                            throw var58;
                        } finally {
                            if (fos != null) {
                                if (var9 != null) {
                                    try {
                                        fos.close();
                                    } catch (Throwable var54) {
                                        var9.addSuppressed(var54);
                                    }
                                } else {
                                    fos.close();
                                }
                                fos = null;
                            }
                        }
                    }
                }
            } catch (Throwable var60) {
                var4 = var60;
                throw var60;
            } finally {
                if (zipFile != null) {
                    if (var4 != null) {
                        try {
                            zipFile.close();
                        } catch (Throwable var53) {
                            var4.addSuppressed(var53);
                        }
                    } else {
                        zipFile.close();
                    }
                }
            }
            System.gc();
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
