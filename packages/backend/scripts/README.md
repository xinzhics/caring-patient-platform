# 安全检查脚本说明

本目录包含用于确保项目安全的脚本文件。

## 脚本说明

### 1. security-check.sh
敏感信息检查脚本，用于检测以下内容：
- 硬编码的API密钥（sk-、app-、pat_开头）
- 证书文件（.pem、.key、.crt等）
- 默认数据库密码
- 生产环境配置文件
- .env文件
- 内部仓库地址

使用方法：
```bash
bash scripts/security-check.sh
```

### 2. pre-commit.sh
Git pre-commit钩子脚本，在每次提交前自动运行安全检查。

## 安装Git钩子

将pre-commit钩子安装到Git中：

```bash
# 方法1：复制钩子文件
cp scripts/pre-commit.sh .git/hooks/pre-commit

# 方法2：创建软链接
ln -s ../../scripts/pre-commit.sh .git/hooks/pre-commit
```

## 使用建议

1. 在提交代码前，先手动运行安全检查脚本
2. 安装pre-commit钩子，自动阻止包含敏感信息的提交
3. 定期更新检查规则，覆盖新的敏感信息模式

## 自定义检查

如需添加新的检查规则，请编辑security-check.sh文件，在相应的检查部分添加新的grep或find命令。