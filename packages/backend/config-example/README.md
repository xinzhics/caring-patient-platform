# Caring SaaS Cloud 开源配置指南

## 概述

本文档提供了 Caring SaaS Cloud 项目开源版本所需的配置说明。为了确保项目安全性和可配置性，所有敏感信息和环境相关配置都已从代码中移除，需要通过配置文件或环境变量进行设置。

## 目录结构

```
config-example/
├── README.md                           # 本文档
├── .env.example                        # 环境变量配置示例
├── config-prod.properties.example      # 生产环境配置示例
├── config-dev.properties.example       # 开发环境配置示例
└── nacos/
    ├── conf/
    │   └── application.properties.example  # Nacos服务配置示例
    └── clientConfig/
        ├── mysql.yml.example               # MySQL数据库配置示例
        ├── redis.yml.example               # Redis缓存配置示例
        └── third-party.yml.example         # 第三方服务配置示例
```

## 配置步骤

### 1. 环境变量配置

复制 `.env.example` 为 `.env`，并填入实际的配置值：

```bash
cp config-example/.env.example config-example/.env
```

**重要提示**：
- `.env` 文件包含敏感信息，请勿提交到版本控制系统
- 确保 `.env` 已添加到 `.gitignore` 文件中

### 2. 环境配置文件

根据部署环境选择相应的配置文件：

#### 生产环境
```bash
cp config-example/config-prod.properties.example src/main/filters/config-prod.properties
```

#### 开发环境
```bash
cp config-example/config-dev.properties.example src/main/filters/config-dev.properties
```

### 3. Nacos配置中心配置

#### Nacos服务配置
```bash
cp config-example/nacos/conf/application.properties.example third-party/nacos/conf/application.properties
```

#### 客户端配置
```bash
# 数据库配置
cp config-example/nacos/clientConfig/mysql.yml.example third-party/nacos/clientConfig/mysql.yml

# Redis配置
cp config-example/nacos/clientConfig/redis.yml.example third-party/nacos/clientConfig/redis.yml

# 第三方服务配置
cp config-example/nacos/clientConfig/third-party.yml.example third-party/nacos/clientConfig/third-party.yml
```

## 配置项说明

### 数据库配置

- **MySQL**: 主数据库，存储业务数据
- **Nacos数据库**: 存储Nacos配置中心数据

必要配置项：
- 数据库IP地址和端口
- 数据库名称
- 用户名和密码

### 缓存配置

- **Redis**: 缓存服务，用于提高系统性能

必要配置项：
- Redis IP地址和端口
- 访问密码（如需要）

### 第三方服务配置

#### 阿里云OSS
- 文件存储服务
- 需要配置AccessKey ID和Secret

#### 火山引擎API
- AI服务接口
- 需要配置访问密钥

#### MiniMax API
- 语音合成服务
- 需要配置API密钥

#### 豆包TTS
- 文字转语音服务
- 需要配置访问令牌

#### 短信服务
- 短信发送功能
- 支持多种短信服务商

#### 微信服务
- 微信公众号和小程序集成
- 包括支付功能

### 安全配置

- **Nacos认证**: 配置中心的访问控制
- **Druid监控**: 数据库连接池监控

## 部署建议

### 开发环境

1. 使用本地数据库和Redis
2. 配置开发环境专用的Nacos命名空间
3. 启用调试和监控功能

### 生产环境

1. 使用高可用的数据库集群
2. 配置Redis集群或哨兵模式
3. 启用Nacos认证和访问控制
4. 配置日志收集和监控
5. 定期备份配置和数据

## 安全注意事项

1. **敏感信息保护**：
   - 所有密钥、密码等敏感信息必须通过环境变量或加密配置文件提供
   - 不要在代码中硬编码任何敏感信息
   - 定期轮换密钥和密码

2. **访问控制**：
   - 生产环境必须启用Nacos认证
   - 限制数据库和Redis的访问权限
   - 使用防火墙限制不必要的端口访问

3. **网络安全**：
   - 使用HTTPS加密通信
   - 配置SSL/TLS证书
   - 定期更新系统和依赖库

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查数据库服务是否启动
   - 验证连接参数和凭据
   - 确认网络连通性

2. **Redis连接失败**
   - 检查Redis服务状态
   - 验证密码配置
   - 确认端口访问权限

3. **Nacos配置加载失败**
   - 检查Nacos服务状态
   - 验证命名空间和分组配置
   - 确认认证凭据

### 日志查看

- 应用日志：`${logging.file.path}/${spring.application.name}/`
- Nacos日志：`nacos/logs/`
- 数据库日志：根据具体数据库配置

## 联系支持

如需技术支持或有疑问，请通过以下方式联系：

- 提交Issue：[项目GitHub地址]
- 技术文档：[项目文档地址]
- 社区讨论：[社区论坛地址]

---

**注意**：本开源版本不包含任何实际的敏感信息，所有配置都需要用户根据实际情况进行设置。请务必在生产环境中正确配置所有必要参数。