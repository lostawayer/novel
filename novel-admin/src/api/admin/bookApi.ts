import request from '@/api/request'

export interface NovelInfo {
  id: number
  NOVEL_NAME: string
  CATEGORY_NAME: string
  PICTURE?: string
  DESCRIPTION?: string
  ACCOUNT?: string
  AUTHOR_NAME?: string
  PUBLISH_TIME?: string
  CLICK_TIME?: string
  ADD_TIME?: string
}

export interface PageParams {
  page: number
  limit: number
  sort?: string
  order?: string
  novelName?: string
  categoryName?: string
  authorName?: string
}

export interface CompatResponse<T> {
  code: number
  msg?: string
  data: T
}

export interface PageData<T> {
  list: T[]
  total: number
}

// 分页查询小说
export function getBookPage(params: PageParams) {
  return request.get<CompatResponse<PageData<NovelInfo>>>('/xiaoshuoxinxi/page', { params })
}

// 获取小说详情
export function getBookById(id: number) {
  return request.get<CompatResponse<NovelInfo>>(`/xiaoshuoxinxi/info/${id}`)
}

// 新增小说
export function addBook(data: {
  novelName: string
  categoryName: string
  picture?: string
  description?: string
  account?: string
  authorName?: string
}) {
  return request.post<CompatResponse<null>>('/xiaoshuoxinxi/save', data)
}

// 更新小说
export function updateBook(data: {
  id: number
  novelName?: string
  categoryName?: string
  picture?: string
  description?: string
}) {
  return request.post<CompatResponse<null>>('/xiaoshuoxinxi/update', data)
}

// 删除小说
export function deleteBooks(ids: number[]) {
  return request.post<CompatResponse<null>>('/xiaoshuoxinxi/delete', ids)
}
