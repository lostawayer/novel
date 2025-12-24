# 文趣阁阅读平台

基于 Spring Boot + Vue3 的在线小说阅读平台，包含用户前台、管理后台和后端服务三个子系统。

## 项目结构

```
├── novel-front/          # 用户前台（Vue3 + TypeScript + Vite）
├── novel-admin/          # 管理后台（Vue3 + TypeScript + Vite）
├── novel-service/        # 后端服务（Spring Boot 3.2）
├── db_xiaoshuo.sql       # 数据库脚本
└── README.md             # 项目文档
```

## 技术栈

### 后端 (novel-service)
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.5 | 基础框架 |
| Spring Security | 6.5.3 | 安全认证 |
| MyBatis-Plus | 3.5.14 | ORM框架 |
| MySQL | 8.0+ | 数据库 |
| Redis | - | 缓存 |
| JWT | 4.5.0 | Token认证 |
| Jetty | - | Web容器 |
| Swagger/OpenAPI | 2.2.0 | API文档 |

### 前台 (novel-front)
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.24 | 前端框架 |
| TypeScript | 5.9.3 | 类型支持 |
| Vite | 7.2.4 | 构建工具 |
| Element Plus | 2.9.2 | UI组件库 |
| Pinia | 2.3.0 | 状态管理 |
| Vue Router | 4.5.0 | 路由管理 |
| Axios | 1.7.9 | HTTP请求 |
| Quill | 2.0.3 | 富文本编辑器 |

### 后台 (novel-admin)
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.24 | 前端框架 |
| TypeScript | 5.9.3 | 类型支持 |
| Vite | 7.2.4 | 构建工具 |
| Element Plus | 2.12.0 | UI组件库 |
| ECharts | 6.0.0 | 图表库 |
| Pinia | 3.0.4 | 状态管理 |

## 功能模块

### 用户前台
- 首页展示（轮播图、推荐小说、最新发布、热门排行）
- 小说列表（分类筛选、搜索）
- 小说详情（封面、简介、章节目录）
- 章节阅读（VIP章节权限控制）
- 用户收藏
- 评论功能
- 公告信息
- 用户注册/登录

### 管理后台
- 管理员登录
- 小说管理（增删改查）
- 章节管理
- 分类管理
- 作者管理
- 用户管理
- 评论管理
- 公告管理
- 轮播图配置
- 数据统计

## 数据库设计

| 表名 | 说明 |
|------|------|
| admin_user | 管理员表 |
| reader | 读者用户表 |
| author | 作者表 |
| novel_info | 小说信息表 |
| novel_chapter | 章节表 |
| novel_category | 小说分类表 |
| novel_comment | 评论表 |
| user_collection | 用户收藏表 |
| announcement | 公告表 |
| message_board | 留言板 |

## 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis（可选）
- Maven 3.6+
- pnpm（推荐）或 npm

## 快速开始

### 1. 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE db_xiaoshuo DEFAULT CHARACTER SET utf8mb4;

-- 导入数据
mysql -u root -p db_xiaoshuo < db_xiaoshuo.sql
```

### 2. 启动后端服务

```bash
cd novel-service

# 修改数据库配置
# 编辑 src/main/resources/config/datasource.yaml

# 启动服务
mvn spring-boot:run
# 或
./mvnw spring-boot:run
```

服务启动后访问：http://localhost:9080

API文档：http://localhost:9080/swagger-ui.html

### 3. 启动用户前台

```bash
cd novel-front

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

访问：http://localhost:8080

### 4. 启动管理后台

```bash
cd novel-admin

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

访问：http://localhost:5173

## 默认账号

### 管理后台
- 用户名：admin
- 密码：123456

### 用户前台
- 用户名：111
- 密码：111

## 项目配置

### 后端配置文件
```
novel-service/src/main/resources/
├── application.yaml          # 主配置
├── config/
│   ├── datasource.yaml       # 数据库配置
│   ├── redis.yaml            # Redis配置
│   ├── security.yaml         # 安全配置
│   └── ...
```

### 前端代理配置
前台和后台的 `vite.config.ts` 中配置了开发环境代理，默认代理到 `http://localhost:9080`

## 部署说明

### 后端打包
```bash
cd novel-service
mvn clean package
# 生成 target/deploy/novel-service-1.1.jar
```

### 前端打包
```bash
# 用户前台
cd novel-front
pnpm build
# 生成 dist 目录

# 管理后台
cd novel-admin
pnpm build
# 生成 dist 目录
```

### Docker部署
```bash
cd novel-service
./docker-build.sh
./docker-run.sh
```

## 目录说明

### novel-service 后端结构
```
src/main/java/com/
├── app/                # 应用入口
├── commons/            # 公共模块（安全、MVC等）
├── domain/             # 实体类
├── persistence/        # 持久层
├── rest/               # REST控制器
│   ├── compat/         # 兼容层接口
│   └── front/          # 前台接口
├── service/            # 业务层
└── SystemConfig.java   # 系统配置
```

### novel-front 前台结构
```
src/
├── assets/             # 静态资源
├── common/             # 公共工具
├── components/         # 公共组件
├── pages/              # 页面组件
│   ├── home/           # 首页
│   ├── xiaoshuoxinxi/  # 小说相关
│   ├── storeup/        # 收藏
│   └── ...
├── router/             # 路由配置
├── store/              # 状态管理
├── types/              # 类型定义
└── utils/              # 工具函数
```

## License

MIT
