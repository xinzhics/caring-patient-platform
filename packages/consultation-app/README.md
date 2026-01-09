# 病例讨论端应用 (@caring/consultation-app)

慢病管理平台的病例讨论(游客)端应用，为医生提供病例讨论、会诊和实时音视频通信功能。

## 功能特性

- 🩺 病例讨论和会诊
- 📹 实时音视频通信
- 📄 文档共享和协作
- 👨‍⚕️ 专家咨询功能
- 📊 医疗数据可视化
- 📱 移动端适配
- 🔒 游客访问权限控制

## 技术栈

- **框架**: Vue 2.5.2
- **UI组件**: Ant Design Vue 1.7.2 + Vux 2.2.0 + Vant 2.11.0
- **状态管理**: Vuex 2.1.1
- **路由**: Vue Router 3.0.1
- **音视频通信**: 环信WebRTC SDK
- **即时通讯**: 环信WebSDK 3.3.2
- **图表库**: ECharts 4.9.0
- **HTTP客户端**: Axios 0.18.0
- **微信集成**: weixin-js-sdk 1.6.0

## 快速开始

### 环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0

### 安装依赖

```bash
npm install
```

### 开发环境运行

```bash
npm run dev
```

### 生产环境构建

```bash
npm run build
```

## 项目结构

```
src/
├── api/                 # API接口
├── assets/              # 静态资源
├── components/          # 公共组件
├── router/              # 路由配置
├── view/                # 页面组件
├── App.vue              # 根组件
└── main.js              # 入口文件
```

## 配置说明

### API配置

API地址通过环境变量配置，开发环境和生产环境使用不同的API地址：

```javascript
// src/api/Content.js
const apiUrl = process.env.NODE_ENV === 'development' 
  ? 'https://dev-api.example.com' 
  : 'https://api.example.com'
```

### 环信SDK配置

```javascript
// 环信WebSDK初始化
const WebIM = require('easemob-websdk')

const conn = new WebIM.connection({
  apiUrl: 'https://a1.easemob.com',
  user: '',
  pwd: '',
  appKey: 'your-app-key',
})
```

### 音视频配置

```javascript
// 环信WebRTC初始化
const emedia = require('easemob-emedia')

emedia.config({
  username: 'username',
  userToken: 'token',
  appKey: 'your-app-key',
})
```

## 开发指南

### 使用Ant Design Vue组件

```javascript
// 按需引入组件
import { Button, Table, Modal } from 'ant-design-vue'

Vue.use(Button)
Vue.use(Table)
Vue.use(Modal)
```

### 音视频通话

```javascript
// 发起音视频通话
emedia.makeCall({
  type: 'video', // 'video' 或 'audio'
  to: 'target-user-id',
})
```

### 即时通讯

```javascript
// 发送消息
conn.sendTextMessage({
  to: 'target-user-id',
  msg: 'message-content',
  type: 'chat',
})
```

### 图表使用

```javascript
// 使用ECharts
import echarts from 'echarts'

const chart = echarts.init(document.getElementById('chart'))
chart.setOption({
  // 图表配置
})
```

## 部署说明

### 环境变量

创建 `.env.development` 和 `.env.production` 文件：

```bash
# .env.development
NODE_ENV=development

# .env.production
NODE_ENV=production
```

### 构建部署

```bash
# 构建生产版本
npm run build

# 构建产物在 dist/ 目录
```

## 注意事项

- 音视频功能需要HTTPS环境
- 环信SDK需要配置相应的AppKey
- 浏览器需要支持WebRTC
- 建议使用Chrome浏览器获得最佳体验
- 移动端需要处理权限请求

## 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](../../../LICENSE) 文件了解详情。