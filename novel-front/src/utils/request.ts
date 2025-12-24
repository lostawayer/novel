/**
 * Axios 封装
 */

import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken, removeUserInfo, removeRole } from '@/common/storage'
import router from '@/router'
import type { ApiResponse } from '@/types'

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_URL || '',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  },
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 添加 Token
    const token = getToken()
    if (token) {
      config.headers['Token'] = token
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data

    // 下载文件直接返回
    if (response.config.responseType === 'blob') {
      return response as any
    }

    // 请求成功 - 直接返回数据
    if (res.code === 0 || res.code === 200) {
      return res as any
    }

    // 未登录或 token 过期
    if (res.code === 401 || res.code === 403) {
      ElMessage.error(res.msg || '登录已过期，请重新登录')
      removeToken()
      removeUserInfo()
      removeRole()
      router.replace('/login')
      return Promise.reject(new Error(res.msg || '登录已过期'))
    }

    // 其他错误 - 返回原始响应让调用方处理
    return res as any
  },
  (error) => {
    console.error('响应错误:', error)

    let message = '网络错误，请稍后重试'

    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '登录已过期，请重新登录'
          removeToken()
          removeUserInfo()
          removeRole()
          router.replace('/login')
          break
        case 403:
          message = '没有权限访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器错误'
          break
        default:
          message = error.response.data?.msg || '请求失败'
      }
    } else if (error.request) {
      message = '网络连接失败，请检查网络'
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }
)

/**
 * 通用请求方法
 */
export function request<T = any>(config: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.request<any, ApiResponse<T>>(config)
}

/**
 * GET 请求
 */
export function get<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return request<T>({
    method: 'GET',
    url,
    params,
    ...config,
  })
}

/**
 * POST 请求
 */
export function post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return request<T>({
    method: 'POST',
    url,
    data,
    ...config,
  })
}

/**
 * PUT 请求
 */
export function put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return request<T>({
    method: 'PUT',
    url,
    data,
    ...config,
  })
}

/**
 * DELETE 请求
 */
export function del<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return request<T>({
    method: 'DELETE',
    url,
    params,
    ...config,
  })
}

export default service

