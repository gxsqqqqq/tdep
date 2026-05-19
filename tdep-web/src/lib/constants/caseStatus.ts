export enum CaseStatus {
  DRAFT = 'draft',
  PENDING_REVIEW = 'pending_review',
  REVIEWING = 'reviewing',
  SCHEDULED = 'scheduled',
  IN_TRIAL = 'in_trial',
  AWAITING_JUDGEMENT = 'awaiting_judgement',
  JUDGED = 'judged',
  APPEALED = 'appealed',
  CLOSED = 'closed',
}

export interface CaseStatusConfig {
  label: string
  color: string
  icon?: string
  description: string
}

export const CASE_STATUS_CONFIG: Record<CaseStatus, CaseStatusConfig> = {
  [CaseStatus.DRAFT]: {
    label: '草稿',
    color: '#999999',
    description: '案件尚未提交',
  },
  [CaseStatus.PENDING_REVIEW]: {
    label: '待审核',
    color: '#faad14',
    description: '案件已提交，等待审核',
  },
  [CaseStatus.REVIEWING]: {
    label: '审核中',
    color: '#1890ff',
    description: '案件正在审核中',
  },
  [CaseStatus.SCHEDULED]: {
    label: '已排期',
    color: '#722ed1',
    description: '庭审时间已确定',
  },
  [CaseStatus.IN_TRIAL]: {
    label: '庭审中',
    color: '#f5222d',
    description: '案件正在庭审',
  },
  [CaseStatus.AWAITING_JUDGEMENT]: {
    label: '待判决',
    color: '#fa8c16',
    description: '等待法官判决',
  },
  [CaseStatus.JUDGED]: {
    label: '已判决',
    color: '#52c41a',
    description: '案件已判决',
  },
  [CaseStatus.APPEALED]: {
    label: '已上诉',
    color: '#eb2f96',
    description: '案件已提起上诉',
  },
  [CaseStatus.CLOSED]: {
    label: '已结案',
    color: '#8c8c8c',
    description: '案件已结案',
  },
}

export function getCaseStatusLabel(status: CaseStatus): string {
  return CASE_STATUS_CONFIG[status]?.label || status
}

export function getCaseStatusColor(status: CaseStatus): string {
  return CASE_STATUS_CONFIG[status]?.color || '#999999'
}

export function getCaseStatusDescription(status: CaseStatus): string {
  return CASE_STATUS_CONFIG[status]?.description || ''
}

export const CASE_STATUS_FLOW: CaseStatus[] = [
  CaseStatus.DRAFT,
  CaseStatus.PENDING_REVIEW,
  CaseStatus.REVIEWING,
  CaseStatus.SCHEDULED,
  CaseStatus.IN_TRIAL,
  CaseStatus.AWAITING_JUDGEMENT,
  CaseStatus.JUDGED,
]
