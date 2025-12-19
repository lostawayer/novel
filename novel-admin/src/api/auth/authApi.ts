import axios from 'axios'

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
  const response = await axios.post(`/novel/author/login`, null, {
    params: {
      account: params.account,
      password: params.password
    }
  })
  if (response.data && response.data.success) {
    return response.data.data
  }
  throw new Error(response.data?.error || '登录失败')
}

// 管理员登录
export async function adminLoginApi(params: AdminLoginParams): Promise<AdminUser> {
  const response = await axios.post(`/users/login`, null, {
    params: {
      username: params.username,
      password: params.password
    }
  })
  if (response.data && response.data.code === 0) {
    return {
      id: response.data.data?.id || 0,
      username: params.username,
      token: response.data.token
    }
  }
  throw new Error(response.data?.msg || '登录失败')
}

// 获取作者信息
export async function getAuthorInfoApi(account: string): Promise<Author> {
  const response = await axios.get(`/novel/author/get`, {
    params: { account }
  })
  if (response.data && response.data.success) {
    return response.data.data
  }
  throw new Error(response.data?.error || '获取信息失败')
}

// 检查账号是否存在
export async function checkAccountExistsApi(account: string): Promise<boolean> {
  const response = await axios.get(`/novel/author/exists`, {
    params: { account }
  })
  return response.data?.data || false
}

// 更新作者信息
export async function updateAuthorInfoApi(data: Partial<Author>): Promise<void> {
  const response = await axios.post(`/novel/author/add`, data)
  if (!response.data?.success) {
    throw new Error(response.data?.error || '更新失败')
  }
}
