# 情侣/家庭财务管理系统 (Couple Finance)

一个简单实用的情侣/家庭财务管理 Web App（PWA），支持双人共享账本、月度预算管理和自动复盘统计。

## 功能特性

### 核心功能
- **双人共享账本** - 创建家庭，通过邀请码加入，共同记账
- **月度预算管理** - 按分类设置预算，实时查看使用情况
- **日常记账** - 支持收入/支出，分类选择，指定付款人
- **月末自动复盘** - 自动统计收支、预算执行情况，生成总结

### 技术特性
- **PWA支持** - 可安装到手机桌面，离线可用
- **响应式设计** - 完美适配手机端
- **JWT认证** - 安全的用户认证机制
- **前后端分离** - 现代化架构，易于扩展

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0

### 前端
- Vue 3 + Vite
- Element Plus UI
- Pinia 状态管理
- Vue Router
- Axios
- PWA (vite-plugin-pwa)

## 快速开始

### 1. 环境准备

确保已安装以下软件：
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 18+

### 2. 数据库配置

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE couple_finance DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行建表脚本
source database/schema.sql
```

### 3. 启动后端

```bash
cd backend

# 修改数据库配置（如果需要）
# 编辑 src/main/resources/application.yml

# 编译并运行
mvn clean install
mvn spring-boot:run

# 后端服务将在 http://localhost:8080 启动
```

### 4. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 前端将在 http://localhost:5173 启动
```

### 5. 访问应用

打开浏览器访问：http://localhost:5173

## 项目结构

```
couple-finance/
├── backend/                    # 后端项目
│   ├── src/main/java/
│   │   └── com/couplefinance/
│   │       ├── controller/     # 控制器层
│   │       ├── service/        # 服务层
│   │       ├── entity/         # 实体类
│   │       ├── repository/     # 数据访问层
│   │       ├── dto/            # 数据传输对象
│   │       ├── security/       # 安全相关
│   │       └── exception/      # 异常处理
│   ├── src/main/resources/
│   │   └── application.yml     # 配置文件
│   └── pom.xml                 # Maven配置
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/                # API接口
│   │   ├── views/              # 页面组件
│   │   ├── stores/             # Pinia状态管理
│   │   ├── router/             # 路由配置
│   │   └── main.js             # 入口文件
│   ├── public/                 # 静态资源
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── database/
│   └── schema.sql              # 数据库脚本
└── README.md
```

## 核心功能说明

### 1. 用户系统
- 注册/登录（JWT认证）
- 用户信息管理

### 2. 家庭绑定
- 创建家庭，自动生成6位邀请码
- 通过邀请码加入家庭
- 查看家庭成员

### 3. 记账系统
- 添加收入/支出记录
- 分类选择（餐饮、交通、购物等）
- 指定付款人
- 删除记录

### 4. 预算管理
- 按分类设置月度预算
- 查看预算使用情况
- 超支提醒

### 5. 月度复盘
- 收支统计
- 预算 vs 实际对比
- 自动总结生成

## API 接口列表

### 认证接口
- `POST /api/auth/register` - 注册
- `POST /api/auth/login` - 登录
- `GET /api/auth/me` - 获取当前用户

### 家庭接口
- `POST /api/family/create` - 创建家庭
- `POST /api/family/join` - 加入家庭
- `GET /api/family/info` - 获取家庭信息
- `POST /api/family/leave` - 退出家庭

### 记账接口
- `POST /api/transaction/add` - 添加记录
- `GET /api/transaction/list` - 获取列表
- `DELETE /api/transaction/delete/{id}` - 删除记录

### 预算接口
- `POST /api/budget/set` - 设置预算
- `GET /api/budget/list` - 获取预算列表
- `DELETE /api/budget/delete` - 删除预算

### 统计接口
- `GET /api/statistics/month` - 获取月度统计

## PWA 配置说明

### 安装到手机

1. **Android (Chrome)**
   - 打开应用网页
   - 点击菜单 → "添加到主屏幕"
   - 确认安装

2. **iOS (Safari)**
   - 打开应用网页
   - 点击分享按钮
   - 选择 "添加到主屏幕"

### PWA 功能
- 离线访问
- 添加到主屏幕
- 独立运行（无浏览器地址栏）
- 主题色适配

## 部署说明

### 生产环境部署

#### 后端部署
```bash
cd backend
mvn clean package -DskipTests

# 运行jar包
java -jar target/couple-finance-backend-1.0.0.jar
```

#### 前端部署
```bash
cd frontend
npm run build

# 构建后的文件在 dist/ 目录
# 将 dist/ 目录部署到Web服务器
```

### 使用Docker部署（可选）

```bash
# 构建镜像
docker build -t couple-finance .

# 运行容器
docker run -p 8080:8080 couple-finance
```

## 开发计划

### 已实现 (MVP)
- [x] 用户注册/登录
- [x] 家庭创建/加入
- [x] 记账功能
- [x] 预算管理
- [x] 月度统计
- [x] PWA支持

### 未来迭代
- [ ] 图表可视化
- [ ] 数据导出
- [ ] 多账本支持
- [ ] 记账提醒
- [ ] 照片附件

## 常见问题

### Q: 如何修改数据库密码？
编辑 `backend/src/main/resources/application.yml` 中的数据库配置。

### Q: 如何修改JWT密钥？
编辑 `backend/src/main/resources/application.yml` 中的 `jwt.secret` 配置。

### Q: 前端端口冲突怎么办？
编辑 `frontend/vite.config.js` 中的 `server.port` 配置。

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎反馈！
