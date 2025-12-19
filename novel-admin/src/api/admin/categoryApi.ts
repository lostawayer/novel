import request from '@/api/request'

export interface Category {
  id: number
  CATEGORY_NAME: string
  ADD_TIME?: string
}

export interface PageParams {
  page: number
  limit: number
  sort?: string
  order?: string
  categoryName?: string
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

// 分页查询类型
export function getCategoryPage(params: PageParams) {
  return request.get<CompatResponse<PageData<Category>>>('/xiaoshuoleixing/page', { params })
}

// 获取类型详情
export function getCategoryById(id: number) {
  return request.get<CompatResponse<Category>>(`/xiaoshuoleixing/info/${id}`)
}

// 新增类型
export function addCategory(data: { categoryName: string }) {
  return request.post<CompatResponse<null>>('/xiaoshuoleixing/save', data)
}

// 更新类型
export function updateCategory(data: { id: number; categoryName: string }) {
  return request.post<CompatResponse<null>>('/xiaoshuoleixing/update', data)
}

// 删除类型
export function deleteCategories(ids: number[]) {
  return request.post<CompatResponse<null>>('/xiaoshuoleixing/delete', ids)
}
