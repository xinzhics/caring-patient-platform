-- 单机版迁移脚本：移除多租户相关字段
-- 执行前请备份数据库

-- 1. 移除用户表中的租户字段
ALTER TABLE `d_user` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_doctor` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_patient` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_nursing_staff` DROP COLUMN IF EXISTS `tenant_code`;

-- 2. 移除业务表中的租户字段
ALTER TABLE `d_consultation` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_consultation_group` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_message` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_file_info` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_notification` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_follow_up` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_keyword_setting` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_patient_recommend_setting` DROP COLUMN IF EXISTS `tenant_code`;

-- 3. 移除微信相关表中的租户字段
ALTER TABLE `d_config` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_config_additional` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_pre_auth_code` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_custom_menu` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_reg_guide` DROP COLUMN IF EXISTS `tenant_code`;

-- 4. 移除AI相关表中的租户字段
ALTER TABLE `d_article` DROP COLUMN IF EXISTS `tenant_code`;
ALTER TABLE `d_writing_task` DROP COLUMN IF EXISTS `tenant_code`;

-- 5. 移除索引（如果存在）
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_user`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_doctor`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_patient`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_nursing_staff`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_consultation`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_consultation_group`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_message`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_file_info`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_notification`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_follow_up`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_keyword_setting`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_patient_recommend_setting`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_config`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_config_additional`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_pre_auth_code`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_custom_menu`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_reg_guide`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_article`;
DROP INDEX IF EXISTS `idx_tenant_code` ON `d_writing_task`;

-- 6. 删除租户相关表（如果不需要保留历史数据）
-- DROP TABLE IF EXISTS `d_tenant`;
-- DROP TABLE IF EXISTS `d_tenant_user`;
-- DROP TABLE IF EXISTS `d_tenant_config`;

-- 7. 创建默认管理员用户（如果不存在）
INSERT IGNORE INTO `d_user` (`id`, `username`, `password`, `email`, `phone`, `status`, `create_time`, `update_time`) 
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi0pM2EGpEJlywgHhQhjAqHS', 'admin@caring.com', '13800138000', 1, NOW(), NOW());

-- 8. 更新系统配置
UPDATE `d_config` SET `value` = 'Caring单机版' WHERE `key` = 'system.name' LIMIT 1;
UPDATE `d_config` SET `value` = '1.0.0' WHERE `key` = 'system.version' LIMIT 1;

-- 9. 清理Redis缓存
-- FLUSHDB;

-- 10. 优化表结构
OPTIMIZE TABLE `d_user`, `d_doctor`, `d_patient`, `d_nursing_staff`, `d_consultation`, `d_consultation_group`, `d_message`, `d_file_info`, `d_notification`, `d_follow_up`, `d_keyword_setting`, `d_patient_recommend_setting`, `d_config`, `d_config_additional`, `d_pre_auth_code`, `d_custom_menu`, `d_reg_guide`, `d_article`, `d_writing_task`;

-- 迁移完成提示
SELECT '单机版迁移脚本执行完成！' AS message;