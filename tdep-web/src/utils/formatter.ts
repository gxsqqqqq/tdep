export function formatDateTime(value?: string | number | Date) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '-'
  return date.toLocaleString('zh-CN', { hour12: false })
}

export function formatRole(role: string, roleNames: Record<string, string>) {
  return roleNames[role] || role
}
