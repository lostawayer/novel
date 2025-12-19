import request from '@/api/request'

export interface Chapter {
  id: number
  REF_ID: number
  CHAPTER_NUM: number
  CHAPTER_TITLE: string
  CONTENT?: string
  VIP_READ?: string
  ADD_TIME?: string
}

export interface PageParams {
  page: number
  limit: number
  sort?: string
  order?: string
  refId?: number
  chapterTitle?: string
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

// 分页查询章节
export function getChapterPage(params: PageParams) {
  return request.get<CompatResponse<PageData<Chapter>>>('/chapterxiaoshuoxinxi/page', { params })
}

// 获取章节详情
export function getChapterById(id: number) {
  return request.get<CompatResponse<Chapter>>(`/chapterxiaoshuoxinxi/info/${id}`)
}

// 新增章节
export function addChapter(data: {
  refId: number
  chapterNum: number
  chapterTitle: string
  content: string
  vipRead?: string
}) {
  return request.post<CompatResponse<null>>('/chapterxiaoshuoxinxi/save', data)
}

// 更新章节
export function updateChapter(data: {
  id: number
  chapterNum?: number
  chapterTitle?: string
  content?: string
  vipRead?: string
}) {
  return request.post<CompatResponse<null>>('/chapterxiaoshuoxinxi/update', data)
}

// 删除章节
export function deleteChapters(ids: number[]) {
  return request.post<CompatResponse<null>>('/chapterxiaoshuoxinxi/delete', ids)
}
