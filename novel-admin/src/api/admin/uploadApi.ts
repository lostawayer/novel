import request from '../request'

/**
 * 上传文件
 */
export function uploadFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取图片完整URL
 * 数据库存储格式: upload/xxx.jpg
 * 访问格式: /upload/xxx.jpg
 */
export function getImageUrl(path: string | null | undefined): string {
  if (!path) return ''
  // 如果已经是完整URL，直接返回
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }
  // 如果路径以 / 开头，直接返回
  if (path.startsWith('/')) {
    return path
  }
  // 否则添加 / 前缀
  return '/' + path
}
