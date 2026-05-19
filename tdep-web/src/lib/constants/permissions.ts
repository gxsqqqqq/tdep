export type Permission =
  | 'user:create'
  | 'user:read'
  | 'user:update'
  | 'user:delete'
  | 'case:create'
  | 'case:read'
  | 'case:update'
  | 'case:delete'
  | 'evidence:create'
  | 'evidence:read'
  | 'evidence:update'
  | 'evidence:delete'
  | 'document:create'
  | 'document:read'
  | 'document:sign'
  | 'document:delete'
  | 'trial:manage'
  | 'trial:record'
  | 'trial:schedule'
  | 'judgement:create'
  | 'judgement:read'
  | 'system:manage'

export const PERMISSION_GROUPS = {
  user: ['user:create', 'user:read', 'user:update', 'user:delete'],
  case: ['case:create', 'case:read', 'case:update', 'case:delete'],
  evidence: ['evidence:create', 'evidence:read', 'evidence:update', 'evidence:delete'],
  document: ['document:create', 'document:read', 'document:sign', 'document:delete'],
  trial: ['trial:manage', 'trial:record', 'trial:schedule'],
  judgement: ['judgement:create', 'judgement:read'],
  system: ['system:manage'],
} as const

export const PERMISSION_LABELS: Record<Permission, string> = {
  'user:create': '创建用户',
  'user:read': '查看用户',
  'user:update': '编辑用户',
  'user:delete': '删除用户',
  'case:create': '创建案件',
  'case:read': '查看案件',
  'case:update': '编辑案件',
  'case:delete': '删除案件',
  'evidence:create': '上传证据',
  'evidence:read': '查看证据',
  'evidence:update': '编辑证据',
  'evidence:delete': '删除证据',
  'document:create': '创建文书',
  'document:read': '查看文书',
  'document:sign': '签署文书',
  'document:delete': '删除文书',
  'trial:manage': '管理庭审',
  'trial:record': '记录庭审',
  'trial:schedule': '排期庭审',
  'judgement:create': '创建判决书',
  'judgement:read': '查看判决书',
  'system:manage': '系统管理',
}
