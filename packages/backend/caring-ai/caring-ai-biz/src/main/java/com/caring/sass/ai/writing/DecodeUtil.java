package com.caring.sass.ai.writing;

import java.io.UnsupportedEncodingException;

public class DecodeUtil {

    public static String unicodeToChinese(String unicodeStr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < unicodeStr.length(); i++) {
            if (unicodeStr.charAt(i) == '\\' && i + 1 < unicodeStr.length() && unicodeStr.charAt(i + 1) == 'u') {
                // 提取四位十六进制数
                String hex = unicodeStr.substring(i + 2, i + 6);
                // 将十六进制数转换为字符
                sb.append((char) Integer.parseInt(hex, 16));
                // 跳过已经处理的Unicode转义序列
                i += 5;
            } else {
                sb.append(unicodeStr.charAt(i));
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String a = "15:25:04.510 [OkHttp http://dify.caringopen.cn/...] INFO com.caring.sass.ai.writing.WritingUtil - id:null, data:{\"event\": \"message\", \"conversation_id\": \"90ddb9a0-c26c-4088-af52-f430f31f4d23\", \"message_id\": \"702dd707-b744-4a37-8cfb-992f4068cff7\", \"created_at\": 1721978700, \"task_id\": \"128725e1-e29d-4ae2-a04b-e7c9187e844f\", \"id\": \"702dd707-b744-4a37-8cfb-992f4068cff7\", \"answer\": \"\\u004f60\\u0053ca\\u0065f6\\u0091c7\\u0053d6\\u00884c\\u0052a8\\u00ff0c\\u00907f\\u00514d\\u0060c5\\u0051b5\\u006076\\u005316\"}\n";

        String unicodeStr = "\\u65e8\\u5728\\u4ee5\\u8f7b\\u677e\\u6109\\u5feb\\u7684\\u65b9\\u5f0f\\u5411\\u5b9d\\u7238";



        String unicodeStr1 = "\\u65e8\\u5728\\u4ee5\\u8f7b\\u677e\\u6109\\u5feb\\u7684\\u65b9\\u5f0f\\u5411\\u5b9d\\u7238";
        System.out.println(unicodeToChinese(unicodeStr1));
    }
}
