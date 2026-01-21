# Caring SaaS Cloud 部署指南

## 📋 目录

- [概述](#概述)
- [架构要求](#架构要求)
- [环境准备](#环境准备)
- [部署步骤](#部署步骤)
- [配置管理](#配置管理)
- [多租户配置](#多租户配置)
- [监控运维](#监控运维)
- [常见问题](#常见问题)

## 📖 概述

Caring SaaS Cloud 采用微服务架构，支持多租户部署模式。本指南将详细介绍如何在不同环境中部署 SaaS 版本的 Caring 医疗管理系统。

### 部署模式

- **开发环境**: 单机部署，适合开发测试
- **测试环境**: 容器化部署，模拟生产环境
- **生产环境**: Kubernetes 集群部署，高可用架构

## 🏗️ 架构要求

### 硬件要求

#### 最小配置
- **CPU**: 8 核心
- **内存**: 16GB RAM
- **存储**: 200GB SSD
- **网络**: 1Gbps 带宽

#### 推荐配置
- **CPU**: 16 核心
- **内存**: 32GB RAM
- **存储**: 500GB SSD
- **网络**: 10Gbps 带宽

#### 生产环境
- **CPU**: 32 核心
- **内存**: 64GB RAM
- **存储**: 1TB SSD + 2TB HDD
- **网络**: 10Gbps 带宽
- **高可用**: 负载均衡 + 多节点部署

### 软件要求

- **操作系统**: CentOS 7.6+ / Ubuntu 18.04+ / RHEL 7.6+
- **容器环境**: Docker 20.10+ / Kubernetes 1.20+
- **数据库**: MySQL 8.0+ 集群
- **缓存**: Redis 6.0+ 集群
- **消息队列**: RabbitMQ 3.8+ / RocketMQ 4.9+
- **配置中心**: Nacos 2.0+ 集群
- **注册中心**: Nacos 2.0+ 集群

## 🛠️ 环境准备

### 1. 基础环境安装

#### 安装 Docker
```bash
# CentOS
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
systemctl enable docker
systemctl start docker

# Ubuntu
apt-get update
apt-get install docker.io docker-compose
systemctl enable docker
systemctl start docker
```

#### 安装 Kubernetes (生产环境)
```bash
# 使用 kubeadm 安装
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -
echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" > /etc/apt/sources.list.d/kubernetes.list
apt-get update
apt-get install -y kubelet kubeadm kubectl
systemctl enable kubelet
```

### 2. 数据库环境

#### MySQL 集群部署
```bash
# 使用 Docker 部署 MySQL 主从
docker-compose -f docker/mysql-cluster.yml up -d

# 或使用 Kubernetes
kubectl apply -f k8s/mysql-cluster.yaml
```

#### Redis 集群部署
```bash
# Docker 部署
docker-compose -f docker/redis-cluster.yml up -d

# Kubernetes 部署
kubectl apply -f k8s/redis-cluster.yaml
```

### 3. 中间件环境

#### Nacos 集群
```bash
# 下载 Nacos
wget https://github.com/alibaba/nacos/releases/download/2.0.4/nacos-server-2.0.4.tar.gz
tar -xzf nacos-server-2.0.4.tar.gz

# 配置集群
cp config-example/nacos/cluster.conf nacos/conf/
vim nacos/conf/cluster.conf  # 添加节点IP

# 启动集群
sh nacos/bin/startup.sh
```

#### RabbitMQ 集群
```bash
# Docker 部署
docker-compose -f docker/rabbitmq-cluster.yml up -d

# Kubernetes 部署
kubectl apply -f k8s/rabbitmq-cluster.yaml
```

## 🚀 部署步骤

### 1. 准备部署文件

```bash
# 克隆项目
git clone https://github.com/your-org/caring-sass-cloud.git
cd caring-sass-cloud

# 复制配置文件
cp -r config-example/* config/
```

### 2. 配置数据库

#### 创建数据库
```sql
-- 创建主数据库
CREATE DATABASE caring_saas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建租户数据库模板
CREATE DATABASE caring_tenant_template CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'caring_saas'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON caring_saas.* TO 'caring_saas'@'%';
GRANT ALL PRIVILEGES ON caring_tenant_template.* TO 'caring_saas'@'%';
FLUSH PRIVILEGES;
```

#### 执行数据库迁移
```bash
# 导入基础表结构
mysql -u caring_saas -p caring_saas < docs/sql/caring_base_0000.sql

# 导入租户模板
mysql -u caring_saas -p caring_tenant_template < docs/sql/caring_column.sql
```

### 3. 配置 Nacos

#### 导入配置
```bash
# 使用 Nacos 配置管理脚本
python scripts/nacos-config-import.py \
  --nacos-server http://nacos-server:8848 \
  --namespace caring-saas \
  --config-dir config/nacos
```

#### 主要配置项
```yaml
# 数据库配置
caring:
  mysql:
    ip: mysql-cluster-master
    port: 3306
    database: caring_saas
    username: caring_saas
    password: ${MYSQL_PASSWORD}

# Redis 配置
spring:
  redis:
    cluster:
      nodes:
        - redis-node-1:6379
        - redis-node-2:6379
        - redis-node-3:6379

# 租户配置
caring:
  tenant:
    enabled: true
    isolation-level: database  # database/schema/table
    auto-create: true
```

### 4. 部署微服务

#### 方式一：Docker Compose 部署
```bash
# 构建镜像
mvn clean package -DskipTests
docker-compose -f docker/saas-compose.yml build

# 启动服务
docker-compose -f docker/saas-compose.yml up -d

# 查看状态
docker-compose -f docker/saas-compose.yml ps
```

#### 方式二：Kubernetes 部署
```bash
# 创建命名空间
kubectl create namespace caring-saas

# 部署配置
kubectl apply -f k8s/configmaps/
kubectl apply -f k8s/secrets/

# 部署服务
kubectl apply -f k8s/services/
kubectl apply -f k8s/deployments/

# 查看状态
kubectl get pods -n caring-saas
```

### 5. 服务启动顺序

1. **基础设施服务**
   - MySQL 集群
   - Redis 集群
   - Nacos 集群
   - RabbitMQ 集群

2. **核心服务**
   - caring-gateway (网关)
   - caring-authority (认证)
   - caring-tenant (租户)

3. **业务服务**
   - caring-ucenter (用户中心)
   - caring-ai (AI 服务)
   - caring-cms (内容管理)
   - caring-file (文件服务)
   - caring-msgs (消息服务)
   - caring-wx (微信服务)
   - caring-nursing (护理服务)

## ⚙️ 配置管理

### 环境配置

#### 开发环境 (dev)
```yaml
spring:
  profiles:
    active: dev
  
caring:
  mysql:
    ip: localhost
    port: 3306
    database: caring_saas_dev
  
  redis:
    host: localhost
    port: 6379
    database: 0
```

#### 测试环境 (test)
```yaml
spring:
  profiles:
    active: test
  
caring:
  mysql:
    ip: test-mysql-cluster
    port: 3306
    database: caring_saas_test
  
  redis:
    cluster:
      nodes:
        - test-redis-1:6379
        - test-redis-2:6379
```

#### 生产环境 (prod)
```yaml
spring:
  profiles:
    active: prod
  
caring:
  mysql:
    ip: prod-mysql-cluster
    port: 3306
    database: caring_saas_prod
    connection-pool:
      maximum-pool-size: 50
      minimum-idle: 10
  
  redis:
    cluster:
      nodes:
        - prod-redis-1:6379
        - prod-redis-2:6379
        - prod-redis-3:6379
        - prod-redis-4:6379
        - prod-redis-5:6379
        - prod-redis-6:6379
```

### 配置热更新

Nacos 支持配置热更新，无需重启服务：

```bash
# 在 Nacos 控制台修改配置
# 或使用 API
curl -X POST "http://nacos-server:8848/nacos/v1/cs/configs" \
  -d "dataId=caring-gateway-server.yml" \
  -d "group=DEFAULT_GROUP" \
  -d "content=$(cat config/gateway-prod.yml)"
```

## 🏢 多租户配置

### 租户隔离级别

#### 数据库级隔离
```yaml
caring:
  tenant:
    isolation-level: database
    auto-create: true
    template-database: caring_tenant_template
```

#### Schema 级隔离
```yaml
caring:
  tenant:
    isolation-level: schema
    auto-create: true
    template-schema: tenant_template
```

#### 表级隔离
```yaml
caring:
  tenant:
    isolation-level: table
    tenant-id-column: tenant_id
    auto-create: false
```

### 租户管理

#### 创建租户
```bash
# 调用租户管理 API
curl -X POST "http://gateway:8080/caring-tenant/api/tenant/create" \
  -H "Content-Type: application/json" \
  -d '{
    "tenantCode": "hospital001",
    "tenantName": "示例医院",
    "contactName": "张三",
    "contactPhone": "13800138000",
    "contactEmail": "admin@hospital001.com",
    "maxUsers": 100,
    "expireTime": "2024-12-31"
  }'
```

#### 租户配置
```json
{
  "tenantCode": "hospital001",
  "tenantName": "示例医院",
  "databaseConfig": {
    "host": "mysql-cluster",
    "port": 3306,
    "database": "caring_tenant_hospital001",
    "username": "tenant_hospital001",
    "password": "encrypted_password"
  },
  "featureConfig": {
    "aiEnabled": true,
    "wechatEnabled": true,
    "maxStorageGB": 100
  },
  "customConfig": {
    "hospitalName": "示例医院",
    "logoUrl": "https://example.com/logo.png",
    "themeColor": "#4A90E2"
  }
}
```

## 📊 监控运维

### 服务监控

#### Spring Boot Actuator
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

#### Prometheus 监控
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'caring-saas'
    kubernetes_sd_configs:
      - role: pod
        namespaces:
          names:
            - caring-saas
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
```

### 日志管理

#### ELK Stack 配置
```yaml
# logstash.conf
input {
  beats {
    port => 5044
  }
}

filter {
  if [fields][service] == "caring-saas" {
    json {
      source => "message"
    }
    
    mutate {
      add_field => { "tenant_id" => "%{[tenant_id]}" }
    }
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "caring-saas-%{+YYYY.MM.dd}"
  }
}
```

### 健康检查

#### 服务健康检查脚本
```bash
#!/bin/bash
# health-check.sh

SERVICES=("caring-gateway:8080" "caring-authority:8082" "caring-tenant:8087")

for service in "${SERVICES[@]}"; do
  IFS=':' read -r name port <<< "$service"
  
  response=$(curl -s -o /dev/null -w "%{http_code}" "http://$name:$port/actuator/health")
  
  if [ "$response" = "200" ]; then
    echo "✅ $name is healthy"
  else
    echo "❌ $name is unhealthy (HTTP $response)"
    # 发送告警
    curl -X POST "http://alertmanager:9093/api/v1/alerts" \
      -H "Content-Type: application/json" \
      -d "[{
        \"labels\": {
          \"alertname\": \"ServiceUnhealthy\",
          \"service\": \"$name\",
          \"severity\": \"critical\"
        }
      }]"
  fi
done
```

## 🔧 常见问题

### Q1: 服务启动失败

**问题**: 微服务启动时出现数据库连接错误

**解决方案**:
1. 检查数据库配置是否正确
2. 确认数据库服务是否正常运行
3. 验证网络连通性
4. 检查数据库用户权限

```bash
# 检查数据库连接
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD -e "SELECT 1"

# 检查服务日志
docker logs caring-gateway
kubectl logs -n caring-saas deployment/caring-gateway
```

### Q2: 租户数据隔离问题

**问题**: 租户间数据出现串扰

**解决方案**:
1. 检查租户隔离配置
2. 验证数据库连接池配置
3. 确认租户上下文传递正确

```sql
-- 检查租户数据
SELECT tenant_id, COUNT(*) FROM user GROUP BY tenant_id;

-- 检查数据隔离
SELECT * FROM user WHERE tenant_id = 'tenant001' LIMIT 10;
```

### Q3: 性能问题

**问题**: 系统响应缓慢

**解决方案**:
1. 检查数据库索引
2. 优化查询语句
3. 调整缓存策略
4. 增加服务实例

```sql
-- 检查慢查询
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';

-- 分析查询计划
EXPLAIN SELECT * FROM user WHERE tenant_id = 'tenant001';
```

### Q4: 配置更新不生效

**问题**: Nacos 配置更新后服务未生效

**解决方案**:
1. 检查 Nacos 配置格式
2. 确认服务监听配置变化
3. 重启相关服务

```bash
# 检查 Nacos 配置
curl "http://nacos-server:8848/nacos/v1/cs/configs?dataId=caring-gateway-server.yml&group=DEFAULT_GROUP"

# 强制刷新配置
curl -X POST "http://gateway:8080/actuator/refresh"
```

## 📞 技术支持

如果在部署过程中遇到问题，可以通过以下方式获取帮助：

- **文档中心**: [https://docs.caring.com](https://docs.caring.com)
- **问题反馈**: [GitHub Issues](https://github.com/your-org/caring-sass-cloud/issues)
- **技术交流**: 企业微信群 / QQ 技术群
- **商务合作**: business@caring.com

---

<div align="center">

**[⬆ 回到顶部](#caring-saas-cloud-部署指南)**

Made with ❤️ by Caring Team

</div>