import request from '@/api/request'

export interface News {
  id: number
  title: string
  introduction?: string
  picture?: string
  content?: string
  addtime?: string
}

export interface PageParams {
  page: number
  limit: number
  sort?: string
  order?: string
  title?: string
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

// 分页查询公告
export function getNewsPage(params: PageParams) {
  return request.get<CompatResponse<PageData<News>>>('/news/page', { params })
}

// 获取公告详情
export function getNewsById(id: number) {
  return request.get<CompatResponse<News>>(`/news/info/${id}`)
}

// 新增公告
export function addNews(data: {
  title: string
  introduction?: string
  picture?: string
  content?: string
}) {
  return request.post<CompatResponse<null>>('/news/save', data)
}

// 更新公告
export function updateNews(data: {
  id: number
  title?: string
  introduction?: string
  picture?: string
  content?: string
}) {
  return request.post<CompatResponse<null>>('/news/update', data)
}

// 删除公告
export function deleteNews(ids: number[]) {
  return request.post<CompatResponse<null>>('/news/delete', ids)
}
