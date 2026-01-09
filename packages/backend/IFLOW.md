# Caring SAAS Cloud 项目上下文

## 项目概述

Caring SAAS Cloud 是一个基于Spring Cloud微服务架构的SaaS平台，提供多租户、AI音频解析、内容管理等功能。项目采用Java 1.8开发，使用Maven进行构建管理，集成Nacos作为配置中心和注册中心。

## 技术栈

- **后端框架**: Spring Boot 2.2.9, Spring Cloud
- **数据库**: MySQL, Redis
- **配置中心**: Nacos
- **消息队列**: Kafka
- **文件存储**: 支持本地存储、七牛云、阿里云OSS
- **AI集成**: LangChain4J (0.28.0), MiniMax语音API
- **安全**: OAuth2, JWT
- **分布式事务**: Seata (1.2.0)
- **容器化**: Docker, Kubernetes

## 项目结构

项目采用多模块Maven结构，主要模块包括：

### 核心模块
- `caring-public/caring-common`: 公共组件和工具类
- `caring-gateway`: 网关服务 (支持Zuul和Spring Cloud Gateway)
- `caring-authority`: 权限认证服务
- `caring-tenant`: 多租户服务
- `caring-ucenter`: 用户中心服务

### 业务模块
- `caring-ai`: AI音频解析服务，提供语音转录、总结和思维导图生成
- `caring-cms`: 内容管理系统
- `caring-msgs`: 消息服务 (短信、邮件等)
- `caring-file`: 文件服务
- `caring-wx`: 微信集成服务
- `caring-nursing`: 护理相关服务
- `caring-open`: 开放平台服务

### 支持模块
- `caring-support`: 支撑服务 (监控等)

## 构建和运行

### 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 3.0+
- Node.js (前端，如需)

### 构建命令
```bash
# 编译整个项目
mvn clean compile

# 打包
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests

# 安装到本地仓库
mvn clean install
```

### 环境配置
项目支持多环境配置：
- `dev`: 开发环境 (默认)
- `local`: 本地开发环境
- `prod`: 生产环境

配置文件优先级：`caring-xx-server-dev.yml(nacos)` > `caring-xx-server.yml(nacos)` > `common.yml(nacos)` > `bootstrap-dev.yml(resources)` > `bootstrap.yml(resources)`

### 启动服务
1. 启动Nacos配置中心
2. 启动MySQL和Redis
3. 配置Nacos中的数据库连接信息
4. 按顺序启动核心服务：gateway -> authority -> tenant -> ucenter
5. 启动业务服务

## 开发规范

### 代码规范
- 使用Lombok减少样板代码
- 使用MyBatis-Plus进行数据库操作
- 使用Hutool工具类库
- 使用FastJSON处理JSON数据
- 所有服务都需要继承公共基础类

### API规范
- 使用RESTful风格API
- 统一响应格式：`{code, data, msg, isSuccess}`
- 使用DTO进行数据传输
- 使用`@PreAuth`注解进行权限控制
- 使用`@Validated`进行参数验证

### 数据库规范
- 表名使用下划线命名法
- 字段名使用下划线命名法
- 必须包含`create_time`, `update_time`, `create_user`, `update_user`字段
- 使用逻辑删除，不物理删除数据

## AI模块功能

### 音频解析流程
1. **上传音频**: 创建音频解析任务
2. **语音转录**: 调用ASR服务将音频转为文本
3. **AI解析**: 使用LLM进行内容总结和思维导图生成
4. **智能问答**: 基于转录内容进行问答交互
5. **作品保存**: 保存到个人作品库

### MiniMax语音API集成
- 语音克隆功能
- 文本转语音(TTS)
- 支持多种音频格式
- 限流控制(QPS限制)

## 安全机制

### 认证授权
- 基于OAuth2的认证机制
- JWT令牌管理
- 支持多地登录控制
- 细粒度权限控制

### 数据安全
- 敏感信息加密存储
- API接口限流
- SQL注入防护
- XSS攻击防护

### 安全检查
项目包含安全检查脚本(`scripts/security-check.sh`)，用于：
- 检测硬编码的API密钥
- 检测证书文件
- 检测默认密码
- 检测生产环境配置

## 部署指南

### Docker部署
每个服务模块都包含Dockerfile，支持容器化部署。
```bash
# 构建镜像
docker build -t caring/{service-name}:latest .

# 运行容器
docker run -d caring/{service-name}:latest
```

### Kubernetes部署
项目提供K8s部署配置文件在`docs/k8s/`目录。

### 配置管理
- 使用Nacos作为配置中心
- 支持配置动态刷新
- 环境隔离配置
- 敏感配置加密

## 常见问题解决

### 数据库连接问题
1. 检查`src/main/filters/config-dev.properties`中的nacos命名空间配置
2. 确认Nacos中的mysql.yml配置是否正确
3. 执行`mvn clean install`编译项目
4. 检查配置文件格式是否为YAML

### 启动报错
- 确保项目路径不包含中文或空格
- 检查JDK版本是否为1.8
- 确认Nacos配置是否正确导入
- 检查数据库表名大小写设置

## 项目特色功能

### 多租户支持
- 基于租户ID的数据隔离
- 支持租户级别配置
- 租户数据权限控制

### AI能力集成
- 语音识别和转录
- 智能内容总结
- 思维导图生成
- 基于内容的智能问答

### 文件管理
- 支持多种存储后端
- 文件上传和下载
- 文件预览功能
- 大文件分片上传

## 开发工具推荐

- IDE: IntelliJ IDEA
- 数据库工具: Navicat, DBeaver
- API测试: Postman, Apifox
- 版本控制: Git
- 容器: Docker, Docker Compose

## 联系方式

如有问题，请联系开发团队或查看项目文档。