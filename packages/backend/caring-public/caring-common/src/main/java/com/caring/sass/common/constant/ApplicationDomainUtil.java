package com.caring.sass.common.constant;


import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;

/**
 * 项目域名工具
 * 获取项目管理后台域名
 * 获取项目微信业务域名
 * 获取项目微信业务域名、js安全域名
 * 获取项目微信服务器配置url
 *
 * TODO：生产环境和测试环境的区别
 * 1. 测试环境的定制化公众号js域名更改， 生产环境定制化公众号数量过多，js域名无法更改
 * 2. 所有的域名都根据 公众号的授权类型判断返回的域名。
 * 3. 定制化项目的公众号，域名保持 caringsaas.com 不变, 开放平台授权的公众号域名 使用 caringsaas.cn 作为域名
 * 4. 主要对 wxPatientBaseDomain wxDoctorBaseDomain wxGuestBaseDomain wxJsSecurityDomain 这四处进行第3步骤的调整
 * 修改人： yangshuai
 *
 * @author xinzh
 */
public class ApplicationDomainUtil {

    /**
     * 获取项目管理后台地址
     * 如：http://caring.caringsaas.cn
     *
     * @param tenantDomain 项目域名
     * @return 项目管理后台地址
     */
    public static String adminUrl(String tenantDomain) {
        if (StrUtil.isBlank(tenantDomain)) {
            return baseDomain();
        }

        String domainName = ApplicationProperties.getMainDomainName();
        return String.format("%s://%s.%s", "https", tenantDomain, domainName);
    }

    /**
     * web域名
     * 如： http://admin.caringsaas.cn
     * @return
     */
    public static String webAdminDomain() {
        String protocol = ApplicationProperties.getDomainProtocol();
        String webTenantDomain = ApplicationProperties.getWebTenantDomain();
        return String.format("%s://%s", protocol, webTenantDomain);
    }



    /**
     * 项目 域名
     * 如： http://kailin.caringsaas.cn
     * TODO: 患者端禁止使用此方法
     * 主要给 非 第三方授权公众号 使用
     * @return
     */
    public static String tenantDomain(String domain) {
        String protocol = ApplicationProperties.getDomainProtocol();
        String domainName = ApplicationProperties.getDomainName();
        return String.format("%s://%s.%s", protocol, domain, domainName);
    }


    public static String baseDomain() {
        String domainName = ApplicationProperties.getDomainName();
        String protocol = ApplicationProperties.getDomainProtocol();
        return String.format("%s://%s", protocol, domainName);
    }

    /**
     * 获取项目接口地址
     * 如： http://api-test.caringsaas.cn
     *
     * @return 项目接口地址
     */
    public static String apiUrl() {
        String protocol = ApplicationProperties.getDomainProtocol();
        String apiSubDomain = ApplicationProperties.getApiSubDomain();
        return String.format("%s://%s", protocol, apiSubDomain);
    }

    /**
     * 获取微信服务器配置url
     * 如：http://api-test.caringsaas.cn/api/wx/0112/callback
     *
     * @param code 项目code
     */
    public static String wxServerUrl(String code) {
        return String.format("%s/api/wx/%s/callback", apiUrl(), code);
    }

    /**
     * 微信患者端根路径
     * 如：http://kailing.caringsaas.cn/wx
     */
    public static String wxPatientBaseDomain(String tenantDomain, Boolean thirdAuthorization) {
        String domainName;
        if (thirdAuthorization != null && thirdAuthorization) {
            domainName = ApplicationProperties.getMainDomainName();
            return String.format("%s://%s.%s/wx", "https", tenantDomain, domainName);
        } else {
            domainName = ApplicationProperties.getDomainName();
        }
        String protocol = ApplicationProperties.getDomainProtocol();
        return String.format("%s://%s.%s/wx", protocol, tenantDomain, domainName);
    }

    /**
     * 获取建站cms路径
     * @param tenantDomain
     * @return
     */
    public static String cmsShowDomain(String tenantDomain, Boolean thirdAuthorization, String bizPath) {
        String domainName;
        if (thirdAuthorization != null && thirdAuthorization) {
            domainName = ApplicationProperties.getMainDomainName();
            String format = String.format("%s://%s.%s/site_cms", "https", tenantDomain, domainName);
            return format + "/" + bizPath;
        } else {
            domainName = ApplicationProperties.getDomainName();
        }
        String protocol = ApplicationProperties.getDomainProtocol();
        String format = String.format("%s://%s.%s/site_cms", protocol, tenantDomain, domainName);
        return format + "/" + bizPath;
    }

    /**
     * 外部链接跳转前的打开页面。
     * @param tenantDomain
     * @param thirdAuthorization
     * @return
     */
    public static String externalLinksShowUrl(String tenantDomain, Boolean thirdAuthorization, String... params) {
        String domainName;
        if (thirdAuthorization != null && thirdAuthorization) {
            domainName = ApplicationProperties.getMainDomainName();
            return String.format("%s://%s.%s/externalLinksShow", "https", tenantDomain, domainName) + "?" + Joiner.on("&").join(params);
        } else {
            domainName = ApplicationProperties.getDomainName();
        }
        String protocol = ApplicationProperties.getDomainProtocol();
        return String.format("%s://%s.%s/externalLinksShow", protocol, tenantDomain, domainName) + "?" + Joiner.on("&").join(params);
    }

    /**
     * 微信医生端根域名
     * 如：http://kailing.caringsaas.cn/doctor
     */
    public static String wxDoctorBaseDomain(String tenantDomain, Boolean thirdAuthorization) {
        String domainName;
        if (thirdAuthorization != null && thirdAuthorization) {
            domainName = ApplicationProperties.getMainDomainName();
            return String.format("%s://%s.%s/doctor", "https", tenantDomain, domainName);
        } else {
            domainName = ApplicationProperties.getDomainName();
        }
        String protocol = ApplicationProperties.getDomainProtocol();
        return String.format("%s://%s.%s/doctor", protocol, tenantDomain, domainName);
    }

    /**
     * 医助端H5域名
     * @param tenantDomain
     * @param thirdAuthorization
     * @return
     */
    public static String wxAssistantBaseDomain(String tenantDomain, Boolean thirdAuthorization) {
        String domainName;
        if (thirdAuthorization != null && thirdAuthorization) {
            domainName = ApplicationProperties.getMainDomainName();
            return String.format("%s://%s.%s/assistantH5", "https", tenantDomain, domainName);
        } else {
            domainName = ApplicationProperties.getDomainName();
        }
        String protocol = ApplicationProperties.getDomainProtocol();
        return String.format("%s://%s.%s/assistantH5", protocol, tenantDomain, domainName);
    }

    /**
     * 前端跟域名
     * @param tenantDomain
     * @param guest
     * @return
     */
    public static String wxGuestBaseDomain(String tenantDomain, String guest, Boolean thirdAuthorization) {
        String domainName;
        if (thirdAuthorization != null && thirdAuthorization) {
            domainName = ApplicationProperties.getMainDomainName();
            return String.format("%s://%s.%s/%s", "https", tenantDomain, domainName, guest);
        } else {
            domainName = ApplicationProperties.getDomainName();
        }
        String protocol = ApplicationProperties.getDomainProtocol();
        return String.format("%s://%s.%s/%s", protocol, tenantDomain, domainName, guest);
    }

    /**
     * 微信js安全域名，不需要http://前缀
     * 如：kailing.caringsaas.cn
     */
    public static String wxJsSecurityDomain(String tenantDomain, Boolean thirdAuthorization) {
        String domainName;
        if (thirdAuthorization != null && thirdAuthorization) {
            domainName = ApplicationProperties.getMainDomainName();
        } else {
            domainName = ApplicationProperties.getDomainName();
        }
        return String.format("%s.%s", tenantDomain, domainName);
    }

    /**
     * 微信业务相关url
     * 如：http://kailing.caringsaas.cn/entryGroup
     *
     * @param tenantDomain 项目域名
     * @param bizPath      业务路径
     * @return 业务url
     */
    public static String wxPatientBizUrl(String tenantDomain, Boolean thirdAuthorization, String bizPath) {
        String jsSecurityUrl = wxPatientBaseDomain(tenantDomain, thirdAuthorization);
        if (StrUtil.isBlank(bizPath)) {
            return jsSecurityUrl;
        }
        return jsSecurityUrl + "/" + bizPath;
    }

    /**
     * 微信业务相关url
     * 如：http://kailing.caringsaas.cn/entryGroup
     *
     * @param tenantDomain 项目域名
     * @param bizPath      业务路径
     * @return 业务url
     */
    public static String wxDoctorBizUrl(String tenantDomain, Boolean thirdAuthorization, String bizPath) {
        String jsSecurityUrl = wxDoctorBaseDomain(tenantDomain, thirdAuthorization);
        if (StrUtil.isBlank(bizPath)) {
            return jsSecurityUrl;
        }
        return jsSecurityUrl + "/" + bizPath;
    }

    public static String wxDoctorBizUrl(String tenantDomain, Boolean thirdAuthorization, String bizPath, String... params) {
        String wxDoctorBaseDomain = wxDoctorBaseDomain(tenantDomain, thirdAuthorization);
        if (StrUtil.isBlank(bizPath)) {
            return wxDoctorBaseDomain;
        }
        if (params == null || params.length == 0) {
            return wxDoctorBaseDomain + "/" + bizPath;
        }
        return wxDoctorBaseDomain + "/" + bizPath + "?" + Joiner.on("&").join(params);
    }

    /**
     * 会诊小组二维码地址的链接
     * @param tenantDomain
     * @param guest
     * @param bizPath
     * @return
     */
    public static String wxGuestBizUrl(String tenantDomain, Boolean thirdAuthorization, String guest, String bizPath) {
        String jsSecurityUrl = wxGuestBaseDomain(tenantDomain, guest, thirdAuthorization);
        if (StrUtil.isBlank(bizPath)) {
            return jsSecurityUrl;
        }
        return jsSecurityUrl + "/" + bizPath;
    }

    /**
     * 根据项目域名、业务路径、参数拼接微信业务相关url
     *
     * @param tenantDomain 项目域名 如：kailing
     * @param bizPath      业务路径 如：wx/im/index
     * @param params       参数 如：id=xxx
     * @return 业务url 如：http://kailing.caringsaas.cn/wx/im/index?id=xxx
     */
    public static String wxPatientBizUrl(String tenantDomain, Boolean thirdAuthorization, String bizPath, String... params) {
        String wxPatientBaseDomain = wxPatientBaseDomain(tenantDomain, thirdAuthorization);
        if (StrUtil.isBlank(bizPath)) {
            return wxPatientBaseDomain;
        }
        if (params == null || params.length == 0) {
            return wxPatientBaseDomain + "/" + bizPath;
        }
        return wxPatientBaseDomain + "/" + bizPath + "?" + Joiner.on("&").join(params);
    }




}