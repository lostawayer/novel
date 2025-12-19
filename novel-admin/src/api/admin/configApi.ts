import request from '@/api/request'

export interface Config {
  id: number
  NAME: string
  VALUE?: string
}

export interface PageParams {
  page: number
  limit: number
  sort?: string
  order?: string
  name?: string
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

// 分页查询配置
export function getConfigPage(params: PageParams) {
  return request.get<CompatResponse<PageData<Config>>>('/config/page', { params })
}

// 获取配置详情
export function getConfigById(id: number) {
  return request.get<CompatResponse<Config>>(`/config/info/${id}`)
}

// 更新配置
export function updateConfig(data: { id: number; value: string }) {
  return request.post<CompatResponse<null>>('/config/update', data)
}
