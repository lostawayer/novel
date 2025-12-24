// ==================== 通用类型 ====================

/**
 * API 响应结构
 */
export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
  token?: string
}

/**
 * 分页结果
 */
export interface PageResult<T = any> {
  total: number
  list: T[]
  pageSize: number
  currPage: number
}

// ==================== 用户相关 ====================

/**
 * 用户信息
 */
export interface User {
  id?: number
  yonghuming?: string
  mima?: string
  xingming?: string
  touxiang?: string
  xingbie?: string
  shoujihaoma?: string
  youxiang?: string
  money?: number
  role?: string
}

/**
 * 登录表单
 */
export interface LoginForm {
  username: string
  password: string
  role?: string
}

/**
 * 注册表单
 */
export interface RegisterForm {
  yonghuming: string
  mima: string
  mima2?: string
  xingming?: string
  xingbie?: string
  shoujihaoma?: string
  youxiang?: string
}

// ==================== 作者相关 ====================

/**
 * 作者信息
 */
export interface Author {
  id?: number
  zuozhezhanghao?: string
  mima?: string
  zuozhexingming?: string
  xingbie?: string
  touxiang?: string
  lianxifangshi?: string
  youxiang?: string
  money?: number
}

// ==================== 书籍相关 ====================

/**
 * 书籍分类
 */
export interface NovelCategory {
  id?: number
  xiaoshuoleixing?: string
}

/**
 * 书籍信息
 */
export interface Novel {
  id?: number
  xiaoshuomingcheng?: string
  xiaoshuoleixing?: string
  fengmian?: string
  zuozhe?: string
  chubanshe?: string
  jiage?: number
  clicktime?: string
  clicknum?: number
  storeupnum?: number
  xiaoshuojieshao?: string
}

/**
 * 书籍详情
 */
export interface NovelDetail extends Novel {
  chapters?: Chapter[]
}

/**
 * 书籍章节
 */
export interface Chapter {
  id?: number
  xiaoshuomingcheng?: string
  zhangjiebianhao?: string
  zhangjiemingcheng?: string
  fabushijian?: string
  neirong?: string
  zuozhe?: string
}

// ==================== 评论相关 ====================

/**
 * 评论信息
 */
export interface Comment {
  id?: number
  refid?: number
  userid?: number
  nickname?: string
  content?: string
  reply?: string
  avatarurl?: string
  addtime?: string
}

// ==================== 收藏相关 ====================

/**
 * 收藏信息
 */
export interface Storeup {
  id?: number
  userid?: number
  refid?: number
  tablename?: string
  name?: string
  picture?: string
  type?: string
  inteltype?: string
  remark?: string
  addtime?: string
}

// ==================== 新闻公告 ====================

/**
 * 新闻信息
 */
export interface News {
  id?: number
  title?: string
  introduction?: string
  picture?: string
  content?: string
  addtime?: string
}

// ==================== 消息相关 ====================

/**
 * 消息信息
 */
export interface Message {
  id?: number
  userid?: number
  username?: string
  content?: string
  cpicture?: string
  reply?: string
  rpicture?: string
  addtime?: string
}

// ==================== 配置相关 ====================

/**
 * 系统配置
 */
export interface SysConfig {
  id?: number
  name?: string
  value?: string
}

// ==================== 路由相关 ====================

/**
 * 路由 Meta 信息
 */
export interface RouteMeta {
  title?: string
  requireAuth?: boolean
  role?: string[]
}

// ==================== 表单规则 ====================

/**
 * 表单验证规则
 */
export interface FormRule {
  required?: boolean
  message?: string
  trigger?: string | string[]
  validator?: (rule: any, value: any, callback: any) => void
  min?: number
  max?: number
  pattern?: RegExp
}

/**
 * 表单规则集合
 */
export type FormRules = Record<string, FormRule[]>

