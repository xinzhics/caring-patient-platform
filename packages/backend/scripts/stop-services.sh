#!/bin/bash

# 停止所有服务脚本
# 停止后端微服务和Docker依赖服务

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "================================================"
echo "  停止所有服务"
echo "================================================"

# 停止微服务
echo ""
echo ">>> 停止Java微服务进程..."
pkill -f "caring-.*-server.jar" || true
sleep 3

# 再次检查是否还有残留进程
REMAINING=$(ps aux | grep "caring-.*-server.jar" | grep -v grep | wc -l)
if [ "$REMAINING" -gt 0 ]; then
    echo ">>> 发现残留进程，强制停止..."
    pkill -9 -f "caring-.*-server.jar" || true
fi

echo ">>> 微服务已停止"

# 停止Docker服务
echo ""
echo ">>> 停止Docker依赖服务..."
cd "$PROJECT_ROOT"
docker-compose down

echo ""
echo "================================================"
echo "  所有服务已停止"
echo "================================================"

echo ""
echo ">>> 查看残留进程: ps aux | grep caring"
echo ">>> 启动所有服务: ./scripts/start-services.sh"
echo "================================================"