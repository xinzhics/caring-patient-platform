/*
 Caring Patient Platform - Database Initialization Script
 合并后的数据库初始化脚本，包含数据库创建、表结构和初始数据
 
 执行方式：
 mysql -u root -p < caring_init.sql
 
 或者：
 mysql -u root -p
 source /path/to/caring_init.sql
*/

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `caring_base_0000` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `caring_base_0000`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================================
-- 基础表结构（来自 caring_column.sql，支持多租户）
-- ============================================================================

-- ----------------------------
-- Table structure for c_auth_application
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_application`;
CREATE TABLE `c_auth_application` (
  `id` bigint NOT NULL COMMENT 'ID',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `client_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `client_secret` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `website` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `app_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `describe_` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` bit(1) DEFAULT b'1' COMMENT '是否启用',
  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `UN_APP_KEY` (`client_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用';

-- ----------------------------
-- Table structure for c_auth_menu
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_menu`;
CREATE TABLE `c_auth_menu` (
  `id` bigint NOT NULL COMMENT '主键',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `label` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `describe_` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_public` bit(1) DEFAULT b'0' COMMENT '公共菜单\nTrue是无需分配所有人就可以访问的',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_enable` bit(1) DEFAULT b'1' COMMENT '状态',
  `sort_value` int DEFAULT '1' COMMENT '排序',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `group_` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_id` bigint DEFAULT '0' COMMENT '父级菜单ID',
  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `I_AUTH_MENU_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单';

-- ----------------------------
-- Table structure for c_auth_resource
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_resource`;
CREATE TABLE `c_auth_resource` (
  `id` bigint NOT NULL COMMENT 'ID',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `menu_id` bigint DEFAULT NULL COMMENT '菜单ID\n#c_auth_menu',
  `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `C_AUTH_RESOURCE_MENU_ID` (`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源';

-- ----------------------------
-- Table structure for c_auth_role
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_role`;
CREATE TABLE `c_auth_role` (
  `id` bigint NOT NULL,
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色所属租户',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色名称',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色编码',
  `describe_` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色描述',
  `status` bit(1) DEFAULT b'1' COMMENT '状态',
  `readonly` bit(1) DEFAULT b'0' COMMENT '是否内置角色',
  `ds_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据范围("全部_ALL", "本级_THIS_LEVEL", "本级以及子级_THIS_LEVEL_CHILDREN", "自定义_CUSTOMIZE", "个人_SELF",)',
  `create_user` bigint DEFAULT '0' COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint DEFAULT '0' COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `I_ROLE_TENANT_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户角色';

-- ----------------------------
-- Table structure for c_auth_role_authority
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_role_authority`;
CREATE TABLE `c_auth_role_authority` (
  `id` bigint NOT NULL COMMENT '主键',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `authority_id` bigint NOT NULL COMMENT '资源id\n#c_auth_resource\n#c_auth_menu',
  `authority_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role_id` bigint NOT NULL COMMENT '角色id\n#c_auth_role',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint DEFAULT '0' COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX_KEY` (`role_id`,`authority_type`,`authority_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色的资源';

-- ----------------------------
-- Table structure for c_auth_role_org
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_role_org`;
CREATE TABLE `c_auth_role_org` (
  `id` bigint NOT NULL COMMENT 'ID',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role_id` bigint DEFAULT NULL COMMENT '角色ID\n#c_auth_role',
  `org_id` bigint DEFAULT NULL COMMENT '部门ID\n#c_core_org',
  `create_time` datetime DEFAULT NULL,
  `create_user` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `I_AUTH_ROLE_ORG_ROLEID` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色组织关系';

-- ----------------------------
-- Table structure for c_auth_user
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_user`;
CREATE TABLE `c_auth_user` (
  `id` bigint NOT NULL COMMENT 'ID',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属租户',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `org_id` bigint DEFAULT NULL COMMENT '组织ID\n#c_core_org\n@InjectionField(api = ORG_ID_CLASS, method = ORG_ID_METHOD, beanClass = Org.class) RemoteData<Long, Org>',
  `station_id` bigint DEFAULT NULL COMMENT '岗位ID\n#c_core_station\n@InjectionField(api = STATION_ID_CLASS, method = STATION_ID_NAME_METHOD) RemoteData<Long, String>',
  `readonly` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否内置',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `sex` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
  `status` bit(1) DEFAULT b'0' COMMENT '状态 \n1启用 0禁用',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `nation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `education` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `position_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `work_describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password_error_last_time` datetime DEFAULT NULL COMMENT '最后一次输错密码时间',
  `password_error_num` int DEFAULT '0' COMMENT '密码错误次数',
  `password_expire_time` datetime DEFAULT NULL COMMENT '密码过期时间',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_user` bigint DEFAULT '0' COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint DEFAULT '0' COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `latest_access_time` date DEFAULT NULL COMMENT '最新访问日期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `I_AUTH_USER_TC` (`tenant_code`,`account`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户管理员表';

-- ----------------------------
-- Table structure for c_auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_user_role`;
CREATE TABLE `c_auth_user_role` (
  `id` bigint NOT NULL,
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据所属租户',
  `role_id` bigint NOT NULL DEFAULT '0' COMMENT '角色ID\n#c_auth_role',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户ID\n#c_core_accou',
  `create_user` bigint DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `I_AUTH_USER_ROLE_USERID` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色分配\r\n账号角色绑定';

-- ----------------------------
-- Table structure for c_auth_user_token
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_user_token`;
CREATE TABLE `c_auth_user_token` (
  `id` bigint NOT NULL COMMENT 'ID',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `client_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `token` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'token',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` bigint DEFAULT NULL COMMENT '登录人ID',
  `update_time` datetime DEFAULT NULL,
  `update_user` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='token';

-- ----------------------------
-- Table structure for c_common_area
-- ----------------------------
DROP TABLE IF EXISTS `c_common_area`;
CREATE TABLE `c_common_area` (
  `id` bigint NOT NULL COMMENT 'id',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sort_value` int DEFAULT '1' COMMENT '排序',
  `longitude` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `latitude` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `source_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_id` bigint DEFAULT '0' COMMENT '父ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint DEFAULT '0' COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint DEFAULT '0' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UN_CODE` (`tenant_code`,`code`) USING BTREE,
  KEY `IDX_PARENT_ID` (`parent_id`) USING BTREE COMMENT '查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地区表';

-- ----------------------------
-- Table structure for c_common_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `c_common_dictionary`;
CREATE TABLE `c_common_dictionary` (
  `id` bigint NOT NULL,
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type_` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `describe_` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status_` bit(1) DEFAULT b'1' COMMENT '状态',
  `create_user` bigint DEFAULT '0' COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint DEFAULT '0' COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `I_COMMON_DICTONARY_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型';

-- ----------------------------
-- Table structure for c_common_dictionary_item
-- ----------------------------
DROP TABLE IF EXISTS `c_common_dictionary_item`;
CREATE TABLE `c_common_dictionary_item` (
  `id` bigint NOT NULL COMMENT 'ID',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dictionary_id` bigint DEFAULT NULL COMMENT '类型ID',
  `dictionary_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status_` bit(1) DEFAULT b'1' COMMENT '状态',
  `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sort_value` int DEFAULT '1' COMMENT '排序',
  `create_user` bigint DEFAULT '0' COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint DEFAULT '0' COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `dict_code_item_code_uniq` (`dictionary_type`,`code`) USING BTREE COMMENT '字典编码与字典项目编码联合唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典项';

-- ----------------------------
-- Table structure for c_common_login_log
-- ----------------------------
DROP TABLE IF EXISTS `c_common_login_log`;
CREATE TABLE `c_common_login_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `request_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` bigint DEFAULT NULL COMMENT '登录人ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_date` date DEFAULT NULL COMMENT '登录时间',
  `ua` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `browser_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operating_system` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX_BROWSER` (`browser`) USING BTREE,
  KEY `IDX_OPERATING` (`operating_system`) USING BTREE,
  KEY `IDX_LOGIN_DATE` (`login_date`,`account`) USING BTREE,
  KEY `IDX_ACCOUNT_IP` (`account`,`request_ip`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志';

-- ----------------------------
-- Table structure for c_common_opt_log
-- ----------------------------
DROP TABLE IF EXISTS `c_common_opt_log`;
CREATE TABLE `c_common_opt_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `request_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `class_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `action_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `request_uri` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `http_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `params` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '请求参数',
  `result` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '返回值',
  `ex_desc` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '异常详情信息',
  `ex_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '异常描述',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `finish_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `consuming_time` bigint DEFAULT '0' COMMENT '消耗时间',
  `ua` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_type` (`type`) USING BTREE COMMENT '日志类型',
  KEY `I_COMMON_OPT_LOG_TC` (`tenant_code`) USING BTREE,
  KEY `I_COMMON_OPT_LOG_REQUEST_URI` (`request_uri`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志';

-- ----------------------------
-- Table structure for c_common_parameter
-- ----------------------------
DROP TABLE IF EXISTS `c_common_parameter`;
CREATE TABLE `c_common_parameter` (
  `id` bigint NOT NULL,
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `key_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status_` bit(1) DEFAULT b'1' COMMENT '状态',
  `readonly_` bit(1) DEFAULT NULL COMMENT '只读',
  `create_user` bigint DEFAULT '0' COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint DEFAULT '0' COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UN_KEY` (`tenant_code`,`key_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='参数配置';

-- ----------------------------
-- Table structure for c_core_org
-- ----------------------------
DROP TABLE IF EXISTS `c_core_org`;
CREATE TABLE `c_core_org` (
  `id` bigint NOT NULL COMMENT 'ID',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type_` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '01' COMMENT '类型\n@InjectionField(api = DICTIONARY_ITEM_CLASS, method = DICTIONARY_ITEM_METHOD, dictType = DictionaryType.ORG_TYPE)',
  `abbreviation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_id` bigint DEFAULT '0' COMMENT '父ID',
  `tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sort_value` int DEFAULT '1' COMMENT '排序',
  `status` bit(1) DEFAULT b'1' COMMENT '状态',
  `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` bigint DEFAULT NULL,
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `class_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `head` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `detailed_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `readonly` bit(1) DEFAULT b'0' COMMENT '是否内置',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `I_CORE_ORG_TC` (`tenant_code`) USING BTREE,
  FULLTEXT KEY `FU_PATH` (`tree_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织';

-- ----------------------------
-- Table structure for c_core_station
-- ----------------------------
DROP TABLE IF EXISTS `c_core_station`;
CREATE TABLE `c_core_station` (
  `id` bigint NOT NULL COMMENT 'ID',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `org_id` bigint DEFAULT '0' COMMENT '组织ID\n#c_core_org',
  `status` bit(1) DEFAULT b'1' COMMENT '状态',
  `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位';

-- ----------------------------
-- Table structure for d_datasource_config
-- ----------------------------
DROP TABLE IF EXISTS `d_datasource_config`;
CREATE TABLE `d_datasource_config` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `driver_class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据源';

-- ----------------------------
-- Table structure for d_global_user
-- ----------------------------
DROP TABLE IF EXISTS `d_global_user`;
CREATE TABLE `d_global_user` (
  `id` bigint NOT NULL COMMENT 'ID',
  `tenant_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户(admin)',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '管理员手机号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
  `readonly` bit(1) DEFAULT b'0' COMMENT '是否内置',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  `global_user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '全局用户类型 admin, cms ，第三方客户 third_party_customers ',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `enterprise` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '企业',
  `enterprise_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '企业logo ',
  `user_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户备注',
  `user_deleted` tinyint(1) DEFAULT '0' COMMENT '删除标记',
  `first_login` int DEFAULT NULL COMMENT '客户是否已经首次登录',
  `latest_access_time` date DEFAULT NULL COMMENT '最新访问日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全局账号';

-- ----------------------------
-- Table structure for d_global_user_tenant
-- ----------------------------
DROP TABLE IF EXISTS `d_global_user_tenant`;
CREATE TABLE `d_global_user_tenant` (
  `id` bigint NOT NULL COMMENT 'ID',
  `account_id` bigint NOT NULL COMMENT '账号ID',
  `tenant_id` bigint NOT NULL COMMENT '项目租户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  `management_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '授权项目(authorized_projects),自建项目(created_projects)',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `I_GLOBAL_USER_TENANT_ACCOUNTID` (`account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户项目管理表';

-- ----------------------------
-- Table structure for d_tenant
-- ----------------------------
DROP TABLE IF EXISTS `d_tenant`;
CREATE TABLE `d_tenant` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `connect_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `readonly` bit(1) DEFAULT b'0' COMMENT '是否内置',
  `duty` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `db_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `linkman` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `linkman_mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `linkman_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `examine_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `examine_user_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `examine_time` datetime DEFAULT NULL COMMENT '审核时间',
  `examine_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UN_CODE` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户';

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'increment id',
  `branch_id` bigint NOT NULL COMMENT 'branch transaction id',
  `xid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'global transaction id',
  `context` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` datetime NOT NULL COMMENT 'create datetime',
  `log_modified` datetime NOT NULL COMMENT 'modify datetime',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='AT transaction mode undo table';

-- ============================================================================
-- 初始数据（来自 caring_base_0000.sql，适配新表结构）
-- ============================================================================

-- ----------------------------
-- Records of c_auth_application
-- ----------------------------
BEGIN;
INSERT INTO `c_auth_application` (`id`, `tenant_code`, `client_id`, `client_secret`, `website`, `name`, `icon`, `app_type`, `describe_`, `status`, `create_user`, `create_time`, `update_user`, `update_time`) 
VALUES 
(1, '0000', 'caring_ui', 'caring_ui_secret', 'http://tangyh.top:10000/caring-ui/', 'SaaS微服务管理后台', '', 'PC', '内置', b'1', 1, '2020-04-02 15:05:14', 1, '2020-04-02 15:05:17'),
(2, '0000', 'caring_admin_ui', 'caring_admin_ui_secret', 'http://tangyh.top:180/caring-admin-ui/', 'SaaS微服务超级管理后台', '', 'PC', '内置', b'1', 1, '2020-04-02 15:05:22', 1, '2020-04-02 15:05:19');
COMMIT;

-- ----------------------------
-- Records of c_auth_menu
-- ----------------------------
BEGIN;
INSERT INTO `c_auth_menu` (`id`, `tenant_code`, `label`, `describe_`, `is_public`, `path`, `component`, `is_enable`, `sort_value`, `icon`, `group_`, `parent_id`, `create_user`, `create_time`, `update_user`, `update_time`) 
VALUES 
(101, '0000', '用户中心', '用户组织机构', b'0', '/user', 'Layout', b'1', 1, 'el-icon-user-solid', '', 0, 1, '2019-07-25 15:35:12', 3, '2019-11-11 14:32:02'),
(102, '0000', '权限管理', '管理权限相关', b'0', '/auth', 'Layout', b'1', 2, 'el-icon-lock', '', 0, 1, '2019-07-27 11:48:49', 3, '2019-11-11 14:35:39'),
(103, '0000', '基础配置', '基础的配置', b'0', '/base', 'Layout', b'1', 3, 'el-icon-set-up', '', 0, 1, '2019-11-11 14:38:29', 3, '2019-11-11 14:35:42'),
(104, '0000', '开发者管理', '开发者', b'0', '/developer', 'Layout', b'1', 4, 'el-icon-user-solid', '', 0, 1, '2019-11-11 14:38:34', 3, '2019-11-11 14:35:44'),
(105, '0000', '消息中心', '站内信', b'0', '/msgs', 'Layout', b'1', 5, 'el-icon-chat-line-square', '', 0, 1, '2019-11-11 14:38:32', 3, '2019-11-11 14:35:47'),
(106, '0000', '短信中心', '短信接口', b'0', '/sms', 'Layout', b'1', 6, 'el-icon-chat-line-round', '', 0, 1, '2019-11-11 14:38:36', 3, '2019-11-11 14:35:49'),
(107, '0000', '文件中心', '附件接口', b'0', '/file', 'Layout', b'1', 7, 'el-icon-folder-add', '', 0, 1, '2019-11-11 14:38:38', 3, '2019-11-11 14:35:51'),
(603976297063910529, '0000', '菜单配置', '', b'0', '/auth/menu', 'caring/auth/menu/Index', b'1', 0, '', '', 102, 1, '2019-07-25 15:46:11', 3, '2019-11-11 14:31:52'),
(603981723864141121, '0000', '角色管理', '', b'0', '/auth/role', 'caring/auth/role/Index', b'1', 1, '', '', 102, 1, '2019-07-25 16:07:45', 3, '2019-11-11 14:31:57'),
(603982542332235201, '0000', '组织管理', '', b'0', '/user/org', 'caring/user/org/Index', b'1', 0, '', '', 101, 1, '2019-07-25 16:11:00', 3, '2019-11-11 14:28:40'),
(603982713849908801, '0000', '岗位管理', '', b'0', '/user/station', 'caring/user/station/Index', b'1', 1, '', '', 101, 1, '2019-07-25 16:11:41', 3, '2019-11-11 14:28:43'),
(603983082961243905, '0000', '用户管理', '', b'0', '/user/user', 'caring/user/user/Index', b'1', 2, '', '', 101, 1, '2019-07-25 16:13:09', 3, '2019-11-11 14:28:49'),
(605078371293987105, '0000', '数据字典维护', '', b'0', '/base/dict', 'caring/base/dict/Index', b'1', 0, '', '', 103, 1, '2019-07-28 16:45:26', 3, '2019-11-11 14:34:23'),
(605078463069552993, '0000', '地区信息维护', '', b'0', '/base/area', 'caring/base/area/Index', b'1', 1, '', '', 103, 1, '2019-07-28 16:45:48', 3, '2019-11-11 14:34:26'),
(605078538881597857, '0000', '应用管理', '', b'0', '/developer/application', 'caring/developer/application/Index', b'1', 0, '', '', 104, 1, '2019-07-28 16:46:06', 3, '2019-12-25 16:19:43'),
(605078672772170209, '0000', '操作日志', '', b'0', '/developer/optLog', 'caring/developer/optLog/Index', b'1', 3, '', '', 104, 1, '2019-07-28 16:46:38', 3, '2019-11-11 14:35:14'),
(605078979149300257, '0000', '数据库监控', '', b'0', '/developer/db', 'caring/developer/db/Index', b'1', 2, '', '', 104, 1, '2019-07-28 16:47:51', 3, '2019-11-16 16:35:50'),
(605079239015793249, '0000', '接口文档', '', b'0', 'http://127.0.0.1:8760/api/gate/doc.html', 'Layout', b'1', 5, '', '', 104, 1, '2019-07-28 16:48:53', 3, '2019-11-16 10:55:03'),
(605079411338773153, '0000', '注册&配置中心', '', b'0', 'http://127.0.0.1:8848/nacos', 'Layout', b'1', 6, '', '', 104, 1, '2019-07-28 16:49:34', 3, '2019-11-16 10:55:06'),
(605079545585861345, '0000', '缓存监控', '', b'0', 'http://www.baidu.com', 'Layout', b'1', 7, '', '', 104, 1, '2019-07-28 16:50:06', 3, '2019-11-16 10:55:08'),
(605079658416833313, '0000', '服务器监控', '', b'0', 'http://127.0.0.1:8762/caring-monitor', 'Layout', b'1', 8, '', '', 104, 1, '2019-07-28 16:50:33', 3, '2019-11-16 10:55:15'),
(605079751035454305, '0000', '消息推送', '', b'0', '/msgs/sendMsgs', 'caring/msgs/sendMsgs/Index', b'1', 0, '', '', 105, 1, '2019-07-28 16:50:55', 3, '2019-11-11 14:28:30'),
(605080023753294753, '0000', '我的消息', '', b'0', '/msgs/myMsgs', 'caring/msgs/myMsgs/Index', b'1', 1, '', '', 105, 1, '2019-07-28 16:52:00', 3, '2019-11-11 14:28:27'),
(605080107379327969, '0000', '账号配置', '', b'0', '/sms/template', 'caring/sms/template/Index', b'1', 1, '', '', 106, 1, '2019-07-28 16:52:20', 3, '2019-11-21 19:53:17'),
(605080359394083937, '0000', '短信管理', '', b'0', '/sms/manage', 'caring/sms/manage/Index', b'1', 0, '', '', 106, 1, '2019-07-28 16:53:20', 3, '2019-11-21 19:53:09'),
(605080648767505601, '0000', '附件列表', '', b'0', '/file/attachment', 'caring/file/attachment/Index', b'1', 0, '', '', 107, 1, '2019-07-28 16:54:29', 3, '2019-11-11 14:28:07'),
(605080816296396097, '0000', '定时调度中心', '', b'0', 'http://127.0.0.1:8767/caring-jobs-server', 'Layout', b'1', 9, '', '', 104, 1, '2019-07-28 16:55:09', 3, '2019-11-16 10:55:18'),
(643464272629728001, '0000', '务必详看', '', b'1', '/doc', 'caring/doc/Index', b'1', 0, 'el-icon-notebook-1', '', 0, 3, '2019-11-11 14:57:18', 3, '2019-11-11 15:01:31'),
(643464392888812545, '0000', '后端代码', '', b'1', 'https://github.com/caring/caring-admin-cloud', 'Layout', b'1', 1, '', '', 643464272629728001, 3, '2019-11-11 14:57:46', 3, '2019-11-11 15:00:05'),
(643464517879071841, '0000', '租户平台-前端代码', '', b'1', 'https://github.com/caring/caring-ui', 'Layout', b'1', 2, '', '', 643464272629728001, 3, '2019-11-11 14:58:16', 3, '2019-11-11 15:00:09'),
(643464706228487361, '0000', '运营平台-前端代码', '', b'1', 'https://github.com/caring/caring-admin-ui', 'Layout', b'1', 3, '', '', 643464272629728001, 3, '2019-11-11 14:59:01', 3, '2019-11-11 15:00:11'),
(643464953478514081, '0000', '在线文档', '', b'1', 'https://www.kancloud.cn/caring/caring-admin-cloud', 'Layout', b'1', 0, '', '', 643464272629728001, 3, '2019-11-11 15:00:00', 3, '2019-11-11 15:01:36'),
(643874916004790785, '0000', '运营平台演示地址', NULL, b'1', 'http://127.0.0.1:8081/caring-admin-ui', 'Layout', b'1', 4, NULL, NULL, 643464272629728001, 3, '2019-11-12 18:09:03', 641577229343523041, '2019-12-04 16:20:13'),
(644111530555611361, '0000', '链路调用监控', '', b'0', 'http://127.0.0.1:8772/zipkin', 'Layout', b'1', 10, '', '', 104, 3, '2019-11-13 09:49:16', 3, '2019-11-13 09:56:51'),
(645215230518909025, '0000', '登录日志', '', b'0', '/developer/loginLog', 'caring/developer/loginLog/Index', b'1', 4, '', '', 104, 3, '2019-11-16 10:54:59', 3, '2019-11-16 10:54:59'),
(1225042542827929600, '0000', '参数配置', '', b'0', '/base/parameter', 'caring/base/parameter/Index', b'1', 3, '', '', 103, 3, '2020-02-05 21:04:37', 3, '2020-02-05 21:04:37');
COMMIT;

-- ----------------------------
-- Records of c_auth_resource
-- ----------------------------
BEGIN;
INSERT INTO `c_auth_resource` (`id`, `tenant_code`, `code`, `name`, `menu_id`, `describe_`, `create_user`, `create_time`, `update_user`, `update_time`) 
VALUES 
(643444685339100193, '0000', 'org:add', '添加', 603982542332235201, '', 3, '2019-11-11 13:39:28', 3, '2019-11-11 13:39:50'),
(643444685339100194, '0000', 'role:config', '配置', 603981723864141121, '', 3, '2019-11-11 13:39:28', 3, '2019-11-11 13:39:50'),
(643444685339100195, '0000', 'resource:add', '添加', 603976297063910529, '', 3, '2019-11-11 13:39:28', 3, '2019-11-11 13:39:50'),
(643444685339100196, '0000', 'resource:update', '修改', 603976297063910529, '', 3, '2020-04-05 19:02:42', 3, '2020-04-05 19:02:46'),
(643444685339100197, '0000', 'resource:delete', '删除', 603976297063910529, '', 3, '2019-11-11 13:39:28', 3, '2019-11-11 13:39:50'),
(643444685339100198, '0000', 'resource:view', '查看', 603976297063910529, '', 3, '2020-04-05 19:02:42', 3, '2020-04-05 19:02:46'),
(643444819758154945, '0000', 'org:update', '修改', 603982542332235201, '', 3, '2019-11-11 13:40:00', 3, '2019-11-11 13:40:00'),
(643444858974897441, '0000', 'org:delete', '删除', 603982542332235201, '', 3, '2019-11-11 13:40:09', 3, '2019-11-11 13:40:09'),
(643444897201784193, '0000', 'org:view', '查询', 603982542332235201, '', 3, '2019-11-11 13:40:18', 3, '2019-11-11 13:40:18'),
(643444992357959137, '0000', 'org:import', '导入', 603982542332235201, '', 3, '2019-11-11 13:40:41', 3, '2019-11-11 13:40:41'),
(643445016773002817, '0000', 'org:export', '导出', 603982542332235201, '', 3, '2019-11-11 13:40:47', 3, '2019-11-11 13:40:47'),
(643445116756821697, '0000', 'station:add', '添加', 603982713849908801, '', 3, '2019-11-11 13:41:11', 3, '2019-11-11 13:41:11'),
(643445162915137313, '0000', 'station:update', '修改', 603982713849908801, '', 3, '2019-11-11 13:41:22', 3, '2019-11-11 13:41:22'),
(643445197954353025, '0000', 'station:delete', '删除', 603982713849908801, '', 3, '2019-11-11 13:41:30', 3, '2019-11-11 13:41:30'),
(643445229575210977, '0000', 'station:view', '查看', 603982713849908801, '', 3, '2019-11-11 13:41:38', 3, '2019-11-11 13:41:38'),
(643445262110427201, '0000', 'station:export', '导出', 603982713849908801, '', 3, '2019-11-11 13:41:45', 3, '2019-11-11 13:41:45'),
(643445283996305569, '0000', 'station:import', '导入', 603982713849908801, '', 3, '2019-11-11 13:41:51', 3, '2019-11-11 13:41:51'),
(643445352703199521, '0000', 'user:add', '添加', 603983082961243905, '', 3, '2019-11-11 13:42:07', 3, '2019-11-11 13:42:07'),
(643445412774021505, '0000', 'user:update', '修改', 603983082961243905, '', 3, '2019-11-11 13:42:21', 3, '2019-11-11 13:42:21'),
(643445448081672673, '0000', 'user:delete', '删除', 603983082961243905, '', 3, '2019-11-11 13:42:30', 3, '2019-11-11 13:42:30'),
(643445477274028609, '0000', 'user:view', '查看', 603983082961243905, '', 3, '2019-11-11 13:42:37', 3, '2019-11-11 13:42:37'),
(643445514607528609, '0000', 'user:import', '导入', 603983082961243905, '', 3, '2019-11-11 13:42:46', 3, '2019-11-11 13:42:46'),
(643445542076025601, '0000', 'user:export', '导出', 603983082961243905, '', 3, '2019-11-11 13:42:52', 3, '2019-11-11 13:42:52'),
(643445641149680705, '0000', 'menu:add', '添加', 603976297063910529, '', 3, '2019-11-11 13:43:16', 3, '2019-11-11 13:43:16'),
(643445674330819745, '0000', 'menu:update', '修改', 603976297063910529, '', 3, '2019-11-11 13:43:24', 3, '2019-11-11 13:43:24'),
(643445704177487105, '0000', 'menu:delete', '删除', 603976297063910529, '', 3, '2019-11-11 13:43:31', 3, '2019-11-11 13:43:31'),
(643445747320098145, '0000', 'menu:view', '查看', 603976297063910529, '', 3, '2019-11-11 13:43:41', 3, '2019-11-11 13:43:41'),
(643445774687931841, '0000', 'menu:export', '导出', 603976297063910529, '', 3, '2019-11-11 13:43:48', 3, '2019-11-11 13:43:48'),
(643445802106097185, '0000', 'menu:import', '导入', 603976297063910529, '', 3, '2019-11-11 13:43:54', 3, '2019-11-11 13:43:54'),
(643448338154263521, '0000', 'role:add', '添加', 603981723864141121, '', 3, '2019-11-11 13:53:59', 3, '2019-11-11 13:53:59'),
(643448369779315777, '0000', 'role:update', '修改', 603981723864141121, '', 3, '2019-11-11 13:54:06', 3, '2019-11-11 13:54:06'),
(643448507767723169, '0000', 'role:delete', '删除', 603981723864141121, '', 3, '2019-11-11 13:54:39', 3, '2019-11-11 13:54:39'),
(643448611161511169, '0000', 'role:view', '查看', 603981723864141121, '', 3, '2019-11-11 13:55:04', 3, '2019-11-11 13:55:04'),
(643448656451605857, '0000', 'role:export', '导出', 603981723864141121, '', 3, '2019-11-11 13:55:15', 3, '2019-11-11 13:55:15'),
(643448730950833601, '0000', 'role:import', '导入', 603981723864141121, '', 3, '2019-11-11 13:55:32', 3, '2019-11-11 13:55:32'),
(643448826945869409, '0000', 'role:auth', '授权', 603981723864141121, '', 3, '2019-11-11 13:55:55', 3, '2019-11-11 13:55:55'),
(643449540959016737, '0000', 'dict:add', '添加', 605078371293987105, '', 3, '2019-11-11 13:58:45', 3, '2019-11-11 13:58:45'),
(643449573045442433, '0000', 'dict:update', '修改', 605078371293987105, '', 3, '2019-11-11 13:58:53', 3, '2019-11-11 13:58:53'),
(643449629173618657, '0000', 'dict:view', '查看', 605078371293987105, '', 3, '2019-11-11 13:59:07', 3, '2019-11-11 13:59:07'),
(643449944866297985, '0000', 'dict:delete', '删除', 605078371293987105, '', 3, '2019-11-11 14:00:22', 3, '2019-11-11 14:00:22'),
(643449998909905121, '0000', 'dict:export', '导出', 605078371293987105, '', 3, '2019-11-11 14:00:35', 3, '2019-11-11 14:00:35'),
(643450072595437889, '0000', 'dict:import', '导入', 605078371293987105, '', 3, '2019-11-11 14:00:52', 3, '2019-11-11 14:00:52'),
(643450171333548481, '0000', 'area:add', '添加', 605078463069552993, '', 3, '2019-11-11 14:01:16', 3, '2019-11-11 14:01:16'),
(643450210449627681, '0000', 'area:update', '修改', 605078463069552993, '', 3, '2019-11-11 14:01:25', 3, '2019-11-11 14:01:25'),
(643450295858240129, '0000', 'area:delete', '删除', 605078463069552993, '', 3, '2019-11-11 14:01:45', 3, '2019-11-11 14:01:45'),
(643450326619265761, '0000', 'area:view', '查看', 605078463069552993, '', 3, '2019-11-11 14:01:53', 3, '2019-11-11 14:01:53'),
(643450551291353921, '0000', 'area:export', '导出', 605078463069552993, '', 3, '2019-11-11 14:02:46', 3, '2019-11-11 14:02:46'),
(643450602218593185, '0000', 'area:import', '导入', 605078463069552993, '', 3, '2019-11-11 14:02:59', 3, '2019-11-11 14:02:59'),
(643450770317909249, '0000', 'optLog:view', '查看', 605078672772170209, '', 3, '2019-11-11 14:03:39', 3, '2019-11-11 14:03:39'),
(643450853134441825, '0000', 'optLog:export', '导出', 605078672772170209, '', 3, '2019-11-11 14:03:58', 3, '2019-11-11 14:03:58'),
(643451893279892129, '0000', 'msgs:view', '查看', 605080023753294753, '', 3, '2019-11-11 14:08:06', 3, '2019-11-11 14:08:06'),
(643451945020826369, '0000', 'msgs:delete', '删除', 605080023753294753, '', 3, '2019-11-11 14:08:19', 3, '2019-11-11 14:08:19'),
(643451994945626977, '0000', 'msgs:update', '修改', 605080023753294753, '', 3, '2019-11-11 14:08:31', 3, '2019-11-11 14:08:31'),
(643452086981239745, '0000', 'msgs:export', '导出', 605080023753294753, '', 3, '2019-11-11 14:08:53', 3, '2019-11-11 14:08:53'),
(643452393857492033, '0000', 'sms:template:add', '添加', 605080107379327969, '', 3, '2019-11-11 14:10:06', 3, '2019-11-11 14:10:06'),
(643452429496493217, '0000', 'sms:template:update', '修改', 605080107379327969, '', 3, '2019-11-11 14:10:14', 3, '2019-11-11 14:10:14'),
(643452458693043457, '0000', 'sms:template:view', '查看', 605080107379327969, '', 3, '2019-11-11 14:10:21', 3, '2019-11-11 14:10:28'),
(643452488447436129, '0000', 'sms:template:delete', '删除', 605080107379327969, '', 3, '2019-11-11 14:10:28', 3, '2019-11-11 14:10:28'),
(643452536954561985, '0000', 'sms:template:import', '导入', 605080107379327969, '', 3, '2019-11-11 14:10:40', 3, '2019-11-11 14:10:40'),
(643452571645650465, '0000', 'sms:template:export', '导入', 605080107379327969, '', 3, '2019-11-11 14:10:48', 3, '2019-11-11 14:10:48'),
(643454073500084577, '0000', 'sms:manage:add', '添加', 605080359394083937, '', 3, '2019-11-11 14:16:46', 3, '2019-11-11 14:16:46'),
(643454110992968129, '0000', 'sms:manage:update', '修改', 605080359394083937, '', 3, '2019-11-11 14:16:55', 3, '2019-11-11 14:16:55'),
(643454150905965089, '0000', 'sms:manage:view', '查看', 605080359394083937, '', 3, '2019-11-11 14:17:05', 3, '2019-11-11 14:17:05'),
(643454825052252961, '0000', 'sms:manage:delete', '删除', 605080359394083937, '', 3, '2019-11-11 14:19:45', 3, '2019-11-11 14:19:45'),
(643455175503129569, '0000', 'sms:manage:export', '导出', 605080359394083937, '', 3, '2019-11-11 14:21:09', 3, '2019-11-11 14:26:05'),
(643455720519380097, '0000', 'sms:manage:import', '导入', 605080359394083937, '', 3, '2019-11-11 14:23:19', 3, '2019-11-11 14:23:19'),
(643456690892582401, '0000', 'file:add', '添加', 605080648767505601, '', 3, '2019-11-11 14:27:10', 3, '2019-11-11 14:27:10'),
(643456724186967649, '0000', 'file:update', '修改', 605080648767505601, '', 3, '2019-11-11 14:27:18', 3, '2019-11-11 14:27:18'),
(643456761927315137, '0000', 'file:delete', '删除', 605080648767505601, '', 3, '2019-11-11 14:27:27', 3, '2019-11-11 14:27:27'),
(643456789920100129, '0000', 'file:view', '查看', 605080648767505601, '', 3, '2019-11-11 14:27:34', 3, '2019-11-11 14:27:34'),
(643456884694593409, '0000', 'file:download', '下载', 605080648767505601, '', 3, '2019-11-11 14:27:56', 3, '2019-11-11 14:27:56'),
(645288214990422241, '0000', 'optLog:delete', '删除', 605078672772170209, '', 3, '2019-11-16 15:45:00', 3, '2019-11-16 15:45:00'),
(645288283693121889, '0000', 'loginLog:delete', '删除', 645215230518909025, '', 3, '2019-11-16 15:45:16', 3, '2019-11-16 15:45:16'),
(645288375300915649, '0000', 'loginLog:export', '导出', 645215230518909025, '', 3, '2019-11-16 15:45:38', 3, '2019-11-16 15:45:38'),
(658002570005972001, '0000', 'msgs:add', '新增', 605080023753294753, '', 3, '2019-12-21 17:47:18', 3, '2019-12-21 17:47:18'),
(658002632467547265, '0000', 'msgs:mark', '标记已读', 605080023753294753, '', 3, '2019-12-21 17:47:33', 3, '2019-12-21 17:47:33'),
(659702641965662497, '0000', 'application:add', '新增', 605078538881597857, '', 3, '2019-12-26 10:22:47', 3, '2019-12-26 10:22:47'),
(659702674622513537, '0000', 'application:update', '修改', 605078538881597857, '', 3, '2019-12-26 10:22:55', 3, '2019-12-26 10:22:55'),
(659702756889592289, '0000', 'application:delete', '删除', 605078538881597857, '', 3, '2019-12-26 10:23:14', 3, '2019-12-26 10:23:14'),
(659702791312245313, '0000', 'application:view', '查看', 605078538881597857, '', 3, '2019-12-26 10:23:22', 3, '2019-12-26 10:23:22'),
(659702853945787041, '0000', 'application:export', '导出', 605078538881597857, '', 3, '2019-12-26 10:23:37', 3, '2019-12-26 10:23:37'),
(1225042691843162112, '0000', 'parameter:add', '添加', 1225042542827929600, '', 3, '2020-02-05 21:05:13', 3, '2020-02-05 21:05:13'),
(1225042821497487360, '0000', 'parameter:update', '修改', 1225042542827929600, '', 3, '2020-02-05 21:05:43', 3, '2020-02-05 21:05:43'),
(1225042949172101120, '0000', 'parameter:delete', '删除', 1225042542827929600, '', 3, '2020-02-05 21:06:14', 3, '2020-02-05 21:06:14'),
(1225043012455759872, '0000', 'parameter:export', '导出', 1225042542827929600, '', 3, '2020-02-05 21:06:29', 3, '2020-02-05 21:06:29'),
(1237035586045345792, '0000', 'parameter:import', '导入', 1225042542827929600, '', 3, '2020-03-09 23:20:41', 3, '2020-03-09 23:20:41'),
(1237035798587506688, '0000', 'msgs:import', '导入', 605080023753294753, '', 3, '2020-03-09 23:21:32', 3, '2020-03-09 23:21:32'),
(1246066924136169472, '0000', 'parameter:view', '查看', 1225042542827929600, '', 3, '2020-04-03 21:28:00', 3, '2020-04-03 21:28:00'),
(1246067318035841024, '0000', 'loginLog:view', '查看', 645215230518909025, '', 3, '2020-04-03 21:29:34', 3, '2020-04-03 21:29:34');
COMMIT;

-- ----------------------------
-- Records of c_auth_role
-- ----------------------------
BEGIN;
INSERT INTO `c_auth_role` (`id`, `tenant_code`, `name`, `code`, `describe_`, `status`, `readonly`, `ds_type`, `create_user`, `create_time`, `update_user`, `update_time`) 
VALUES 
(100, '0000', '平台管理员', 'PT_ADMIN', '平台内置管理员', b'1', b'1', 'ALL', 1, '2019-10-25 13:46:00', 3, '2020-02-13 11:05:28');
COMMIT;

-- ----------------------------
-- Records of c_auth_role_authority
-- ----------------------------
BEGIN;
INSERT INTO `c_auth_role_authority` (`id`, `tenant_code`, `authority_id`, `authority_type`, `role_id`, `create_time`, `create_user`) 
VALUES 
(1291627753673588736, '0000', 643445116756821697, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753686171648, '0000', 643444685339100197, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753690365952, '0000', 643445802106097185, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753690365953, '0000', 643448826945869409, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753690365954, '0000', 643444685339100196, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753694560256, '0000', 643450072595437889, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753694560257, '0000', 643444685339100198, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753698754560, '0000', 643452536954561985, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753698754561, '0000', 643444685339100193, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753702948864, '0000', 1225984359173980160, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753702948865, '0000', 643445514607528609, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753702948866, '0000', 643444685339100195, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753707143168, '0000', 659702853945787041, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753707143169, '0000', 643444685339100194, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753707143170, '0000', 1246066924136169472, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753707143171, '0000', 643448507767723169, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753707143172, '0000', 643450295858240129, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753711337472, '0000', 643449998909905121, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753711337473, '0000', 1225042949172101120, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753711337474, '0000', 643445197954353025, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753711337475, '0000', 645288283693121889, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753715531776, '0000', 1246067318035841024, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753715531777, '0000', 643444897201784193, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753715531778, '0000', 643448730950833601, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753715531779, '0000', 643456690892582401, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753715531780, '0000', 1232574830335754240, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753719726080, '0000', 643445412774021505, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753719726081, '0000', 1225043012455759872, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753719726082, '0000', 643445262110427201, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753723920384, '0000', 1237035586045345792, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753723920385, '0000', 643450853134441825, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753723920386, '0000', 643448656451605857, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753723920387, '0000', 643445774687931841, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753723920388, '0000', 643450171333548481, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753728114688, '0000', 1225984228617879552, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753728114689, '0000', 643456761927315137, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753728114690, '0000', 643445477274028609, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753728114691, '0000', 1225984257483079680, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753732308992, '0000', 659702756889592289, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753732308993, '0000', 643445352703199521, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753732308994, '0000', 643450551291353921, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753736503296, '0000', 643445747320098145, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753736503297, '0000', 643452458693043457, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753736503298, '0000', 643454110992968129, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753736503299, '0000', 643445016773002817, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753740697600, '0000', 643445162915137313, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753740697601, '0000', 643452571645650465, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753740697602, '0000', 643444858974897441, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753744891904, '0000', 643449944866297985, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753744891905, '0000', 643450770317909249, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753744891906, '0000', 1225984321857257472, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753744891907, '0000', 643455720519380097, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753749086208, '0000', 643450210449627681, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753749086209, '0000', 643444992357959137, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753753280512, '0000', 643456789920100129, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753753280513, '0000', 659702641965662497, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753757474816, '0000', 658002632467547265, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753757474817, '0000', 643445229575210977, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753757474818, '0000', 643445641149680705, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753757474819, '0000', 643444819758154945, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753761669120, '0000', 643449629173618657, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753761669121, '0000', 1225984389242945536, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753761669122, '0000', 643451945020826369, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753761669123, '0000', 643448369779315777, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753761669124, '0000', 643454825052252961, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753765863424, '0000', 643456884694593409, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753765863425, '0000', 643448338154263521, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753765863426, '0000', 643449573045442433, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753765863427, '0000', 1225042691843162112, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753765863428, '0000', 659702791312245313, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753770057728, '0000', 643445704177487105, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753770057729, '0000', 643452393857492033, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753770057730, '0000', 643452429496493217, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753770057731, '0000', 643452458693043457, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753770057732, '0000', 643452488447436129, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753770057733, '0000', 643452536954561985, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753770057734, '0000', 643452571645650465, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753774252032, '0000', 643454073500084577, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753774252033, '0000', 643454110992968129, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753774252034, '0000', 643454150905965089, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753774252035, '0000', 643454825052252961, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753774252036, '0000', 643455175503129569, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753774252037, '0000', 643455720519380097, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753778446336, '0000', 643451893279892129, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753778446337, '0000', 643451945020826369, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753778446338, '0000', 643451994945626977, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753778446339, '0000', 643452086981239745, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753778446340, '0000', 1237035798587506688, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753782640640, '0000', 643450171333548481, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753782640641, '0000', 643450210449627681, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753782640642, '0000', 643450295858240129, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753782640643, '0000', 643450326619265761, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753782640644, '0000', 643450551291353921, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753782640645, '0000', 643450602218593185, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753786834944, '0000', 643450770317909249, 'RESOURCE', 100, '2020-08-07 14:50:28', 3),
(1291627753786834945, '0000', 645288214990422241, 'RESOURCE', 100, '2020-08-07 14:50:28', 3);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;


-- ----------------------------
-- Table structure for a_appoint_config
-- ----------------------------
DROP TABLE IF EXISTS `a_appoint_config`;
CREATE TABLE `a_appoint_config` (
                                    `id` bigint NOT NULL COMMENT 'id',
                                    `organization_id` bigint DEFAULT NULL COMMENT '机构id',
                                    `doctor_id` bigint DEFAULT NULL COMMENT '医生id',
                                    `num_of_monday_morning` int DEFAULT NULL COMMENT '星期一上午人数',
                                    `num_of_monday_afternoon` int DEFAULT NULL COMMENT '星期一下午人数',
                                    `num_of_tuesday_morning` int DEFAULT NULL COMMENT '星期二上午人数',
                                    `num_of_tuesday_afternoon` int DEFAULT NULL COMMENT '星期二下午人数',
                                    `num_of_wednesday_morning` int DEFAULT NULL COMMENT '星期三上午人数',
                                    `num_of_wednesday_afternoon` int DEFAULT NULL COMMENT '星期三下午人数',
                                    `num_of_thursday_morning` int DEFAULT NULL COMMENT '星期四上午人数',
                                    `num_of_thursday_afternoon` int DEFAULT NULL COMMENT '星期四下午人数',
                                    `num_of_friday_morning` int DEFAULT NULL COMMENT '星期五上午人数',
                                    `num_of_friday_afternoon` int DEFAULT NULL COMMENT '星期五下午人数',
                                    `num_of_saturday_morning` int DEFAULT NULL COMMENT '星期六上午人数',
                                    `num_of_saturday_afternoon` int DEFAULT NULL COMMENT '星期六下午人数',
                                    `num_of_sunday_morning` int DEFAULT NULL COMMENT '星期日上午人数',
                                    `num_of_sunday_afternoon` int DEFAULT NULL COMMENT '星期日下午人数',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                    `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                    `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE,
                                    KEY `I_APPOINT_CONFIG_DOCTORID` (`doctor_id`,`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记录医生的周预约配置';

-- ----------------------------
-- Table structure for a_appointment
-- ----------------------------
DROP TABLE IF EXISTS `a_appointment`;
CREATE TABLE `a_appointment` (
                                 `id` bigint NOT NULL COMMENT '预约id',
                                 `organization_id` bigint DEFAULT NULL COMMENT '机构id',
                                 `patient_id` bigint DEFAULT NULL COMMENT '患者id',
                                 `patient_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `appoint_date` date DEFAULT NULL COMMENT '预约日期',
                                 `status` int DEFAULT NULL COMMENT '就诊状态  未就诊：0  已就诊：1,  取消就诊 2，审核中 -1，(预约失败)审核失败 3， 就诊过期：4',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `time` int DEFAULT NULL COMMENT '1 上午  2：下午',
                                 `doctor_id` bigint DEFAULT NULL COMMENT '医生id',
                                 `doctor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `group_id` bigint DEFAULT NULL COMMENT '小组Id',
                                 `audit_fail_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核失败原因ABOUT_FULL: 该时段已约满， OTHER: 其他',
                                 `appointment_sort` tinyint(1) DEFAULT NULL COMMENT '排序序号 待就诊0 ，审核中 1 ，预约失败 2 ，已就诊 3 ，已取消 4 ，已过期 5',
                                 `audit_fail_reason_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核失败原因说明',
                                 `patient_delete_mark` tinyint DEFAULT '0' COMMENT '患者删除标记 1：删除, 0: 未删除',
                                 `appointment_date` datetime DEFAULT NULL COMMENT '预约时间',
                                 `visit_time` datetime DEFAULT NULL COMMENT '就诊时间',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `appointment_doctor_id_idx` (`doctor_id`) USING BTREE COMMENT '医生id索引，便于快速统计医生约号数量',
                                 KEY `appointment_appoint_date_time_idx` (`appoint_date`,`time`) USING BTREE COMMENT '对创建日期和上下提供索引',
                                 KEY `I_APPOINTMENT_PATIENTID` (`patient_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者预约表';

-- ----------------------------
-- Table structure for ai_pod_allergy_keywords
-- ----------------------------
DROP TABLE IF EXISTS `ai_pod_allergy_keywords`;
CREATE TABLE `ai_pod_allergy_keywords` (
                                           `id` int NOT NULL AUTO_INCREMENT,
                                           `keyword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                           `category_cn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                           `sub_category_cn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                                           `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for c_auth_application
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_application`;
CREATE TABLE `c_auth_application` (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `client_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `client_secret` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `website` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `app_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `describe_` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `status` bit(1) DEFAULT b'1' COMMENT '是否启用',
                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      KEY `UN_APP_KEY` (`client_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用';

-- ----------------------------
-- Table structure for c_auth_menu
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_menu`;
CREATE TABLE `c_auth_menu` (
                               `id` bigint NOT NULL COMMENT '主键',
                               `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `label` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `describe_` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `is_public` bit(1) DEFAULT b'0' COMMENT '公共菜单\nTrue是无需分配所有人就可以访问的',
                               `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `is_enable` bit(1) DEFAULT b'1' COMMENT '状态',
                               `sort_value` int DEFAULT '1' COMMENT '排序',
                               `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `group_` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `parent_id` bigint DEFAULT '0' COMMENT '父级菜单ID',
                               `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                               `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE,
                               KEY `I_AUTH_MENU_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单';

-- ----------------------------
-- Table structure for c_auth_resource
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_resource`;
CREATE TABLE `c_auth_resource` (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `menu_id` bigint DEFAULT NULL COMMENT '菜单ID\n#c_auth_menu',
                                   `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `C_AUTH_RESOURCE_MENU_ID` (`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源';

-- ----------------------------
-- Table structure for c_auth_role
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_role`;
CREATE TABLE `c_auth_role` (
                               `id` bigint NOT NULL,
                               `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色所属租户',
                               `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色名称',
                               `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色编码',
                               `describe_` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色描述',
                               `status` bit(1) DEFAULT b'1' COMMENT '状态',
                               `readonly` bit(1) DEFAULT b'0' COMMENT '是否内置角色',
                               `ds_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据范围("全部_ALL", "本级_THIS_LEVEL", "本级以及子级_THIS_LEVEL_CHILDREN", "自定义_CUSTOMIZE", "个人_SELF",)',
                               `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                               `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE,
                               KEY `I_ROLE_TENANT_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户角色';

-- ----------------------------
-- Table structure for c_auth_role_authority
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_role_authority`;
CREATE TABLE `c_auth_role_authority` (
                                         `id` bigint NOT NULL COMMENT '主键',
                                         `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `authority_id` bigint NOT NULL COMMENT '资源id\n#c_auth_resource\n#c_auth_menu',
                                         `authority_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `role_id` bigint NOT NULL COMMENT '角色id\n#c_auth_role',
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         `create_user` bigint DEFAULT '0' COMMENT '创建人',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `IDX_KEY` (`role_id`,`authority_type`,`authority_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色的资源';

-- ----------------------------
-- Table structure for c_auth_role_org
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_role_org`;
CREATE TABLE `c_auth_role_org` (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `role_id` bigint DEFAULT NULL COMMENT '角色ID\n#c_auth_role',
                                   `org_id` bigint DEFAULT NULL COMMENT '部门ID\n#c_core_org',
                                   `create_time` datetime DEFAULT NULL,
                                   `create_user` bigint DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `I_AUTH_ROLE_ORG_ROLEID` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色组织关系';

-- ----------------------------
-- Table structure for c_auth_user
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_user`;
CREATE TABLE `c_auth_user` (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属租户',
                               `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号',
                               `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
                               `org_id` bigint DEFAULT NULL COMMENT '组织ID\n#c_core_org\n@InjectionField(api = ORG_ID_CLASS, method = ORG_ID_METHOD, beanClass = Org.class) RemoteData<Long, Org>',
                               `station_id` bigint DEFAULT NULL COMMENT '岗位ID\n#c_core_station\n@InjectionField(api = STATION_ID_CLASS, method = STATION_ID_NAME_METHOD) RemoteData<Long, String>',
                               `readonly` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否内置',
                               `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
                               `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
                               `sex` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
                               `status` bit(1) DEFAULT b'0' COMMENT '状态 \n1启用 0禁用',
                               `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
                               `nation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `education` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `position_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `work_describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `password_error_last_time` datetime DEFAULT NULL COMMENT '最后一次输错密码时间',
                               `password_error_num` int DEFAULT '0' COMMENT '密码错误次数',
                               `password_expire_time` datetime DEFAULT NULL COMMENT '密码过期时间',
                               `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
                               `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
                               `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                               `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                               `latest_access_time` date DEFAULT NULL COMMENT '最新访问日期',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE KEY `I_AUTH_USER_TC` (`tenant_code`,`account`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户管理员表';

-- ----------------------------
-- Table structure for c_auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_user_role`;
CREATE TABLE `c_auth_user_role` (
                                    `id` bigint NOT NULL,
                                    `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据所属租户',
                                    `role_id` bigint NOT NULL DEFAULT '0' COMMENT '角色ID\n#c_auth_role',
                                    `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户ID\n#c_core_accou',
                                    `create_user` bigint DEFAULT NULL COMMENT '创建人ID',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    KEY `I_AUTH_USER_ROLE_USERID` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色分配\r\n账号角色绑定';

-- ----------------------------
-- Table structure for c_auth_user_token
-- ----------------------------
DROP TABLE IF EXISTS `c_auth_user_token`;
CREATE TABLE `c_auth_user_token` (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `client_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `token` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'token',
                                     `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
                                     `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `create_time` datetime DEFAULT NULL,
                                     `create_user` bigint DEFAULT NULL COMMENT '登录人ID',
                                     `update_time` datetime DEFAULT NULL,
                                     `update_user` bigint DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='token';

-- ----------------------------
-- Table structure for c_common_area
-- ----------------------------
DROP TABLE IF EXISTS `c_common_area`;
CREATE TABLE `c_common_area` (
                                 `id` bigint NOT NULL COMMENT 'id',
                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `sort_value` int DEFAULT '1' COMMENT '排序',
                                 `longitude` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `latitude` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `source_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `parent_id` bigint DEFAULT '0' COMMENT '父ID',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `create_user` bigint DEFAULT '0' COMMENT '创建人',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 `update_user` bigint DEFAULT '0' COMMENT '更新人',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE KEY `UN_CODE` (`tenant_code`,`code`) USING BTREE,
                                 KEY `IDX_PARENT_ID` (`parent_id`) USING BTREE COMMENT '查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地区表';

-- ----------------------------
-- Table structure for c_common_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `c_common_dictionary`;
CREATE TABLE `c_common_dictionary` (
                                       `id` bigint NOT NULL,
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `type_` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `describe_` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `status_` bit(1) DEFAULT b'1' COMMENT '状态',
                                       `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_COMMON_DICTONARY_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型';

-- ----------------------------
-- Table structure for c_common_dictionary_item
-- ----------------------------
DROP TABLE IF EXISTS `c_common_dictionary_item`;
CREATE TABLE `c_common_dictionary_item` (
                                            `id` bigint NOT NULL COMMENT 'ID',
                                            `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `dictionary_id` bigint DEFAULT NULL COMMENT '类型ID',
                                            `dictionary_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `status_` bit(1) DEFAULT b'1' COMMENT '状态',
                                            `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `sort_value` int DEFAULT '1' COMMENT '排序',
                                            `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                            `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                            PRIMARY KEY (`id`) USING BTREE,
                                            KEY `dict_code_item_code_uniq` (`dictionary_type`,`code`) USING BTREE COMMENT '字典编码与字典项目编码联合唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典项';

-- ----------------------------
-- Table structure for c_common_login_log
-- ----------------------------
DROP TABLE IF EXISTS `c_common_login_log`;
CREATE TABLE `c_common_login_log` (
                                      `id` bigint NOT NULL COMMENT '主键',
                                      `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `request_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `user_id` bigint DEFAULT NULL COMMENT '登录人ID',
                                      `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `login_date` date DEFAULT NULL COMMENT '登录时间',
                                      `ua` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `browser_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `operating_system` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `create_time` datetime DEFAULT NULL,
                                      `create_user` bigint DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE,
                                      KEY `IDX_BROWSER` (`browser`) USING BTREE,
                                      KEY `IDX_OPERATING` (`operating_system`) USING BTREE,
                                      KEY `IDX_LOGIN_DATE` (`login_date`,`account`) USING BTREE,
                                      KEY `IDX_ACCOUNT_IP` (`account`,`request_ip`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志';

-- ----------------------------
-- Table structure for c_common_opt_log
-- ----------------------------
DROP TABLE IF EXISTS `c_common_opt_log`;
CREATE TABLE `c_common_opt_log` (
                                    `id` bigint NOT NULL COMMENT '主键',
                                    `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `request_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `type` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `class_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `action_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `request_uri` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `http_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `params` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '请求参数',
                                    `result` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '返回值',
                                    `ex_desc` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '异常详情信息',
                                    `ex_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '异常描述',
                                    `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
                                    `finish_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
                                    `consuming_time` bigint DEFAULT '0' COMMENT '消耗时间',
                                    `ua` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `create_time` datetime DEFAULT NULL,
                                    `create_user` bigint DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE,
                                    KEY `index_type` (`type`) USING BTREE COMMENT '日志类型',
                                    KEY `I_COMMON_OPT_LOG_TC` (`tenant_code`) USING BTREE,
                                    KEY `I_COMMON_OPT_LOG_REQUEST_URI` (`request_uri`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志';

-- ----------------------------
-- Table structure for c_common_parameter
-- ----------------------------
DROP TABLE IF EXISTS `c_common_parameter`;
CREATE TABLE `c_common_parameter` (
                                      `id` bigint NOT NULL,
                                      `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `key_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `status_` bit(1) DEFAULT b'1' COMMENT '状态',
                                      `readonly_` bit(1) DEFAULT NULL COMMENT '只读',
                                      `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE KEY `UN_KEY` (`tenant_code`,`key_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='参数配置';

-- ----------------------------
-- Table structure for c_core_org
-- ----------------------------
DROP TABLE IF EXISTS `c_core_org`;
CREATE TABLE `c_core_org` (
                              `id` bigint NOT NULL COMMENT 'ID',
                              `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `type_` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '01' COMMENT '类型\n@InjectionField(api = DICTIONARY_ITEM_CLASS, method = DICTIONARY_ITEM_METHOD, dictType = DictionaryType.ORG_TYPE)',
                              `abbreviation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `parent_id` bigint DEFAULT '0' COMMENT '父ID',
                              `tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `sort_value` int DEFAULT '1' COMMENT '排序',
                              `status` bit(1) DEFAULT b'1' COMMENT '状态',
                              `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `create_time` datetime DEFAULT NULL,
                              `create_user` bigint DEFAULT NULL,
                              `update_time` datetime DEFAULT NULL,
                              `update_user` bigint DEFAULT NULL,
                              `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `class_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `head` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `tel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `detailed_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `readonly` bit(1) DEFAULT b'0' COMMENT '是否内置',
                              PRIMARY KEY (`id`) USING BTREE,
                              KEY `I_CORE_ORG_TC` (`tenant_code`) USING BTREE,
                              FULLTEXT KEY `FU_PATH` (`tree_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织';

-- ----------------------------
-- Table structure for c_core_station
-- ----------------------------
DROP TABLE IF EXISTS `c_core_station`;
CREATE TABLE `c_core_station` (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `org_id` bigint DEFAULT '0' COMMENT '组织ID\n#c_core_org',
                                  `status` bit(1) DEFAULT b'1' COMMENT '状态',
                                  `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `create_time` datetime DEFAULT NULL,
                                  `create_user` bigint DEFAULT NULL,
                                  `update_time` datetime DEFAULT NULL,
                                  `update_user` bigint DEFAULT NULL,
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位';

-- ----------------------------
-- Table structure for city
-- ----------------------------
DROP TABLE IF EXISTS `city`;
CREATE TABLE `city` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                        `city_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                        `province_id` bigint NOT NULL,
                        `province_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                        `province_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                        `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否删除(0-已删除 1-正常)',
                        `create_user` bigint DEFAULT NULL COMMENT '创建人',
                        `update_user` bigint DEFAULT NULL COMMENT '修改人',
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=357 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='行政区域地州市信息表';

-- ----------------------------
-- Table structure for d_datasource_config
-- ----------------------------
DROP TABLE IF EXISTS `d_datasource_config`;
CREATE TABLE `d_datasource_config` (
                                       `id` bigint NOT NULL COMMENT '主键ID',
                                       `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `driver_class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                       `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                       `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据源';

-- ----------------------------
-- Table structure for d_global_user
-- ----------------------------
DROP TABLE IF EXISTS `d_global_user`;
CREATE TABLE `d_global_user` (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `tenant_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户(admin)',
                                 `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号',
                                 `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '管理员手机号',
                                 `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
                                 `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
                                 `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
                                 `readonly` bit(1) DEFAULT b'0' COMMENT '是否内置',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                 `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                 `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                 `global_user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '全局用户类型 admin, cms ，第三方客户 third_party_customers ',
                                 `login_time` datetime DEFAULT NULL COMMENT '登录时间',
                                 `enterprise` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '企业',
                                 `enterprise_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '企业logo ',
                                 `user_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户备注',
                                 `user_deleted` tinyint(1) DEFAULT '0' COMMENT '删除标记',
                                 `first_login` int DEFAULT NULL COMMENT '客户是否已经首次登录',
                                 `latest_access_time` date DEFAULT NULL COMMENT '最新访问日期',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全局账号';

-- ----------------------------
-- Table structure for d_global_user_tenant
-- ----------------------------
DROP TABLE IF EXISTS `d_global_user_tenant`;
CREATE TABLE `d_global_user_tenant` (
                                        `id` bigint NOT NULL COMMENT 'ID',
                                        `account_id` bigint NOT NULL COMMENT '账号ID',
                                        `tenant_id` bigint NOT NULL COMMENT '项目租户ID',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                        `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                        `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                        `management_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '授权项目(authorized_projects),自建项目(created_projects)',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `I_GLOBAL_USER_TENANT_ACCOUNTID` (`account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户项目管理表';

-- ----------------------------
-- Table structure for d_tenant
-- ----------------------------
DROP TABLE IF EXISTS `d_tenant`;
CREATE TABLE `d_tenant` (
                            `id` bigint NOT NULL COMMENT '主键ID',
                            `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `connect_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `readonly` bit(1) DEFAULT b'0' COMMENT '是否内置',
                            `duty` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `expiration_time` datetime DEFAULT NULL COMMENT '有效期\n为空表示永久',
                            `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `create_user` bigint DEFAULT NULL COMMENT '创建人',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `update_user` bigint DEFAULT NULL COMMENT '修改人',
                            `domain_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `wx_app_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `wx_status` tinyint(1) DEFAULT '0' COMMENT '公众号状态（0：未校验  1：已校验）',
                            `doctor_qr_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `old_domain_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `wx_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `publish_time` datetime DEFAULT NULL COMMENT '上线时间',
                            `qun_fa_notification_qr_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `sort` int DEFAULT NULL COMMENT '顺序',
                            `wx_bind_error_message` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号绑定信息',
                            `pre_auth_code` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预授权码',
                            `wx_bind_time` datetime DEFAULT NULL COMMENT '微信绑定时间',
                            `doctor_share_qr_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生分享二维码的地址',
                            `ai_assistant_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'AI助手的名称',
                            `ai_assistant_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'AI助手的头像',
                            `days_remaining` int DEFAULT NULL COMMENT '剩余天数',
                            `status_sort` int DEFAULT NULL COMMENT '状态所在的顺序',
                            `assistant_qr_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医助激活二维码',
                            `project_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '项目种类：mentalDisease(精神病)',
                            `english_doctor_qr_url` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '英文医生扫码关注图片地址',
                            `english_doctor_share_qr_url` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '英语医生扫码关注图片分享地址',
                            `english_assistant_qr_url` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '英语医助激活的二维码',
                            `default_language` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '默认语言',
                            `doctor_register_type` int DEFAULT '0' COMMENT '医生注册的方式 0 自由注册， 1 必须审核',
                            `diseases_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '疾病类型',
                            `official_account_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号类型',
                            `data_security_settings` tinyint(1) DEFAULT '0' COMMENT '数据安全开关',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE KEY `UN_CODE` (`code`) USING BTREE COMMENT '租户唯一编码',
                            KEY `I_TENANT_NAME` (`name`) USING BTREE,
                            KEY `I_TENANT_WX_APPID_IDX` (`wx_app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业';

-- ----------------------------
-- Table structure for d_tenant_app_config
-- ----------------------------
DROP TABLE IF EXISTS `d_tenant_app_config`;
CREATE TABLE `d_tenant_app_config` (
                                       `id` bigint NOT NULL COMMENT '主键',
                                       `qr_code_background` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `app_application_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `app_application_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `app_version_code` int DEFAULT NULL COMMENT '版本号',
                                       `app_version_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `app_launch_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `app_login_background` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `app_agreement` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '服务协议',
                                       `app_frequently_asked_question` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '常见问题',
                                       `question_feedback` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '问题反馈',
                                       `app_about_us` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '关于我们',
                                       `app_huawei_appid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `app_weixin_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `app_img_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `app_im_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `app_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `apk_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `package_status` tinyint unsigned DEFAULT '0' COMMENT '打包状态（0：未打包  1：打包中  2：打包失败  3：打包成功）',
                                       `doctor_qr_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `upload_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原生安卓的下载地址',
                                       `jpush_channel` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `jpush_appkey` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `jpush_master_secret` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `drugstore_qrcode_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `drugstore_apk_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `drugstore_apk_packing_status` tinyint(1) DEFAULT NULL COMMENT '药店apk打包状态',
                                       `mi_app_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `mi_app_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `tenant_id` bigint DEFAULT NULL COMMENT '租户Id',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `appointment_switch` tinyint unsigned DEFAULT '1' COMMENT '预约管理开关：0开启，1关闭',
                                       `consultation_switch` tinyint(1) DEFAULT '1' COMMENT '会诊管理开关：0开启，1关闭',
                                       `referral_switch` tinyint(1) DEFAULT '1' COMMENT '转诊开关：0开启，1关闭',
                                       `app_user_call` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `wx_user_call` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `appointment_doctor_scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预约的医生范围：myself， organ',
                                       `patient_manage_switch` tinyint(1) DEFAULT '1' COMMENT '患者管理平台：0开启，1关闭',
                                       `renew_record` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                                       `dcloud_appkey` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'uniapp的离线打包key',
                                       `dcloud_appid` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'uniapp中的APPID',
                                       `uni_apk_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'UNI app打包的url二维码图片地址',
                                       `uni_apk_download_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'uniApp安卓安装包的下载地址',
                                       `uni_package_status` tinyint unsigned DEFAULT '0' COMMENT 'UNI打包状态（0：未打包  1：打包中  2：打包失败  3：打包成功）',
                                       `uni_app_version_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'uni的安卓版本的版本号',
                                       `uni_renew_record` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'uni安卓的更新记录',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_TENANT_APP_CONFIG_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目APP配置';

-- ----------------------------
-- Table structure for d_tenant_app_version
-- ----------------------------
DROP TABLE IF EXISTS `d_tenant_app_version`;
CREATE TABLE `d_tenant_app_version` (
                                        `id` bigint NOT NULL,
                                        `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `platform` int DEFAULT NULL COMMENT '系统平台，0：iOS；1：Andorid',
                                        `boundle_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `app_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `version_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `version_code` int DEFAULT NULL COMMENT '版本号，android升级覆盖安装必须递增；iOS中为versionName对应的每次build的版本号',
                                        `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `upgrade_desc` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `enable` int DEFAULT NULL COMMENT '是否启用，1-启用，0-禁用，表示该次升级是否启用',
                                        `qrcode_image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `I_VERSION_BUNDLEID` (`boundle_id`) USING BTREE,
                                        KEY `I_VERSION_TENANT_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='版本表';

-- ----------------------------
-- Table structure for d_tenant_batch_build_apktask
-- ----------------------------
DROP TABLE IF EXISTS `d_tenant_batch_build_apktask`;
CREATE TABLE `d_tenant_batch_build_apktask` (
                                                `id` bigint NOT NULL,
                                                `task_status` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `fail` int DEFAULT '0' COMMENT '异常打包数量',
                                                `finish` int DEFAULT '0' COMMENT '完成打包数量',
                                                `all_task` int DEFAULT '0' COMMENT '总打包数量',
                                                `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                                `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                                                `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                                `start_time` datetime DEFAULT NULL COMMENT '开始时间',
                                                `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                                `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                                                `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务名称',
                                                `last_update_time` bigint DEFAULT NULL COMMENT '最后更新时间',
                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='批量打包apk任务';

-- ----------------------------
-- Table structure for d_tenant_batch_build_child_task
-- ----------------------------
DROP TABLE IF EXISTS `d_tenant_batch_build_child_task`;
CREATE TABLE `d_tenant_batch_build_child_task` (
                                                   `id` bigint NOT NULL,
                                                   `task_status` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                   `message` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                                                   `code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                   `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                                   `batch_build_apk_task_id` bigint DEFAULT '0' COMMENT '批量任务',
                                                   `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                                                   `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                                   `start_time` datetime DEFAULT NULL COMMENT '开始时间',
                                                   `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                                   `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                                                   `app_version_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '打包的版本号',
                                                   `last_update_time` bigint DEFAULT NULL COMMENT '最后更新时间',
                                                   `app_version_name_last` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原先的版本号',
                                                   PRIMARY KEY (`id`) USING BTREE,
                                                   KEY `I_TENANT_BATCH_BUILD_CHILD_TASK_ID_IDX` (`batch_build_apk_task_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='批量打包的子任务';

-- ----------------------------
-- Table structure for d_tenant_configuration_mark
-- ----------------------------
DROP TABLE IF EXISTS `d_tenant_configuration_mark`;
CREATE TABLE `d_tenant_configuration_mark` (
                                               `id` bigint NOT NULL,
                                               `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `library_tenant_status` int DEFAULT '0' COMMENT '关联内容库状态 0 默认设置, 1 已设置',
                                               `tenant_register_guide_status` int DEFAULT '0' COMMENT '注册引导设置状态 0 默认设置, 1 已设置',
                                               `tenant_register_success_status` int DEFAULT '0' COMMENT '注册成功提示状态 0 默认设置, 1 已设置',
                                               `base_info_form_status` int DEFAULT '0' COMMENT '基本信息表单状态 0 未设置, 1 已设置',
                                               `keyword_reply_status` int DEFAULT '0' COMMENT '关键字回复 0 未设置, 1 已设置',
                                               `received_message_reply` int DEFAULT '0' COMMENT '收到消息回复 0 未设置, 1 已设置',
                                               `menu_setting_status` int DEFAULT '0' COMMENT '菜单设置 0 未设置, 1 已设置',
                                               `disease_info_status` int DEFAULT '0' COMMENT '疾病信息设置状态 0 未设置, 1 已设置',
                                               `label_management_status` int DEFAULT '0' COMMENT '标签管理的状态 0 未设置, 1 已设置',
                                               `follow_manage_status` int DEFAULT '0' COMMENT '随访管理设置的状态 0 未设置, 1 已设置',
                                               `system_role_status` int DEFAULT '0' COMMENT '角色设置状态 0 未设置, 1 已设置',
                                               `patient_center_status` int DEFAULT '0' COMMENT '患者个人中心设置状态 0 未设置, 1 已设置',
                                               `doctor_center_status` int DEFAULT '0' COMMENT '医生个人中心设置状态 0 未设置, 1 已设置',
                                               `nursing_center_status` int DEFAULT '0' COMMENT '医助个人中心设置状态 0 未设置, 1 已设置',
                                               `project_backend_statistics_status` int DEFAULT '0' COMMENT '项目后台统计状态 0 未设置, 1 已设置',
                                               `recommend_config_status` int DEFAULT '0' COMMENT '患者推荐设置 0 未设置, 1 已设置',
                                               `operational_support_status` int DEFAULT '0' COMMENT '运营支持 0 未设置, 1 已设置',
                                               `push_configuration_status` int DEFAULT '0' COMMENT 'APP推送配置 0 未设置, 1 已设置',
                                               `android_change_log_status` int DEFAULT '0' COMMENT '安卓变更日志 0 未设置, 1 已设置',
                                               `uni_change_log_status` int DEFAULT '0' COMMENT 'UNI安卓变更日志 0 未设置, 1 已设置',
                                               `uni_configuration_status` int DEFAULT '0' COMMENT 'UNI配置 0 未设置, 1 已设置',
                                               `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                               `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                               `banner_status` int DEFAULT '0' COMMENT 'banner设置 0未设置 1已设置',
                                               PRIMARY KEY (`id`) USING BTREE,
                                               KEY `I_TENANT_CONFIGURATION_MARK_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目配置标记表';

-- ----------------------------
-- Table structure for d_tenant_configuration_schedule
-- ----------------------------
DROP TABLE IF EXISTS `d_tenant_configuration_schedule`;
CREATE TABLE `d_tenant_configuration_schedule` (
                                                   `id` bigint NOT NULL,
                                                   `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                   `base_set_status` int DEFAULT '0' COMMENT '基础设置状态 1 已配置。 0未配置',
                                                   `official_account_status` int DEFAULT '0' COMMENT '公众号设置状态 1 已配置。 0未配置',
                                                   `function_set_status` int DEFAULT '0' COMMENT '功能设置状态 1 已配置。 0未配置',
                                                   `interface_set_status` int DEFAULT '0' COMMENT '界面设置状态 1 已配置。 0未配置',
                                                   `operate_set_status` int DEFAULT '0' COMMENT '运营设置状态 1 已配置。 0未配置',
                                                   `app_set_status` int DEFAULT '0' COMMENT 'APP设置状态 1 已配置。 0未配置',
                                                   `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                   `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                   PRIMARY KEY (`id`) USING BTREE,
                                                   KEY `I_TENANT_CONFIGURATION_SCHEDULE_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目配置进度表';

-- ----------------------------
-- Table structure for d_tenant_datasource_config
-- ----------------------------
DROP TABLE IF EXISTS `d_tenant_datasource_config`;
CREATE TABLE `d_tenant_datasource_config` (
                                              `id` bigint NOT NULL COMMENT 'ID',
                                              `tenant_id` bigint DEFAULT NULL COMMENT '租户id',
                                              `datasource_config_id` bigint DEFAULT NULL COMMENT '数据源id',
                                              `application` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户数据源关系';

-- ----------------------------
-- Table structure for d_tenant_operate
-- ----------------------------
DROP TABLE IF EXISTS `d_tenant_operate`;
CREATE TABLE `d_tenant_operate` (
                                    `id` bigint NOT NULL COMMENT '主键ID',
                                    `organ_limitation` int DEFAULT NULL COMMENT '机构是否限制 0：不 1：限制',
                                    `organ_limitation_number` int DEFAULT NULL COMMENT '机构限制数量',
                                    `nursing_limitation` int DEFAULT NULL COMMENT '医助是否限制 0：不 1：限制',
                                    `nursing_limitation_number` int DEFAULT NULL COMMENT '医助限制数量',
                                    `doctor_limitation` int DEFAULT NULL COMMENT '医生是否限制',
                                    `doctor_limitation_number` int DEFAULT NULL COMMENT '医生限制数量',
                                    `duration_limitation` int DEFAULT NULL COMMENT '时长是否限制 0：不， 1：限制',
                                    `expiration_time` date DEFAULT NULL COMMENT '到期时间',
                                    `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                    `days_remaining` int DEFAULT NULL COMMENT '剩余天数',
                                    `tenant_id` bigint DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE,
                                    KEY `I_TENANT_OPERATE_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目运营配置';

-- ----------------------------
-- Table structure for d_tenant_step
-- ----------------------------
DROP TABLE IF EXISTS `d_tenant_step`;
CREATE TABLE `d_tenant_step` (
                                 `id` bigint NOT NULL COMMENT '主键ID',
                                 `cur_top_step` int unsigned NOT NULL DEFAULT '0' COMMENT '当前顶级配置步骤，默认0即未开始',
                                 `cur_child_step` int unsigned NOT NULL DEFAULT '0' COMMENT '当前顶级步骤的子步骤，默认0即未开始',
                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目配置步骤信息';

-- ----------------------------
-- Table structure for f_attachment
-- ----------------------------
DROP TABLE IF EXISTS `f_attachment`;
CREATE TABLE `f_attachment` (
                                `id` bigint NOT NULL COMMENT 'ID',
                                `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `biz_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `biz_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `data_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `submitted_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `group_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `relative_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `file_md5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `context_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `ext` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `size` bigint DEFAULT '0' COMMENT '大小',
                                `org_id` bigint DEFAULT NULL COMMENT '组织ID\n#c_core_org',
                                `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `create_month` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `create_week` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `create_day` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                `update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
                                `update_user` bigint DEFAULT NULL COMMENT '最后修改人',
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `I_FILE_TC_FILEMD5` (`tenant_code`,`file_md5`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='附件';

-- ----------------------------
-- Table structure for f_file
-- ----------------------------
DROP TABLE IF EXISTS `f_file`;
CREATE TABLE `f_file` (
                          `id` bigint NOT NULL COMMENT '主键',
                          `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `data_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `submitted_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `grade` int DEFAULT '1' COMMENT '层级等级\n从1开始计算',
                          `is_delete` bit(1) DEFAULT b'0' COMMENT '是否删除\n#BooleanStatus{TRUE:1,已删除;FALSE:0,未删除}',
                          `folder_id` bigint DEFAULT '0' COMMENT '父文件夹ID',
                          `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                          `size` bigint DEFAULT '0' COMMENT '文件大小\n单位字节',
                          `folder_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `group_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `relative_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `file_md5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `context_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `ext` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `create_month` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `create_week` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `create_day` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                          `create_user` bigint DEFAULT NULL COMMENT '创建人',
                          `update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
                          `update_user` bigint DEFAULT NULL COMMENT '最后修改人',
                          `owner_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          PRIMARY KEY (`id`) USING BTREE,
                          KEY `I_FILE_TC_MD5` (`tenant_code`,`file_md5`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';

-- ----------------------------
-- Table structure for f_file_classification
-- ----------------------------
DROP TABLE IF EXISTS `f_file_classification`;
CREATE TABLE `f_file_classification` (
                                         `id` bigint NOT NULL COMMENT '主键ID',
                                         `classification_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分组名称',
                                         `classification_sort` int DEFAULT NULL COMMENT '分组的排序',
                                         `classification_belong` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分组的归属(公众图片，我的图片)',
                                         `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `I_FILE_CLASSIFICATION_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图片分组';

-- ----------------------------
-- Table structure for f_file_my
-- ----------------------------
DROP TABLE IF EXISTS `f_file_my`;
CREATE TABLE `f_file_my` (
                             `id` bigint NOT NULL COMMENT '主键ID',
                             `file_id` bigint NOT NULL COMMENT '文件id',
                             `file_classification_id` bigint DEFAULT NULL COMMENT '图片分组ID',
                             `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `create_time` datetime DEFAULT NULL COMMENT '上传时间',
                             `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                             `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                             `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
                             PRIMARY KEY (`id`) USING BTREE,
                             KEY `I_FILE_MY_TC` (`tenant_code`) USING BTREE,
                             KEY `I_FILE_MY_TC_FILE_CLASSID` (`tenant_code`,`file_classification_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='我的图片';

-- ----------------------------
-- Table structure for f_file_public_image
-- ----------------------------
DROP TABLE IF EXISTS `f_file_public_image`;
CREATE TABLE `f_file_public_image` (
                                       `id` bigint NOT NULL COMMENT '主键ID',
                                       `file_id` bigint NOT NULL COMMENT '文件id',
                                       `file_classification_id` bigint DEFAULT NULL COMMENT '图片分组ID',
                                       `create_time` datetime DEFAULT NULL COMMENT '上传时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_FILE_PUNLIC_IMAGE_FILE_CLASSIFICATIONID` (`file_classification_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公共图片';

-- ----------------------------
-- Table structure for f_file_recently_used
-- ----------------------------
DROP TABLE IF EXISTS `f_file_recently_used`;
CREATE TABLE `f_file_recently_used` (
                                        `id` bigint NOT NULL COMMENT '主键ID',
                                        `file_id` bigint NOT NULL COMMENT '文件id',
                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `create_time` datetime DEFAULT NULL COMMENT '上传时间',
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `I_FILE_RECENTLY_USED_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='最近使用图片';

-- ----------------------------
-- Table structure for f_file_screenshot_task
-- ----------------------------
DROP TABLE IF EXISTS `f_file_screenshot_task`;
CREATE TABLE `f_file_screenshot_task` (
                                          `id` bigint NOT NULL COMMENT '主键ID',
                                          `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频文件名称',
                                          `task_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '华为截图任务ID',
                                          `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '华为截图任务状态',
                                          `output` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '输出文件信息',
                                          `output_file_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '截图文件名称',
                                          `thumbnail_time` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '指定的截图时间点',
                                          `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '截图任务描述，当截图出现异常时，此字段为异常的原因',
                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `I_FILE_SCREENSHOT_TASK_FILENAME` (`file_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频截图任务';

-- ----------------------------
-- Table structure for g_block_list
-- ----------------------------
DROP TABLE IF EXISTS `g_block_list`;
CREATE TABLE `g_block_list` (
                                `id` bigint NOT NULL COMMENT 'ID',
                                `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `request_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `limit_start` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `limit_end` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `status` bit(1) DEFAULT b'1' COMMENT '状态',
                                `create_time` datetime DEFAULT NULL,
                                `create_user` bigint DEFAULT NULL,
                                `update_time` datetime DEFAULT NULL,
                                `update_user` bigint DEFAULT NULL,
                                PRIMARY KEY (`id`) USING BTREE,
                                FULLTEXT KEY `FU_PATH` (`limit_start`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='黑名单\n';

-- ----------------------------
-- Table structure for g_rate_limiter
-- ----------------------------
DROP TABLE IF EXISTS `g_rate_limiter`;
CREATE TABLE `g_rate_limiter` (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `count` int DEFAULT '0' COMMENT '次数',
                                  `request_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `limit_start` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `limit_end` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `status` bit(1) DEFAULT b'1' COMMENT '状态',
                                  `interval_sec` bigint DEFAULT '0' COMMENT '时间窗口',
                                  `create_time` datetime DEFAULT NULL,
                                  `create_user` bigint DEFAULT NULL,
                                  `update_time` datetime DEFAULT NULL,
                                  `update_user` bigint DEFAULT NULL,
                                  PRIMARY KEY (`id`) USING BTREE,
                                  FULLTEXT KEY `FU_PATH` (`limit_start`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='限流';

-- ----------------------------
-- Table structure for hospital
-- ----------------------------
DROP TABLE IF EXISTS `hospital`;
CREATE TABLE `hospital` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '医院ID',
                            `province_id` bigint DEFAULT NULL COMMENT '省ID',
                            `city_id` bigint DEFAULT NULL COMMENT '城市ID',
                            `region_id` bigint DEFAULT NULL COMMENT '区域ID',
                            `address` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '地址',
                            `hospital_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '医院编码',
                            `hospital_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '医院名称',
                            `hospital_abbreviation` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '医院简称',
                            `hospital_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '医院等级',
                            `hospital_property` tinyint(1) DEFAULT '0' COMMENT '医院属性(0-非公立 1-公立)',
                            `telephone` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
                            `simple_introduce` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '简介',
                            `is_affirm` tinyint(1) DEFAULT '0' COMMENT '是否确认(0-未确认 1-已确认)',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否删除(0-已删除 1-正常)',
                            `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                            `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                            PRIMARY KEY (`id`) USING BTREE,
                            KEY `I_HOSPITAL_NAME_IDEX` (`hospital_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1895017822002610214 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for hospital_copy
-- ----------------------------
DROP TABLE IF EXISTS `hospital_copy`;
CREATE TABLE `hospital_copy` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '医院ID',
                                 `province_id` bigint DEFAULT NULL COMMENT '省ID',
                                 `city_id` bigint DEFAULT NULL COMMENT '城市ID',
                                 `region_id` bigint DEFAULT NULL COMMENT '区域ID',
                                 `address` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '地址',
                                 `hospital_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '医院编码',
                                 `hospital_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '医院名称',
                                 `hospital_abbreviation` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '医院简称',
                                 `hospital_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '医院等级',
                                 `hospital_property` tinyint(1) DEFAULT '0' COMMENT '医院属性(0-非公立 1-公立)',
                                 `telephone` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
                                 `simple_introduce` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '简介',
                                 `is_affirm` tinyint(1) DEFAULT '0' COMMENT '是否确认(0-未确认 1-已确认)',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                 `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否删除(0-已删除 1-正常)',
                                 `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                 `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `I_HOSPITAL_NAME_IDEX` (`hospital_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1895017822002610214 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for m_ai_article_account_consumption
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_account_consumption`;
CREATE TABLE `m_ai_article_account_consumption` (
                                                    `id` bigint NOT NULL,
                                                    `user_mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户手机号',
                                                    `source_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '能量豆明细类型 article, textual',
                                                    `consumption_id` bigint DEFAULT NULL COMMENT '能量豆明细ID',
                                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                    `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                    `user_id` bigint DEFAULT NULL,
                                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='能量豆明细关联表';

-- ----------------------------
-- Table structure for m_ai_article_convergence_media
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_convergence_media`;
CREATE TABLE `m_ai_article_convergence_media` (
                                                  `id` bigint NOT NULL,
                                                  `user_id` bigint DEFAULT NULL COMMENT '博主ID',
                                                  `user_mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户手机号',
                                                  `file_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件标题',
                                                  `file_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '链接路径',
                                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库融媒体';

-- ----------------------------
-- Table structure for m_ai_article_daily_usage_data
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_daily_usage_data`;
CREATE TABLE `m_ai_article_daily_usage_data` (
                                                 `id` bigint NOT NULL,
                                                 `user_id` bigint DEFAULT NULL COMMENT '用户Id',
                                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                 `week_number` int DEFAULT NULL COMMENT '周次(2025年6月2日为初始日期)',
                                                 `register_number` bigint DEFAULT NULL COMMENT '注册用户数量',
                                                 `active_number` bigint DEFAULT NULL COMMENT '活跃用户数量',
                                                 `produce_first_complete_user_number` bigint DEFAULT NULL COMMENT '完成首次创作用户数量',
                                                 `produce_complete_user_number` bigint DEFAULT NULL COMMENT '完成创作用户数量',
                                                 `produce_start_user_number` bigint DEFAULT NULL COMMENT '开启创作用户数量',
                                                 `produce_complete_total_number` bigint DEFAULT NULL COMMENT '累计创作数量',
                                                 `produce_error_total_number` bigint DEFAULT NULL COMMENT '创作异常数量(程序异常)',
                                                 `produce_unfinished_total_number` bigint DEFAULT NULL COMMENT '创作未完成数量(用户离开)',
                                                 `article_complete_number` bigint DEFAULT NULL COMMENT '文章完成数量',
                                                 `podcast_complete_number` bigint DEFAULT NULL COMMENT '播客完成数量',
                                                 `video_complete_number` bigint DEFAULT NULL COMMENT '视频完成数量',
                                                 `create_day` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '日期',
                                                 `create_month` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '月份',
                                                 `day_7_active` int DEFAULT '0' COMMENT '统计 前 8天注册的用户 在今天的留存率',
                                                 `day_30_active` int DEFAULT NULL COMMENT '统计 前30天注册的用户， 留存率',
                                                 `article_original_science_number` int DEFAULT NULL COMMENT '原创科普文章创作',
                                                 `article_rewrite_science_number` int DEFAULT NULL COMMENT '仿写科普文章创作',
                                                 `article_original_social_number` int DEFAULT NULL COMMENT '原创社交媒体文案',
                                                 `article_rewrite_social_number` int DEFAULT NULL COMMENT '仿写社交媒体文案',
                                                 `article_original_video_number` int DEFAULT NULL COMMENT '原创短视频口播',
                                                 `article_rewrite_video_number` int DEFAULT NULL COMMENT '仿写短视频口播',
                                                 `article_original_digital_person_number` int DEFAULT NULL COMMENT '数字人创作数量',
                                                 `podcast_number` int DEFAULT NULL COMMENT '播客数量',
                                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI创作日使用数据累计';

-- ----------------------------
-- Table structure for m_ai_article_event_log
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_event_log`;
CREATE TABLE `m_ai_article_event_log` (
                                          `id` bigint NOT NULL,
                                          `create_day` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '日期',
                                          `week_number` int DEFAULT NULL COMMENT '周次(2025年6月2日为初始日期)',
                                          `create_month` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '月份',
                                          `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                          `event_type` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件类型',
                                          `task_id` bigint DEFAULT NULL COMMENT '事件相关的任务ID',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `article_event_create_day_idx` (`create_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI创作用户事件日志';

-- ----------------------------
-- Table structure for m_ai_article_outline
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_outline`;
CREATE TABLE `m_ai_article_outline` (
                                        `id` bigint NOT NULL,
                                        `task_id` bigint DEFAULT NULL COMMENT 'AI创作任务',
                                        `article_outline` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '文章大纲',
                                        `article_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '文章的正文',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ai创作文章正文';

-- ----------------------------
-- Table structure for m_ai_article_podcast_join
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_podcast_join`;
CREATE TABLE `m_ai_article_podcast_join` (
                                             `id` bigint NOT NULL,
                                             `task_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务名称',
                                             `task_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务类型 音频 文章',
                                             `task_status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务状态 已完成 未完成',
                                             `user_id` bigint NOT NULL COMMENT '所属用户ID',
                                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                             `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                             `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                             `task_id` bigint DEFAULT NULL COMMENT '任务ID',
                                             `show_ai_knowledge` int DEFAULT '0' COMMENT '是否展示到ai医生数字分身平台',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播客和创作文章关联表';

-- ----------------------------
-- Table structure for m_ai_article_task
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_task`;
CREATE TABLE `m_ai_article_task` (
                                     `id` bigint NOT NULL,
                                     `step` int DEFAULT NULL COMMENT '创作的步骤',
                                     `key_words` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关键词',
                                     `audience` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '受众',
                                     `writing_style` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '写作风格',
                                     `article_word_count` int DEFAULT NULL COMMENT '文章字数',
                                     `automatic_picture_matching` int DEFAULT NULL COMMENT '自动配图 0 关闭 1 开启',
                                     `title` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '选择的标题',
                                     `task_status` int DEFAULT NULL COMMENT '任务状态 0 创作中 1 创作完成 2 存为草稿',
                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                     `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '版本',
                                     `task_type` int DEFAULT '1' COMMENT '任务创作类型 1: 图文创作， 2 小红书文案， 3 视频口播',
                                     `creative_approach` int DEFAULT '1' COMMENT '创作方式 1 原创， 2 仿写',
                                     `imitative_writing_type` int DEFAULT NULL COMMENT '仿写方式: 1 文本， 2 链接',
                                     `imitative_writing_material` text COLLATE utf8mb4_unicode_ci COMMENT '仿写素材： 文本内容 或 链接地址',
                                     `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '非原创图文的 最终结果',
                                     `show_ai_knowledge` int DEFAULT '0' COMMENT '是否展示到ai医生数字分身平台',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ai创作任务';

-- ----------------------------
-- Table structure for m_ai_article_title
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_title`;
CREATE TABLE `m_ai_article_title` (
                                      `id` bigint NOT NULL,
                                      `task_id` bigint DEFAULT NULL COMMENT 'AI创作任务',
                                      `article_title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '文章标题',
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ai创作文章标题';

-- ----------------------------
-- Table structure for m_ai_article_user
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_user`;
CREATE TABLE `m_ai_article_user` (
                                     `id` bigint NOT NULL,
                                     `user_mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户手机号',
                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                     `password` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
                                     `user_avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
                                     `user_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
                                     `user_gender` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户性别',
                                     `personal_profile` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介',
                                     `membership_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Annual_Membership' COMMENT '会员等级',
                                     `membership_expiration` datetime DEFAULT '2099-05-06 16:11:51' COMMENT '会员到期时间',
                                     `energy_beans` bigint DEFAULT '0' COMMENT '会员能量豆',
                                     `free_energy_beans` bigint DEFAULT '0' COMMENT '注册用户赠送的20能量豆',
                                     `first_creation` tinyint DEFAULT '0' COMMENT '是否完成首次创作',
                                     `day_7_active` tinyint DEFAULT '0' COMMENT '第7天是否活跃',
                                     `day_30_active` tinyint DEFAULT '0' COMMENT '第30天是否活跃',
                                     `first_creation_date` datetime DEFAULT NULL COMMENT '完成首次创作的日期',
                                     `look_guide` tinyint DEFAULT '0' COMMENT '查看引导',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ai创作用户';

-- ----------------------------
-- Table structure for m_ai_article_user_account
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_user_account`;
CREATE TABLE `m_ai_article_user_account` (
                                             `id` bigint NOT NULL,
                                             `user_mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户手机号',
                                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                             `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                             `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                             `energy_beans` bigint DEFAULT '0' COMMENT '会员能量豆',
                                             `free_energy_beans` bigint DEFAULT '0' COMMENT '注册用户赠送的20能量豆',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ai用户能量豆账户';

-- ----------------------------
-- Table structure for m_ai_article_user_avatar
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_user_avatar`;
CREATE TABLE `m_ai_article_user_avatar` (
                                            `id` bigint NOT NULL,
                                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                            `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                            `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                            `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片链接',
                                            `default_avatar` tinyint(1) DEFAULT '0' COMMENT '1默认，其他否',
                                            `user_id` bigint DEFAULT NULL COMMENT '用户id',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='形象管理';

-- ----------------------------
-- Table structure for m_ai_article_user_consumption
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_user_consumption`;
CREATE TABLE `m_ai_article_user_consumption` (
                                                 `id` bigint NOT NULL,
                                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                 `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                                 `consumption_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消费类型',
                                                 `consumption_amount` int DEFAULT NULL COMMENT '消耗能量币数量',
                                                 `message_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普创作用户能量豆消费记录';

-- ----------------------------
-- Table structure for m_ai_article_user_free_limit
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_user_free_limit`;
CREATE TABLE `m_ai_article_user_free_limit` (
                                                `id` bigint NOT NULL,
                                                `user_id` bigint NOT NULL COMMENT '用户ID',
                                                `voice_remaining_times` int NOT NULL DEFAULT '0' COMMENT '音色克隆次数',
                                                `human_remaining_times` int NOT NULL DEFAULT '0' COMMENT '形象次数',
                                                `expiration_date` datetime DEFAULT NULL COMMENT '可用额度到期时间',
                                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普创作会员免费额度';

-- ----------------------------
-- Table structure for m_ai_article_user_notice
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_user_notice`;
CREATE TABLE `m_ai_article_user_notice` (
                                            `id` bigint NOT NULL,
                                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                            `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                            `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                            `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                            `read_status` tinyint DEFAULT NULL COMMENT '是否已读',
                                            `notice_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息类型',
                                            `notice_content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知内容',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普用户系统通知';

-- ----------------------------
-- Table structure for m_ai_article_user_order
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_user_order`;
CREATE TABLE `m_ai_article_user_order` (
                                           `id` bigint NOT NULL,
                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                           `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                           `goods_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员充值，能量币充值',
                                           `goods_price` int DEFAULT NULL COMMENT '价格，单位分',
                                           `payment_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付状态  成功 success,   未支付 noPay',
                                           `open_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                           `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
                                           `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
                                           `uid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'uid',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普创作用户支付订单';

-- ----------------------------
-- Table structure for m_ai_article_user_video
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_user_video`;
CREATE TABLE `m_ai_article_user_video` (
                                           `id` bigint NOT NULL,
                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                           `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                           `voice_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频链接',
                                           `default_voice` tinyint(1) DEFAULT '0' COMMENT '1默认，其他否',
                                           `user_id` bigint DEFAULT NULL COMMENT '用户id',
                                           `h_file_id` bigint DEFAULT NULL COMMENT '海螺平台文件id',
                                           `h_demo_audio` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '克隆试听音频链接',
                                           `h_has_clone` tinyint(1) DEFAULT '0' COMMENT '海螺是否克隆：1已克隆 0未克隆',
                                           `h_voice_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '海螺音频复刻id',
                                           `h_clone_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '海螺音频复刻信息',
                                           `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频名称',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='声音管理';

-- ----------------------------
-- Table structure for m_ai_article_user_video_template
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_user_video_template`;
CREATE TABLE `m_ai_article_user_video_template` (
                                                    `id` bigint NOT NULL,
                                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                    `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                    `user_id` bigint DEFAULT NULL COMMENT '用户id',
                                                    `template_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频底板id',
                                                    `video_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频链接',
                                                    `video_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '底板名称',
                                                    `file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件原始名称',
                                                    `template_video_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '底板视频素材文件 ID',
                                                    `video_height` int DEFAULT NULL COMMENT '视频高',
                                                    `video_width` int DEFAULT NULL COMMENT '视频宽',
                                                    `type_` int DEFAULT NULL COMMENT '类型1 图片， 2视频',
                                                    `avatar_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片链接',
                                                    `use_count` int DEFAULT '0' COMMENT '使用次数',
                                                    `delete_status` tinyint DEFAULT '0' COMMENT '删除状态',
                                                    `auth_video_url` varchar(400) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '授权视频',
                                                    `volcengine_image_task_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '火山单图形象任务',
                                                    `volcengine_image_result` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '火山单图形象结果',
                                                    `volcengine_image_error_message` text COLLATE utf8mb4_unicode_ci COMMENT '火山单图形象异常消息',
                                                    `clone_voice` int DEFAULT '0' COMMENT '是否克隆声音',
                                                    `clone_voice_status` int DEFAULT '0' COMMENT '克隆声音的状态',
                                                    `water_job_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '水印任务ID',
                                                    `water_job_request_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '水印任务请求ID',
                                                    `water_job_result_list_json` text COLLATE utf8mb4_unicode_ci COMMENT '水印任务结果列表json',
                                                    `water_job_start_time` datetime DEFAULT NULL COMMENT '水印任务开始时间',
                                                    `water_job_query_result_json` text COLLATE utf8mb4_unicode_ci COMMENT '水印任务查询结果',
                                                    `file_url_back` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频源文件',
                                                    `water_job_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '水印任务状态',
                                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='视频底板';

-- ----------------------------
-- Table structure for m_ai_article_user_voice
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_user_voice`;
CREATE TABLE `m_ai_article_user_voice` (
                                           `id` bigint NOT NULL,
                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                           `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                           `user_id` bigint DEFAULT NULL COMMENT '用户id',
                                           `voice_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频链接',
                                           `default_voice` tinyint unsigned DEFAULT '0' COMMENT '{"是_true", "否_false", "_null"}',
                                           `h_file_id` bigint DEFAULT NULL COMMENT '海螺平台文件id',
                                           `h_voice_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '海螺声音克隆id',
                                           `h_demo_audio` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '克隆试听音频链接',
                                           `h_has_clone` tinyint unsigned DEFAULT '0' COMMENT '海螺是否克隆：1已克隆 0未克隆',
                                           `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频名称',
                                           `fail_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '失败原因',
                                           `delete_status` tinyint DEFAULT '0' COMMENT '删除状态',
                                           `use_count` int DEFAULT '0' COMMENT '使用次数',
                                           `h_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'kailing' COMMENT '海螺账户',
                                           `user_mobile` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户手机号',
                                           `clone_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '克隆状态， 完成，进行中，失败，等待中',
                                           `mini_expired` int DEFAULT '0' COMMENT '在海螺是否过期 0 未过期，1 已过期',
                                           `video_template_id` bigint DEFAULT NULL COMMENT '视频形象ID',
                                           `fail_refund` int DEFAULT '0' COMMENT '失败是否退费',
                                           `textual_` int DEFAULT '0' COMMENT '是否文献解读的音色',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='声音管理';

-- ----------------------------
-- Table structure for m_ai_article_user_voice_task
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_user_voice_task`;
CREATE TABLE `m_ai_article_user_voice_task` (
                                                `id` bigint NOT NULL,
                                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                `user_id` bigint DEFAULT NULL COMMENT '用户id',
                                                `voice_id` bigint DEFAULT NULL COMMENT '音色id',
                                                `template_id` bigint DEFAULT NULL COMMENT '视频底板id',
                                                `talk_text` text COLLATE utf8mb4_unicode_ci COMMENT '讲述内容',
                                                `generate_audio_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '复刻制作的音频文件链接',
                                                `task_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务状态',
                                                `task_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务id',
                                                `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务名',
                                                `task_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务类型',
                                                `template_type` tinyint DEFAULT NULL COMMENT '形象类型 1 图片 2 视频',
                                                `create_type` tinyint DEFAULT NULL COMMENT '1 文本， 2 本地音频， 3 我的博客',
                                                `audio_url` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频的URL',
                                                `podcast_id` bigint DEFAULT NULL COMMENT '播客ID',
                                                `error_message` longtext COLLATE utf8mb4_unicode_ci COMMENT '异常信息',
                                                `avatar_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `upload_audio_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `human_video_cover` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频封面',
                                                `show_ai_knowledge` int DEFAULT '0' COMMENT '是否展示到ai医生数字分身平台',
                                                `make_video_time` datetime DEFAULT NULL COMMENT '开始制作视频的时间',
                                                `open_volcengine_take` tinyint DEFAULT '0' COMMENT '是否开启火山任务',
                                                `cover_video` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面视频',
                                                `back_cover_video` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封底视频',
                                                `final_video_result` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最终视频结果',
                                                `wait_merge_cover` tinyint DEFAULT '1' COMMENT '等待合并封面',
                                                `final_video_result_cover` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最终结果的封面',
                                                `fail_refund` int DEFAULT '0' COMMENT '失败退费',
                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='合成任务';

-- ----------------------------
-- Table structure for m_ai_article_volcengine_voice_task
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_article_volcengine_voice_task`;
CREATE TABLE `m_ai_article_volcengine_voice_task` (
                                                      `id` bigint NOT NULL,
                                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                      `user_id` bigint DEFAULT NULL COMMENT '用户id',
                                                      `voice_task_id` bigint DEFAULT NULL COMMENT '视频创作原任务id',
                                                      `template_id` bigint DEFAULT NULL COMMENT '图片模板或视频模版ID',
                                                      `audio_url` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频的URL',
                                                      `task_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务状态 WAIT_IMAGE 等待做火山形象， WAIT_VIDEO 等待做火山视频',
                                                      `volcengine_task_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '火山视频制作任务id',
                                                      `volcengine_task_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '火山视频制作任务结果',
                                                      `template_type` tinyint DEFAULT NULL COMMENT '形象类型 1 图片 2 视频',
                                                      `volcengine_task_error_message` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '异常信息',
                                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='火山方案视频任务';

-- ----------------------------
-- Table structure for m_ai_audio_analysis_task
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_audio_analysis_task`;
CREATE TABLE `m_ai_audio_analysis_task` (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                            `task_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务唯一标识',
                                            `step` int DEFAULT NULL COMMENT '解析步骤 1-上传音频 \n	2-转录完成 3-AI解析完成',
                                            `file_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传音频文件名称',
                                            `title` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频标题',
                                            `task_status` int DEFAULT NULL COMMENT '任务状态 0-解析中\n	1-解析完成 2-存为草稿 3-解析失败',
                                            `audio_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频文件URL',
                                            `audio_duration` int DEFAULT NULL COMMENT '音频时长(秒)',
                                            `transcript_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '转录文本数据(JSON格式)',
                                            `summary_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'AI总结数据(JSON格式,包含章节、要点、思维导图等)',
                                            `mindmap_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '思维导图数据(JSON格式)',
                                            `chat_history` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '问答历史记录(JSON格式)',
                                            `keywords` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关键词(逗号分隔)',
                                            `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频分类',
                                            `language` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'zh-CN' COMMENT '音频语言',
                                            `ai_model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '使用的AI模型',
                                            `coze_file_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件在coze的ID',
                                            `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会话ID',
                                            `show_ai_knowledge` int DEFAULT '0' COMMENT '是否展示到AI知识平台 0-否 1-是',
                                            `is_public` int DEFAULT '0' COMMENT '是否公开 0-私有 \n	1-公开',
                                            `view_count` int DEFAULT '0' COMMENT '查看次数',
                                            `like_count` int DEFAULT '0' COMMENT '点赞次数',
                                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `create_user` bigint DEFAULT NULL COMMENT '创建人ID',
                                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            `update_user` bigint DEFAULT NULL COMMENT '更新人ID',
                                            `last_analysis_time` datetime DEFAULT NULL COMMENT '最后一次AI解析时间',
                                            PRIMARY KEY (`id`) USING BTREE,
                                            KEY `idx_task_id` (`task_id`) USING BTREE,
                                            KEY `idx_create_user` (`create_user`) USING BTREE,
                                            KEY `idx_create_time` (`create_time`) USING BTREE,
                                            KEY `idx_task_status` (`task_status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2009542752294928385 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='音频解析任务表';

-- ----------------------------
-- Table structure for m_ai_business_card
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card`;
CREATE TABLE `m_ai_business_card` (
                                      `id` bigint NOT NULL DEFAULT '0',
                                      `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
                                      `doctor_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生姓名',
                                      `doctor_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生职称',
                                      `doctor_department` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生科室',
                                      `doctor_hospital` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生医院',
                                      `doctor_be_good_at` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '擅长(60字内)',
                                      `doctor_personal` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人介绍(120字)',
                                      `doctor_meta_human` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生数字人',
                                      `doctor_studio` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作室',
                                      `doctor_AI_dialogue` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'AI对话地址',
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                      `doctor_meta_human_poster` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数字人封面',
                                      `doctor_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生头像',
                                      `doctor_ai_dialogue_key` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'AI对话秘钥',
                                      `doctor_ai_dialogue_question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '对话问题，json列表',
                                      `doctor_ai_type` int DEFAULT NULL COMMENT '0 通用， 1定制',
                                      `user_id` bigint DEFAULT NULL COMMENT '小程序用户ID',
                                      `has_doctor_studio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '有系统医生工作室(内置才有) 0 没有， 1 有',
                                      `create_button_status` int DEFAULT '0' COMMENT '编辑或创建入口隐藏',
                                      `organ_id` bigint DEFAULT NULL COMMENT '机构ID',
                                      `mini_or_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信小程序的二维码',
                                      `business_or_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名片分享二维码',
                                      `activation_time` datetime DEFAULT NULL COMMENT '名片激活时间',
                                      `open_video_account` tinyint(1) DEFAULT '0' COMMENT '是否开通视频号',
                                      `finder_user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频号ID：以“sph”开头的id，可在视频号助手获取',
                                      `feed_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频 feedId',
                                      `open_contact_me` tinyint(1) DEFAULT '0' COMMENT '开启联系我',
                                      `contact_img_url` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开启联系我',
                                      `give_thumbs_up_count` int DEFAULT '0' COMMENT '点赞数量',
                                      `member_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'BASIC_EDITION' COMMENT '会员版本， 默认基础版',
                                      `number_of_views` int DEFAULT '0' COMMENT '浏览量',
                                      `phone` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI名片';

-- ----------------------------
-- Table structure for m_ai_business_card_admin
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card_admin`;
CREATE TABLE `m_ai_business_card_admin` (
                                            `id` bigint NOT NULL,
                                            `organ_id` bigint DEFAULT NULL COMMENT '机构id',
                                            `user_mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号的手机号',
                                            `user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号类型 超管 机构管理员',
                                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                            `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                            `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                            `password` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普名片管理员';

-- ----------------------------
-- Table structure for m_ai_business_card_diagram
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card_diagram`;
CREATE TABLE `m_ai_business_card_diagram` (
                                              `id` bigint NOT NULL,
                                              `order_` int DEFAULT NULL COMMENT '优先级',
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Male 男, Female 女',
                                              `image_obs_url` varchar(350) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片的obs地址',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生名片头像模板';

-- ----------------------------
-- Table structure for m_ai_business_card_diagram_result
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card_diagram_result`;
CREATE TABLE `m_ai_business_card_diagram_result` (
                                                     `id` bigint NOT NULL,
                                                     `order_` int DEFAULT NULL COMMENT '优先级',
                                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                     `image_obs_url` varchar(350) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片的obs地址',
                                                     `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性別',
                                                     `history_` tinyint(1) DEFAULT '0' COMMENT '是否过期',
                                                     `useding` tinyint(1) DEFAULT NULL COMMENT '被使用中',
                                                     `task_id` bigint DEFAULT NULL COMMENT '任务ID',
                                                     `original_drawing` tinyint(1) DEFAULT NULL COMMENT '原图',
                                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生名片头像合成结果';

-- ----------------------------
-- Table structure for m_ai_business_card_diagram_task
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card_diagram_task`;
CREATE TABLE `m_ai_business_card_diagram_task` (
                                                   `id` bigint NOT NULL,
                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                   `user_id` bigint DEFAULT NULL COMMENT '用户Id',
                                                   `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Male 男, Female 女',
                                                   `image_obs_url` varchar(350) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片的obs地址',
                                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户名片任务';

-- ----------------------------
-- Table structure for m_ai_business_card_human_limit
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card_human_limit`;
CREATE TABLE `m_ai_business_card_human_limit` (
                                                  `id` bigint NOT NULL,
                                                  `user_id` bigint NOT NULL COMMENT '用户ID',
                                                  `remaining_times` int NOT NULL COMMENT '剩余次数',
                                                  `expiration_date` datetime DEFAULT NULL COMMENT '可用额度到期时间',
                                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户数字人额度';

-- ----------------------------
-- Table structure for m_ai_business_card_member_redemption_code
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card_member_redemption_code`;
CREATE TABLE `m_ai_business_card_member_redemption_code` (
                                                             `id` bigint NOT NULL,
                                                             `user_id` bigint DEFAULT NULL COMMENT '兑换的用户ID',
                                                             `organ_id` bigint DEFAULT NULL COMMENT '机构ID',
                                                             `redemption_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                                             `exchange_time` datetime DEFAULT NULL COMMENT '兑换时间',
                                                             `exchange_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '兑换状态',
                                                             `redemption_code_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '兑换码版本',
                                                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                             `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                             `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                             `delete_flag` tinyint(1) DEFAULT '0' COMMENT '删除标记',
                                                             PRIMARY KEY (`id`,`redemption_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='机构会员兑换码';

-- ----------------------------
-- Table structure for m_ai_business_card_member_version
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card_member_version`;
CREATE TABLE `m_ai_business_card_member_version` (
                                                     `id` bigint NOT NULL,
                                                     `user_id` bigint NOT NULL COMMENT '用户ID',
                                                     `member_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户版本',
                                                     `expiration_date` datetime DEFAULT NULL COMMENT '版本到期时间',
                                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户的会员版本';

-- ----------------------------
-- Table structure for m_ai_business_card_organ
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card_organ`;
CREATE TABLE `m_ai_business_card_organ` (
                                            `id` bigint NOT NULL,
                                            `organ_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构名称',
                                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                            `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                            `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普名片组织';

-- ----------------------------
-- Table structure for m_ai_business_card_studio
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card_studio`;
CREATE TABLE `m_ai_business_card_studio` (
                                             `id` bigint NOT NULL,
                                             `business_card` bigint NOT NULL COMMENT '医生名片ID',
                                             `doctor_studio` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作室',
                                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                             `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                             `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                             `studio_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作室说明',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI医生名片工作室';

-- ----------------------------
-- Table structure for m_ai_business_card_use_data
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card_use_data`;
CREATE TABLE `m_ai_business_card_use_data` (
                                               `id` bigint NOT NULL,
                                               `business_card_id` bigint NOT NULL COMMENT '名片ID',
                                               `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'openId',
                                               `data_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据类型',
                                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                               `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                               `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='名片使用数据收集';

-- ----------------------------
-- Table structure for m_ai_business_card_use_day_statistics
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card_use_day_statistics`;
CREATE TABLE `m_ai_business_card_use_day_statistics` (
                                                         `id` bigint NOT NULL,
                                                         `business_card_id` bigint NOT NULL COMMENT '名片ID',
                                                         `hospital_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名片用户的医院名称',
                                                         `department_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名片用户的科室名称',
                                                         `people_of_views` int DEFAULT NULL COMMENT '浏览人数',
                                                         `number_of_views` int DEFAULT NULL COMMENT '浏览次数',
                                                         `forwarding_frequency` int DEFAULT NULL COMMENT '转发次数',
                                                         `ai_dialogue_number_count` int DEFAULT NULL COMMENT 'AI对话点击人次',
                                                         `ai_dialogue_click_count` int DEFAULT NULL COMMENT 'AI对话点击次数',
                                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                         `use_date` date DEFAULT NULL COMMENT '日期',
                                                         `organ_id` bigint DEFAULT NULL COMMENT '机构ID',
                                                         `doctor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生名称',
                                                         `give_thumbs_up_count` int DEFAULT NULL COMMENT '点赞数量',
                                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='名片使用数据日统计';

-- ----------------------------
-- Table structure for m_ai_business_card_user_order
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_card_user_order`;
CREATE TABLE `m_ai_business_card_user_order` (
                                                 `id` bigint NOT NULL,
                                                 `user_id` bigint NOT NULL COMMENT '用户id',
                                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                 `goods_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员版本 BASIC_EDITION， MEMBERSHIP_VERSION',
                                                 `goods_price` int DEFAULT NULL COMMENT '价格，单位分',
                                                 `payment_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付状态  成功 success,   未支付 noPay',
                                                 `open_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普名片会员支付订单';

-- ----------------------------
-- Table structure for m_ai_business_digital_human_video_task
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_digital_human_video_task`;
CREATE TABLE `m_ai_business_digital_human_video_task` (
                                                          `id` bigint NOT NULL,
                                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                          `business_card_id` bigint DEFAULT NULL COMMENT '名片ID',
                                                          `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                                          `task_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务状态',
                                                          `task_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务名称',
                                                          `make_way` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '制作方式(photo：照片, video: 视频)',
                                                          `photo_human_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '照片数字人url',
                                                          `audio_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '录音文件url',
                                                          `video_text_content` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频内容文案',
                                                          `template_video_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模版视频url',
                                                          `auth_video_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '授权视频url',
                                                          `human_video_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最终数字人视频结果url',
                                                          `template_video_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '底板视频素材文件 ID,使用文件上传接口上传视频文件获取文件ID',
                                                          `template_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '底板 ID，使用 templateVideoId 合成视频后会返回',
                                                          `baidu_task_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '百度视频任务ID',
                                                          `task_message` longtext COLLATE utf8mb4_unicode_ci COMMENT '任务异常说明',
                                                          `task_source` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务来源(小程序， 管理员)',
                                                          `video_width` int DEFAULT NULL COMMENT '视频宽度',
                                                          `video_height` int DEFAULT NULL COMMENT '视频高度',
                                                          `create_timbre` tinyint(1) DEFAULT NULL COMMENT '是否需要克隆音色',
                                                          `human_video_cover` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数字人封面地址',
                                                          `start_time` datetime DEFAULT NULL COMMENT '任务开始时间',
                                                          `end_time` datetime DEFAULT NULL COMMENT '任务完成时间',
                                                          `baidu_video_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '百度视频url',
                                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数字人视频制作任务';

-- ----------------------------
-- Table structure for m_ai_business_user_audio_result
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_user_audio_result`;
CREATE TABLE `m_ai_business_user_audio_result` (
                                                   `id` bigint NOT NULL,
                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                   `timbre_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音色ID',
                                                   `text_content` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频文字内容',
                                                   `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '语音文件url',
                                                   `api_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '合成状态',
                                                   `error_message` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '合成异常备注',
                                                   `video_task_id` bigint DEFAULT NULL COMMENT '制作数字人视频任务ID',
                                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户声音结果';

-- ----------------------------
-- Table structure for m_ai_business_user_audio_template
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_user_audio_template`;
CREATE TABLE `m_ai_business_user_audio_template` (
                                                     `id` bigint NOT NULL,
                                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                     `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件的url',
                                                     `task_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务状态(等待克隆， 克隆完成， 克隆失败)',
                                                     `task_message` longtext COLLATE utf8mb4_unicode_ci COMMENT '克隆信息',
                                                     `timbre_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '克隆使用的音色',
                                                     `video_task_id` bigint DEFAULT NULL COMMENT '制作数字人视频任务ID',
                                                     `h_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'leizhi' COMMENT '海螺账户',
                                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户提交的录音素材';

-- ----------------------------
-- Table structure for m_ai_business_volcengine_timbre
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_business_volcengine_timbre`;
CREATE TABLE `m_ai_business_volcengine_timbre` (
                                                   `id` bigint NOT NULL,
                                                   `timbre_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '声音ID',
                                                   `expiration_date` datetime DEFAULT NULL COMMENT '到期时间',
                                                   `remaining_times` int DEFAULT NULL COMMENT '剩余次数',
                                                   `timbre_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '音色状态(空闲中，训练中，语音合成中，无效)',
                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='火山音色管理';

-- ----------------------------
-- Table structure for m_ai_call_config
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_call_config`;
CREATE TABLE `m_ai_call_config` (
                                    `id` bigint NOT NULL,
                                    `minute_quantity` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分钟数量 100  500，other',
                                    `cost_` bigint DEFAULT NULL COMMENT '金额(分)',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                    `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通话充值配置';

-- ----------------------------
-- Table structure for m_ai_call_content
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_call_content`;
CREATE TABLE `m_ai_call_content` (
                                     `id` bigint NOT NULL,
                                     `call_record_id` bigint DEFAULT NULL COMMENT '通话记录id',
                                     `user_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户 user，智能体：agent',
                                     `call_content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息内容',
                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分身通话内容';

-- ----------------------------
-- Table structure for m_ai_call_recharge_order
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_call_recharge_order`;
CREATE TABLE `m_ai_call_recharge_order` (
                                            `id` bigint NOT NULL,
                                            `user_id` bigint NOT NULL COMMENT '用户id',
                                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                            `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                            `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                            `see_uid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'seeUid',
                                            `goods_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `goods_price` int DEFAULT NULL COMMENT '价格，单位分',
                                            `payment_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付状态  成功 success,   未支付 noPay',
                                            `payment_time` datetime DEFAULT NULL COMMENT '订单支付时间',
                                            `minute_duration` int DEFAULT NULL COMMENT '分钟时长',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='通话充值订单';

-- ----------------------------
-- Table structure for m_ai_call_record
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_call_record`;
CREATE TABLE `m_ai_call_record` (
                                    `id` bigint NOT NULL,
                                    `user_mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户手机号',
                                    `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
                                    `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
                                    `dial_user_id` bigint DEFAULT NULL COMMENT '拨号用户id',
                                    `blogger_user_id` bigint DEFAULT NULL COMMENT '拨号用户id',
                                    `call_start_time` datetime DEFAULT NULL COMMENT '通话开始时间',
                                    `call_last_updated_time` datetime DEFAULT NULL COMMENT '通话最后更新时间',
                                    `call_end_time` datetime DEFAULT NULL COMMENT '通话结束时间',
                                    `call_duration` int DEFAULT NULL COMMENT '通话时长',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                    `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分身通话记录';

-- ----------------------------
-- Table structure for m_ai_ckd_cook_book
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_ckd_cook_book`;
CREATE TABLE `m_ai_ckd_cook_book` (
                                      `id` bigint NOT NULL COMMENT 'id',
                                      `carbohydrates` double DEFAULT NULL COMMENT '碳水化合物',
                                      `detail_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '食谱详情',
                                      `fat` double DEFAULT NULL COMMENT '脂肪',
                                      `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称',
                                      `protein` double DEFAULT NULL COMMENT '蛋白质',
                                      `taste` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '味道',
                                      `total_heat_energy` double DEFAULT NULL COMMENT '\n总热量',
                                      `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '早中晚类型',
                                      `describe_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
                                      `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '食谱图片',
                                      `update_time` datetime DEFAULT NULL,
                                      `create_time` datetime DEFAULT NULL,
                                      `update_user` bigint DEFAULT NULL,
                                      `create_user` bigint DEFAULT NULL,
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for m_ai_ckd_cookbook_plan
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_ckd_cookbook_plan`;
CREATE TABLE `m_ai_ckd_cookbook_plan` (
                                          `id` bigint NOT NULL COMMENT 'ID',
                                          `open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                          `present_date` datetime DEFAULT NULL COMMENT '生成时间',
                                          `breakfast_id` bigint NOT NULL COMMENT '早餐',
                                          `lunch_id` bigint NOT NULL COMMENT '午餐',
                                          `dinner_id` bigint NOT NULL COMMENT '晚餐',
                                          `is_duplicate` int NOT NULL COMMENT '是否复制',
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户食谱计划';

-- ----------------------------
-- Table structure for m_ai_ckd_laboratory_test_report
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_ckd_laboratory_test_report`;
CREATE TABLE `m_ai_ckd_laboratory_test_report` (
                                                   `id` bigint NOT NULL COMMENT 'ID',
                                                   `open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                                   `file_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                   `serum_creatinine` double(8,3) DEFAULT NULL COMMENT '血肌酐',
                                                   `tc_of_blood_lipid` double(8,3) DEFAULT NULL COMMENT '总胆固醇TC',
                                                   `k_of_electrolyte` double(8,3) DEFAULT NULL COMMENT '钾',
                                                   `na_of_electrolyte` double(8,3) DEFAULT NULL COMMENT '钠',
                                                   `ca_of_electrolyte` double(8,3) DEFAULT NULL COMMENT '钙',
                                                   `p_of_electrolyte` double(8,3) DEFAULT NULL COMMENT '磷',
                                                   `uric_acid` double(8,3) DEFAULT NULL COMMENT '尿酸',
                                                   `mark_html_content` longtext COLLATE utf8mb4_unicode_ci COMMENT '解读内容',
                                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ckd用户化验单';

-- ----------------------------
-- Table structure for m_ai_ckd_recipe_record
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_ckd_recipe_record`;
CREATE TABLE `m_ai_ckd_recipe_record` (
                                          `id` bigint NOT NULL COMMENT 'ID',
                                          `open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                          `image_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                          `recipe_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '食谱名',
                                          `meal_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '食谱类型',
                                          `calories` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '卡路里',
                                          `nutrition` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '营养数据json',
                                          `ingredients` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '食材列表json',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户食谱记录';

-- ----------------------------
-- Table structure for m_ai_ckd_user_coze_token
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_ckd_user_coze_token`;
CREATE TABLE `m_ai_ckd_user_coze_token` (
                                            `id` bigint NOT NULL COMMENT 'ID',
                                            `open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                            `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'appId',
                                            `kid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'OAuth 应用的钥指纹',
                                            `access_token` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'OAuth 应用的钥指纹',
                                            `session_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '访问令牌的会话标识',
                                            `expires_time` datetime DEFAULT NULL COMMENT '过期时间',
                                            `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                            `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CKD用户coze的token';

-- ----------------------------
-- Table structure for m_ai_ckd_user_gfr
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_ckd_user_gfr`;
CREATE TABLE `m_ai_ckd_user_gfr` (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                     `gfr` double(10,2) DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户GFR';

-- ----------------------------
-- Table structure for m_ai_ckd_user_info
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_ckd_user_info`;
CREATE TABLE `m_ai_ckd_user_info` (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                      `nickname` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `head_img_url` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                      `user_role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `user_weight` int DEFAULT NULL COMMENT '体重',
                                      `user_height` int DEFAULT NULL COMMENT '身高',
                                      `date_of_birth` datetime DEFAULT NULL COMMENT '出生日期',
                                      `serum_creatinine` double(8,3) DEFAULT NULL COMMENT '血肌酐',
                                      `gender` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
                                      `membership_level` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员等级',
                                      `expiration_date` datetime DEFAULT NULL COMMENT '到期时间',
                                      `gfr` double(10,3) DEFAULT NULL,
                                      `protein_in_take` double(8,3) DEFAULT NULL COMMENT '蛋白',
                                      `tc_of_blood_lipid` double(8,3) DEFAULT NULL COMMENT '总胆固醇TC',
                                      `k_of_electrolyte` double(8,3) DEFAULT NULL COMMENT '钾',
                                      `na_of_electrolyte` double(8,3) DEFAULT NULL COMMENT '钠',
                                      `ca_of_electrolyte` double(8,3) DEFAULT NULL COMMENT '钙',
                                      `p_of_electrolyte` double(8,3) DEFAULT NULL COMMENT '磷',
                                      `uric_acid` double(8,3) DEFAULT NULL COMMENT '尿酸',
                                      `kidney_disease_staging` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `protein_in_take_everyday` double(8,3) DEFAULT NULL,
                                      `in_take_energy_everyday` double(8,3) DEFAULT NULL,
                                      `high_blood_pressure_label` tinyint(1) DEFAULT NULL,
                                      `hyperlipidemia_label` tinyint(1) DEFAULT NULL,
                                      `high_uric_acid_label` tinyint(1) DEFAULT NULL,
                                      `high_phosphorus_label` tinyint(1) DEFAULT NULL,
                                      `high_potassium_label` tinyint(1) DEFAULT NULL,
                                      `fat_label` tinyint(1) DEFAULT NULL,
                                      `advanced_age_label` tinyint(1) DEFAULT NULL,
                                      `hypertension` tinyint(1) DEFAULT NULL,
                                      `free_duration_fans` int DEFAULT '7' COMMENT '粉丝免费时长',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      KEY `I_WX_USER_INFO_OPEN_ID` (`open_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CKD用户信息';

-- ----------------------------
-- Table structure for m_ai_ckd_user_order
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_ckd_user_order`;
CREATE TABLE `m_ai_ckd_user_order` (
                                       `id` bigint NOT NULL,
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `goods_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员版本 9.9， 99， 199',
                                       `goods_price` int DEFAULT NULL COMMENT '价格，单位分',
                                       `payment_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付状态  成功 success,   未支付 noPay',
                                       `open_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                       `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
                                       `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ckd会员订单';

-- ----------------------------
-- Table structure for m_ai_ckd_user_share
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_ckd_user_share`;
CREATE TABLE `m_ai_ckd_user_share` (
                                       `id` bigint NOT NULL,
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `share_user_id` bigint DEFAULT NULL COMMENT '分享人ID',
                                       `fan_users` bigint DEFAULT NULL COMMENT '粉丝用户',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='ckd会员分享成功转换记录';

-- ----------------------------
-- Table structure for m_ai_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge`;
CREATE TABLE `m_ai_knowledge` (
                                  `id` bigint NOT NULL,
                                  `know_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '知识库名称',
                                  `know_dify_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '知识库dify的Id',
                                  `know_user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                  `know_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '知识库类型(专业学术资料，个人成果，病例库，日常收集)',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                  `view_permissions` int DEFAULT NULL COMMENT '查看需要的用户等级',
                                  `download_permission` int DEFAULT NULL COMMENT '下载需要的用户权限',
                                  `metadata_ownership_id` varchar(400) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '知识库元数据所属ID',
                                  `metadata_category_id` varchar(400) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '知识库元数据分类ID',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  KEY `knowledge_user_id` (`know_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='dify知识库关联表';

-- ----------------------------
-- Table structure for m_ai_knowledge_activity_consult
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_activity_consult`;
CREATE TABLE `m_ai_knowledge_activity_consult` (
                                                   `id` bigint NOT NULL,
                                                   `menu_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二级平台域名',
                                                   `activity_consult_title` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活动咨询标题',
                                                   `consult_cover` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '咨询封面',
                                                   `consult_sort` int DEFAULT NULL COMMENT '咨询排序从小到大排序',
                                                   `consult_type` bigint DEFAULT NULL COMMENT '咨询分类',
                                                   `consult_show` int DEFAULT NULL COMMENT '咨询是否显示',
                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间 倒序',
                                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                   `cms_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '文章内容',
                                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动咨询内容';

-- ----------------------------
-- Table structure for m_ai_knowledge_activity_type
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_activity_type`;
CREATE TABLE `m_ai_knowledge_activity_type` (
                                                `id` bigint NOT NULL,
                                                `menu_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二级平台域名',
                                                `activity_type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活动分类名称',
                                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                `activity_show_status` int DEFAULT '0' COMMENT '是否显示',
                                                `activity_sort` int DEFAULT '0' COMMENT '排序',
                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普活动分类';

-- ----------------------------
-- Table structure for m_ai_knowledge_admin
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_admin`;
CREATE TABLE `m_ai_knowledge_admin` (
                                        `id` bigint NOT NULL,
                                        `menu_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '平台域名',
                                        `user_account` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号',
                                        `user_password` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普管理员';

-- ----------------------------
-- Table structure for m_ai_knowledge_banner
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_banner`;
CREATE TABLE `m_ai_knowledge_banner` (
                                         `id` bigint NOT NULL,
                                         `menu_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二级平台域名',
                                         `banner_cover` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'banner封面',
                                         `banner_jump_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'banner跳转链接',
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                         `banner_show_status` int DEFAULT '0' COMMENT 'banner是否显示',
                                         `banner_sort` int DEFAULT '0' COMMENT 'banner排序',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普banner';

-- ----------------------------
-- Table structure for m_ai_knowledge_cms_article
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_cms_article`;
CREATE TABLE `m_ai_knowledge_cms_article` (
                                              `id` bigint NOT NULL,
                                              `menu_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二级平台域名',
                                              `cms_article_title` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'cms文章标题',
                                              `cms_cover` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'cms文章封面',
                                              `cms_sort` int DEFAULT NULL COMMENT 'cms文章排序从小到大排序',
                                              `cms_type` bigint DEFAULT NULL COMMENT 'cms分类',
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间 倒序',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `cms_label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'cms标签(待审核，已收录，优选文章)',
                                              `cms_content` longtext COLLATE utf8mb4_unicode_ci COMMENT '文章内容',
                                              `know_user_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '博主姓名',
                                              `know_user_id` bigint DEFAULT NULL COMMENT '博主ID',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普文章内容';

-- ----------------------------
-- Table structure for m_ai_knowledge_cms_type
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_cms_type`;
CREATE TABLE `m_ai_knowledge_cms_type` (
                                           `id` bigint NOT NULL,
                                           `menu_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二级平台域名',
                                           `cms_type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'cms分类名称',
                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                           `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                           `cms_show_status` int DEFAULT '0' COMMENT '是否显示',
                                           `cms_sort` int DEFAULT '0' COMMENT '排序',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普文章分类';

-- ----------------------------
-- Table structure for m_ai_knowledge_daily_collection
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_daily_collection`;
CREATE TABLE `m_ai_knowledge_daily_collection` (
                                                   `id` bigint NOT NULL,
                                                   `know_id` bigint DEFAULT NULL,
                                                   `text_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '语音文本标题',
                                                   `text_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '语音文本内容',
                                                   `user_id` bigint DEFAULT NULL COMMENT '所属用户ID',
                                                   `file_upload_time` datetime DEFAULT NULL COMMENT '上传时间',
                                                   `dify_file_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档状态 启用 禁用',
                                                   `dify_file_index_progress` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档索引进度',
                                                   `dify_batch` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'dify文档的批次号batch',
                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                   `file_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '语音文件的路径',
                                                   `document_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'dify文档的ID',
                                                   `know_dify_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'dify知识库ID',
                                                   `key_word` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '关键词',
                                                   `view_permissions` int DEFAULT NULL COMMENT '查看需要的用户等级',
                                                   `download_permission` int DEFAULT NULL COMMENT '下载需要的用户权限',
                                                   `is_update_permissions` tinyint(1) DEFAULT '0' COMMENT '文档权限是否自定义更新',
                                                   `audio_duration` int DEFAULT NULL COMMENT '录音时长',
                                                   `case_recording_dialogue_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '病例录音内容',
                                                   `data_type` int DEFAULT '1' COMMENT '数据类型(1 正常日常收集， 2 病历录音内容)',
                                                   PRIMARY KEY (`id`) USING BTREE,
                                                   KEY `daily_collection_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日常收集文本内容';

-- ----------------------------
-- Table structure for m_ai_knowledge_file
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_file`;
CREATE TABLE `m_ai_knowledge_file` (
                                       `id` bigint NOT NULL,
                                       `know_id` bigint DEFAULT NULL COMMENT '业务知识库id',
                                       `know_dify_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '知识库dify的Id',
                                       `file_user_id` bigint DEFAULT NULL COMMENT '所属用户ID',
                                       `know_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '知识类型(专业学术资料，个人成果，病例库，日常收集)',
                                       `file_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
                                       `file_size` bigint DEFAULT NULL COMMENT '文件大小(kb)',
                                       `file_upload_time` datetime DEFAULT NULL COMMENT '上传时间',
                                       `dify_file_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档状态 启用 禁用',
                                       `dify_file_index_progress` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档索引进度',
                                       `dify_batch` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'dify文档的批次号batch',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `dify_file_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'dify的文档ID',
                                       `file_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件的链接地址',
                                       `initial_file_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
                                       `document_belongs` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档的所属 system 系统； user 用户的',
                                       `view_permissions` int DEFAULT NULL COMMENT '查看需要的用户等级',
                                       `download_permission` int DEFAULT NULL COMMENT '下载需要的用户权限',
                                       `is_update_permissions` tinyint(1) DEFAULT '0' COMMENT '文档权限是否自定义更新',
                                       `try_count` int DEFAULT '0' COMMENT '索引重试次数，超过3次，不在重试',
                                       `system_file_id` bigint DEFAULT NULL COMMENT '系统文件ID',
                                       `file_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'TEXT_FILE' COMMENT '文件类型',
                                       `file_cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频封面',
                                       `set_metadata` tinyint DEFAULT NULL COMMENT '设置元数据',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `knowledge_file_user_id` (`file_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='dify知识库文档关联表';

-- ----------------------------
-- Table structure for m_ai_knowledge_file_academic_materials_label
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_file_academic_materials_label`;
CREATE TABLE `m_ai_knowledge_file_academic_materials_label` (
                                                                `id` bigint NOT NULL,
                                                                `language_` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '语言',
                                                                `research_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '研究类型',
                                                                `key_word` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关键词',
                                                                `release_time` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发布时间',
                                                                `conference_journal_name` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '期刊/会议名称',
                                                                `author_` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者',
                                                                `know_user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                                `file_id` bigint DEFAULT NULL COMMENT '文件ID',
                                                                `know_id` bigint DEFAULT NULL COMMENT '业务知识库ID',
                                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文档专业学术资料标签表';

-- ----------------------------
-- Table structure for m_ai_knowledge_file_case_database_label
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_file_case_database_label`;
CREATE TABLE `m_ai_knowledge_file_case_database_label` (
                                                           `id` bigint NOT NULL,
                                                           `language_` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '语言',
                                                           `diagnostic_results` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '诊断结果',
                                                           `ji_lu_time` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '记录时间',
                                                           `treatment_plan` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '治疗方案',
                                                           `key_symptoms` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关键症状',
                                                           `case_type` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '病例类型',
                                                           `treatment_outcome` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '治疗结果',
                                                           `case_source` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '病例来源',
                                                           `post_status` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发表状态',
                                                           `follow_up_results` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '随访结果',
                                                           `author_` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者',
                                                           `know_user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                           `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                           `file_id` bigint DEFAULT NULL COMMENT '文件ID',
                                                           `know_id` bigint DEFAULT NULL COMMENT '业务知识库ID',
                                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='病例库资料标签';

-- ----------------------------
-- Table structure for m_ai_knowledge_file_personal_achievements_label
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_file_personal_achievements_label`;
CREATE TABLE `m_ai_knowledge_file_personal_achievements_label` (
                                                                   `id` bigint NOT NULL,
                                                                   `language_` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '语言',
                                                                   `key_word` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关键词',
                                                                   `release_time` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发布时间',
                                                                   `personal_achievement_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型',
                                                                   `author_` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者',
                                                                   `know_user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                                   `file_id` bigint DEFAULT NULL COMMENT '文件ID',
                                                                   `know_id` bigint DEFAULT NULL COMMENT '业务知识库ID',
                                                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文档个人成果标签表';

-- ----------------------------
-- Table structure for m_ai_knowledge_jcr_cas
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_jcr_cas`;
CREATE TABLE `m_ai_knowledge_jcr_cas` (
                                          `id` int unsigned NOT NULL AUTO_INCREMENT,
                                          `journal_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '期刊的全名',
                                          `jcr_abbr_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '期刊的缩写名',
                                          `issn` varchar(54) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '期刊的国际标准序列号-印刷版',
                                          `eissn` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '期刊的国际标准序列号-电子版',
                                          `jif` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '期刊影响因子',
                                          `jif_5_years` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '期刊五年影响因子',
                                          `category` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '期刊所属的学科类别',
                                          `jif_quartile` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '期刊分区',
                                          `jcr_rank` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '期刊排名',
                                          `cas_sub_category` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '中科院小类',
                                          `cas_category` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '中科院大类',
                                          `cas_category_quartile` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '中科院大类期刊分区',
                                          `cas_sub_category_quartile` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '中科院小类期刊分区',
                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21801 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='jcr和cas分区信息表';

-- ----------------------------
-- Table structure for m_ai_knowledge_menu
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_menu`;
CREATE TABLE `m_ai_knowledge_menu` (
                                       `id` bigint NOT NULL,
                                       `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单名称',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `parent_id` bigint DEFAULT NULL COMMENT '父级ID',
                                       `sort_` int DEFAULT NULL COMMENT '从小到大排序',
                                       `domain_id` bigint DEFAULT NULL COMMENT '平台ID',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库菜单';

-- ----------------------------
-- Table structure for m_ai_knowledge_menu_domain
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_menu_domain`;
CREATE TABLE `m_ai_knowledge_menu_domain` (
                                              `id` bigint NOT NULL,
                                              `menu_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二级平台名称',
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `menu_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二级平台域名',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库专科域名';

-- ----------------------------
-- Table structure for m_ai_knowledge_question_evidence
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_question_evidence`;
CREATE TABLE `m_ai_knowledge_question_evidence` (
                                                    `id` bigint NOT NULL,
                                                    `user_id` bigint NOT NULL COMMENT '用户id',
                                                    `question_id` bigint NOT NULL COMMENT '问题id',
                                                    `document_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文献名称',
                                                    `publication_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发表时间',
                                                    `study_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '研究类型',
                                                    `if_value` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IF值',
                                                    `cas_large_category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '中科院大类',
                                                    `cas_subclass` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '中科院小类',
                                                    `jcr_partition` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'JCR分区',
                                                    `journal` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '期刊',
                                                    `author` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '作者',
                                                    `summary_` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '摘要',
                                                    `doi` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'DOI',
                                                    `file_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原文链接',
                                                    `issn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '国际标准连续出版物号',
                                                    `eissn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '国际标准连续出版物号-电子版',
                                                    `cas_category_quartile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '中科院大类分区',
                                                    `cas_sub_category_quartile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '中科院小类分区',
                                                    `update_time` datetime DEFAULT NULL,
                                                    `update_user` bigint DEFAULT NULL,
                                                    `create_time` datetime DEFAULT NULL,
                                                    `create_user` bigint DEFAULT NULL,
                                                    `pmid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'PubmedArticleID',
                                                    `translate_title` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '翻译的标题',
                                                    `translate_abstract` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '翻译得到的摘要',
                                                    `ai_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'AI总结',
                                                    `ai_summary_status` tinyint(1) DEFAULT NULL COMMENT 'AI总结状态',
                                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库问题证据';

-- ----------------------------
-- Table structure for m_ai_knowledge_subscribe_update_message
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_subscribe_update_message`;
CREATE TABLE `m_ai_knowledge_subscribe_update_message` (
                                                           `id` bigint NOT NULL,
                                                           `knowledge_user_id` bigint DEFAULT NULL COMMENT '知识库博主ID',
                                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                           `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                           `update_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '修改记录说明',
                                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博主订阅设置修改记录';

-- ----------------------------
-- Table structure for m_ai_knowledge_system_file
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_system_file`;
CREATE TABLE `m_ai_knowledge_system_file` (
                                              `id` bigint NOT NULL,
                                              `know_dify_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '知识库dify的Id',
                                              `know_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '知识类型(专业学术资料，个人成果，病例库，日常收集)',
                                              `file_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
                                              `file_size` bigint DEFAULT NULL COMMENT '文件大小(kb)',
                                              `file_upload_time` datetime DEFAULT NULL COMMENT '上传时间',
                                              `dify_file_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档状态 启用 禁用',
                                              `dify_file_index_progress` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档索引进度',
                                              `dify_batch` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'dify文档的批次号batch',
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `dify_file_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'dify的文档ID',
                                              `file_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件的链接地址',
                                              `initial_file_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库系统文件表';

-- ----------------------------
-- Table structure for m_ai_knowledge_user
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_user`;
CREATE TABLE `m_ai_knowledge_user` (
                                       `id` bigint NOT NULL,
                                       `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名称',
                                       `user_mobile` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户手机号',
                                       `user_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户类型 主任 普通医生',
                                       `user_domain` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户(一个主任一个租户)',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
                                       `membership_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员版本',
                                       `user_business_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '博主名片的图片地址',
                                       `membership_expiration` datetime DEFAULT NULL COMMENT '会员到期时间',
                                       `experience_ai_time` datetime DEFAULT NULL COMMENT '体验版体验AI工具的截止时间',
                                       `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信openId, 通过domain数据隔离',
                                       `share_poster` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '博主知识库的海报地址',
                                       `password` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
                                       `basic_membership_price` int DEFAULT '0' COMMENT '月度会员，单位是分',
                                       `professional_version_price` int DEFAULT '0' COMMENT '年度会员，单位是分',
                                       `user_set_ui_style` int DEFAULT '0' COMMENT '用户选择的UI模板',
                                       `number_views` bigint DEFAULT '0' COMMENT '浏览次数',
                                       `work_unit` varchar(400) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作单位',
                                       `department` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '科室',
                                       `doctor_title` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职称',
                                       `personal_profile` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介',
                                       `ai_studio_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转到AI工作室',
                                       `digital_studio_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数字工作室',
                                       `other_url` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '其他链接',
                                       `sort_` int DEFAULT NULL COMMENT '排序',
                                       `ai_studio_doctor_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ai工作室医生id',
                                       `ai_interactive_audio_trial_listening` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'AI交互试听音频',
                                       `greeting_video` varchar(400) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '打招呼视频',
                                       `greeting_video_cover` varchar(400) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '打招呼视频封面',
                                       `real_human_avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真人头像',
                                       `knowledge_menu_id` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '总平台 菜单id',
                                       `menu_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '总平台域名',
                                       `specialty` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '擅长',
                                       `subscribe_switch` tinyint DEFAULT '0' COMMENT '订阅开关',
                                       `subscribe_last_update_time` datetime DEFAULT NULL COMMENT '订阅最后一次修改时间',
                                       `subscribe_user_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订阅会员名称',
                                       `open_monthly_payment` tinyint DEFAULT '1' COMMENT '开启月度付费',
                                       `open_annual_payment` tinyint DEFAULT '1' COMMENT '开启年度付费',
                                       `open_article_data` tinyint DEFAULT '1' COMMENT '开放科普患教数据',
                                       `open_academic_materials` tinyint DEFAULT '1' COMMENT '开放学术资料',
                                       `open_personal_achievements` tinyint DEFAULT '1' COMMENT '开放个人成果',
                                       `open_case_database` tinyint DEFAULT '1' COMMENT '开放病例分享',
                                       `open_daily_collection` tinyint DEFAULT '0' COMMENT '开放日常收集',
                                       `show_in_docuknow` tinyint DEFAULT '1' COMMENT '是否展示在聚合页',
                                       `data_source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'self_built' COMMENT '数据来源 ai工作室， 赛柏蓝。自建',
                                       `third_party_user_id` bigint DEFAULT NULL COMMENT '第三方平台博主ID',
                                       `third_party_knowledge_link` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第三方平台博主主页',
                                       `child_domain_menu_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '子平台菜单分类',
                                       `child_domain` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '子平台域名',
                                       `open_text_ual` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '开放文献解读',
                                       `total_call_duration` int DEFAULT '100' COMMENT '总通话时长 分钟数',
                                       `call_switch` int DEFAULT '1' COMMENT '通话功能开关 0关闭  1 开启',
                                       `saas_tenant_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '博主在saas的医生项目租户',
                                       `sms_code_login_first_time` datetime DEFAULT NULL COMMENT '短信验证码首次登录时间',
                                       `sms_code_login_last_time` datetime DEFAULT NULL COMMENT '短信验证码最后登录时间',
                                       `upload_qualification_remind_time` datetime DEFAULT NULL COMMENT '上次提醒上传资质时间',
                                       `agree_agreement` int DEFAULT '0' COMMENT '是否同意协议',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `knowledge_user_mobile` (`user_mobile`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库用户';

-- ----------------------------
-- Table structure for m_ai_knowledge_user_browsing_history
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_user_browsing_history`;
CREATE TABLE `m_ai_knowledge_user_browsing_history` (
                                                        `id` bigint NOT NULL,
                                                        `user_id` bigint NOT NULL COMMENT '用户id',
                                                        `problem_evidence_id` bigint NOT NULL COMMENT '问题证据id',
                                                        `view_time` datetime NOT NULL COMMENT '浏览时间',
                                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库证据浏览记录';

-- ----------------------------
-- Table structure for m_ai_knowledge_user_domain
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_user_domain`;
CREATE TABLE `m_ai_knowledge_user_domain` (
                                              `id` bigint NOT NULL,
                                              `domain_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二级域名名称',
                                              `domain_menu_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二级域名菜单分类',
                                              `know_user_id` bigint DEFAULT NULL COMMENT '博主ID',
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `sort_` int DEFAULT '0' COMMENT '从小到大排序',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库博主子平台分类';

-- ----------------------------
-- Table structure for m_ai_knowledge_user_order
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_user_order`;
CREATE TABLE `m_ai_knowledge_user_order` (
                                             `id` bigint NOT NULL,
                                             `user_id` bigint NOT NULL COMMENT '用户id',
                                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                             `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                             `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                             `see_uid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'seeUid',
                                             `goods_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品类型 体验版 trialVersion 专业版 professionalEdition',
                                             `goods_price` int DEFAULT NULL COMMENT '价格，单位分',
                                             `payment_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付状态  成功 success,   未支付 noPay',
                                             `deductible_amount` int DEFAULT NULL COMMENT '抵扣金额',
                                             `user_domain` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单所属域名',
                                             `payment_time` datetime DEFAULT NULL COMMENT '订单支付时间',
                                             `membership_expiration` datetime DEFAULT NULL COMMENT '用户会员到期时间',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库用户购买会员订单';

-- ----------------------------
-- Table structure for m_ai_knowledge_user_qualification
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_user_qualification`;
CREATE TABLE `m_ai_knowledge_user_qualification` (
                                                     `id` bigint NOT NULL,
                                                     `file_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
                                                     `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件链接',
                                                     `know_user_id` bigint DEFAULT NULL COMMENT '博主ID',
                                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库博主资质';

-- ----------------------------
-- Table structure for m_ai_knowledge_user_question
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_user_question`;
CREATE TABLE `m_ai_knowledge_user_question` (
                                                `id` bigint NOT NULL,
                                                `user_id` bigint NOT NULL COMMENT '用户id',
                                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                `question_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '问题描述',
                                                `question_analyze_status` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '问题分析状态',
                                                `question_quantity_evidence` int DEFAULT NULL COMMENT '问题证据数量',
                                                `conversation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会话',
                                                `is_new_question` int DEFAULT NULL COMMENT '是否本组最新问题',
                                                `uid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'uid',
                                                `evidence_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '证据列表ID',
                                                `start_search_time` datetime DEFAULT NULL COMMENT '搜索的开始时间',
                                                `finish_search_time` datetime DEFAULT NULL COMMENT '搜索的结束时间',
                                                `question_keyword` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '问题的keyword',
                                                `keyword_expire_time` datetime DEFAULT NULL COMMENT 'keyword的有效期',
                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库用户问题';

-- ----------------------------
-- Table structure for m_ai_knowledge_user_subscribe
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_knowledge_user_subscribe`;
CREATE TABLE `m_ai_knowledge_user_subscribe` (
                                                 `id` bigint NOT NULL,
                                                 `knowledge_user_id` bigint DEFAULT NULL COMMENT '知识库用户ID',
                                                 `user_domain` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户(一个主任一个租户)',
                                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                 `membership_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员版本',
                                                 `membership_expiration` datetime DEFAULT NULL COMMENT '会员到期时间',
                                                 `experience_ai_time` datetime DEFAULT NULL COMMENT '体验版体验AI工具的截止时间',
                                                 `subscribe_status` tinyint DEFAULT NULL COMMENT '用户订阅博主状态',
                                                 `subscribe_time` datetime DEFAULT NULL COMMENT '订阅时间',
                                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库用户订阅关系表';

-- ----------------------------
-- Table structure for m_ai_megvii_digital_people
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_megvii_digital_people`;
CREATE TABLE `m_ai_megvii_digital_people` (
                                              `id` bigint NOT NULL,
                                              `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片url',
                                              `user_id` bigint DEFAULT NULL COMMENT '用户',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数字人';

-- ----------------------------
-- Table structure for m_ai_megvii_fusion_diagram
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_megvii_fusion_diagram`;
CREATE TABLE `m_ai_megvii_fusion_diagram` (
                                              `id` bigint NOT NULL,
                                              `template_diagram_type_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '人脸融合分类ids',
                                              `image_base64` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '人脸融合图base64',
                                              `error_message` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '失败的异常信息',
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `user_id` bigint DEFAULT NULL,
                                              `create_image_total` int DEFAULT NULL,
                                              `finish_image_total` int DEFAULT NULL,
                                              `task_status` int DEFAULT NULL,
                                              `out_trade_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预支付交易会话标识',
                                              `delete_status` int DEFAULT '0' COMMENT '删除标识',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='融合图管理';

-- ----------------------------
-- Table structure for m_ai_megvii_fusion_diagram_result
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_megvii_fusion_diagram_result`;
CREATE TABLE `m_ai_megvii_fusion_diagram_result` (
                                                     `id` bigint NOT NULL,
                                                     `fusion_diagram_id` bigint NOT NULL COMMENT '融合图ID',
                                                     `image_base64` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '融合图结果',
                                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                     `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                                     `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                                                     `image_obs_url` varchar(350) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片的obs地址',
                                                     `fusion_diagram_type_id` bigint DEFAULT NULL,
                                                     `fusion_diagram_template_id` bigint DEFAULT NULL,
                                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='融合图结果';

-- ----------------------------
-- Table structure for m_ai_megvii_merge_image_free_frequency
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_megvii_merge_image_free_frequency`;
CREATE TABLE `m_ai_megvii_merge_image_free_frequency` (
                                                          `id` bigint NOT NULL COMMENT '小程序用户ID',
                                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                          `free_merge_total` int DEFAULT NULL,
                                                          `user_id` bigint NOT NULL COMMENT '用户ID',
                                                          PRIMARY KEY (`id`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='融合图片免费次数';

-- ----------------------------
-- Table structure for m_ai_megvii_template_diagram
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_megvii_template_diagram`;
CREATE TABLE `m_ai_megvii_template_diagram` (
                                                `id` bigint NOT NULL,
                                                `order_` int DEFAULT NULL COMMENT '优先级',
                                                `image_base64` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '模版图的base64编码',
                                                `face_rectangle` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '人脸矩形框的位置',
                                                `landmark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '人脸的关键点坐标数组',
                                                `attributes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '人脸属性特征',
                                                `template_diagram_type` bigint DEFAULT NULL COMMENT '融合图分类',
                                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Male 男, Female 女',
                                                `image_obs_url` varchar(350) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片的obs地址',
                                                `skin` float(5,2) DEFAULT NULL COMMENT '	美化效果,支持[0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]',
  `gpen` float(5,2) DEFAULT NULL COMMENT '高清效果,支持[0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模版图管理';

-- ----------------------------
-- Table structure for m_ai_megvii_template_diagram_type
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_megvii_template_diagram_type`;
CREATE TABLE `m_ai_megvii_template_diagram_type` (
                                                     `id` bigint NOT NULL,
                                                     `order_` int DEFAULT NULL COMMENT '优先级',
                                                     `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类名称',
                                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                     `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Male 男, Female 女',
                                                     `free_` int DEFAULT NULL COMMENT '是否免费 1 免费， 0 收费',
                                                     `cost_` int DEFAULT NULL COMMENT '价格 单位分',
                                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模版图分类';

-- ----------------------------
-- Table structure for m_ai_miniapp_user_chat
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_miniapp_user_chat`;
CREATE TABLE `m_ai_miniapp_user_chat` (
                                          `id` bigint NOT NULL,
                                          `history_id` bigint NOT NULL COMMENT '对话ID，不存在时自动生成一次对话',
                                          `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会话标识',
                                          `create_time` timestamp(3) NULL DEFAULT NULL COMMENT '创建时间',
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                          `update_time` timestamp(3) NULL DEFAULT NULL COMMENT '更新时间',
                                          `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `sender_id` bigint DEFAULT NULL COMMENT '发送者ID',
                                          `current_user_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录的用户类型(miniappuser, doctor)',
                                          `sender_role_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'AI交互中两者的角色(user, ai)',
                                          `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '消息内容',
                                          `type_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型(文本)',
                                          `send_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送状态(发送成功，发送失败，发送中)',
                                          `send_error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '发送失败时的收集信息',
                                          `reply_msg_id` bigint DEFAULT NULL COMMENT '回复的消息ID',
                                          `reply_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复的消息状态(成功 失败)',
                                          `more_question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '更多追问',
                                          `reference` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '参考资料',
                                          `refer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '相关搜索',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `i_ai_miniapp_user_dx_uid` (`session_id`) USING BTREE,
                                          KEY `i_ai_miniapp_user_history_id` (`history_id`) USING BTREE,
                                          KEY `I_AI_MINIAPP_USER_CHAT_SENDERID` (`sender_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='敏智用户聊天记录';

-- ----------------------------
-- Table structure for m_ai_miniapp_user_chat_history_group
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_miniapp_user_chat_history_group`;
CREATE TABLE `m_ai_miniapp_user_chat_history_group` (
                                                        `id` bigint NOT NULL,
                                                        `create_time` timestamp(3) NULL DEFAULT NULL COMMENT '创建时间',
                                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                        `update_time` timestamp(3) NULL DEFAULT NULL COMMENT '更新时间',
                                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                        `sender_id` bigint DEFAULT NULL COMMENT '发送者ID',
                                                        `current_user_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录的用户类型(miniappuser, doctor)',
                                                        `content_title` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '对话的历史标题',
                                                        PRIMARY KEY (`id`) USING BTREE,
                                                        KEY `I_AI_MINIAPP_USER_CHAT_SENDERID` (`sender_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='敏智用户历史对话';

-- ----------------------------
-- Table structure for m_ai_podcast
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_podcast`;
CREATE TABLE `m_ai_podcast` (
                                `id` bigint NOT NULL,
                                `step` int DEFAULT NULL COMMENT '制作的步骤',
                                `task_status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务状态 生成对话，制作音频',
                                `podcast_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '播客名称',
                                `podcast_input_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '播客素材类型 文章链接，文档，文字输入',
                                `podcast_input_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '文章链接或文档url',
                                `podcast_input_text_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '文字内容',
                                `podcast_input_file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档的文件名称',
                                `podcast_input_file_size` int DEFAULT NULL COMMENT '文档的文件大小（KB）',
                                `podcast_style` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '播客风格',
                                `podcast_preview_contents` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '预览内容',
                                `podcast_final_audio_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '播客最终音频地址',
                                `podcast_audio_task_number` int DEFAULT NULL COMMENT '音频任务总数',
                                `user_id` bigint NOT NULL COMMENT '所属用户ID',
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                `merge_audio_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '合并音频状态',
                                `merge_audio_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '合并音频失败信息',
                                `podcast_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'article' COMMENT '科普创作/文献解读/易智声',
                                `show_ai_knowledge` tinyint DEFAULT '0' COMMENT '是否显示到docuKnow',
                                `fail_refund` int DEFAULT '0' COMMENT '失败是否退费',
                                `podcast_model` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '速听精华/深度发现',
                                `podcast_language` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '中文、英文',
                                `release_status` int DEFAULT '0' COMMENT '易智声是否发布',
                                `show_number` int DEFAULT NULL COMMENT '查看次数',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播客';

-- ----------------------------
-- Table structure for m_ai_podcast_audio_task
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_podcast_audio_task`;
CREATE TABLE `m_ai_podcast_audio_task` (
                                           `id` bigint NOT NULL,
                                           `podcast_id` bigint NOT NULL COMMENT '播客ID',
                                           `audio_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '音频文本',
                                           `sound_set_id` bigint NOT NULL COMMENT '声音设置ID',
                                           `task_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频任务ID',
                                           `task_status` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '音频任务状态',
                                           `audio_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '临时音频文件路径',
                                           `task_error_message` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务异常消息',
                                           `task_sort` int DEFAULT NULL COMMENT '任务顺序',
                                           `user_id` bigint NOT NULL COMMENT '所属用户ID',
                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                           `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播客音频任务';

-- ----------------------------
-- Table structure for m_ai_podcast_sound_set
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_podcast_sound_set`;
CREATE TABLE `m_ai_podcast_sound_set` (
                                          `id` bigint NOT NULL,
                                          `podcast_id` bigint NOT NULL COMMENT '播客ID',
                                          `role_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色名字',
                                          `role_title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色头衔',
                                          `role_gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色性别',
                                          `role_sound_set` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色声音',
                                          `user_voice_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户克隆声音id',
                                          `user_id` bigint NOT NULL COMMENT '所属用户ID',
                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播客声音设置';

-- ----------------------------
-- Table structure for m_ai_sms_task
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_sms_task`;
CREATE TABLE `m_ai_sms_task` (
                                 `id` bigint NOT NULL COMMENT '短信记录ID',
                                 `status` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `receiver` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '接收者手机号',
                                 `template_params` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `send_time` datetime DEFAULT NULL COMMENT '发送时间',
                                 `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `create_user` bigint DEFAULT '0' COMMENT '创建人ID',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_user` bigint DEFAULT '0' COMMENT '最后修改人',
                                 `update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
                                 `biz_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回调ID',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI短信验证码表';

-- ----------------------------
-- Table structure for m_ai_textual_interpretation_ppt_task
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_textual_interpretation_ppt_task`;
CREATE TABLE `m_ai_textual_interpretation_ppt_task` (
                                                        `id` bigint NOT NULL,
                                                        `step` int DEFAULT NULL COMMENT '创作的步骤',
                                                        `file_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传文件名称',
                                                        `title` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '选择的标题',
                                                        `task_status` int DEFAULT NULL COMMENT '任务状态 0 创作中 1 创作完成 2 存为草稿',
                                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                        `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '生产的ppt大纲',
                                                        `show_ai_knowledge` int DEFAULT '0' COMMENT '是否展示到ai医生数字分身平台',
                                                        `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传的文献地址',
                                                        `ppt_url` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '做好的ppt地址',
                                                        `coze_file_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件在coze的id',
                                                        `uid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会话ID',
                                                        `ppt_task_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ppt的任务ID',
                                                        `ppt_task_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ppt任务状态1 进行中2 已完成 3 失败',
                                                        `last_query_status_time` datetime DEFAULT NULL COMMENT '最后一次查询状态的时间',
                                                        `ppt_data_result` longtext COLLATE utf8mb4_unicode_ci COMMENT '第三方的ppt数据',
                                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文献解读PPT';

-- ----------------------------
-- Table structure for m_ai_textual_interpretation_text_task
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_textual_interpretation_text_task`;
CREATE TABLE `m_ai_textual_interpretation_text_task` (
                                                         `id` bigint NOT NULL,
                                                         `step` int DEFAULT NULL COMMENT '创作的步骤',
                                                         `file_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传文件名称',
                                                         `title` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '选择的标题',
                                                         `task_status` int DEFAULT NULL COMMENT '任务状态 0 创作中 1 创作完成 2 存为草稿',
                                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                         `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '生产的文稿',
                                                         `show_ai_knowledge` int DEFAULT '0' COMMENT '是否展示到ai医生数字分身平台',
                                                         `coze_file_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件在coze的id',
                                                         `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件在saas的路径',
                                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文献解读txt';

-- ----------------------------
-- Table structure for m_ai_textual_interpretation_user
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_textual_interpretation_user`;
CREATE TABLE `m_ai_textual_interpretation_user` (
                                                    `id` bigint NOT NULL,
                                                    `user_mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户手机号',
                                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                    `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                    `password` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
                                                    `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
                                                    `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
                                                    `user_gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户性别',
                                                    `personal_profile` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介',
                                                    `membership_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Annual_Membership' COMMENT '会员等级',
                                                    `membership_expiration` datetime DEFAULT '2099-05-06 16:11:51' COMMENT '会员到期时间',
                                                    `energy_beans` bigint DEFAULT '0' COMMENT '会员能量豆',
                                                    `free_energy_beans` bigint DEFAULT '0' COMMENT '注册用户赠送的20能量豆',
                                                    `first_creation` tinyint DEFAULT '0' COMMENT '是否完成首次创作',
                                                    `day_7_active` tinyint DEFAULT '0' COMMENT '第7天是否活跃',
                                                    `day_30_active` tinyint DEFAULT '0' COMMENT '第30天是否活跃',
                                                    `first_creation_date` datetime DEFAULT NULL COMMENT '完成首次创作的日期',
                                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文献解读用户表';

-- ----------------------------
-- Table structure for m_ai_textual_interpretation_user_consumption
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_textual_interpretation_user_consumption`;
CREATE TABLE `m_ai_textual_interpretation_user_consumption` (
                                                                `id` bigint NOT NULL,
                                                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                                `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                                                `consumption_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消费类型',
                                                                `consumption_amount` int DEFAULT NULL COMMENT '消耗能量币数量',
                                                                `message_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文献解读能量豆消费记录';

-- ----------------------------
-- Table structure for m_ai_textual_interpretation_user_notice
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_textual_interpretation_user_notice`;
CREATE TABLE `m_ai_textual_interpretation_user_notice` (
                                                           `id` bigint NOT NULL,
                                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                           `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                           `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                                           `read_status` tinyint DEFAULT NULL COMMENT '是否已读',
                                                           `notice_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息类型',
                                                           `notice_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知内容',
                                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文献解读系统通知';

-- ----------------------------
-- Table structure for m_ai_textual_interpretation_user_order
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_textual_interpretation_user_order`;
CREATE TABLE `m_ai_textual_interpretation_user_order` (
                                                          `id` bigint NOT NULL,
                                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                          `goods_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员充值，能量币充值',
                                                          `goods_price` int DEFAULT NULL COMMENT '价格，单位分',
                                                          `payment_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付状态  成功 success,   未支付 noPay',
                                                          `open_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                                          `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
                                                          `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
                                                          `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'uid',
                                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文献解读用户支付订单';

-- ----------------------------
-- Table structure for m_ai_textual_interpretation_user_voice
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_textual_interpretation_user_voice`;
CREATE TABLE `m_ai_textual_interpretation_user_voice` (
                                                          `id` bigint NOT NULL,
                                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                          `user_id` bigint DEFAULT NULL COMMENT '用户id',
                                                          `voice_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频链接',
                                                          `default_voice` tinyint unsigned DEFAULT '0' COMMENT '{"是_true", "否_false", "_null"}',
                                                          `h_file_id` bigint DEFAULT NULL COMMENT '海螺平台文件id',
                                                          `h_voice_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '海螺声音克隆id',
                                                          `h_demo_audio` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '克隆试听音频链接',
                                                          `h_has_clone` tinyint unsigned DEFAULT '0' COMMENT '海螺是否克隆：1已克隆 0未克隆',
                                                          `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频名称',
                                                          `fail_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '失败原因',
                                                          `delete_status` tinyint DEFAULT '0' COMMENT '删除状态',
                                                          `use_count` int DEFAULT '0' COMMENT '使用次数',
                                                          `h_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'kailing' COMMENT '海螺账户',
                                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='文献解读用户声音';

-- ----------------------------
-- Table structure for m_ai_user_join
-- ----------------------------
DROP TABLE IF EXISTS `m_ai_user_join`;
CREATE TABLE `m_ai_user_join` (
                                  `id` bigint NOT NULL,
                                  `business_card_id` bigint DEFAULT NULL COMMENT '科普名片id',
                                  `ai_know_user_id` bigint DEFAULT NULL COMMENT 'AI分身平台用户ID',
                                  `ai_studio_tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ai工作室医生租户',
                                  `ai_studio_doctor_id` bigint DEFAULT NULL COMMENT 'ai工作室医生ID',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ai用户关联表';

-- ----------------------------
-- Table structure for m_msg_baidu_bot_doctor_chat
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_baidu_bot_doctor_chat`;
CREATE TABLE `m_msg_baidu_bot_doctor_chat` (
                                               `id` bigint NOT NULL,
                                               `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会话标识',
                                               `create_time` timestamp(3) NULL DEFAULT NULL COMMENT '创建时间',
                                               `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                               `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                               `update_time` timestamp(3) NULL DEFAULT NULL COMMENT '更新时间',
                                               `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `sender_id` bigint DEFAULT NULL COMMENT '发送者ID',
                                               `sender_role_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息角色(doctor, ai)',
                                               `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '消息内容',
                                               `type_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型(文本)',
                                               `send_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送状态(发送成功，发送失败，发送中)',
                                               `send_error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '发送失败时的收集信息',
                                               `reply_msg_id` bigint DEFAULT NULL COMMENT '回复的消息ID',
                                               `reply_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复的消息状态(成功 失败)',
                                               PRIMARY KEY (`id`) USING BTREE,
                                               KEY `idx_uid` (`session_id`) USING BTREE,
                                               KEY `I_BAIDU_BOT_DOCTOR_CHAT_SENDERID` (`sender_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='百度灵医BOT医生聊天记录';

-- ----------------------------
-- Table structure for m_msg_chat
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_chat`;
CREATE TABLE `m_msg_chat` (
                              `id` bigint NOT NULL COMMENT 'ID',
                              `sender_im_account` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `sender_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `sender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `sender_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `sender_role_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `receiver_im_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `receiver_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `type_` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                              `create_time` timestamp(3) NULL DEFAULT NULL COMMENT '创建时间',
                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                              `update_time` timestamp(3) NULL DEFAULT NULL COMMENT '更新时间',
                              `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `receiver_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `receiver_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `status` tinyint DEFAULT '0' COMMENT '0:未读     1：已读',
                              `receiver_role_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `diagnosis_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `group_id` bigint DEFAULT NULL COMMENT '小组Id',
                              `doctor_id` bigint DEFAULT NULL COMMENT '医生Id',
                              `history_visible` tinyint DEFAULT NULL COMMENT '患者历史是否可见（1 可见， 0 不可见）',
                              `withdraw_chat_status` tinyint DEFAULT '0' COMMENT '撤回状态 1: 为撤回。 null 或 0 为正常显示',
                              `withdraw_chat_user_id` bigint DEFAULT NULL COMMENT '撤回人员的ID， 这条消息的发送人id',
                              `delete_this_message_user_id_json_array_string` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除这条消息的人员的ID的json数组集合',
                              `recommendation_function` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '推荐功能类型',
                              `recommendation_function_params` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '推荐功能的附加参加',
                              `ai_hosted_send` tinyint DEFAULT '0' COMMENT '是否AI自动回复',
                              PRIMARY KEY (`id`) USING BTREE,
                              KEY `I_CHAT_TC_IM_ACCOUNT_ID` (`tenant_code`,`receiver_im_account`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小组';

-- ----------------------------
-- Table structure for m_msg_chat_at_record
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_chat_at_record`;
CREATE TABLE `m_msg_chat_at_record` (
                                        `id` bigint NOT NULL COMMENT 'ID',
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `chat_id` bigint DEFAULT NULL COMMENT '消息ID',
                                        `at_user_id` bigint DEFAULT NULL COMMENT '被at人员的ID',
                                        `chat_user_new_msg_id` bigint DEFAULT NULL COMMENT '所属消息记录列表的ID',
                                        `receiver_im_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '被at人员的角色',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `I_CHAT_AT_RECORD_USERID` (`chat_id`,`at_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for m_msg_chat_automatic_reply
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_chat_automatic_reply`;
CREATE TABLE `m_msg_chat_automatic_reply` (
                                              `id` bigint NOT NULL COMMENT 'ID',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `content` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复的内容',
                                              `status` tinyint(1) DEFAULT NULL COMMENT '1 开启， 0关闭',
                                              `time_out` int DEFAULT NULL COMMENT '消息超出多少时间未回复',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `I_CHAT_AUTOMATIC_REPLY_TENANT_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for m_msg_chat_group_send
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_chat_group_send`;
CREATE TABLE `m_msg_chat_group_send` (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `sender_im_account` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `sender_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `sender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `sender_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `receiver_ids` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `type_` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                         `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `cms_id` bigint DEFAULT NULL COMMENT '文章ID',
                                         `cms_title` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章标题',
                                         `group_hair_mode` int DEFAULT '0' COMMENT '群发模式 定时：1 ；立即：0',
                                         `send_time` datetime DEFAULT NULL COMMENT '发送时间',
                                         `send_status` int DEFAULT NULL COMMENT '发送状态 -1 正在发送， 0 等待发送， 1 发送成功， 2 取消发送',
                                         `patient_number` bigint DEFAULT NULL COMMENT '患者人数',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `I_CHAT_GROUP_SEND_SENDERID` (`sender_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小组';

-- ----------------------------
-- Table structure for m_msg_chat_group_send_object
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_chat_group_send_object`;
CREATE TABLE `m_msg_chat_group_send_object` (
                                                `id` bigint NOT NULL COMMENT 'ID',
                                                `chat_group_send_id` bigint DEFAULT NULL COMMENT '群发消息ID',
                                                `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                                `user_role` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户角色',
                                                `object_association_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '群发对象关联ID',
                                                `object_association_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '群发对象关联类型',
                                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `object_association_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '群发对象关联冗余name',
                                                PRIMARY KEY (`id`) USING BTREE,
                                                KEY `I_CHAT_GROUP_SEND_OBJECT_GROUPID` (`chat_group_send_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群发消息对象';

-- ----------------------------
-- Table structure for m_msg_chat_send_read
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_chat_send_read`;
CREATE TABLE `m_msg_chat_send_read` (
                                        `id` bigint NOT NULL COMMENT 'ID',
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        `chat_id` bigint DEFAULT NULL COMMENT '消息ID',
                                        `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                        `group_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属消息的组ID（saas中是chat对象的 receiverImAccount ）',
                                        `role_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户的角色',
                                        `is_delete` tinyint(1) DEFAULT '0' COMMENT '是否逻辑删除 1是， 0否',
                                        `send_user_role` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送人的角色',
                                        `need_sms_notice` tinyint(1) DEFAULT NULL COMMENT '是否需要短信提醒',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `INDEX_CHAT_SEND_USER_CHAT_ID` (`chat_id`) USING BTREE COMMENT '使用聊天记录ID生产索引',
                                        KEY `INDEX_CHAT_SEND_USER_GROUP_TYPE_IDX` (`user_id`,`role_type`) USING BTREE COMMENT '消息所属用户和消息所在组生成索引',
                                        KEY `I_CHAT_SEND_READ_GROUPID` (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for m_msg_chat_user_new
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_chat_user_new`;
CREATE TABLE `m_msg_chat_user_new` (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `patient_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `request_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `request_role_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `chat_id` bigint DEFAULT NULL COMMENT '最新的消息记录Id',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `patient_id` bigint DEFAULT NULL COMMENT '患者Id',
                                       `patient_avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `receiver_im_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `patient_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '患者备注',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_CHAT_USER_NEW_RECEIVER_IM_ACCOUNT` (`receiver_im_account`) USING BTREE,
                                       KEY `I_CHAT_USER_NEW_TC_REQUEST_ROLE` (`request_id`,`request_role_type`,`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for m_msg_consultation_chat
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_consultation_chat`;
CREATE TABLE `m_msg_consultation_chat` (
                                           `id` bigint NOT NULL,
                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                           `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                           `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `sender_im_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `sender_id` bigint DEFAULT NULL COMMENT '发送者ID',
                                           `sender_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `sender_role_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `sender_avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `group_id` bigint DEFAULT NULL COMMENT '群组ID',
                                           `im_group_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `type_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `sender_role_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           PRIMARY KEY (`id`) USING BTREE,
                                           KEY `consultatioin_chat_groupIdx` (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='环信群组消息';

-- ----------------------------
-- Table structure for m_msg_consultation_message_status
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_consultation_message_status`;
CREATE TABLE `m_msg_consultation_message_status` (
                                                     `id` bigint NOT NULL,
                                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                     `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                     `receiver_role_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                     `receiver_id` bigint DEFAULT NULL COMMENT '接收者ID',
                                                     `consultation_chat_id` bigint DEFAULT NULL COMMENT '群消息ID',
                                                     `group_id` bigint DEFAULT NULL COMMENT '群组ID',
                                                     `status` int DEFAULT NULL COMMENT '读取状态(1 已读， 2 未读)',
                                                     PRIMARY KEY (`id`) USING BTREE,
                                                     KEY `consultation_message_receiveridx_groupidx` (`receiver_id`,`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='环信群组消息读取状态';

-- ----------------------------
-- Table structure for m_msg_gpt_doctor_chat
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_gpt_doctor_chat`;
CREATE TABLE `m_msg_gpt_doctor_chat` (
                                         `id` bigint NOT NULL,
                                         `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会话标识',
                                         `create_time` timestamp(3) NULL DEFAULT NULL COMMENT '创建时间',
                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                         `update_time` timestamp(3) NULL DEFAULT NULL COMMENT '更新时间',
                                         `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `sender_im_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送人im账号',
                                         `sender_id` bigint DEFAULT NULL COMMENT '发送者ID',
                                         `sender_role_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息角色(doctor, ai)',
                                         `im_group_id` bigint DEFAULT NULL COMMENT '群组ID（医生ID）',
                                         `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '消息内容',
                                         `type_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型(文本)',
                                         `send_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送状态(发送成功，发送失败，发送中)',
                                         `send_error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '发送失败时的收集信息',
                                         `reply_msg_id` bigint DEFAULT NULL COMMENT '回复的消息ID',
                                         `reply_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复的消息状态(成功 失败)',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `gpt_doctor_chat_groupIdx` (`im_group_id`) USING BTREE,
                                         KEY `idx_uid` (`session_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='GPT医生聊天记录';

-- ----------------------------
-- Table structure for m_msg_patient_system_message
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_patient_system_message`;
CREATE TABLE `m_msg_patient_system_message` (
                                                `id` bigint NOT NULL COMMENT 'ID',
                                                `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `function_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '功能类型(健康日志，复查提醒，病例讨论，转诊，预约，用药提醒，学习计划，指标监测，自定义随访)',
                                                `jump_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '点击消息跳转的地址',
                                                `jump_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转类型(网页链接)',
                                                `patient_id` bigint NOT NULL COMMENT '接收患者ID',
                                                `read_status` int DEFAULT '1' COMMENT '状态 1 已读， 0 未读',
                                                `push_time` datetime DEFAULT NULL COMMENT '消息推送时间',
                                                `push_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息推送人',
                                                `push_content` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '系统消息推送的主要文本内容',
                                                `create_time` datetime DEFAULT NULL,
                                                `create_user` bigint DEFAULT NULL,
                                                `update_time` datetime DEFAULT NULL,
                                                `update_user` bigint DEFAULT NULL,
                                                `business_id` bigint DEFAULT NULL COMMENT '业务ID(随访计划ID，病例讨论的ID，预约记录id)',
                                                `case_discussion_status` int DEFAULT NULL COMMENT '病例讨论状态(0 开始, 1 进行中, 2结束 )',
                                                `appointment_status` int DEFAULT NULL COMMENT '预约状态',
                                                `appointment_time` datetime DEFAULT NULL COMMENT '预约时间',
                                                `plan_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '计划名称',
                                                `interactive_function_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '互动功能类型(健康日志，疾病信息，检查报告，我的药箱，指标监测，随访表单)',
                                                `patient_can_see` int DEFAULT '1' COMMENT '患者是否可以看到（互动消息） 医生评论或看过后，消息变为1 患者可见，默认患者可见',
                                                `medicine_operation_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '药箱动作 add, updated',
                                                `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
                                                `doctor_read_status` int DEFAULT '-1' COMMENT '医生看过状态 1 已读， 0 未读',
                                                `doctor_comment_status` int DEFAULT '-1' COMMENT '医生评论状态  1 评论， 0 未评论',
                                                `doctor_read_time` datetime DEFAULT NULL COMMENT '医生查看的时间',
                                                `patient_read_doctor_status` int DEFAULT NULL COMMENT '患者查看医生已读状态',
                                                `patient_read_doctor_comment_status` int DEFAULT NULL COMMENT '患者查看医生评论状态',
                                                `doctor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生姓名',
                                                `patient_open_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '患者open_id',
                                                PRIMARY KEY (`id`) USING BTREE,
                                                KEY `MSG_PATIENT_SYSTEM_MESSAGE_PATIENTID` (`patient_id`,`business_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者系统消息';

-- ----------------------------
-- Table structure for m_msg_patient_system_message_remark
-- ----------------------------
DROP TABLE IF EXISTS `m_msg_patient_system_message_remark`;
CREATE TABLE `m_msg_patient_system_message_remark` (
                                                       `id` bigint NOT NULL COMMENT 'ID',
                                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                       `message_id` bigint NOT NULL COMMENT '消息ID',
                                                       `doctor_id` bigint NOT NULL COMMENT '医生ID',
                                                       `doctor_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '医生名称',
                                                       `comment_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生评论的内容',
                                                       `create_time` datetime DEFAULT NULL,
                                                       `create_user` bigint DEFAULT NULL,
                                                       `update_time` datetime DEFAULT NULL,
                                                       `update_user` bigint DEFAULT NULL,
                                                       `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                                       PRIMARY KEY (`id`) USING BTREE,
                                                       KEY `MESSAGE_REMARK_INDEX` (`message_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者系统消息备注表';

-- ----------------------------
-- Table structure for mail_provider
-- ----------------------------
DROP TABLE IF EXISTS `mail_provider`;
CREATE TABLE `mail_provider` (
                                 `id` bigint NOT NULL DEFAULT '0' COMMENT 'ID',
                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `mail_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `host` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `port` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `auth` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `description` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `properties` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件供应商';

-- ----------------------------
-- Table structure for mail_send_status
-- ----------------------------
DROP TABLE IF EXISTS `mail_send_status`;
CREATE TABLE `mail_send_status` (
                                    `id` bigint NOT NULL DEFAULT '0' COMMENT 'ID',
                                    `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `task_id` bigint NOT NULL COMMENT '任务id\n#mail_task',
                                    `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `mail_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                    `update_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件发送状态';

-- ----------------------------
-- Table structure for mail_task
-- ----------------------------
DROP TABLE IF EXISTS `mail_task`;
CREATE TABLE `mail_task` (
                             `id` bigint NOT NULL DEFAULT '0' COMMENT 'ID',
                             `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `provider_id` bigint DEFAULT NULL COMMENT '发件人id\n#mail_provider',
                             `to` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '收件人\n多个,号分割',
                             `cc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '抄送人\n多个,分割',
                             `bcc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '密送人\n多个,分割',
                             `subject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '主题',
                             `body` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '正文',
                             `err_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '发送失败原因',
                             `sender_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '发送商编码',
                             `plan_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '计划发送时间\n(默认当前时间，可定时发送)',
                             `create_user` bigint DEFAULT NULL COMMENT '创建人',
                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                             `update_user` bigint DEFAULT NULL COMMENT '修改人',
                             `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件任务';

-- ----------------------------
-- Table structure for msgs_center_info
-- ----------------------------
DROP TABLE IF EXISTS `msgs_center_info`;
CREATE TABLE `msgs_center_info` (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `biz_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `msgs_center_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '内容',
                                    `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `handler_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `handler_params` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `is_single_handle` bit(1) DEFAULT b'1' COMMENT '是否单人处理',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                    `update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
                                    `update_user` bigint DEFAULT '0' COMMENT '最后修改人',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息中心表';

-- ----------------------------
-- Table structure for msgs_center_info_receive
-- ----------------------------
DROP TABLE IF EXISTS `msgs_center_info_receive`;
CREATE TABLE `msgs_center_info_receive` (
                                            `id` bigint NOT NULL COMMENT 'ID',
                                            `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `msgs_center_id` bigint NOT NULL COMMENT '消息中心ID\n#msgs_center_info',
                                            `user_id` bigint NOT NULL COMMENT '接收人ID\n#c_user',
                                            `is_read` bit(1) DEFAULT b'0' COMMENT '是否已读\n#BooleanStatus',
                                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                            `create_user` bigint DEFAULT '0' COMMENT '创建人',
                                            `update_user` bigint DEFAULT '0' COMMENT '最后修改人',
                                            `update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息中心接收表';

-- ----------------------------
-- Table structure for province
-- ----------------------------
DROP TABLE IF EXISTS `province`;
CREATE TABLE `province` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `province_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                            `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                            `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否删除(0-已删除 1-正常)',
                            `create_user` bigint DEFAULT NULL COMMENT '创建人',
                            `update_user` bigint DEFAULT NULL COMMENT '修改人',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='省份信息表';

-- ----------------------------
-- Table structure for s_statistics_doctor
-- ----------------------------
DROP TABLE IF EXISTS `s_statistics_doctor`;
CREATE TABLE `s_statistics_doctor` (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `patient_num` int DEFAULT NULL COMMENT '全部会员数',
                                       `patient_register_num` int DEFAULT NULL COMMENT '注册会员数',
                                       `patient_no_register_num` int DEFAULT NULL COMMENT '未注册会员数',
                                       `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
                                       `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生姓名',
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `avatar` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生头像',
                                       `nursing_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '护理专员',
                                       `hospital_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属医院',
                                       `organ_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属机构',
                                       `doctor_status` tinyint DEFAULT '2' COMMENT '状态：1已注册（已登录） 2未注册（未登录）',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_STATISTICS_DOCTOR_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生统计';

-- ----------------------------
-- Table structure for s_statistics_nursing_staff
-- ----------------------------
DROP TABLE IF EXISTS `s_statistics_nursing_staff`;
CREATE TABLE `s_statistics_nursing_staff` (
                                              `id` bigint NOT NULL,
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `patient_num` int DEFAULT NULL COMMENT '全部会员数',
                                              `patient_register_num` int DEFAULT NULL COMMENT '注册会员数',
                                              `patient_no_register_num` int DEFAULT NULL COMMENT '未注册会员数',
                                              `nursing_staff_id` bigint DEFAULT NULL COMMENT '医生ID',
                                              `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生姓名',
                                              `avatar` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生头像',
                                              `organ_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属机构',
                                              `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `doctor_num` int DEFAULT NULL COMMENT '全部医生数',
                                              `doctor_register_num` int DEFAULT NULL COMMENT '注册医生（已登录）数',
                                              `doctor_no_register_num` int DEFAULT NULL COMMENT '未注册医生数',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `I_STATISTICS_NURSING_STAFF_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理专员统计';

-- ----------------------------
-- Table structure for s_statistics_tenant
-- ----------------------------
DROP TABLE IF EXISTS `s_statistics_tenant`;
CREATE TABLE `s_statistics_tenant` (
                                       `id` bigint NOT NULL,
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `domain_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '域名',
                                       `patient_num` int DEFAULT NULL COMMENT '全部会员数',
                                       `patient_register_num` int DEFAULT NULL COMMENT '注册会员数',
                                       `patient_no_register_num` int DEFAULT NULL COMMENT '未注册会员数',
                                       `doctor_num` int DEFAULT NULL COMMENT '全部医生数',
                                       `doctor_register_num` int DEFAULT NULL COMMENT '注册医生（已登录）数',
                                       `doctor_no_register_num` int DEFAULT NULL COMMENT '未注册医生数',
                                       `nursing_staff_num` int DEFAULT NULL COMMENT '全部医助数',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_TENANT_STATISTICS_TENANT_CODE` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目统计';

-- ----------------------------
-- Table structure for sms_business_reminder_log
-- ----------------------------
DROP TABLE IF EXISTS `sms_business_reminder_log`;
CREATE TABLE `sms_business_reminder_log` (
                                             `id` bigint NOT NULL,
                                             `patient_id` bigint DEFAULT NULL,
                                             `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                             `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                             `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                             `status_` int DEFAULT NULL COMMENT '推送状态 0 未推送， 1 已推送 2 推送失败',
                                             `type_` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '推送类型',
                                             `group_id` bigint DEFAULT NULL COMMENT '疾病讨论组id',
                                             `query_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                                             `finish_this_check_in` int DEFAULT NULL COMMENT '打开状态',
                                             `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
                                             `nursing_id` bigint DEFAULT NULL COMMENT '专员ID',
                                             `open_this_message` int DEFAULT '0' COMMENT '用户打开了此消息，比如打开表单',
                                             `error_message` text COLLATE utf8mb4_unicode_ci COMMENT '失败原因',
                                             `template_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '短信模版ID',
                                             `mobile` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
                                             `sms_param_id` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短链接要求唯一',
                                             `wechat_redirect_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务地址',
                                             PRIMARY KEY (`id`,`sms_param_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for sms_send_status
-- ----------------------------
DROP TABLE IF EXISTS `sms_send_status`;
CREATE TABLE `sms_send_status` (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `task_id` bigint NOT NULL COMMENT '任务ID\n#sms_task',
                                   `send_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `receiver` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `biz_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `ext` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `fee` int DEFAULT '0' COMMENT '短信计费的条数\n腾讯专用',
                                   `create_month` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `create_week` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `create_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `create_user` bigint DEFAULT '0' COMMENT '创建人',
                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                   `update_user` bigint DEFAULT '0' COMMENT '最后修改人',
                                   `update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `I_SMS_SEND_STATUS_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信发送状态';

-- ----------------------------
-- Table structure for sms_task
-- ----------------------------
DROP TABLE IF EXISTS `sms_task`;
CREATE TABLE `sms_task` (
                            `id` bigint NOT NULL COMMENT '短信记录ID',
                            `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `template_id` bigint NOT NULL COMMENT '模板ID\n#sms_template',
                            `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `source_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `receiver` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '接收者手机号\n群发用英文逗号分割.\n支持2种格式:\n1: 手机号,手机号 \n2: 姓名<手机号>,姓名<手机号>',
                            `topic` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `template_params` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `send_time` datetime DEFAULT NULL COMMENT '发送时间',
                            `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `draft` bit(1) DEFAULT b'0' COMMENT '是否草稿',
                            `create_user` bigint DEFAULT '0' COMMENT '创建人ID',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_user` bigint DEFAULT '0' COMMENT '最后修改人',
                            `update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
                            PRIMARY KEY (`id`) USING BTREE,
                            KEY `I_SMS_TASK_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发送任务\r\n所有的短息发送调用，都视为是一次短信任务，任务表只保存数据和执行状态等信息，\r\n具体的发送状态查看发送状态（#sms_send_status）表';

-- ----------------------------
-- Table structure for sms_template
-- ----------------------------
DROP TABLE IF EXISTS `sms_template`;
CREATE TABLE `sms_template` (
                                `id` bigint NOT NULL COMMENT '模板ID',
                                `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `provider_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `app_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `app_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `custom_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `template_params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `template_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `sign_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `template_describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `create_user` bigint DEFAULT '0' COMMENT '创建人ID',
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `update_user` bigint DEFAULT '0' COMMENT '最后修改人',
                                `update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE KEY `UN_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信模板';

-- ----------------------------
-- Table structure for t_build_site_color_history
-- ----------------------------
DROP TABLE IF EXISTS `t_build_site_color_history`;
CREATE TABLE `t_build_site_color_history` (
                                              `id` bigint NOT NULL,
                                              `tenant_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                              `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                              `create_user` bigint DEFAULT NULL,
                                              `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                              `update_user` bigint DEFAULT NULL,
                                              `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '颜色',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `I_BUILD_SITE_COLOR_HISTORY_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='建站最近使用颜色';

-- ----------------------------
-- Table structure for t_build_site_folder
-- ----------------------------
DROP TABLE IF EXISTS `t_build_site_folder`;
CREATE TABLE `t_build_site_folder` (
                                       `folder_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件夹名称',
                                       `template` int DEFAULT '0' COMMENT '是否为模板(0 不是， 1 是)',
                                       `id` bigint NOT NULL,
                                       `tenant_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                       `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                       `create_user` bigint DEFAULT NULL,
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                       `update_user` bigint DEFAULT NULL,
                                       `copy_number` int DEFAULT NULL COMMENT '复制次数',
                                       `replica_` int DEFAULT '0' COMMENT '复制品（0, 1）',
                                       `delete_status` int DEFAULT '0' COMMENT '删除状态 0 1',
                                       `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
                                       `folder_name_pinyin` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件夹名称拼音',
                                       `name_first_letter` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称首字母',
                                       `share_error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '分享异常的原因',
                                       `can_share` int DEFAULT '0' COMMENT '可以分享0, 1',
                                       `delete_day` int DEFAULT '30' COMMENT '删除剩余时间',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_BUILD_SITE_FOLDER_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='建站文件夹表';

-- ----------------------------
-- Table structure for t_build_site_folder_page
-- ----------------------------
DROP TABLE IF EXISTS `t_build_site_folder_page`;
CREATE TABLE `t_build_site_folder_page` (
                                            `page_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件夹名称',
                                            `template` int DEFAULT '0' COMMENT '是否为模板(0 不是， 1 是)',
                                            `id` bigint NOT NULL,
                                            `tenant_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                            `home_status` int DEFAULT '0' COMMENT '是否首页(0 不是， 1 是)  文件夹中第一个页面是首页',
                                            `template_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板类型(page 单页模板 ， folder 文件夹模板)',
                                            `page_style` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '页面的基础样式',
                                            `folder_id` bigint DEFAULT NULL COMMENT '文件夹的ID',
                                            `create_time` datetime DEFAULT NULL,
                                            `create_user` bigint DEFAULT NULL,
                                            `update_time` datetime DEFAULT NULL,
                                            `update_user` bigint DEFAULT NULL,
                                            `copy_number` int DEFAULT NULL COMMENT '复制次数',
                                            `name_pinyin` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件夹名称拼音',
                                            `name_first_letter` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称首字母',
                                            `cover_image` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面的截图',
                                            PRIMARY KEY (`id`) USING BTREE,
                                            KEY `I_BUILD_SITE_FOLDER_PAGE_TC_FOLDERID` (`tenant_code`,`folder_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='建站文件夹中的页面表';

-- ----------------------------
-- Table structure for t_build_site_folder_page_module
-- ----------------------------
DROP TABLE IF EXISTS `t_build_site_folder_page_module`;
CREATE TABLE `t_build_site_folder_page_module` (
                                                   `module_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件类型(文本，文章，视频，图片，轮播图。。。)',
                                                   `template` int DEFAULT '0' COMMENT '是否为模板(0 不是， 1 是)',
                                                   `id` bigint NOT NULL,
                                                   `tenant_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                                   `module_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '组件类型的汉语名称',
                                                   `folder_id` bigint DEFAULT NULL COMMENT '文件夹的ID',
                                                   `folder_page_id` bigint DEFAULT NULL COMMENT '组件所在页面ID',
                                                   `class_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件拥有的css类名数组对象',
                                                   `module_style` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '组件样式对象',
                                                   `module_html_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '组件的h5内容(文本组件内容，文章主标题内容)',
                                                   `module_image_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单图片组件的url',
                                                   `carousel_way` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '轮播方式 （默认，自动，手动）',
                                                   `carousel_show_time` float(6,2) DEFAULT '4.00' COMMENT '展示时间',
  `carousel_switch_time` float(6,2) DEFAULT '0.80' COMMENT '轮播的切换时间',
  `module_title_show_status` int DEFAULT '1' COMMENT '组件主标题显示状态（0不显示， 1显示）',
  `module_title_show_position` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件主标题显示位置(left, center)',
  `module_show_content` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '控制显示（摘要，时间，收藏，浏览量，留言）存储json对象',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_user` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_user` bigint DEFAULT NULL,
  `carousel_image_spacing` int DEFAULT NULL COMMENT '轮播图片间距',
  `carousel_image_fill_style` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '轮播图片缩放方式',
  `module_subtitle_show_status` int DEFAULT NULL COMMENT '组件副标题显示状态（0不显示， 1显示）',
  `module_subtitle_content` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件副标题内容',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频组件使用视频的url',
  `video_cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频组件使用视频的封面',
  `ware_house_video_id` bigint DEFAULT NULL COMMENT '视频库内视频的ID (视频组件用)',
  `screen_show_number` int DEFAULT NULL COMMENT '一屏显示数量',
  `page_id_json_array` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '搜索组件关联的页面ID',
  `hide_image_border` int DEFAULT '0' COMMENT '隐藏图片的边框',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `folder_page_module_index_page_id` (`folder_page_id`) USING BTREE COMMENT '所属页面索引',
  KEY `I_BUILD_SITE_PAGE_MODULE_FOLDERID` (`folder_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='建站文件夹中的页面表';

-- ----------------------------
-- Table structure for t_build_site_jump_information
-- ----------------------------
DROP TABLE IF EXISTS `t_build_site_jump_information`;
CREATE TABLE `t_build_site_jump_information` (
                                                 `id` bigint NOT NULL,
                                                 `tenant_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                                 `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                                 `create_user` bigint DEFAULT NULL,
                                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                                 `update_user` bigint DEFAULT NULL,
                                                 `folder_id` bigint DEFAULT NULL COMMENT '文件夹ID',
                                                 `page_id` bigint DEFAULT NULL COMMENT '页面ID',
                                                 `module_id` bigint DEFAULT NULL COMMENT '组件id',
                                                 `module_plate_id` bigint DEFAULT NULL COMMENT '板块ID',
                                                 `jump_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转方式',
                                                 `jump_page_id` bigint DEFAULT NULL COMMENT '跳转文件夹内页面ID',
                                                 `jump_app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转小程序ID',
                                                 `jump_app_page_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转小程序页面的路径',
                                                 `jump_website` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '跳转外链网址',
                                                 `jump_cms_id` bigint DEFAULT NULL COMMENT '跳转文章的ID',
                                                 `jump_video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转视频url',
                                                 `jump_video_title` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转视频名称',
                                                 `jump_video_cover` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转视频封面',
                                                 `jump_video_source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '本地视频，视频地址',
                                                 `jump_video_id` bigint DEFAULT NULL COMMENT '跳转视频的ID',
                                                 PRIMARY KEY (`id`) USING BTREE,
                                                 KEY `jump_index_page_id` (`page_id`) USING BTREE,
                                                 KEY `I_BUILD_SITE_JUMP_INFOR_FOLDERID` (`folder_id`) USING BTREE,
                                                 KEY `I_BUILD_SITE_JUMP_INFOR_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='建站组件元素跳转信息';

-- ----------------------------
-- Table structure for t_build_site_module_label
-- ----------------------------
DROP TABLE IF EXISTS `t_build_site_module_label`;
CREATE TABLE `t_build_site_module_label` (
                                             `id` bigint NOT NULL,
                                             `tenant_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                             `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                             `create_user` bigint DEFAULT NULL,
                                             `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                             `update_user` bigint DEFAULT NULL,
                                             `folder_id` bigint DEFAULT NULL COMMENT '文件夹ID',
                                             `page_id` bigint DEFAULT NULL COMMENT '页面ID',
                                             `module_id` bigint DEFAULT NULL COMMENT '组件ID',
                                             `label_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签的名称',
                                             `label_sort_index` int DEFAULT NULL COMMENT '标签排序下标(从小到大)',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             KEY `module_label_index_page_id` (`page_id`) USING BTREE,
                                             KEY `I_BUILD_SITE_MODULE_LABEL_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='建站多功能导航标签';

-- ----------------------------
-- Table structure for t_build_site_module_plate
-- ----------------------------
DROP TABLE IF EXISTS `t_build_site_module_plate`;
CREATE TABLE `t_build_site_module_plate` (
                                             `id` bigint NOT NULL,
                                             `tenant_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                             `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                             `create_user` bigint DEFAULT NULL,
                                             `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                             `update_user` bigint DEFAULT NULL,
                                             `folder_id` bigint DEFAULT NULL,
                                             `page_id` bigint DEFAULT NULL,
                                             `module_id` bigint DEFAULT NULL,
                                             `module_label_id` bigint DEFAULT NULL COMMENT '组件标签ID',
                                             `plate_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '板块类型(图片，文章，视频)',
                                             `plate_sort_index` int DEFAULT NULL COMMENT '板块顺序',
                                             `image_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片，文章视频封面地址',
                                             `plate_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章或视频的标题',
                                             `plate_content_id` bigint DEFAULT NULL COMMENT '文章ID，视频ID，图片库图片ID',
                                             `cms_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '文章的摘要',
                                             `video_duration` float(20,2) DEFAULT NULL COMMENT '视频时长',
  `video_file_size` float(20,2) DEFAULT NULL COMMENT '视频大小(kb)',
  `video_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `module_plate_index_page_id` (`page_id`) USING BTREE,
  KEY `I_BUILD_SITE_MODULE_PLATE_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='建站组件的板块表';

-- ----------------------------
-- Table structure for t_build_site_module_title_style
-- ----------------------------
DROP TABLE IF EXISTS `t_build_site_module_title_style`;
CREATE TABLE `t_build_site_module_title_style` (
                                                   `id` bigint NOT NULL,
                                                   `tenant_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                                   `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                                   `create_user` bigint DEFAULT NULL,
                                                   `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                                   `update_user` bigint DEFAULT NULL,
                                                   `folder_id` bigint DEFAULT NULL,
                                                   `page_id` bigint DEFAULT NULL,
                                                   `module_id` bigint DEFAULT NULL,
                                                   `title_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题类型(主标题，文章标题，文章时间，文章摘要，视频标题，视频描述)',
                                                   `title_style` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'json格式的样式Style对象',
                                                   PRIMARY KEY (`id`) USING BTREE,
                                                   KEY `module_title_style_index_page_id` (`page_id`) USING BTREE,
                                                   KEY `I_BUILD_SITE_MODULE_TITLE_STYLE_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='建站组件主题样式表';

-- ----------------------------
-- Table structure for t_build_site_video_click_remark
-- ----------------------------
DROP TABLE IF EXISTS `t_build_site_video_click_remark`;
CREATE TABLE `t_build_site_video_click_remark` (
                                                   `id` bigint NOT NULL,
                                                   `open_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信openId',
                                                   `video_id` bigint NOT NULL COMMENT '视频id',
                                                   `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                   PRIMARY KEY (`id`) USING BTREE,
                                                   KEY `I_VIDEO_CLICK_REMARK` (`open_id`) USING BTREE,
                                                   KEY `I_BUILD_SITE_VIDEO_CLICK_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频点击播放';

-- ----------------------------
-- Table structure for t_build_site_video_warehouse
-- ----------------------------
DROP TABLE IF EXISTS `t_build_site_video_warehouse`;
CREATE TABLE `t_build_site_video_warehouse` (
                                                `id` bigint NOT NULL,
                                                `tenant_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                                `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                                `create_user` bigint DEFAULT NULL,
                                                `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                                `update_user` bigint DEFAULT NULL,
                                                `video_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `video_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `video_duration` float(20,2) DEFAULT NULL,
  `video_file_size` float(20,2) DEFAULT NULL,
  `video_cover` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频封面',
  `obs_file_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'obs对象名称',
  `title_pinyin` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '视频标题的拼音',
  `title_first_letter` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频名首字母',
  `number_views` int DEFAULT '0',
  `delete_mark` int DEFAULT '0' COMMENT '删除标记 (0 , 1)',
  `video_desc` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频描述',
  `message_num` int DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `I_BUILD_SITE_VIDEO_WAREHOUSE_TC` (`tenant_code`) USING BTREE,
  KEY `I_BUILD_SITE_VIDEO_WAREHOUSE_TC_TITLE` (`video_title`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='建站视频库';

-- ----------------------------
-- Table structure for t_bulk_notification
-- ----------------------------
DROP TABLE IF EXISTS `t_bulk_notification`;
CREATE TABLE `t_bulk_notification` (
                                       `id` bigint NOT NULL,
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `notification_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '群发名称',
                                       `code` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息的code随机生成',
                                       `sass_template_msg_id` bigint DEFAULT NULL COMMENT 'sass模板的Id',
                                       `message_id` bigint DEFAULT NULL COMMENT '消息ID从1000自增',
                                       `send_time` datetime DEFAULT NULL COMMENT '发送时间',
                                       `notification_target` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知的目标',
                                       `template_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板名称',
                                       `notification_send_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送状态',
                                       `notification_jump_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转类型',
                                       `jump_business_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '链接/内容ID/等',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_BUIL_NOTIFICATION_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群发通知';

-- ----------------------------
-- Table structure for t_bulk_notification_scan_user
-- ----------------------------
DROP TABLE IF EXISTS `t_bulk_notification_scan_user`;
CREATE TABLE `t_bulk_notification_scan_user` (
                                                 `id` bigint NOT NULL,
                                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                 `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                                 `nick_name` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信昵称',
                                                 `wx_app_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'appId',
                                                 PRIMARY KEY (`id`) USING BTREE,
                                                 KEY `I_BULK_NOTIFICATION_SCAN_USER_OPENID` (`open_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知的扫码用户';

-- ----------------------------
-- Table structure for t_bulk_notification_send_record
-- ----------------------------
DROP TABLE IF EXISTS `t_bulk_notification_send_record`;
CREATE TABLE `t_bulk_notification_send_record` (
                                                   `id` bigint NOT NULL,
                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                   `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                   `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知的目标openId',
                                                   `notification_target` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知的目标',
                                                   `notification_id` bigint DEFAULT NULL COMMENT '通知ID',
                                                   `target_id` bigint DEFAULT NULL COMMENT '通知的目标',
                                                   `target_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知的目标名称',
                                                   `notification_send_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送状态',
                                                   `wx_error` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '给程序员看的异常',
                                                   `error_msg` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户自己看的异常',
                                                   PRIMARY KEY (`id`) USING BTREE,
                                                   KEY `I_BULK_NOTIFICATION_SEND_RECORD_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群发通知记录';

-- ----------------------------
-- Table structure for t_cms_article_news
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_article_news`;
CREATE TABLE `t_cms_article_news` (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
                                      `thumb_media_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图片的url',
                                      `thumb_media_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图片素材Id',
                                      `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者',
                                      `digest` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图文消息的摘要',
                                      `show_cover_pic` int DEFAULT NULL COMMENT '是否显示封面',
                                      `need_open_comment` int DEFAULT NULL COMMENT '是否打开评论',
                                      `only_fans_can_comment` int DEFAULT NULL COMMENT '是否粉丝才可评论',
                                      `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '文章',
                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                      `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `thumb_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信素材的url',
                                      `file_url_map` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '文章内容中替换的链接',
                                      `content_source_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原文链接',
                                      `user_declare_state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户声明文章的状态',
                                      `original_article_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '相似原创文的url',
                                      `need_replace_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否需要替换成原创文内容',
                                      `need_show_reprint_source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否需要注明转载来源',
                                      `original_article_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '相似原创文的类型',
                                      `can_reprint` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否能转载',
                                      `audit_state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '系统校验的状态',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      KEY `I_CMS_ARTICLE_NEWS_TC_TITLE` (`tenant_code`,`title`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图文素材';

-- ----------------------------
-- Table structure for t_cms_article_other
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_article_other`;
CREATE TABLE `t_cms_article_other` (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '素材标题',
                                       `media_saas_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '素材saas的url',
                                       `file_size` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件大小',
                                       `material_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '素材类型(image,voice,video,thumb)',
                                       `introduction` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频描述',
                                       `media_id` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '素材微信的id',
                                       `media_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '素材微信的url',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
                                       `video_upload_wx` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频上传状态',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_CMS_ARTICLE_OTHER_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='其他素材';

-- ----------------------------
-- Table structure for t_cms_banner
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_banner`;
CREATE TABLE `t_cms_banner` (
                                `id` bigint NOT NULL,
                                `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `order_` int DEFAULT NULL COMMENT '优先级',
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                `jump_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转类型',
                                `jump_website` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '网址',
                                `system_function` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '系统功能',
                                `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                `user_role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色',
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `I_CMS_BANNER_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='banner';

-- ----------------------------
-- Table structure for t_cms_banner_switch
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_banner_switch`;
CREATE TABLE `t_cms_banner_switch` (
                                       `id` bigint NOT NULL,
                                       `user_role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户角色',
                                       `switch_status` int DEFAULT NULL COMMENT '开关状态',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_CMS_BANNER_SWITCH_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='banner';

-- ----------------------------
-- Table structure for t_cms_channel
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_channel`;
CREATE TABLE `t_cms_channel` (
                                 `id` bigint NOT NULL DEFAULT '0',
                                 `channel_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `sort` int DEFAULT NULL COMMENT '排序',
                                 `channel_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `owner_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `doctor_owner` tinyint unsigned DEFAULT '0' COMMENT '是否是医生学院，true是，false否',
                                 `channel_group_id` bigint DEFAULT NULL COMMENT '栏目组的ID',
                                 `parent_id` bigint DEFAULT NULL COMMENT '父级ID',
                                 `cms_role_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色分类',
                                 `library_id` bigint DEFAULT NULL COMMENT '内容库ID',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `I_CMS_CHANNEL_GROUPID` (`channel_group_id`) USING BTREE,
                                 KEY `I_CMS_CHANNEL_TC_DOCTOROWNER` (`tenant_code`,`doctor_owner`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='栏目表';

-- ----------------------------
-- Table structure for t_cms_channel_content
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_channel_content`;
CREATE TABLE `t_cms_channel_content` (
                                         `id` bigint NOT NULL,
                                         `c_channel_id` bigint DEFAULT NULL COMMENT '栏目id',
                                         `c_title` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                         `c_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                         `c_source_id` bigint DEFAULT NULL COMMENT '来源表主键ID',
                                         `c_source_entity_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                         `c_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
                                         `author` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                         `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                         `c_link` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                         `n_sort` int DEFAULT NULL COMMENT '排序',
                                         `n_is_top` int DEFAULT '0' COMMENT '置顶（0, 1）',
                                         `n_hit_count` bigint unsigned DEFAULT '0' COMMENT '点击量',
                                         `c_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
                                         `n_can_comment` int NOT NULL DEFAULT '1' COMMENT '是否允许评论，1允许，0不允许。',
                                         `n_message_num` int NOT NULL DEFAULT '0' COMMENT '留言数量',
                                         `owner_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                         `c_channel_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                         `doctor_owner` tinyint unsigned DEFAULT '0' COMMENT '是否是医生学院，true是，false否',
                                         `channel_group_id` bigint DEFAULT NULL COMMENT '栏目组的ID',
                                         `library_id` bigint DEFAULT NULL COMMENT '内容库ID',
                                         `patient_home_show` int DEFAULT NULL COMMENT '是否患者首页展示',
                                         `patient_home_region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '患者首页展示区域',
                                         `english_title` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文章的英文标题',
                                         `c_keyword` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关键词',
                                         `c_cms_period` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文献的期次',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `I_CMSCONTENT_CHANNELID` (`c_channel_id`) USING BTREE,
                                         KEY `I_CMS_CHANNEL_CONTENT_TC` (`tenant_code`) USING BTREE,
                                         KEY `I_CMS_CHANNEL_CONTENT_CHANNEL_GROUP_ID` (`channel_group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='栏目内容表';

-- ----------------------------
-- Table structure for t_cms_channel_content_guomin_back
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_channel_content_guomin_back`;
CREATE TABLE `t_cms_channel_content_guomin_back` (
                                                     `id` bigint NOT NULL,
                                                     `c_channel_id` bigint DEFAULT NULL COMMENT '栏目id',
                                                     `c_title` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                                     `c_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                                     `c_source_id` bigint DEFAULT NULL COMMENT '来源表主键ID',
                                                     `c_source_entity_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                                     `c_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
                                                     `author` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                                     `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                                     `c_link` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                                     `n_sort` int DEFAULT NULL COMMENT '排序',
                                                     `n_is_top` int DEFAULT '0' COMMENT '置顶（0, 1）',
                                                     `n_hit_count` bigint unsigned DEFAULT '0' COMMENT '点击量',
                                                     `c_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
                                                     `n_can_comment` int NOT NULL DEFAULT '1' COMMENT '是否允许评论，1允许，0不允许。',
                                                     `n_message_num` int NOT NULL DEFAULT '0' COMMENT '留言数量',
                                                     `owner_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                     `c_channel_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                                     `doctor_owner` tinyint unsigned DEFAULT '0' COMMENT '是否是医生学院，true是，false否',
                                                     `channel_group_id` bigint DEFAULT NULL COMMENT '栏目组的ID',
                                                     `library_id` bigint DEFAULT NULL COMMENT '内容库ID',
                                                     `patient_home_show` int DEFAULT NULL COMMENT '是否患者首页展示',
                                                     `patient_home_region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '患者首页展示区域',
                                                     `english_title` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文章的英文标题',
                                                     `c_keyword` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关键词',
                                                     `c_cms_period` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文献的期次',
                                                     PRIMARY KEY (`id`) USING BTREE,
                                                     KEY `IDX_TOP` (`n_is_top`) USING BTREE,
                                                     KEY `IDX_SOURCE` (`c_source_id`) USING BTREE,
                                                     KEY `IDX_ENTITY_CLASS` (`c_source_entity_class`) USING BTREE,
                                                     KEY `I_CMSCONTENT_CHANNELID` (`c_channel_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='栏目内容表';

-- ----------------------------
-- Table structure for t_cms_channel_group
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_channel_group`;
CREATE TABLE `t_cms_channel_group` (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分组名称',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `cms_role_remark` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色分类',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_CMS_CHANNEL_GROUP_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='栏目组';

-- ----------------------------
-- Table structure for t_cms_content_collect
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_content_collect`;
CREATE TABLE `t_cms_content_collect` (
                                         `id` bigint NOT NULL,
                                         `user_id` bigint NOT NULL COMMENT '用户id',
                                         `content_id` bigint NOT NULL COMMENT '内容id',
                                         `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                         `collect_status` int DEFAULT NULL COMMENT '收藏状态1收藏0取消',
                                         `role_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `I_ContentCollect` (`user_id`) USING BTREE,
                                         KEY `I_CMS_CONTENT_COLLECT_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏记录';

-- ----------------------------
-- Table structure for t_cms_content_library
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_content_library`;
CREATE TABLE `t_cms_content_library` (
                                         `id` bigint NOT NULL,
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                         `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                         `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                         `library_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '内容库名称',
                                         `library_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '内容库描述',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容库';

-- ----------------------------
-- Table structure for t_cms_content_reply
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_content_reply`;
CREATE TABLE `t_cms_content_reply` (
                                       `id` bigint NOT NULL,
                                       `c_parent_comment_id` bigint DEFAULT NULL COMMENT '父ID',
                                       `c_replier_id` bigint NOT NULL COMMENT '留言人',
                                       `c_replier_wx_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `c_replier_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `c_content_id` bigint NOT NULL COMMENT '文章Id',
                                       `c_content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `n_audit_status` int NOT NULL DEFAULT '1' COMMENT '审核状态：1通过，2不通过',
                                       `like_count` bigint unsigned DEFAULT '0' COMMENT '点赞量',
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `role_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_CONTENTREPLY_REPID` (`c_replier_id`) USING BTREE,
                                       KEY `I_CONTENTREPLY_CONTENTID` (`c_content_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容留言表';

-- ----------------------------
-- Table structure for t_cms_doctor_last_pull
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_doctor_last_pull`;
CREATE TABLE `t_cms_doctor_last_pull` (
                                          `id` bigint NOT NULL,
                                          `doctor_id` bigint NOT NULL COMMENT '医生ID',
                                          `cms_content_id` bigint NOT NULL COMMENT '文章ID',
                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `I_CMS_DOCTOR_LAST_PULL_DOCTORID` (`doctor_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生订阅最新文章推送表';

-- ----------------------------
-- Table structure for t_cms_doctor_subscribe_key_word
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_doctor_subscribe_key_word`;
CREATE TABLE `t_cms_doctor_subscribe_key_word` (
                                                   `id` bigint NOT NULL,
                                                   `doctor_id` bigint NOT NULL COMMENT '医生ID',
                                                   `key_word` bigint DEFAULT NULL,
                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                   PRIMARY KEY (`id`) USING BTREE,
                                                   KEY `I_CMS_DOCTOR_SUBSCRIBE_KEYWORD_DOCTORID` (`doctor_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生订阅关键词表';

-- ----------------------------
-- Table structure for t_cms_key_word
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_key_word`;
CREATE TABLE `t_cms_key_word` (
                                  `id` bigint NOT NULL,
                                  `key_word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `order_` int DEFAULT NULL COMMENT '优先级',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                  `cms_id` bigint DEFAULT NULL COMMENT '文章的ID',
                                  `show_status` int DEFAULT '0' COMMENT '显示状态',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  KEY `I_CMS_KEY_WORD_IDX` (`key_word`) USING BTREE,
                                  KEY `I_CMS_KEY_WORD_CMSID` (`cms_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关键词表';

-- ----------------------------
-- Table structure for t_cms_mass_mailing
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_mass_mailing`;
CREATE TABLE `t_cms_mass_mailing` (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `send_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送类型 定时发送： timing、手动发送： manual',
                                      `timing_send_time` datetime DEFAULT NULL COMMENT '定时发送的时间',
                                      `send_time` datetime DEFAULT NULL COMMENT '实际发送的时间',
                                      `media_type_enum` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息类型',
                                      `send_target` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送的目标',
                                      `article_news_list` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图文素材id的集合',
                                      `article_image_list` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片素材的 id 集合 toJOSNString',
                                      `text_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文字内容(消息类型为文本时)',
                                      `tag_ids` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '选择的标签',
                                      `openids` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '发送的目标的openIds',
                                      `article_other_id` bigint DEFAULT NULL COMMENT '素材id（视频、音频素材的id）',
                                      `send_ignore_reprint` tinyint DEFAULT NULL COMMENT '转载时是否继续群发',
                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                      `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `message_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '群发消息状态(等待发送, 提交中, 已提交, 发送成功, 发送失败)',
                                      `msg_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '群信发送的ID',
                                      `sent_count` int DEFAULT NULL COMMENT '发送成功的人数',
                                      `error_count` int DEFAULT NULL COMMENT '发送失败的人数',
                                      `sent_result` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '微信推送的结果',
                                      `recommend` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '推荐语',
                                      `error_message` varchar(1500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `mass_sort` int DEFAULT NULL COMMENT '群发排序',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      KEY `I_CMS_MASS_MAILING_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群发管理';

-- ----------------------------
-- Table structure for t_cms_reply_like_log
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_reply_like_log`;
CREATE TABLE `t_cms_reply_like_log` (
                                        `id` bigint NOT NULL,
                                        `user_id` bigint NOT NULL COMMENT '点赞人id',
                                        `reply_id` bigint NOT NULL COMMENT '评论id',
                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `create_time` datetime NOT NULL COMMENT '创建时间',
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `has_cancel` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '是否取消点赞：1未取消，2已取消',
                                        `role_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `I_CONTENTREPLY_REPID` (`user_id`,`reply_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录';

-- ----------------------------
-- Table structure for t_cms_studio_cms
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_studio_cms`;
CREATE TABLE `t_cms_studio_cms` (
                                    `id` bigint NOT NULL COMMENT '主键',
                                    `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                    `cms_title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标题',
                                    `cms_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'cms类型（文章，声音，视频）',
                                    `cms_file_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'cms声音视频url',
                                    `cms_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'cms文章内容',
                                    `cms_file_title` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'cms文件标题',
                                    `cms_file_size` bigint DEFAULT NULL COMMENT '文件大小',
                                    `cms_file_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件类型(mp4,avi,mp3...)',
                                    `cms_file_remark` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'cms文件备注(音频描述)',
                                    `release_status` int DEFAULT '0' COMMENT '发布状态 0: 草稿， 1: 已发布',
                                    `pin_to_top_sort` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '0' COMMENT '置顶排序',
                                    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
                                    `doctor_id` bigint DEFAULT NULL COMMENT '数据所属医生',
                                    `pin_to_top` int DEFAULT '0' COMMENT '置顶状态0 不置顶， 1 置顶',
                                    `cms_video_cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '视频封面',
                                    `article_data_id` bigint DEFAULT NULL COMMENT '科普创作数据id',
                                    `article_data_update_status` int DEFAULT '0' COMMENT '科普创作数据修改状态',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='医生cms详情表';

-- ----------------------------
-- Table structure for t_cms_studio_collect
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_studio_collect`;
CREATE TABLE `t_cms_studio_collect` (
                                        `id` bigint NOT NULL,
                                        `user_id` bigint NOT NULL COMMENT '用户id',
                                        `cms_id` bigint NOT NULL COMMENT '内容id',
                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `collect_status` int DEFAULT NULL COMMENT '收藏状态1收藏0取消',
                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生CMS收藏记录';

-- ----------------------------
-- Table structure for t_cms_studio_content_reply
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_studio_content_reply`;
CREATE TABLE `t_cms_studio_content_reply` (
                                              `id` bigint NOT NULL,
                                              `c_parent_comment_id` bigint DEFAULT NULL COMMENT '父ID',
                                              `c_replier_id` bigint NOT NULL COMMENT '留言人',
                                              `c_cms_id` bigint NOT NULL COMMENT '文章Id',
                                              `c_content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `n_audit_status` int NOT NULL DEFAULT '1' COMMENT '审核状态：1通过，2不通过',
                                              `like_count` bigint unsigned DEFAULT '0' COMMENT '点赞量',
                                              `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `replier_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
                                              `replier_avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生cms留言表';

-- ----------------------------
-- Table structure for t_common_msg
-- ----------------------------
DROP TABLE IF EXISTS `t_common_msg`;
CREATE TABLE `t_common_msg` (
                                `id` bigint NOT NULL,
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `content` varchar(800) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `account_id` bigint DEFAULT NULL COMMENT '护理专员Id',
                                `user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户类型 NursingStaff, doctor',
                                `source_template_id` bigint DEFAULT NULL COMMENT '来自模版的ID',
                                `type_id` bigint DEFAULT NULL COMMENT '分类ID',
                                `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '常用语标题',
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `I_COMMON_MSG_TC_ACCOUNTID` (`tenant_code`,`account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='常用语';

-- ----------------------------
-- Table structure for t_common_msg_template_content
-- ----------------------------
DROP TABLE IF EXISTS `t_common_msg_template_content`;
CREATE TABLE `t_common_msg_template_content` (
                                                 `id` bigint NOT NULL,
                                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                 `template_type_id` bigint DEFAULT NULL,
                                                 `template_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '模板内容',
                                                 `template_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板标题',
                                                 `user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户类型 NursingStaff, doctor',
                                                 `template_release` int DEFAULT NULL COMMENT '模板发布（0未发布， 1发布）',
                                                 PRIMARY KEY (`id`) USING BTREE,
                                                 KEY `I_COMMON_MSG_TEMPLATE_CONTENT_TYPEID` (`template_type_id`) USING BTREE,
                                                 KEY `I_COMMON_MSG_TEMPLATE_CONTENT_TENANT_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='常用语模板分类';

-- ----------------------------
-- Table structure for t_common_msg_template_type
-- ----------------------------
DROP TABLE IF EXISTS `t_common_msg_template_type`;
CREATE TABLE `t_common_msg_template_type` (
                                              `id` bigint NOT NULL,
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `type_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `type_sort` int DEFAULT NULL COMMENT '序号',
                                              `user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户类型 NursingStaff, doctor',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `I_COMMON_MSG_TEMPLATE_TYPE_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='常用语模板分类';

-- ----------------------------
-- Table structure for t_common_msg_type
-- ----------------------------
DROP TABLE IF EXISTS `t_common_msg_type`;
CREATE TABLE `t_common_msg_type` (
                                     `id` bigint NOT NULL,
                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                     `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                     `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户类型 NursingStaff, doctor',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     KEY `I_COMMON_MSG_TYPE_TC_USERID` (`tenant_code`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='常用语分类';

-- ----------------------------
-- Table structure for t_custom_commend_drugs
-- ----------------------------
DROP TABLE IF EXISTS `t_custom_commend_drugs`;
CREATE TABLE `t_custom_commend_drugs` (
                                          `id` bigint NOT NULL,
                                          `drugs_id` bigint DEFAULT NULL,
                                          `number` int DEFAULT NULL,
                                          `dosage` float(20,0) DEFAULT NULL,
  `time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cycle` int DEFAULT NULL,
  `order_` int DEFAULT NULL,
  `drugs_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `I_DRUGSCOMMEND_DRUGSID` (`drugs_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='自定义推荐药品';

-- ----------------------------
-- Table structure for t_custom_drugs_category
-- ----------------------------
DROP TABLE IF EXISTS `t_custom_drugs_category`;
CREATE TABLE `t_custom_drugs_category` (
                                           `id` bigint NOT NULL,
                                           `category_id` bigint NOT NULL COMMENT '药品类别Id',
                                           `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           KEY `I_PMC_CATEGORYID` (`category_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目药品类别';

-- ----------------------------
-- Table structure for t_custom_form
-- ----------------------------
DROP TABLE IF EXISTS `t_custom_form`;
CREATE TABLE `t_custom_form` (
                                 `id` bigint unsigned NOT NULL DEFAULT '2',
                                 `business_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `fields_json` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '表单的数据字典描述',
                                 `source_business_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 `one_question_page` int unsigned DEFAULT '2' COMMENT '一题一页，1是，2否',
                                 `show_trend` int DEFAULT '0' COMMENT '趋势图，1 是， null 0 否',
                                 `source_form_id` bigint DEFAULT NULL COMMENT '原先的表单的ID',
                                 `one_day_curve` int DEFAULT NULL COMMENT '单日曲线， 1是， null 0 否',
                                 `score_questionnaire` int DEFAULT NULL COMMENT '评分问卷 1是  0否',
                                 `daily_submission_limit` int DEFAULT '0' COMMENT '每日最大提交次数',
                                 `score_questionnaire_analysis` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '评分问卷结果解析',
                                 `show_score_questionnaire_analysis` tinyint DEFAULT NULL COMMENT '是否展示解析',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `I_CSFORM_BUSINESSID` (`business_id`) USING BTREE,
                                 KEY `I_FORM_TC_CATEGORY` (`tenant_code`,`category`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='自定义表单表';

-- ----------------------------
-- Table structure for t_custom_form_fields_group
-- ----------------------------
DROP TABLE IF EXISTS `t_custom_form_fields_group`;
CREATE TABLE `t_custom_form_fields_group` (
                                              `id` bigint NOT NULL,
                                              `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分组名称',
                                              `group_sort` int DEFAULT NULL COMMENT '分组的排序',
                                              `field_group_uuid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分组的UUID。给题目关联使用, 创建后不会改变',
                                              `form_id` bigint DEFAULT NULL COMMENT '表单ID',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `I_FORM_FIELD_GROUP_FORMID` (`form_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单题目的分组规则';

-- ----------------------------
-- Table structure for t_custom_form_patient_field_reference
-- ----------------------------
DROP TABLE IF EXISTS `t_custom_form_patient_field_reference`;
CREATE TABLE `t_custom_form_patient_field_reference` (
                                                         `id` bigint unsigned NOT NULL DEFAULT '2',
                                                         `field_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '字段ID',
                                                         `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                                         `business_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '监测计划ID',
                                                         `reference_value` double(9,2) DEFAULT NULL COMMENT '基准值',
                                                         `target_value` double(9,2) DEFAULT NULL COMMENT '目标值',
                                                         `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                         `org_id` bigint DEFAULT NULL COMMENT '机构ID',
                                                         `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
                                                         `nursing_id` bigint DEFAULT NULL COMMENT '医助ID',
                                                         PRIMARY KEY (`id`) USING BTREE,
                                                         KEY `I_CSFORM_FIELD_PATIENT_ID` (`patient_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者的监测字段被修改的基准值目标值';

-- ----------------------------
-- Table structure for t_custom_form_result
-- ----------------------------
DROP TABLE IF EXISTS `t_custom_form_result`;
CREATE TABLE `t_custom_form_result` (
                                        `id` bigint NOT NULL,
                                        `message_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属推送消息id',
                                        `business_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属随访ID',
                                        `form_id` bigint DEFAULT NULL COMMENT '表单Id',
                                        `user_id` bigint DEFAULT NULL COMMENT '填写人Id',
                                        `json_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '表单填写结果数据',
                                        `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单名称',
                                        `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单类型',
                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                        `one_question_page` int unsigned DEFAULT '2' COMMENT '一题一页，1是，2否',
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        `show_trend` int DEFAULT '0' COMMENT '趋势图，1 是， null 0 否',
                                        `fill_in_index` int DEFAULT NULL COMMENT '本信息疾病信息一题一页模式已经填写的进度 完成时传 -1',
                                        `delete_mark` int DEFAULT '0' COMMENT '0未删除，1已删除',
                                        `form_history` int DEFAULT '0' COMMENT '是否来自历史修改记录',
                                        `score_questionnaire` int DEFAULT NULL COMMENT '评分问卷1是   0否',
                                        `form_error_result` int DEFAULT NULL COMMENT '校验数据异常状态0 表示无状态。 1表示正常， 2表示异常',
                                        `data_feed_back` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据反馈的结果JSON',
                                        `first_submit_time` datetime DEFAULT NULL COMMENT '最初提交时间(不受校验日期组件控制)',
                                        `encrypted` int DEFAULT '1' COMMENT '数据是否加密',
                                        `score_questionnaire_analysis` longtext COLLATE utf8mb4_unicode_ci COMMENT '评分问卷结果解析',
                                        `show_score_questionnaire_analysis` tinyint DEFAULT NULL COMMENT '是否展示解析',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `I_FORMRESULT_MESSAGEID` (`message_id`) USING BTREE,
                                        KEY `I_FORMRESULT_BUSINESSID` (`business_id`) USING BTREE,
                                        KEY `I_FORMRESULT_FORMID` (`form_id`) USING BTREE,
                                        KEY `I_FORMRESULT_USERID` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单填写结果表';

-- ----------------------------
-- Table structure for t_custom_form_result_back_up
-- ----------------------------
DROP TABLE IF EXISTS `t_custom_form_result_back_up`;
CREATE TABLE `t_custom_form_result_back_up` (
                                                `id` bigint NOT NULL,
                                                `form_result_id` bigint DEFAULT NULL COMMENT '表单结果Id',
                                                `update_user_id` bigint DEFAULT NULL COMMENT '修改人ID',
                                                `json_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '填写结果数据',
                                                `user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                                `encrypted` int DEFAULT '1' COMMENT '数据是否加密',
                                                PRIMARY KEY (`id`) USING BTREE,
                                                KEY `I_FORMRESULT_BACKUP_RESULT_ID` (`form_result_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单填写修改备份表';

-- ----------------------------
-- Table structure for t_custom_form_result_export
-- ----------------------------
DROP TABLE IF EXISTS `t_custom_form_result_export`;
CREATE TABLE `t_custom_form_result_export` (
                                               `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                               `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                               `id` bigint unsigned NOT NULL DEFAULT '2',
                                               `export_file_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '导出的文件名字',
                                               `export_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '导出类型(baseInfo 基本信息 ,follow_up 随访计划)',
                                               `plan_id_array_json` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '随访计划的id数组JSON格式存储',
                                               `base_info_scope_array_json` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '基本信息导出的范围([BASE_INFO,HEALTH_RECORD])',
                                               `export_progress` int DEFAULT NULL COMMENT '导出的进度(已导出患者数/患者总数)',
                                               `export_id` bigint DEFAULT NULL COMMENT '导出任务的序号ID',
                                               `export_file_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '导出的表格的下载链接',
                                               `export_total` int DEFAULT NULL COMMENT '患者数或医生数或医助数',
                                               `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '导出过程中产生的信息',
                                               `org_id` bigint DEFAULT NULL COMMENT '机构的ID',
                                               `patient_im_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '患者的IM账号',
                                               `page_query_json` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '导出查询条件json',
                                               `current_user_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '当前登录用户角色',
                                               PRIMARY KEY (`id`) USING BTREE,
                                               KEY `I_FORM_RESULT_EXPORT_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单结果导出记录表';

-- ----------------------------
-- Table structure for t_custom_form_result_score
-- ----------------------------
DROP TABLE IF EXISTS `t_custom_form_result_score`;
CREATE TABLE `t_custom_form_result_score` (
                                              `id` bigint NOT NULL,
                                              `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `form_result_sum_score` float(5,1) DEFAULT NULL COMMENT '总分',
  `form_result_average_score` float(5,1) DEFAULT NULL COMMENT '平均分',
  `form_result_sum_average_score` float(5,1) DEFAULT NULL COMMENT '总分和+平均分',
  `form_field_group_sum_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '各分组成绩json数据对象',
  `form_id` bigint DEFAULT NULL COMMENT '表单ID',
  `form_result_id` bigint DEFAULT NULL COMMENT '表单结果ID',
  `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `I_FORM_RESULT_SCORE_FORM_RESULT_ID` (`form_result_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单结果的成绩计算';

-- ----------------------------
-- Table structure for t_custom_form_score_rule
-- ----------------------------
DROP TABLE IF EXISTS `t_custom_form_score_rule`;
CREATE TABLE `t_custom_form_score_rule` (
                                            `id` bigint NOT NULL,
                                            `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                            `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                            `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                            `form_result_count_way` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '结果计算方式(sum_score, average_score, sum_average_score)',
                                            `show_result_sum` int DEFAULT NULL COMMENT '展示总分',
                                            `show_group_sum` int DEFAULT NULL COMMENT '展示分组之和',
                                            `show_average` int DEFAULT NULL COMMENT '展示平均分',
                                            `form_id` bigint DEFAULT NULL COMMENT '所属表单ID',
                                            PRIMARY KEY (`id`) USING BTREE,
                                            KEY `I_FORM_SCORE_RULE_FORMID` (`form_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单设置的成绩规则';

-- ----------------------------
-- Table structure for t_custom_sugar_form_result
-- ----------------------------
DROP TABLE IF EXISTS `t_custom_sugar_form_result`;
CREATE TABLE `t_custom_sugar_form_result` (
                                              `id` bigint NOT NULL,
                                              `sugar_value` float(10,2) DEFAULT NULL COMMENT '血糖值',
  `patient_id` bigint DEFAULT NULL COMMENT '会员ID',
  `del_flag` tinyint DEFAULT NULL,
  `remarks` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` tinyint DEFAULT NULL COMMENT '类型(0凌晨 1早餐前 2早餐后 3午餐前 4午餐后 5晚餐前 6晚餐后 7睡前)',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `time_` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_day` bigint DEFAULT NULL COMMENT '记录时间戳',
  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `data_feed_back` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据反馈的结果JSON, null不用显示',
  `message_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `I_FORMRESULTSUGAR_PATIENTID` (`patient_id`) USING BTREE,
  KEY `I_FORMRESULTSUGAR_TYPE` (`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='血糖表';

-- ----------------------------
-- Table structure for t_form_statistics_baseline_value
-- ----------------------------
DROP TABLE IF EXISTS `t_form_statistics_baseline_value`;
CREATE TABLE `t_form_statistics_baseline_value` (
                                                    `id` bigint NOT NULL,
                                                    `statistics_task_id` bigint DEFAULT NULL,
                                                    `form_field_label` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                    `chart_order` int DEFAULT NULL,
                                                    `chart_width` int DEFAULT NULL,
                                                    `show_or_hide` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                    `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                                    `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                                    `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                                    PRIMARY KEY (`id`) USING BTREE,
                                                    KEY `I_STATISTICS_BASELINE_VALUE_TASKID` (`tenant_code`,`statistics_task_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='基线值统计图';

-- ----------------------------
-- Table structure for t_form_statistics_compliance_rate
-- ----------------------------
DROP TABLE IF EXISTS `t_form_statistics_compliance_rate`;
CREATE TABLE `t_form_statistics_compliance_rate` (
                                                     `id` bigint NOT NULL,
                                                     `statistics_task_id` bigint DEFAULT NULL,
                                                     `form_field_label` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                     `chart_order` int DEFAULT NULL,
                                                     `chart_width` int DEFAULT NULL,
                                                     `show_or_hide` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                     `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                                     `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                                     `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                                     PRIMARY KEY (`id`) USING BTREE,
                                                     KEY `I_STATISTICS_COMPLIANCE_RATE_TASKID_IDX` (`tenant_code`,`statistics_task_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='达标率统计图';

-- ----------------------------
-- Table structure for t_form_statistics_data
-- ----------------------------
DROP TABLE IF EXISTS `t_form_statistics_data`;
CREATE TABLE `t_form_statistics_data` (
                                          `id` bigint NOT NULL,
                                          `statistics_task_id` bigint DEFAULT NULL,
                                          `form_id` bigint DEFAULT NULL,
                                          `form_result_id` bigint DEFAULT NULL,
                                          `form_field_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `class_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `organ_id` bigint DEFAULT NULL,
                                          `patient_id` bigint DEFAULT NULL,
                                          `doctor_id` bigint DEFAULT NULL,
                                          `nursing_id` bigint DEFAULT NULL,
                                          `submit_year` char(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `submit_month` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `submit_date` datetime DEFAULT NULL,
                                          `widget_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `submit_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                          `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                          `target_value` double(8,4) DEFAULT NULL COMMENT '用户填写时的目标值',
                                          `reach_the_standard` int DEFAULT NULL COMMENT '是否达标',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `STATISTICS_DATA_SUBMIT_DATE_IDX` (`submit_year`,`submit_month`,`submit_date`) USING BTREE COMMENT '按提交的年月日创建索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统计任务数据收集表';

-- ----------------------------
-- Table structure for t_form_statistics_data_month
-- ----------------------------
DROP TABLE IF EXISTS `t_form_statistics_data_month`;
CREATE TABLE `t_form_statistics_data_month` (
                                                `id` bigint NOT NULL,
                                                `statistics_task_id` bigint DEFAULT NULL,
                                                `form_id` bigint DEFAULT NULL,
                                                `form_field_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `class_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `organ_id` bigint DEFAULT NULL,
                                                `patient_id` bigint DEFAULT NULL,
                                                `doctor_id` bigint DEFAULT NULL,
                                                `nursing_id` bigint DEFAULT NULL,
                                                `submit_year` char(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `submit_month` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `widget_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `submit_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                                `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                                `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                                `target_value` double(8,4) DEFAULT NULL COMMENT '用户填写时的目标值',
                                                `reach_the_standard` int DEFAULT NULL COMMENT '是否达标',
                                                `submit_date` datetime DEFAULT NULL COMMENT '提交时间',
                                                `form_result_id` bigint DEFAULT NULL COMMENT '表单结果的ID',
                                                PRIMARY KEY (`id`) USING BTREE,
                                                KEY `I_STATISTICS_DATA_MONTH_ORGANID` (`tenant_code`,`organ_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统计任务数据收集表';

-- ----------------------------
-- Table structure for t_form_statistics_data_new
-- ----------------------------
DROP TABLE IF EXISTS `t_form_statistics_data_new`;
CREATE TABLE `t_form_statistics_data_new` (
                                              `id` bigint NOT NULL,
                                              `statistics_task_id` bigint DEFAULT NULL,
                                              `form_id` bigint DEFAULT NULL,
                                              `form_field_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `class_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `organ_id` bigint DEFAULT NULL,
                                              `patient_id` bigint DEFAULT NULL,
                                              `doctor_id` bigint DEFAULT NULL,
                                              `nursing_id` bigint DEFAULT NULL,
                                              `widget_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `submit_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户填写值',
                                              `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                              `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                              `target_value` double(8,4) DEFAULT NULL COMMENT '用户填写值时目标值',
                                              `reach_the_standard` int DEFAULT NULL COMMENT '是否达标',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `I_STATISTICS_DATA_NEW_ORGANID` (`organ_id`) USING BTREE,
                                              KEY `I_STATISTICS_DATA_NEW_TASKID` (`statistics_task_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统计任务数据收集表';

-- ----------------------------
-- Table structure for t_form_statistics_interval
-- ----------------------------
DROP TABLE IF EXISTS `t_form_statistics_interval`;
CREATE TABLE `t_form_statistics_interval` (
                                              `id` bigint NOT NULL,
                                              `statistics_task_id` bigint DEFAULT NULL,
                                              `statistics_interval_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `max_value` double(8,4) DEFAULT NULL,
                                              `min_value` double(8,4) DEFAULT NULL,
                                              `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                              `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `I_STATISTICS_INTERVAL_TC_TASKID` (`statistics_task_id`,`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统计任务区间设置';

-- ----------------------------
-- Table structure for t_form_statistics_master
-- ----------------------------
DROP TABLE IF EXISTS `t_form_statistics_master`;
CREATE TABLE `t_form_statistics_master` (
                                            `id` bigint NOT NULL,
                                            `statistics_task_id` bigint DEFAULT NULL,
                                            `form_field_label` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `chart_order` int DEFAULT NULL,
                                            `chart_width` int DEFAULT NULL,
                                            `show_or_hide` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `belong_type` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `chart_type` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                            `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                            PRIMARY KEY (`id`) USING BTREE,
                                            KEY `I_STATISTICS_MSATER_TENANTCODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='主统计图表';

-- ----------------------------
-- Table structure for t_form_statistics_task
-- ----------------------------
DROP TABLE IF EXISTS `t_form_statistics_task`;
CREATE TABLE `t_form_statistics_task` (
                                          `id` bigint NOT NULL,
                                          `statistics_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '统计图名称',
                                          `plan_id` bigint DEFAULT NULL,
                                          `form_id` bigint DEFAULT NULL,
                                          `form_field_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `form_field_label` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `show_number` int DEFAULT NULL,
                                          `show_percentage` int DEFAULT NULL,
                                          `compliance_rate_chart` int DEFAULT NULL,
                                          `baseline_value_chart` int DEFAULT NULL,
                                          `init_data` int DEFAULT NULL,
                                          `step` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                          `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                          `form_field_unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '字段的单位',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `t_form_statistics_task_tc` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统计任务';

-- ----------------------------
-- Table structure for t_h5_core_functions
-- ----------------------------
DROP TABLE IF EXISTS `t_h5_core_functions`;
CREATE TABLE `t_h5_core_functions` (
                                       `id` bigint NOT NULL,
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `calendar_status` int DEFAULT NULL COMMENT '1展示：0 隐藏',
                                       `im_copywriting` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '在线咨询显示文案',
                                       `im_button_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '在线咨询按钮文本',
                                       `im_button_style_left` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '在线咨询按钮风格左侧颜色',
                                       `im_status` int DEFAULT NULL COMMENT '1 展示:  0 隐藏',
                                       `im_button_style_right` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '在线咨询按钮风格右侧颜色',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者个人中心核心功能';

-- ----------------------------
-- Table structure for t_h5_router
-- ----------------------------
DROP TABLE IF EXISTS `t_h5_router`;
CREATE TABLE `t_h5_router` (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `status_` bit(1) NOT NULL DEFAULT b'1' COMMENT '显示状态',
                               `ban_delete` bit(1) NOT NULL DEFAULT b'1' COMMENT '禁止删除',
                               `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `sort_value` int DEFAULT '1' COMMENT '排序',
                               `dict_item_id` bigint NOT NULL COMMENT '类型ID',
                               `dict_item_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                               `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                               `user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `patient_menu_doctor_status` bit(1) DEFAULT NULL COMMENT '患者菜单医生可见状态',
                               `patient_menu_nursing_status` bit(1) DEFAULT NULL COMMENT '患者菜单医助可见状态',
                               `dict_item_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单字典类型',
                               `module_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'baseInfo（基本信息），myFeatures(我的功能)，myFile(我的档案)',
                               PRIMARY KEY (`id`) USING BTREE,
                               KEY `I_ROUTER_TENANT_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者路由';

-- ----------------------------
-- Table structure for t_h5_ui
-- ----------------------------
DROP TABLE IF EXISTS `t_h5_ui`;
CREATE TABLE `t_h5_ui` (
                           `id` bigint NOT NULL COMMENT 'ID',
                           `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `attribute_1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `attribute_2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `sort_value` int DEFAULT '1' COMMENT '排序',
                           `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                           `type_` tinyint(1) DEFAULT NULL COMMENT 'ui组件类型：1图片 2其它',
                           `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           PRIMARY KEY (`id`) USING BTREE,
                           KEY `I_H5_TENANT_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者路由';

-- ----------------------------
-- Table structure for t_nursing_aim
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_aim`;
CREATE TABLE `t_nursing_aim` (
                                 `id` bigint NOT NULL,
                                 `name_` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `is_contain` tinyint DEFAULT NULL COMMENT '是否包含（0：不包含  1：包含）',
                                 `project_id` bigint DEFAULT NULL COMMENT '项目ID',
                                 `is_default` tinyint DEFAULT NULL COMMENT '是否是默认目标 （0：不是  1：是）',
                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `IDX_NURSING_AIM_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理目标';

-- ----------------------------
-- Table structure for t_nursing_completeness_information
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_completeness_information`;
CREATE TABLE `t_nursing_completeness_information` (
                                                      `id` bigint NOT NULL,
                                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                      `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                      `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                                      `last_write_time` datetime DEFAULT NULL COMMENT '最后填写时间(更新field时，更新这个字段)',
                                                      `completion` int DEFAULT NULL COMMENT '患者信息完整度（100到0的整数）',
                                                      `complete` int DEFAULT NULL COMMENT '患者信息是否完整(1：是，0：否)',
                                                      PRIMARY KEY (`id`) USING BTREE,
                                                      KEY `I_CONPLETENESS_INFORMATION_PATIENTID` (`tenant_code`,`patient_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者信息完整度概览表';

-- ----------------------------
-- Table structure for t_nursing_completion_information_statistics
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_completion_information_statistics`;
CREATE TABLE `t_nursing_completion_information_statistics` (
                                                               `id` bigint NOT NULL,
                                                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                               `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                               `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                               `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                               `nursing_id` bigint DEFAULT NULL COMMENT '医助ID',
                                                               `patient_total` int DEFAULT NULL COMMENT '患者总数',
                                                               `statistics_date` datetime DEFAULT NULL COMMENT '记录日期',
                                                               PRIMARY KEY (`id`) USING BTREE,
                                                               KEY `I_CONPLETION_INFOMATION_STATISTICS_NURSING_ID` (`nursing_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='信息完整度统计';

-- ----------------------------
-- Table structure for t_nursing_completion_information_statistics_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_completion_information_statistics_detail`;
CREATE TABLE `t_nursing_completion_information_statistics_detail` (
                                                                      `id` bigint NOT NULL,
                                                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                                      `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                                      `statistics_id` bigint DEFAULT NULL COMMENT '统计ID',
                                                                      `interval_id` bigint DEFAULT NULL COMMENT '区间ID',
                                                                      `interval_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区间名称',
                                                                      `interval_min_value` double(6,3) DEFAULT NULL COMMENT '区间最小值',
                                                                      `interval_max_value` double(6,3) DEFAULT NULL COMMENT '区间最大值',
                                                                      `interval_proportion` int DEFAULT NULL COMMENT '区间占比',
                                                                      `patient_number` int DEFAULT NULL COMMENT '患者人数',
                                                                      PRIMARY KEY (`id`) USING BTREE,
                                                                      KEY `I_COMPLETION_INFORMATION_STATISTICS_DETAIL_STATISTICSID` (`statistics_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='信息完整度区间统计详细表';

-- ----------------------------
-- Table structure for t_nursing_drugs_condition_monitoring
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_drugs_condition_monitoring`;
CREATE TABLE `t_nursing_drugs_condition_monitoring` (
                                                        `id` bigint NOT NULL,
                                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户编码',
                                                        `drugs_id` bigint DEFAULT NULL COMMENT '药品ID',
                                                        `drugs_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '药品名称',
                                                        `spec` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规格',
                                                        `manufactor` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '厂商',
                                                        `reminder_time` bigint DEFAULT NULL COMMENT '提醒时间（余药不足前X天）',
                                                        `buying_medicine_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '购药地址',
                                                        `template_msg_id` bigint DEFAULT NULL COMMENT '模板消息ID',
                                                        PRIMARY KEY (`id`) USING BTREE,
                                                        KEY `I_DRUGS_CONDITION_MONITORING_DRUGS_ID` (`tenant_code`,`drugs_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者管理-用药预警-预警条件表';

-- ----------------------------
-- Table structure for t_nursing_drugs_result_handle_his
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_drugs_result_handle_his`;
CREATE TABLE `t_nursing_drugs_result_handle_his` (
                                                     `id` bigint NOT NULL,
                                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                     `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户编码',
                                                     `warning_type` tinyint DEFAULT '1' COMMENT '预警类型 (1 余药不足， 2 购药逾期)',
                                                     `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                                     `patient_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '患者名称',
                                                     `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '患者头像',
                                                     `doctor_id` bigint DEFAULT NULL COMMENT '患者所属医生ID',
                                                     `doctor_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '患者所属医生名称',
                                                     `handle_time` datetime DEFAULT NULL COMMENT '异常处理时间',
                                                     `handle_user` bigint DEFAULT NULL COMMENT '异常处理人id',
                                                     `clear_status` tinyint DEFAULT '1' COMMENT '清理状态 (1 未清理， 2 已清理)',
                                                     `drugs_condition_id` bigint DEFAULT NULL COMMENT '预警条件ID',
                                                     `drugs_id` bigint DEFAULT NULL COMMENT '药品ID',
                                                     `drugs_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '药品名称',
                                                     `spec` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规格',
                                                     `manufactor` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '厂商',
                                                     `reminder_time` bigint DEFAULT NULL COMMENT '提醒时间',
                                                     `buying_medicine_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '购药地址',
                                                     `template_msg_id` bigint DEFAULT NULL COMMENT '模板消息ID',
                                                     `template_msg_send_status` tinyint DEFAULT '1' COMMENT '模板消息发送状态 (1 未发送， 2 已发送)',
                                                     `nursing_id` bigint DEFAULT NULL COMMENT '患者所属医助ID',
                                                     PRIMARY KEY (`id`) USING BTREE,
                                                     KEY `I_DRUGS_RESULT_HANDLE_HIS_NURSING_ID` (`tenant_code`,`nursing_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者管理-用药预警-预警处理历史表';

-- ----------------------------
-- Table structure for t_nursing_drugs_result_information
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_drugs_result_information`;
CREATE TABLE `t_nursing_drugs_result_information` (
                                                      `id` bigint NOT NULL,
                                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                      `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户编码',
                                                      `warning_type` tinyint DEFAULT '1' COMMENT '预警类型 (1 余药不足， 2 购药逾期)',
                                                      `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                                      `patient_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '患者名称',
                                                      `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '患者头像',
                                                      `doctor_id` bigint DEFAULT NULL COMMENT '患者所属医生ID',
                                                      `doctor_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '患者所属医生名称',
                                                      `drugs_available_day` int DEFAULT NULL COMMENT '剩余药量可用（逾期）天数',
                                                      `drugs_end_time` date DEFAULT NULL COMMENT '药量用完时间',
                                                      `drugs_condition_id` bigint DEFAULT NULL COMMENT '预警条件ID',
                                                      `drugs_id` bigint DEFAULT NULL COMMENT '药品ID',
                                                      `drugs_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '药品名称',
                                                      `spec` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规格',
                                                      `manufactor` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '厂商',
                                                      `reminder_time` bigint DEFAULT NULL COMMENT '提醒时间（余药不足前X天）',
                                                      `buying_medicine_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '购药地址',
                                                      `template_msg_id` bigint DEFAULT NULL COMMENT '模板消息ID',
                                                      `template_msg_send_status` tinyint DEFAULT '1' COMMENT '模板消息发送状态 (1 未发送， 2 已发送)',
                                                      `nursing_id` bigint DEFAULT NULL COMMENT '患者所属医助ID',
                                                      PRIMARY KEY (`id`) USING BTREE,
                                                      KEY `I_DRUGS_RESULT_INFORMATION_NURSINGID` (`tenant_code`,`nursing_id`) USING BTREE,
                                                      KEY `I_DRUGS_RESULT_INFORMATION_PATIENTID` (`patient_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者管理-用药预警-预警结果表';

-- ----------------------------
-- Table structure for t_nursing_follow_task
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_follow_task`;
CREATE TABLE `t_nursing_follow_task` (
                                         `id` bigint NOT NULL,
                                         `name_` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `show_outline` tinyint DEFAULT NULL COMMENT '是否显示概要（0：不显示  1：显示）',
                                         `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                         `use_default` tinyint DEFAULT NULL COMMENT '使用默认背景图 (1: 使用默认，0 使用上传的)',
                                         `bg_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '背景图片的名称',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `I_NURSING_FOLLOW_TASK_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='随访任务';

-- ----------------------------
-- Table structure for t_nursing_follow_task_content
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_follow_task_content`;
CREATE TABLE `t_nursing_follow_task_content` (
                                                 `id` bigint NOT NULL,
                                                 `plan_id` bigint DEFAULT NULL COMMENT '计划ID',
                                                 `show_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '显示的名称',
                                                 `show_content` tinyint DEFAULT NULL COMMENT '显示或隐藏（0：隐藏  1：显示）',
                                                 `time_frame` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '时间范围',
                                                 `content_sort` int DEFAULT NULL COMMENT '排序',
                                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                 `plan_type` tinyint DEFAULT NULL COMMENT '1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱',
                                                 `plan_status` tinyint DEFAULT NULL COMMENT '护理计划的状态(0前端不可见，不可管理 , 1 可见，可配置管理)',
                                                 `plan_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '护理计划的名称',
                                                 PRIMARY KEY (`id`) USING BTREE,
                                                 KEY `I_FOLLOW_TASK_CONTENT_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='随访任务内容列表';

-- ----------------------------
-- Table structure for t_nursing_function_configuration
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_function_configuration`;
CREATE TABLE `t_nursing_function_configuration` (
                                                    `id` bigint NOT NULL COMMENT 'ID',
                                                    `function_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '功能名称',
                                                    `plan_type` tinyint(1) DEFAULT NULL COMMENT '护理计划类型（1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱）',
                                                    `plan_id` bigint DEFAULT NULL COMMENT '计划的ID',
                                                    `function_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '功能类型',
                                                    `function_status` int DEFAULT NULL COMMENT '功能的状态 1, 开启， 0关闭',
                                                    `has_push_config` int DEFAULT NULL COMMENT '是否有推送配置 1 有， 0 无',
                                                    `has_function_config` int DEFAULT NULL COMMENT '是否有功能配置 1 有， 0 无',
                                                    `caring_template_id` bigint DEFAULT NULL COMMENT '是否有功能配置 1 有， 0 无',
                                                    `wei_xin_template_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有功能配置 1 有， 0 无',
                                                    `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                    `create_time` timestamp NULL DEFAULT NULL,
                                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                    `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                    `has_wei_xin_template` int DEFAULT NULL COMMENT '是否选择微信模板',
                                                    PRIMARY KEY (`id`) USING BTREE,
                                                    KEY `I_FUNCTION_CONFIGURATION_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='功能配置';

-- ----------------------------
-- Table structure for t_nursing_indicators_condition_monitoring
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_indicators_condition_monitoring`;
CREATE TABLE `t_nursing_indicators_condition_monitoring` (
                                                             `id` bigint NOT NULL,
                                                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                             `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                             `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                             `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户编码',
                                                             `plan_id` bigint DEFAULT NULL COMMENT '计划ID',
                                                             `plan_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '计划名称',
                                                             `form_id` bigint DEFAULT NULL COMMENT '表单ID',
                                                             `field_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单中字段ID',
                                                             `field_label` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单字段',
                                                             `max_condition_symbol` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最大值条件符号 1-小于 2-小于等于 3-等于',
                                                             `max_condition_value` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最大值条件值',
                                                             `min_condition_symbol` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最小值条件符号 1-小于 2-小于等于 3-等于',
                                                             `min_condition_value` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最小值条件值',
                                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者管理-监测数据-监控指标条件表';

-- ----------------------------
-- Table structure for t_nursing_indicators_result_information
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_indicators_result_information`;
CREATE TABLE `t_nursing_indicators_result_information` (
                                                           `id` bigint NOT NULL,
                                                           `create_time` datetime NOT NULL COMMENT '创建时间',
                                                           `create_user` bigint NOT NULL COMMENT '创建人id',
                                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                           `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户编码',
                                                           `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                                           `patient_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '患者名称',
                                                           `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '患者头像',
                                                           `nursing_id` bigint DEFAULT NULL COMMENT '患者所属医助ID',
                                                           `doctor_id` bigint NOT NULL COMMENT '患者所属医生ID',
                                                           `doctor_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '患者所属医生名称',
                                                           `handle_status` tinyint DEFAULT '1' COMMENT '异常处理状态 (1 未处理， 2 已处理)',
                                                           `clear_status` tinyint DEFAULT '1' COMMENT '清理状态 (1 未清理， 2 已清理)',
                                                           `handle_time` datetime DEFAULT NULL COMMENT '异常处理时间',
                                                           `handle_user` bigint DEFAULT NULL COMMENT '异常处理人id',
                                                           `indicators_condition_id` bigint DEFAULT NULL COMMENT '监控指标条件ID',
                                                           `plan_id` bigint DEFAULT NULL COMMENT '计划ID',
                                                           `form_id` bigint DEFAULT NULL COMMENT '表单ID',
                                                           `form_result_id` bigint DEFAULT NULL COMMENT '表单结果ID',
                                                           `field_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单中字段ID',
                                                           `field_label` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单字段',
                                                           `real_write_value` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单监控条件字段实际填写值',
                                                           `real_write_value_right_unit` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单监控条件字段右侧单位',
                                                           `form_write_time` datetime DEFAULT NULL COMMENT '表单填写后提交时间',
                                                           `max_condition_symbol` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最大值条件符号',
                                                           `max_condition_value` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最大值条件值',
                                                           `min_condition_symbol` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最小值条件符号',
                                                           `min_condition_value` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最小值条件值',
                                                           PRIMARY KEY (`id`) USING BTREE,
                                                           KEY `I_INDICATORS_RESULT_INFORMATION_PLAN_NURSING_ID` (`plan_id`,`nursing_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者监测数据结果及处理表';

-- ----------------------------
-- Table structure for t_nursing_information_integrity_monitoring
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_information_integrity_monitoring`;
CREATE TABLE `t_nursing_information_integrity_monitoring` (
                                                              `id` bigint NOT NULL,
                                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                              `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                              `business_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '基本信息，疾病信息， 复查提醒，健康日志，护理计划，监测计划，自定义护理计划，自定义监测计划',
                                                              `plan_id` bigint DEFAULT NULL COMMENT '计划ID',
                                                              `form_id` bigint DEFAULT NULL COMMENT '表单ID',
                                                              `field_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单中字段ID',
                                                              `monitor_sort` int DEFAULT NULL COMMENT '排序',
                                                              `field_label` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单字段',
                                                              `plan_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '计划名称',
                                                              PRIMARY KEY (`id`) USING BTREE,
                                                              KEY `I_INFORMATION_INTEGRITY_MONITORING_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='信息完整度监控指标';

-- ----------------------------
-- Table structure for t_nursing_information_monitoring_interval
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_information_monitoring_interval`;
CREATE TABLE `t_nursing_information_monitoring_interval` (
                                                             `id` bigint NOT NULL,
                                                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                             `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                             `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                             `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区间名称',
                                                             `min_value` double(8,3) DEFAULT NULL COMMENT '最小值',
                                                             `max_value` double(8,3) DEFAULT NULL COMMENT '最大值',
                                                             PRIMARY KEY (`id`) USING BTREE,
                                                             KEY `I_INFORMATION_MONITORING_INTERVAL_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='信息完整度区间设置';

-- ----------------------------
-- Table structure for t_nursing_management_history
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_management_history`;
CREATE TABLE `t_nursing_management_history` (
                                                `id` bigint NOT NULL,
                                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `history_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '信息完整度',
                                                `patient_number` int DEFAULT NULL COMMENT '患者数量',
                                                `nursing_id` bigint DEFAULT NULL COMMENT '医助的ID',
                                                `org_id` bigint DEFAULT NULL COMMENT '机构ID',
                                                `send_type` int unsigned NOT NULL COMMENT '发送发送：1->单发，2->群发',
                                                PRIMARY KEY (`id`) USING BTREE,
                                                KEY `t_nursing_management_history_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理历史';

-- ----------------------------
-- Table structure for t_nursing_management_history_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_management_history_detail`;
CREATE TABLE `t_nursing_management_history_detail` (
                                                       `id` bigint NOT NULL,
                                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                       `patient_number` int DEFAULT NULL COMMENT '患者数量(可能没有用处)',
                                                       `history_id` bigint DEFAULT NULL COMMENT '管理历史ID',
                                                       `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                                       `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
                                                       PRIMARY KEY (`id`) USING BTREE,
                                                       KEY `t_nursing_management_history_detail_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理历史详细记录';

-- ----------------------------
-- Table structure for t_nursing_patient_custom_plan_time
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_patient_custom_plan_time`;
CREATE TABLE `t_nursing_patient_custom_plan_time` (
                                                      `id` bigint NOT NULL COMMENT 'ID',
                                                      `patient_id` bigint DEFAULT NULL COMMENT '病人ID',
                                                      `nursing_plant_id` bigint DEFAULT NULL COMMENT '护理计划ID',
                                                      `nursing_plan_detail_id` bigint DEFAULT NULL COMMENT '护理计划详情ID',
                                                      `nursing_plan_detail_time_id` bigint DEFAULT NULL COMMENT '护理计划推送时间详情ID',
                                                      `customize_status` tinyint DEFAULT NULL COMMENT '开启自定义 (1 开启， 2 关闭)',
                                                      `frequency` tinyint DEFAULT NULL COMMENT '下次提醒之后 搁几天推送 频率(0:单次 )',
                                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                      `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                      `next_remind_time` datetime DEFAULT NULL COMMENT '下一次提醒的时间',
                                                      `plan_type` tinyint(1) DEFAULT NULL COMMENT '护理计划类型',
                                                      `remind_time_out` datetime DEFAULT NULL COMMENT '超时提醒时间',
                                                      PRIMARY KEY (`id`) USING BTREE,
                                                      KEY `I_PATIENT_USTOM_PLAN_TIME_PATIENT_IDX` (`patient_id`) USING BTREE,
                                                      KEY `I_PATIENT_CUSTOM_PLAN_TIME_PLANID` (`nursing_plant_id`) USING BTREE,
                                                      KEY `I_PATIENT_CUSTOM_PLAN_TIME_next_remind_time` (`next_remind_time`) USING BTREE,
                                                      KEY `I_PATIENT_CUSTOM_PLAN_TIME_tenant_code` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='自定义护理计划推送';

-- ----------------------------
-- Table structure for t_nursing_patient_information_field
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_patient_information_field`;
CREATE TABLE `t_nursing_patient_information_field` (
                                                       `id` bigint NOT NULL,
                                                       `information_id` bigint DEFAULT NULL,
                                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                       `field_sort` int DEFAULT NULL COMMENT '排序: 跟随监控指标中 monitorSort 字段更新',
                                                       `form_id` bigint DEFAULT NULL COMMENT '表单ID',
                                                       `field_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单中字段ID',
                                                       `field_exact_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '字段的业务类型',
                                                       `field_widget_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '字段的题型',
                                                       `field_values` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '字段的结果',
                                                       `field_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '字段的结果',
                                                       `field_other_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '其他选项设置的备注',
                                                       `business_type` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '基本信息，健康档案，复查提醒...',
                                                       `field_label` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '字段的名称',
                                                       `field_write` int DEFAULT NULL COMMENT '是否填写(1：是，0： 否)',
                                                       `write_time` datetime DEFAULT NULL COMMENT '填写时间',
                                                       `field_label_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '字段的描述 （产品变更）',
                                                       `plan_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '计划名称',
                                                       PRIMARY KEY (`id`) USING BTREE,
                                                       KEY `I_INFORMATION_FIELD_INFORMATIONID` (`information_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者信息完整度字段';

-- ----------------------------
-- Table structure for t_nursing_plan
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_plan`;
CREATE TABLE `t_nursing_plan` (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `name_` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `status_` int DEFAULT NULL COMMENT '状态 1： 开启，0 关闭',
                                  `project_id` bigint DEFAULT NULL COMMENT '项目Id',
                                  `type_` int DEFAULT NULL COMMENT '护理计划类型（单条 多条）',
                                  `is_admin_template` tinyint DEFAULT NULL COMMENT '系统或其他计划  0：系统计划   1：其他计划',
                                  `execute_` int DEFAULT NULL COMMENT '第几天开始执行',
                                  `effective_time` int DEFAULT NULL COMMENT '有效时间（0：长期  N：具体天数）',
                                  `plan_type` tinyint(1) DEFAULT NULL COMMENT '护理计划类型（1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱）',
                                  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `create_time` timestamp NULL DEFAULT NULL,
                                  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                  `system_template` int DEFAULT NULL COMMENT '系统模板 0 否, 1 是',
                                  `follow_up_plan_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '随访计划类型  护理计划 care_plan， 监测数据 monitoring_data',
                                  `plan_icon` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '计划图标',
                                  `plan_desc` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '计划的描述',
                                  `learn_plan_role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学习计划角色 医生 doctor, 患者 patient',
                                  `plan_model` int DEFAULT '0' COMMENT '0 普通计划 1 注射计划',
                                  `next_remind` int DEFAULT NULL COMMENT '下次提醒间隔天数',
                                  `time_out_remind` int DEFAULT NULL COMMENT '超时提醒天数',
                                  `time_out_remind_template` bigint DEFAULT NULL COMMENT '超时模版',
                                  `timeout_recovery` int DEFAULT NULL COMMENT '超时后间隔几天恢复推送',
                                  `remind_template_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '正常推送的服务标题',
                                  `time_out_remind_template_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '超时推送服务标题',
                                  `time_out_recovery_remind_template_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '超时后恢复时推送的提醒',
                                  `time_out_plan_id` bigint DEFAULT NULL COMMENT '超时推荐模板跳转的计划',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  KEY `IDX_NURSING_PLAN_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理计划（随访服务）';

-- ----------------------------
-- Table structure for t_nursing_plan_cms_reminder_log
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_plan_cms_reminder_log`;
CREATE TABLE `t_nursing_plan_cms_reminder_log` (
                                                   `id` bigint NOT NULL COMMENT '主键ID',
                                                   `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                                   `cms_id` bigint DEFAULT NULL COMMENT '文章ID',
                                                   `c_icon` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章缩略图',
                                                   `cms_title` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章标题',
                                                   `cms_link` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章的链接',
                                                   `send_time` datetime DEFAULT NULL COMMENT '发送时间',
                                                   `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                   `user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                   PRIMARY KEY (`id`) USING BTREE,
                                                   KEY `PLAN_CMS_REMINDER_LOG_USER_ID` (`user_id`) USING BTREE,
                                                   KEY `I_PLAN_CMS_REMINDER_LOG_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='历史推文';

-- ----------------------------
-- Table structure for t_nursing_plan_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_plan_detail`;
CREATE TABLE `t_nursing_plan_detail` (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `execute_` int DEFAULT NULL COMMENT '第几天开始执行',
                                         `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `nursing_plan_id` bigint DEFAULT NULL COMMENT '护理计划ID',
                                         `type_` int DEFAULT NULL COMMENT '推送类型(0:图文消息 1：模板消息 2：文字)',
                                         `num` int DEFAULT NULL COMMENT '营养食谱小时数',
                                         `diet_type` tinyint(1) DEFAULT NULL COMMENT '营养食谱小时数类型（0  检验数据 1个性化饮食）',
                                         `template_message_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                         `push_type` tinyint DEFAULT NULL COMMENT ' null 0 默认表单, 2 外部跳转链接',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `IDX_NURSING_PLAN_DETAIL_PLANID` (`nursing_plan_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理计划详情';

-- ----------------------------
-- Table structure for t_nursing_plan_detail_time
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_plan_detail_time`;
CREATE TABLE `t_nursing_plan_detail_time` (
                                              `id` bigint NOT NULL COMMENT 'ID',
                                              `frequency` int DEFAULT NULL COMMENT '推送频率(0:单次 )',
                                              `time_` time DEFAULT NULL COMMENT '推送时间',
                                              `nursing_plan_detail_id` bigint DEFAULT NULL COMMENT '护理计划详情ID',
                                              `pre_time` int DEFAULT NULL COMMENT '触发前？触发后？几天？',
                                              `template_message_id` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `cms_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章标题',
                                              `send_url` int DEFAULT '0' COMMENT '推送的是否链接 (0： 文章， 1： 链接)',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `I_NPDT_DID` (`nursing_plan_detail_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理计划详情时间';

-- ----------------------------
-- Table structure for t_nursing_plan_learn_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_plan_learn_detail`;
CREATE TABLE `t_nursing_plan_learn_detail` (
                                               `id` bigint NOT NULL COMMENT 'ID',
                                               `learn_plan_id` bigint DEFAULT NULL COMMENT '学习计划ID',
                                               `nursing_plan_detail_time_id` bigint DEFAULT NULL,
                                               `status_` tinyint(1) DEFAULT NULL COMMENT '0 未推送 1已推送',
                                               `order_` int DEFAULT NULL COMMENT '学习计划顺序',
                                               `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                               `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                               `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                               PRIMARY KEY (`id`) USING BTREE,
                                               KEY `IDX_NPDL_LID` (`learn_plan_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理计划-学习';

-- ----------------------------
-- Table structure for t_nursing_plan_reminder_log
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_plan_reminder_log`;
CREATE TABLE `t_nursing_plan_reminder_log` (
                                               `id` bigint NOT NULL,
                                               `patient_id` bigint DEFAULT NULL,
                                               `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                               `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                               `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                               `status_` int DEFAULT NULL COMMENT '打卡状态（0 未打卡，1 打卡）',
                                               `type_` int DEFAULT NULL COMMENT '推送类型',
                                               `work_id` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `comment_` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `finish_this_check_in` int DEFAULT NULL COMMENT '打开状态',
                                               `class_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '机构码',
                                               `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
                                               `nursing_id` bigint DEFAULT NULL COMMENT '专员ID',
                                               `org_id` bigint DEFAULT NULL COMMENT '机构ID',
                                               `plan_id` bigint DEFAULT NULL COMMENT '护理计划ID',
                                               `open_this_message` int DEFAULT '0' COMMENT '用户打开了此消息， 比如打开表单',
                                               `wait_push_time` datetime DEFAULT NULL COMMENT '等待推送的时间',
                                               `push_time_success` datetime DEFAULT NULL COMMENT '推送成功时间',
                                               `plan_detail_id` bigint DEFAULT NULL COMMENT '计划详情ID',
                                               `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '推送失败原因',
                                               PRIMARY KEY (`id`) USING BTREE,
                                               KEY `I_PLAN_REMINDER_LOG_PATIENTID` (`patient_id`) USING BTREE,
                                               KEY `I_PLAN_REMINDER_LOG_ORGID` (`org_id`) USING BTREE,
                                               KEY `I_PLAN_REMINDER_LOG_DOCTORID` (`doctor_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for t_nursing_plan_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_plan_tag`;
CREATE TABLE `t_nursing_plan_tag` (
                                      `id` bigint NOT NULL COMMENT '主键ID',
                                      `tag_id` bigint DEFAULT NULL COMMENT '标签ID',
                                      `nursing_plan_id` bigint DEFAULT NULL COMMENT '业务ID(学习计划是详情时间ID，护理计划是计划ID，护理目标是护理目标ID)',
                                      `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      KEY `I_NPT_TAGID` (`tag_id`) USING BTREE,
                                      KEY `I_NPT_NPID` (`nursing_plan_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for t_nursing_unfinished_form_setting
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_unfinished_form_setting`;
CREATE TABLE `t_nursing_unfinished_form_setting` (
                                                     `id` bigint NOT NULL,
                                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                     `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户编码',
                                                     `plan_id` bigint DEFAULT NULL COMMENT '计划ID',
                                                     `plan_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '计划名称',
                                                     `form_id` bigint DEFAULT NULL COMMENT '表单ID',
                                                     `reminder_time` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '提醒时间(1天，2天，3天，5天，7天，其他)',
                                                     `reminder_day` int DEFAULT NULL COMMENT '提醒天数(1,2,3,5,7)',
                                                     `medicine_push` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用药推送',
                                                     `plan_type` tinyint DEFAULT NULL COMMENT '计划类型',
                                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者管理-未完成表单跟踪设置';

-- ----------------------------
-- Table structure for t_nursing_unfinished_patient_record
-- ----------------------------
DROP TABLE IF EXISTS `t_nursing_unfinished_patient_record`;
CREATE TABLE `t_nursing_unfinished_patient_record` (
                                                       `id` bigint NOT NULL,
                                                       `create_time` datetime NOT NULL COMMENT '创建时间',
                                                       `create_user` bigint NOT NULL COMMENT '创建人id',
                                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户编码',
                                                       `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                                       `nursing_id` bigint DEFAULT NULL COMMENT '患者所属医助ID',
                                                       `doctor_id` bigint NOT NULL COMMENT '患者所属医生ID',
                                                       `see_status` tinyint DEFAULT '1' COMMENT '查看状态 (1 未处理， 2 已处理)',
                                                       `handle_status` tinyint DEFAULT '1' COMMENT '处理状态 (1 未处理， 2 已处理)',
                                                       `clear_status` tinyint DEFAULT '1' COMMENT '清理状态 (1 未清理， 2 已清理)',
                                                       `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
                                                       `handle_user` bigint DEFAULT NULL COMMENT '处理人id',
                                                       `unfinished_form_setting_id` bigint DEFAULT NULL COMMENT '未完成表单设置ID',
                                                       `plan_id` bigint DEFAULT NULL COMMENT '计划ID',
                                                       `form_id` bigint DEFAULT NULL COMMENT '表单ID',
                                                       `medicine_or_form` tinyint DEFAULT NULL COMMENT '1表单，2用药日历',
                                                       `remind_message_id` bigint DEFAULT NULL COMMENT '推送提醒的消息ID',
                                                       `remind_time` datetime DEFAULT NULL COMMENT '推送提醒的时间',
                                                       `delete_flag` tinyint(1) DEFAULT '0' COMMENT '移除标记： 0 未移除， 1 移除',
                                                       `plan_type` tinyint DEFAULT NULL COMMENT '计划类型',
                                                       `see_time` datetime DEFAULT NULL COMMENT '查看时间',
                                                       PRIMARY KEY (`id`) USING BTREE,
                                                       KEY `I_UNFINISHED_PATIENT_RECORD_NURSING_ID` (`tenant_code`,`nursing_id`) USING BTREE,
                                                       KEY `I_UNFINISHED_PATIENT_RECORD_DOCTOR_ID` (`tenant_code`,`doctor_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='未完成推送的患者记录';

-- ----------------------------
-- Table structure for t_patient_day_drugs
-- ----------------------------
DROP TABLE IF EXISTS `t_patient_day_drugs`;
CREATE TABLE `t_patient_day_drugs` (
                                       `id` bigint NOT NULL COMMENT '主键id',
                                       `patient_id` bigint DEFAULT NULL COMMENT '患者id',
                                       `drugs_count_of_day` float DEFAULT NULL COMMENT '每天用药总量',
                                       `take_drugs_count_of_day` float DEFAULT NULL COMMENT '每天已用总量',
                                       `checkined_number` int DEFAULT NULL COMMENT '已打卡次数',
                                       `checkin_number_total` int DEFAULT NULL COMMENT '总打卡次数',
                                       `status` int unsigned DEFAULT '0' COMMENT '0:未打卡  1：部分打卡   2已打卡',
                                       `day_compliance` int DEFAULT NULL,
                                       `doctor_id` bigint DEFAULT NULL COMMENT '医生Id',
                                       `service_advisor_id` bigint DEFAULT NULL COMMENT '服务专员Id',
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_PATIENTDAYDRUGS_PATIENTID` (`patient_id`) USING BTREE,
                                       KEY `I_PATIENTDAYDRUGS_STATUS` (`status`) USING BTREE,
                                       KEY `I_PATIENTDAYDRUGS_DOCTORID` (`doctor_id`) USING BTREE,
                                       KEY `I_PATIENTDAYDRUGS_SDVID` (`service_advisor_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者每天的用药量记录（一天生成一次）';

-- ----------------------------
-- Table structure for t_patient_drugs
-- ----------------------------
DROP TABLE IF EXISTS `t_patient_drugs`;
CREATE TABLE `t_patient_drugs` (
                                   `id` bigint NOT NULL COMMENT '主键id',
                                   `patient_id` bigint DEFAULT NULL COMMENT '患者id',
                                   `drugs_id` bigint DEFAULT NULL COMMENT '药品id',
                                   `number_of_day` int DEFAULT NULL COMMENT '周期内用药次数',
                                   `checkin_num` int DEFAULT NULL COMMENT '每天已推送打卡次数',
                                   `drugs_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `cycle` int DEFAULT NULL COMMENT '用药周期(0  代表无限期  1：选择截止日期)',
                                   `end_time` datetime DEFAULT NULL COMMENT '结束日期',
                                   `dose` float DEFAULT NULL COMMENT '每次剂量',
                                   `status` int DEFAULT NULL COMMENT '0：使用  1：停用（不推送，没有终止）  2：终止用药（历史用药）',
                                   `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `cycle_day` int DEFAULT NULL COMMENT '周期循环次数',
                                   `medicine_icon` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `medicine_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `number_of_boxes` int DEFAULT NULL COMMENT '盒数',
                                   `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                   `close_remind` int DEFAULT '2' COMMENT '关闭提醒 1 表示关闭， 2 表示开启',
                                   `stop_reason` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '停止用药的原因recover: 病好了， bad_curative_effect: 疗效不好，" +\r\n            "adverse_reactions: 出现严重不良反应（需要备注）， other: 其他（需要备注）',
                                   `stop_reason_remark` varchar(1500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '停止用药的原因备注',
                                   `buy_drugs_reminder_time` date DEFAULT NULL COMMENT '购药提醒时间',
                                   `early_warning_monitor` int DEFAULT NULL COMMENT '预警监听 1： 可监听， 2：不需要监听',
                                   `start_take_medicine_date` date DEFAULT NULL COMMENT '当前周期开始吃药日期',
                                   `time_period` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '时间周期：天(day), 小时(hour)，周(week),月(moon)',
                                   `cycle_duration` int DEFAULT NULL COMMENT '1到30',
                                   `next_reminder_date` datetime DEFAULT NULL COMMENT '下次推送时间',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `I_PATIENTDRUGS_PATIENTID` (`patient_id`) USING BTREE,
                                   KEY `I_PATIENTDRUGS_DRUGSID` (`drugs_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者添加的用药';

-- ----------------------------
-- Table structure for t_patient_drugs_history_log
-- ----------------------------
DROP TABLE IF EXISTS `t_patient_drugs_history_log`;
CREATE TABLE `t_patient_drugs_history_log` (
                                               `id` bigint NOT NULL,
                                               `operate_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `medicine_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `current_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `before_current_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                               `drugs_id` bigint DEFAULT NULL COMMENT '药品ID',
                                               `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                               `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                               `history_date` date DEFAULT NULL COMMENT '历史记录日期',
                                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                               `operate_type_sort` int DEFAULT NULL COMMENT '操作类型（created 4 ， updated 3 ， stop 2, current 1）',
                                               `current_number_of_day` int DEFAULT NULL COMMENT '当前每天用药次数',
                                               `before_current_number_of_day` int DEFAULT NULL COMMENT '改前每天用药次数',
                                               `current_dose` float DEFAULT NULL COMMENT '当前每次剂量',
                                               `before_current_dose` float DEFAULT NULL COMMENT '改前每次剂量',
                                               `tenant_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `create_time` datetime DEFAULT NULL,
                                               `before_time_period` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '之前时间周期：天(day), 小时(hour)，周(week),月(moon)',
                                               `before_cycle_duration` int DEFAULT NULL COMMENT '之前周期时长 1到30',
                                               `current_time_period` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '当前时间周期：天(day), 小时(hour)，周(week),月(moon)',
                                               `current_cycle_duration` int DEFAULT NULL COMMENT '当前1到30',
                                               PRIMARY KEY (`id`) USING BTREE,
                                               KEY `I_PATIENT_DRGUS_HISTORY_LOG_PATIENTID` (`patient_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者药箱操作的记录';

-- ----------------------------
-- Table structure for t_patient_drugs_time
-- ----------------------------
DROP TABLE IF EXISTS `t_patient_drugs_time`;
CREATE TABLE `t_patient_drugs_time` (
                                        `id` bigint NOT NULL COMMENT '主键id',
                                        `drugs_id` bigint DEFAULT NULL COMMENT '患者药品id',
                                        `drugs_time` datetime DEFAULT NULL COMMENT '用药时间',
                                        `status` int unsigned DEFAULT '2' COMMENT '状态(1:已打卡 2：未打卡)',
                                        `drugs_dose` float DEFAULT NULL COMMENT '消耗量',
                                        `patient_id` bigint DEFAULT NULL COMMENT '患者id',
                                        `medicine_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `medicine_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        `need_push` int DEFAULT '1' COMMENT '需要推送 1， 不需要推送0',
                                        `patient_drugs_id` bigint DEFAULT NULL COMMENT '患者药品记录的ID',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `I_PATIENTDRUGS_TIME_DRUGSID` (`drugs_id`) USING BTREE,
                                        KEY `I_PATIENTDRUGS_TIME_PATIENTID` (`patient_id`) USING BTREE,
                                        KEY `I_PATIENTDRUGS_TIME_PATIENT_DRUGS_ID` (`patient_drugs_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每次推送生成一条记录，（记录药量，药品）';

-- ----------------------------
-- Table structure for t_patient_drugs_time_setting
-- ----------------------------
DROP TABLE IF EXISTS `t_patient_drugs_time_setting`;
CREATE TABLE `t_patient_drugs_time_setting` (
                                                `id` bigint NOT NULL COMMENT '主键id',
                                                `patient_drugs_id` bigint DEFAULT NULL COMMENT '患者药品的记录id',
                                                `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                `the_first_time` int DEFAULT NULL COMMENT '第几次(1-9)',
                                                `day_of_the_cycle` int DEFAULT NULL COMMENT '周期内第几天',
                                                `trigger_time_of_the_day` time DEFAULT NULL COMMENT '当天触发的时间(N点00分，N点15分，N点30分，N点45分)',
                                                `patient_Id` bigint DEFAULT NULL COMMENT '患者ID',
                                                PRIMARY KEY (`id`) USING BTREE,
                                                KEY `I_PATIENTDRUGS_DRUGSID_INDEX` (`patient_drugs_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者用药的时间设置';

-- ----------------------------
-- Table structure for t_patient_manage_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_patient_manage_menu`;
CREATE TABLE `t_patient_manage_menu` (
                                         `id` bigint NOT NULL,
                                         `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `icon` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `menu_sort` int DEFAULT NULL,
                                         `show_status` int DEFAULT '1' COMMENT '显示或隐藏(0 隐藏 1显示)',
                                         `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                         `menu_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单类型(信息完整度，监测数据，用药预警)',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `I_PATIENT_MANAGE_MENU` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者管理平台菜单';

-- ----------------------------
-- Table structure for t_patient_nursing_plan
-- ----------------------------
DROP TABLE IF EXISTS `t_patient_nursing_plan`;
CREATE TABLE `t_patient_nursing_plan` (
                                          `id` bigint unsigned NOT NULL COMMENT 'ID',
                                          `patient_id` bigint DEFAULT NULL COMMENT '病人ID',
                                          `nursing_plant_id` bigint DEFAULT NULL COMMENT '护理计划ID',
                                          `start_date` date DEFAULT NULL COMMENT '护理开始时间',
                                          `is_subscribe` tinyint DEFAULT NULL COMMENT '订阅状态(1：订阅   0：未订阅)',
                                          `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                          `patient_cancel_subscribe` tinyint DEFAULT NULL COMMENT '用药提醒患者手动取消订阅',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `I_PNP_PATIENTID` (`patient_id`) USING BTREE,
                                          KEY `I_PNP_NPID` (`nursing_plant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员订阅护理计划';

-- ----------------------------
-- Table structure for t_sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dict`;
CREATE TABLE `t_sys_dict` (
                              `id` bigint NOT NULL,
                              `type_` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `describe_` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `status_` bit(1) DEFAULT b'1' COMMENT '状态',
                              `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                              `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统字典类型';

-- ----------------------------
-- Table structure for t_sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dict_item`;
CREATE TABLE `t_sys_dict_item` (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `dictionary_id` bigint NOT NULL COMMENT '类型ID',
                                   `dictionary_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `attr1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `status_` bit(1) DEFAULT b'1' COMMENT '状态',
                                   `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `sort_value` int DEFAULT '1' COMMENT '排序',
                                   `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                   `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `dict_code_item_code_uniq` (`dictionary_type`,`code`) USING BTREE COMMENT '字典编码与字典项目编码联合唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典项';

-- ----------------------------
-- Table structure for t_sys_drugs
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_drugs`;
CREATE TABLE `t_sys_drugs` (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `generic_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `icon` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `gyzz` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `manufactor` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `spec` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `usage_` varchar(2500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `instructions` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '说明书',
                               `applicable_disease` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '适用症',
                               `dosage` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '用量',
                               `taboo` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '禁忌症',
                               `side_effects` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '不良反应',
                               `interaction` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '药物相互作用',
                               `del_flag` int DEFAULT NULL COMMENT '删除标记(0：未删除)',
                               `number` int DEFAULT NULL COMMENT '每日用药次数',
                               `dosage_count` int DEFAULT NULL COMMENT '药量计数',
                               `dose` float(10,2) DEFAULT NULL COMMENT '每次剂量',
  `time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cycle` int DEFAULT NULL COMMENT '用药周期(0:长期   N :N天)',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pyszm` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `commondity_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `characters` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `english_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `c_executive_standard` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_mi` tinyint(1) DEFAULT NULL COMMENT '是否MI用药',
  `is_otc` tinyint(1) DEFAULT NULL COMMENT '是否OTC用药',
  `pack` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `storage` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `valid_time` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sticky` int DEFAULT '-1' COMMENT '是否置顶 1 置顶  -1 不置顶',
  `sticky_time` datetime DEFAULT NULL COMMENT '置顶时间',
  `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_user` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `link` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reptile_ok` int DEFAULT NULL COMMENT '爬取详情状态',
  `reptile` int DEFAULT NULL COMMENT '爬取过来的药品',
  `dosage_form` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_status` int DEFAULT '0' COMMENT '1已更新，0未更新',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `I_DRUGS_CATEGORYID` (`category_id`) USING BTREE,
  KEY `I_DRUGS_GYZZ` (`gyzz`) USING BTREE,
  KEY `I_DRUGS_CODE` (`code`) USING BTREE,
  KEY `I_DRUGS_PYSZM` (`pyszm`) USING BTREE,
  KEY `I_DRUGS_ISOTC` (`is_otc`) USING BTREE,
  KEY `I_DRUGS_NAME` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='药品';

-- ----------------------------
-- Table structure for t_sys_drugs_category
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_drugs_category`;
CREATE TABLE `t_sys_drugs_category` (
                                        `id` bigint NOT NULL,
                                        `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `parent_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `class_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `sort_value` int DEFAULT NULL COMMENT '排序',
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        `link` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `reptile_ok` int DEFAULT NULL COMMENT '爬取成功1',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='药品类别';

-- ----------------------------
-- Table structure for t_sys_drugs_category_link
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_drugs_category_link`;
CREATE TABLE `t_sys_drugs_category_link` (
                                             `id` bigint NOT NULL AUTO_INCREMENT,
                                             `drugs_id` bigint DEFAULT NULL COMMENT '药品Id',
                                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                             `category_parent_id` bigint DEFAULT NULL COMMENT '父类别ID',
                                             `category_id` bigint DEFAULT NULL COMMENT '类别ID',
                                             `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                             `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1904428174792458241 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='药品类别关联表';

-- ----------------------------
-- Table structure for t_sys_drugs_image
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_drugs_image`;
CREATE TABLE `t_sys_drugs_image` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `drugs_id` bigint DEFAULT NULL,
                                     `icon` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `w_link` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `update_time` timestamp(6) NULL DEFAULT NULL,
                                     `update_user` bigint DEFAULT NULL,
                                     `create_time` timestamp(6) NULL DEFAULT NULL,
                                     `create_user` bigint DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     KEY `I_DRUGS_IMAGE_DRUGSID` (`drugs_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1570223199646384129 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for t_sys_sequence
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_sequence`;
CREATE TABLE `t_sys_sequence` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                  `seq_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `current_val` bigint DEFAULT NULL COMMENT '当前值',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='自增';

-- ----------------------------
-- Table structure for t_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag` (
                         `id` bigint NOT NULL,
                         `name_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `order_` int DEFAULT NULL COMMENT '优先级',
                         `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                         `handle_attr_bind` int DEFAULT NULL COMMENT '处理标签绑定 0：未处理 2：处理中 1：完成',
                         PRIMARY KEY (`id`) USING BTREE,
                         KEY `I_TAG_TENANT_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签管理';

-- ----------------------------
-- Table structure for t_tag_association
-- ----------------------------
DROP TABLE IF EXISTS `t_tag_association`;
CREATE TABLE `t_tag_association` (
                                     `id` bigint NOT NULL,
                                     `tag_id` bigint DEFAULT NULL COMMENT '标签Id',
                                     `c_association_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `association_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `order_` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `attr_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                                     `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     KEY `I_TAGASS_TAGID` (`tag_id`) USING BTREE,
                                     KEY `I_TAGASS_ASID` (`association_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业务关联标签记录表';

-- ----------------------------
-- Table structure for t_tag_attr
-- ----------------------------
DROP TABLE IF EXISTS `t_tag_attr`;
CREATE TABLE `t_tag_attr` (
                              `id` bigint NOT NULL,
                              `attr_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `attr_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `attr_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `attr_value_min` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `attr_value_max` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `tag_id` bigint DEFAULT NULL COMMENT '标签Id',
                              `widget_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                              `attr_source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '基本信息， 健康档案， 监测计划， 药品',
                              `plan_id` bigint DEFAULT NULL COMMENT '监测计划的ID',
                              PRIMARY KEY (`id`) USING BTREE,
                              KEY `I_TAGATTR_ATTRID` (`attr_id`) USING BTREE,
                              KEY `I_TAGATTR_TAGID` (`tag_id`) USING BTREE,
                              KEY `I_TAGATTR_TYPE` (`widget_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签属性';

-- ----------------------------
-- Table structure for t_tenant_library
-- ----------------------------
DROP TABLE IF EXISTS `t_tenant_library`;
CREATE TABLE `t_tenant_library` (
                                    `id` bigint NOT NULL,
                                    `library_id` bigint DEFAULT NULL COMMENT '内容库ID',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                    `update_user` bigint DEFAULT NULL COMMENT '修改人id',
                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                    `tenant_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目的内容库';

-- ----------------------------
-- Table structure for t_tenant_library_global_user
-- ----------------------------
DROP TABLE IF EXISTS `t_tenant_library_global_user`;
CREATE TABLE `t_tenant_library_global_user` (
                                                `id` bigint NOT NULL,
                                                `library_id` bigint DEFAULT NULL COMMENT '内容库ID',
                                                `global_user_id` bigint DEFAULT NULL COMMENT '全局用户ID',
                                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                                `update_user` bigint DEFAULT NULL COMMENT '修改人id',
                                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容库的权限账号';

-- ----------------------------
-- Table structure for t_trace_into_field_option_config
-- ----------------------------
DROP TABLE IF EXISTS `t_trace_into_field_option_config`;
CREATE TABLE `t_trace_into_field_option_config` (
                                                    `id` bigint NOT NULL,
                                                    `plan_id` bigint NOT NULL COMMENT '随访计划ID',
                                                    `form_id` bigint NOT NULL COMMENT '表单ID',
                                                    `parent_field_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '父题目的ID',
                                                    `parent_field_option_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '父题目选项的ID',
                                                    `form_field_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '跟踪的选项它所属的题目',
                                                    `field_option_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '最终跟踪的选项',
                                                    `is_child_field` int NOT NULL COMMENT '是否子题目（0 否 1是）',
                                                    `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                    `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                    PRIMARY KEY (`id`) USING BTREE,
                                                    KEY `I_TRACE_INTO_FIELD_OPTION_CONFIG_PLANID` (`plan_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选项跟踪表单字段选项配置表';

-- ----------------------------
-- Table structure for t_trace_into_form_result_last_push_time
-- ----------------------------
DROP TABLE IF EXISTS `t_trace_into_form_result_last_push_time`;
CREATE TABLE `t_trace_into_form_result_last_push_time` (
                                                           `id` bigint NOT NULL,
                                                           `plan_id` bigint NOT NULL COMMENT '随访计划ID',
                                                           `patient_id` bigint NOT NULL COMMENT '患者ID',
                                                           `nursing_id` bigint NOT NULL COMMENT '医助ID',
                                                           `form_id` bigint NOT NULL COMMENT '表单ID',
                                                           `push_time` datetime NOT NULL COMMENT '表单结果的上传时间',
                                                           `handle_time` datetime NOT NULL COMMENT '处理时间',
                                                           `handle_status` int NOT NULL COMMENT '处理状态（0 未处理， 1 已处理）',
                                                           `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                           `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                           `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                           `doctor_id` bigint DEFAULT NULL COMMENT '患者的医生ID',
                                                           PRIMARY KEY (`id`) USING BTREE,
                                                           KEY `I_TRACE_INFO_FORM_RESULT_LAST_PUSHTIME_TC_NURSINGID` (`nursing_id`,`tenant_code`) USING BTREE,
                                                           KEY `I_TRACE_INFO_FORM_RESULT_LAST_PUSHTIME_PATIENTID` (`patient_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选项跟踪患者最新上传时间记录表';

-- ----------------------------
-- Table structure for t_trace_into_result
-- ----------------------------
DROP TABLE IF EXISTS `t_trace_into_result`;
CREATE TABLE `t_trace_into_result` (
                                       `id` bigint NOT NULL,
                                       `plan_id` bigint NOT NULL COMMENT '随访计划ID',
                                       `patient_id` bigint NOT NULL COMMENT '患者ID',
                                       `nursing_id` bigint NOT NULL COMMENT '医助ID',
                                       `form_id` bigint NOT NULL COMMENT '表单ID',
                                       `form_result_id` bigint NOT NULL COMMENT '表单结果ID',
                                       `push_time` datetime NOT NULL COMMENT '表单结果的上传时间',
                                       `handle_status` int NOT NULL COMMENT '处理状态（0 未处理， 1 已处理）',
                                       `clear_status` int NOT NULL COMMENT '清空状态（0 未处理， 1 已处理）',
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `doctor_id` bigint DEFAULT NULL COMMENT '患者的医生ID',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_TRACE_INFO_RESULT_NURSINGID` (`nursing_id`) USING BTREE,
                                       KEY `I_TRACE_INFO_RESULT_FORMID` (`form_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选项跟踪结果异常表';

-- ----------------------------
-- Table structure for t_trace_into_result_field_abnormal
-- ----------------------------
DROP TABLE IF EXISTS `t_trace_into_result_field_abnormal`;
CREATE TABLE `t_trace_into_result_field_abnormal` (
                                                      `id` bigint NOT NULL,
                                                      `trace_into_result_id` bigint DEFAULT NULL COMMENT '记录当前选项异常结果所属哪条选项结果',
                                                      `form_result_id` bigint NOT NULL COMMENT '表单结果ID',
                                                      `patient_id` bigint NOT NULL COMMENT '患者ID',
                                                      `nursing_id` bigint NOT NULL COMMENT '医助ID',
                                                      `plan_id` bigint NOT NULL COMMENT '随访计划ID',
                                                      `parent_field_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '父题目的ID',
                                                      `parent_field_option_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '父题目选项ID',
                                                      `form_field_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '异常选项所在的题目ID',
                                                      `form_field_option_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '异常选项的ID',
                                                      `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                      `form_id` bigint DEFAULT NULL COMMENT '表单ID',
                                                      `trace_into_config_id` bigint NOT NULL COMMENT '记录当前选项异常结果所属哪条选项结果',
                                                      `doctor_id` bigint DEFAULT NULL COMMENT '患者的医生ID',
                                                      PRIMARY KEY (`id`) USING BTREE,
                                                      KEY `I_TRACE_INFO_RESULT_FIELD_ABNORMAL_TC_NURSING_FORMID` (`tenant_code`,`nursing_id`,`form_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选项跟踪异常题目明细记录表';

-- ----------------------------
-- Table structure for t_wx_api_error
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_api_error`;
CREATE TABLE `t_wx_api_error` (
                                  `id` bigint NOT NULL,
                                  `wx_error_message` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                                  `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                  `wx_app_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信接口异常';

-- ----------------------------
-- Table structure for t_wx_app_component_token
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_app_component_token`;
CREATE TABLE `t_wx_app_component_token` (
                                            `id` bigint NOT NULL COMMENT 'ID',
                                            `app_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第三方平台的appid',
                                            `component_app_secret` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `component_verify_ticket` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `component_access_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `component_access_token_expires_in` bigint DEFAULT NULL COMMENT '排序',
                                            `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                            `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方平台verifyTicket记录表';

-- ----------------------------
-- Table structure for t_wx_config
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_config`;
CREATE TABLE `t_wx_config` (
                               `id` bigint NOT NULL,
                               `app_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `app_secret` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `token` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `ase_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `source_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `authorization_file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `menu` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '微信菜单',
                               `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                               `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                               `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `third_authorization` tinyint DEFAULT NULL COMMENT '第三方授权的公众号，默认为false表示之前手动配置的公众号',
                               `component_app_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `third_create_time` bigint DEFAULT NULL,
                               `authorization_code` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `pre_auth_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `authorization_code_expired_time` bigint DEFAULT NULL,
                               `auth_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `authorizer_access_token` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `authorizer_refresh_token` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `expires_in` bigint DEFAULT NULL,
                               `access_token_create_time` bigint DEFAULT NULL,
                               `func_info_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                               `become_silent` int DEFAULT NULL COMMENT '静默公众号 1 静默 不处理公众号的任何事件和消息',
                               `auth_type` int DEFAULT '1' COMMENT '要授权的账号类型, 1 表示手机端仅展示公众号；2 表示仅展示小程序，3 表示公众号和小程序都展示',
                               `wx_qr_code_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信二维码链接',
                               PRIMARY KEY (`id`) USING BTREE,
                               KEY `I_PUBNO_APPID` (`app_id`,`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信配置信息';

-- ----------------------------
-- Table structure for t_wx_config_additional
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_config_additional`;
CREATE TABLE `t_wx_config_additional` (
                                          `id` bigint NOT NULL,
                                          `wx_app_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `message_callback_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `image_message_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `link_message_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `location_message_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `text_message_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `video_message_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `voice_message_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `voice_reconige_message_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `subscribe_event_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `click_event_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `location_event_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `scan_event_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `template_job_finish_event_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `unsubscribe_event_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `view_event_handler_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `doctor_weixin_login_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `patient_clock_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `patient_pain_log_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `patient_check_data_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `appointment_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `patient_group_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `doctor_chat` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `patient_chat` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `blood_pressure_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `blood_glucose_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `review_reminder_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `health_log_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `study_plan_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `nutrition_recipes_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                          `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                          `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `key_word_status` int unsigned DEFAULT '2' COMMENT '关键字回复是否开启 1 开启， 2 关闭',
                                          `automatic_reply` int unsigned DEFAULT '2' COMMENT '自动回复是否开启 1 开启， 2 关闭',
                                          `cms_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `I_PUBNOSETTING_PUBNOAPPID` (`wx_app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信附加信息配置';

-- ----------------------------
-- Table structure for t_wx_custom_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_custom_menu`;
CREATE TABLE `t_wx_custom_menu` (
                                    `id` bigint NOT NULL,
                                    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `key_` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `del_flag` tinyint DEFAULT NULL COMMENT '删除标志',
                                    `app_id` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `page_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                    `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE,
                                    KEY `I_MBTYPE` (`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目自定义微信菜单';

-- ----------------------------
-- Table structure for t_wx_js_api_ticket
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_js_api_ticket`;
CREATE TABLE `t_wx_js_api_ticket` (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `app_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号appid',
                                      `jsapi_ticket` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `jsapi_ticket_expired_time` bigint DEFAULT NULL COMMENT '排序',
                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方平台使用JSSDK需要的jsapi_ticket';

-- ----------------------------
-- Table structure for t_wx_keyword
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_keyword`;
CREATE TABLE `t_wx_keyword` (
                                `id` bigint NOT NULL,
                                `key_word` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `match_type` tinyint DEFAULT NULL COMMENT '类型（0：半匹配 1：全匹配）',
                                `message_type` tinyint DEFAULT NULL COMMENT '消息类型（0：文本  1：图文  ）',
                                `media_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `rule_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `del_flag` tinyint DEFAULT NULL COMMENT '删除标志',
                                `reply` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `I_KEYWORD_MEDIAID` (`media_id`) USING BTREE,
                                KEY `I_KEYWORD_KEY_WORD` (`tenant_code`,`key_word`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信服务号自动回复关键词';

-- ----------------------------
-- Table structure for t_wx_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_menu`;
CREATE TABLE `t_wx_menu` (
                             `id` bigint NOT NULL,
                             `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单标题',
                             `menu_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT ' 菜单的响应动作类型.view表示网页类型，click表示点击类型，miniprogram表示小程序类型',
                             `menu_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单KEY值，用于消息接口推送，不超过128字节.click等点击类型必须',
                             `menu_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '网页链接.',
                             `media_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '新增永久素材接口返回的合法media_id.',
                             `article_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发布图文接口获得的article_id.',
                             `appid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '小程序的appid.miniprogram类型必须',
                             `pagepath` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '小程序的页面路径.miniprogram类型必须',
                             `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                             `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                             `text_content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文本内容',
                             `wx_app_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信公众号菜单';

-- ----------------------------
-- Table structure for t_wx_pre_auth_code
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_pre_auth_code`;
CREATE TABLE `t_wx_pre_auth_code` (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `pre_auth_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预授权码',
                                      `tenant_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                      `auth_type` int DEFAULT '1' COMMENT '1 表示手机端仅展示公众号；2 表示仅展示小程序，3 表示公众号和小程序都展示。',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台申请的预授权码用于授权公众号或小程序';

-- ----------------------------
-- Table structure for t_wx_reg_guide
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_reg_guide`;
CREATE TABLE `t_wx_reg_guide` (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `guide` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `describe_` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `icon` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `del_flag` int DEFAULT NULL COMMENT '删除标记(0：未删除)',
                                  `enable_intro` tinyint DEFAULT NULL COMMENT '是否添加项目介绍 0:添加  1：不添加',
                                  `intro` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目介绍url',
                                  `agreement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '协议',
                                  `nursing_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `success_msg_type` tinyint DEFAULT NULL COMMENT '入组成功消息类型 0：图文消息   1：文字消息',
                                  `success_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `has_show_doctor` tinyint unsigned DEFAULT '0' COMMENT '是否显示医生：0显示，1不显示',
                                  `has_fill_wx_name` tinyint unsigned DEFAULT '0' COMMENT '是否填充微信名：0：填充 1：不填充',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `create_user` bigint DEFAULT NULL COMMENT '创建人',
                                  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                  `update_user` bigint DEFAULT NULL COMMENT '修改人',
                                  `has_show_recommend_drugs` tinyint unsigned DEFAULT '0' COMMENT '是否在入组界面中是否显示推荐用药：0显示，1不显示',
                                  `has_fill_drugs` tinyint unsigned DEFAULT '0' COMMENT '是否在注册时填写用药信息：0填写，1不填写',
                                  `base_info_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `health_info_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `has_unregistered_reminder` tinyint DEFAULT NULL,
                                  `unregistered_reminder` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `buy_drugs_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '购药跳转url',
                                  `buy_drugs_url_switch` tinyint unsigned DEFAULT '0' COMMENT '是否开启购药跳转url：0开启，1不开启',
                                  `doctor_agreement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '医生的服务协议',
                                  `form_history_record` tinyint DEFAULT '0' COMMENT '表单历史记录功能： 0不记录， 1记录',
                                  `wx_user_default_role` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'patient' COMMENT '游客，医生，患者',
                                  `has_show_org_name` tinyint DEFAULT NULL COMMENT '是否显示机构名称：0显示，1不显示',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  KEY `I_WX_REG_GUIDE_TENANT_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信注册引导';

-- ----------------------------
-- Table structure for t_wx_sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_sys_menu`;
CREATE TABLE `t_wx_sys_menu` (
                                 `id` bigint NOT NULL,
                                 `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `button_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `del_flag` tinyint DEFAULT NULL COMMENT '删除标志',
                                 `app_id` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `page_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统定义的微信菜单';

-- ----------------------------
-- Table structure for t_wx_template_msg
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_template_msg`;
CREATE TABLE `t_wx_template_msg` (
                                     `id` bigint NOT NULL,
                                     `template_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `business_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `extra_json` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '模板json',
                                     `indefiner` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `source_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                     `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                     `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     KEY `I_TEMPLATEMSG_INDEF` (`tenant_code`,`indefiner`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模板消息';

-- ----------------------------
-- Table structure for t_wx_template_msg_fields
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_template_msg_fields`;
CREATE TABLE `t_wx_template_msg_fields` (
                                            `id` bigint NOT NULL,
                                            `template_id` bigint DEFAULT NULL COMMENT '对应微信公众平台中的模板Id',
                                            `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `attr` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `type` tinyint DEFAULT NULL COMMENT '类型',
                                            `value` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '属性值',
                                            `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `business_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `create_user` bigint DEFAULT '0' COMMENT '创建人id',
                                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                            `update_user` bigint DEFAULT '0' COMMENT '更新人id',
                                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                            `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            PRIMARY KEY (`id`) USING BTREE,
                                            KEY `I_TMF_TPID` (`tenant_code`,`template_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模板消息 属性表通过 templateId 和 TemplateMessage 表关联。';

-- ----------------------------
-- Table structure for t_wx_user_info
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_user_info`;
CREATE TABLE `t_wx_user_info` (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                  `nickname` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `head_img_url` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `union_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                  `user_role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  PRIMARY KEY (`id`) USING BTREE,
                                  KEY `I_WX_USER_INFO_OPEN_ID` (`open_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生授权后得到的医生微信信息';

-- ----------------------------
-- Table structure for t_wx_wechat_orders
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_wechat_orders`;
CREATE TABLE `t_wx_wechat_orders` (
                                      `id` bigint NOT NULL COMMENT 'ID商户订单号',
                                      `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号ID或小程序ID',
                                      `mchid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商户号',
                                      `open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '下单openId',
                                      `description` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品描述',
                                      `time_expire` datetime DEFAULT NULL COMMENT '交易结束时间',
                                      `amount` int DEFAULT NULL COMMENT '订单金额 单位是 分',
                                      `amount_currency` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'CNY：人民币',
                                      `prepay_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预支付交易会话标识',
                                      `error_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误码',
                                      `error_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误描述',
                                      `notify_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付通知ID',
                                      `notify_create_time` datetime DEFAULT NULL COMMENT '通知创建时间',
                                      `notify_event_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知类型 支付成功通知的类型为TRANSACTION.SUCCESS',
                                      `notify_resource_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知的资源数据类型 支付成功通知为encrypt-resource',
                                      `notify_summary` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回调摘要',
                                      `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信支付订单号',
                                      `trade_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '交易类型',
                                      `bank_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '付款银行',
                                      `trade_state` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '交易状态',
                                      `success_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付完成时间',
                                      `payer_total` int DEFAULT NULL COMMENT '用户支付金额',
                                      `payer_currency` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户支付币种',
                                      `payer_openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付者',
                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                      `business_id` bigint DEFAULT NULL COMMENT '业务的ID',
                                      `business_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务类',
                                      `notify_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回调业务接口',
                                      `pay_way` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付方式 小程序 H5 native',
                                      `refund_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退款单号ID',
                                      `refund_channel` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单退款渠道',
                                      `user_received_account` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '取当前退款单的退款入账方，有以下几种情况',
                                      `refund_success_time` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退款成功时间',
                                      `refund_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退款单的退款处理状态。',
                                      `refund_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退款所使用资金对应的资金账户类型',
                                      `refund_amount` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单退款金额信息',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信支付订单';

-- ----------------------------
-- Table structure for u_consultation_group
-- ----------------------------
DROP TABLE IF EXISTS `u_consultation_group`;
CREATE TABLE `u_consultation_group` (
                                        `id` bigint NOT NULL,
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `nurse_id` bigint DEFAULT NULL COMMENT '服务专员ID',
                                        `im_group_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `group_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                        `continued` bigint DEFAULT NULL COMMENT '持续时间(分钟)',
                                        `consultation_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `member_avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `consultation_entrance` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
                                        `create_user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人身份',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `consultation_group_nurse_id_consultation_status` (`nurse_id`,`consultation_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会诊组';

-- ----------------------------
-- Table structure for u_consultation_group_member
-- ----------------------------
DROP TABLE IF EXISTS `u_consultation_group_member`;
CREATE TABLE `u_consultation_group_member` (
                                               `id` bigint NOT NULL,
                                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                               `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                               `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                               `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `consultation_group_id` bigint DEFAULT NULL COMMENT '会诊组ID',
                                               `member_im_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `member_open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `member_avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `member_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `member_role` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `member_role_remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                               `member_status` int DEFAULT NULL COMMENT '成员状态(-1 不可移除，0 扫码未加入 1 加入状态， 2 已移除)',
                                               `patient_id` bigint DEFAULT NULL COMMENT '患者ID(只有角色是患者的时候此值才有)',
                                               `receive_weixin_template` int DEFAULT NULL COMMENT '是否接收微信的模板消息 1 是,  0 否',
                                               `member_refuse_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '拒绝原因',
                                               `member_user_id` bigint DEFAULT NULL COMMENT '成员用户ID(医助ID，医生ID)',
                                               `invite_people` bigint DEFAULT NULL COMMENT '邀请人ID',
                                               `invite_people_role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邀请人角色',
                                               `mobile` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户手机号(代替openId识别用户)',
                                               PRIMARY KEY (`id`) USING BTREE,
                                               KEY `consultation_group_member_groupIdx` (`consultation_group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会诊组成员';

-- ----------------------------
-- Table structure for u_user_custom_grouping
-- ----------------------------
DROP TABLE IF EXISTS `u_user_custom_grouping`;
CREATE TABLE `u_user_custom_grouping` (
                                          `id` bigint NOT NULL COMMENT 'ID',
                                          `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                          `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `role_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色(doctor, NursingStaff)',
                                          `group_sort` int DEFAULT NULL COMMENT '排序',
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                          `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `custom_grouping_userId_idx` (`tenant_code`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='自定义小组';

-- ----------------------------
-- Table structure for u_user_custom_grouping_patient
-- ----------------------------
DROP TABLE IF EXISTS `u_user_custom_grouping_patient`;
CREATE TABLE `u_user_custom_grouping_patient` (
                                                  `id` bigint NOT NULL COMMENT 'ID',
                                                  `custom_grouping_id` bigint DEFAULT NULL COMMENT '自定义分组的ID',
                                                  `patient_id` bigint DEFAULT NULL COMMENT '患者的ID',
                                                  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                  PRIMARY KEY (`id`) USING BTREE,
                                                  KEY `custom_grouping_patient_id_idx` (`tenant_code`,`custom_grouping_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='自定义小组的患者';

-- ----------------------------
-- Table structure for u_user_doctor
-- ----------------------------
DROP TABLE IF EXISTS `u_user_doctor`;
CREATE TABLE `u_user_doctor` (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `group_id` bigint DEFAULT NULL COMMENT '小组ID',
                                 `remarks` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `group_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `nursing_id` bigint DEFAULT NULL COMMENT '服务专员ID',
                                 `nursing_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `org_id` bigint DEFAULT NULL COMMENT '机构ID',
                                 `province` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `city` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `hospital_id` bigint DEFAULT NULL COMMENT '医院',
                                 `department_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `title` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `mobile` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `avatar` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `nick_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `wx_app_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `open_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `im_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `hospital_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `qr_code` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `business_card_qr_code` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `deptartment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `organ_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `organ_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `birthday` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `last_login_ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `total_login_times` int DEFAULT NULL COMMENT '登录次数',
                                 `sex` tinyint DEFAULT NULL COMMENT '性别 0:男 1：女',
                                 `last_login_time` bigint DEFAULT NULL COMMENT '最后登录时间，时间戳类型',
                                 `extra_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '扩展内容',
                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `login_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `class_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `im_group_status` int DEFAULT NULL COMMENT '是否展开聊天小组, 0 关闭，1展示',
                                 `im_msg_status` int DEFAULT NULL COMMENT '是否接收IM消息 0 不接收, 1接收',
                                 `wx_status` int DEFAULT NULL COMMENT '微信关注状态 null 或 0 未关注， 1 关注',
                                 `online` int DEFAULT NULL COMMENT '环信在线1 在线， 0 离线',
                                 `im_wx_template_status` int DEFAULT NULL COMMENT '是否接收Im微信模板消息 0 不接收, 1接收',
                                 `close_appoint` int DEFAULT '0' COMMENT '关闭预约',
                                 `first_login_time` datetime DEFAULT NULL COMMENT '首次登陆时间',
                                 `build_in` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '内置医生：0否，1是',
                                 `independence` tinyint(1) DEFAULT NULL COMMENT '独立医生 null 1 为独立医生， 0 为非独立医生',
                                 `has_group` int DEFAULT '0' COMMENT '拥有小组 (0 没有， 1 拥有)',
                                 `appointment_review` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核开关(无须审核：no_review， 需要审核 need_review)',
                                 `latest_access_time` date DEFAULT NULL COMMENT '最新访问日期',
                                 `register_org_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '注册时填写的机构名称',
                                 `down_load_qr_code` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '下载时的二维码',
                                 `encrypted` int DEFAULT '0' COMMENT '数据是否加密',
                                 `english_qr_code` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '英文版患者关注二维码',
                                 `english_business_card_qr_code` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '英文版医生名片',
                                 `password_updated` int DEFAULT '0' COMMENT '初始密码是否修改',
                                 `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生密码',
                                 `doctor_leader` int DEFAULT '0' COMMENT '个人服务号医生组长',
                                 `login_status` int DEFAULT '0' COMMENT '登录状态0 未登录， 1 一登录',
                                 `ai_hosted` int DEFAULT '0' COMMENT 'AI托管开启状态',
                                 `registration_information` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '挂号信息',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE KEY `uni_tenant_mobile` (`tenant_code`,`mobile`) USING BTREE,
                                 KEY `I_DOCTOR_ACCOUNTID` (`tenant_code`,`nursing_id`) USING BTREE,
                                 KEY `I_DOCTOR_ORGANID` (`tenant_code`,`org_id`) USING BTREE,
                                 KEY `I_DOCTOR_OPENID` (`tenant_code`,`open_id`) USING BTREE,
                                 KEY `I_DOCTOR_TENANT_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生表';

-- ----------------------------
-- Table structure for u_user_doctor_audit
-- ----------------------------
DROP TABLE IF EXISTS `u_user_doctor_audit`;
CREATE TABLE `u_user_doctor_audit` (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生名字',
                                       `nursing_id` bigint DEFAULT NULL COMMENT '服务专员ID',
                                       `nursing_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '服务专员',
                                       `title` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职称',
                                       `mobile` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号码',
                                       `avatar` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
                                       `nick_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信昵称',
                                       `wx_app_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'wxAppId',
                                       `open_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                       `hospital_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医院名称',
                                       `deptartment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '科室名称',
                                       `specialties` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专业特长',
                                       `introduction` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详细介绍',
                                       `audit_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核状态, pass_through 通过， reject 拒绝',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `audit_sort` int DEFAULT NULL COMMENT '审核排序, 已审核 1，未审核 0，审核失败 2',
                                       `hospital_id` bigint DEFAULT NULL,
                                       `password` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_DOCTOR_AUDIT_MOBILE` (`tenant_code`,`mobile`) USING BTREE,
                                       KEY `I_DOCTOR_AUDIT_NAME` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生审核表';

-- ----------------------------
-- Table structure for u_user_doctor_custom_group
-- ----------------------------
DROP TABLE IF EXISTS `u_user_doctor_custom_group`;
CREATE TABLE `u_user_doctor_custom_group` (
                                              `id` bigint NOT NULL,
                                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                              `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                              `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                              `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                              `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
                                              `group_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '小组名称',
                                              `group_number_total` int DEFAULT NULL COMMENT '小组人员数量',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `doctor_custom_group_doctorId_idx` (`doctor_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生的自定义小组';

-- ----------------------------
-- Table structure for u_user_doctor_custom_group_patient
-- ----------------------------
DROP TABLE IF EXISTS `u_user_doctor_custom_group_patient`;
CREATE TABLE `u_user_doctor_custom_group_patient` (
                                                      `id` bigint NOT NULL,
                                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                      `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                      `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                      `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                      `doctor_custom_group_id` bigint DEFAULT NULL COMMENT '医生自定义小组ID',
                                                      `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                                      PRIMARY KEY (`id`) USING BTREE,
                                                      KEY `doctor_custom_group_id_idx` (`doctor_custom_group_id`) USING BTREE,
                                                      KEY `doctor_custom_GROUP_PATIENT_idx` (`patient_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生的自定义小组患者';

-- ----------------------------
-- Table structure for u_user_doctor_group
-- ----------------------------
DROP TABLE IF EXISTS `u_user_doctor_group`;
CREATE TABLE `u_user_doctor_group` (
                                       `id` bigint NOT NULL,
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
                                       `group_id` bigint DEFAULT NULL COMMENT '小组ID',
                                       `join_group_time` datetime DEFAULT NULL COMMENT '加入时间',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `doctor_group_groupId_idx` (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生小组关联表';

-- ----------------------------
-- Table structure for u_user_doctor_no_read_group_doctor_msg
-- ----------------------------
DROP TABLE IF EXISTS `u_user_doctor_no_read_group_doctor_msg`;
CREATE TABLE `u_user_doctor_no_read_group_doctor_msg` (
                                                          `id` bigint NOT NULL,
                                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                          `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                          `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
                                                          `no_read_group_doctor_id` bigint DEFAULT NULL COMMENT '小组ID',
                                                          PRIMARY KEY (`id`) USING BTREE,
                                                          KEY `doctor_noread_group_docor_id_idx` (`doctor_id`) USING BTREE,
                                                          KEY `doctor_noread_group_noreadDocor_id_idx` (`no_read_group_doctor_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生不看那些医生的患者IM';

-- ----------------------------
-- Table structure for u_user_follow_reply
-- ----------------------------
DROP TABLE IF EXISTS `u_user_follow_reply`;
CREATE TABLE `u_user_follow_reply` (
                                       `id` bigint NOT NULL,
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `reply_switch` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '功能开关(open, close)',
                                       `reply_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复类别(Reply_after_following 关注后,  unregistered_reply 未注册回复 )',
                                       `reply_content` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复内容',
                                       `reply_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复类型(text,cms)',
                                       `reply_form` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复形式(system，medical_assistance 医助)',
                                       `reply_time` time DEFAULT NULL COMMENT '回复的时间',
                                       `triggering_conditions` int DEFAULT NULL COMMENT '触发条件 0 当天 正数 后N天。 负数是前N天',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_USER_FOLLOW_REPLY_TENANT_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注后回复和关注未注册回复';

-- ----------------------------
-- Table structure for u_user_group
-- ----------------------------
DROP TABLE IF EXISTS `u_user_group`;
CREATE TABLE `u_user_group` (
                                `id` bigint NOT NULL COMMENT 'ID',
                                `nurse_id` bigint DEFAULT NULL COMMENT '服务专员ID',
                                `nurse_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `icon` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `order_` int DEFAULT NULL COMMENT '排序',
                                `org_id` bigint DEFAULT NULL COMMENT '机构ID',
                                `organ_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `contact_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `contact_mobile` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `class_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '机构CLASSCODE',
                                `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `I_GROUP_NURSEID` (`nurse_id`) USING BTREE,
                                KEY `I_GROUP_ORGANID` (`org_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小组';

-- ----------------------------
-- Table structure for u_user_im_recommendation_heat
-- ----------------------------
DROP TABLE IF EXISTS `u_user_im_recommendation_heat`;
CREATE TABLE `u_user_im_recommendation_heat` (
                                                 `id` bigint NOT NULL COMMENT 'ID',
                                                 `function_id` bigint DEFAULT NULL COMMENT '功能ID',
                                                 `function_heat` int DEFAULT NULL COMMENT '功能热度',
                                                 `function_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '功能类型',
                                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                 `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                                 `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户类型',
                                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                 PRIMARY KEY (`id`) USING BTREE,
                                                 KEY `I_USER_IM_RECOM_HEAT_TUF_IDX` (`tenant_code`,`user_id`,`function_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='im推荐功能热度';

-- ----------------------------
-- Table structure for u_user_keyword_project_settings
-- ----------------------------
DROP TABLE IF EXISTS `u_user_keyword_project_settings`;
CREATE TABLE `u_user_keyword_project_settings` (
                                                   `id` bigint NOT NULL,
                                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                   `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                   `keyword_reply_switch` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '快捷回复开关',
                                                   `keyword_reply_form` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '快捷回复形式',
                                                   PRIMARY KEY (`id`) USING BTREE,
                                                   KEY `I_USER_KEYWORD_PROJECT_SETTING_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目关键字开关配置';

-- ----------------------------
-- Table structure for u_user_keyword_reply
-- ----------------------------
DROP TABLE IF EXISTS `u_user_keyword_reply`;
CREATE TABLE `u_user_keyword_reply` (
                                        `id` bigint NOT NULL,
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `rule_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规则名',
                                        `reply_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复类型 (text, cms)',
                                        `reply_content` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复内容',
                                        `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '启用或关闭（open, close）',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `I_USER_KEYWORD_REPLY_TENANTCODE_IDX` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关键字回复内容';

-- ----------------------------
-- Table structure for u_user_keyword_reply_trigger_record
-- ----------------------------
DROP TABLE IF EXISTS `u_user_keyword_reply_trigger_record`;
CREATE TABLE `u_user_keyword_reply_trigger_record` (
                                                       `id` bigint NOT NULL,
                                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                       `keyword_reply_id` bigint DEFAULT NULL COMMENT '关键字规则ID',
                                                       `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                                       `trigger_date` date DEFAULT NULL COMMENT '触发日期',
                                                       `trigger_date_time` datetime DEFAULT NULL COMMENT '触发时间',
                                                       PRIMARY KEY (`id`) USING BTREE,
                                                       KEY `I_USER_KEYWORD_REPLY_TRIGGER_RECORD_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关键字触发日期';

-- ----------------------------
-- Table structure for u_user_keyword_setting
-- ----------------------------
DROP TABLE IF EXISTS `u_user_keyword_setting`;
CREATE TABLE `u_user_keyword_setting` (
                                          `id` bigint NOT NULL,
                                          `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                          `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                          `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                          `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `keyword_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关键字',
                                          `match_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '匹配类型(full_match: 全匹配， semi_match 半匹配)',
                                          `keyword_reply_id` bigint DEFAULT NULL COMMENT '关键字规则id',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `I_user_keyword_setting_tc` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关键字设置';

-- ----------------------------
-- Table structure for u_user_keyword_trigger_record
-- ----------------------------
DROP TABLE IF EXISTS `u_user_keyword_trigger_record`;
CREATE TABLE `u_user_keyword_trigger_record` (
                                                 `id` bigint NOT NULL,
                                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                 `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                 `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                 `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                 `keyword_id` bigint DEFAULT NULL COMMENT '关键字ID',
                                                 `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
                                                 `trigger_date` date DEFAULT NULL COMMENT '触发日期',
                                                 `trigger_date_time` datetime DEFAULT NULL COMMENT '触发时间',
                                                 PRIMARY KEY (`id`) USING BTREE,
                                                 KEY `I_USER_KEYWORD_TRIGGER_RECORD_TC_KEYID` (`tenant_code`,`keyword_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关键字触发日期';

-- ----------------------------
-- Table structure for u_user_merck_constant
-- ----------------------------
DROP TABLE IF EXISTS `u_user_merck_constant`;
CREATE TABLE `u_user_merck_constant` (
                                         `id` bigint NOT NULL,
                                         `const_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `create_time` datetime DEFAULT NULL,
                                         `create_user` bigint DEFAULT NULL,
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for u_user_merck_person
-- ----------------------------
DROP TABLE IF EXISTS `u_user_merck_person`;
CREATE TABLE `u_user_merck_person` (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `person_id` bigint DEFAULT NULL,
                                       `open_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `union_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `information_sync` int DEFAULT NULL COMMENT '同步状态 1 已同步 0 未同步',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `subscribe` int DEFAULT NULL COMMENT '关注状态',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='敏识用户';

-- ----------------------------
-- Table structure for u_user_miniapp_info
-- ----------------------------
DROP TABLE IF EXISTS `u_user_miniapp_info`;
CREATE TABLE `u_user_miniapp_info` (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                       `role_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色(doctor, NursingStaff,patient)',
                                       `mini_app_open_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '小程序openId',
                                       `mini_app_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '小程序appId',
                                       `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户手机号',
                                       `remind_subscription_massage` int DEFAULT NULL COMMENT '是否需要发送关注公众号消息',
                                       `session_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会话密钥',
                                       `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名称',
                                       `user_avatar` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `I_USER_MINIAPP_OPENID` (`mini_app_open_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小程序用户openId关联表';

-- ----------------------------
-- Table structure for u_user_nursing_staff
-- ----------------------------
DROP TABLE IF EXISTS `u_user_nursing_staff`;
CREATE TABLE `u_user_nursing_staff` (
                                        `id` bigint NOT NULL COMMENT '主键',
                                        `login_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `password_strong_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `im_account` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `gesture_pwd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `class_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `certificate` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `total_login_times` int DEFAULT NULL COMMENT '登录次数',
                                        `org_id` bigint DEFAULT NULL COMMENT '所属单位ID',
                                        `organ_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `organ_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `sex` tinyint DEFAULT NULL COMMENT '性别 0:男 1：女',
                                        `birthday` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `extra_info` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin COMMENT '额外信息',
                                        `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `last_login_time` bigint DEFAULT NULL COMMENT '创建时间',
                                        `create_time` datetime DEFAULT NULL,
                                        `create_user` bigint DEFAULT NULL,
                                        `update_time` datetime DEFAULT NULL,
                                        `update_user` bigint DEFAULT NULL,
                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `im_group_status` int DEFAULT NULL COMMENT '是否展开聊天小组, 0 关闭，1展示',
                                        `latest_access_time` date DEFAULT NULL COMMENT '最新访问日期',
                                        `encrypted` int DEFAULT '0' COMMENT '数据是否加密',
                                        `open_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'openId',
                                        `wx_status` int DEFAULT NULL COMMENT '微信关注状态 null 或 0 未关注， 1 关注',
                                        `im_wx_template_status` int DEFAULT NULL COMMENT '是否接收Im微信模板消息 0 不接收, 1接收',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE KEY `t_user_unique_login_name_corp_id` (`login_name`,`org_id`) USING BTREE,
                                        UNIQUE KEY `idx_unique_mobile_tenant` (`tenant_code`,`mobile`) USING BTREE,
                                        KEY `t_user_normal_login_name` (`login_name`) USING BTREE,
                                        KEY `t_user_normal_name` (`name`) USING BTREE,
                                        KEY `I_USER_ORGANID` (`org_id`) USING BTREE,
                                        KEY `I_USER_NURSING_STAFF_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-服务专员';

-- ----------------------------
-- Table structure for u_user_patient
-- ----------------------------
DROP TABLE IF EXISTS `u_user_patient`;
CREATE TABLE `u_user_patient` (
                                  `id` bigint NOT NULL COMMENT '主键编号',
                                  `service_advisor_id` bigint DEFAULT NULL COMMENT '服务专员ID',
                                  `service_advisor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `nick_name` varchar(180) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `mobile` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `sex` tinyint(1) DEFAULT NULL COMMENT '性别，0男，1女',
                                  `name` varchar(180) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `birthday` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `group_id` bigint DEFAULT NULL COMMENT '小组ID',
                                  `group_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
                                  `doctor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `status_` int DEFAULT NULL COMMENT '会员状态（0:未注册  1：已注册  2：已注册取关 3：未注册取关)',
                                  `diagnosis_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `diagnosis_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `wx_app_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `open_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '本系统公众号的openId',
                                  `avatar` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `im_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `remark` varchar(180) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `is_complete_enter_group` tinyint DEFAULT NULL COMMENT '是否完成入组信息（0：否  1：是）',
                                  `examine_count` int DEFAULT NULL COMMENT '随访计划完成次数',
                                  `ckd` tinyint DEFAULT NULL COMMENT '病分期',
                                  `nursing_time` datetime DEFAULT NULL COMMENT '随访计划开始时间',
                                  `hospital_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `certificate_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `agree_agreement` int DEFAULT NULL COMMENT '是否同意 入组协议 （0； 同意 1 ）',
                                  `grand_total_check` int DEFAULT NULL COMMENT '用户累计打卡天数',
                                  `successive_check` int DEFAULT NULL COMMENT '用户连续打卡天数',
                                  `height` int DEFAULT NULL COMMENT '身高',
                                  `weight` int DEFAULT NULL COMMENT '体重',
                                  `org_id` bigint DEFAULT NULL COMMENT '所属机构ID',
                                  `organ_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `organ_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `last_login_ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `last_login_time` bigint DEFAULT NULL COMMENT '最后登录时间，时间戳类型',
                                  `total_login_times` int DEFAULT NULL COMMENT '登录次数',
                                  `extra_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '用户额外信息',
                                  `complete_enter_group_time` datetime DEFAULT NULL COMMENT '入组时间',
                                  `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `class_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `im_group_status` int DEFAULT NULL COMMENT '是否展开聊天小组, 0 关闭，1展示',
                                  `online` int DEFAULT NULL COMMENT '环信在线1 在线， 0 离线',
                                  `nursing_staff_im_group_status` int DEFAULT NULL COMMENT '专员是否展开聊天小组, 0 关闭，1展示',
                                  `doctor_im_group_status` int DEFAULT NULL COMMENT '医生是否展开聊天小组 0 关闭  1 展示',
                                  `un_subscribe_time` datetime DEFAULT NULL COMMENT '取关时间',
                                  `union_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                  `doctor_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生对患者备注',
                                  `name_first_letter` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名字第一个字的首字母',
                                  `nursing_exit_chat` int DEFAULT NULL COMMENT '专员是否退出聊天小组, 0 未退出，1 退出',
                                  `doctor_exit_chat` int DEFAULT NULL COMMENT '医生是否退出聊天小组, 0 未退出，1 退出',
                                  `doctor_exit_chat_list_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '医生退出聊天的ID的集合转JSON',
                                  `disease_information_status` int DEFAULT NULL COMMENT '疾病信息的填写状态。 0 未填写， 1 已填写',
                                  `import_wx_open_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '导入公众号粉丝时的openId',
                                  `follow_stage_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '随访阶段ID',
                                  `follow_stage_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '随访阶段名称',
                                  `has_send_chat` tinyint(1) unsigned zerofill DEFAULT '0' COMMENT '是否发起过咨询：1是，0否',
                                  `default_doctor_patient` tinyint(1) DEFAULT NULL COMMENT '默认医生的患者 0不是，1是',
                                  `encrypted` int DEFAULT '0' COMMENT '数据是否加密',
                                  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  KEY `I_PATIENT_SAID` (`service_advisor_id`) USING BTREE,
                                  KEY `I_PATIENT_MOBILE` (`mobile`) USING BTREE,
                                  KEY `I_PATIENT_DOCTORID` (`doctor_id`) USING BTREE,
                                  KEY `I_PATIENT_STATUS` (`status_`) USING BTREE,
                                  KEY `I_PATIENT_OPENID` (`open_id`) USING BTREE,
                                  KEY `I_PATIENT_ORGANID` (`org_id`) USING BTREE,
                                  KEY `I_PATIENT_NAME` (`name`) USING BTREE,
                                  KEY `I_PATIENT_TENANT_CODE` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者表';

-- ----------------------------
-- Table structure for u_user_patient_recommend_relationship
-- ----------------------------
DROP TABLE IF EXISTS `u_user_patient_recommend_relationship`;
CREATE TABLE `u_user_patient_recommend_relationship` (
                                                         `id` bigint NOT NULL,
                                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                         `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                         `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                         `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户编码',
                                                         `patient_id` bigint DEFAULT NULL COMMENT '推荐人患者ID',
                                                         `patient_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '推荐人患者名称',
                                                         `qr_doctor_type` tinyint DEFAULT '1' COMMENT '推荐人二维码医生类型 (1 推荐人原医生， 2 指定医生)',
                                                         `doctor_id` bigint DEFAULT NULL COMMENT '推荐人二维码医生ID',
                                                         `doctor_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '推荐人二维码医生名称',
                                                         `passive_patient_id` bigint DEFAULT NULL COMMENT '受邀人患者ID',
                                                         `passive_patient_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '受邀人患者名称',
                                                         `passive_patient_register` tinyint DEFAULT '1' COMMENT '受邀人患者注册状态 (1 未注册， 2 已注册)',
                                                         `passive_patient_register_time` datetime DEFAULT NULL COMMENT '受邀人患者注册时间',
                                                         `scan_code_time` datetime DEFAULT NULL COMMENT '受邀人扫码时间',
                                                         PRIMARY KEY (`id`) USING BTREE,
                                                         KEY `I_USER_PATIENT_RECOMMEND_RELATIONSHIP_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运营配置-患者推荐关系';

-- ----------------------------
-- Table structure for u_user_patient_recommend_setting
-- ----------------------------
DROP TABLE IF EXISTS `u_user_patient_recommend_setting`;
CREATE TABLE `u_user_patient_recommend_setting` (
                                                    `id` bigint NOT NULL,
                                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                    `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                    `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                    `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户编码',
                                                    `qr_doctor_type` tinyint DEFAULT '1' COMMENT '二维码医生类型 (1 推荐人原医生， 2 指定医生)',
                                                    `doctor_id` bigint DEFAULT NULL COMMENT '二维码医生ID',
                                                    `doctor_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二维码医生名称',
                                                    `activity_rule_switch` tinyint DEFAULT '1' COMMENT '活动规则介绍开关 (1 关闭， 2 开启)',
                                                    `activity_rule_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活动规则介绍H5链接',
                                                    `poster_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活动海报链接',
                                                    `activity_end_date` date DEFAULT NULL COMMENT '活动截止日期',
                                                    PRIMARY KEY (`id`) USING BTREE,
                                                    KEY `I_USER_PATIENT_RECOMMEND_SETTING_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运营配置-患者推荐配置';

-- ----------------------------
-- Table structure for u_user_patient_unregistered_reminder
-- ----------------------------
DROP TABLE IF EXISTS `u_user_patient_unregistered_reminder`;
CREATE TABLE `u_user_patient_unregistered_reminder` (
                                                        `id` bigint NOT NULL,
                                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                                        `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                                        `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                                        `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                        `reminder` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                        `patient_id` bigint DEFAULT NULL COMMENT '发送者ID',
                                                        `error_message` varchar(1500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                                        PRIMARY KEY (`id`) USING BTREE,
                                                        KEY `I_USER_PATIENT_UNREGIS_REMINDER_TC` (`tenant_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者注册提醒';

-- ----------------------------
-- Table structure for u_user_referral
-- ----------------------------
DROP TABLE IF EXISTS `u_user_referral`;
CREATE TABLE `u_user_referral` (
                                   `id` bigint NOT NULL,
                                   `patient_id` bigint DEFAULT NULL COMMENT '患者id',
                                   `patient_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '患者头像',
                                   `patient_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '患者姓名',
                                   `patient_sex` tinyint(1) DEFAULT NULL COMMENT '患者性别，0男，1女',
                                   `patient_age` int DEFAULT NULL COMMENT '患者年龄',
                                   `launch_doctor_id` bigint DEFAULT NULL COMMENT '发起医生id',
                                   `launch_doctor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '发起医生姓名',
                                   `accept_doctor_id` bigint DEFAULT NULL COMMENT '接收医生id',
                                   `accept_doctor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '接收医生姓名',
                                   `accept_doctor_hospital_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收医生医院',
                                   `qr_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '转诊二维码路径',
                                   `referral_status` smallint DEFAULT NULL COMMENT '转诊状态：0未接收、1已接收、2已取消',
                                   `referral_category` tinyint(1) DEFAULT NULL COMMENT '转诊性质：0单次转诊、1长期转诊',
                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                   `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                   `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                   `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `launch_service_id` bigint DEFAULT NULL COMMENT '发起医生专员的id',
                                   `accept_service_id` bigint DEFAULT NULL COMMENT '接收医生专员id',
                                   `accept_time` datetime DEFAULT NULL COMMENT '接收转诊时间',
                                   `launch_time` datetime DEFAULT NULL COMMENT '发起转诊时间',
                                   `launch_nursing_patient_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发起转诊医助对患者的备注',
                                   `launch_doctor_patient_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发起医生对患者的备注',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `I_USER_REFERRAL_TC_ACCEPT_DOCTOR_ID` (`tenant_code`,`accept_doctor_id`) USING BTREE,
                                   KEY `I_USER_REFERRAL_TC_LAUNCH_DOCTOR_ID` (`tenant_code`,`launch_doctor_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者转诊';

-- ----------------------------
-- Table structure for u_user_system_msg
-- ----------------------------
DROP TABLE IF EXISTS `u_user_system_msg`;
CREATE TABLE `u_user_system_msg` (
                                     `id` bigint NOT NULL,
                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                     `create_user` bigint DEFAULT NULL COMMENT '创建人id',
                                     `update_user` bigint DEFAULT NULL COMMENT '更新人id',
                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                     `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `user_id` bigint DEFAULT NULL COMMENT '护理专员Id',
                                     `msg_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `user_role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户角色，默认是专员',
                                     `read_status` int DEFAULT '0' COMMENT '消息状态 0 未读 1 已读',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     KEY `I_USER_SYSTEM_MSG_TC_USER_ID` (`tenant_code`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户消息';

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'increment id',
                            `branch_id` bigint NOT NULL COMMENT 'branch transaction id',
                            `xid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `context` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `rollback_info` longblob NOT NULL COMMENT 'rollback info',
                            `log_status` int NOT NULL COMMENT '0:normal status,1:defense status',
                            `log_created` datetime NOT NULL COMMENT 'create datetime',
                            `log_modified` datetime NOT NULL COMMENT 'modify datetime',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='AT transaction mode undo table';

SET FOREIGN_KEY_CHECKS = 1;
