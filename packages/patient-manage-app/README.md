# 患者管理平台 (@caring/patient-manage-app)

慢病管理平台的医助患者管理平台H5应用，提供专业的患者数据管理、异常数据监控和用药管理功能。

## 功能特性

- 📊 患者数据完整性管理
- 🔍 异常数据监控和处理
- 💊 用药管理和提醒
- 📈 健康监测数据分析
- 🏢 行政管理和统计
- 📋 数据质量监控
- 📱 移动端优化
- 🔐 权限控制和租户隔离

## 技术栈

- **框架**: Vue 3.5.13
- **构建工具**: Vite 2.8.0
- **语言**: TypeScript 4.5.4
- **UI组件**: Vant 3.4.5
- **状态管理**: Pinia 2.0.11
- **路由**: Vue Router 4.0.12
- **HTTP客户端**: Axios 0.26.0
- **图表库**: ECharts 5.3.2 + Chart.js 3.7.1
- **移动端适配**: amfe-flexible 2.2.1
- **云存储**: 腾讯云COS SDK 1.3.6

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

### 测试环境

```bash
npm run test
```

### 生产环境构建

```bash
npm run build:prod
```

### 测试环境构建

```bash
npm run build:test
```

### 预览构建结果

```bash
npm run preview
```

## 项目结构

```
src/
├── api/                 # API接口
├── assets/              # 静态资源
├── components/          # 公共组件
├── plugins/             # 插件配置
├── router/              # 路由配置
├── store/               # 状态管理
├── utils/               # 工具函数
├── views/               # 页面组件
├── App.vue              # 根组件
└── main.ts              # 入口文件
```

## 配置说明

### 环境变量

项目支持多环境配置：

```bash
# .env.development (开发环境)
VITE_AXIOS_BASE_URL=https://dev-api.example.com/api
VITE_PUBLIC_PATH=/
VITE_PORT=9085
VITE_PROXY=https://dev-api.example.com

# .env.production (生产环境)
VITE_AXIOS_BASE_URL=https://api.example.com/api
VITE_PUBLIC_PATH=/
VITE_PORT=9085
VITE_PROXY=https://api.example.com

# .env.test (测试环境)
VITE_AXIOS_BASE_URL=https://test-api.example.com/api
VITE_PUBLIC_PATH=/
VITE_PORT=9085
VITE_PROXY=https://test-api.example.com
```

### HTTP请求配置

使用Axios进行HTTP请求，支持请求拦截和响应拦截：

```typescript
// src/utils/http.ts
import http from '@/utils/http'

// GET请求
http.get('/api/endpoint')

// POST请求
http.post('/api/endpoint', data)
```

### 状态管理

使用Pinia进行状态管理：

```typescript
// src/store/example.ts
import { defineStore } from 'pinia'

export const useExampleStore = defineStore('example', {
  state: () => ({
    count: 0
  }),
  actions: {
    increment() {
      this.count++
    }
  }
})
```

## 开发指南

### 使用Vant组件

项目使用unplugin-vue-components自动按需引入Vant组件：

```vue
<template>
  <van-button type="primary">按钮</van-button>
  <van-cell title="单元格" value="内容" />
</template>
```

### 图表使用

```typescript
// 使用ECharts
import * as echarts from 'echarts'

const chart = echarts.init(document.getElementById('chart'))
chart.setOption({
  // 图表配置
})

// 使用Chart.js
import { Chart } from 'chart.js'

const ctx = document.getElementById('canvas') as HTMLCanvasElement
new Chart(ctx, {
  type: 'bar',
  data: {
    labels: ['Red', 'Blue', 'Yellow', 'Green'],
    datasets: [{
      label: '# of Votes',
      data: [12, 19, 3, 5],
      backgroundColor: [
        'rgba(255, 99, 132, 0.2)',
        'rgba(54, 162, 235, 0.2)'
      ]
    }]
  }
})
```

### 文件上传

```typescript
// 使用腾讯云COS上传
import COS from 'cos-js-sdk-v5'

const cos = new COS({
  SecretId: 'your-secret-id',
  SecretKey: 'your-secret-key'
})

cos.uploadFile({
  Bucket: 'your-bucket',
  Region: 'your-region',
  Key: 'file-key',
  FilePath: file
}, (err, data) => {
  console.log(err || data)
})
```

## 部署说明

### 构建配置

项目使用Vite进行构建，支持多环境构建：

```bash
# 开发环境
npm run dev

# 生产环境构建
npm run build:prod

# 测试环境构建
npm run build:test
```

### 移动端适配

项目使用amfe-flexible进行移动端适配：

```javascript
// main.ts
import 'amfe-flexible'
```

### 代理配置

开发环境支持API代理：

```typescript
// vite.config.ts
server: {
  proxy: {
    '^/api/.*': {
      changeOrigin: true,
      target: 'https://dev-api.example.com',
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

## 注意事项

- 本项目使用Vue 3 + TypeScript开发
- 使用Vite构建工具，启动速度快
- 支持多环境配置和部署
- 移动端适配已优化
- 图表组件需要预留容器空间
- 文件上传需要配置云存储服务

## 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](../../../LICENSE) 文件了解详情。