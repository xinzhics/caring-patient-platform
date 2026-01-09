# 医生端应用 (@caring/doctor-app)

慢病管理平台的医生端移动应用，为医生提供患者管理、随访计划制定、病例讨论和会诊等功能。

## 功能特性

- 👥 患者管理和随访计划制定
- 🩺 病例讨论和会诊功能
- 📊 统计分析和数据报表
- 📝 常用语库管理
- 📢 群发消息功能
- 📝 富文本编辑器
- ☁️ 华为云存储集成

## 技术栈

- **框架**: Vue 2.7.16
- **UI组件**: Vux 2.2.0 + Vant 2.13.2
- **状态管理**: Vuex 2.1.1
- **路由**: Vue Router 3.0.1
- **构建工具**: Webpack 3.6.0
- **富文本编辑**: wangeditor 4.7.15
- **云存储**: 华为云OBS SDK 3.22.3
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
├── service/             # 工具服务
├── view/                # 页面组件
├── App.vue              # 根组件
└── main.js              # 入口文件
```

## 配置说明

### API配置

API地址通过环境变量配置，开发环境和生产环境使用不同的API地址：

```javascript
// src/api/baseUrl.js
const apiUrl = process.env.NODE_ENV === 'development' 
  ? 'https://dev-api.example.com/api' 
  : 'https://api.example.com/api'
```

### 华为云OBS配置

华为云对象存储服务配置：

```javascript
// 华为云OBS初始化
const obsClient = new ObsClient({
  access_key_id: 'your-access-key',
  secret_access_key: 'your-secret-key',
  server: 'https://your-endpoint.obs.cn-north-4.myhuaweicloud.com'
})
```

### 富文本编辑器

使用wangeditor作为富文本编辑器：

```javascript
// 引入编辑器
import E from 'wangeditor'

// 创建编辑器实例
const editor = new E('#editor')
```

## 开发指南

### 添加新页面

1. 在 `src/view/` 目录下创建页面组件
2. 在 `src/router/index.js` 中添加路由配置
3. 如需API调用，在 `src/api/` 中添加接口定义

### 文件上传

```javascript
// 使用华为云OBS上传文件
obsClient.putObject({
  Bucket: 'your-bucket-name',
  Key: 'object-key',
  SourceFile: file
}, (err, result) => {
  if (err) {
    console.error('上传失败:', err)
  } else {
    console.log('上传成功:', result)
  }
})
```

### 富文本内容处理

```javascript
// 编辑器内容获取
const content = editor.txt.html()

// 内容处理
const processedContent = content.replace(/<img[^>]*>/g, (match) => {
  // 处理图片标签
  return match
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

- 本项目专为微信环境优化
- 富文本编辑器需要配合后端接口使用
- 华为云OBS需要配置相应的访问密钥
- 建议使用CDN加速静态资源访问

## 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](../../../LICENSE) 文件了解详情。