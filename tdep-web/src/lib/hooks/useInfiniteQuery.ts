import { ref, computed } from 'vue'
import type { Ref } from 'vue'

interface UseInfiniteQueryOptions<T> {
  queryKey: string
  queryFn: ({ pageParam }: { pageParam: number }) => Promise<{
    data: T[]
    nextPage: number | null
  }>
  getNextPageParam: (lastPage: { data: T[]; nextPage: number | null }) => number | null
  enabled?: boolean
}

export function useInfiniteQuery<T>(options: UseInfiniteQueryOptions<T>) {
  const pages = ref<Array<T[]>>([])
  const error = ref<Error | null>(null)
  const isLoading = ref(false)
  const isFetchingNextPage = ref(false)
  const hasNextPage = computed(() => {
    const lastPage = pages.value[pages.value.length - 1]
    if (!lastPage) return false
    return options.getNextPageParam({
      data: lastPage,
      nextPage: pages.value.length + 1,
    }) !== null
  })

  async function fetchNextPage() {
    if (!hasNextPage.value || isFetchingNextPage.value) return

    isFetchingNextPage.value = true
    try {
      const nextPageNumber = pages.value.length + 1
      const result = await options.queryFn({ pageParam: nextPageNumber })
      pages.value.push(result.data)
    } catch (e) {
      error.value = e instanceof Error ? e : new Error(String(e))
    } finally {
      isFetchingNextPage.value = false
    }
  }

  async function fetchFirstPage() {
    isLoading.value = true
    pages.value = []
    error.value = null

    try {
      const result = await options.queryFn({ pageParam: 1 })
      pages.value.push(result.data)
    } catch (e) {
      error.value = e instanceof Error ? e : new Error(String(e))
    } finally {
      isLoading.value = false
    }
  }

  const allData = computed(() => pages.value.flat())

  fetchFirstPage()

  return {
    data: allData as Ref<T[]>,
    pages: pages as Ref<T[][]>,
    error,
    isLoading,
    isFetchingNextPage,
    hasNextPage,
    fetchNextPage,
  }
}
