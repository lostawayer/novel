export interface DataPage<T> {
  pageNumber: number
  pageSize: number
  totalCount: number
  content: T[]
}

export interface PageParams {
  page: number
  limit: number
  sort?: string
  order?: 'asc' | 'desc'
}
