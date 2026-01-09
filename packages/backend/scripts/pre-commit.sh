#!/bin/bash

# Git pre-commit hook
# 在每次提交前自动运行安全检查

# 获取脚本所在目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# 运行安全检查脚本
bash "$SCRIPT_DIR/security-check.sh"

# 检查安全检查的退出码
if [ $? -ne 0 ]; then
    echo -e "\n\033[0;31m提交被阻止：请先修复安全问题\033[0m"
    exit 1
fi

echo -e "\n\033[0;32m安全检查通过，可以提交\033[0m"
exit 0