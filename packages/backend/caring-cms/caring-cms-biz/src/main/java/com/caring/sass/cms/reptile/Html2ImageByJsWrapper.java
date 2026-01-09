package com.caring.sass.cms.reptile;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @className: Html2ImageByJsWrapper
 * @author: 杨帅
 * @date: 2023/12/5
 */
public class Html2ImageByJsWrapper {

    private static PhantomJSDriver webDriver = getPhantomJs();

    private static PhantomJSDriver getPhantomJs() {

        DesiredCapabilities dcaps = new DesiredCapabilities();
        // ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        // 截屏支持
        dcaps.setCapability("takesScreenshot", true);

        //css 搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        // js 支持
        dcaps.setJavascriptEnabled(true);
        // 驱动支持
        String osName = System.getProperty("os.name");
        if(osName != null && osName.startsWith("Windows")) {
            dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                    "D:\\project file\\phantomjs\\bin\\phantomjs.exe");
        } else {
            dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                    "/usr/local/bin/phantomjs");

        }

        PhantomJSDriver jsDriver = new PhantomJSDriver(dcaps);

        // 设置屏幕大小为手机屏幕
        Dimension dimension = new Dimension(1920,1080);

        jsDriver.manage().window().setSize(dimension);
        return jsDriver;
    }

    public static boolean executeScript() {
        Object script = webDriver.executeScript("return window.pageLoaded;");
        if (Objects.nonNull(script)) {
            return true;
        } else {
            return false;
        }
    }

    public static File renderHtml2Image(String url, String outFilePath) {
        webDriver.get(url);
        webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        byte[] screenshotAs = webDriver.getScreenshotAs(OutputType.BYTES);
        File file = save(screenshotAs, outFilePath);
        return file;
    }

    private static File save(byte[] data, String outFilePath) {
        OutputStream stream = null;

        try {
            File tmpFile = new File(outFilePath);
            stream = new FileOutputStream(tmpFile);
            stream.write(data);

            return tmpFile;
        } catch (IOException e) {
            throw new WebDriverException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // Nothing sane to do
                }
            }
        }
    }
}
