import request from '@/api/request'

export interface LoginParams {
  account: string
  password: string
}

export interface AdminLoginParams {
  username: string
  password: string
}

export interface Author {
  id: number
  account: string
  password?: string
  authorName: string
  gender?: string
  avatar?: string
  phone?: string
  email?: string
}

export interface AdminUser {
  id: number
  username: string
  token: string
}

// 作者登录
export async function loginApi(params: LoginParams): Promise<Author> {
  const response = await request.post('/novel/author/login', null, {
    params: {
      account: params.account,
      password: params.password
    },
    baseURL: '' // 直接请求 /novel/author/login
  })
  const data = response.data
  if (data && data.success) {
    return data.data
  }
  throw new Error(data?.error || '登录失败')
}

// 管理员登录
export async function adminLoginApi(params: AdminLoginParams): Promise<AdminUser> {
  const response = await request.post('/users/login', null, {
    params: {
      username: params.username,
      password: params.password
    },
    baseURL: '' // 管理员登录不需要 /api 前缀
  })
  const data = response.data
  if (data && data.code === 0) {
    return {
      id: data.data?.id || 0,
      username: params.username,
      token: data.token
    }
  }
  throw new Error(data?.msg || '登录失败')
}

// 获取作者信息
export async function getAuthorInfoApi(account: string): Promise<Author> {
  const response = await request.get('/novel/author/get', {
    params: { account },
    baseURL: ''
  })
  const data = response.data
  if (data && data.success) {
    return data.data
  }
  throw new Error(data?.error || '获取信息失败')
}

// 检查账号是否存在
export async function checkAccountExistsApi(account: string): Promise<boolean> {
  const response = await request.get('/novel/author/exists', {
    params: { account },
    baseURL: ''
  })
  return response.data?.data || false
}

// 更新作者信息
export async function updateAuthorInfoApi(data: Partial<Author>): Promise<void> {
  const response = await request.post('/novel/author/add', data, {
    baseURL: ''
  })
  if (!response.data?.success) {
    throw new Error(response.data?.error || '更新失败')
  }
}
