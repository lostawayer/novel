import request from '@/api/request'

export interface Message {
  id: number
  USER_ID: number
  USERNAME?: string
  AVATAR_URL?: string
  CONTENT: string
  C_PICTURE?: string
  REPLY?: string
  R_PICTURE?: string
  ADD_TIME?: string
}

export interface PageParams {
  page: number
  limit: number
  sort?: string
  order?: string
  username?: string
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

// 分页查询留言
export function getMessagePage(params: PageParams) {
  return request.get<CompatResponse<PageData<Message>>>('/messages/page', { params })
}

// 获取留言详情
export function getMessageById(id: number) {
  return request.get<CompatResponse<Message>>(`/messages/info/${id}`)
}

// 回复留言
export function replyMessage(data: { id: number; reply: string; rPicture?: string }) {
  return request.post<CompatResponse<null>>('/messages/reply', data)
}

// 删除留言
export function deleteMessages(ids: number[]) {
  return request.post<CompatResponse<null>>('/messages/delete', ids)
}
