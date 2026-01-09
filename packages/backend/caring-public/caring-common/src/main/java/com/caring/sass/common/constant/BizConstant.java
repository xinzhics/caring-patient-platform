package com.caring.sass.common.constant;

/**
 * 业务常量
 *
 * @author caring
 * @date 2019/08/06
 */
public interface BizConstant {
    /**
     * 超级管理员
     */
    String SUPER_ADMIN = "admin";

    /**
     * 超级租户编码
     */
    String SUPER_TENANT = "admin";

    /**
     * 第三方客户
     */
    String THIRD_PARTY_CUSTOMERS = "third_party_customers";

    String ADMIN = "admin";

    /**
     * 初始化的租户管理员角色
     */
    String INIT_ROLE_CODE = "PT_ADMIN";

    /**
     * 演示用的组织ID
     */
    Long DEMO_ORG_ID = 101L;
    /**
     * 演示用的岗位ID
     */
    Long DEMO_STATION_ID = 101L;

    /**
     * 默认密码 - 注意：生产环境请通过环境变量配置或强制用户首次登录修改
     */
    String DEF_PASSWORD = System.getenv().getOrDefault("DEFAULT_PASSWORD", "TempPass@2024!");
    // MD5哈希会在运行时计算，不要硬编码
    String DEF_PASSWORD_MD5_PLACEHOLDER = "PLEASE_USE_RUNTIME_HASH";

    /**
     * 默认的定时任务组
     */
    String DEF_JOB_GROUP_NAME = "caring-jobs-server";
    /**
     * 短信发送处理器
     */
    String SMS_SEND_JOB_HANDLER = "smsSendJobHandler";

    /**
     * 基础库
     */
    String BASE_DATABASE = "caring_base";
    /**
     * 扩展库
     */
    String EXTEND_DATABASE = "caring_extend";

    /**
     * 被T
     */
    String LOGIN_STATUS = "T";

    String AUTHORITY = "caring-authority-server";
    String FILE = "caring-file-server";
    String MSGS = "caring-msgs-server";
    String OAUTH = "caring-oauth-server";
    String GATE = "caring-gate-server";
    String ORDER = "caring-order-server";
    String DEMO = "caring-demo-server";

    /**
     * 初始化数据源时json的参数，
     * method 的可选值为 {INIT_DS_PARAM_METHOD_INIT} 和 {INIT_DS_PARAM_METHOD_REMOVE}
     */
    String INIT_DS_PARAM_METHOD = "method";
    /**
     * 初始化数据源时json的参数，
     * tenant 的值为 需要初始化的租户编码
     */
    String INIT_DS_PARAM_TENANT = "tenant";
    /**
     * 初始化数据源时，需要执行的方法
     * init 表示初始化数据源
     * remove 表示删除数据源
     */
    String INIT_DS_PARAM_METHOD_INIT = "init";
    /**
     * 初始化数据源时，需要执行的方法
     * init 表示初始化数据源
     * remove 表示删除数据源
     */
    String INIT_DS_PARAM_METHOD_REMOVE = "remove";

    String AI_EXCEPTION = "不好意思，信息量太大，我没有反应过来，您等会儿再试一下";
}
