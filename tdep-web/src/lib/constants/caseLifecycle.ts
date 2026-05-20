export enum CaseStatus {
  DRAFT = 'DRAFT',
  PENDING_REVIEW = 'PENDING_REVIEW',
  ACCEPTED = 'ACCEPTED',
  EVIDENCE_CLOSING = 'EVIDENCE_CLOSING',
  TRIAL_SCHEDULED = 'TRIAL_SCHEDULED',
  IN_TRIAL = 'IN_TRIAL',
  JUDGED = 'JUDGED',
  CLOSED = 'CLOSED',
  REJECTED = 'REJECTED',
}

export enum CaseTransition {
  SUBMIT = 'SUBMIT',
  APPROVE = 'APPROVE',
  REJECT = 'REJECT',
  START_EVIDENCE = 'START_EVIDENCE',
  COMPLETE_EVIDENCE = 'COMPLETE_EVIDENCE',
  SCHEDULE_TRIAL = 'SCHEDULE_TRIAL',
  START_TRIAL = 'START_TRIAL',
  COMPLETE_TRIAL = 'COMPLETE_TRIAL',
  ISSUE_JUDGMENT = 'ISSUE_JUDGMENT',
  CLOSE_CASE = 'CLOSE_CASE',
}

export interface CaseStateDefinition {
  status: CaseStatus
  label: string
  description: string
  color: string
  bgColor: string
  icon: string
  order: number
  isTerminal: boolean
  allowedTransitions: CaseTransition[]
}

export interface TransitionRule {
  from: CaseStatus
  transition: CaseTransition
  to: CaseStatus
  label: string
  description: string
  requiresRole?: string[]
  confirmRequired?: boolean
}

export const CASE_STATES: Record<CaseStatus, CaseStateDefinition> = {
  [CaseStatus.DRAFT]: {
    status: CaseStatus.DRAFT,
    label: '草稿',
    description: '案件已创建，待提交审核',
    color: 'text-gray-600',
    bgColor: 'bg-gray-100',
    icon: 'Edit',
    order: 1,
    isTerminal: false,
    allowedTransitions: [CaseTransition.SUBMIT],
  },
  [CaseStatus.PENDING_REVIEW]: {
    status: CaseStatus.PENDING_REVIEW,
    label: '待审核',
    description: '案件已提交，等待法官审核',
    color: 'text-yellow-600',
    bgColor: 'bg-yellow-100',
    icon: 'Clock',
    order: 2,
    isTerminal: false,
    allowedTransitions: [CaseTransition.APPROVE, CaseTransition.REJECT],
  },
  [CaseStatus.ACCEPTED]: {
    status: CaseStatus.ACCEPTED,
    label: '已受理',
    description: '案件已通过审核，正式立案',
    color: 'text-blue-600',
    bgColor: 'bg-blue-100',
    icon: 'CircleCheck',
    order: 3,
    isTerminal: false,
    allowedTransitions: [CaseTransition.START_EVIDENCE],
  },
  [CaseStatus.EVIDENCE_CLOSING]: {
    status: CaseStatus.EVIDENCE_CLOSING,
    label: '举证中',
    description: '证据收集阶段，双方提交证据材料',
    color: 'text-indigo-600',
    bgColor: 'bg-indigo-100',
    icon: 'FolderOpened',
    order: 4,
    isTerminal: false,
    allowedTransitions: [CaseTransition.COMPLETE_EVIDENCE],
  },
  [CaseStatus.TRIAL_SCHEDULED]: {
    status: CaseStatus.TRIAL_SCHEDULED,
    label: '已排期',
    description: '庭审时间已确定，等待开庭',
    color: 'text-purple-600',
    bgColor: 'bg-purple-100',
    icon: 'Calendar',
    order: 5,
    isTerminal: false,
    allowedTransitions: [CaseTransition.START_TRIAL],
  },
  [CaseStatus.IN_TRIAL]: {
    status: CaseStatus.IN_TRIAL,
    label: '审理中',
    description: '庭审进行中',
    color: 'text-orange-600',
    bgColor: 'bg-orange-100',
    icon: 'Gavel',
    order: 6,
    isTerminal: false,
    allowedTransitions: [CaseTransition.COMPLETE_TRIAL],
  },
  [CaseStatus.JUDGED]: {
    status: CaseStatus.JUDGED,
    label: '已判决',
    description: '法院已作出判决',
    color: 'text-green-600',
    bgColor: 'bg-green-100',
    icon: 'DocumentChecked',
    order: 7,
    isTerminal: false,
    allowedTransitions: [CaseTransition.ISSUE_JUDGMENT, CaseTransition.CLOSE_CASE],
  },
  [CaseStatus.CLOSED]: {
    status: CaseStatus.CLOSED,
    label: '已结案',
    description: '案件已正式结案归档',
    color: 'text-slate-600',
    bgColor: 'bg-slate-100',
    icon: 'CircleClose',
    order: 8,
    isTerminal: true,
    allowedTransitions: [],
  },
  [CaseStatus.REJECTED]: {
    status: CaseStatus.REJECTED,
    label: '已驳回',
    description: '案件未通过审核，已驳回',
    color: 'text-red-600',
    bgColor: 'bg-red-100',
    icon: 'CircleCloseFilled',
    order: -1,
    isTerminal: true,
    allowedTransitions: [],
  },
}

export const TRANSITION_RULES: TransitionRule[] = [
  {
    from: CaseStatus.DRAFT,
    transition: CaseTransition.SUBMIT,
    to: CaseStatus.PENDING_REVIEW,
    label: '提交审核',
    description: '将案件提交给法官审核',
    requiresRole: ['PARTY'],
    confirmRequired: true,
  },
  {
    from: CaseStatus.PENDING_REVIEW,
    transition: CaseTransition.APPROVE,
    to: CaseStatus.ACCEPTED,
    label: '受理案件',
    description: '审核通过，正式立案',
    requiresRole: ['JUDGE', 'ADMIN'],
    confirmRequired: true,
  },
  {
    from: CaseStatus.PENDING_REVIEW,
    transition: CaseTransition.REJECT,
    to: CaseStatus.REJECTED,
    label: '驳回案件',
    description: '不满足立案条件，驳回申请',
    requiresRole: ['JUDGE', 'ADMIN'],
    confirmRequired: true,
  },
  {
    from: CaseStatus.ACCEPTED,
    transition: CaseTransition.START_EVIDENCE,
    to: CaseStatus.EVIDENCE_CLOSING,
    label: '开始举证',
    description: '进入证据收集阶段',
    requiresRole: ['JUDGE', 'CLERK'],
  },
  {
    from: CaseStatus.EVIDENCE_CLOSING,
    transition: CaseTransition.COMPLETE_EVIDENCE,
    to: CaseStatus.TRIAL_SCHEDULED,
    label: '完成举证',
    description: '证据收集完成，安排庭审',
    requiresRole: ['JUDGE', 'CLERK'],
  },
  {
    from: CaseStatus.TRIAL_SCHEDULED,
    transition: CaseTransition.START_TRIAL,
    to: CaseStatus.IN_TRIAL,
    label: '开始庭审',
    description: '正式开庭审理',
    requiresRole: ['JUDGE'],
  },
  {
    from: CaseStatus.IN_TRIAL,
    transition: CaseTransition.COMPLETE_TRIAL,
    to: CaseStatus.JUDGED,
    label: '结束庭审',
    description: '庭审结束，进入判决阶段',
    requiresRole: ['JUDGE'],
  },
  {
    from: CaseStatus.JUDGED,
    transition: CaseTransition.ISSUE_JUDGMENT,
    to: CaseStatus.CLOSED,
    label: '发布判决书',
    description: '正式发布判决文书并结案',
    requiresRole: ['JUDGE'],
    confirmRequired: true,
  },
]

export function getAvailableTransitions(currentStatus: CaseStatus): TransitionRule[] {
  return TRANSITION_RULES.filter(rule => rule.from === currentStatus)
}

export function canTransition(from: CaseStatus, transition: CaseTransition): boolean {
  return TRANSITION_RULES.some(rule => rule.from === from && rule.transition === transition)
}

export function getNextState(from: CaseStatus, transition: CaseTransition): CaseStatus | null {
  const rule = TRANSITION_RULES.find(r => r.from === from && r.transition === transition)
  return rule?.to || null
}

export function getStatusTimeline(): CaseStateDefinition[] {
  return Object.values(CASE_STATES)
    .filter(state => state.order > 0)
    .sort((a, b) => a.order - b.order)
}
