# 慢病管理平台 - 后端服务

慢病管理平台的后端服务，基于Spring Boot和Spring Cloud构建，提供完整的医疗健康管理API接口。

## 项目简介

本后端项目采用Spring Boot微服务架构，支持多租户SaaS模式，提供完整的医疗健康管理API接口。

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

- **Java 1.8** (⚠️ 必须使用 JDK 1.8 进行编译，其他版本可能导致编译失败)
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Docker & Docker Compose (用于启动依赖服务)

### 启动流程

#### 第一步：启动依赖服务（Docker Compose）

使用 Docker Compose 快速启动所有依赖环境（MySQL、Nacos、Redis、XXL-Job、Nginx）：

```bash
cd packages/backend
docker-compose up -d
```

此命令会：
- 启动 MySQL 8.0 数据库服务
- 启动 Nacos 配置中心
- 启动 Redis 缓存服务
- 启动 XXL-Job 分布式调度中心
- 启动 Nginx 反向代理服务
- 自动执行数据库初始化脚本（`config/sql/nacos-init.sql`）

**注意**：
- 首次启动需要等待 1-2 分钟，等待所有服务完全启动
- 可以使用 `docker-compose ps` 查看服务状态
- 可以使用 `docker-compose logs -f [service-name]` 查看服务日志

依赖环境启动后，可以访问以下服务：

- **Nacos 控制台**: http://localhost:8848/nacos (用户名/密码: nacos/nacos)
- **MySQL**: localhost:3306 (用户名: root, 密码: change-this-password)
- **Redis**: localhost:6379 (密码: change-this-password)
- **XXL-Job**: http://localhost:8080/xxl-job-admin (用户名: admin, 密码: 123456)
- **Nginx**: http://localhost:80

#### 第二步：验证 Nacos 配置初始化

数据库初始化脚本会自动执行以下操作：
1. 创建 `nacos_config` 数据库和 Nacos 相关表
2. 创建 `dev` 命名空间（自动生成 UUID）
3. 导入基础配置文件到 Nacos 配置中心

**验证配置是否导入成功**：
1. 访问 Nacos 控制台：http://localhost:8848/nacos
2. 登录（用户名/密码: nacos/nacos）
3. 进入"配置管理" -> "配置列表"
4. 在命名空间下拉框中找到并选择 `dev` 命名空间
5. 确认分组为 `sass-cloud`
6. 应该能看到以下配置文件：
   - `common.yml` - 公共配置
   - `mysql.yml` - MySQL 数据库配置
   - `redis.yml` - Redis 配置
   - `rabbitmq.yml` - RabbitMQ 配置
   - `third-party.yml` - 第三方服务配置
   - `caring-authority-server.yml` - 权限服务配置
   - `caring-gateway-server.yml` - 网关服务配置
   - `caring-file-server.yml` - 文件服务配置
   - 等其他服务配置

#### 第三步：获取 Nacos 命名空间 UUID

⚠️ **重要**：启动后端服务前，必须确保 `config-dev.properties` 中的 `nacos.namespace` 与 Nacos 中的 `dev` 命名空间 UUID 一致。

**获取命名空间 UUID 的方法**：

**方法一：通过 Nacos 控制台查看**
1. 访问 Nacos 控制台：http://localhost:8848/nacos
2. 登录后，进入"命名空间"菜单
3. 找到名为 `dev` 的命名空间
4. 复制其命名空间 ID（UUID 格式，如：`85d56e61-f676-11f0-a8b0-328ff568776d`）

**方法二：通过数据库查询**
```bash
# 连接到 MySQL 容器
docker exec -it caring-mysql mysql -uroot -pchange-this-password

# 执行查询
USE nacos_config;
SELECT tenant_id FROM tenant_info WHERE tenant_name = 'dev';
```

**方法三：使用命令行直接查询**
```bash
docker exec -i caring-mysql mysql -uroot -pchange-this-password nacos_config -e "SELECT tenant_id FROM tenant_info WHERE tenant_name = 'dev';"
```

#### 第四步：配置 Nacos 命名空间

将获取到的命名空间 UUID 更新到 `src/main/filters/config-dev.properties` 文件中：

```properties
# 注意: nacos.namespace 需要使用 Nacos 中创建的命名空间的实际 UUID
# 执行 nacos-init.sql 后,会自动创建 dev 命名空间,请从 tenant_info 表中查询对应的 tenant_id
# 查询语句: SELECT tenant_id FROM tenant_info WHERE tenant_name = 'dev';
nacos.namespace=85d56e61-f676-11f0-a8b0-328ff568776d
```

**注意**：
- 如果查询到的 UUID 与配置文件中的不一致，请更新配置文件
- 每次重新初始化数据库后，UUID 可能会变化，需要重新配置
- 配置文件中的 `nacos.group` 应该为 `sass-cloud`


#### 第五步：启动后端服务

**方式一：IDE 启动（推荐用于开发）**

1. 在 IDE（如 IntelliJ IDEA）中打开项目
2. 确认 `src/main/filters/config-dev.properties` 中的配置正确
3. 按顺序启动服务：
   - **caring-gateway**（网关服务）- 端口 8760
   - **caring-authority**（权限服务）- 端口 8764
   - **caring-tenant**（租户服务）
   - **caring-ucenter**（用户中心）
   - 其他业务服务（caring-ai、caring-cms、caring-file、caring-msgs、caring-nursing、caring-wx 等）

**方式二：命令行启动**

```bash
# 编译项目
cd packages/backend
mvn clean package -DskipTests

# 启动各个服务的Server模块（在终端中分别执行）
java -jar caring-gateway/caring-gateway-server/target/caring-gateway-server.jar
java -jar caring-authority/caring-authority-server/target/caring-authority-server.jar
java -jar caring-tenant/caring-tenant-server/target/caring-tenant-server.jar
java -jar caring-ucenter/caring-ucenter-server/target/caring-ucenter-server.jar
# ... 其他服务
```

**方式三：使用启动脚本**

```bash
# 启动所有服务
./scripts/start-services.sh

# 重启所有服务
./scripts/restart-services.sh
```

#### 第六步：验证服务启动

启动成功后，可以通过以下方式验证：

1. **检查 Nacos 服务注册**
   - 访问 Nacos 控制台：http://localhost:8848/nacos
   - 进入"服务管理" -> "服务列表"
   - 在 `dev` 命名空间下应该能看到已启动的服务

2. **访问网关**
   ```bash
   curl http://localhost:8760/api/actuator/health
   ```

3. **访问 Swagger 文档**
   - 网关 Swagger: http://localhost:8760/api/doc.html
   - 权限服务 Swagger: http://localhost:8764/doc.html
   - 文件服务 Swagger: http://localhost:8765/doc.html


## API接口

本系统已集成 **Knife4j**（增强版 Swagger）在线 API 文档工具，提供完整的接口文档和在线调试功能。

### API 文档地址

启动服务后，可以通过以下地址访问各服务的 API 文档：

- **网关服务**: http://localhost:8760/api/doc.html
- **权限服务**: http://localhost:{端口}/doc.html
- **文件服务**: http://localhost:{端口}/doc.html
- **用户中心**: http://localhost:{端口}/doc.html
- **租户服务**: http://localhost:{端口}/doc.html
- **AI 服务**: http://localhost:{端口}/doc.html
- **内容管理**: http://localhost:{端口}/doc.html
- **消息服务**: http://localhost:{端口}/doc.html
- **护理服务**: http://localhost:{端口}/doc.html
- **微信服务**: http://localhost:{端口}/doc.html

> **注意**：除网关服务外，其他服务的端口号在 Nacos 配置中心的对应服务配置文件中定义，请根据实际配置访问。

### 使用说明

1. **访问文档**: 在浏览器中打开上述任一地址
2. **查看接口**: 左侧导航栏显示所有接口分组
3. **在线调试**: 点击接口可查看详细信息，并进行在线测试
4. **认证配置**: 在文档页面右上角配置认证信息：
   - `token`: 用户身份 token（Bearer Token）
   - `Authorization`: 客户端信息（Basic Auth）
   - `tenant`: 租户编码

### 认证方式

系统支持多种认证方式：

- **Bearer Token**: 用于用户身份认证
- **Basic Auth**: 用于客户端认证
- **租户隔离**: 通过 header 中的租户编码实现多租户数据隔离

## 配置说明

### 配置管理方式

本项目的所有配置都集成在 **Nacos 配置中心**中，采用集中式配置管理：

- **配置初始化**: Nacos 会随着 Docker 容器启动自动初始化配置信息（通过 `config/sql/nacos-init.sql` 脚本）
- **配置修改**: 所有配置的修改都应在 **Nacos 控制台**中进行，直接修改本地配置文件不会生效
- **配置示例**: `config-example/nacos/clientConfig/` 目录下提供了各服务的配置示例，供参考使用

### 配置文件结构

Nacos 中的配置文件按以下结构组织：

```
Nacos 配置中心 (dev 命名空间, sass-cloud 分组)
├── common.yml                    # 公共配置（所有服务共享）
├── mysql.yml                     # MySQL 数据库配置
├── redis.yml                     # Redis 配置
├── rabbitmq.yml                  # RabbitMQ 配置
├── third-party.yml               # 第三方服务配置
├── caring-authority-server.yml   # 权限服务配置
├── caring-gateway-server.yml     # 网关服务配置
├── caring-ai-server.yml          # AI 服务配置
├── caring-cms-server.yml         # 内容管理服务配置
├── caring-file-server.yml        # 文件服务配置
├── caring-msgs-server.yml        # 消息服务配置
├── caring-nursing-server.yml     # 护理服务配置
├── caring-tenant-server.yml      # 租户服务配置
├── caring-ucenter-server.yml     # 用户中心配置
├── caring-wx-server.yml          # 微信服务配置
└── ...                           # 其他服务配置
```

### 如何修改配置

1. **访问 Nacos 控制台**
   - 地址: http://localhost:8848/nacos
   - 用户名: `nacos`
   - 密码: `nacos`

2. **进入配置管理**
   - 点击左侧菜单"配置管理" -> "配置列表"
   - 选择命名空间: `dev`
   - 选择分组: `sass-cloud`

3. **修改配置**
   - 找到需要修改的配置文件（如 `caring-ai-server.yml`）
   - 点击"编辑"按钮
   - 修改配置内容
   - 点击"发布"保存更改

4. **配置生效**
   - 大部分配置修改后会自动生效（支持配置热更新）
   - 部分配置需要重启对应服务才能生效

### 配置参考

在修改配置前，可以参考 `config-example/nacos/clientConfig/` 目录下的配置示例：

```bash
# 查看配置示例目录
ls -la config-example/nacos/clientConfig/
```

**注意**: 配置示例文件中的值（如 `your_api_key`、`your_app_id` 等）需要根据实际情况替换为真实值。

### 数据库配置

数据库配置在 Nacos 的 `mysql.yml` 文件中：

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

Redis 配置在 Nacos 的 `redis.yml` 文件中：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your-password
```

### 安全配置

安全配置（JWT 等）在 Nacos 的 `common.yml` 文件中：

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

## 常见问题

### 1. 数据库连接失败

**问题现象**：启动服务时报错 "Could not create connection to database server"

**解决方案**：
1. 确认 Docker 容器已启动：`docker-compose ps`
2. 检查 MySQL 是否就绪：`docker-compose logs mysql`
3. 验证数据库连接：
   ```bash
   docker exec -it caring-mysql mysql -uroot -pchange-this-password -e "SELECT 1;"
   ```
4. 检查 `config-dev.properties` 中的数据库配置是否正确
5. 确认 `nacos-config` 数据库已创建：
   ```bash
   docker exec -it caring-mysql mysql -uroot -pchange-this-password -e "SHOW DATABASES;"
   ```

### 2. Redis连接失败

**问题现象**：启动服务时报错 "Unable to connect to Redis"

**解决方案**：
1. 确认 Redis 容器已启动：`docker-compose ps`
2. 检查 Redis 日志：`docker-compose logs redis`
3. 验证 Redis 连接：
   ```bash
   docker exec -it caring-redis redis-cli -a change-this-password ping
   ```
4. 检查 `config-dev.properties` 中的 Redis 配置是否正确

### 3. Nacos 配置获取失败

**问题现象**：启动服务时报错 "get config from nacos error" 或 "config not found"

**解决方案**：
1. 确认 Nacos 容器已启动：`docker-compose ps`
2. 访问 Nacos 控制台：http://localhost:8848/nacos
3. 检查命名空间配置：
   - 进入"命名空间"菜单，确认 `dev` 命名空间存在
   - 记录 `dev` 命名空间的 UUID
4. 检查配置文件：
   - 进入"配置管理" -> "配置列表"
   - 选择 `dev` 命名空间和 `sass-cloud` 分组
   - 确认配置文件存在（common.yml、mysql.yml、redis.yml 等）
5. 更新 `src/main/filters/config-dev.properties` 中的 `nacos.namespace`：
   ```properties
   nacos.namespace=实际的-dev-命名空间-UUID
   nacos.group=sass-cloud
   ```

### 4. Nacos 命名空间 UUID 不匹配

**问题现象**：服务启动时无法读取配置，或读取到错误的配置

**解决方案**：
1. 查询数据库获取正确的 UUID：
   ```bash
   docker exec -i caring-mysql mysql -uroot -pchange-this-password nacos_config -e "SELECT tenant_id, tenant_name FROM tenant_info WHERE tenant_name = 'dev';"
   ```
2. 更新 `src/main/filters/config-dev.properties` 中的 `nacos.namespace`
3. 重新编译并启动服务

### 5. 端口冲突

**问题现象**：启动服务时报错 "Address already in use"

**解决方案**：
1. 查看端口占用情况：
   ```bash
   # macOS/Linux
   lsof -i :8848  # Nacos
   lsof -i :3306  # MySQL
   lsof -i :6379  # Redis
   lsof -i :8760  # Gateway
   lsof -i :8764  # Authority
   ```
2. 停止占用端口的服务或修改配置文件中的端口
3. 或修改 `docker-compose.yml` 中的端口映射

### 6. 依赖下载失败

**问题现象**：Maven 编译时报错 "Could not resolve dependencies"

**解决方案**：
1. 配置 Maven 镜像源（在 `~/.m2/settings.xml` 中）：

   ```xml
   <mirrors>
     <!-- 阿里云镜像 -->
     <mirror>
       <id>aliyun</id>
       <mirrorOf>central</mirrorOf>
       <name>Aliyun Maven</name>
       <url>https://maven.aliyun.com/repository/public</url>
     </mirror>
     
     <!-- 内部 Nexus 仓库 -->
     <mirror>
       <id>nexus</id>
       <mirrorOf>*</mirrorOf>
       <name>Caring Nexus Repository</name>
       <url>https://nexus.caringsaas.cn/repository/maven-snapshots/</url>
     </mirror>
   </mirrors>

   <repositories>
     <repository>
       <id>nexus</id>
       <url>https://nexus.caringsaas.cn/repository/maven-snapshots/</url>
       <releases>
         <enabled>true</enabled>
       </releases>
       <snapshots>
         <enabled>true</enabled>
       </snapshots>
     </repository>
   </repositories>
   ```

2. 清理本地 Maven 缓存：
   ```bash
   rm -rf ~/.m2/repository/com/caring
   ```
3. 重新执行编译：
   ```bash
   mvn clean install -DskipTests -U
   ```

### 7. Docker 容器启动失败

**问题现象**：执行 `docker-compose up -d` 后容器无法启动

**解决方案**：
1. 查看容器日志：
   ```bash
   docker-compose logs mysql
   docker-compose logs nacos
   docker-compose logs redis
   ```
2. 检查端口是否被占用
3. 清理并重新启动：
   ```bash
   docker-compose down
   docker-compose up -d
   ```
4. 如果是权限问题，确保 `volumes/data` 目录有正确的写权限

### 8. 服务注册到 Nacos 失败

**问题现象**：服务启动成功，但在 Nacos 控制台看不到服务

**解决方案**：
1. 检查 `config-dev.properties` 中的 Nacos 配置是否正确
2. 确认 `nacos.namespace` 与 Nacos 中的命名空间 UUID 一致
3. 检查服务日志，查看是否有注册错误信息
4. 在 Nacos 控制台检查：
   - 命名空间是否选择正确
   - 分组是否为 `sass-cloud`
5. 确认服务启动顺序：网关服务应该先启动

### 9. 编译失败：找不到公共模块

**问题现象**：编译时报错 "Could not find artifact com.caring:caring-common"

**解决方案**：
1. 确保先编译公共模块：
   ```bash
   cd packages/backend/caring-public/caring-common
   mvn clean install -DskipTests
   ```
2. 确认公共模块已安装到本地 Maven 仓库：
   ```bash
   ls ~/.m2/repository/com/caring/caring-common
   ```

### 10. 数据库初始化脚本未执行

**问题现象**：Nacos 控制台看不到配置文件

**解决方案**：
1. 检查 MySQL 容器日志：
   ```bash
   docker-compose logs mysql
   ```
2. 手动执行初始化脚本：
   ```bash
   docker exec -i caring-mysql mysql -uroot -pchange-this-password < config/sql/nacos-init.sql
   ```
3. 验证数据库和表是否创建：
   ```bash
   docker exec -it caring-mysql mysql -uroot -pchange-this-password -e "USE nacos_config; SHOW TABLES;"
   ```
4. 如果数据已存在，先清空再重新导入：
   ```bash
   docker exec -it caring-mysql mysql -uroot -pchange-this-password -e "DROP DATABASE IF EXISTS nacos_config;"
   docker-compose down
   docker-compose up -d
   ```


## 前后端联调

### 1. 启动后端服务

确保依赖服务（Docker Compose）已启动，然后选择以下任一方式启动后端服务：

**方式一：使用启动脚本（推荐）**
```bash
# 启动所有后端服务
./scripts/start-services.sh

# 或重启所有服务
./scripts/restart-services.sh
```

**方式二：IDE 启动**
1. 在 IDE（如 IntelliJ IDEA）中打开项目
2. 按顺序启动服务：
   - `caring-gateway-server`（网关服务）- 端口 8760
   - `caring-authority-server`（权限服务）- 端口 8764
   - `caring-tenant-server`（租户服务）
   - `caring-ucenter-server`（用户中心）
   - 其他业务服务

**方式三：命令行启动**
```bash
# 先编译打包
mvn clean package -DskipTests

# 逐个启动服务
java -jar caring-gateway/caring-gateway-server/target/caring-gateway-server.jar
java -jar caring-authority/caring-authority-server/target/caring-authority-server.jar
# ... 其他服务
```

### 2. 验证后端服务

启动成功后，验证服务是否正常运行：

**检查服务注册**
1. 访问 Nacos 控制台：http://localhost:8848/nacos
2. 进入"服务管理" -> "服务列表"
3. 在 `dev` 命名空间下确认所有服务已注册

**访问健康检查**
```bash
curl http://localhost:8760/api/actuator/health
```

**访问 API 文档**
- 网关文档：http://localhost:8760/api/doc.html
- 权限服务文档：http://localhost:8764/doc.html

### 3. 配置前端API地址

修改前端项目的 API 配置，指向后端网关地址：

```javascript
const apiUrl = 'http://localhost:8760/api'
```

**环境变量配置（推荐）**
```bash
# .env.development
VUE_APP_BASE_API=http://localhost:8760/api
```

### 4. 启动前端服务

进入前端项目目录：

```bash
cd ../../frontend  # 或前端项目的实际路径
npm run dev
```

### 5. 联调测试

**测试接口连通性**
```bash
# 测试网关健康检查
curl http://localhost:8760/api/actuator/health

# 测试权限服务（需要先获取 token）
curl -X POST http://localhost:8760/api/auth/oauth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**使用 Swagger 在线调试**
1. 访问 http://localhost:8760/api/doc.html
2. 在右上角配置认证信息（Bearer Token）
3. 选择接口进行在线测试

**常见联调问题**
- **跨域问题**：确保网关已配置 CORS 允许前端域名
- **认证失败**：检查 token 是否正确传递到后端
- **404 错误**：确认请求路径是否正确，是否包含 `/api` 前缀
- **超时问题**：检查网络连接和防火墙设置

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