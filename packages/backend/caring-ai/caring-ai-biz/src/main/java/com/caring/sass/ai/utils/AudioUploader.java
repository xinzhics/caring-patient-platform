package com.caring.sass.ai.utils;

import com.caring.sass.file.api.FileUploadApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class AudioUploader {

    @Autowired
    private FileUploadApi fileUploadApi; // 注入文件上传服务

    private static final String FILE_PATH = "/saas/podcast/audio/downloads";

    /**
     * 手动实现将 hex 字符串转换为字节数组
     */
    public byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2]; // 每两个 hex 字符表示一个字节
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    // 将字节数组保存为 MP3 文件
    public File byteArrayToFile(byte[] byteArray, String outputFilePath) throws IOException {
        File outputFile = new File(outputFilePath);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(byteArray);
        }
        return outputFile;
    }

    // 将 MP3 文件转换为 MultipartFile
    public MultipartFile fileToMultipartFile(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        return new MockMultipartFile(
                file.getName(), // 文件名
                file.getName(), // 原始文件名
                "audio/mpeg",   // 内容类型（MP3 文件的 MIME 类型）
                fileContent // 文件内容
        );
    }

    /**
     * 生成临时文件
     * @return 临时文件路径
     */
    public String generateTmpFile(String hexAudio) throws IOException {
        // Step 1: 将 hex 编码的字符串转换为字节数组
        byte[] audioBytes = hexStringToByteArray(hexAudio);

        // Step 2: 将字节数组保存为 MP3 文件，使用唯一文件名
        String tempFilePath = FILE_PATH + File.separator + "temp_audio_" + Thread.currentThread().getId() + "_" + System.nanoTime() + ".mp3";
        File tempMp3File = byteArrayToFile(audioBytes, tempFilePath);
        return tempFilePath;
    }


    // 上传音频文件
    public String uploadAudio(String hexAudio, Long folderId, String obsFileName) throws IOException {
        // Step 1: 将 hex 编码的字符串转换为字节数组
        byte[] audioBytes = hexStringToByteArray(hexAudio);

        // Step 2: 将字节数组保存为 MP3 文件，使用唯一文件名
        String tempFileName = "temp_audio_" + Thread.currentThread().getId() + "_" + System.nanoTime() + ".mp3";
        File tempMp3File = byteArrayToFile(audioBytes, tempFileName);

        // Step 3: 将 MP3 文件转换为 MultipartFile
        MultipartFile multipartFile = fileToMultipartFile(tempMp3File);

        // Step 4: 调用文件上传服务上传文件
        com.caring.sass.base.R<com.caring.sass.file.entity.File> r = fileUploadApi.uploadAppFile(folderId, multipartFile, obsFileName);
        // todo 需要验证清理临时文件
        tempMp3File.delete();
        return r.getData().getUrl();
    }
}
