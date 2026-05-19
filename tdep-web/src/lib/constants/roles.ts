export enum UserRole {
  ADMIN = 'admin',
  JUDGE = 'judge',
  CLERK = 'clerk',
  PARTY = 'party',
}

export const ROLE_LABELS: Record<UserRole, string> = {
  [UserRole.ADMIN]: '管理员',
  [UserRole.JUDGE]: '法官',
  [UserRole.CLERK]: '书记员',
  [UserRole.PARTY]: '当事人',
}

export const ROLE_OPTIONS = Object.entries(ROLE_LABELS).map(([value, label]) => ({
  value,
  label,
}))

export function getRoleLabel(role: UserRole): string {
  return ROLE_LABELS[role] || role
}

export function isValidRole(role: string): role is UserRole {
  return Object.values(UserRole).includes(role as UserRole)
}
