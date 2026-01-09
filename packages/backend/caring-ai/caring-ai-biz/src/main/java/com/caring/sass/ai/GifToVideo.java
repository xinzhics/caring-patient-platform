//package com.caring.sass.ai;
//
//import com.caring.sass.ai.utils.ImageDrawUtils;
//import org.bytedeco.ffmpeg.global.avutil;
//import org.bytedeco.javacv.*;
//import org.bytedeco.javacv.Frame;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//import javax.imageio.ImageIO;
//import javax.imageio.ImageReader;
//import javax.imageio.metadata.IIOMetadata;
//import javax.imageio.stream.ImageInputStream;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.util.Iterator;
//
//public class GifToVideo {
//
//    public static void main(String[] args) throws Exception {
//        ImageDrawUtils imageDrawUtils = new ImageDrawUtils();
//        String aiArticle = imageDrawUtils.createCoverImage("C:\\Users\\Administrator\\Pictures\\fengdi.png",
//                "AI \n 科普创作");
//        BufferedImage background = ImageIO.read(new File(aiArticle));
//
//        String gifFile = "C:\\Users\\Administrator\\Pictures\\c2cec3fdfc039245d34113a18694a4c27c1e25a3.gif";           // GIF 动画
//        String outputFile = "C:\\Users\\Administrator\\Pictures\\output.mp4";           // 输出视频
//        int width = 450;                           // 视频宽度
//        int height = 600;                          // 视频高度
//        int fps = 25;                               // 帧率（根据 GIF 调整）
//
//        // 读取背景图
//        background = resizeImage(background, width, height);
//
//        // 打开 GIF
//        ImageInputStream input = ImageIO.createImageInputStream(new File(gifFile));
//        Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
//        ImageReader reader = readers.next();
//        reader.setInput(input);
//
//        // 初始化视频录制器
//        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, width, height, 0);
//        recorder.setVideoCodecName("libx264");
//        recorder.setFormat("mp4");
//        recorder.setFrameRate(fps);
//        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P); // 常用像素格式
//        recorder.start();
//        // 初始化Java2DFrameConverter用于转换BufferedImage到Frame
//        Java2DFrameConverter converter = new Java2DFrameConverter();
//        // 合成每一帧
//        int frameCount = reader.getNumImages(true);
//        for (int i = 0; i < frameCount; i++) {
//            BufferedImage gifFrame = reader.read(i);
//            // 获取 GIF 帧延迟（单位：毫秒）
//            IIOMetadata metadata = reader.getImageMetadata(i);
//            long delayMs = getFrameDelay(metadata);
//            int repeatFrames = Math.max(1, (int) Math.round(fps * delayMs / 1000.0));
//
//            // 合成图像：将 gifFrame 叠加到 background 上（居中示例）
//            BufferedImage composite = new BufferedImage(width, height, background.getType());
//            Graphics2D g = composite.createGraphics();
//            g.drawImage(background, 0, 0, null);
//            int x = (width - gifFrame.getWidth()) / 2;
//            int y = (height - gifFrame.getHeight()) / 2;
//            System.out.println("合成图像：" + x + " " + (y - gifFrame.getHeight() / 2));
//            g.drawImage(gifFrame, x, y - gifFrame.getHeight() / 2, null);
//            g.dispose();
//
//            // 写入 repeatFrames 次（保持时长）
//            for (int j = 0; j < repeatFrames; j++) {
//                // 将BufferedImage转换为Frame
//                Frame frame = converter.convert(composite);
//                recorder.record(frame, avutil.AV_PIX_FMT_RGB24);
//            }
//        }
//
//        // 释放资源
//        recorder.stop();
//        recorder.release();
//        reader.dispose();
//        input.close();
//
//        System.out.println("视频生成完成：" + outputFile);
//    }
//
//    private static int getFrameDelay(IIOMetadata metadata) {
//        Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
//        NodeList children = tree.getChildNodes();
//
//        for (int i = 0; i < children.getLength(); i++) {
//            Node node = children.item(i);
//            if (node instanceof Element) {
//                Element element = (Element) node;
//                String delayStr = element.getAttribute("delayTime");
//                if (delayStr != null && !delayStr.isEmpty()) {
//                    try {
//                        int centisecs = Integer.parseInt(delayStr);
//                        return centisecs * 10; // 返回毫秒
//                    } catch (NumberFormatException e) {
//                        return 100; // 解析失败，返回默认 100ms
//                    }
//                }
//            }
//        }
//        return 100; // 默认延迟 100ms（即 10fps）
//    }
//
//    // 图像缩放工具方法
//    private static BufferedImage resizeImage(BufferedImage original, int targetWidth, int targetHeight) {
//        Image tmp = original.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
//        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, original.getType());
//        Graphics2D g2d = resized.createGraphics();
//        g2d.drawImage(tmp, 0, 0, null);
//        g2d.dispose();
//        return resized;
//    }
//}