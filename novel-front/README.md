# Novel Front - 小说网站系统前端

基于 Vue 3 + TypeScript + Vite + Element Plus 的小说网站系统前端项目。

## 技术栈

- **框架**: Vue 3.5.24
- **构建工具**: Vite 7.2.4
- **语言**: TypeScript 5.9.3
- **UI 框架**: Element Plus 2.9.2
- **状态管理**: Pinia 2.3.0
- **路由**: Vue Router 4.5.0
- **HTTP 请求**: Axios 1.7.9
- **富文本编辑器**: @vueup/vue-quill 1.2.0

## 项目结构

```
novel-front/
├── public/              # 静态资源
├── src/
│   ├── assets/         # 资源文件（CSS、图片等）
│   ├── common/         # 公共工具
│   │   ├── storage.ts  # localStorage 封装
│   │   ├── system.ts   # 系统工具函数
│   │   └── validate.ts # 表单验证
│   ├── components/     # 公共组件
│   │   ├── Breadcrumb.vue     # 面包屑
│   │   ├── CountDown.vue      # 倒计时
│   │   ├── Editor.vue         # 富文本编辑器
│   │   ├── FileUpload.vue     # 文件上传
│   │   └── img.vue            # 图片/拍照组件
│   ├── config/         # 配置文件
│   │   └── config.ts   # 项目配置
│   ├── pages/          # 页面组件
│   │   ├── home/       # 首页
│   │   ├── login/      # 登录
│   │   ├── register/   # 注册
│   │   ├── center/     # 个人中心
│   │   ├── messages/   # 留言板
│   │   ├── xiaoshuoxinxi/  # 小说管理
│   │   └── index.vue   # 主布局
│   ├── router/         # 路由配置
│   │   └── index.ts
│   ├── store/          # 状态管理
│   │   └── index.ts
│   ├── types/          # TypeScript 类型定义
│   │   └── index.ts
│   ├── utils/          # 工具类
│   │   └── request.ts  # Axios 封装
│   ├── App.vue         # 根组件
│   └── main.ts         # 入口文件
├── .env.development    # 开发环境变量（需手动创建）
├── .env.production     # 生产环境变量（需手动创建）
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 配置环境变量

创建 `.env.development` 文件：

```env
# API 基础路径
VITE_APP_BASE_URL=http://localhost:8088

# 应用标题
VITE_APP_TITLE=小说网站系统
```

创建 `.env.production` 文件：

```env
# API 基础路径
VITE_APP_BASE_URL=http://your-production-url

# 应用标题
VITE_APP_TITLE=小说网站系统
```

### 3. 复制静态资源

从老项目复制以下静态资源到 `src/assets/` 目录：

- **图片文件**: `*.png`, `*.jpg`
- **字体文件**: `iconfont/` 目录（包含 .ttf, .woff, .woff2）
- **JS 文件**: `js/` 目录下的 canvas-bg-*.js 文件

### 4. 启动开发服务器

```bash
npm run dev
```

访问: http://localhost:8080

### 5. 构建生产版本

```bash
npm run build
```

构建后的文件在 `dist` 目录。

## 主要功能

- ✅ 用户注册/登录
- ✅ 首页展示（推荐、最新、热门小说）
- ✅ 小说列表（搜索、分页）
- ✅ 小说详情
- ✅ 个人中心
- ✅ 留言板
- ✅ 收藏功能
- ✅ 文件上传
- ✅ 富文本编辑器

## 待完善功能

以下功能需要根据具体业务逻辑继续开发：

1. **小说相关页面**：
   - `xiaoshuoxinxi/detail.vue` - 小说详情页
   - `xiaoshuoxinxi/add.vue` - 添加/编辑小说
   - `xiaoshuoxinxi/chapter.vue` - 章节管理

2. **用户相关页面**：
   - `yonghu/` 目录下的页面
   - `zuozhe/` 目录下的页面

3. **其他业务页面**：
   - `news/` - 新闻公告
   - `storeup/` - 收藏列表
   - 各分类管理页面

## 开发规范

### 组件开发

- 使用 Composition API (`<script setup lang="ts">`)
- 使用 TypeScript 类型定义
- Props 和 Emits 需要明确类型

```vue
<script setup lang="ts">
interface Props {
  title: string
  count?: number
}

const props = withDefaults(defineProps<Props>(), {
  count: 0
})

const emit = defineEmits<{
  change: [value: number]
}>()
</script>
```

### 状态管理

使用 Pinia store：

```typescript
import { useUserStore } from '@/store'

const userStore = useUserStore()
console.log(userStore.userInfo)
```

### API 请求

```typescript
import { get, post } from '@/utils/request'

// GET 请求
const res = await get('/api/list', { page: 1 })

// POST 请求
const res = await post('/api/add', { name: 'test' })
```

### 路由跳转

```typescript
import { useRouter } from 'vue-router'

const router = useRouter()

// 跳转
router.push('/login')
router.push({ path: '/detail', query: { id: 1 } })
```

## 常见问题

### 1. 启动报错找不到模块

确保已经安装所有依赖：
```bash
npm install
```

### 2. 图片显示不出来

检查：
- 静态资源是否已从老项目复制到 `src/assets/`
- API 基础路径配置是否正确
- `getImageUrl` 函数使用是否正确

### 3. 接口请求失败

检查：
- `.env.development` 文件中的 `VITE_APP_BASE_URL` 是否正确
- 后端服务是否已启动
- 浏览器控制台查看具体错误信息

## 注意事项

1. 本项目由 Vue 2 迁移至 Vue 3，部分页面为简化版本
2. 需要根据实际业务需求完善未实现的页面
3. 图片和字体等静态资源需要手动从老项目复制
4. 环境变量文件（.env.*）需要手动创建

## License

MIT
