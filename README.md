# 卡柠慢病管理平台 (Caring Patient Platform)

[English](./README_EN.md) | [中文](./README.md)

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Node](https://img.shields.io/badge/node-%3E%3D16.0.0-green.svg)
![Vue](https://img.shields.io/badge/vue-2.6%20%7C%203.5-brightgreen.svg)
![TypeScript](https://img.shields.io/badge/typescript-4.5+-blue.svg)

一个完整的慢病管理平台全栈解决方案，包含患者端、医生端、医助端、病例讨论端、患者管理平台和管理后台。专注于院后管理、患者随访、私域运营和全生命周期健康服务，为药企、民营医疗和康养机构提供数字化患者管理能力，打造医疗行业的Salesforce。

## 🌟 项目特性

- **医疗CRM核心**: 专业的患者关系管理系统，支持患者标签、分层管理、精准营销
- **私域运营**: 全渠道患者触达，支持IM、短信、微信等多渠道沟通，提升患者粘性
- **院后管理**: 完整的随访计划、用药提醒、康复指导，打破院墙限制，提供7x24h伴随式健康服务
- **多端覆盖**: 患者端、医生端、医助端、管理后台全端支持，满足不同角色协作需求
- **现代化技术栈**: Vue 2/3 + TypeScript + Vite + Element UI + Vant
- **移动优先**: 专为移动端优化，提供流畅的移动体验
- **实时通信**: 集成环信SDK，支持音视频通话和即时消息，高效医患协作
- **数据可视化**: 丰富的图表组件，直观展示患者数据和运营指标
- **权限管理**: 基于RBAC的细粒度权限控制，支持多租户部署

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│              卡柠慢病管理平台                  │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────┐ │
│  │   患者端     │ │   医生端     │ │   医助端     │ │病例讨论端│ │
│  │  (H5应用)   │ │  (H5应用)   │ │  (H5应用)   │ │(H5应用) │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────┘ │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐ ┌─────────────┐                             │
│  │患者管理平台   │ │   管理后台   │                             │
│  │  (H5应用)   │ │ (Web应用)   │                             │
│  └─────────────┘ └─────────────┘                             │
├─────────────────────────────────────────────────────────────┤
│                    共享组件库                                │
├─────────────────────────────────────────────────────────────┤
│                    后端API服务                               │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐             │
│  │ 患者管理服务 │ │ 权限认证服务 │ │   AI服务    │             │
│  └─────────────┘ └─────────────┘ └─────────────┘             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐             │
│  │ 内容管理服务 │ │ 微信集成服务 │ │   文件服务   │             │
│  └─────────────┘ └─────────────┘ └─────────────┘             │
├─────────────────────────────────────────────────────────────┤
│                    数据存储层                                │
│  ┌─────────────┐ ┌─────────────┐                             │
│  │  MySQL 8.0  │ │  Redis 6.0  │                             │
│  └─────────────┘ └─────────────┘                             │
└─────────────────────────────────────────────────────────────┘
```

## 📦 项目结构

```
caring-patient-platform/
├── packages/
│   ├── backend/              # 后端服务
│   │   ├── caring-ai/        # AI服务
│   │   ├── caring-authority/ # 权限认证
│   │   ├── caring-cms/       # 内容管理
│   │   ├── caring-file/      # 文件服务
│   │   ├── caring-ucenter/   # 用户中心
│   │   ├── caring-wx/        # 微信集成
│   │   └── ...               # 其他服务
│   ├── patient-app/          # 患者端应用
│   ├── doctor-app/           # 医生端应用
│   ├── assistant-app/        # 医助端应用
│   ├── consultation-app/     # 病例讨论端应用
│   ├── patient-manage-app/   # 患者管理平台应用
│   ├── admin-ui/             # 管理后台应用
│   └── shared-components/    # 共享组件库
├── docs/                     # 文档目录
├── scripts/                  # 构建和部署脚本
├── .github/                  # GitHub Actions CI/CD
├── package.json              # 根package.json
├── lerna.json                # Lerna配置
└── README.md                 # 项目说明
```

## 🚀 快速开始

### 环境要求

**前端环境**:
- Node.js >= 16.0.0
- npm >= 8.0.0

**后端环境**:
- Java 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 安装依赖

```bash
# 安装前端依赖
npm install
npm run bootstrap

# 安装后端依赖
npm run backend:install
```

### 开发环境

**前端开发**:
```bash
# 启动所有前端项目
npm run dev

# 启动单个前端项目
npm run dev --scope=@caring/patient-app
```

**后端开发**:

详细的开发文档请参考 [packages/backend/README.md](./packages/backend/README.md)

**环境要求**:
- Java 1.8 (⚠️ 必须使用 JDK 1.8 进行编译)
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Docker & Docker Compose (用于启动依赖服务)

**快速启动后端**:

#### 第一步：启动依赖服务（Docker Compose）

```bash
cd packages/backend
docker-compose up -d
```

此命令会启动 MySQL、Nacos、Redis、XXL-Job、Nginx 等依赖服务，并自动执行数据库初始化脚本。

**首次启动需要等待 1-2 分钟，等待所有服务完全启动。**

依赖环境启动后，可以访问以下服务：
- **Nacos 控制台**: http://localhost:8848/nacos (用户名/密码: nacos/nacos)
- **MySQL**: localhost:3306 (用户名: root, 密码: change-this-password)
- **Redis**: localhost:6379 (密码: change-this-password)
- **XXL-Job**: http://localhost:8080/xxl-job-admin (用户名: admin, 密码: 123456)

#### 第二步：验证 Nacos 配置初始化

1. 访问 Nacos 控制台：http://localhost:8848/nacos
2. 登录（用户名/密码: nacos/nacos）
3. 进入"配置管理" -> "配置列表"
4. 在命名空间下拉框中找到并选择 `dev` 命名空间
5. 确认分组为 `sass-cloud`
6. 应该能看到配置文件（common.yml、mysql.yml、redis.yml 等）

#### 第三步：获取 Nacos 命名空间 UUID

⚠️ **重要**：启动后端服务前，必须确保 `config-dev.properties` 中的 `nacos.namespace` 与 Nacos 中的 `dev` 命名空间 UUID 一致。

**获取命名空间 UUID**：

**方法一：通过 Nacos 控制台**
1. 进入"命名空间"菜单
2. 找到名为 `dev` 的命名空间
3. 复制其命名空间 ID（UUID 格式）

**方法二：通过数据库查询**
```bash
docker exec -i caring-mysql mysql -uroot -pchange-this-password nacos_config -e "SELECT tenant_id FROM tenant_info WHERE tenant_name = 'dev';"
```

#### 第四步：配置 Nacos 命名空间

将获取到的命名空间 UUID 更新到 `src/main/filters/config-dev.properties` 文件中：

```properties
nacos.namespace=85d56e61-f676-11f0-a8b0-328ff568776d
nacos.group=sass-cloud
```

#### 第五步：安装依赖

⚠️ **重要**: 首次编译时，必须**优先编译** `caring-public/caring-common` 公共模块。

```bash
# 在项目根目录执行
cd caring-patient-platform
npm run backend:install
```

#### 第六步：启动后端服务

**方式一：使用启动脚本（推荐）**
```bash
cd packages/backend
./scripts/start-services.sh
```

**方式二：IDE 启动**
在 IDE（如 IntelliJ IDEA）中按顺序启动服务：
- caring-gateway（网关服务）- 端口 8760
- caring-authority（权限服务）- 端口 8764
- caring-tenant（租户服务）
- caring-ucenter（用户中心）
- 其他业务服务

**方式三：命令行启动**
```bash
cd packages/backend
mvn clean package -DskipTests
java -jar caring-gateway/caring-gateway-server/target/caring-gateway-server.jar
java -jar caring-authority/caring-authority-server/target/caring-authority-server.jar
# ... 其他服务
```

#### 第七步：验证服务启动

启动成功后，可以通过以下方式验证：

1. **检查 Nacos 服务注册**
   - 访问 Nacos 控制台：http://localhost:8848/nacos
   - 进入"服务管理" -> "服务列表"
   - 在 `dev` 命名空间下应该能看到已启动的服务

2. **访问网关**
   ```bash
   curl http://localhost:8760/api/
   ```

3. **访问 API 文档**
   - 网关 API 文档: http://localhost:8760/api/doc.html
   - 权限服务: http://localhost:8764/doc.html

**后端API文档**:
- 网关服务: http://localhost:8760/api/doc.html
- 权限服务: http://localhost:8764/doc.html
- 文件服务: http://localhost:8765/doc.html

### 快速启动（Docker）

```bash
# 一键启动前后端服务
docker-compose up -d

# 查看日志
docker-compose logs -f
```

### 构建

```bash
# 构建前端项目
npm run build

# 构建后端项目
npm run backend:build
```

### 代码检查

```bash
# 检查前端代码
npm run lint

# 运行后端测试
npm run backend:test
```

## 📱 应用介绍

### 患者端 (@caring/patient-app)

患者使用的移动端应用，提供健康档案管理、随访管理、健康咨询等功能，是患者与医疗服务机构连接的入口。

**技术栈**: Vue 2.5.2 + Vux 2.2.0 + Vuex + Vue Router

**主要功能**:
- 🏥 健康档案管理
- 📋 随访计划和健康监测
- 💬 在线健康咨询(IM聊天)
- 📝 问卷调查和数据记录
- 📅 随访管理和复诊提醒

### 医生端 (@caring/doctor-app)

医生使用的移动端应用，提供患者管理、医患协作和病例讨论功能，是医生进行患者服务和随访管理的核心工具。

**技术栈**: Vue 2.7.16 + Vux 2.2.0 + Vuex + Vue Router

**主要功能**:
- 👥 患者管理和分层运营
- 🩺 病例讨论和专家协作
- 📊 统计分析和运营报表
- 📝 常用语库和患教内容管理
- 📢 群发消息和精准营销

### 医助端 (@caring/assistant-app)

医助人员使用的移动端应用，协助医生进行患者服务和随访管理，提升服务效率。

**技术栈**: Vue 2.5.2 + Vant 2.11.0 + Axios

**主要功能**:
- 👥 患者信息管理和随访协助
- 📅 随访计划执行和健康指导
- 📰 患教内容推送和健康管理
- 📊 统计报表和数据监控
- 📝 常用语和模板管理

### 病例讨论端 (@caring/consultation-app)

开放的病例讨论和医疗协作平台，支持多方医疗专家进行病例交流和专业探讨。

**技术栈**: Vue 2.5.2 + Ant Design Vue + 环信SDK

**主要功能**:
- 🩺 病例讨论和专家协作
- 📹 实时音视频通信
- 📄 文档共享和协作
- 👨‍⚕️ 专业咨询功能

### 患者管理平台 (@caring/patient-manage-app)

专业的患者数据管理和运营平台，侧重患者标签体系、数据质量监控和精细化运营。

**技术栈**: Vue 3.5.13 + Vant 3.4.5 + Vite + TypeScript

**主要功能**:
- 📊 患者标签体系和分层管理
- 🔍 异常数据监控和处理
- 💊 用药管理和依从性分析
- 📈 健康监测数据分析
- 🏢 行政管理和运营统计

### 管理后台 (@caring/admin-ui)

平台运营管理后台，支撑整个系统的配置、管理和私域运营。

**技术栈**: Vue 2.6.10 + Element UI 2.12.0

**主要功能**:
- 🏢 租户管理和权限控制
- 👥 用户和组织管理
- 📝 内容管理系统(CMS)和患教内容管理
- ⚙️ 系统配置和监控
- 📱 短信、消息和私域触达管理

### 共享组件库 (@caring/shared-components)

通用组件和工具函数库，提供统一的UI组件和工具方法。

**主要功能**:
- 🎨 统一的UI组件设计
- 🔧 通用工具函数
- 📱 移动端适配组件
- 🌐 多框架支持(Vue 2/3)
- 📊 图表组件封装

## 🛠️ 开发指南

### 技术规范

- **代码风格**: 使用ESLint + Prettier进行代码规范化
- **提交规范**: 遵循Conventional Commits规范
- **分支策略**: 采用Git Flow工作流
- **版本管理**: 使用Lerna进行多包版本管理

### 组件开发

```vue
<template>
  <div class="caring-component">
    <!-- 组件内容 -->
  </div>
</template>

<script>
export default {
  name: 'CaringComponent',
  props: {
    // 属性定义
  },
  data() {
    return {
      // 数据定义
    }
  }
}
</script>

<style scoped>
.caring-component {
  /* 组件样式 */
}
</style>
```

### API调用

```javascript
// 使用共享组件库的请求方法
import { request } from '@caring/shared-components'

// GET请求
request.get('/api/patients').then(data => {
  console.log(data)
})

// POST请求
request.post('/api/patients', patientData).then(data => {
  console.log(data)
})
```

### 权限控制

```javascript
// 使用权限指令
<template>
  <button v-permission="'patient:edit'">编辑患者</button>
</template>

// 使用权限方法
import { hasPermission } from '@caring/shared-components'

if (hasPermission('patient:edit')) {
  // 有权限的逻辑
}
```

## 📊 技术栈详情

### 前端框架

| 项目 | Vue版本 | 构建工具 | UI框架 | 状态管理 |
|------|---------|----------|--------|----------|
| 患者端 | 2.5.2 | Webpack 3.6.0 | Vux 2.2.0 | Vuex 2.1.1 |
| 医生端 | 2.7.16 | Webpack 3.6.0 | Vux 2.2.0 | Vuex 2.1.1 |
| 医助端 | 2.5.2 | Webpack 3.6.0 | Vant 2.11.0 | - |
| 病例讨论端 | 2.5.2 | Webpack 3.6.0 | Ant Design Vue 1.7.2 | Vuex 2.1.1 |
| 患者管理平台 | 3.5.13 | Vite 2.8.0 | Vant 3.4.5 | Pinia 2.0.11 |
| 管理后台 | 2.6.10 | Vue CLI 4.5.7 | Element UI 2.12.0 | Vuex 3.1.0 |

### 核心依赖

- **HTTP客户端**: Axios
- **图表库**: ECharts
- **富文本编辑**: wangeditor
- **音视频通信**: 环信WebRTC SDK（支持健康咨询和协作）
- **即时通讯**: 环信WebSDK（支持医患沟通和随访）
- **云存储**: 华为云OBS / 阿里云COS
- **微信集成**: weixin-js-sdk

## 🚀 部署指南

### 环境配置

```bash
# 开发环境
NODE_ENV=development
VUE_APP_BASE_API=https://dev-api.example.com/api

# 生产环境
NODE_ENV=production
VUE_APP_BASE_API=https://api.example.com/api
```

### 构建部署

```bash
# 构建所有项目
npm run build

# 构建产物在各个项目的 dist/ 目录
```

### Docker部署

```dockerfile
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 📈 性能优化

- **代码分割**: 按路由进行代码分割，减少首屏加载时间
- **资源压缩**: 图片压缩、JS/CSS压缩
- **缓存策略**: 合理设置浏览器缓存和CDN缓存
- **懒加载**: 图片和组件懒加载
- **Tree Shaking**: 移除未使用的代码

## 🧪 测试

```bash
# 运行所有项目测试
npm run test

# 运行单个项目测试
npm run test --scope=@caring/patient-app
```

## 📝 更新日志

### v1.0.0 (2024-01-07)

- ✨ 初始版本发布
- 🎉 完成所有6个子项目的迁移和整合
- 🔧 完成脱敏处理和环境配置
- 📚 完善文档和部署指南
- 🎯 定位为慢病管理平台，专注于院后管理和患者运营

## 🤝 贡献指南

我们欢迎所有形式的贡献，包括但不限于：

- 🐛 报告Bug
- 💡 提出新功能建议
- 📝 改进文档
- 🔧 提交代码

### 开发流程

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 提交规范

- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

## 📄 许可证

本项目采用 [MIT](LICENSE) 许可证。

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者和设计师。

## 📞 联系我们

- 项目主页: [GitHub Repository](https://github.com/xinzhics/caring-patient-platform)
- 问题反馈: [Issues](https://github.com/xinzhics/caring-patient-platform/issues)
- 邮箱: allercura_ai@caringcloud.cn
- 微信群：![微信群](docs/IMG_7686-2.JPG)

## 🔧 后端服务

后端服务基于Spring Boot和Spring Cloud构建，提供完整的医疗CRM和患者管理API接口。

### 后端技术栈

- **框架**: Spring Boot 2.2.9.RELEASE
- **微服务**: Spring Cloud (Hoxton.SR12)
- **数据库**: MySQL 8.0+
- **缓存**: Redis 6.0+
- **ORM**: MyBatis-Plus
- **安全**: OAuth2 + JWT

### 后端模块

- **caring-ucenter**: 用户中心服务
- **caring-authority**: 权限认证服务
- **caring-ai**: AI服务（语音识别、智能问答、患教内容生成）
- **caring-cms**: 内容管理服务（患教内容、健康知识库）
- **caring-wx**: 微信集成服务（私域触达）
- **caring-file**: 文件服务
- **caring-msgs**: 消息服务（短信、推送）
- **caring-nursing**: 护理服务（随访管理）

### 后端开发

详细的开发文档请参考 [packages/backend/README.md](./packages/backend/README.md)

**环境要求**:
- Java 1.8 (⚠️ 必须使用 JDK 1.8 进行编译)
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Docker & Docker Compose (用于启动依赖服务)

**快速启动后端**:

#### 第一步：启动依赖服务（Docker Compose）

```bash
cd packages/backend
docker-compose up -d
```

此命令会启动 MySQL、Nacos、Redis、XXL-Job、Nginx 等依赖服务，并自动执行数据库初始化脚本。

**首次启动需要等待 1-2 分钟，等待所有服务完全启动。**

依赖环境启动后，可以访问以下服务：
- **Nacos 控制台**: http://localhost:8848/nacos (用户名/密码: nacos/nacos)
- **MySQL**: localhost:3306 (用户名: root, 密码: change-this-password)
- **Redis**: localhost:6379 (密码: change-this-password)
- **XXL-Job**: http://localhost:8080/xxl-job-admin (用户名: admin, 密码: 123456)

#### 第二步：验证 Nacos 配置初始化

1. 访问 Nacos 控制台：http://localhost:8848/nacos
2. 登录（用户名/密码: nacos/nacos）
3. 进入"配置管理" -> "配置列表"
4. 在命名空间下拉框中找到并选择 `dev` 命名空间
5. 确认分组为 `sass-cloud`
6. 应该能看到配置文件（common.yml、mysql.yml、redis.yml 等）

#### 第三步：获取 Nacos 命名空间 UUID

⚠️ **重要**：启动后端服务前，必须确保 `config-dev.properties` 中的 `nacos.namespace` 与 Nacos 中的 `dev` 命名空间 UUID 一致。

**获取命名空间 UUID**：

**方法一：通过 Nacos 控制台**
1. 进入"命名空间"菜单
2. 找到名为 `dev` 的命名空间
3. 复制其命名空间 ID（UUID 格式）

**方法二：通过数据库查询**
```bash
docker exec -i caring-mysql mysql -uroot -pchange-this-password nacos_config -e "SELECT tenant_id FROM tenant_info WHERE tenant_name = 'dev';"
```

#### 第四步：配置 Nacos 命名空间

将获取到的命名空间 UUID 更新到 `src/main/filters/config-dev.properties` 文件中：

```properties
nacos.namespace=85d56e61-f676-11f0-a8b0-328ff568776d
nacos.group=sass-cloud
```

#### 第五步：安装依赖

⚠️ **重要**: 首次编译时，必须**优先编译** `caring-public/caring-common` 公共模块。

```bash
# 在项目根目录执行
cd caring-patient-platform
npm run backend:install
```

#### 第六步：启动后端服务

**方式一：使用启动脚本（推荐）**
```bash
cd packages/backend
./scripts/start-services.sh
```

**方式二：IDE 启动**
在 IDE（如 IntelliJ IDEA）中按顺序启动服务：
- caring-gateway（网关服务）- 端口 8760
- caring-authority（权限服务）- 端口 8764
- caring-tenant（租户服务）
- caring-ucenter（用户中心）
- 其他业务服务

**方式三：命令行启动**
```bash
cd packages/backend
mvn clean package -DskipTests
java -jar caring-gateway/caring-gateway-server/target/caring-gateway-server.jar
java -jar caring-authority/caring-authority-server/target/caring-authority-server.jar
# ... 其他服务
```

#### 第七步：验证服务启动

启动成功后，可以通过以下方式验证：

1. **检查 Nacos 服务注册**
   - 访问 Nacos 控制台：http://localhost:8848/nacos
   - 进入"服务管理" -> "服务列表"
   - 在 `dev` 命名空间下应该能看到已启动的服务

2. **访问网关**
   ```bash
   curl http://localhost:8760/api/actuator/health
   ```

3. **访问 API 文档**
   - 网关 API 文档: http://localhost:8760/api/doc.html
   - 权限服务: http://localhost:8764/doc.html

**后端API文档**:
- 网关服务: http://localhost:8760/api/doc.html
- 权限服务: http://localhost:8764/doc.html
- 文件服务: http://localhost:8765/doc.html

### 前后端协作

1. 启动后端服务
2. 配置前端API地址
3. 启动前端服务
4. 开始协作开发

详细的开发指南请参考各前端项目的README文档。

---

## 💡 核心价值

- **打破院墙限制**: 提供7x24h伴随式健康服务，延伸医疗服务场景
- **提升患者依从性**: 通过随访计划、用药提醒、健康指导，提高患者治疗依从性
- **私域运营能力**: 全渠道患者触达，支持精准营销和患者分层运营
- **数据驱动决策**: 丰富的数据分析和报表，支持精细化运营管理
- **合规安全**: 强调非诊疗场景，专注于健康咨询、随访管理和患者服务，规避医疗合规风险

⭐ 如果这个项目对您有帮助，请给我们一个Star！
