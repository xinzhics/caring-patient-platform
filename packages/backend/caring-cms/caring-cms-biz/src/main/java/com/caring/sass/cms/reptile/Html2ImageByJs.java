package com.caring.sass.cms.reptile;

import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * @className: Html2ImageByJsWrapper
 * @author: 杨帅
 * @date: 2023/12/5
 */
public class Html2ImageByJs {

    final static String BLANK = "  ";
    //通过phantomjs将url截取成图片保存
    public static String UrlHtmlToImage(String phantomPath, String jsPath, String url, String dest) throws IOException {
        Process process = Runtime.getRuntime().exec(phantomPath + BLANK //你的phantomjs.exe路径
                + jsPath + BLANK //就是上文中那段javascript脚本的存放路径
                + url + BLANK //你的目标url地址
                + dest);//你的图片输出路径

        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        return dest;
    }


    public static String imageToBase64(String path) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(path);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }


    /**
     * 截图程序,根据url地址，返回一个base64的图片,
     * @param url
     * @param outImagePath
     * @return
     * @throws IOException
     */
    public static String cuttingUrlToImage(String url, String outImagePath) throws IOException {
        String phantomJsPath = "";
        String jsPath = "";//截图js脚本

        //截屏并保存在相应路径，返回一个图片的路径
        String fpath = outImagePath;
        //根据操作系统
        String osName = System.getProperty("os.name");
        if (osName != null && osName.startsWith("Windows")) {
            //如果是window，则选择合适的phantomjs
            phantomJsPath = "D:\\project file\\phantomjs\\bin\\phantomjs.exe";
            jsPath = "D:\\project file\\phantomjs\\examples\\rasterize.js";
            //获取js的路径
        } else {
            phantomJsPath = "/usr/local/bin/phantomjs";
            //获取js的路径
            jsPath = "/usr/local/html2Image.js";
        }

        String s = UrlHtmlToImage(phantomJsPath , jsPath, url, fpath);
//        System.out.println(s);
//        String base64Image = "data:image/jpg;base64," + imageToBase64(s);//根据图片路径将对应的图片转为base64
//        File file = new File(fpath);//生成一个临时的图片，并转为base64，然后把图片删除
//        if (file.exists()) {
//            file.delete();
//        }
//        return base64Image;
            return null;
    }
}
