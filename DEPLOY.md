# Render 免费部署指南

## 概述

使用 Render 免费托管：
- **后端**：Spring Boot + Docker（免费）
- **数据库**：PostgreSQL（免费）
- **前端**：静态站点（免费）

## 部署步骤

### 第一步：准备代码

1. 确保所有修改已保存
2. 初始化 Git 仓库（如果没有）：
```bash
cd couple-finance
git init
git add .
git commit -m "Initial commit"
```

3. 创建 GitHub 仓库并推送：
```bash
git remote add origin https://github.com/你的用户名/couple-finance.git
git branch -M main
git push -u origin main
```

### 第二步：部署到 Render

1. 访问 https://render.com
2. 用 GitHub 账号登录
3. 点击 "New" → "Blueprint"
4. 选择你的 `couple-finance` 仓库
5. Render 会自动识别 `render.yaml` 配置
6. 点击 "Apply"

### 第三步：等待部署完成

Render 会自动：
- 创建 PostgreSQL 数据库
- 构建并部署后端
- 构建并部署前端

大约需要 **5-10分钟**。

### 第四步：获取访问地址

部署完成后，在 Render Dashboard 中：
- 前端地址：`https://couple-finance-web.onrender.com`
- 后端地址：`https://couple-finance-api.onrender.com`

把这个前端地址发给女朋友，双方都可以访问！

## 免费额度说明

| 资源 | 免费额度 | 说明 |
|------|---------|------|
| Web Service | 750小时/月 | 足够24小时运行 |
| PostgreSQL | 1GB存储 | 足够使用 |
| 带宽 | 100GB/月 | 足够 |

**注意：**
- 免费服务在15分钟无访问后会休眠
- 首次访问需要等待10-30秒唤醒
- 可以设置定时ping保持活跃（可选）

## 更新部署

代码修改后，推送到 GitHub，Render 会自动重新部署：

```bash
git add .
git commit -m "更新说明"
git push
```

## 常见问题

### Q: 部署失败怎么办？
查看 Render Dashboard 的 Logs 标签页，查看错误日志。

### Q: 数据库数据会丢失吗？
不会，PostgreSQL 数据持久化存储。

### Q: 如何查看数据库？
Render 提供 PostgreSQL 连接信息，可用 DBeaver 等工具连接。

### Q: 免费服务会过期吗？
只要持续使用就不会删除，长期不用可能被清理。

## 备用方案

如果 Render 免费额度用完：

1. **Railway**（类似Render，也有免费额度）
2. **Fly.io**（免费额度更慷慨）
3. **阿里云学生机**（约10元/月）

## 技术支持

Render 文档：https://render.com/docs
