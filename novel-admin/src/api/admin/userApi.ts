import request from '@/api/request'

export interface Reader {
  id: number
  USERNAME: string
  PASSWORD?: string
  NICKNAME: string
  REAL_NAME: string
  GENDER?: string
  AVATAR?: string
  EMAIL?: string
  PHONE?: string
  VIP?: string
  ADD_TIME?: string
}

export interface PageParams {
  page: number
  limit: number
  sort?: string
  order?: string
  username?: string
  realName?: string
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

// 分页查询用户
export function getUserPage(params: PageParams) {
  return request.get<CompatResponse<PageData<Reader>>>('/yonghu/page', { params })
}

// 获取用户详情
export function getUserById(id: number) {
  return request.get<CompatResponse<Reader>>(`/yonghu/info/${id}`)
}

// 新增用户
export function addUser(data: {
  username: string
  password: string
  nickname: string
  realName: string
  gender?: string
  avatar?: string
  email?: string
  phone?: string
  vip?: string
}) {
  return request.post<CompatResponse<null>>('/yonghu/save', data)
}

// 更新用户
export function updateUser(data: {
  id: number
  nickname?: string
  realName?: string
  gender?: string
  avatar?: string
  email?: string
  phone?: string
  vip?: string
}) {
  return request.post<CompatResponse<null>>('/yonghu/update', data)
}

// 删除用户
export function deleteUsers(ids: number[]) {
  return request.post<CompatResponse<null>>('/yonghu/delete', ids)
}
