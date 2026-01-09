# 医助端应用 (@caring/assistant-app)

慢病管理平台的医助端H5应用，为医助人员提供患者信息管理、随访协助、预约管理等功能。

## 功能特性

- 👥 患者信息管理和随访协助
- 📅 预约管理和转诊功能
- 📰 新闻推送和内容管理
- 📊 统计报表和数据监控
- 📝 常用语和模板管理
- 🖼️ 图片压缩和上传
- 📈 数据可视化图表

## 技术栈

- **框架**: Vue 2.5.2
- **UI组件**: Vant 2.11.0
- **HTTP客户端**: Axios 0.27.2
- **图表库**: ECharts 5.4.0
- **富文本编辑**: wangeditor 4.7.15
- **云存储**: 华为云OBS SDK 3.22.3
- **图片处理**: vue-cropper 0.5.8 + compressorjs 1.1.1
- **懒加载**: vue-lazyload 3.0.0

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

### 代码检查

```bash
npm run lint
```

### 单元测试

```bash
npm run unit
```

## 项目结构

```
src/
├── api/                 # API接口
├── assets/              # 静态资源
├── components/          # 公共组件
├── router/              # 路由配置
├── utils/               # 工具函数
├── views/               # 页面组件
├── App.vue              # 根组件
└── main.js              # 入口文件
```

## 配置说明

### API配置

API地址通过环境变量配置，开发环境和生产环境使用不同的API地址：

```javascript
// src/api/baseUrl.js
const apiUrl = process.env.NODE_ENV === 'development' 
  ? 'https://dev-api.example.com/' 
  : 'https://api.example.com/'
```

### 华为云OBS配置

```javascript
// 华为云OBS初始化
const obsClient = new ObsClient({
  access_key_id: 'your-access-key',
  secret_access_key: 'your-secret-key',
  server: 'https://your-endpoint.obs.cn-north-4.myhuaweicloud.com'
})
```

## 开发指南

### 使用Vant组件

```javascript
// 按需引入Vant组件
import { Button, Cell, CellGroup } from 'vant'

Vue.use(Button)
Vue.use(Cell)
Vue.use(CellGroup)
```

### 图片上传和压缩

```javascript
// 图片压缩
import Compressor from 'compressorjs'

new Compressor(file, {
  quality: 0.6,
  success(result) {
    // 压缩后的图片
    const compressedFile = result
  }
})
```

### 图表使用

```javascript
// 使用ECharts
import * as echarts from 'echarts'

const chart = echarts.init(document.getElementById('chart'))
chart.setOption({
  // 图表配置
})
```

### 富文本编辑器

```javascript
// 使用wangeditor
import E from 'wangeditor'

const editor = new E('#editor')
editor.create()
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

- 本项目使用Vant UI组件库，确保样式正确引入
- 图片上传功能需要配置云存储服务
- 图表组件需要预留足够的容器空间
- 移动端适配建议使用rem布局

## 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](../../../LICENSE) 文件了解详情。