import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export interface PageParams {
  page: number
  limit: number
  [key: string]: any
}

export interface PageResult<T> {
  list: T[]
  total: number
}

export interface UseTableOptions<T, S extends Record<string, any>> {
  // 获取数据的 API 函数
  fetchApi: (params: PageParams) => Promise<{ data: { code: number; data: PageResult<T>; msg?: string } }>
  // 删除数据的 API 函数
  deleteApi?: (ids: number[]) => Promise<{ data: { code: number; msg?: string } }>
  // 初始搜索参数
  searchParams?: S
  // 删除确认文字
  deleteConfirmText?: string
  // ID 字段名（有些是 id，有些是 ID）
  idField?: string
}

export function useTable<T extends Record<string, any>, S extends Record<string, any> = Record<string, any>>(
  options: UseTableOptions<T, S>
) {
  const {
    fetchApi,
    deleteApi,
    searchParams = {} as S,
    deleteConfirmText = '确定要删除选中的数据吗？',
    idField = 'id'
  } = options

  // 状态
  const loading = ref(false)
  const tableData = ref<T[]>([]) as { value: T[] }
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)
  const selectedIds = ref<number[]>([])
  const searchForm = reactive<S>({ ...searchParams } as S)

  // 加载数据
  async function loadData() {
    loading.value = true
    try {
      const res = await fetchApi({
        page: currentPage.value,
        limit: pageSize.value,
        ...searchForm
      })
      if (res.data.code === 0) {
        tableData.value = res.data.data.list
        total.value = res.data.data.total
      } else {
        ElMessage.error(res.data.msg || '获取数据失败')
      }
    } catch (error: any) {
      ElMessage.error(error.message || '网络错误')
    } finally {
      loading.value = false
    }
  }

  // 搜索
  function handleSearch() {
    currentPage.value = 1
    loadData()
  }

  // 重置搜索
  function handleReset() {
    Object.keys(searchForm).forEach((key) => {
      ;(searchForm as any)[key] = (searchParams as any)[key] ?? ''
    })
    handleSearch()
  }

  // 选择变化
  function handleSelectionChange(rows: T[]) {
    selectedIds.value = rows.map((r) => r[idField] as number)
  }

  // 删除
  async function handleDelete(ids: number[]) {
    if (!deleteApi) {
      ElMessage.warning('删除功能未配置')
      return
    }
    if (!ids.length) {
      ElMessage.warning('请选择要删除的数据')
      return
    }
    
    try {
      await ElMessageBox.confirm(deleteConfirmText, '提示', { type: 'warning' })
      const res = await deleteApi(ids)
      if (res.data.code === 0) {
        ElMessage.success('删除成功')
        // 如果当前页数据删完了，回到上一页
        if (tableData.value.length === ids.length && currentPage.value > 1) {
          currentPage.value--
        }
        loadData()
      } else {
        ElMessage.error(res.data.msg || '删除失败')
      }
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error.message || '删除失败')
      }
    }
  }

  // 页码变化
  function handlePageChange() {
    loadData()
  }

  return {
    // 状态
    loading,
    tableData,
    total,
    currentPage,
    pageSize,
    selectedIds,
    searchForm,
    // 方法
    loadData,
    handleSearch,
    handleReset,
    handleSelectionChange,
    handleDelete,
    handlePageChange
  }
}
