#!/bin/bash

# 患者私域管理平台快速启动脚本

echo "🚀 正在启动患者私域管理平台..."

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker未安装，请先安装Docker"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose未安装，请先安装Docker Compose"
    exit 1
fi

# 检查.env文件
if [ ! -f .env ]; then
    echo "⚠️  未找到.env文件，从.env.example创建..."
    cp .env.example .env
    echo "📝 请修改.env文件中的配置，特别是密码相关配置"
    echo "⚠️  修改完成后请重新运行此脚本"
    exit 0
fi

# 创建必要的目录
echo "📁 创建必要的目录..."
mkdir -p uploads logs

# 停止现有容器
echo "🛑 停止现有容器..."
docker-compose down

# 构建并启动服务
echo "🔨 构建并启动服务..."
docker-compose up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 10

# 检查服务状态
echo "📊 检查服务状态..."
docker-compose ps

# 显示访问地址
echo ""
echo "✅ 服务启动完成！"
echo ""
echo "📱 访问地址："
echo "   - 前端应用: http://localhost"
echo "   - 后端API: http://localhost/caring-standalone"
echo "   - Swagger文档: http://localhost/swagger-ui.html"
echo "   - Druid监控: http://localhost/druid"
echo ""
echo "📝 默认账号："
echo "   - 用户名: admin"
echo "   - 密码: admin123 (请在.env文件中修改)"
echo ""
echo "📋 查看日志："
echo "   docker-compose logs -f"
echo ""
echo "🛑 停止服务："
echo "   docker-compose down"
echo ""