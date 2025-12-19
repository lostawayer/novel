import axios from 'axios'
import type { DataPage } from '@/api/commons/dataPage'

export interface Author {
  id?: number
  account: string
  password?: string
  authorName: string
  gender?: string
  avatar?: string
  phone?: string
  email?: string
  createTime?: string
}

// 获取作者列表
export async function findAuthorApi(
  page: number,
  limit: number,
  account?: string
): Promise<DataPage<Author>> {
  const params: any = { pageNumber: page, pageSize: limit }
  if (account) params.account = account

  const response = await axios.get(`/novel/author/list`, { params })
  const data = response.data?.data || response.data
  return {
    pageNumber: data.pageNumber || page,
    pageSize: data.pageSize || limit,
    totalCount: data.totalCount || 0,
    content: data.content || []
  }
}

// 获取作者详情
export async function getAuthorApi(account: string): Promise<Author> {
  const response = await axios.get(`/novel/author/get`, {
    params: { account }
  })
  return response.data?.data || response.data
}

// 新增或更新作者
export async function saveOrUpdateAuthorApi(author: Author): Promise<void> {
  await axios.post(`/novel/author/add`, author)
}

// 删除作者
export async function deleteAuthorApi(accounts: string[]): Promise<void> {
  const params = new URLSearchParams()
  accounts.forEach((account) => params.append('accounts', account))
  await axios.delete(`/novel/author/delete?${params.toString()}`)
}

// 注册作者
export async function registerAuthorApi(author: Author): Promise<void> {
  await axios.post(`/novel/author/add`, author)
}
