package com.caring.sass.ai.utils;

import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
public class ImageDrawUtils {

//    public static String textBackImage = "https://caring-saas-video.oss-cn-beijing-internal.aliyuncs.com/human/background_20250820100645_49.png";
    public static String textBackImage = "https://caring-saas-video.oss-cn-beijing.aliyuncs.com/human/background_20250820100645_49.png";

    static Font font = new Font("Microsoft YaHei", Font.BOLD, 90);

    static String dir = System.getProperty("java.io.tmpdir");

    static String path = "/saas/article/video/cover/";

    public static void main(String[] args) {
        String videoCover = "https://caring-saas-video.oss-cn-beijing.aliyuncs.com/human/video/e63bc1fbeb5b49c68ff103ecce8d1c68.jpg";
        String text = "脱敏治疗选舌下含服还是皮下注射？";

        try {
            String coverImage = createCoverImage(videoCover, text);
            System.out.println(coverImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 创建封面
     * @param videoCover
     * @param text
     * @return
     * @throws IOException
     */
    public static String createCoverImage(String videoCover, String text) throws IOException {

        // 下载图片到本地
        File folder = new File(dir + path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = "cover_" + UUID.randomUUID().toString().replace("-", "");
        File file;
        try {
            file = FileUtils.downLoadFromFile(videoCover, fileName, dir + path);
        } catch (IOException e) {
            throw new BizException("下载视频封面失败");
        }
        if (file == null) {
            log.error("createCoverImage download video cover error: file not exist, videoCover: {}", videoCover);
        }

        String fileType = videoCover.substring(videoCover.lastIndexOf(".") + 1);
        if (!fileType.equals("jpg") && !fileType.equals("png")) {
            fileType = "jpg";
        }

        BufferedImage originalImage = ImageIO.read(file);

        log.error("text back resetTextBackImage {}", videoCover);
        String textBackImage = resetTextBackImage(originalImage.getWidth(), text);

        if (textBackImage ==null) {
            log.error("text back image is null");
            return null;
        }

        int y;
        fileName = "cover_" + UUID.randomUUID().toString().replace("-", "");
        File textBackImageFile = null;
        try {
            textBackImageFile = new File(textBackImage);
            // 加载背景图片和要叠加的图片
            BufferedImage overlayImage = ImageIO.read(textBackImageFile);

            // 创建Graphics2D对象以进行绘制
            Graphics2D g2d = originalImage.createGraphics();

            // 计算叠加图像在背景图像上的中心位置
            y = (originalImage.getHeight() - overlayImage.getHeight()) / 11 * 9;

            // 绘制叠加图像到背景图像的中心位置
            g2d.drawImage(overlayImage, 0, y, null);

            // 释放Graphics2D对象资源
            g2d.dispose();

            // 保存带有文字的新图片
            ImageIO.write(originalImage, fileType, new File(dir + path + fileName + "." + fileType));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (file.exists()) {
                file.delete();
            }
            if (textBackImageFile != null && textBackImageFile.exists()) {
                textBackImageFile.delete();
            }
        }

        // 将文字写入到 图片中 透明背景的区域
        String imageUrl = dir + path + fileName + "." + fileType;
        try {
            String textToImage = writeTextToImage(y, imageUrl, text, fileType);
            return textToImage;
        } catch (IOException e) {
            return null;
        } finally {
            if (new File(imageUrl).exists()) {
                new File(imageUrl).delete();
            }
        }

    }


    /**
     * 根据文字的行数占位。调整文字背景图的宽高
     * @param imageWidth
     * @param text
     * @return
     */
    public static String resetTextBackImage(int imageWidth, String text) {
        File file = null;
        try {
            // 下载图片到本地
            File folder = new File(dir + path);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String fileName = "cover_" + UUID.randomUUID().toString().replace("-", "");
            try {
                file = FileUtils.downLoadFromFile(textBackImage, fileName, dir + path);
            } catch (IOException e) {
                throw new BizException("下载视频封面失败");
            }
            String[] split = text.split("\n");
            BufferedImage originalImage = ImageIO.read(file);
            log.error("text back resetTextBackImage downLoadFromFile success");
            Graphics2D g2d = originalImage.createGraphics();

            // 设置字体和颜色
            g2d.setFont(font);

            // 获取字体度量信息
            FontMetrics fm = g2d.getFontMetrics();

            // 计算最长行的宽度
            int countLine = 0;
            int textMaxWidth = imageWidth / 3 * 2;
            int lineHeight = fm.getHeight(); // 每行高度（包含行间距）
            for (String line : split) {
                countLine++;
                int lineWidth = fm.stringWidth(line);
                if (lineWidth > textMaxWidth) {
                    countLine++;
                }
            }

            // 计算所有行的总高度
            int totalTextHeight = lineHeight * countLine;
            // 增加参数校验
            log.error("Invalid image dimensions: width={}, height={}", imageWidth, totalTextHeight);
            if (imageWidth <= 0 || totalTextHeight <= 0) {
                return null;
            }
            g2d.dispose();
            // 把文字的背景图拉宽
            BufferedImage bufferedImage = null;
            try {
                // 确保最小高度，避免过小的图像
                int adjustedHeight = Math.max(totalTextHeight + 30, 100); // 最小高度50
                int adjustedWidth = Math.max(imageWidth, 100); // 最小宽度100

                bufferedImage = resizeImage(adjustedWidth, adjustedHeight, originalImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bufferedImage == null) {
                log.error("resetTextBackImage resizeImage error bufferedImage is null");
                return null;
            }

            fileName = "cover_" + UUID.randomUUID().toString().replace("-", "");

            // 保存带有文字的新图片
            ImageIO.write(bufferedImage, "png", new File(dir + path + fileName + ".png"));
            System.out.println(dir + path + fileName + ".png");
            return dir + path + fileName + ".png";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
        return null;
    }


    /**
     * 写入文字到图片中
     * @param startY
     * @param imageUrl
     * @param text
     * @return
     * @throws IOException
     */
    public static String writeTextToImage(int startY, String imageUrl, String text, String fileType) throws IOException {

        BufferedImage bufferedImage = ImageIO.read(new File(imageUrl));

        int imageWidth = bufferedImage.getWidth();
        // 创建一个新的BufferedImage对象，用于绘制文字
        BufferedImage imageWithText = new BufferedImage(imageWidth, bufferedImage.getHeight(), bufferedImage.getType());
        Graphics2D g2d = imageWithText.createGraphics();

        // 绘制原始图片到新的BufferedImage对象上
        g2d.drawImage(bufferedImage, 0, 0, null);

        FontMetrics fm = g2d.getFontMetrics(font);
        int lineHeight = fm.getHeight(); // 每行高度（包含行间距）

        // 获取图片的宽度 高度
        int y;


        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 添加阴影效果
        Color shadowColor = new Color(76, 91, 151); // 阴影颜色
        // 设置抗锯齿等渲染属性
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setFont(font);
        g2d.setColor(shadowColor);
        int textMaxWidth = imageWidth / 3 * 2;
        String[] split = text.split("\n");

        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < split.length; i++) {
            String string = split[i];
            int lineWidth = fm.stringWidth(string);
            if (lineWidth > textMaxWidth) {
                // 精确按像素宽度分割
                arrayList.addAll(splitStringByWidth(string, fm, textMaxWidth));
            } else {
                arrayList.add(string);
            }
        }

        for (int i = 0; i < arrayList.size(); i++) {
            String string = arrayList.get(i);
            int lineWidth = fm.stringWidth(string);


            int startX = (imageWidth - lineWidth) / 2;
            y = startY + (i + 1) * lineHeight;


            // 通过绘制多遍带有小偏移的文字来创建描边效果
            int strokeThickness = 5; // 调整这个值可以改变描边的厚度
            for (int offsetX = -strokeThickness; offsetX <= strokeThickness; offsetX += strokeThickness) {
                for (int offsetY = -strokeThickness; offsetY <= strokeThickness; offsetY += strokeThickness) {
                    if (offsetX != 0 || offsetY != 0) { // 避免在原始位置重复绘制
                        g2d.drawString(string, startX + offsetX, y + offsetY);
                    }
                }
            }

        }


        for (int i = 0; i < arrayList.size(); i++) {
            String string = arrayList.get(i);
            int lineWidth = fm.stringWidth(string);
            int startX = (imageWidth - lineWidth) / 2;
            y = startY + (i + 1) * lineHeight;

            // 再绘制主文字（覆盖在描边上）
            g2d.setColor(Color.WHITE); // 主文字颜色为白色
            g2d.drawString(string, startX, y); // 同样位置绘制主文字
        }


        // 清理资源
        g2d.dispose();
        String fileName = "cover_" + UUID.randomUUID().toString().replace("-", "");

        // 保存带有文字的新图片
        ImageIO.write(imageWithText, fileType, new File(dir + path + fileName + "." + fileType));
        return dir + path + fileName + "." + fileType;

    }


    public static BufferedImage resizeImage(int x, int y, BufferedImage bfi) {
        // 使用TYPE_INT_ARGB以确保支持透明度
        BufferedImage resizedImage = new BufferedImage(x, y, bfi.getType());
        Graphics2D g2d = resizedImage.createGraphics();

        // 设置高质量的渲染属性
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制并缩放原始图像
        g2d.drawImage(bfi, 0, 0, x, y, null);
        g2d.dispose(); // 释放资源

        return resizedImage;
    }

    // 辅助方法：按像素宽度精确分割字符串
    private static ArrayList<String> splitStringByWidth(String text, FontMetrics fm, int maxWidth) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            line.append(ch);
            int currentWidth = fm.stringWidth(line.toString());

            // 如果当前行宽度超过了最大宽度
            if (currentWidth > maxWidth) {
                // 移除最后一个字符
                line.deleteCharAt(line.length() - 1);
                // 如果行不为空，则添加到结果中
                if (line.length() > 0) {
                    result.add(line.toString());
                }
                // 重新开始新的一行，包含当前字符
                line = new StringBuilder();
                line.append(ch);
            }
        }

        // 添加最后一行
        if (line.length() > 0) {
            result.add(line.toString());
        }

        // 如果结果为空但原文本不为空，至少添加一个字符
        if (result.isEmpty() && text.length() > 0) {
            result.add(String.valueOf(text.charAt(0)));
        }

        return result;
    }






}
