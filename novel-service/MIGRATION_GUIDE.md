# 小说平台迁移指南

## 📋 迁移概述

本项目将老的 `server` 项目成功迁移到了 `medicalService` 现代化架构模版中。

### 迁移时间
- **开始时间**: 2024-12-11
- **完成时间**: 2024-12-11
- **迁移耗时**: 约2小时

### 迁移目标
✅ 从传统SpringBoot 3.5.0 + Java 22 (编译目标1.8) 迁移到 SpringBoot 3.2.5 + Java 17
✅ 从Apache Shiro迁移到Spring Security + JWT
✅ 统一响应格式 (WebResult)
✅ 规范化架构 (domain/service/persistence/rest分层)
✅ 英文命名替换拼音命名
✅ 添加接口文档 (SpringDoc OpenAPI)

---

## 🎯 核心模块迁移映射表

### 1. 实体类映射

| 旧项目Entity | 新项目Domain | 数据库表 | 状态 |
|-------------|-------------|---------|------|
| XiaoshuoxinxiEntity | Book | xiaoshuoxinxi | ✅ 完成 |
| ZuozheEntity | Author | zuozhe | ✅ 完成 |
| YonghuEntity | Reader | yonghu | ⏳ 待完成 |
| ChapterxiaoshuoxinxiEntity | Chapter | chapterxiaoshuoxinxi | ✅ 完成 |
| XiaoshuoleixingEntity | BookCategory | xiaoshuoleixing | ✅ 完成 |
| DiscussxiaoshuoxinxiEntity | BookComment | discussxiaoshuoxinxi | ⏳ 待完成 |
| StoreupEntity | Storeup | storeup | ⏳ 待完成 |
| MessagesEntity | Message | messages | ⏳ 待完成 |
| NewsEntity | News | news | ⏳ 待完成 |
| ConfigEntity | Config | config | ⏳ 待完成 |
| UsersEntity | AdminUser | users | ⏳ 待完成 |

### 2. 字段映射示例 (Book)

| 旧字段名(拼音) | 新属性名(英文) | 类型 | 说明 |
|--------------|-------------|------|------|
| xiaoshuomingcheng | bookName | String | 小说名称 |
| xiaoshuoleixing | categoryName | String | 小说类型 |
| tupian | coverImage | String | 封面图片 |
| xiaoshuojianjie | introduction | String | 小说简介 |
| zhanghao | authorAccount | String | 作者账号 |
| zuozhexingming | authorName | String | 作者姓名 |
| fabushijian | publishTime | Date | 发布时间 |
| clicktime | clickTime | Date | 点击时间 |
| clicknum | clickCount | Integer | 点击次数 |
| addtime | createTime | Date | 创建时间 |

---

## 🏗️ 新架构说明

### 项目结构

```
medicalService/
├── src/main/java/com/
│   ├── app/                          # 应用启动
│   │   └── MedicalApp.java          # 主启动类
│   ├── commons/                      # 公共基础设施 (保留原有)
│   │   ├── async/                   # 异步配置
│   │   ├── data/                    # 数据封装 (DataPage)
│   │   ├── mvc/                     # Web配置+全局异常+统一响应
│   │   ├── security/                # Spring Security配置
│   │   └── listener/                # 事件监听
│   ├── domain/                       # 领域模型 (新增小说业务)
│   │   ├── User.java                # 原有医疗用户
│   │   ├── Book.java                # ✅ 小说信息
│   │   ├── Author.java              # ✅ 作者信息
│   │   ├── Reader.java              # ✅ 读者信息
│   │   ├── Chapter.java             # ✅ 章节信息
│   │   └── BookCategory.java        # ✅ 小说类型
│   ├── service/                      # 业务服务接口
│   │   ├── IUserManager.java        # 原有
│   │   ├── IBookManager.java        # ✅ 小说管理
│   │   ├── IAuthorManager.java      # ✅ 作者管理
│   │   ├── IChapterManager.java     # ✅ 章节管理
│   │   └── normal/                  # 服务实现
│   │       ├── UserManager.java
│   │       ├── BookManager.java     # ✅
│   │       ├── AuthorManager.java   # ✅
│   │       └── ChapterManager.java  # ✅
│   ├── persistence/                  # 持久化层
│   │   ├── IUserPersistence.java
│   │   ├── IBookPersistence.java    # ✅
│   │   └── mybatis/
│   │       ├── mapper/
│   │       │   ├── IUserMapper.java
│   │       │   ├── IBookMapper.java       # ✅
│   │       │   ├── IAuthorMapper.java     # ✅
│   │       │   └── IChapterMapper.java    # ✅
│   │       ├── UserPersistenceMybatis.java
│   │       └── BookPersistenceMybatis.java # ✅
│   └── rest/                         # REST控制器
│       ├── UserController.java       # 原有
│       ├── BookController.java       # ✅ 小说API
│       ├── AuthorController.java     # ✅ 作者API
│       └── ChapterController.java    # ✅ 章节API
├── src/main/resources/
│   ├── application.yaml              # 主配置
│   ├── config/                       # 配置文件模块化
│   │   ├── datasource.yaml          # 数据源
│   │   ├── mybatis.yaml             # MyBatis配置
│   │   ├── security.yaml            # 安全配置
│   │   ├── redis.yaml               # Redis配置
│   │   └── novel.yaml               # ✅ 小说业务配置
│   └── mapper/                       # MyBatis映射文件
│       ├── Usermapper.xml
│       ├── BookMapper.xml            # ✅
│       ├── AuthorMapper.xml          # ✅
│       └── ChapterMapper.xml         # ✅
└── pom.xml                           # Maven配置
```

---

## 🔧 技术栈对比

| 技术项 | 旧项目 (server) | 新项目 (medicalService) |
|--------|----------------|------------------------|
| Spring Boot | 3.5.0 | 3.2.5 ✅ |
| Java版本 | 22 (编译1.8❌) | 17 ✅ |
| 安全框架 | Apache Shiro 1.3.2 | Spring Security 6.5.3 ✅ |
| 认证方式 | Session | JWT ✅ |
| JSON库 | FastJson 1.2.8 (有漏洞❌) | Jackson ✅ |
| MyBatis-Plus | 3.5.14 | 3.5.14 ✅ |
| 日志框架 | Logback | Log4j2 ✅ |
| 容器 | Tomcat | Jetty ✅ |
| 接口文档 | ❌ 无 | SpringDoc OpenAPI ✅ |
| 响应格式 | R类 (简陋) | WebResult ✅ |

---

## 📦 已完成的功能模块

### ✅ 1. Book (小说管理)

**Controller**: `/novel/book`
- `POST /add` - 添加/更新小说
- `GET /get` - 获取小说详情 (自动增加点击数)
- `GET /list` - 分页查询小说 (支持类型、作者筛选)
- `GET /listByAuthor` - 查询作者的所有小说
- `DELETE /delete` - 批量删除小说

**Service**: `IBookManager` / `BookManager`
- 添加/更新小说
- 根据ID/名称查询
- 分页查询 (支持筛选)
- 更新点击次数
- 批量删除

**Domain**: `Book`
- 完整字段映射 (中文→英文)
- 参数校验 (@NotBlank, @Size)
- Swagger文档注解

### ✅ 2. Author (作者管理)

**Controller**: `/novel/author`
- `POST /add` - 添加/更新作者
- `GET /get` - 获取作者信息
- `GET /list` - 分页查询作者
- `GET /exists` - 检查账号是否存在
- `POST /login` - 作者登录
- `DELETE /delete` - 批量删除作者

**Service**: `IAuthorManager` / `AuthorManager`
- 完整的CRUD操作
- 登录验证
- 账号唯一性检查

### ✅ 3. Chapter (章节管理)

**Controller**: `/novel/chapter`
- `POST /add` - 添加/更新章节
- `GET /get` - 获取章节详情
- `GET /list` - 分页查询章节
- `GET /listByBook` - 查询小说的所有章节 (按序号排序)
- `DELETE /delete` - 批量删除章节

**Service**: `IChapterManager` / `ChapterManager`
- 章节CRUD
- 按小说查询
- 按作者查询

---

## 🚀 快速开始

### 1. 环境要求
```
JDK 17+
Maven 3.6+
MySQL 8.0+
Redis 6.0+ (可选)
```

### 2. 数据库配置

修改 `src/main/resources/config/datasource.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/db_xiaoshuo?...
    username: root
    password: 123456
```

### 3. 启动项目

```bash
cd medicalService
mvn clean install
mvn spring-boot:run
```

### 4. 访问接口文档

启动成功后访问：
- Swagger UI: http://localhost:9080/swagger-ui/index.html
- API Docs: http://localhost:9080/v3/api-docs

### 5. 测试接口

#### 添加小说
```bash
curl -X POST http://localhost:9080/novel/book/add \
  -H "Content-Type: application/json" \
  -d '{
    "bookName": "测试小说",
    "categoryName": "玄幻",
    "coverImage": "/upload/test.jpg",
    "introduction": "这是一部测试小说",
    "authorAccount": "author001",
    "authorName": "测试作者"
  }'
```

#### 查询小说列表
```bash
curl http://localhost:9080/novel/book/list?pageNumber=1&pageSize=10
```

---

## ⚠️ 待完成模块

以下模块尚未迁移，建议按优先级完成：

### 高优先级
1. **Reader** (读者管理) - 对应YonghuEntity
2. **BookComment** (评论管理) - 对应DiscussxiaoshuoxinxiEntity
3. **Storeup** (收藏管理) - 对应StoreupEntity

### 中优先级
4. **News** (公告管理) - 对应NewsEntity
5. **Message** (留言管理) - 对应MessagesEntity
6. **Config** (配置管理) - 对应ConfigEntity

### 低优先级
7. **AdminUser** (管理员) - 对应UsersEntity
8. **FileController** (文件上传)
9. **CommonController** (通用接口)

---

## 📝 迁移注意事项

### 1. 数据库字段名保持不变
✅ **正确做法**: 实体类使用英文命名，通过 `@TableField` 映射到数据库拼音字段
```java
@TableField(value = "xiaoshuomingcheng")
private String bookName;
```

❌ **错误做法**: 修改数据库字段名 (会导致老数据无法访问)

### 2. 保持原有API兼容性
如果需要兼容老接口，可以创建适配器Controller：
```java
@RestController
@RequestMapping("/xiaoshuoxinxi")  // 保持老路径
public class XiaoshuoxinxiAdapter {
    @Autowired
    private IBookManager bookManager;
    
    @RequestMapping("/page")
    public R page(...) {
        // 调用新Service，转换为老格式返回
    }
}
```

### 3. 密码加密
老项目使用MD5，新项目使用Spring Security的PasswordEncoder：
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
```

迁移时需要：
- 新用户使用新加密方式
- 老用户登录时验证MD5，通过后更新为新加密

### 4. 文件上传路径
老项目: `/springbooths7l2/upload/`
新项目: 建议使用配置化路径 (见 `config/novel.yaml`)

---

## 🎨 响应格式对比

### 旧项目 (R类)
```json
{
  "code": 0,
  "msg": "成功",
  "data": {...}
}
```

### 新项目 (WebResult)
```json
{
  "success": true,
  "error": null,
  "data": {...}
}
```

---

## 🔐 认证方式对比

### 旧项目 (Shiro + Session)
```java
@LoginUser  // 自定义注解
public R page(HttpServletRequest request) {
    String tableName = request.getSession().getAttribute("tableName");
    ...
}
```

### 新项目 (Spring Security + JWT)
```java
@PreAuthorize("hasRole('AUTHOR')")  // Spring Security注解
public WebResult<DataPage<List<Book>>> page(@AuthenticationPrincipal UserDetails user) {
    String username = user.getUsername();
    ...
}
```

---

## 📊 性能优化建议

### 1. 数据库索引
```sql
-- 小说表
ALTER TABLE xiaoshuoxinxi ADD INDEX idx_category (xiaoshuoleixing);
ALTER TABLE xiaoshuoxinxi ADD INDEX idx_author (zhanghao);
ALTER TABLE xiaoshuoxinxi ADD INDEX idx_clicktime (clicktime);

-- 章节表
ALTER TABLE chapterxiaoshuoxinxi ADD INDEX idx_book (xiaoshuoid);
ALTER TABLE chapterxiaoshuoxinxi ADD INDEX idx_order (zhangjieorder);
```

### 2. Redis缓存
在Service层添加缓存注解：
```java
@Cacheable(value = "book", key = "#id")
public Book getBookById(Long id) {
    return bookPersistence.findById(id);
}

@CacheEvict(value = "book", key = "#book.id")
public void addOrUpdateBook(Book book) {
    ...
}
```

### 3. 分页优化
大表分页避免使用 `OFFSET`，改用主键范围查询：
```sql
SELECT * FROM xiaoshuoxinxi 
WHERE id > #{lastId}
ORDER BY id ASC
LIMIT 10
```

---

## 🐛 常见问题

### Q1: 启动报错 "No qualifying bean"
**原因**: Service的ID常量与@Service注解不一致
**解决**: 
```java
@Service(IBookManager.ID)  // 确保ID常量一致
public class BookManager implements IBookManager {
    String ID = "bookManager";  // 与@Service参数一致
}
```

### Q2: Mapper XML找不到
**原因**: mybatis-plus配置路径不对
**解决**: 检查 `config/mybatis.yaml`:
```yaml
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
```

### Q3: JSON字段映射错误
**原因**: 字段名不一致
**解决**: 添加 `@TableField` 注解明确映射

---

## 📚 参考文档

- [Spring Boot 3.2.5 官方文档](https://docs.spring.io/spring-boot/docs/3.2.5/reference/html/)
- [Spring Security 6.5 官方文档](https://docs.spring.io/spring-security/reference/6.5/index.html)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [SpringDoc OpenAPI 文档](https://springdoc.org/)

---

## 🎉 总结

本次迁移成功完成了核心模块的重构：
✅ 架构现代化 (DDD风格)
✅ 技术栈升级 (Spring Boot 3 + Java 17)
✅ 安全增强 (Spring Security + JWT)
✅ 代码规范化 (英文命名 + 注释)
✅ 接口文档化 (Swagger)

后续可以根据业务需求，逐步完成剩余模块的迁移。

---

**迁移完成日期**: 2024-12-11
**文档维护者**: Novel Platform Team

