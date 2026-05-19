import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/lib/stores/auth'
import type {
  LoginRequest,
  RegisterRequest,
  LoginResponse,
} from '@/types/auth'
import { UserRole } from '@/lib/constants/roles'
import type { Permission } from '@/lib/constants/permissions'
import { useMutation } from '@/lib/hooks/useMutation'

export function useAuth() {
  const router = useRouter()
  const authStore = useAuthStore()
  const loading = ref(false)

  const isAuthenticated = computed(() => authStore.isAuthenticated)
  const user = computed(() => authStore.user)
  const role = computed(() => authStore.role)
  const permissions = computed(() => authStore.permissions)

  function login(credentials: LoginRequest) {
    return useMutation({
      mutationFn: () => authStore.login(credentials),
      successMessage: '登录成功',
      errorMessage: '用户名或密码错误',
    })
  }

  function register(data: RegisterRequest) {
    return useMutation({
      mutationFn: () => authStore.register(data),
      successMessage: '注册成功，请登录',
      errorMessage: '注册失败',
    })
  }

  async function logout(): Promise<void> {
    loading.value = true
    try {
      await authStore.logout()
      router.push('/login')
    } finally {
      loading.value = false
    }
  }

  async function fetchUserInfo() {
    try {
      return await authStore.fetchUserInfo()
    } catch (error) {
      throw error
    }
  }

  function hasRole(roleCheck: UserRole | UserRole[]): boolean {
    return authStore.hasRole(roleCheck)
  }

  function isAdmin(): boolean {
    return authStore.isAdmin()
  }

  function isJudge(): boolean {
    return authStore.isJudge()
  }

  function isClerk(): boolean {
    return authStore.isClerk()
  }

  function isParty(): boolean {
    return authStore.isParty()
  }

  function hasPermission(permission: Permission | Permission[]): boolean {
    return authStore.hasPermission(permission)
  }

  function requireRole(allowedRoles: UserRole[]): boolean {
    if (!isAuthenticated.value) return false
    return allowedRoles.some((r) => role.value.includes(r))
  }

  function requireAnyPermission(requiredPermissions: Permission[]): boolean {
    if (!isAuthenticated.value) return false
    return requiredPermissions.some((p) => permissions.value.includes(p))
  }

  function requireAllPermissions(requiredPermissions: Permission[]): boolean {
    if (!isAuthenticated.value) return false
    return requiredPermissions.every((p) => permissions.value.includes(p))
  }

  function checkAccess({
    roles,
    permissions: perms,
  }: {
    roles?: UserRole[]
    permissions?: Permission[]
  }): boolean {
    if (roles && !requireRole(roles)) return false
    if (perms && !requireAllPermissions(perms)) return false
    return true
  }

  return {
    isAuthenticated,
    user,
    role,
    permissions,
    loading,
    login,
    register,
    logout,
    fetchUserInfo,
    hasRole,
    isAdmin,
    isJudge,
    isClerk,
    isParty,
    hasPermission,
    requireRole,
    requireAnyPermission,
    requireAllPermissions,
    checkAccess,
  }
}
