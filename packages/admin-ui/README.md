# 管理后台 (@caring/admin-ui)

患者私域管理平台的管理后台应用，提供租户管理、权限控制、内容管理和系统配置等功能。

## 功能特性

- 🏢 租户管理和权限控制
- 👥 用户和组织管理
- 📝 内容管理系统(CMS)
- ⚙️ 系统配置和监控
- 📱 短信和消息管理
- 📊 数据统计和报表
- 🎨 主题定制
- 🌐 国际化支持

## 技术栈

- **框架**: Vue 2.6.10
- **UI组件**: Element UI 2.12.0
- **状态管理**: Vuex 3.1.0
- **路由**: Vue Router 3.0.2
- **构建工具**: Vue CLI 4.5.7
- **HTTP客户端**: Axios 0.19.0
- **图表库**: ECharts 4.2.1
- **富文本编辑**: tui-editor 1.3.3
- **国际化**: Vue-i18n 7.3.2

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
npm run build:prod
```

### 其他环境构建

```bash
# 预发布环境
npm run build:stage

# Boot环境
npm run build:boot

# Docker环境
npm run build:docker
```

### 预览构建结果

```bash
npm run preview
```

### 代码检查

```bash
npm run lint
```

### SVG图标优化

```bash
npm run svgo
```

### 生成新组件

```bash
npm run new
```

## 项目结构

```
src/
├── api/                 # API接口
├── assets/              # 静态资源
├── components/          # 公共组件
├── directive/           # 自定义指令
├── filters/             # 过滤器
├── icons/               # SVG图标
├── lang/                # 国际化文件
├── layout/              # 布局组件
├── router/              # 路由配置
├── store/               # 状态管理
├── styles/              # 样式文件
├── utils/               # 工具函数
├── views/               # 页面组件
├── App.vue              # 根组件
├── main.js              # 入口文件
└── settings.js          # 全局配置
```

## 配置说明

### 环境变量

项目支持多环境配置：

```bash
# .env.development (开发环境)
NODE_ENV=development
VUE_APP_PROJECT_NAME=dist
VUE_APP_BASE_API=/api
VUE_APP_DEV_REQUEST_DOMAIN_PREFIX=https://dev-api.example.com
VUE_APP_PROD_REQUEST_DOMAIN_PREFIX=https://api.example.com

# .env.production (生产环境)
NODE_ENV=production
VUE_APP_PROJECT_NAME=dist
VUE_APP_BASE_API=/api
VUE_APP_DEV_REQUEST_DOMAIN_PREFIX=https://dev-api.example.com
VUE_APP_PROD_REQUEST_DOMAIN_PREFIX=https://api.example.com

# .env.staging (预发布环境)
NODE_ENV=production
VUE_APP_PROJECT_NAME=dist-staging
VUE_APP_BASE_API=/api
VUE_APP_DEV_REQUEST_DOMAIN_PREFIX=https://staging-api.example.com
VUE_APP_PROD_REQUEST_DOMAIN_PREFIX=https://staging-api.example.com
```

### 代理配置

开发环境支持API代理：

```javascript
// vue.config.js
devServer: {
  proxy: {
    '/api': {
      target: 'https://dev-api.example.com',
      changeOrigin: true,
      pathRewrite: {
        '^/api': '/api'
      }
    }
  }
}
```

## 开发指南

### 使用Element UI组件

```javascript
// 全局引入
import ElementUI from 'element-ui'
Vue.use(ElementUI)

// 按需引入
import { Button, Table } from 'element-ui'
Vue.use(Button)
Vue.use(Table)
```

### 权限控制

```javascript
// 路由权限配置
{
  path: '/example',
  name: 'Example',
  component: () => import('@/views/example'),
  meta: {
    title: '示例页面',
    roles: ['admin', 'editor'] // 可访问该页面的角色
  }
}
```

### 国际化

```javascript
// 使用i18n
this.$t('common.confirm')
this.$t('user.profile')

// 切换语言
this.$i18n.locale = 'en'
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

### 富文本编辑

```javascript
// 使用tui-editor
import Editor from '@toast-ui/vue-editor'

export default {
  components: {
    Editor
  }
}
```

## 部署说明

### 构建配置

项目使用Vue CLI进行构建，支持多环境构建：

```bash
# 开发环境
npm run dev

# 生产环境构建
npm run build:prod
```

### Nginx配置

```nginx
server {
  listen 80;
  server_name your-domain.com;
  root /path/to/dist;
  index index.html;

  location / {
    try_files $uri $uri/ /index.html;
  }

  location /api {
    proxy_pass https://api.example.com;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
  }
}
```

### Docker部署

```dockerfile
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 注意事项

- 本项目基于vue-element-admin构建
- 支持多租户架构
- 权限控制基于RBAC模型
- 图标使用SVG sprite方式
- 建议使用Chrome浏览器获得最佳体验
- 生产环境需要配置HTTPS

## 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](../../../LICENSE) 文件了解详情。