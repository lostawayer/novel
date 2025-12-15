# 📡 API测试示例集

## 目录
- [小说管理API](#小说管理api)
- [作者管理API](#作者管理api)
- [章节管理API](#章节管理api)
- [Postman导入](#postman导入)

---

## 小说管理API

### 1. 添加小说
```bash
curl -X POST 'http://localhost:9080/novel/book/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "bookName": "斗破苍穹",
    "categoryName": "玄幻",
    "coverImage": "/upload/doupocangqiong.jpg",
    "introduction": "三十年河东，三十年河西，莫欺少年穷！萧炎的修炼之路...",
    "authorAccount": "tiancan",
    "authorName": "天蚕土豆"
  }'
```

**响应**:
```json
{
  "success": true,
  "error": null,
  "data": null
}
```

### 2. 更新小说
```bash
curl -X POST 'http://localhost:9080/novel/book/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "id": 1,
    "bookName": "斗破苍穹（修订版）",
    "categoryName": "玄幻",
    "coverImage": "/upload/doupocangqiong_v2.jpg",
    "introduction": "三十年河东，三十年河西，莫欺少年穷！",
    "authorAccount": "tiancan",
    "authorName": "天蚕土豆"
  }'
```

### 3. 获取小说详情
```bash
curl 'http://localhost:9080/novel/book/get?id=1'
```

**响应**:
```json
{
  "success": true,
  "error": null,
  "data": {
    "id": 1,
    "bookName": "斗破苍穹",
    "categoryName": "玄幻",
    "coverImage": "/upload/doupocangqiong.jpg",
    "introduction": "三十年河东，三十年河西，莫欺少年穷！",
    "authorAccount": "tiancan",
    "authorName": "天蚕土豆",
    "publishTime": "2024-12-11 10:30:00",
    "clickTime": "2024-12-11 11:15:23",
    "clickCount": 5,
    "createTime": "2024-12-11 10:30:00"
  }
}
```

### 4. 分页查询小说（所有）
```bash
curl 'http://localhost:9080/novel/book/list?pageNumber=1&pageSize=10'
```

### 5. 按类型查询小说
```bash
curl 'http://localhost:9080/novel/book/list?pageNumber=1&pageSize=10&categoryName=玄幻'
```

### 6. 按作者查询小说
```bash
curl 'http://localhost:9080/novel/book/list?pageNumber=1&pageSize=10&authorAccount=tiancan'
```

### 7. 查询作者的所有小说
```bash
curl 'http://localhost:9080/novel/book/listByAuthor?authorAccount=tiancan'
```

### 8. 删除小说
```bash
# 删除单个
curl -X DELETE 'http://localhost:9080/novel/book/delete?ids=1'

# 删除多个
curl -X DELETE 'http://localhost:9080/novel/book/delete?ids=1&ids=2&ids=3'
```

---

## 作者管理API

### 1. 注册作者
```bash
curl -X POST 'http://localhost:9080/novel/author/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "account": "tiancan",
    "password": "123456",
    "authorName": "天蚕土豆",
    "gender": "男",
    "avatar": "/upload/avatar_tiancan.jpg",
    "phone": "13800138000",
    "email": "tiancan@example.com"
  }'
```

### 2. 更新作者信息
```bash
curl -X POST 'http://localhost:9080/novel/author/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "id": 1,
    "account": "tiancan",
    "password": "123456",
    "authorName": "天蚕土豆",
    "gender": "男",
    "avatar": "/upload/avatar_tiancan_new.jpg",
    "phone": "13900139000",
    "email": "tiancan@newmail.com"
  }'
```

### 3. 获取作者信息
```bash
curl 'http://localhost:9080/novel/author/get?account=tiancan'
```

**响应**:
```json
{
  "success": true,
  "error": null,
  "data": {
    "id": 1,
    "account": "tiancan",
    "password": null,
    "authorName": "天蚕土豆",
    "gender": "男",
    "avatar": "/upload/avatar_tiancan.jpg",
    "phone": "13800138000",
    "email": "tiancan@example.com",
    "createTime": "2024-12-11 09:00:00"
  }
}
```

### 4. 检查账号是否存在
```bash
curl 'http://localhost:9080/novel/author/exists?account=tiancan'
```

**响应**:
```json
{
  "success": true,
  "error": null,
  "data": true
}
```

### 5. 作者登录
```bash
curl -X POST 'http://localhost:9080/novel/author/login?account=tiancan&password=123456'
```

**成功响应**:
```json
{
  "success": true,
  "error": null,
  "data": {
    "id": 1,
    "account": "tiancan",
    "password": null,
    "authorName": "天蚕土豆",
    "gender": "男",
    "avatar": "/upload/avatar_tiancan.jpg",
    "phone": "13800138000",
    "email": "tiancan@example.com"
  }
}
```

**失败响应**:
```json
{
  "success": false,
  "error": "账号或密码错误",
  "data": null
}
```

### 6. 分页查询作者
```bash
curl 'http://localhost:9080/novel/author/list?pageNumber=1&pageSize=10'
```

### 7. 删除作者
```bash
# 删除单个
curl -X DELETE 'http://localhost:9080/novel/author/delete?accounts=tiancan'

# 删除多个
curl -X DELETE 'http://localhost:9080/novel/author/delete?accounts=tiancan&accounts=author2'
```

---

## 章节管理API

### 1. 添加章节
```bash
curl -X POST 'http://localhost:9080/novel/chapter/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "chapterTitle": "第一章 陨落的天才",
    "chapterContent": "「斗之力，三段！」\n望着测验魔石碑上面闪亮得甚至有些刺眼的五个大字，少年面无表情，唇角有着一抹自嘲，紧握的手掌，因为大力，而导致略微尖锐的指甲深深的刺进了掌心之中，带来一阵阵钻心的疼痛...",
    "chapterOrder": 1,
    "bookName": "斗破苍穹",
    "bookId": 1,
    "authorAccount": "tiancan",
    "authorName": "天蚕土豆"
  }'
```

### 2. 更新章节
```bash
curl -X POST 'http://localhost:9080/novel/chapter/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "id": 1,
    "chapterTitle": "第一章 陨落的天才（修订）",
    "chapterContent": "更新后的内容...",
    "chapterOrder": 1,
    "bookName": "斗破苍穹",
    "bookId": 1,
    "authorAccount": "tiancan",
    "authorName": "天蚕土豆"
  }'
```

### 3. 获取章节详情
```bash
curl 'http://localhost:9080/novel/chapter/get?id=1'
```

**响应**:
```json
{
  "success": true,
  "error": null,
  "data": {
    "id": 1,
    "chapterTitle": "第一章 陨落的天才",
    "chapterContent": "「斗之力，三段！」...",
    "chapterOrder": 1,
    "bookName": "斗破苍穹",
    "bookId": 1,
    "authorAccount": "tiancan",
    "authorName": "天蚕土豆",
    "publishTime": "2024-12-11 10:35:00",
    "createTime": "2024-12-11 10:35:00"
  }
}
```

### 4. 分页查询章节（所有）
```bash
curl 'http://localhost:9080/novel/chapter/list?pageNumber=1&pageSize=20'
```

### 5. 按小说查询章节（分页）
```bash
curl 'http://localhost:9080/novel/chapter/list?pageNumber=1&pageSize=20&bookId=1'
```

### 6. 按作者查询章节（分页）
```bash
curl 'http://localhost:9080/novel/chapter/list?pageNumber=1&pageSize=20&authorAccount=tiancan'
```

### 7. 查询小说的所有章节（不分页，按序号排序）
```bash
curl 'http://localhost:9080/novel/chapter/listByBook?bookId=1'
```

**响应**:
```json
{
  "success": true,
  "error": null,
  "data": [
    {
      "id": 1,
      "chapterTitle": "第一章 陨落的天才",
      "chapterContent": "...",
      "chapterOrder": 1,
      "bookName": "斗破苍穹",
      "bookId": 1
    },
    {
      "id": 2,
      "chapterTitle": "第二章 萧家",
      "chapterContent": "...",
      "chapterOrder": 2,
      "bookName": "斗破苍穹",
      "bookId": 1
    }
  ]
}
```

### 8. 删除章节
```bash
# 删除单个
curl -X DELETE 'http://localhost:9080/novel/chapter/delete?ids=1'

# 删除多个
curl -X DELETE 'http://localhost:9080/novel/chapter/delete?ids=1&ids=2&ids=3'
```

---

## 完整业务流程示例

### 场景：新作者发布小说

```bash
# 1. 注册作者
curl -X POST 'http://localhost:9080/novel/author/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "account": "newauthor",
    "password": "password123",
    "authorName": "新人作者",
    "gender": "男",
    "phone": "13900139000",
    "email": "newauthor@example.com"
  }'

# 2. 作者登录
curl -X POST 'http://localhost:9080/novel/author/login?account=newauthor&password=password123'

# 3. 发布小说
curl -X POST 'http://localhost:9080/novel/book/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "bookName": "我的第一部小说",
    "categoryName": "都市",
    "coverImage": "/upload/firstbook.jpg",
    "introduction": "这是我的第一部小说，请多多支持！",
    "authorAccount": "newauthor",
    "authorName": "新人作者"
  }'

# 4. 添加第一章
curl -X POST 'http://localhost:9080/novel/chapter/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "chapterTitle": "第一章 开始",
    "chapterContent": "故事从这里开始...",
    "chapterOrder": 1,
    "bookName": "我的第一部小说",
    "bookId": 2,
    "authorAccount": "newauthor",
    "authorName": "新人作者"
  }'

# 5. 添加第二章
curl -X POST 'http://localhost:9080/novel/chapter/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "chapterTitle": "第二章 冒险",
    "chapterContent": "冒险开始了...",
    "chapterOrder": 2,
    "bookName": "我的第一部小说",
    "bookId": 2,
    "authorAccount": "newauthor",
    "authorName": "新人作者"
  }'

# 6. 查看我的所有小说
curl 'http://localhost:9080/novel/book/listByAuthor?authorAccount=newauthor'

# 7. 查看小说的所有章节
curl 'http://localhost:9080/novel/chapter/listByBook?bookId=2'
```

### 场景：读者浏览小说

```bash
# 1. 查看首页小说列表（最新发布）
curl 'http://localhost:9080/novel/book/list?pageNumber=1&pageSize=10'

# 2. 按玄幻类型筛选
curl 'http://localhost:9080/novel/book/list?pageNumber=1&pageSize=10&categoryName=玄幻'

# 3. 查看小说详情（自动增加点击数）
curl 'http://localhost:9080/novel/book/get?id=1'

# 4. 查看小说的章节目录
curl 'http://localhost:9080/novel/chapter/listByBook?bookId=1'

# 5. 阅读第一章
curl 'http://localhost:9080/novel/chapter/get?id=1'

# 6. 阅读第二章
curl 'http://localhost:9080/novel/chapter/get?id=2'
```

---

## Postman导入

### 导出为Postman Collection

创建文件 `Novel_Platform_API.postman_collection.json`:

```json
{
  "info": {
    "name": "Novel Platform API",
    "description": "小说平台API集合",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Book Management",
      "item": [
        {
          "name": "Add Book",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"bookName\": \"斗破苍穹\",\n  \"categoryName\": \"玄幻\",\n  \"coverImage\": \"/upload/doupocangqiong.jpg\",\n  \"introduction\": \"三十年河东，三十年河西，莫欺少年穷！\",\n  \"authorAccount\": \"tiancan\",\n  \"authorName\": \"天蚕土豆\"\n}"
            },
            "url": {
              "raw": "http://localhost:9080/novel/book/add",
              "protocol": "http",
              "host": ["localhost"],
              "port": "9080",
              "path": ["novel", "book", "add"]
            }
          }
        },
        {
          "name": "Get Book",
          "request": {
            "method": "GET",
            "url": {
              "raw": "http://localhost:9080/novel/book/get?id=1",
              "protocol": "http",
              "host": ["localhost"],
              "port": "9080",
              "path": ["novel", "book", "get"],
              "query": [{"key": "id", "value": "1"}]
            }
          }
        },
        {
          "name": "List Books",
          "request": {
            "method": "GET",
            "url": {
              "raw": "http://localhost:9080/novel/book/list?pageNumber=1&pageSize=10",
              "protocol": "http",
              "host": ["localhost"],
              "port": "9080",
              "path": ["novel", "book", "list"],
              "query": [
                {"key": "pageNumber", "value": "1"},
                {"key": "pageSize", "value": "10"}
              ]
            }
          }
        }
      ]
    }
  ]
}
```

### 导入到Postman
1. 打开Postman
2. 点击 Import
3. 选择上面的JSON文件
4. 即可使用所有API

---

## 测试工具推荐

### 1. Swagger UI（推荐）
```
http://localhost:9080/swagger-ui/index.html
```
- ✅ 最直观
- ✅ 自动生成
- ✅ 在线测试

### 2. curl（命令行）
- ✅ 适合脚本测试
- ✅ 跨平台

### 3. Postman（客户端）
- ✅ 功能强大
- ✅ 团队协作

### 4. HTTPie（推荐命令行工具）
```bash
# 安装
pip install httpie

# 使用示例
http POST localhost:9080/novel/book/add \
  bookName="斗破苍穹" \
  categoryName="玄幻" \
  authorAccount="tiancan"
```

---

## 错误处理示例

### 参数校验失败
```bash
curl -X POST 'http://localhost:9080/novel/book/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "bookName": "",
    "categoryName": "玄幻"
  }'
```

**响应**:
```json
{
  "success": false,
  "error": "小说名称不能为空,作者账号不能为空",
  "data": null
}
```

### 资源不存在
```bash
curl 'http://localhost:9080/novel/book/get?id=99999'
```

**响应**:
```json
{
  "success": false,
  "error": "小说不存在",
  "data": null
}
```

---

**更多API请查看Swagger文档！** 📖

