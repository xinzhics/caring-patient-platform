-- =============================================
-- 卡柠 SaaS - 登录权限相关表初始化数据脚本
-- 导出时间: 2026-02-05
-- 说明: 包含基础初始化数据，用于开源项目快速启动
-- =============================================

-- 清空表数据（按依赖关系顺序）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `c_auth_user_token`;
TRUNCATE TABLE `c_auth_user_role`;
TRUNCATE TABLE `c_auth_role_org`;
TRUNCATE TABLE `c_auth_role_authority`;
TRUNCATE TABLE `c_auth_user`;
TRUNCATE TABLE `c_auth_role`;
TRUNCATE TABLE `c_auth_resource`;
TRUNCATE TABLE `c_auth_menu`;
TRUNCATE TABLE `c_auth_application`;
SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 初始化 c_auth_application 数据
-- ----------------------------
INSERT INTO `c_auth_application` (`id`, `tenant_code`, `client_id`, `client_secret`, `website`, `name`, `icon`, `app_type`, `describe_`, `status`, `create_user`, `create_time`, `update_user`, `update_time`) VALUES
(1, '000000', 'caring_test_open', '123456', NULL, '卡柠开放授权', NULL, 'SERVER', NULL, b'1', NULL, NULL, NULL, NULL),
(2, '000000', 'caring_ui', 'caring_ui_secret', 'http://localhost:10000/caring-ui/', '卡柠管理后台', '', 'PC', '', b'1', 1, NOW(), 1, NOW()),
(3, '000000', 'caring_admin_ui', 'caring_admin_ui_secret', 'http://localhost:180/caring-admin-ui/', '卡柠管理员后台', '', 'PC', '', b'1', 1, NOW(), 1, NOW());

-- ----------------------------
-- 初始化 c_auth_menu 数据
-- ----------------------------
INSERT INTO `c_auth_menu` (`id`, `tenant_code`, `label`, `describe_`, `is_public`, `path`, `component`, `is_enable`, `sort_value`, `icon`, `group_`, `parent_id`, `create_user`, `create_time`, `update_user`, `update_time`) VALUES
-- 一级菜单
(100, '000000', '系统管理', '系统管理模块', b'0', '/system', 'Layout', b'1', 1, 'el-icon-setting', '', 0, 1, NOW(), 1, NOW()),
(200, '000000', '用户中心', '用户服务中心', b'0', '/ucenter', 'Layout', b'1', 2, 'el-icon-user', '', 0, 1, NOW(), 1, NOW()),
(300, '000000', '权限管理', '权限相关管理', b'0', '/auth', 'Layout', b'1', 3, 'el-icon-lock', '', 0, 1, NOW(), 1, NOW()),
(400, '000000', '组织架构', '组织机构管理', b'0', '/org', 'Layout', b'1', 4, 'el-icon-office-building', '', 0, 1, NOW(), 1, NOW()),
-- 系统管理子菜单
(101, '000000', '用户管理', '系统用户管理', b'0', '/system/user', 'system/user/Index', b'1', 1, 'el-icon-s-custom', '', 100, 1, NOW(), 1, NOW()),
(102, '000000', '角色管理', '系统角色管理', b'0', '/system/role', 'system/role/Index', b'1', 2, 'el-icon-s-check', '', 100, 1, NOW(), 1, NOW()),
(103, '000000', '菜单管理', '系统菜单管理', b'0', '/system/menu', 'system/menu/Index', b'1', 3, 'el-icon-menu', '', 100, 1, NOW(), 1, NOW()),
(104, '000000', '资源管理', '系统资源管理', b'0', '/system/resource', 'system/resource/Index', b'1', 4, 'el-icon-link', '', 100, 1, NOW(), 1, NOW()),
(105, '000000', '应用管理', '应用客户端管理', b'0', '/system/application', 'system/application/Index', b'1', 5, 'el-icon-s-platform', '', 100, 1, NOW(), 1, NOW()),
-- 用户中心子菜单
(201, '000000', '医生管理', '医生信息管理', b'0', '/ucenter/doctor', 'ucenter/doctor/Index', b'1', 1, 'el-icon-user-solid', '', 200, 1, NOW(), 1, NOW()),
(202, '000000', '患者管理', '患者信息管理', b'0', '/ucenter/patient', 'ucenter/patient/Index', b'1', 2, 'el-icon-s-custom', '', 200, 1, NOW(), 1, NOW()),
(203, '000000', '小组管理', '小组信息管理', b'0', '/ucenter/group', 'ucenter/group/Index', b'1', 3, 'el-icon-user', '', 200, 1, NOW(), 1, NOW()),
-- 权限管理子菜单
(301, '000000', '角色授权', '角色权限配置', b'0', '/auth/role', 'auth/role/Index', b'1', 1, 'el-icon-key', '', 300, 1, NOW(), 1, NOW()),
(302, '000000', '数据权限', '数据范围权限', b'0', '/auth/ds', 'auth/ds/Index', b'1', 2, 'el-icon-data-line', '', 300, 1, NOW(), 1, NOW());

-- ----------------------------
-- 初始化 c_auth_resource 数据
-- ----------------------------
INSERT INTO `c_auth_resource` (`id`, `tenant_code`, `code`, `name`, `menu_id`, `describe_`, `create_user`, `create_time`, `update_user`, `update_time`) VALUES
-- 用户管理资源
(1001, '000000', 'user:add', '新增', 101, '', 1, NOW(), 1, NOW()),
(1002, '000000', 'user:edit', '编辑', 101, '', 1, NOW(), 1, NOW()),
(1003, '000000', 'user:delete', '删除', 101, '', 1, NOW(), 1, NOW()),
(1004, '000000', 'user:view', '查看', 101, '', 1, NOW(), 1, NOW()),
(1005, '000000', 'user:export', '导出', 101, '', 1, NOW(), 1, NOW()),
(1006, '000000', 'user:import', '导入', 101, '', 1, NOW(), 1, NOW()),
-- 角色管理资源
(1011, '000000', 'role:add', '新增', 102, '', 1, NOW(), 1, NOW()),
(1012, '000000', 'role:edit', '编辑', 102, '', 1, NOW(), 1, NOW()),
(1013, '000000', 'role:delete', '删除', 102, '', 1, NOW(), 1, NOW()),
(1014, '000000', 'role:view', '查看', 102, '', 1, NOW(), 1, NOW()),
(1015, '000000', 'role:grant', '授权', 102, '', 1, NOW(), 1, NOW()),
-- 菜单管理资源
(1021, '000000', 'menu:add', '新增', 103, '', 1, NOW(), 1, NOW()),
(1022, '000000', 'menu:edit', '编辑', 103, '', 1, NOW(), 1, NOW()),
(1023, '000000', 'menu:delete', '删除', 103, '', 1, NOW(), 1, NOW()),
(1024, '000000', 'menu:view', '查看', 103, '', 1, NOW(), 1, NOW()),
-- 资源管理资源
(1031, '000000', 'resource:add', '新增', 104, '', 1, NOW(), 1, NOW()),
(1032, '000000', 'resource:edit', '编辑', 104, '', 1, NOW(), 1, NOW()),
(1033, '000000', 'resource:delete', '删除', 104, '', 1, NOW(), 1, NOW()),
(1034, '000000', 'resource:view', '查看', 104, '', 1, NOW(), 1, NOW()),
-- 应用管理资源
(1041, '000000', 'application:add', '新增', 105, '', 1, NOW(), 1, NOW()),
(1042, '000000', 'application:edit', '编辑', 105, '', 1, NOW(), 1, NOW()),
(1043, '000000', 'application:delete', '删除', 105, '', 1, NOW(), 1, NOW()),
(1044, '000000', 'application:view', '查看', 105, '', 1, NOW(), 1, NOW()),
-- 医生管理资源
(2001, '000000', 'doctor:add', '新增', 201, '', 1, NOW(), 1, NOW()),
(2002, '000000', 'doctor:edit', '编辑', 201, '', 1, NOW(), 1, NOW()),
(2003, '000000', 'doctor:delete', '删除', 201, '', 1, NOW(), 1, NOW()),
(2004, '000000', 'doctor:view', '查看', 201, '', 1, NOW(), 1, NOW()),
-- 患者管理资源
(2011, '000000', 'patient:add', '新增', 202, '', 1, NOW(), 1, NOW()),
(2012, '000000', 'patient:edit', '编辑', 202, '', 1, NOW(), 1, NOW()),
(2013, '000000', 'patient:delete', '删除', 202, '', 1, NOW(), 1, NOW()),
(2014, '000000', 'patient:view', '查看', 202, '', 1, NOW(), 1, NOW()),
-- 小组管理资源
(2021, '000000', 'group:add', '新增', 203, '', 1, NOW(), 1, NOW()),
(2022, '000000', 'group:edit', '编辑', 203, '', 1, NOW(), 1, NOW()),
(2023, '000000', 'group:delete', '删除', 203, '', 1, NOW(), 1, NOW()),
(2024, '000000', 'group:view', '查看', 203, '', 1, NOW(), 1, NOW());

-- ----------------------------
-- 初始化 c_auth_role 数据
-- ----------------------------
INSERT INTO `c_auth_role` (`id`, `tenant_code`, `name`, `code`, `describe_`, `status`, `readonly`, `ds_type`, `create_user`, `create_time`, `update_user`, `update_time`) VALUES
(1, '000000', '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', b'1', b'1', 'ALL', 1, NOW(), 1, NOW()),
(2, '000000', '管理员', 'ADMIN', '系统管理员，拥有大部分权限', b'1', b'0', 'THIS_LEVEL_CHILDREN', 1, NOW(), 1, NOW()),
(3, '000000', '普通用户', 'USER', '普通用户，基础权限', b'1', b'0', 'THIS_LEVEL', 1, NOW(), 1, NOW()),
(4, '000000', '访客', 'GUEST', '访客用户，只读权限', b'1', b'0', 'THIS_LEVEL', 1, NOW(), 1, NOW());

-- ----------------------------
-- 初始化 c_auth_role_authority 数据
-- ----------------------------
-- 超级管理员拥有所有权限
INSERT INTO `c_auth_role_authority` (`id`, `tenant_code`, `authority_id`, `authority_type`, `role_id`, `create_time`, `create_user`) VALUES
-- 系统管理菜单权限
(100001, '000000', 100, 'MENU', 1, NOW(), 1),
(100002, '000000', 101, 'MENU', 1, NOW(), 1),
(100003, '000000', 102, 'MENU', 1, NOW(), 1),
(100004, '000000', 103, 'MENU', 1, NOW(), 1),
(100005, '000000', 104, 'MENU', 1, NOW(), 1),
(100006, '000000', 105, 'MENU', 1, NOW(), 1),
-- 用户中心菜单权限
(100007, '000000', 200, 'MENU', 1, NOW(), 1),
(100008, '000000', 201, 'MENU', 1, NOW(), 1),
(100009, '000000', 202, 'MENU', 1, NOW(), 1),
(100010, '000000', 203, 'MENU', 1, NOW(), 1),
-- 权限管理菜单权限
(100011, '000000', 300, 'MENU', 1, NOW(), 1),
(100012, '000000', 301, 'MENU', 1, NOW(), 1),
(100013, '000000', 302, 'MENU', 1, NOW(), 1),
-- 组织架构菜单权限
(100014, '000000', 400, 'MENU', 1, NOW(), 1);

-- 超级管理员资源权限（所有资源）
INSERT INTO `c_auth_role_authority` (`id`, `tenant_code`, `authority_id`, `authority_type`, `role_id`, `create_time`, `create_user`) VALUES
-- 用户管理资源
(100101, '000000', 1001, 'RESOURCE', 1, NOW(), 1),
(100102, '000000', 1002, 'RESOURCE', 1, NOW(), 1),
(100103, '000000', 1003, 'RESOURCE', 1, NOW(), 1),
(100104, '000000', 1004, 'RESOURCE', 1, NOW(), 1),
(100105, '000000', 1005, 'RESOURCE', 1, NOW(), 1),
(100106, '000000', 1006, 'RESOURCE', 1, NOW(), 1),
-- 角色管理资源
(100111, '000000', 1011, 'RESOURCE', 1, NOW(), 1),
(100112, '000000', 1012, 'RESOURCE', 1, NOW(), 1),
(100113, '000000', 1013, 'RESOURCE', 1, NOW(), 1),
(100114, '000000', 1014, 'RESOURCE', 1, NOW(), 1),
(100115, '000000', 1015, 'RESOURCE', 1, NOW(), 1),
-- 菜单管理资源
(100121, '000000', 1021, 'RESOURCE', 1, NOW(), 1),
(100122, '000000', 1022, 'RESOURCE', 1, NOW(), 1),
(100123, '000000', 1023, 'RESOURCE', 1, NOW(), 1),
(100124, '000000', 1024, 'RESOURCE', 1, NOW(), 1),
-- 资源管理资源
(100131, '000000', 1031, 'RESOURCE', 1, NOW(), 1),
(100132, '000000', 1032, 'RESOURCE', 1, NOW(), 1),
(100133, '000000', 1033, 'RESOURCE', 1, NOW(), 1),
(100134, '000000', 1034, 'RESOURCE', 1, NOW(), 1),
-- 应用管理资源
(100141, '000000', 1041, 'RESOURCE', 1, NOW(), 1),
(100142, '000000', 1042, 'RESOURCE', 1, NOW(), 1),
(100143, '000000', 1043, 'RESOURCE', 1, NOW(), 1),
(100144, '000000', 1044, 'RESOURCE', 1, NOW(), 1),
-- 医生管理资源
(100201, '000000', 2001, 'RESOURCE', 1, NOW(), 1),
(100202, '000000', 2002, 'RESOURCE', 1, NOW(), 1),
(100203, '000000', 2003, 'RESOURCE', 1, NOW(), 1),
(100204, '000000', 2004, 'RESOURCE', 1, NOW(), 1),
-- 患者管理资源
(100211, '000000', 2011, 'RESOURCE', 1, NOW(), 1),
(100212, '000000', 2012, 'RESOURCE', 1, NOW(), 1),
(100213, '000000', 2013, 'RESOURCE', 1, NOW(), 1),
(100214, '000000', 2014, 'RESOURCE', 1, NOW(), 1),
-- 小组管理资源
(100221, '000000', 2021, 'RESOURCE', 1, NOW(), 1),
(100222, '000000', 2022, 'RESOURCE', 1, NOW(), 1),
(100223, '000000', 2023, 'RESOURCE', 1, NOW(), 1),
(100224, '000000', 2024, 'RESOURCE', 1, NOW(), 1);

-- 管理员权限（只读权限）
INSERT INTO `c_auth_role_authority` (`id`, `tenant_code`, `authority_id`, `authority_type`, `role_id`, `create_time`, `create_user`) VALUES
-- 系统管理菜单权限
(200001, '000000', 100, 'MENU', 2, NOW(), 1),
(200002, '000000', 101, 'MENU', 2, NOW(), 1),
(200003, '000000', 102, 'MENU', 2, NOW(), 1),
(200004, '000000', 103, 'MENU', 2, NOW(), 1),
(200005, '000000', 104, 'MENU', 2, NOW(), 1),
(200006, '000000', 105, 'MENU', 2, NOW(), 1),
-- 用户中心菜单权限
(200007, '000000', 200, 'MENU', 2, NOW(), 1),
(200008, '000000', 201, 'MENU', 2, NOW(), 1),
(200009, '000000', 202, 'MENU', 2, NOW(), 1),
(200010, '000000', 203, 'MENU', 2, NOW(), 1),
-- 权限管理菜单权限
(200011, '000000', 300, 'MENU', 2, NOW(), 1),
(200012, '000000', 301, 'MENU', 2, NOW(), 1),
(200013, '000000', 302, 'MENU', 2, NOW(), 1),
-- 组织架构菜单权限
(200014, '000000', 400, 'MENU', 2, NOW(), 1);

-- 管理员资源权限（只读）
INSERT INTO `c_auth_role_authority` (`id`, `tenant_code`, `authority_id`, `authority_type`, `role_id`, `create_time`, `create_user`) VALUES
(200101, '000000', 1004, 'RESOURCE', 2, NOW(), 1),
(200111, '000000', 1014, 'RESOURCE', 2, NOW(), 1),
(200121, '000000', 1024, 'RESOURCE', 2, NOW(), 1),
(200131, '000000', 1034, 'RESOURCE', 2, NOW(), 1),
(200141, '000000', 1044, 'RESOURCE', 2, NOW(), 1),
(200201, '000000', 2004, 'RESOURCE', 2, NOW(), 1),
(200211, '000000', 2014, 'RESOURCE', 2, NOW(), 1),
(200221, '000000', 2024, 'RESOURCE', 2, NOW(), 1);

-- 普通用户权限
INSERT INTO `c_auth_role_authority` (`id`, `tenant_code`, `authority_id`, `authority_type`, `role_id`, `create_time`, `create_user`) VALUES
(300001, '000000', 200, 'MENU', 3, NOW(), 1),
(300002, '000000', 201, 'MENU', 3, NOW(), 1),
(300003, '000000', 202, 'MENU', 3, NOW(), 1),
(300004, '000000', 203, 'MENU', 3, NOW(), 1),
(300005, '000000', 2004, 'RESOURCE', 3, NOW(), 1),
(300006, '000000', 2014, 'RESOURCE', 3, NOW(), 1),
(300007, '000000', 2024, 'RESOURCE', 3, NOW(), 1);

-- ----------------------------
-- 初始化 c_auth_user 数据
-- 注意：密码使用 MD5 加密
-- admin: e10adc3949ba59abbe56e057f20f883e (123456)
-- test: e10adc3949ba59abbe56e057f20f883e (123456)
-- ----------------------------
INSERT INTO `c_auth_user` (`id`, `tenant_code`, `account`, `name`, `org_id`, `station_id`, `readonly`, `email`, `mobile`, `sex`, `status`, `avatar`, `nation`, `education`, `position_status`, `work_describe`, `password_error_last_time`, `password_error_num`, `password_expire_time`, `password`, `last_login_time`, `create_user`, `create_time`, `update_user`, `update_time`, `latest_access_time`) VALUES
(1, '000000', 'admin', '超级管理员', NULL, NULL, b'1', 'admin@kalin.com', '13800138000', 'M', b'1', '', '汉族', '本科', '在职', '系统超级管理员', NULL, 0, NULL, 'e10adc3949ba59abbe56e057f20f883e', NOW(), 1, NOW(), 1, NOW(), NOW()),
(2, '000000', 'test', '测试用户', NULL, NULL, b'0', 'test@kalin.com', '13800138001', 'F', b'1', '', '汉族', '本科', '在职', '测试用户', NULL, 0, NULL, 'e10adc3949ba59abbe56e057f20f883e', NULL, 1, NOW(), 1, NOW(), NULL);

-- ----------------------------
-- 初始化 c_auth_user_role 数据
-- ----------------------------
INSERT INTO `c_auth_user_role` (`id`, `tenant_code`, `role_id`, `user_id`, `create_user`, `create_time`) VALUES
(1, '000000', 1, 1, 1, NOW()),
(2, '000000', 3, 2, 1, NOW());

-- ----------------------------
-- 初始化完成提示
-- ----------------------------
SELECT '========================================' AS '';
SELECT '卡柠 SaaS 权限系统初始化数据完成！' AS '';
SELECT '========================================' AS '';
SELECT '默认账号信息：' AS '';
SELECT '账号: admin, 密码: 123456 (超级管理员)' AS '';
SELECT '账号: test, 密码: 123456 (测试用户)' AS '';
SELECT '========================================' AS '';