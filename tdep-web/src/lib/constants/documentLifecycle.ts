export enum DocumentStatus {
  DRAFT = 'DRAFT',
  GENERATED = 'GENERATED',
  SIGNED = 'SIGNED',
  ARCHIVED = 'ARCHIVED',
}

export enum DocumentTransition {
  GENERATE = 'GENERATE',
  REVIEW = 'REVIEW',
  SIGN = 'SIGN',
  ARCHIVE = 'ARCHIVE',
}

export interface DocumentStateDefinition {
  status: DocumentStatus
  label: string
  description: string
  color: string
  bgColor: string
  icon: string
  order: number
  isTerminal: boolean
  allowedTransitions: DocumentTransition[]
}

export interface DocumentTransitionRule {
  from: DocumentStatus
  transition: DocumentTransition
  to: DocumentStatus
  label: string
  description: string
  requiresRole?: string[]
  confirmRequired?: boolean
}

export const DOCUMENT_STATES: Record<DocumentStatus, DocumentStateDefinition> = {
  [DocumentStatus.DRAFT]: {
    status: DocumentStatus.DRAFT,
    label: '草稿',
    description: '文书已创建，待生成内容',
    color: 'text-gray-600',
    bgColor: 'bg-gray-100',
    icon: 'Edit',
    order: 1,
    isTerminal: false,
    allowedTransitions: [DocumentTransition.GENERATE],
  },
  [DocumentStatus.GENERATED]: {
    status: DocumentStatus.GENERATED,
    label: '已生成',
    description: 'AI/模板生成完成，待签署确认',
    color: 'text-blue-600',
    bgColor: 'bg-blue-100',
    icon: 'Document',
    order: 2,
    isTerminal: false,
    allowedTransitions: [DocumentTransition.REVIEW, DocumentTransition.SIGN],
  },
  [DocumentStatus.SIGNED]: {
    status: DocumentStatus.SIGNED,
    label: '已签署',
    description: '电子签章完成，待归档',
    color: 'text-green-600',
    bgColor: 'bg-green-100',
    icon: 'CircleCheck',
    order: 3,
    isTerminal: false,
    allowedTransitions: [DocumentTransition.ARCHIVE],
  },
  [DocumentStatus.ARCHIVED]: {
    status: DocumentStatus.ARCHIVED,
    label: '已归档',
    description: '文书已完成归档，不可修改',
    color: 'text-slate-600',
    bgColor: 'bg-slate-100',
    icon: 'FolderOpened',
    order: 4,
    isTerminal: true,
    allowedTransitions: [],
  },
}

export const DOCUMENT_TRANSITION_RULES: DocumentTransitionRule[] = [
  {
    from: DocumentStatus.DRAFT,
    transition: DocumentTransition.GENERATE,
    to: DocumentStatus.GENERATED,
    label: '生成文书',
    description: '使用 AI 或模板生成文书内容',
    requiresRole: ['JUDGE', 'CLERK', 'PARTY'],
    confirmRequired: true,
  },
  {
    from: DocumentStatus.GENERATED,
    transition: DocumentTransition.REVIEW,
    to: DocumentStatus.GENERATED,
    label: '提交审核',
    description: '将文书提交给法官审核（审核流程暂不实现）',
    requiresRole: ['PARTY'],
  },
  {
    from: DocumentStatus.GENERATED,
    transition: DocumentTransition.SIGN,
    to: DocumentStatus.SIGNED,
    label: '签署文书',
    description: '使用手写签名或电子签章完成签署',
    requiresRole: ['JUDGE', 'PARTY'],
    confirmRequired: true,
  },
  {
    from: DocumentStatus.SIGNED,
    transition: DocumentTransition.ARCHIVE,
    to: DocumentStatus.ARCHIVED,
    label: '归档文书',
    description: '将已签署的文书正式归档',
    requiresRole: ['JUDGE', 'ADMIN', 'CLERK'],
    confirmRequired: true,
  },
]

export function getAvailableDocumentTransitions(currentStatus: DocumentStatus): DocumentTransitionRule[] {
  return DOCUMENT_TRANSITION_RULES.filter(rule => rule.from === currentStatus)
}

export function canDocumentTransition(from: DocumentStatus, transition: DocumentTransition): boolean {
  return DOCUMENT_TRANSITION_RULES.some(rule => rule.from === from && rule.transition === transition)
}

export function getNextDocumentStatus(from: DocumentStatus, transition: DocumentTransition): DocumentStatus | null {
  const rule = DOCUMENT_TRANSITION_RULES.find(r => r.from === from && r.transition === transition)
  return rule?.to || null
}

export function getDocumentStatusTimeline(): DocumentStateDefinition[] {
  return Object.values(DOCUMENT_STATES)
    .filter(state => state.order > 0)
    .sort((a, b) => a.order - b.order)
}
