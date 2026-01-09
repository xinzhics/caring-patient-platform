package com.caring.sass.user.constant;

/**
 * @ClassName Constant
 * @Description 组织机构用户服务，常量
 * @Author yangShuai
 * @Date 2020/9/27 13:54
 * @Version 1.0
 */
public interface Constant {
    /**
     * app登录名的后缀
     */
    String SERVICE_LOGIN_NAME_SUFFIX = "app";
    /**
     * 默认医生登录名的后缀
     */
    String DOCTOR_LOGIN_NAME_SUFFIX = "_doctor";
    /**
     * 默认密码 - 注意：生产环境请通过环境变量配置或强制用户首次登录修改
     */
    String DEFAULT_PASSWORD = System.getenv().getOrDefault("DEFAULT_PASSWORD", "TempPass@2024!");
    
    String DEFAULT_NURSING_STAFF_PASSWORD = System.getenv().getOrDefault("DEFAULT_NURSING_STAFF_PASSWORD", "TempNursePass@2024!");

    Long HOUR = 12 * 60 * 60L;

    /**
     * 30天
     */
    Long THIRTY_DAYS = 30 *24* 60 * 60L;

    /**
     * 360天
     */
    Long ONE_YEAR_DAYS= 360 *24* 60 * 60L;

}
