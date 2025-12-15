/**
 * 项目配置文件
 */

interface Config {
  /**
   * API 基础路径
   */
  baseUrl: string

  /**
   * 项目名称
   */
  projectName: string

  /**
   * 文件上传地址
   */
  uploadUrl: string

  /**
   * 图片前缀
   */
  imagePrefixUrl: string

  /**
   * 分页大小
   */
  pageSize: number

  /**
   * 角色列表
   */
  roles: {
    value: string
    label: string
  }[]
}

const config: Config = {
  // 开发环境使用代理，生产环境使用实际地址
  baseUrl: import.meta.env.VITE_APP_BASE_URL || 'http://localhost:8088',

  projectName: '小说网站系统',

  uploadUrl: '/file/upload',

  imagePrefixUrl: 'http://localhost:8088/',

  pageSize: 10,

  roles: [
    { value: 'yonghu', label: '用户' },
    { value: 'zuozhe', label: '作者' },
  ],
}

export default config

