import { ref } from 'vue'

export type DialogMode = 'add' | 'edit' | 'view'

export function useDialog<T>() {
  const visible = ref(false)
  const mode = ref<DialogMode>('add')
  const currentRow = ref<T | null>(null)

  function openAdd() {
    currentRow.value = null
    mode.value = 'add'
    visible.value = true
  }

  function openEdit(row: T) {
    currentRow.value = row
    mode.value = 'edit'
    visible.value = true
  }

  function openView(row: T) {
    currentRow.value = row
    mode.value = 'view'
    visible.value = true
  }

  function close() {
    visible.value = false
  }

  return {
    visible,
    mode,
    currentRow,
    openAdd,
    openEdit,
    openView,
    close
  }
}
