# 患者私域管理平台 - 后端服务

患者私域管理平台的后端服务，基于Spring Boot和Spring Cloud构建，提供完整的医疗健康管理API接口。

## 项目简介

本后端项目采用Spring Boot微服务架构，支持多租户SaaS模式，现已简化为单机部署版本，便于快速开发和测试。

## 技术栈

- **框架**: Spring Boot 2.2.9.RELEASE
- **微服务**: Spring Cloud (Hoxton.SR12)
- **数据库**: MySQL 8.0+
- **缓存**: Redis 6.0+
- **ORM**: MyBatis-Plus
- **安全**: OAuth2 + JWT
- **文档**: Swagger
- **监控**: Spring Boot Actuator

## 项目结构

```
backend/
├── caring-ai/              # AI服务
├── caring-authority/        # 权限认证
├── caring-cms/             # 内容管理
├── caring-file/            # 文件服务
├── caring-gateway/         # 网关服务
├── caring-msgs/            # 消息服务
├── caring-nursing/         # 护理服务
├── caring-open/            # 开放平台
├── caring-public/          # 公共组件
├── caring-support/         # 支撑服务
├── caring-tenant/          # 租户管理
├── caring-ucenter/         # 用户中心
├── caring-wx/              # 微信集成
├── config-example/         # 配置示例
├── docs/                   # 文档
├── scripts/                # 脚本
├── pom.xml                 # Maven配置
└── README.md               # 项目说明
```

## 核心模块

### 1. 患者管理 (caring-ucenter)
- 患者信息管理
- 医生信息管理
- 组织架构管理
- 随访管理

### 2. 权限认证 (caring-authority)
- OAuth2认证
- JWT Token管理
- RBAC权限控制
- 菜单管理

### 3. AI服务 (caring-ai)
- 音频解析
- 语音识别
- 智能问答
- 文章生成

### 4. 内容管理 (caring-cms)
- 文章管理
- Banner管理
- 频道管理
- 关键词管理

### 5. 微信集成 (caring-wx)
- 公众号接入
- 小程序管理
- 微信支付
- 模板消息

## 快速开始

### 环境要求

- Java 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 安装依赖

```bash
# 在项目根目录执行
cd caring-patient-platform
npm run backend:install
```

### 启动开发环境

```bash
# 使用Maven启动
cd packages/backend
mvn spring-boot:run

# 或使用npm脚本
npm run backend:run
```

### 构建项目

```bash
# 编译打包
npm run backend:build

# 跳过测试编译
npm run backend:install
```

## 单机版部署

详细的单机版部署指南请参考 [STANDALONE.md](./STANDALONE.md)。

### Docker快速启动

```bash
cd packages/backend/config-example/standalone/docker
docker-compose up -d
```

### 配置环境变量

创建 `.env` 文件：

```bash
MYSQL_ROOT_PASSWORD=your-password
MYSQL_PASSWORD=your-password
REDIS_PASSWORD=your-password
JWT_SECRET=your-secret-key
```

## API接口

### 基础信息

- **基础URL**: `http://localhost:8080/caring-standalone/api`
- **认证方式**: Bearer Token
- **响应格式**: JSON

### 主要接口

#### 患者管理
- `GET /api/patient` - 获取患者列表
- `POST /api/patient` - 创建患者
- `PUT /api/patient/{id}` - 更新患者
- `DELETE /api/patient/{id}` - 删除患者

#### 医生管理
- `GET /api/doctor` - 获取医生列表
- `POST /api/doctor` - 创建医生
- `PUT /api/doctor/{id}` - 更新医生

#### 随访管理
- `GET /api/follow-up` - 获取随访列表
- `POST /api/follow-up` - 创建随访计划
- `PUT /api/follow-up/{id}` - 更新随访计划

#### AI服务
- `POST /api/ai/audio/analysis` - 音频解析
- `POST /api/ai/chat` - 智能问答
- `POST /api/ai/article/generate` - 文章生成

### API文档

启动应用后访问 Swagger 文档：

```
http://localhost:8080/caring-standalone/swagger-ui.html
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
    password: your-password
```

### 安全配置

```yaml
security:
  jwt:
    secret: your-secret-key
    expiration: 86400
```

## 开发指南

### 添加新API

1. 在对应的Controller中添加接口：

```java
@RestController
@RequestMapping("/api/example")
public class ExampleController {
    
    @GetMapping
    public Result<List<Example>> list() {
        // 实现逻辑
    }
    
    @PostMapping
    public Result<Example> create(@RequestBody Example example) {
        // 实现逻辑
    }
}
```

2. 在Service中实现业务逻辑：

```java
@Service
public class ExampleService {
    
    @Autowired
    private ExampleMapper exampleMapper;
    
    public List<Example> list() {
        return exampleMapper.selectList(null);
    }
}
```

### 数据库操作

使用MyBatis-Plus：

```java
// 查询
List<Example> list = exampleMapper.selectList(null);
Example example = exampleMapper.selectById(id);

// 新增
exampleMapper.insert(example);

// 更新
exampleMapper.updateById(example);

// 删除
exampleMapper.deleteById(id);
```

### 权限控制

使用注解：

```java
@PreAuth("hasAuthority('example:view')")
@GetMapping
public Result<List<Example>> list() {
    // 实现逻辑
}
```

## 测试

### 运行测试

```bash
# 运行所有测试
npm run backend:test

# 运行指定模块测试
cd caring-ucenter
mvn test
```

### 编写测试

```java
@SpringBootTest
public class ExampleServiceTest {
    
    @Autowired
    private ExampleService exampleService;
    
    @Test
    public void testList() {
        List<Example> list = exampleService.list();
        assertNotNull(list);
    }
}
```

## 监控和日志

### 应用日志

```bash
# 查看日志
tail -f logs/caring-standalone.log
```

### Druid监控

访问：http://localhost:8080/caring-standalone/druid

默认账号：
- 用户名：admin
- 密码：admin123

### 健康检查

```bash
curl http://localhost:8080/caring-standalone/actuator/health
```

## 常见问题

### 1. 数据库连接失败

检查MySQL配置和网络连接。

### 2. Redis连接失败

检查Redis配置和密码。

### 3. 端口冲突

修改 `application.yml` 中的端口号。

### 4. 依赖下载失败

配置Maven镜像源。

## 前后端联调

### 1. 启动后端服务

```bash
npm run backend:run
```

### 2. 配置前端API地址

修改前端项目的API配置：

```javascript
const apiUrl = 'http://localhost:8080/caring-standalone/api'
```

### 3. 启动前端服务

```bash
npm run dev
```

## 技术支持

- 文档：查看 [docs/](./docs/) 目录
- 配置：参考 [config-example/](./config-example/) 目录
- 问题：提交 Issue

## 贡献指南

欢迎贡献代码，请遵循以下步骤：

1. Fork 本项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证。

---

**注意**: 本版本为单机部署版本，适用于开发和测试环境。生产环境请使用完整的多租户版本。