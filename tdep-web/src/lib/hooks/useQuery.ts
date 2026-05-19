import { useQuery as useVueQuery, type UseQueryOptions } from '@tanstack/vue-query'
import type { Ref } from 'vue'
import { ElMessage } from 'element-plus'

export interface TdepQueryOptions<TData, TError = Error>
  extends Omit<UseQueryOptions<TData, TError, TData>, 'queryKey' | 'queryFn'> {
  queryKey: string[]
  queryFn: () => Promise<TData>
  errorMessage?: string
  showError?: boolean
}

export function useQuery<TData, TError = Error>(
  options: TdepQueryOptions<TData, TError>
) {
  const {
    queryKey,
    queryFn,
    errorMessage,
    showError = true,
    retry = 1,
    retryDelay,
    ...restOptions
  } = options

  return useVueQuery<TData, TError>(queryKey, queryFn, {
    retry,
    retryDelay: retryDelay ?? ((retryIndex) => Math.min(1000 * 2 ** retryIndex, 30000)),
    ...restOptions,
    onError: (error) => {
      if (showError) {
        const message =
          errorMessage ||
          (error instanceof Error ? error.message : '数据加载失败')
        ElMessage.error(message)
      }
      restOptions.onError?.(error as TError)
    },
  } as UseQueryOptions<TData, TError, TData>)
}

export interface TdepInfiniteQueryOptions<TData, TError = Error>
  extends Omit<
    UseQueryOptions<TData[], TError, TData[]>,
    'queryKey' | 'queryFn'
  > {
  queryKey: string[]
  queryFn: ({ pageParam }: { pageParam: number }) => Promise<{
    data: TData[]
    nextPage?: number | null
  }>
  initialPageParam?: number
  getNextPageParam?: (
    lastPage: { data: TData[]; nextPage?: number | null }
  ) => number | undefined | null
  errorMessage?: string
  showError?: boolean
}
