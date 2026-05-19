import { UserRole } from '@/lib/constants/roles'
import type { Permission } from '@/lib/constants/permissions'

const ROLE_PERMISSIONS: Record<UserRole, Permission[]> = {
  [UserRole.ADMIN]: [
    'user:create',
    'user:read',
    'user:update',
    'user:delete',
    'case:create',
    'case:read',
    'case:update',
    'case:delete',
    'evidence:create',
    'evidence:read',
    'evidence:update',
    'evidence:delete',
    'document:create',
    'document:read',
    'document:sign',
    'system:manage',
  ],
  [UserRole.JUDGE]: [
    'case:read',
    'case:update',
    'evidence:read',
    'evidence:update',
    'document:read',
    'document:sign',
    'trial:manage',
    'judgement:create',
  ],
  [UserRole.CLERK]: [
    'case:read',
    'evidence:read',
    'evidence:create',
    'document:read',
    'document:create',
    'trial:record',
  ],
  [UserRole.PARTY]: [
    'case:read',
    'evidence:read',
    'evidence:create',
    'document:read',
    'document:sign',
  ],
}

export function getPermissionsByRole(role: UserRole): Permission[] {
  return ROLE_PERMISSIONS[role] || []
}

export function hasPermission(
  userRole: UserRole,
  requiredPermission: Permission | Permission[]
): boolean {
  const permissions = getPermissionsByRole(userRole)
  const required = Array.isArray(requiredPermission)
    ? requiredPermission
    : [requiredPermission]

  return required.some((perm) => permissions.includes(perm))
}

export function canAccessResource(
  userRole: UserRole,
  resource: string,
  action: string
): boolean {
  const permission = `${resource}:${action}` as Permission
  return hasPermission(userRole, permission)
}
