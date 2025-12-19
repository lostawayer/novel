import axios from 'axios'
import type { DataPage } from '@/api/commons/dataPage'

// 小说信息
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
}

// 小说章节
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
}

// ========== 小说信息 API ==========
export async function findNovelApi(
  page: number,
  limit: number,
  params?: { bookName?: string; categoryName?: string; authorAccount?: string }
): Promise<DataPage<Novel>> {
  const queryParams: any = { pageNumber: page, pageSize: limit }
  if (params?.bookName) queryParams.bookName = params.bookName
  if (params?.categoryName) queryParams.categoryName = params.categoryName
  if (params?.authorAccount) queryParams.authorAccount = params.authorAccount

  const response = await axios.get(`/novel/book/list`, { params: queryParams })
  const data = response.data?.data || response.data
  return {
    pageNumber: data.pageNumber || page,
    pageSize: data.pageSize || limit,
    totalCount: data.totalCount || 0,
    content: data.content || []
  }
}

export async function getNovelApi(id: number): Promise<Novel> {
  const response = await axios.get(`/novel/book/get`, {
    params: { id }
  })
  return response.data?.data || response.data
}

export async function saveOrUpdateNovelApi(novel: Novel): Promise<void> {
  await axios.post(`/novel/book/add`, novel)
}

export async function deleteNovelApi(ids: number[]): Promise<void> {
  const params = new URLSearchParams()
  ids.forEach((id) => params.append('ids', String(id)))
  await axios.delete(`/novel/book/delete?${params.toString()}`)
}

// 获取作者的所有小说
export async function getNovelsByAuthorApi(authorAccount: string): Promise<Novel[]> {
  const response = await axios.get(`/novel/book/listByAuthor`, {
    params: { authorAccount }
  })
  return response.data?.data || response.data || []
}

// ========== 小说章节 API ==========
export async function findChapterApi(
  page: number,
  limit: number,
  bookId?: number
): Promise<DataPage<NovelChapter>> {
  const params: any = { pageNumber: page, pageSize: limit }
  if (bookId) params.bookId = bookId

  const response = await axios.get(`/novel/chapter/list`, { params })
  const data = response.data?.data || response.data
  return {
    pageNumber: data.pageNumber || page,
    pageSize: data.pageSize || limit,
    totalCount: data.totalCount || 0,
    content: data.content || []
  }
}

export async function getChapterApi(id: number): Promise<NovelChapter> {
  const response = await axios.get(`/novel/chapter/get`, {
    params: { id }
  })
  return response.data?.data || response.data
}

export async function saveOrUpdateChapterApi(chapter: NovelChapter): Promise<void> {
  await axios.post(`/novel/chapter/add`, chapter)
}

export async function deleteChapterApi(ids: number[]): Promise<void> {
  const params = new URLSearchParams()
  ids.forEach((id) => params.append('ids', String(id)))
  await axios.delete(`/novel/chapter/delete?${params.toString()}`)
}

// 获取小说的所有章节（不分页）
export async function getChaptersByBookApi(bookId: number): Promise<NovelChapter[]> {
  const response = await axios.get(`/novel/chapter/listByBook`, {
    params: { bookId }
  })
  return response.data?.data || response.data || []
}
