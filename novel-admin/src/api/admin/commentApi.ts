import request from '@/api/request'

export interface Comment {
  id: number
  REF_ID: number
  USER_ID: number
  AVATAR_URL?: string
  NICKNAME?: string
  CONTENT: string
  REPLY?: string
  ADD_TIME?: string
  NOVEL_NAME?: string
}

export interface PageParams {
  page: number
  limit: number
  sort?: string
  order?: string
  refId?: number
  nickname?: string
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

// 分页查询评论
export function getCommentPage(params: PageParams) {
  return request.get<CompatResponse<PageData<Comment>>>('/discussxiaoshuoxinxi/page', { params })
}

// 获取评论详情
export function getCommentById(id: number) {
  return request.get<CompatResponse<Comment>>(`/discussxiaoshuoxinxi/info/${id}`)
}

// 回复评论
export function replyComment(data: { id: number; reply: string }) {
  return request.post<CompatResponse<null>>('/discussxiaoshuoxinxi/reply', data)
}

// 删除评论
export function deleteComments(ids: number[]) {
  return request.post<CompatResponse<null>>('/discussxiaoshuoxinxi/delete', ids)
}
