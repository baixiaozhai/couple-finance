# 快速开始指南

## 5分钟运行起来

### 第一步：准备数据库（1分钟）

```bash
# 打开MySQL命令行
mysql -u root -p

# 在MySQL中执行：
CREATE DATABASE couple_finance DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE couple_finance;

# 复制粘贴 database/schema.sql 中的所有内容执行
```

### 第二步：启动后端（2分钟）

```bash
# 进入后端目录
cd couple-finance/backend

# 编译运行（需要Maven）
mvn clean install
mvn spring-boot:run

# 看到 "Started CoupleFinanceApplication" 表示启动成功
# 后端运行在 http://localhost:8080
```

### 第三步：启动前端（2分钟）

```bash
# 新开一个终端，进入前端目录
cd couple-finance/frontend

# 安装依赖（首次需要）
npm install

# 运行开发服务器
npm run dev

# 看到 "Local: http://localhost:5173/" 表示启动成功
```

### 第四步：开始使用

1. 打开浏览器访问：http://localhost:5173
2. 点击"立即注册"创建账号
3. 创建或加入家庭
4. 开始记账！

---

## 使用流程

### 场景1：两个人一起使用

**第一个人：**
1. 注册账号
2. 在首页点击"创建家庭"
3. 进入"设置"页面，复制邀请码给另一个人

**第二个人：**
1. 注册账号
2. 在首页点击"加入家庭"
3. 输入邀请码
4. 现在两个人可以共同记账了！

### 场景2：设置月度预算

1. 进入"预算"页面
2. 选择月份
3. 点击"添加"按钮
4. 选择分类（如"餐饮"）
5. 输入预算金额
6. 保存

### 场景3：查看月度复盘

1. 进入"复盘"页面
2. 选择要查看的月份
3. 查看：
   - 总收入/总支出/结余
   - 各分类预算执行情况
   - 自动生成的总结

---

## 目录结构速览

```
couple-finance/
├── backend/              # Spring Boot 后端
│   ├── src/main/java/    # Java源代码
│   └── pom.xml           # Maven配置
├── frontend/             # Vue3 前端
│   ├── src/views/        # 页面文件
│   └── package.json      # npm配置
├── database/
│   └── schema.sql        # 数据库脚本
└── README.md             # 详细文档
```

---

## 常见问题

### Q: 后端启动失败，提示数据库连接错误？
检查 `backend/src/main/resources/application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/couple_finance
    username: root        # 你的MySQL用户名
    password: root        # 你的MySQL密码
```

### Q: 前端无法连接后端？
确保 `frontend/vite.config.js` 中的代理配置正确：
```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',  // 后端地址
    changeOrigin: true
  }
}
```

### Q: 如何重置数据？
```sql
-- 在MySQL中执行
USE couple_finance;
DELETE FROM transaction_record;
DELETE FROM budget;
DELETE FROM family_member;
DELETE FROM family;
DELETE FROM user;
```

---

## 技术栈版本

- Java 17
- Spring Boot 3.2.0
- MySQL 8.0
- Vue 3.4
- Node.js 18+

## 下一步

- 阅读完整文档：[README.md](./README.md)
- 查看 API 文档：启动后端后访问 http://localhost:8080/swagger-ui.html
- 自定义开发：修改前端主题色在 `frontend/src/views/Layout.vue`

---

**祝你使用愉快！** 🎉
