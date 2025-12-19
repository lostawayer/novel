/**
 * 系统工具函数
 */

import { getToken, getRole } from './storage'

/**
 * 判断是否有权限
 * @param authName 权限名称
 */
export function isAuth(authName: string): boolean {
  // TODO: 实现权限判断逻辑
  const token = getToken()
  if (!token) {
    return false
  }
  return true
}

/**
 * 获取当前日期时间
 * @param format 格式化字符串，默认 'yyyy-MM-dd hh:mm:ss'
 */
export function getCurDateTime(format: string = 'yyyy-MM-dd hh:mm:ss'): string {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const date = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')

  return format
    .replace('yyyy', String(year))
    .replace('MM', month)
    .replace('dd', date)
    .replace('hh', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 获取当前日期
 */
export function getCurDate(): string {
  return getCurDateTime('yyyy-MM-dd')
}

/**
 * 格式化日期时间
 * @param date 日期对象或时间戳
 * @param format 格式化字符串
 */
export function formatDateTime(
  date: Date | number | string,
  format: string = 'yyyy-MM-dd hh:mm:ss'
): string {
  const d = typeof date === 'string' ? new Date(date) : new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('yyyy', String(year))
    .replace('MM', month)
    .replace('dd', day)
    .replace('hh', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 检查是否登录
 */
export function checkLogin(): boolean {
  const token = getToken()
  return !!token
}

/**
 * 获取图片完整路径
 * @param path 图片路径（可能是逗号分隔的多张图片）
 */
export function getImageUrl(path: string): string {
  if (!path) return ''
  // 如果有多张图片，取第一张
  const firstPath = path.split(',')[0].trim()
  if (firstPath.startsWith('http://') || firstPath.startsWith('https://')) {
    return firstPath
  }
  // 开发环境使用代理，直接返回相对路径
  if (firstPath.startsWith('/')) {
    return firstPath
  }
  return `/${firstPath}`
}

/**
 * 获取文件完整路径
 * @param path 文件路径
 */
export function getFileUrl(path: string): string {
  return getImageUrl(path)
}

