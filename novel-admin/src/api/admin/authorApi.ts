import request from '@/api/request'

export interface Author {
  ID: number
  ACCOUNT: string
  PASSWORD?: string
  AUTHOR_NAME: string
  GENDER?: string
  AVATAR?: string
  AGE?: string
  ID_CARD?: string
  PHONE?: string
  EMAIL?: string
  AUDIT_STATUS?: string
  AUDIT_REPLY?: string
  CREATE_TIME?: string
}

export interface PageParams {
  page: number
  limit: number
  sort?: string
  order?: string
  account?: string
  authorName?: string
  auditStatus?: string
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

// 分页查询作者
export function getAuthorPage(params: PageParams) {
  return request.get<CompatResponse<PageData<Author>>>('/zuozhe/page', { params })
}

// 获取作者详情
export function getAuthorById(id: number) {
  return request.get<CompatResponse<Author>>(`/zuozhe/info/${id}`)
}

// 新增作者
export function addAuthor(data: {
  account: string
  password: string
  authorName: string
  gender?: string
  avatar?: string
  age?: string
  idCard?: string
  phone?: string
  email?: string
}) {
  return request.post<CompatResponse<null>>('/zuozhe/save', data)
}

// 更新作者
export function updateAuthor(data: {
  id: number
  authorName?: string
  gender?: string
  avatar?: string
  age?: string
  idCard?: string
  phone?: string
  email?: string
  auditStatus?: string
  auditReply?: string
}) {
  return request.post<CompatResponse<null>>('/zuozhe/update', data)
}

// 审核作者
export function auditAuthor(data: { id: number; auditStatus: string; auditReply?: string }) {
  return request.post<CompatResponse<null>>('/zuozhe/audit', data)
}

// 删除作者
export function deleteAuthors(ids: number[]) {
  return request.post<CompatResponse<null>>('/zuozhe/delete', ids)
}
