export enum SystemStatus {
  ACTIVE = 'active',
  INACTIVE = 'inactive',
  PENDING = 'pending',
  SUSPENDED = 'suspended',
  DELETED = 'deleted',
}

export const STATUS_LABELS: Record<SystemStatus, string> = {
  [SystemStatus.ACTIVE]: '启用',
  [SystemStatus.INACTIVE]: '禁用',
  [SystemStatus.PENDING]: '待处理',
  [SystemStatus.SUSPENDED]: '暂停',
  [SystemStatus.DELETED]: '已删除',
}

export const STATUS_COLORS: Record<SystemStatus, string> = {
  [SystemStatus.ACTIVE]: '#52c41a',
  [SystemStatus.INACTIVE]: '#999999',
  [SystemStatus.PENDING]: '#faad14',
  [SystemStatus.SUSPENDED]: '#fa8c16',
  [SystemStatus.DELETED]: '#ff4d4f',
}

export function getStatusLabel(status: SystemStatus): string {
  return STATUS_LABELS[status] || status
}

export function getStatusColor(status: SystemStatus): string {
  return STATUS_COLORS[status] || '#999999'
}
