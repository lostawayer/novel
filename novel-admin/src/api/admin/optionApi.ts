import request from '@/api/request'

export interface Option {
  id?: number
  label: string
  value: string
}

export interface CompatResponse<T> {
  code: number
  msg?: string
  data: T
}

// 获取小说类型选项
export function getCategoryOptions() {
  return request.get<CompatResponse<Option[]>>('/option/category')
}

// 获取作者选项
export function getAuthorOptions() {
  return request.get<CompatResponse<Option[]>>('/option/author')
}

// 获取审核状态选项
export function getAuditStatusOptions() {
  return request.get<CompatResponse<Option[]>>('/option/auditStatus')
}

// 获取性别选项
export function getGenderOptions() {
  return request.get<CompatResponse<Option[]>>('/option/gender')
}

// 获取VIP选项
export function getVipOptions() {
  return request.get<CompatResponse<Option[]>>('/option/vip')
}
