import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

import type { CaseDetail, EvidenceItem, DocumentItem } from '@/api/models'

// ── Types ──

export interface AgentMessage {
  id: string
  type: 'hint' | 'warning' | 'success' | 'info' | 'action'
  title: string
  content: string
  action?: {
    label: string
    route?: string
    callback?: string  // action name, resolved at component level
  }
  dismissible: boolean
  priority: number
  createdAt: number
  expiresAt?: number
  source: 'flow' | 'form' | 'notify' | 'system'
}

export interface AgentContext {
  currentCaseId: number | null
  currentStep: number
  caseStatus: string
  caseTitle: string
  missingFields: string[]
  missingMaterials: string[]
  evidenceCount: number
  documentCount: number
  unreadNotifications: number
  nextAction: string | null
  nextActionRoute: string | null
  canProceed: boolean
  blockers: string[]
  suggestions: string[]
}

// ── Status → Step mapping ──

const STATUS_STEP_MAP: Record<string, number> = {
  DRAFT: 0,
  SUBMITTED: 0,
  FILING_REVIEW: 0,
  FILING_REJECTED: 0,
  ACCEPTED: 1,
  EVIDENCE_SUBMITTING: 2,
  SCHEDULED: 3,
  IN_TRIAL: 4,
  JUDGED: 5,
  EFFECTIVE: 5,
  CLOSED: 5
}

const STEP_NAMES = ['提交立案', '身份材料', '上传证据', '确认文书', '等待审理', '案件进度']

const STEP_ROUTES = [
  '/case/create',
  null,  // identity materials — same as evidence page
  null,  // evidence — resolved dynamically
  null,  // documents — resolved dynamically
  null,  // waiting — no action
  null   // timeline — resolved dynamically
]

const STATUS_ACTION_MAP: Record<string, { action: string; route: string | null }> = {
  DRAFT:               { action: '填写立案信息',     route: '/case/create' },
  SUBMITTED:           { action: '等待立案审核',     route: null },
  FILING_REVIEW:       { action: '法院审核中',       route: null },
  FILING_REJECTED:     { action: '立案被驳回，请修改', route: '/case/create' },
  ACCEPTED:            { action: '上传身份材料',     route: null },
  EVIDENCE_SUBMITTING: { action: '上传证据材料',     route: null },
  SCHEDULED:           { action: '确认开庭文书',     route: null },
  IN_TRIAL:            { action: '等待审理',         route: null },
  JUDGED:              { action: '查看判决结果',     route: null },
  EFFECTIVE:           { action: '判决已生效',       route: null },
  CLOSED:              { action: '案件已结案',       route: null }
}

// ── Hint rule engine ──

interface HintRule {
  id: string
  condition: (ctx: AgentContext) => boolean
  build: (ctx: AgentContext) => Omit<AgentMessage, 'id' | 'createdAt' | 'source'>
  cooldownMs: number
  priority: number
}

const hintRules: HintRule[] = [
  {
    id: 'filing-incomplete',
    condition: (ctx) => ctx.currentStep === 0 && ctx.missingFields.length > 0,
    build: (ctx) => ({
      type: 'hint',
      title: '立案信息未完成',
      content: `还有 ${ctx.missingFields.length} 项必填信息未填写：${ctx.missingFields.join('、')}`,
      action: { label: '继续填写', route: '/case/create' },
      dismissible: true,
      priority: 8
    }),
    cooldownMs: 60_000,
    priority: 8
  },
  {
    id: 'filing-rejected',
    condition: (ctx) => ctx.caseStatus === 'FILING_REJECTED',
    build: () => ({
      type: 'warning',
      title: '立案被驳回',
      content: '您的立案申请未通过审核，请根据驳回原因修改后重新提交。',
      action: { label: '重新提交', route: '/case/create' },
      dismissible: true,
      priority: 10
    }),
    cooldownMs: 0,
    priority: 10
  },
  {
    id: 'evidence-empty',
    condition: (ctx) => ctx.currentStep === 2 && ctx.evidenceCount === 0,
    build: () => ({
      type: 'warning',
      title: '尚未上传证据',
      content: '您还没有上传任何证据材料，建议尽快上传相关证据。',
      action: { label: '上传证据' },
      dismissible: true,
      priority: 9
    }),
    cooldownMs: 300_000,
    priority: 9
  },
  {
    id: 'evidence-suggest',
    condition: (ctx) => ctx.currentStep === 2 && ctx.evidenceCount > 0 && ctx.evidenceCount < 3,
    build: () => ({
      type: 'hint',
      title: '建议补充证据',
      content: '已上传部分证据，建议补充更多材料以增强证据链完整性。',
      action: { label: '继续上传' },
      dismissible: true,
      priority: 6
    }),
    cooldownMs: 600_000,
    priority: 6
  },
  {
    id: 'new-document',
    condition: (ctx) => ctx.documentCount > 0 && ctx.currentStep >= 3,
    build: (ctx) => ({
      type: 'success',
      title: '新文书到达',
      content: `有 ${ctx.documentCount} 份文书等待您查阅。`,
      action: { label: '查看文书' },
      dismissible: true,
      priority: 7
    }),
    cooldownMs: 120_000,
    priority: 7
  },
  {
    id: 'unread-notify',
    condition: (ctx) => ctx.unreadNotifications > 0,
    build: (ctx) => ({
      type: 'info',
      title: '新通知',
      content: `您有 ${ctx.unreadNotifications} 条未读通知。`,
      action: { label: '查看通知', route: '/notifications' },
      dismissible: true,
      priority: 5
    }),
    cooldownMs: 180_000,
    priority: 5
  },
  {
    id: 'waiting-patient',
    condition: (ctx) => ctx.currentStep === 4,
    build: () => ({
      type: 'info',
      title: '案件审理中',
      content: '您的案件正在审理中，请耐心等待。有新进展时我们会第一时间通知您。',
      dismissible: true,
      priority: 3
    }),
    cooldownMs: 3_600_000,
    priority: 3
  }
]

// ── Store ──

export const useAgentStore = defineStore('agent', {
  state: () => ({
    messages: [] as AgentMessage[],
    context: {
      currentCaseId: null,
      currentStep: 0,
      caseStatus: '',
      caseTitle: '',
      missingFields: [],
      missingMaterials: [],
      evidenceCount: 0,
      documentCount: 0,
      unreadNotifications: 0,
      nextAction: null,
      nextActionRoute: null,
      canProceed: false,
      blockers: [],
      suggestions: []
    } as AgentContext,
    isPanelOpen: false,
    mode: 'float' as 'float' | 'panel' | 'minimized',
    hasGreeted: false,
    dismissedIds: new Set<string>(),
    _cooldowns: new Map<string, number>(),
    _msgCounter: 0
  }),

  getters: {
    visibleMessages(state): AgentMessage[] {
      const now = Date.now()
      return state.messages
        .filter((m) => !state.dismissedIds.has(m.id))
        .filter((m) => !m.expiresAt || m.expiresAt > now)
        .sort((a, b) => b.priority - a.priority)
    },

    topMessage(state): AgentMessage | undefined {
      const now = Date.now()
      return state.messages
        .filter((m) => !state.dismissedIds.has(m.id))
        .filter((m) => !m.expiresAt || m.expiresAt > now)
        .sort((a, b) => b.priority - a.priority)[0]
    },

    unreadCount(state): number {
      const now = Date.now()
      return state.messages
        .filter((m) => !state.dismissedIds.has(m.id))
        .filter((m) => !m.expiresAt || m.expiresAt > now)
        .length
    },

    currentStepName(state): string {
      return STEP_NAMES[state.context.currentStep] || '未知'
    },

    progressPercent(state): number {
      const step = state.context.currentStep
      return Math.round((step / (STEP_NAMES.length - 1)) * 100)
    },

    statusAction(state) {
      return STATUS_ACTION_MAP[state.context.caseStatus] || { action: '未知', route: null }
    }
  },

  actions: {
    // ── Context management ──

    initCaseContext(caseDetail: CaseDetail, evidences: EvidenceItem[] = [], documents: DocumentItem[] = []) {
      const status = caseDetail.status
      const step = STATUS_STEP_MAP[status] ?? 0
      const statusAction = STATUS_ACTION_MAP[status]

      this.context = {
        currentCaseId: caseDetail.id,
        currentStep: step,
        caseStatus: status,
        caseTitle: caseDetail.caseTitle || '',
        missingFields: [],
        missingMaterials: [],
        evidenceCount: evidences.length,
        documentCount: documents.length,
        unreadNotifications: this.context.unreadNotifications,
        nextAction: statusAction?.action || null,
        nextActionRoute: statusAction?.route || null,
        canProceed: !['SUBMITTED', 'FILING_REVIEW', 'IN_TRIAL'].includes(status),
        blockers: status === 'FILING_REJECTED' ? ['立案被驳回'] : [],
        suggestions: []
      }

      this.evaluateRules()
    },

    updateContext(partial: Partial<AgentContext>) {
      Object.assign(this.context, partial)
      this.evaluateRules()
    },

    updateFormContext(missingFields: string[]) {
      this.context.missingFields = missingFields
      this.evaluateRules()
    },

    updateEvidenceCount(count: number) {
      this.context.evidenceCount = count
      this.evaluateRules()
    },

    updateDocumentCount(count: number) {
      this.context.documentCount = count
      this.evaluateRules()
    },

    updateUnreadCount(count: number) {
      this.context.unreadNotifications = count
      this.evaluateRules()
    },

    // ── Message management ──

    addMessage(msg: Omit<AgentMessage, 'id' | 'createdAt'>) {
      const id = `msg_${++this._msgCounter}_${Date.now()}`
      const fullMsg: AgentMessage = {
        ...msg,
        id,
        createdAt: Date.now()
      }

      // Deduplicate by title+source within 10s
      const duplicate = this.messages.find(
        (m) => m.title === fullMsg.title
          && m.source === fullMsg.source
          && Date.now() - m.createdAt < 10_000
      )
      if (duplicate) return

      this.messages.unshift(fullMsg)

      // Keep max 50 messages
      if (this.messages.length > 50) {
        this.messages = this.messages.slice(0, 50)
      }
    },

    dismissMessage(id: string) {
      this.dismissedIds.add(id)
    },

    clearMessages() {
      this.messages = []
      this.dismissedIds.clear()
    },

    // ── UI state ──

    togglePanel() {
      this.isPanelOpen = !this.isPanelOpen
    },

    openPanel() {
      this.isPanelOpen = true
    },

    closePanel() {
      this.isPanelOpen = false
    },

    setMode(mode: 'float' | 'panel' | 'minimized') {
      this.mode = mode
    },

    markGreeted() {
      this.hasGreeted = true
    },

    // ── Rule engine ──

    evaluateRules() {
      const now = Date.now()

      for (const rule of hintRules) {
        if (!rule.condition(this.context)) continue

        // Check cooldown
        const lastFired = this._cooldowns.get(rule.id) || 0
        if (now - lastFired < rule.cooldownMs) continue

        // Check if already dismissed
        if (this.dismissedIds.has(rule.id)) continue

        // Check if a message with same title from same rule already exists and is recent
        const existing = this.messages.find(
          (m) => m.id.startsWith(`rule_${rule.id}`) && now - m.createdAt < rule.cooldownMs
        )
        if (existing) continue

        const msg = rule.build(this.context)
        const id = `rule_${rule.id}_${now}`
        this.messages.unshift({
          ...msg,
          id,
          createdAt: now,
          source: 'flow'
        })

        this._cooldowns.set(rule.id, now)
      }
    },

    // ── Greeting ──

    greet(userName?: string) {
      if (this.hasGreeted) return
      const name = userName || '您好'
      this.addMessage({
        type: 'info',
        title: '欢迎使用 TDEP',
        content: `${name}！我是您的司法服务助手，将全程引导您完成案件流程。如有疑问，随时呼叫我。`,
        dismissible: true,
        priority: 10,
        source: 'system'
      })
      this.hasGreeted = true
    },

    // ── Generate welcome for empty case list ──

    welcomeNewUser() {
      this.addMessage({
        type: 'action',
        title: '开始您的第一个案件',
        content: '您还没有案件记录。点击下方按钮开始创建您的立案申请。',
        action: { label: '创建新案件', route: '/case/create' },
        dismissible: false,
        priority: 10,
        source: 'system'
      })
    },

    // ── Reset ──

    reset() {
      this.messages = []
      this.context = {
        currentCaseId: null,
        currentStep: 0,
        caseStatus: '',
        caseTitle: '',
        missingFields: [],
        missingMaterials: [],
        evidenceCount: 0,
        documentCount: 0,
        unreadNotifications: 0,
        nextAction: null,
        nextActionRoute: null,
        canProceed: false,
        blockers: [],
        suggestions: []
      }
      this.isPanelOpen = false
      this.hasGreeted = false
      this.dismissedIds.clear()
      this._cooldowns.clear()
      this._msgCounter = 0
    }
  }
})
