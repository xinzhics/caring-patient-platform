# Docker Compose 依赖环境快速启动指南

> **注意**: 此配置仅用于启动后端项目所需的**依赖环境**，不包括后端业务服务本身。后端服务需要单独启动。

## 前置要求

- Docker 20.10+
- Docker Compose 1.29+

## 镜像源配置

如果在国内网络环境下拉取镜像速度慢或失败，建议配置国内镜像源加速。

### 配置 Docker 镜像加速器

编辑 Docker 配置文件（通常位于 `/etc/docker/daemon.json` 或 `~/.docker/daemon.json`）：

```json
{
  "registry-mirrors": [
    "https://docker.m.daocloud.io",
    "https://dockerproxy.com",
    "https://docker.mirrors.ustc.edu.cn",
    "https://docker.nju.edu.cn"
  ]
}
```

### 重启 Docker 服务

```bash
# Linux/Mac
sudo systemctl restart docker

# 或者使用 Docker Desktop 重启
```

### 验证配置

```bash
docker info | grep -A 10 "Registry Mirrors"
```

### 常用国内镜像源

- DaoCloud: `https://docker.m.daocloud.io`
- 中科大: `https://docker.mirrors.ustc.edu.cn`
- 南京大学: `https://docker.nju.edu.cn`
- DockerProxy: `https://dockerproxy.com`

### 使用阿里云容器镜像服务 (ACR) 专属加速器（推荐）

使用阿里云容器镜像服务 (ACR) 是一个非常稳妥的方案。阿里云会为每个账号生成一个专属的加速器地址，稳定性比公共加速器高很多。

以下是针对 macOS (Apple Silicon/Intel 均适用) 的详细配置教程：

**第一步：获取你的专属加速器地址**

1. 登录 [阿里云容器镜像服务控制台](https://cr.console.aliyun.com/)
2. 在左侧菜单栏选择 **镜像工具** -> **镜像加速器**
3. 在页面中间你会看到一个 **加速器地址**，通常格式为：`https://[你的随机字符].mirror.aliyuncs.com`
4. 请复制这个地址备用

**第二步：配置到 Docker**

将获取到的专属加速器地址添加到 Docker 配置文件中：

```json
{
  "registry-mirrors": [
    "https://[你的专属加速器地址].mirror.aliyuncs.com",
    "https://docker.m.daocloud.io",
    "https://dockerproxy.com"
  ]
}
```

配置完成后，重新执行 `docker-compose pull` 即可加速镜像下载。

## 快速启动

### 1. 启动依赖环境服务

此配置将启动以下依赖服务：

- **MySQL 8.0** - 数据库服务
- **Nacos 1.4.1** - 配置中心和注册中心
- **Redis 5.0.3** - 缓存服务
- **XXL-Job 2.2.0** - 分布式任务调度中心
- **Nginx 1.10** - 反向代理和静态资源服务

```bash
# 启动所有依赖服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 2. 服务访问地址

启动成功后，可以通过以下地址访问各服务：

- **Nacos控制台**: http://localhost:8848/nacos
  - 默认用户名: `nacos`
  - 默认密码: `nacos`

- **MySQL**: localhost:3306
  - Root密码: `change-this-password` (请在 .env 中修改)

- **Redis**: localhost:6379
  - 密码: `change-this-password` (请在 .env 中修改)

- **XXL-Job**: http://localhost:9001/xxl-job-admin
  - 默认用户名: `admin`
  - 默认密码: `123456`

### 3. 导入Nacos配置

#### 方式一：通过Nacos控制台手动导入

1. 访问 http://localhost:8848/nacos
2. 登录后进入"配置管理" -> "配置列表"
3. 点击"导入配置"
4. 选择 `config-example/nacos/` 目录下的配置文件进行导入

#### 方式二：使用配置导入脚本（推荐）

```bash
# 确保已安装 curl
# 导入 Nacos 配置
curl -X POST "http://localhost:8848/nacos/v1/cs/configs" \
  -d "dataId=caring-common.yml" \
  -d "group=CARING_GROUP" \
  -d "content=$(cat config-example/nacos/conf/caring-common.yml)"
```

### 4. 配置数据库

#### 创建业务数据库

```bash
# 连接到MySQL
docker exec -it caring-mysql mysql -uroot -pchange-this-password

# 创建业务数据库
CREATE DATABASE IF NOT EXISTS caring_job DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS caring_standalone DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 退出
exit;
```

#### 导入初始化SQL

```bash
# 导入XXL-Job数据库
docker exec -i caring-mysql mysql -uroot -pchange-this-password caring_job < docs/sql/xxl-job.sql

# 导入业务数据库（如果有初始化脚本）
docker exec -i caring-mysql mysql -uroot -pchange-this-password caring_standalone < docs/sql/init.sql
```

### 5. 配置应用启动参数

在启动后端服务前，请确保以下配置正确：

#### 修改 .env 文件

```bash
# 编辑环境变量
vi .env

# 修改以下关键配置：
# - MYSQL_PASSWORD: 修改为安全的数据库密码
# - REDIS_PASSWORD: 修改为安全的Redis密码
# - NACOS_AUTH_TOKEN_KEY: 修改为安全的Token密钥
# - JWT_SECRET: 修改为安全的JWT密钥
```

#### 修改 Nacos 配置

在Nacos控制台中，根据实际情况修改以下配置：

1. **数据源配置** (caring-mysql.yml):
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://127.0.0.1:3306/caring_standalone?...
       username: root
       password: change-this-password  # 修改为实际密码
   ```

2. **Redis配置** (caring-redis.yml):
   ```yaml
   spring:
     redis:
       host: 127.0.0.1
       port: 6379
       password: change-this-password  # 修改为实际密码
   ```

## 常用命令

### 服务管理

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose stop

# 停止并删除容器
docker-compose down

# 停止并删除容器及数据卷（慎用）
docker-compose down -v

# 重启服务
docker-compose restart

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f [服务名]
```

### 数据管理

```bash
# 备份MySQL数据
docker exec caring-mysql mysqldump -uroot -pchange-this-password --all-databases > backup.sql

# 恢复MySQL数据
docker exec -i caring-mysql mysql -uroot -pchange-this-password < backup.sql

# 清理Redis数据
docker exec caring-redis redis-cli -a change-this-password FLUSHALL
```

## 故障排查

### MySQL连接失败

```bash
# 检查MySQL状态
docker-compose logs mysql

# 进入MySQL容器
docker exec -it caring-mysql bash

# 检查MySQL进程
ps aux | grep mysql
```

### Nacos无法启动

```bash
# 检查Nacos日志
docker-compose logs nacos

# 检查MySQL连接是否正常
docker exec -it caring-nacos bash
ping mysql
```

### Redis连接失败

```bash
# 检查Redis状态
docker-compose logs redis

# 测试Redis连接
docker exec -it caring-redis redis-cli -a change-this-password ping
```

## 安全建议

1. **修改默认密码**: 生产环境务必修改所有服务的默认密码
2. **网络安全**: 生产环境建议不要使用 `network_mode: host`
3. **数据备份**: 定期备份MySQL和Redis数据
4. **访问控制**: 限制Nacos、XXL-Job等管理平台的访问IP
5. **日志监控**: 配置日志收集和监控告警

## 数据持久化

所有服务的配置和数据都通过Docker volume进行持久化：

- MySQL数据: `./volumes/data/mysqldata`
- Redis数据: `./volumes/data/redsidata`
- Nacos日志: `./volumes/data/nacos-logs`
- XXL-Job日志: `./volumes/data/xxl-job`
- Nginx日志: `./volumes/data/nginx-log`

## 配置文件说明

所有依赖服务的配置文件已整合到 `./config/` 目录下，按应用名称分类管理：

```
config/
├── mysql/              # MySQL配置目录
│   └── my.cnf         # MySQL主配置文件
├── redis/              # Redis配置目录
│   └── redis.conf     # Redis配置文件
├── nacos/             # Nacos配置目录
│   ├── conf/          # Nacos配置文件
│   └── init.d/        # Nacos初始化脚本
│       └── custom.properties
├── nginx/             # Nginx配置目录
│   ├── conf/          # Nginx主配置
│   └── html/          # 静态资源目录
├── xxl-job/           # XXL-Job配置目录
│   └── application.properties
└── sql/               # SQL脚本目录
    ├── caring_base_0000.sql
    └── caring_column.sql
```

这些配置文件从 `docs/dockerfile/` 和 `docs/sql/` 目录迁移而来，避免重复维护。

## 注意事项

1. 首次启动MySQL需要较长时间初始化
2. 确保端口未被占用：8848(Nacos), 3306(MySQL), 6379(Redis), 9001(XXL-Job)
3. 修改配置后需要重启对应服务：`docker-compose restart [服务名]`
4. 生产环境建议使用独立的配置管理和密钥管理方案

## 启动后端业务服务

依赖环境启动成功后，需要单独启动后端业务服务：

### 方式一：IDE启动

1. 在IDE中打开项目
2. 配置Nacos连接地址：`127.0.0.1:8848`
3. 修改 `src/main/filters/config-dev.properties` 中的配置
4. 按顺序启动服务：
   - caring-gateway（网关服务）
   - caring-authority（权限服务）
   - caring-tenant（租户服务）
   - caring-ucenter（用户中心）
   - 其他业务服务

### 方式二：命令行启动

```bash
# 编译项目
mvn clean package -DskipTests

# 启动各个服务的Server模块
java -jar caring-gateway/caring-gateway-server/target/caring-gateway-server.jar
java -jar caring-authority/caring-authority-server/target/caring-authority-server.jar
java -jar caring-tenant/caring-tenant-server/target/caring-tenant-server.jar
java -jar caring-ucenter/caring-ucenter-server/target/caring-ucenter-server.jar
# ... 其他服务
```

### 单机版启动

如果使用单机版配置，可以直接启动：

```bash
java -jar caring-standalone/target/caring-standalone.jar
```

详细启动说明请参考项目 README.md 或 STANDALONE.md