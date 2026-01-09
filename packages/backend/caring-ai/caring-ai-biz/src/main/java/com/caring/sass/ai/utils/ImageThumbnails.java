package com.caring.sass.ai.utils;

import cn.hutool.core.lang.UUID;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Comparator;

@Slf4j
public class ImageThumbnails {

    /**
     * 下载图片保存到本地
     * @param input 图片的输入流
     * @return  图片的路径
     */
    private static File storageFile(InputStream input, String thisWorkDirPath) {
        try (InputStream inputStream = input) {
            BufferedImage originalImage = ImageIO.read(inputStream);
            BufferedImage jpegImage;
            if (originalImage.getColorModel().hasAlpha()) {
                // 如果原图有透明度，需要处理
                int width = originalImage.getWidth();
                int height = originalImage.getHeight();
                jpegImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = jpegImage.createGraphics();
                g.drawImage(originalImage, 0, 0, null);
                g.dispose();
            } else {
                // 如果原图无透明度，直接转换
                jpegImage = originalImage;
            }
            File outputFile = new File(thisWorkDirPath + UUID.fastUUID() + ".jpg");
            ImageWriter writer = ImageIO.getImageWritersByFormatName("JPEG").next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionQuality(1.0f); // 设置最高质量

            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
                writer.setOutput(ios);
                writer.write(jpegImage);
                writer.dispose();
                ios.close();
            }
            return outputFile;
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
            throw new BizException("保存图片失败");
        }

    }


    public static void deleteDirectory(String path) {
        Path dir = Paths.get(path);
        try {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            log.error("删除目录失败", e);
        }
    }

    /**
     * 检查图片的分辨率大小
     * @param file 原图片
     * @return 可能是原图片。或压缩后尺寸合适的图片
     */
    private static File checkImageSize(File file, String thisWorkDirPath) {
        try {
            BufferedImage originalImage = ImageIO.read(file);
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // 检查图片分辨率是否超过4096*4096
            if (originalWidth > 4096 || originalHeight > 4096) {
                // 计算压缩比例，保持原有长宽比
                double scale = Math.min(4096.0 / originalWidth, 4096.0 / originalHeight);
                int targetWidth = (int) (originalWidth * scale);
                int targetHeight = (int) (originalHeight * scale);

                // 使用Thumbnails进行压缩和尺寸调整
                BufferedImage resizedImage = Thumbnails.of(originalImage)
                        .size(targetWidth, targetHeight)
                        .outputQuality(1.0) // 保持最佳质量，因为主要是通过尺寸调整来限制分辨率
                        .asBufferedImage();
                File output = new File(thisWorkDirPath + UUID.fastUUID() + ".jpg");
                output.createNewFile();
                // 写入压缩后的图片
                ImageIO.write(resizedImage, "jpg", output);
                return output;
            } else {
                System.out.println("Image resolution is already within 4096x4096. No resizing needed.");
                return file;
            }
        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
        }
        return file;
    }

    public static String getImageBase64(MultipartFile multipartFile) {
        float quality = 0.9f; // 初始压缩质量
        float scale = 0.9f; // 初始压缩质量
        File file;
        String tempWorkDirPath = System.getProperty("user.dir") + "/" + UUID.randomUUID();
        String thisWorkDirPath = tempWorkDirPath + "/face/";
        File dir = new File(thisWorkDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file = storageFile(multipartFile.getInputStream(), thisWorkDirPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException("上传图片失败");
        }
        file = checkImageSize(file, thisWorkDirPath);
        while (true) {
            try {
                byte[] read = Files.readAllBytes(file.toPath());
                if (read.length > 4.7 * 1024 * 1024) {
                    // 如果图片大于4.5MB，则压缩
                    File thumbnailsFile = new File(thisWorkDirPath + UUID.randomUUID() + ".jpg");
                    Thumbnails.of(file)
                            .scale(scale)
                            .outputQuality(quality) // 设置压缩质量
                            .outputFormat("JPEG")
                            .toFile(thumbnailsFile); // 输出到输出流
                    file = thumbnailsFile;
                } else {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                deleteDirectory(tempWorkDirPath);
                throw new BizException("压缩图片失败");
            }
        }
        try {
            byte[] read = Files.readAllBytes(file.toPath());
            String encodedToString = Base64.getEncoder().encodeToString(read);
            return "data:image/jpeg;base64," + encodedToString;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException("获取图片base64失败");
        } finally {
            deleteDirectory(tempWorkDirPath);
        }
    }




}
