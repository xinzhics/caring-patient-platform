# 单机版部署指南

本文档介绍如何部署慢病管理平台的单机版本。

## 环境要求

- Java 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16.0+ (用于前端)

## 快速开始

### 1. 使用Docker Compose快速启动

```bash
# 进入配置目录
cd packages/backend/config-example/standalone/docker

# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f caring-app
```

### 2. 配置环境变量

创建 `.env` 文件：

```bash
# MySQL配置
MYSQL_ROOT_PASSWORD=your-root-password
MYSQL_PASSWORD=your-password

# Redis配置
REDIS_PASSWORD=your-redis-password

# 应用配置
JWT_SECRET=your-jwt-secret-key
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your-admin-password
ADMIN_EMAIL=admin@example.com
```

### 3. 初始化数据库

```bash
# 导入数据库脚本
docker exec -i caring-mysql mysql -uroot -p${MYSQL_ROOT_PASSWORD} caring_standalone < ../../docs/sql/caring_base_0000.sql

# 执行单机版迁移脚本
docker exec -i caring-mysql mysql -uroot -p${MYSQL_ROOT_PASSWORD} caring_standalone < migration/remove_tenant_fields.sql
```

### 4. 访问应用

- 后端API: http://localhost:8080/caring-standalone
- Nginx代理: http://localhost
- Druid监控: http://localhost:8080/caring-standalone/druid

## 手动部署

### 1. 编译后端项目

```bash
cd packages/backend
mvn clean package -DskipTests
```

### 2. 配置数据库

创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS caring_standalone CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'caring'@'%' IDENTIFIED BY 'your-password';
GRANT ALL PRIVILEGES ON caring_standalone.* TO 'caring'@'%';
FLUSH PRIVILEGES;
```

导入数据：

```bash
mysql -uroot -p caring_standalone < docs/sql/caring_base_0000.sql
mysql -uroot -p caring_standalone < config-example/standalone/migration/remove_tenant_fields.sql
```

### 3. 配置应用

编辑 `config-example/standalone/application.yml`，设置数据库连接信息：

```yaml
caring:
  mysql:
    ip: localhost
    port: 3306
    database: caring_standalone
    username: caring
    password: your-password
```

### 4. 启动应用

```bash
cd packages/backend
java -jar caring-standalone/target/caring-standalone-1.0.0.jar --spring.profiles.active=standalone
```

## 配置说明

### 数据库配置

```yaml
caring:
  mysql:
    ip: localhost
    port: 3306
    database: caring_standalone
    username: caring
    password: your-password
```

### Redis配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your-redis-password
```

### 安全配置

```yaml
security:
  jwt:
    secret: your-secret-key
    expiration: 86400
```

### 管理员账户

```yaml
business:
  admin:
    username: admin
    password: admin123
    email: admin@example.com
```

## 前后端联调

### 1. 启动后端服务

按照上述步骤启动后端服务。

### 2. 配置前端API地址

修改前端项目的API配置：

```javascript
// packages/patient-app/src/api/baseUrl.js
const apiUrl = 'http://localhost:8080/caring-standalone/api'
```

### 3. 启动前端服务

```bash
cd caring-patient-platform
npm run dev
```

## 常见问题

### 1. 数据库连接失败

检查MySQL服务是否启动，用户名密码是否正确。

### 2. Redis连接失败

检查Redis服务是否启动，密码是否正确。

### 3. 端口冲突

修改 `application.yml` 中的端口号。

### 4. 权限问题

确保数据库用户有足够的权限。

## 性能优化

### 1. 数据库优化

- 调整连接池大小
- 优化SQL查询
- 添加索引

### 2. Redis优化

- 调整内存配置
- 使用持久化

### 3. 应用优化

- 调整JVM参数
- 启用缓存
- 优化日志级别

## 监控和日志

### 应用日志

```bash
# 查看应用日志
tail -f logs/caring-standalone.log
```

### Druid监控

访问：http://localhost:8080/caring-standalone/druid

默认账号：
- 用户名：admin
- 密码：admin123

### 性能监控

使用Spring Boot Actuator：

```bash
curl http://localhost:8080/caring-standalone/actuator/health
```

## 备份和恢复

### 数据库备份

```bash
# 备份数据库
docker exec caring-mysql mysqldump -uroot -p${MYSQL_ROOT_PASSWORD} caring_standalone > backup.sql

# 恢复数据库
docker exec -i caring-mysql mysql -uroot -p${MYSQL_ROOT_PASSWORD} caring_standalone < backup.sql
```

### 文件备份

```bash
# 备份上传文件
tar -czf uploads-backup.tar.gz uploads/
```

## 安全建议

1. **修改默认密码**
   - 修改数据库密码
   - 修改Redis密码
   - 修改管理员密码
   - 修改JWT密钥

2. **启用HTTPS**
   - 配置SSL证书
   - 使用HTTPS协议

3. **防火墙配置**
   - 限制数据库访问
   - 限制Redis访问
   - 配置端口白名单

4. **定期更新**
   - 更新依赖版本
   - 修复安全漏洞
   - 关注安全公告

## 故障排查

### 查看日志

```bash
# 应用日志
tail -f logs/caring-standalone.log

# Docker日志
docker-compose logs -f caring-app
```

### 检查服务状态

```bash
# 检查MySQL
docker exec caring-mysql mysqladmin ping

# 检查Redis
docker exec caring-redis redis-cli ping

# 检查应用
curl http://localhost:8080/caring-standalone/actuator/health
```

### 重启服务

```bash
# 重启应用
docker-compose restart caring-app

# 重启所有服务
docker-compose restart
```

## 技术支持

如遇到问题，请：

1. 查看日志文件
2. 检查配置文件
3. 参考文档
4. 提交Issue

---

**注意**: 本文档仅适用于单机版部署，生产环境请使用完整的多租户版本。