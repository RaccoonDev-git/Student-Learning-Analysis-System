<<<<<<< HEAD
# 学生学习情况分析系统

一个基于 Spring Boot + Flask + MySQL 的全栈学生学习管理与分析系统。

## 📋 项目概述

本系统提供学生、教师、管理员三种角色的完整功能，包括课程管理、成绩管理、学习数据分析等功能。

### 主要功能

- 🔐 **用户认证与授权** - 基于 JWT 的安全认证系统
- 👨‍🎓 **学生管理** - 学生信息管理、课程选课、成绩查询
- 👨‍🏫 **教师管理** - 教师信息管理、课程管理、成绩录入
- 📚 **课程管理** - 课程创建、编辑、删除、查询
- 📊 **成绩管理** - 成绩录入、查询、统计分析
- 📈 **数据分析** - 学习情况可视化分析
- 📤 **数据导入导出** - 支持批量导入导出学生信息和成绩数据
- ⚠️ **学生预警** - 自动检测学生异常情况并生成预警
- 💬 **消息通知** - 师生之间消息传递和系统通知

## 🏗️ 技术栈

### 后端

- **框架**: Spring Boot 3.2.0
- **语言**: Java 17
- **数据库**: MySQL 8.0
- **安全**: Spring Security + JWT
- **ORM**: Spring Data JPA + Hibernate
- **文档**: SpringDoc OpenAPI (Swagger)
- **工具**: Lombok, MapStruct

### 前端

- **框架**: Flask (Python 3.x)
- **UI**: HTML5 + CSS3 + JavaScript
- **图表**: Chart.js
- **HTTP**: Fetch API

### 数据库

- MySQL 8.0
- 核心表: users, students, teachers, courses, course_enrollments, grades, comprehensive_grades, learning_activities, notifications, student_warnings, course_weight_configs, grade_types, resources, messages

## 🚀 快速开始

### 前置要求

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Python 3.8+
- Git

### 1. 克隆项目

```bash
git clone <repository-url>
cd Student_Learning_Situation_Analysis_System
```

### 2. 数据库初始化

#### 启动 MySQL 服务

```bash
# Windows
net start MySQL80

# Linux/Mac
sudo systemctl start mysql
```

#### 创建数据库并导入数据

执行数据库初始化脚本（包含完整的表结构和初始数据）：

```bash
mysql -u root -p < database/init_database.sql
```

或使用命令行：

```bash
mysql -u root -p
```

```sql
source database/init_database.sql
```

数据库初始化脚本将自动：
- 创建 `student_analysis` 数据库
- 创建所有表结构
- 建立索引和外键关系
- 插入测试数据

### 3. 后端配置与启动

#### 配置数据库连接

编辑 `backend/src/main/resources/application-mysql.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_analysis
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

#### 编译并启动后端

```bash
cd backend
mvn clean package -DskipTests
java -jar target/student-analysis-system-2.0.0-SNAPSHOT.jar
```

后端将在 `http://localhost:8082` 启动

#### API 文档

访问 Swagger UI: `http://localhost:8082/swagger-ui.html`

### 4. 前端配置与启动

#### 安装 Python 依赖

```bash
cd frontend
pip install -r requirements.txt
```

#### 启动 Flask 服务器

```bash
python server.py
```

前端将在 `http://localhost:3000` 启动

### 5. 访问系统

打开浏览器访问: `http://localhost:3000`

## 👥 测试账户

系统预置了以下测试账户（密码统一为 `password123`）：

### 管理员

- 用户名: `admin`
- 密码: `password123`
- 邮箱: admin@example.com

### 教师账户

- `teacher1` / `password123` - 王娜老师
- `teacher2` / `password123` - 李教授

### 学生账户

- `student1` / `password123` - 申刚
- `student2` / `password123` - 贾阳
- `student3` / `password123` - 王奕程
- 更多学生账户请查看数据库初始化脚本

## 📁 项目结构

```
Student_Learning_Situation_Analysis_System/
├── backend/                          # Spring Boot 后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/studentanalysissystem/
│   │   │   │       ├── config/      # 配置类
│   │   │   │       ├── controller/  # REST API 控制器
│   │   │   │       ├── dto/         # 数据传输对象
│   │   │   │       ├── exception/   # 异常处理
│   │   │   │       ├── mapper/      # MapStruct 映射器
│   │   │   │       ├── model/       # JPA 实体类
│   │   │   │       ├── repository/  # 数据访问层
│   │   │   │       ├── security/    # 安全配置
│   │   │   │       ├── service/     # 业务逻辑层
│   │   │   │       └── util/        # 工具类
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-mysql.properties
│   │   │       └── logback-spring.xml
│   │   └── test/                    # 测试代码
│   └── pom.xml                      # Maven 配置
│
├── frontend/                         # Flask 前端
│   ├── src/
│   │   ├── pages/                   # HTML 页面
│   │   │   ├── student/            # 学生页面
│   │   │   ├── teacher/            # 教师页面
│   │   │   └── admin/              # 管理员页面
│   │   ├── assets/
│   │   │   ├── css/                # 样式文件
│   │   │   └── js/                 # JavaScript 文件
│   │   └── components/             # 共享组件
│   ├── server.py                    # Flask 服务器
│   └── requirements.txt             # Python 依赖
│
├── database/                         # 数据库脚本
│   └── init_database.sql           # 数据库初始化脚本
│
├── .gitignore                        # Git 忽略文件
└── README.md                         # 项目说明文档
```

## 🔑 核心 API 端点

### 认证相关

- `POST /api/authentication/register` - 用户注册
- `POST /api/authentication/login` - 用户登录
- `POST /api/authentication/change-password` - 修改密码

### 学生管理

- `GET /api/students` - 获取学生列表
- `GET /api/students/{id}` - 获取学生详情
- `POST /api/students` - 创建学生
- `PUT /api/students/{id}` - 更新学生信息
- `DELETE /api/students/{id}` - 删除学生

### 教师管理

- `GET /api/teachers` - 获取教师列表
- `GET /api/teachers/{id}` - 获取教师详情
- `POST /api/teachers` - 创建教师
- `PUT /api/teachers/{id}` - 更新教师信息

### 课程管理

- `GET /api/courses` - 获取课程列表
- `GET /api/courses/{id}` - 获取课程详情
- `POST /api/courses` - 创建课程
- `PUT /api/courses/{id}` - 更新课程信息
- `DELETE /api/courses/{id}` - 删除课程

### 成绩管理

- `GET /api/grades/student/{studentId}` - 获取学生成绩
- `POST /api/grades` - 录入成绩
- `PUT /api/grades/{id}` - 更新成绩
- `GET /api/comprehensive-grades` - 获取综合成绩
- `POST /api/comprehensive-grades` - 创建综合成绩

### 数据导入导出

- `GET /api/admin/students/import/template` - 下载学生导入模板
- `POST /api/admin/students/import` - 批量导入学生
- `GET /api/admin/students/export` - 导出所有学生
- `POST /api/admin/students/export/selected` - 导出选中学生
- `GET /api/comprehensive-grades/template` - 下载成绩导入模板
- `POST /api/comprehensive-grades/import` - 批量导入成绩

## 🔒 安全性

- ✅ JWT Token 认证
- ✅ BCrypt 密码加密
- ✅ CORS 跨域配置
- ✅ Spring Security 权限控制
- ✅ SQL 注入防护
- ✅ XSS 攻击防护

## 📊 数据库设计

### 核心表结构

1. **users** - 用户基础信息表
2. **students** - 学生信息扩展表
3. **teachers** - 教师信息扩展表
4. **courses** - 课程信息表
5. **course_enrollments** - 选课关系表
6. **grades** - 成绩记录表（原始成绩）
7. **comprehensive_grades** - 综合成绩表（计算后的总成绩）
8. **course_weight_configs** - 课程权重配置表
9. **grade_types** - 成绩类型配置表
10. **student_warnings** - 学生预警表
11. **learning_activities** - 学习活动记录表
12. **notifications** - 通知消息表
13. **messages** - 用户消息表
14. **resources** - 教学资源表

### 数据库关系

- 用户（users）与学生（students）、教师（teachers）一对一关系
- 学生与课程通过选课表（course_enrollments）多对多关系
- 成绩（grades）关联学生和课程
- 综合成绩（comprehensive_grades）基于原始成绩计算得出

详细设计请参考 `database/init_database.sql`

## 📤 数据导入导出功能

### 导入功能

系统支持以下数据的批量导入：

1. **学生信息导入**
   - 格式: Excel (.xlsx)
   - 必需字段: 学号、姓名、年级、班级
   - 可选字段: 专业、手机号、邮箱、备注

2. **综合成绩导入**
   - 格式: CSV (.csv)
   - 必需字段: 学号、课程ID、学期、综合成绩
   - 可选字段: 平时分、期中成绩、期末成绩

3. **课程专用模板**
   - 支持根据课程类型生成专用模板
   - 动态模板：根据课程ID生成专属模板

### 导出功能

系统支持以下数据的导出：

1. **学生信息导出**
   - 导出所有学生
   - 导出选中学生
   - 导出单个学生
   - 格式: Excel (.xlsx)

2. **成绩数据导出**
   - 导出所有成绩
   - 导出学生成绩
   - 导出课程成绩
   - 格式: Excel (.xlsx) 或 CSV (.csv)

详细使用说明请参考代码中的导入导出功能实现。

## 📈 成绩计算说明

### 平时分计算

系统支持灵活的平时分计算，基于以下流程：

1. **数据收集**: 从grades表获取原始成绩
2. **数据分离**: 按exam_type分离平时分和期末分
3. **权重配置**: 从grade_types表获取权重配置
4. **加权计算**: 按权重计算总平时分
5. **结果存储**: 保存到comprehensive_grades表

平时分类型包括：
- ATTENDANCE（签到）- 默认权重20%
- HOMEWORK（作业）- 默认权重30%
- LAB（实验报告）- 默认权重25%
- QUIZ（随堂测验）- 默认权重25%

### 综合成绩计算

综合成绩 = 平时分 × 平时权重 + 期末分 × 期末权重

默认权重配置：
- 平时分权重: 30%
- 期末分权重: 70%

### 课程效果分析

系统提供课程效果分析功能：
- **预期通过率统计**: 基于学生平均成绩计算预期通过情况
- **成绩分布分析**: 统计成绩在各分数段的分布情况
- **学生参与度分析**: 基于学习活动数据统计参与度

## ⚠️ 学生预警系统

系统自动检测以下异常情况并生成预警：

1. **出勤率低**: 出勤率低于阈值
2. **平时分偏低**: 平时分低于设定阈值
3. **综合成绩偏低**: 综合成绩接近及格线
4. **平时分严重偏低**: 平时分为0或极低

预警级别：
- HIGH（高）: 需要立即关注
- MEDIUM（中）: 需要提醒
- LOW（低）: 需要观察

## 🛠️ 开发指南

### 后端开发

```bash
cd backend
mvn spring-boot:run
```

### 前端开发

```bash
cd frontend
python server.py
```

### 编译打包

```bash
cd backend
mvn clean package
```

## 📝 端口配置

### 后端服务器 (Spring Boot)
- **端口**: 8082
- **访问地址**: http://localhost:8082
- **API文档**: http://localhost:8082/swagger-ui.html

### 前端服务器 (Flask)
- **端口**: 3000
- **访问地址**: http://localhost:3000

### 数据库服务器 (MySQL)
- **端口**: 3306
- **访问地址**: localhost:3306
- **数据库名**: student_analysis

## ⚠️ 注意事项

1. **端口冲突**: 如果端口被占用，可以修改配置文件中的端口号
2. **数据库编码**: 确保数据库使用UTF-8编码，以支持中文
3. **文件上传**: 上传的文件存储在服务器本地，需要确保有足够的磁盘空间
4. **日志文件**: 后端日志位于 `backend/logs/application.log`

## 🤝 贡献

欢迎提交 Issue 和 Pull Request!

## 📄 许可证

本项目仅用于学习和研究目的。

## 📮 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 GitHub Issue
- 发送邮件至项目维护者

---

**开发时间**: 2025年10月  
**版本**: 2.0.0-SNAPSHOT
=======
# -
>>>>>>> d664dcef71cd64ce5df17c0a49c7778abfcc1d72
