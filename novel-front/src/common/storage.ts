/**
 * localStorage 封装工具
 */

/**
 * 存储数据到 localStorage
 */
export function setStorage(key: string, value: any): void {
  try {
    const jsonValue = JSON.stringify(value)
    localStorage.setItem(key, jsonValue)
  } catch (error) {
    console.error('存储数据失败:', error)
  }
}

/**
 * 从 localStorage 获取数据
 */
export function getStorage(key: string): any {
  try {
    const jsonValue = localStorage.getItem(key)
    if (jsonValue) {
      return JSON.parse(jsonValue)
    }
    return null
  } catch (error) {
    console.error('获取数据失败:', error)
    return null
  }
}

/**
 * 从 localStorage 删除数据
 */
export function removeStorage(key: string): void {
  try {
    localStorage.removeItem(key)
  } catch (error) {
    console.error('删除数据失败:', error)
  }
}

/**
 * 清空 localStorage
 */
export function clearStorage(): void {
  try {
    localStorage.clear()
  } catch (error) {
    console.error('清空数据失败:', error)
  }
}

/**
 * 获取 Token
 */
export function getToken(): string | null {
  return localStorage.getItem('Token')
}

/**
 * 设置 Token
 */
export function setToken(token: string): void {
  localStorage.setItem('Token', token)
}

/**
 * 移除 Token
 */
export function removeToken(): void {
  localStorage.removeItem('Token')
}

/**
 * 获取用户信息
 */
export function getUserInfo(): any {
  return getStorage('UserInfo')
}

/**
 * 设置用户信息
 */
export function setUserInfo(userInfo: any): void {
  setStorage('UserInfo', userInfo)
}

/**
 * 移除用户信息
 */
export function removeUserInfo(): void {
  removeStorage('UserInfo')
}

/**
 * 获取角色
 */
export function getRole(): string | null {
  return localStorage.getItem('role')
}

/**
 * 设置角色
 */
export function setRole(role: string): void {
  localStorage.setItem('role', role)
}

/**
 * 移除角色
 */
export function removeRole(): void {
  localStorage.removeItem('role')
}

