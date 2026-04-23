# Fly.io 部署指南

## 1. 安装 Fly.io CLI

### Windows (PowerShell)
```powershell
iwr https://fly.io/install.ps1 -useb | iex
```

### macOS/Linux
```bash
curl -L https://fly.io/install.sh | sh
```

## 2. 登录 Fly.io
```bash
fly auth login
```

## 3. 部署应用

### 首次部署
```bash
# 进入项目目录
cd couple-finance

# 创建应用（已配置 fly.toml，直接部署）
fly deploy

# 查看应用状态
fly status
```

### 后续更新
```bash
fly deploy
```

## 4. 查看日志
```bash
fly logs
```

## 5. 访问应用
```bash
# 打开应用网址
fly open

# 或查看网址
fly apps list
```

## 6. 配置环境变量（可选）
```bash
fly secrets set JWT_SECRET=your-secret-key
```

## 费用说明
- 免费额度：约 $5/月
- shared-cpu-1x (256MB)：约 $1.94/月
- 超出后按量计费
- 建议绑定信用卡，但免费额度内不扣费

## 测试接口
部署成功后访问：
```
https://couple-finance-api.fly.dev/api/health
```

预期返回：
```json
{"status":"ok"}
```
