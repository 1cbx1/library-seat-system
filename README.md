# 图书馆座位预约系统

基于 Spring Boot + MyBatis Plus + MySQL 的图书馆座位预约管理系统（毕业设计项目）。

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
- **数据导出**：座位信息 Excel 导出

## 技术栈

- 后端：Spring Boot 2.7 + MyBatis Plus + MySQL
- 前端：HTML/CSS/JavaScript (Thymeleaf 模板引擎)
- 安全：JWT Token 认证
- 数据库：MySQL 5.7+

## 快速部署

### 1. 环境要求

- JDK 8+
- MySQL 5.7+
- Maven 3.6+

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE library_seat DEFAULT CHARACTER SET utf8mb4;
```

2. 导入 SQL 文件：
```bash
mysql -u root -p library_seat < sql/library_seat.sql
```

3. 修改配置文件 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_seat?...
    username: 你的数据库用户名
    password: 你的数据库密码
```

### 3. 运行项目

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/library-seat-system-1.0.0.jar
```

或直接在 IDE 中运行 `LibrarySeatApplication.java`

### 4. 访问系统

- 前台：http://localhost:8080
- 后台：http://localhost:8080/admin
- 管理员账号：`admin` / `admin123`

## 项目结构

```
src/
├── main/
│   ├── java/com/library/seat/
│   │   ├── controller/    # 控制器 (17个)
│   │   ├── service/       # 业务逻辑 (11个)
│   │   ├── service/impl/  # 业务实现 (11个)
│   │   ├── mapper/       # 数据访问层 (14个)
│   │   ├── entity/       # 实体类 (14个)
│   │   ├── config/       # 配置类 (6个)
│   │   ├── task/         # 定时任务 (2个)
│   │   └── common/       # 通用工具类 (6个)
│   └── resources/
│       ├── templates/    # 前端页面 (13个)
│       ├── static/       # 静态资源
│       └── application.yml
└── sql/
    └── library_seat.sql  # 数据库脚本
```

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |

普通用户需要自行注册。

## 使用 AI 辅助开发

本项目使用 **Claude Code** (Anthropic AI) 辅助开发，从需求分析、架构设计到代码实现全程使用 AI 辅助编程，提升了开发效率。