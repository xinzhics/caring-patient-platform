# Caring Healthcare CRM & Patient Private Domain Platform

[English](./README_EN.md) | [中文](./README.md)

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Node](https://img.shields.io/badge/node-%3E%3D16.0.0-green.svg)
![Vue](https://img.shields.io/badge/vue-2.6%20%7C%203.5-brightgreen.svg)
![TypeScript](https://img.shields.io/badge/typescript-4.5+-blue.svg)

A complete full-stack solution for healthcare CRM and patient private domain services, including patient app, doctor app, assistant app, consultation app, patient management platform, and admin dashboard. Focused on post-discharge management, patient follow-up, private domain operations, and full-lifecycle health services, providing digital patient management capabilities for pharmaceutical companies, private healthcare institutions, and wellness organizations. Building the Salesforce for the healthcare industry.

## 🌟 Key Features

- **Healthcare CRM Core**: Professional patient relationship management system with patient tagging, segmentation, and precision marketing
- **Private Domain Operations**: Omnichannel patient engagement supporting IM, SMS, WeChat, and other channels to improve patient stickiness
- **Post-Discharge Management**: Complete follow-up plans, medication reminders, and rehabilitation guidance. Break hospital walls and provide 7x24h accompanying health services
- **Multi-Platform Support**: Full coverage including patient app, doctor app, assistant app, and admin dashboard to meet different collaboration needs
- **Modern Tech Stack**: Vue 2/3 + TypeScript + Vite + Element UI + Vant
- **Mobile-First**: Optimized for mobile devices with smooth mobile experience
- **Real-time Communication**: Integrated Easemob SDK supporting audio/video calls and instant messaging for efficient doctor-patient collaboration
- **Data Visualization**: Rich chart components for intuitive display of patient data and operational metrics
- **Permission Management**: RBAC-based fine-grained permission control with multi-tenant deployment support

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│              Caring Healthcare CRM Platform                 │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────┐ │
│  │  Patient    │ │   Doctor    │ │  Assistant  │ │Consult. │ │
│  │    App      │ │    App      │ │    App      │ │   App   │ │
│  │  (H5 App)   │ │  (H5 App)   │ │  (H5 App)   │ │(H5 App) │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────┘ │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐ ┌─────────────┐                             │
│  │  Patient    │ │   Admin     │                             │
│  │ Management  │ │  Dashboard  │                             │
│  │  Platform   │ │  (Web App)  │                             │
│  └─────────────┘ └─────────────┘                             │
├─────────────────────────────────────────────────────────────┤
│                    Shared Components                        │
├─────────────────────────────────────────────────────────────┤
│                    Backend API Services                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐             │
│  │  Patient    │ │   Auth      │ │     AI      │             │
│  │  Service    │ │  Service    │ │   Service   │             │
│  └─────────────┘ └─────────────┘ └─────────────┘             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐             │
│  │    CMS      │ │   WeChat    │ │    File     │             │
│  │  Service    │ │  Service    │ │   Service   │             │
│  └─────────────┘ └─────────────┘ └─────────────┘             │
├─────────────────────────────────────────────────────────────┤
│                    Data Storage Layer                       │
│  ┌─────────────┐ ┌─────────────┐                             │
│  │  MySQL 8.0  │ │  Redis 6.0  │                             │
│  └─────────────┘ └─────────────┘                             │
└─────────────────────────────────────────────────────────────┘
```

## 📦 Project Structure

```
caring-patient-platform/
├── packages/
│   ├── backend/              # Backend services
│   │   ├── caring-ai/        # AI service
│   │   ├── caring-authority/ # Authentication & authorization
│   │   ├── caring-cms/       # Content management
│   │   ├── caring-file/      # File service
│   │   ├── caring-ucenter/   # User center
│   │   ├── caring-wx/        # WeChat integration
│   │   └── ...               # Other services
│   ├── patient-app/          # Patient app
│   ├── doctor-app/           # Doctor app
│   ├── assistant-app/        # Assistant app
│   ├── consultation-app/     # Consultation app
│   ├── patient-manage-app/   # Patient management platform
│   ├── admin-ui/             # Admin dashboard
│   └── shared-components/    # Shared component library
├── docs/                     # Documentation
├── scripts/                  # Build and deployment scripts
├── .github/                  # GitHub Actions CI/CD
├── package.json              # Root package.json
├── lerna.json                # Lerna configuration
└── README.md                 # Project description
```

## 🚀 Quick Start

### Requirements

**Frontend**:
- Node.js >= 16.0.0
- npm >= 8.0.0

**Backend**:
- Java 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### Install Dependencies

```bash
# Install frontend dependencies
npm install
npm run bootstrap

# Install backend dependencies
npm run backend:install
```

### Development

**Frontend Development**:
```bash
# Start all frontend projects
npm run dev

# Start single frontend project
npm run dev --scope=@caring/patient-app
```

**Backend Development**:
```bash
# Start backend services
npm run backend:run

# Or use Maven
cd packages/backend
mvn spring-boot:run
```

### Quick Start (Docker)

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f
```

### Build

```bash
# Build frontend projects
npm run build

# Build backend projects
npm run backend:build
```

### Code Check

```bash
# Check frontend code
npm run lint

# Run backend tests
npm run backend:test
```

## 📱 Application Overview

### Patient App (@caring/patient-app)

Mobile app for patients, providing health record management, follow-up management, and health consultation services. It's the entry point for patients to connect with healthcare service providers.

**Tech Stack**: Vue 2.5.2 + Vux 2.2.0 + Vuex + Vue Router

**Key Features**:
- 🏥 Health record management
- 📋 Follow-up plans and health monitoring
- 💬 Online health consultation (IM chat)
- 📝 Questionnaires and data recording
- 📅 Follow-up management and follow-up reminders

### Doctor App (@caring/doctor-app)

Mobile app for doctors, providing patient management, doctor-patient collaboration, and case discussion features. It's the core tool for doctors to provide patient services and follow-up management.

**Tech Stack**: Vue 2.7.16 + Vux 2.2.0 + Vuex + Vue Router

**Key Features**:
- 👥 Patient management and segmentation operations
- 🩺 Case discussion and expert collaboration
- 📊 Statistical analysis and operational reports
- 📝 Common phrases and patient education content management
- 📢 Bulk messaging and precision marketing

### Assistant App (@caring/assistant-app)

Mobile app for medical assistants, assisting doctors in patient services and follow-up management to improve service efficiency.

**Tech Stack**: Vue 2.5.2 + Vant 2.11.0 + Axios

**Key Features**:
- 👥 Patient information management and follow-up assistance
- 📅 Follow-up plan execution and health guidance
- 📰 Patient education content push and health management
- 📊 Statistical reports and data monitoring
- 📝 Common phrases and template management

### Consultation App (@caring/consultation-app)

Open case discussion and medical collaboration platform supporting multi-party medical experts for case exchange and professional discussion.

**Tech Stack**: Vue 2.5.2 + Ant Design Vue + Easemob SDK

**Key Features**:
- 🩺 Case discussion and expert collaboration
- 📹 Real-time audio/video communication
- 📄 Document sharing and collaboration
- 👨‍⚕️ Professional consultation features

### Patient Management Platform (@caring/patient-manage-app)

Professional patient data management and operations platform, focusing on patient tagging system, data quality monitoring, and精细化 operations.

**Tech Stack**: Vue 3.5.13 + Vant 3.4.5 + Vite + TypeScript

**Key Features**:
- 📊 Patient tagging system and segmentation management
- 🔍 Anomaly data monitoring and processing
- 💊 Medication management and adherence analysis
- 📈 Health monitoring data analysis
- 🏢 Administrative management and operational statistics

### Admin Dashboard (@caring/admin-ui)

Platform operations management dashboard supporting system configuration, management, and private domain operations.

**Tech Stack**: Vue 2.6.10 + Element UI 2.12.0

**Key Features**:
- 🏢 Tenant management and permission control
- 👥 User and organization management
- 📝 Content management system (CMS) and patient education content
- ⚙️ System configuration and monitoring
- 📱 SMS, messaging, and private domain engagement management

### Shared Components (@caring/shared-components)

Common component and utility library providing unified UI components and utility methods.

**Key Features**:
- 🎨 Unified UI component design
- 🔧 Common utility functions
- 📱 Mobile-adaptive components
- 🌐 Multi-framework support (Vue 2/3)
- 📊 Chart component wrappers

## 🛠️ Development Guide

### Technical Standards

- **Code Style**: ESLint + Prettier for code standardization
- **Commit Convention**: Follow Conventional Commits specification
- **Branch Strategy**: Git Flow workflow
- **Version Management**: Lerna for multi-package version management

### Component Development

```vue
<template>
  <div class="caring-component">
    <!-- Component content -->
  </div>
</template>

<script>
export default {
  name: 'CaringComponent',
  props: {
    // Props definition
  },
  data() {
    return {
      // Data definition
    }
  }
}
</script>

<style scoped>
.caring-component {
  /* Component styles */
}
</style>
```

### API Calls

```javascript
// Use shared component library request method
import { request } from '@caring/shared-components'

// GET request
request.get('/api/patients').then(data => {
  console.log(data)
})

// POST request
request.post('/api/patients', patientData).then(data => {
  console.log(data)
})
```

### Permission Control

```javascript
// Use permission directive
<template>
  <button v-permission="'patient:edit'">Edit Patient</button>
</template>

// Use permission method
import { hasPermission } from '@caring/shared-components'

if (hasPermission('patient:edit')) {
  // Permission logic
}
```

## 📊 Tech Stack Details

### Frontend Frameworks

| Project | Vue Version | Build Tool | UI Framework | State Management |
|---------|-------------|------------|--------------|------------------|
| Patient App | 2.5.2 | Webpack 3.6.0 | Vux 2.2.0 | Vuex 2.1.1 |
| Doctor App | 2.7.16 | Webpack 3.6.0 | Vux 2.2.0 | Vuex 2.1.1 |
| Assistant App | 2.5.2 | Webpack 3.6.0 | Vant 2.11.0 | - |
| Consultation App | 2.5.2 | Webpack 3.6.0 | Ant Design Vue 1.7.2 | Vuex 2.1.1 |
| Patient Management | 3.5.13 | Vite 2.8.0 | Vant 3.4.5 | Pinia 2.0.11 |
| Admin Dashboard | 2.6.10 | Vue CLI 4.5.7 | Element UI 2.12.0 | Vuex 3.1.0 |

### Core Dependencies

- **HTTP Client**: Axios
- **Chart Library**: ECharts
- **Rich Text Editor**: wangeditor
- **Audio/Video Communication**: Easemob WebRTC SDK (supports health consultation and collaboration)
- **Instant Messaging**: Easemob WebSDK (supports doctor-patient communication and follow-up)
- **Cloud Storage**: Huawei Cloud OBS / Alibaba Cloud COS
- **WeChat Integration**: weixin-js-sdk

## 🚀 Deployment Guide

### Environment Configuration

```bash
# Development environment
NODE_ENV=development
VUE_APP_BASE_API=https://dev-api.example.com/api

# Production environment
NODE_ENV=production
VUE_APP_BASE_API=https://api.example.com/api
```

### Build & Deploy

```bash
# Build all projects
npm run build

# Build artifacts are in the dist/ directory of each project
```

### Docker Deployment

```dockerfile
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 📈 Performance Optimization

- **Code Splitting**: Route-based code splitting to reduce initial load time
- **Resource Compression**: Image compression, JS/CSS compression
- **Caching Strategy**: Proper browser cache and CDN cache settings
- **Lazy Loading**: Image and component lazy loading
- **Tree Shaking**: Remove unused code

## 🧪 Testing

```bash
# Run all project tests
npm run test

# Run single project test
npm run test --scope=@caring/patient-app
```

## 📝 Changelog

### v1.0.0 (2024-01-07)

- ✨ Initial release
- 🎉 Completed migration and integration of all 6 sub-projects
- 🔧 Completed data anonymization and environment configuration
- 📚 Improved documentation and deployment guide
- 🎯 Positioned as healthcare CRM and patient private domain service platform, focusing on post-discharge management and patient operations

## 🤝 Contributing

We welcome all forms of contributions, including but not limited to:

- 🐛 Bug reports
- 💡 New feature suggestions
- 📝 Documentation improvements
- 🔧 Code submissions

### Development Workflow

1. Fork this project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'feat: Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Create a Pull Request

### Commit Convention

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation update
- `style`: Code formatting
- `refactor`: Code refactoring
- `test`: Testing related
- `chore`: Build process or auxiliary tool changes

## 📄 License

This project is licensed under the [MIT](LICENSE) license.

## 🙏 Acknowledgments

Thanks to all developers and designers who contributed to this project.

## 📞 Contact Us

- Project Homepage: [GitHub Repository](https://github.com/xinzhics/caring-patient-platform)
- Issue Tracker: [Issues](https://github.com/xinzhics/caring-patient-platform/issues)
- Email: allercura_ai@caringcloud.cn
- WeChat Group: ![WeChat Group](docs/IMG_7686-2.JPG)

## 🔧 Backend Services

Backend services built with Spring Boot and Spring Cloud, providing complete healthcare CRM and patient management API interfaces.

### Backend Tech Stack

- **Framework**: Spring Boot 2.2.9.RELEASE
- **Microservices**: Spring Cloud (Hoxton.SR12)
- **Database**: MySQL 8.0+
- **Cache**: Redis 6.0+
- **ORM**: MyBatis-Plus
- **Security**: OAuth2 + JWT

### Backend Modules

- **caring-ucenter**: User center service
- **caring-authority**: Authentication and authorization service
- **caring-ai**: AI service (speech recognition, intelligent Q&A, patient education content generation)
- **caring-cms**: Content management service (patient education content, health knowledge base)
- **caring-wx**: WeChat integration service (private domain engagement)
- **caring-file**: File service
- **caring-msgs**: Message service (SMS, push notifications)
- **caring-nursing**: Nursing service (follow-up management)

### Backend Development

For detailed development documentation, please refer to [packages/backend/README.md](./packages/backend/README.md)

**Requirements**:
- Java 1.8 (⚠️ Must use JDK 1.8 for compilation)
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Docker & Docker Compose (for starting dependency services)

**Quick Start Backend**:

#### Step 1: Start Dependency Services (Docker Compose)

```bash
cd packages/backend
docker-compose up -d
```

This command will start MySQL, Nacos, Redis, XXL-Job, Nginx and other dependency services, and automatically execute database initialization scripts.

**Wait 1-2 minutes for the first startup to allow all services to fully start.**

After the dependency environment starts, you can access the following services:
- **Nacos Console**: http://localhost:8848/nacos (username/password: nacos/nacos)
- **MySQL**: localhost:3306 (username: root, password: change-this-password)
- **Redis**: localhost:6379 (password: change-this-password)
- **XXL-Job**: http://localhost:8080/xxl-job-admin (username: admin, password: 123456)

#### Step 2: Verify Nacos Configuration Initialization

1. Access Nacos Console: http://localhost:8848/nacos
2. Login (username/password: nacos/nacos)
3. Go to "Configuration Management" -> "Configuration List"
4. Find and select the `dev` namespace in the namespace dropdown
5. Confirm the group is `sass-cloud`
6. You should see configuration files (common.yml, mysql.yml, redis.yml, etc.)

#### Step 3: Get Nacos Namespace UUID

⚠️ **Important**: Before starting backend services, ensure that the `nacos.namespace` in `config-dev.properties` matches the `dev` namespace UUID in Nacos.

**Get Namespace UUID**:

**Method 1: Through Nacos Console**
1. Go to "Namespace" menu
2. Find the namespace named `dev`
3. Copy its namespace ID (UUID format)

**Method 2: Through Database Query**
```bash
docker exec -i caring-mysql mysql -uroot -pchange-this-password nacos_config -e "SELECT tenant_id FROM tenant_info WHERE tenant_name = 'dev';"
```

#### Step 4: Configure Nacos Namespace

Update the obtained namespace UUID to the `src/main/filters/config-dev.properties` file:

```properties
nacos.namespace=85d56e61-f676-11f0-a8b0-328ff568776d
nacos.group=sass-cloud
```

#### Step 5: Install Dependencies

⚠️ **Important**: For the first compilation, you must **prioritize compiling** the `caring-public/caring-common` common module.

```bash
# Execute from project root directory
cd caring-patient-platform
npm run backend:install
```

#### Step 6: Start Backend Services

**Method 1: Using Startup Script (Recommended)**
```bash
cd packages/backend
./scripts/start-services.sh
```

**Method 2: IDE Startup**
Start services in order in your IDE (like IntelliJ IDEA):
- caring-gateway (Gateway service) - Port 8760
- caring-authority (Authority service) - Port 8764
- caring-tenant (Tenant service)
- caring-ucenter (User center)
- Other business services

**Method 3: Command Line Startup**
```bash
cd packages/backend
mvn clean package -DskipTests
java -jar caring-gateway/caring-gateway-server/target/caring-gateway-server.jar
java -jar caring-authority/caring-authority-server/target/caring-authority-server.jar
# ... other services
```

#### Step 7: Verify Service Startup

After successful startup, verify through the following methods:

1. **Check Nacos Service Registration**
   - Access Nacos Console: http://localhost:8848/nacos
   - Go to "Service Management" -> "Service List"
   - You should see started services under the `dev` namespace

2. **Access Gateway**
   ```bash
   curl http://localhost:8760/api/actuator/health
   ```

3. **Access API Documentation**
   - Gateway API Docs: http://localhost:8760/api/doc.html
   - Authority Service: http://localhost:8764/doc.html

**Backend API Documentation**:
- Gateway Service: http://localhost:8760/api/doc.html
- Authority Service: http://localhost:8764/doc.html
- File Service: http://localhost:8765/doc.html

### Frontend-Backend Collaboration

1. Start backend services
2. Configure frontend API address
3. Start frontend services
4. Begin collaborative development

For detailed development guide, please refer to the README documentation of each frontend project.

---

## 💡 Core Value Proposition

- **Break Hospital Walls**: Provide 7x24h accompanying health services, extending healthcare service scenarios
- **Improve Patient Adherence**: Increase patient treatment adherence through follow-up plans, medication reminders, and health guidance
- **Private Domain Operations**: Omnichannel patient engagement supporting precision marketing and patient segmentation operations
- **Data-Driven Decisions**: Rich data analysis and reporting supporting精细化 operations management
- **Compliance & Safety**: Focus on non-diagnostic scenarios, specializing in health consultation, follow-up management, and patient services to avoid medical compliance risks

⭐ If this project helps you, please give us a Star!
