import request from '@/api/request'
import type { DataPage } from '@/api/commons/dataPage'

// 书籍信息
export interface Novel {
  id?: number
  bookName: string
  categoryName: string
  coverImage?: string
  introduction?: string
  authorAccount?: string
  authorName?: string
  publishTime?: string
  clickCount?: number
  price?: number  // 书籍价格
}

// 书籍章节
export interface NovelChapter {
  id?: number
  chapterTitle: string
  chapterContent?: string
  chapterOrder: number
  bookName?: string
  bookId: number
  authorAccount?: string
  authorName?: string
  publishTime?: string
  vipOnly?: string // VIP章节标识：是/否
}

// ========== 书籍信息 API ==========
export async function findNovelApi(
  page: number,
  limit: number,
  params?: { bookName?: string; categoryName?: string; authorAccount?: string }
): Promise<DataPage<Novel>> {
  const queryParams: any = { pageNumber: page, pageSize: limit }
  if (params?.bookName) queryParams.bookName = params.bookName
  if (params?.categoryName) queryParams.categoryName = params.categoryName
  if (params?.authorAccount) queryParams.authorAccount = params.authorAccount

  const response = await request.get(`/novel/book/list`, { params: queryParams, baseURL: '' })
  const data = response.data
  if (data && data.success) {
    const result = data.data
    return {
      pageNumber: result.pageNumber || page,
      pageSize: result.pageSize || limit,
      totalCount: result.totalCount || 0,
      content: result.data || result.content || []
    }
  }
  throw new Error(data?.error || '获取数据失败')
}

export async function getNovelApi(id: number): Promise<Novel> {
  const response = await request.get(`/novel/book/get`, {
    params: { id },
    baseURL: ''
  })
  const data = response.data
  if (data && data.success) {
    return data.data
  }
  throw new Error(data?.error || '获取数据失败')
}

export async function saveOrUpdateNovelApi(novel: Novel): Promise<void> {
  const response = await request.post(`/novel/book/add`, novel, { baseURL: '' })
  if (!response.data?.success) {
    throw new Error(response.data?.error || '操作失败')
  }
}

export async function deleteNovelApi(ids: number[]): Promise<void> {
  const params = new URLSearchParams()
  ids.forEach((id) => params.append('ids', String(id)))
  const response = await request.delete(`/novel/book/delete?${params.toString()}`, { baseURL: '' })
  if (!response.data?.success) {
    throw new Error(response.data?.error || '删除失败')
  }
}

// 获取作者的所有书籍
export async function getNovelsByAuthorApi(authorAccount: string): Promise<Novel[]> {
  const response = await request.get(`/novel/book/listByAuthor`, {
    params: { authorAccount },
    baseURL: ''
  })
  const data = response.data
  if (data && data.success) {
    return data.data || []
  }
  throw new Error(data?.error || '获取数据失败')
}

// ========== 书籍章节 API ==========
export async function findChapterApi(
  page: number,
  limit: number,
  bookId?: number
): Promise<DataPage<NovelChapter>> {
  const params: any = { pageNumber: page, pageSize: limit }
  if (bookId) params.bookId = bookId

  const response = await request.get(`/novel/chapter/list`, { params, baseURL: '' })
  const data = response.data
  if (data && data.success) {
    const result = data.data
    return {
      pageNumber: result.pageNumber || page,
      pageSize: result.pageSize || limit,
      totalCount: result.totalCount || 0,
      content: result.data || result.content || []
    }
  }
  throw new Error(data?.error || '获取数据失败')
}

export async function getChapterApi(id: number): Promise<NovelChapter> {
  const response = await request.get(`/novel/chapter/get`, {
    params: { id },
    baseURL: ''
  })
  const data = response.data
  if (data && data.success) {
    return data.data
  }
  throw new Error(data?.error || '获取数据失败')
}

export async function saveOrUpdateChapterApi(chapter: NovelChapter): Promise<void> {
  const response = await request.post(`/novel/chapter/add`, chapter, { baseURL: '' })
  if (!response.data?.success) {
    throw new Error(response.data?.error || '操作失败')
  }
}

export async function deleteChapterApi(ids: number[]): Promise<void> {
  const params = new URLSearchParams()
  ids.forEach((id) => params.append('ids', String(id)))
  const response = await request.delete(`/novel/chapter/delete?${params.toString()}`, { baseURL: '' })
  if (!response.data?.success) {
    throw new Error(response.data?.error || '删除失败')
  }
}

// 获取书籍的所有章节（不分页）
export async function getChaptersByBookApi(bookId: number): Promise<NovelChapter[]> {
  const response = await request.get(`/novel/chapter/listByBook`, {
    params: { bookId },
    baseURL: ''
  })
  const data = response.data
  if (data && data.success) {
    return data.data || []
  }
  throw new Error(data?.error || '获取数据失败')
}
