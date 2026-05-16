import { reactive, ref } from 'vue'

export function useTable<T>(loader?: (params: Record<string, unknown>) => Promise<{ records?: T[]; total?: number }>) {
  const loading = ref(false)
  const data = ref<T[]>([])
  const pagination = reactive({ page: 1, size: 10, total: 0 })

  async function load(extra: Record<string, unknown> = {}) {
    if (!loader) return
    loading.value = true
    try {
      const result = await loader({ current: pagination.page, size: pagination.size, ...extra })
      data.value = result.records || []
      pagination.total = result.total || 0
    } finally {
      loading.value = false
    }
  }

  return { loading, data, pagination, load }
}
