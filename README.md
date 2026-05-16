# 图书馆座位预约系统

基于 Spring Boot + Vue 3 的图书馆座位预约管理系统（毕业设计项目）。

## 技术栈

- **后端**：Spring Boot 2.7 + MyBatis Plus + MySQL + JWT
- **前端**：Vue 3 + Vite + Element Plus + Pinia + Axios + ECharts
- **数据库**：MySQL 8

## 功能模块

- **用户管理**：注册、登录、个人信息维护
- **座位管理**：座位状态实时显示、座位选择
- **自习室管理**：多自习室管理、座位可视化
- **预约系统**：座位预约、取消预约、排队管理
- **签到签退**：扫码签到、超时自动签退
- **违规检测**：自动检测违约用户、违规记录
- **论坛模块**：帖子发布、评论、敏感词过滤
- **公告系统**：管理员发布通知公告
- **留言板**：用户互动留言
- **评价系统**：座位/自习室评分
- **数据统计**：ECharts 可视化图表
- **数据导出**：座位信息 Excel 导出

## 项目结构

```
library-seat-system
├── src/                      # 后端 (Spring Boot)
│   ├── main/java/com/library/seat/
│   │   ├── controller/       # 控制器
│   │   ├── service/         # 业务逻辑
│   │   ├── mapper/          # 数据访问层
│   │   ├── entity/          # 实体类
│   │   ├── config/          # 配置类
│   │   └── task/            # 定时任务
│   └── resources/
│       ├── templates/       # 页面模板
│       ├── static/          # 静态资源
│       └── application.yml  # 配置文件
├── frontend/                 # 前端 (Vue 3)
│   ├── src/
│   │   ├── api/            # API 接口
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理
│   │   └── views/          # 页面组件
│   ├── public/             # 静态资源
│   ├── package.json
│   └── vite.config.js
└── sql/
    └── library_seat.sql     # 数据库脚本
```

## 快速部署

### 1. 环境要求

- JDK 8+
- MySQL 5.7+
- Maven 3.6+
- Node.js 16+

### 2. 数据库配置

```sql
-- 创建数据库
CREATE DATABASE library_seat DEFAULT CHARACTER SET utf8mb4;

-- 导入数据
mysql -u root -p library_seat < sql/library_seat.sql
```

### 3. 修改后端配置

编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    username: 你的数据库用户名
    password: 你的数据库密码
```

### 4. 启动后端

```bash
cd library-seat-system
mvn spring-boot:run
```

后端地址：http://localhost:8080

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端地址：http://localhost:5173

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 测试用户 | test | 123456 |

普通用户需要自行注册。

## 使用 AI 辅助开发

本项目使用 **Claude Code** (Anthropic AI) 辅助开发，从需求分析、架构设计到代码实现全程使用 AI 辅助编程，提升了开发效率。