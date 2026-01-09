
package com.caring.sass.common.constant;

import com.caring.sass.common.utils.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xinzh
 */
@Component
@ConfigurationProperties(prefix = ApplicationProperties.PREFIX)
public class ApplicationProperties {

    public static final String PREFIX = "caring.application";

    /**
     * web前端的域名
     */
    private static String webTenantDomain;

    /**
     * 域名协议
     */
    private static String domainProtocol;

    /**
     * 域名
     */
    private static String domainName;

    /**
     * 主要域名
     * 此域名存在时， domainName 会失效
     */
    private static String mainDomainName;

    /**
     * 接口子域名
     */
    private static String apiSubDomain;

    /**
     * 微信IP白名单
     */
    private static String whiteList;

    /**
     * 打包临时文件夹
     */
    private static String tempFolderLocation;

    /**
     * apk源码文件夹
     */
    private static String apkSourceCodeFolder;

    /**
     * apk默认显示版本号
     */
    private static String apkVersion;

    /**
     * uniapp基座应用ID
     */
    private static String baseUniAppId;

    /**
     * apk默认版本号
     */
    private static Integer apkVersionCode;

    /**
     * apk默认启动图
     */
    private static String apkLaunchImage;

    /**
     * 医生端token失效时间
     */
    private static Long doctorTokenExpire;

    /**
     * 第三方平台的 EncodingAesKey
     */
    private static String thirdPlatformEncodingAesKey;

    /**
     * 第三方平台的 token
     */
    private static String thirdPlatformToken;

    /**
     * 第三方平台的 appSecret
     */
    private static String thirdPlatformComponentAppSecret;

    /**
     * 第三方平台的 appId
     */
    private static String thirdPlatformComponentAppId;

    /**
     * 服务协议
     */
    public static final String APK_AGREEMENT = "<p>尊敬的用户： 欢迎使用${project.name}！" +
            " &nbsp;</p >↵<p>您在使用${project.name}服务前，请仔细阅读本原则，一旦您开始使用，则意味着您将自愿遵守以下原则，并完全服从于${project.name}的统一管理。" +
            " &nbsp; 1、${project.name}平台的服务是一项慢病云呵护服务，针对已确诊并需要慢病照护的慢病患者。" +
            " &nbsp;</p >↵<p>2、${project.name}的慢病照护服务为广大患者提供了一个便捷的渠道，可以通过微信服务号提交信息，平台医生/医助能够通过互联网看到这些信息，并给予相应指导，所有指导仅限参考，一切诊疗问题请线下咨询自己的医生。" +
            " &nbsp;</p >↵<p>3、患者所有的病情信息均自行提供，需要确保录入或者上传信息及时、准确，平台医生/医助将完全根据患者提供的信息做出指导和建议，如果患者提供的信息有误，造成的任何后果自行承担。" +
            " &nbsp;</p >↵<p>4、在照护服务过程中，所有需要定期体检的项目均是患者所患疾病所必须的，并由患者自行决定是否接受，平台医生/医助不会增加额外的非必要化验检查。 " +
            "&nbsp;</p >↵<p>5、患者提供的所有信息，其个人隐私内容不会向除患者本人和提供服务的平台医生/医助之外的任何第三方公开。" +
            " &nbsp;</p >↵<p>6、${project.name}平台提供的慢病照护服务是常规医疗服务的有效补充，但不能代替到医院就诊。平台医生/医助的回复也可能有延迟，尤其是患者病情严重或者发生急性症状和化验结果严重异常的情况下，都需要尽快就医，不能依赖系统回复。" +
            " &nbsp;</p >↵<p>7、患者提供的病情信息、提交的健康咨询问题，如果${project.name}平台认为其内容包含任何不基于事实、虚构、编造及无亲身经历的言论，带有威胁、淫秽、漫骂、非法及带有人身攻击之言论，含有色情、暴力、恐怖内容、含有违背伦理道德内容，涉及违法犯罪的内容，以及其他${project.name}平台认为不恰当的情况，${project.name}平台有权进行编辑、删除、直至取消该账户资格，并保留进行法律追究的权利。" +
            " &nbsp;</p >↵<p>" +
            "8、内容中出现的所有商标、服务标志、商号、商业外观、商业标识等所有权均归${project.name}平台所有。未经${project.name}平台或第三方的书面许可，禁止以任何方式使用。 &nbsp;</p >↵<p>" +
            "9、本免责声明以及其修改权、更新权及最终解释权均属${project.name}平台所有。</p>";

    /**
     * OpenAi秘钥，可多个
     */
    private static List<String> openAiToken;

    /**
     * OpenAI地址
     */
    private static String openAiHost;

    /**
     * 医生和GPT聊天线程池的最大线程数量
     */
    private static int doctorGptMaxiMumPoolSize;

    /**
     * 机器人名称
     */
    private static String botName;

    /**
     * 百度灵医Bot AK
     */
    private static String baiduBotAK;
    /**
     * 百度灵医Bot SK
     */
    private static String baiduBotSK;

    private static String encryptionKey;



    public static void setEncryptionKeyValue(String encryptionKey) {
        ApplicationProperties.encryptionKey = encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        setEncryptionKeyValue(encryptionKey);
    }

    public static String getEncryptionKey() {
        return encryptionKey;
    }


    public static void setOpenAiHostValue(String openAiHost) {
        ApplicationProperties.openAiHost = openAiHost;
    }

    public void setOpenAiHost(String openAiHost){
        setOpenAiHostValue(openAiHost);
    }

    public static String getOpenAiHost() {
        return openAiHost;
    }

    public static void setBaiduBotSKValue(String baiduBotSK) {
        ApplicationProperties.baiduBotSK = baiduBotSK;
    }

    public void setBaiduBotSK(String baiduBotSK) {
        setBaiduBotSKValue(baiduBotSK);
    }

    public static String getBaiduBotSK() {
        return baiduBotSK;
    }

    public static void setBaiduBotAKValue(String baiduBotAK) {
        ApplicationProperties.baiduBotAK = baiduBotAK;
    }

    public void setBaiduBotAK(String baiduBotAK) {
        setBaiduBotAKValue(baiduBotAK);
    }

    public static String getBaiduBotAK() {
        return baiduBotAK;
    }

    public static void setBotNameValue(String botName) {
        ApplicationProperties.botName = botName;
    }

    public void setBotName(String botName) {
        setBotNameValue(botName);
    }

    public static String getBotName() {
        return botName;
    }

    public void setDoctorGptMaxiMumPoolSize(Integer doctorGptMaxiMumPoolSize) {
        setDoctorGptMaxiMumPoolSizeValue(doctorGptMaxiMumPoolSize);
    }

    public static void setDoctorGptMaxiMumPoolSizeValue(Integer doctorGptMaxiMumPoolSize) {
        if (doctorGptMaxiMumPoolSize == null) {
            ApplicationProperties.doctorGptMaxiMumPoolSize = 8;
        } else {
            ApplicationProperties.doctorGptMaxiMumPoolSize = doctorGptMaxiMumPoolSize;
        }
    }

    public static int getDoctorGptMaxiMumPoolSize() {
        return doctorGptMaxiMumPoolSize;
    }

    public void setOpenAiToken(List<String> openAiToken) {
        setOpenAiTokenValue(openAiToken);
    }

    public static void setOpenAiTokenValue(List<String> openAiToken) {
        ApplicationProperties.openAiToken = openAiToken;
    }

    public static List<String> getOpenAiToken() {
        return openAiToken;
    }

    public void setDoctorTokenExpire(Long doctorTokenExpire) {
        setDoctorTokenExpireValue(doctorTokenExpire);
    }

    public static void setDoctorTokenExpireValue(Long doctorTokenExpire) {
        ApplicationProperties.doctorTokenExpire = doctorTokenExpire;
    }

    public static Long getDoctorTokenExpire() {
        return doctorTokenExpire;
    }

    public void setApkVersion(String apkVersion) {
        setApkVersionValue(apkVersion);
    }

    public void setApkLaunchImage(String apkLaunchImage) {
        setApkLaunchImageValue(apkLaunchImage);
    }

    public void setApkVersionCode(Integer apkVersionCode) {
        setApkVersionCodeValue(apkVersionCode);
    }

    public static void setApkVersionValue(String apkVersion) {
        ApplicationProperties.apkVersion = apkVersion;
    }

    public static void setApkLaunchImageValue(String apkLaunchImage) {
        ApplicationProperties.apkLaunchImage = apkLaunchImage;
    }

    public static void setApkVersionCodeValue(Integer apkVersionCode) {
        ApplicationProperties.apkVersionCode = apkVersionCode;
    }

    public static String getApkVersion() {
        return apkVersion;
    }

    public void setBaseUniAppId(String baseUniAppId) {
        setBaseUniAppIdValue(baseUniAppId);
    }

    public static void setBaseUniAppIdValue(String baseUniAppId) {
        ApplicationProperties.baseUniAppId = baseUniAppId;
    }

    public static String getBaseUniAppId() {
        return baseUniAppId;
    }

    public static Integer getApkVersionCode() {
        return apkVersionCode;
    }

    public static String getApkLaunchImage() {
        return apkLaunchImage;
    }

    public static String getApkSourceCodeFolder() {
        return apkSourceCodeFolder;
    }

    public static void setApkSourceCodeFolderValue(String apkSourceCodeFolder) {
        ApplicationProperties.apkSourceCodeFolder = apkSourceCodeFolder;
    }

    public void setApkSourceCodeFolder(String apkSourceCodeFolder) {
        setApkSourceCodeFolderValue(apkSourceCodeFolder);
    }

    public static String getDomainProtocol() {
        return domainProtocol;
    }

    public static void setDomainProtocolValue(String domainProtocol) {
        ApplicationProperties.domainProtocol = domainProtocol;
    }

    public static String getMainDomainName() {
        if (StringUtils.isEmpty(mainDomainName)) {
            return domainName;
        } else {
            return mainDomainName;
        }
    }

    public static void setMainDomainNameValue(String mainDomainName) {
        ApplicationProperties.mainDomainName = mainDomainName;
    }


    public static String getDomainName() {
        return domainName;
    }

    public static void setDomainNameValue(String domainName) {
        ApplicationProperties.domainName = domainName;
    }

    public static String getWebTenantDomain() {
        return webTenantDomain;
    }

    public static void setWebTenantDomainValue(String webTenantDomain) {
        ApplicationProperties.webTenantDomain = webTenantDomain;
    }


    public static String getTempFolderLocation() {
        return tempFolderLocation;
    }

    public static void setTempFolderLocationValue(String tempFolderLocation) {
        ApplicationProperties.tempFolderLocation = tempFolderLocation;
    }

    public static String getApiSubDomain() {
        return apiSubDomain;
    }

    public static void setWhiteListValue(String whiteList) {
        ApplicationProperties.whiteList = whiteList;
    }

    public static String getWhiteList() {
        return whiteList;
    }

    public static void setApiSubDomainValue(String apiSubDomain) {
        ApplicationProperties.apiSubDomain = apiSubDomain;
    }

    public void setApiSubDomain(String apiSubDomain) {
        setApiSubDomainValue(apiSubDomain);
    }

    public void setDomainProtocol(String domainProtocol) {
        setDomainProtocolValue(domainProtocol);
    }

    public void setDomainName(String domainName) {
        setDomainNameValue(domainName);
    }

    public void setMainDomainName(String mainDomainName) {
        setMainDomainNameValue(mainDomainName);
    }

    public void setWebTenantDomain(String webTenantDomain) {
        setWebTenantDomainValue(webTenantDomain);
    }

    public void setTempFolderLocation(String tempFolderLocation) {
        setTempFolderLocationValue(tempFolderLocation);
    }

    public void setWhiteList(String whiteList) {
        setWhiteListValue(whiteList);
    }

    public static String getThirdPlatformEncodingAesKey() {
        return thirdPlatformEncodingAesKey;
    }

    public void setThirdPlatformEncodingAesKey(String thirdPlatformEncodingAesKey) {
        setThirdPlatformEncodingAesKeyValue(thirdPlatformEncodingAesKey);
    }

    public static void setThirdPlatformEncodingAesKeyValue(String thirdPlatformEncodingAesKey) {
        ApplicationProperties.thirdPlatformEncodingAesKey = thirdPlatformEncodingAesKey;
    }

    public static String getThirdPlatformToken() {
        return thirdPlatformToken;
    }

    public void setThirdPlatformToken(String thirdPlatformToken) {
        setThirdPlatformTokenValue(thirdPlatformToken);
    }

    public static void setThirdPlatformTokenValue(String thirdPlatformToken) {
        ApplicationProperties.thirdPlatformToken = thirdPlatformToken;
    }

    public static String getThirdPlatformComponentAppSecret() {
        return thirdPlatformComponentAppSecret;
    }

    public static void setThirdPlatformComponentAppSecretValue(String thirdPlatformComponentAppSecret) {
        ApplicationProperties.thirdPlatformComponentAppSecret = thirdPlatformComponentAppSecret;
    }

    public void setThirdPlatformComponentAppSecret(String thirdPlatformComponentAppSecret) {
        setThirdPlatformComponentAppSecretValue(thirdPlatformComponentAppSecret);
    }

    public static String getThirdPlatformComponentAppId() {
        return thirdPlatformComponentAppId;
    }

    public static void setThirdPlatformComponentAppIdValue(String thirdPlatformComponentAppId) {
        ApplicationProperties.thirdPlatformComponentAppId = thirdPlatformComponentAppId;
    }

    public void setThirdPlatformComponentAppId(String thirdPlatformComponentAppId) {
        setThirdPlatformComponentAppIdValue(thirdPlatformComponentAppId);
    }


}
