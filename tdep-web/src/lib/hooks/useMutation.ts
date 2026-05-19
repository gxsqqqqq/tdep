import { useMutation as useVueMutation, useQueryClient, type UseMutationOptions } from '@tanstack/vue-query'
import type { Ref } from 'vue'
import { ElMessage } from 'element-plus'

export interface TdepMutationOptions<TData = unknown, TError = Error, TVariables = void>
  extends Omit<UseMutationOptions<TData, TError, TVariables>, 'mutationFn'> {
  mutationFn: (variables: TVariables) => Promise<TData>
  successMessage?: string
  errorMessage?: string
  showSuccess?: boolean
  showError?: boolean
  invalidateQueries?: string[][]
  onSuccess?: (data: TData, variables: TVariables) => void | Promise<void>
  onSettled?: (data: TData | undefined, error: TError | null, variables: TVariables) => void | Promise<void>
}

export function useMutation<TData = unknown, TError = Error, TVariables = void>(
  options: TdepMutationOptions<TData, TError, TVariables>
) {
  const queryClient = useQueryClient()

  const {
    mutationFn,
    successMessage,
    errorMessage,
    showSuccess = true,
    showError = true,
    invalidateQueries,
    onSuccess: userOnSuccess,
    onSettled: userOnSettled,
    ...restOptions
  } = options

  return useVueMutation<TData, TError, TVariables>(mutationFn, {
    ...restOptions,
    onSuccess: async (data, variables) => {
      if (showSuccess && successMessage) {
        ElMessage.success(successMessage)
      }
      if (invalidateQueries?.length) {
        await Promise.all(
          invalidateQueries.map((key) =>
            queryClient.invalidateQueries({ queryKey: key })
          )
        )
      }
      await userOnSuccess?.(data, variables)
    },
    onError: (error) => {
      if (showError) {
        const message =
          errorMessage ||
          (error instanceof Error ? error.message : '操作失败')
        ElMessage.error(message)
      }
      restOptions.onError?.(error as TError)
    },
    onSettled: async (data, error, variables) => {
      await userOnSettled?.(data, error as TError | null, variables)
    },
  })
}

export function useOptimisticMutation<TData = unknown, TError = Error, TVariables = void>(
  options: TdepMutationOptions<TData, TError, TVariables> & {
    optimisticUpdate: (variables: TVariables) => void
    rollback: () => void
    queryKeysToInvalidate?: string[][]
  }
) {
  const queryClient = useQueryClient()

  const { mutationFn, optimisticUpdate, rollback, queryKeysToInvalidate, ...restOptions } = options

  return useVueMutation<TData, TError, TVariables>(mutationFn, {
    ...restOptions,
    onMutate: async (variables) => {
      await queryClient.cancelQueries()
      optimisticUpdate(variables)
      return { rollback }
    },
    onError: (error, _variables, context) => {
      if (showError) {
        const message =
          restOptions.errorMessage ||
          (error instanceof Error ? error.message : '操作失败')
        ElMessage.error(message)
      }
      context?.rollback()
      restOptions.onError?.(error as TError)
    },
    onSuccess: async (data, variables) => {
      if (restOptions.showSuccess !== false && restOptions.successMessage) {
        ElMessage.success(restOptions.successMessage)
      }
      if (queryKeysToInvalidate?.length) {
        await Promise.all(
          queryKeysToInvalidate.map((key) =>
            queryClient.invalidateQueries({ queryKey: key })
          )
        )
      }
      await restOptions.onSuccess?.(data, variables)
    },
    onSettled: async (data, error, variables) => {
      await restOptions.onSettled?.(data, error as TError | null, variables)
    },
  })
}
