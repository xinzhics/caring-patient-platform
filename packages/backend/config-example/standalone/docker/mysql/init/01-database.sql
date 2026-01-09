-- 初始化数据库
CREATE DATABASE IF NOT EXISTS caring_standalone CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE caring_standalone;

-- 创建用户并授权
-- 注意：密码应该通过环境变量传入，这里只是示例
CREATE USER IF NOT EXISTS 'caring'@'%' IDENTIFIED BY 'change-this-password-in-production';
GRANT ALL PRIVILEGES ON caring_standalone.* TO 'caring'@'%';
FLUSH PRIVILEGES;