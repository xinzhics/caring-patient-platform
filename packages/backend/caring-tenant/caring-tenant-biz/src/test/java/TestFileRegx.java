import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class TestFileRegx {

    private static final String workFolder = "D:\\android\\assistantApp";

    // 2. 替换文件assistantApp\app\src\main\assets\apps\__UNI__DA034C0\www\app-service.js中的MDExMg==值为相应的租户编码
    private static void replaceBaseFiles(String workFolder, String dcloudAppid, String tenantCode) {
        // 替换文件assistantApp\app\src\main\assets\data\dcloud_control.xml中 默认的__UNI__DA034C0的值为 dcloudAppid
        File dcloudControlXmlPath = Paths.get(workFolder, "app", "src", "main", "assets", "data", "dcloud_control.xml").toFile();
        if (FileUtil.exist(dcloudControlXmlPath)) {
            String fileContent = FileUtil.readString(dcloudControlXmlPath, StandardCharsets.UTF_8);
            String replaceFileContent = StrUtil.replace(fileContent, "__UNI__DA034C0", dcloudAppid);
            FileUtil.writeString(replaceFileContent, dcloudControlXmlPath, StandardCharsets.UTF_8);
        }

        // 2. 替换文件assistantApp\app\src\main\assets\apps\__UNI__DA034C0\www\app-service.js中的MDExMg==值为相应的租户编码
        final File appServiceJsDir = new File(Paths.get(workFolder, "app", "src", "main", "assets", "apps").toUri());
        if (!appServiceJsDir.exists()) {
            log.error("基座{}目录不存在", appServiceJsDir.getPath());
            return;
        }
        final String[] fileList = appServiceJsDir.list();
        if (fileList == null) {
            log.error("基座{}目录暂无文件", appServiceJsDir.getPath());
        }

        String regexp = "^__UNI\\_\\S*";
        String appServiceJsFileUrl = "", manifestJson = "";
        for (String uniDir : fileList) {
            if (uniDir.matches(regexp)) {
                // 目录替换
                Path oldUniPath = Paths.get(workFolder, "app", "src", "main", "assets", "apps", uniDir);
                Path newUniPath = Paths.get(workFolder, "app", "src", "main", "assets", "apps", dcloudAppid);
                new File(oldUniPath.toUri()).renameTo(new File(newUniPath.toUri()));

                appServiceJsFileUrl = Paths.get(newUniPath.toString(), "www", "app-service.js").toString();
                manifestJson = Paths.get(newUniPath.toString(), "www", "manifest.json").toString();
                break;
            }
        }
        String content = FileUtil.readString(appServiceJsFileUrl, StandardCharsets.UTF_8);
        content = StrUtil.replace(content, "MDExMg==", tenantCode);
        FileUtil.writeString(content, appServiceJsFileUrl, StandardCharsets.UTF_8);
        FileUtil.writeString(StrUtil.replace(FileUtil.readString(manifestJson, StandardCharsets.UTF_8), "__UNI__DA034C0", dcloudAppid), manifestJson, StandardCharsets.UTF_8);
    }


    public static void main(String[] args) {
        replaceBaseFiles("D:\\work\\0112_ahpu\\0112_ahpu\\assistantApp","__UNI__9914F58","MDExMg==");
    }
}
