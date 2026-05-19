<template>
  <div class="case-detail">
    <ErrorBoundary @retry="fetchCaseData">
      <div v-if="loading" class="case-detail__skeleton">
        <CaseDetailSkeleton />
      </div>
      <template v-else-if="caseInfo">
        <header class="case-hero">
          <div class="case-hero__back">
            <button class="back-btn" @click="goBack" aria-label="返回案件列表">
              <span class="back-btn__icon">←</span>
              <span class="back-btn__text">返回</span>
            </button>
          </div>
          <div class="case-hero__body">
            <div class="case-hero__title-row">
              <h1 class="case-hero__title">{{ caseInfo.title }}</h1>
              <span
                class="case-hero__badge"
                :class="`case-hero__badge--${statusCategory}`"
              >
                <span class="case-hero__dot" />
                {{ statusLabel }}
              </span>
            </div>
            <div class="case-hero__no">#{{ caseInfo.caseNumber }}</div>
            <dl class="case-hero__meta">
              <div class="case-hero__meta-item">
                <dt class="case-hero__meta-label">法院</dt>
                <dd class="case-hero__meta-value">{{ caseInfo.courtName || '—' }}</dd>
              </div>
              <div class="case-hero__meta-item">
                <dt class="case-hero__meta-label">法官</dt>
                <dd class="case-hero__meta-value">{{ caseInfo.judge?.name || '未分配' }}</dd>
              </div>
              <div class="case-hero__meta-item">
                <dt class="case-hero__meta-label">创建时间</dt>
                <dd class="case-hero__meta-value">{{ formatDate(caseInfo.createdAt) }}</dd>
              </div>
            </dl>
          </div>
        </header>

        <nav class="case-tabs" role="tablist" aria-label="案件功能导航">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            role="tab"
            :aria-selected="activeTab === tab.key"
            class="case-tab"
            :class="{ 'case-tab--active': activeTab === tab.key }"
            @click="switchTab(tab.key)"
          >
            <span class="case-tab__icon">{{ tab.icon }}</span>
            <span class="case-tab__label">{{ tab.label }}</span>
          </button>
        </nav>

        <div class="case-layout">
          <main class="case-main" role="tabpanel" :aria-label="currentTabLabel">
            <router-view v-if="$route.name !== 'CaseDetail'" />
            <section v-else class="case-default-view">
              <div class="timeline-section">
                <div class="section-header">
                  <h2 class="section-title">
                    <span class="section-title__icon">⏱️</span>
                    案件流程时间轴
                  </h2>
                  <span class="section-subtitle">全生命周期追踪</span>
                </div>
                <ProcessTimeline
                  :steps="timelineSteps"
                  :current="currentStepIndex"
                  @step-click="handleStepClick"
                />
                <div class="timeline-logs">
                  <article
                    v-for="(log, idx) in timelineLogs"
                    :key="idx"
                    class="timeline-log__item"
                    :class="`timeline-log__item--${log.status}`"
                  >
                    <div class="timeline-log__dot" />
                    <div class="timeline-log__body">
                      <div class="timeline-log__header">
                        <time class="timeline-log__time">{{ log.time }}</time>
                        <span
                          class="timeline-log__tag"
                          :class="`timeline-log__tag--${log.status}`"
                        >
                          {{ log.tag }}
                        </span>
                      </div>
                      <h3 class="timeline-log__title">{{ log.title }}</h3>
                      <p class="timeline-log__desc">{{ log.description }}</p>
                      <div v-if="log.operator" class="timeline-log__operator">
                        操作人：{{ log.operator }}
                      </div>
                    </div>
                  </article>
                </div>
              </div>

              <section v-if="recentDocuments.length" class="recent-docs">
                <div class="section-header">
                  <h2 class="section-title">
                    <span class="section-title__icon">📄</span>
                    最近文书
                  </h2>
                  <router-link :to="`/case/${caseId}/documents`" class="section-link">
                    查看全部 →
                  </router-link>
                </div>
                <div class="doc-list">
                  <div
                    v-for="doc in recentDocuments"
                    :key="doc.id"
                    class="doc-item"
                  >
                    <span class="doc-item__icon">📄</span>
                    <div class="doc-item__info">
                      <span class="doc-item__name">{{ doc.title }}</span>
                      <span class="doc-item__date">{{ doc.createdAt }}</span>
                    </div>
                    <span
                      class="doc-item__status"
                      :class="`doc-item__status--${doc.status}`"
                    >
                      {{ docStatusLabel(doc.status) }}
                    </span>
                  </div>
                </div>
              </section>
            </section>
          </main>

          <aside class="case-sidebar">
            <div class="sidebar-panel sidebar-panel--actions">
              <h3 class="sidebar-panel__title">快捷操作</h3>
              <div class="action-list">
                <button
                  v-for="action in quickActions"
                  :key="action.key"
                  class="action-btn"
                  :class="{ 'action-btn--primary': action.primary }"
                  @click="handleAction(action.key)"
                >
                  <span class="action-btn__icon">{{ action.icon }}</span>
                  <span class="action-btn__label">{{ action.label }}</span>
                  <span v-if="action.arrow" class="action-btn__arrow">→</span>
                </button>
              </div>
            </div>

            <div class="sidebar-panel sidebar-panel--participants">
              <h3 class="sidebar-panel__title">案件参与人</h3>
              <ul class="participant-list">
                <li v-for="p in participants" :key="p.role" class="participant-item">
                  <span class="participant-item__icon">{{ p.icon }}</span>
                  <div class="participant-item__info">
                    <span class="participant-item__role">{{ p.role }}</span>
                    <span class="participant-item__name">{{ p.name }}</span>
                  </div>
                </li>
              </ul>
            </div>

            <div class="sidebar-panel sidebar-panel--stats">
              <h3 class="sidebar-panel__title">案件统计</h3>
              <div class="stats-grid">
                <div class="stat-item">
                  <span class="stat-item__value">{{ caseInfo.evidenceCount }}</span>
                  <span class="stat-item__label">证据材料</span>
                </div>
                <div class="stat-item">
                  <span class="stat-item__value">{{ caseInfo.documentCount }}</span>
                  <span class="stat-item__label">关联文书</span>
                </div>
                <div class="stat-item">
                  <span class="stat-item__value">{{ caseInfo.trialCount }}</span>
                  <span class="stat-item__label">庭审记录</span>
                </div>
                <div class="stat-item">
                  <span class="stat-item__value">{{ daysSinceCreation }}</span>
                  <span class="stat-item__label">已进行天数</span>
                </div>
              </div>
            </div>
          </aside>
        </div>
      </template>
      <EmptyState
        v-else
        icon="📂"
        title="案件不存在"
        description="请检查案件编号是否正确，或联系管理员"
        action-text="返回列表"
        @action="goBack"
      />
    </ErrorBoundary>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CaseStatus, CASE_STATUS_CONFIG } from '@/lib/constants/caseStatus'
import type { Case } from '@/types/case'
import ProcessTimeline from '@/components/ProcessTimeline/index.vue'

const route = useRoute()
const router = useRouter()

const caseId = computed(() => route.params.id as string)
const loading = ref(true)
const error = ref<string | null>(null)
const caseInfo = ref<Case | null>(null)
const activeTab = ref('overview')

const tabs = [
  { key: 'overview', label: '总览', icon: '📋' },
  { key: 'timeline', label: '流程', icon: '⏱️' },
  { key: 'evidence', label: '证据链', icon: '📎' },
  { key: 'documents', label: '文书中心', icon: '📄' },
  { key: 'ai-assistant', label: 'AI 助手', icon: '🤖' },
]

const currentTabLabel = computed(() => {
  const found = tabs.find(t => t.key === activeTab.value)
  return found?.label ?? '总览'
})

const timelineSteps = [
  '创建草稿',
  '提交审核',
  '立案受理',
  '庭审排期',
  '开庭审理',
  '判决宣判',
]

const currentStepIndex = computed(() => {
  const status = caseInfo.value?.status
  if (!status) return 0
  const flow = ['DRAFT', 'PENDING_REVIEW', 'REVIEWING', 'SCHEDULED', 'IN_TRIAL', 'AWAITING_JUDGEMENT']
  return flow.indexOf(status)
})

const timelineLogs = computed(() => {
  if (!caseInfo.value) return []
  return [
    {
      time: '2026-05-17 10:30',
      title: '创建草稿',
      description: `当事人：${caseInfo.value.plaintiff.name} 提交了案件申请`,
      operator: caseInfo.value.plaintiff.name,
      tag: '已完成',
      status: 'done' as const,
    },
    {
      time: '2026-05-17 14:00',
      title: '提交审核',
      description: `系统自动分配给 ${caseInfo.value.judge?.name || '法官'}`,
      operator: '系统',
      tag: '已完成',
      status: 'done' as const,
    },
    {
      time: '2026-05-18 09:00',
      title: '立案受理',
      description: '法官已确认立案，等待排期',
      operator: caseInfo.value.judge?.name,
      tag: '当前阶段',
      status: 'active' as const,
    },
    {
      time: '2026-05-25 14:00',
      title: '庭审排期',
      description: '等待确定庭审时间与地点',
      operator: null,
      tag: '待办',
      status: 'pending' as const,
    },
    {
      time: '2026-06-01 09:30',
      title: '开庭审理',
      description: null,
      operator: null,
      tag: '待办',
      status: 'pending' as const,
    },
    {
      time: '2026-06-15 16:00',
      title: '判决宣判',
      description: null,
      operator: null,
      tag: '终审结案',
      status: 'pending' as const,
    },
  ]
})

const quickActions = [
  { key: 'workflow', label: '推进流程', icon: '▶️', primary: true, arrow: true },
  { key: 'evidence', label: '关联证据', icon: '➕', primary: false },
  { key: 'document', label: '生成文书', icon: '✍️', primary: false },
  { key: 'sign', label: '在线签字', icon: '✒️', primary: false },
  { key: 'ai', label: 'AI 分析', icon: '🤖', primary: false },
]

const participants = computed(() => {
  const c = caseInfo.value
  if (!c) return []
  const list = [
    { role: '原告', name: c.plaintiff.name, icon: '👤' },
    { role: '被告', name: c.defendant.name, icon: '👤' },
  ]
  if (c.judge) list.push({ role: '法官', name: c.judge.name, icon: '👨‍⚖️' })
  if (c.clerk) list.push({ role: '书记员', name: c.clerk.name, icon: '📝' })
  return list
})

const recentDocuments = ref([
  { id: '1', title: '民事起诉状.pdf', status: 'signed', createdAt: '2026-05-17' },
  { id: '2', title: '开庭传票.pdf', status: 'approved', createdAt: '2026-05-18' },
])

const statusLabel = computed(() => {
  if (!caseInfo.value) return ''
  return CASE_STATUS_CONFIG[caseInfo.value.status]?.label || caseInfo.value.status
})

const statusCategory = computed(() => {
  const s = caseInfo.value?.status
  if (!s) return 'pending'
  if ([CaseStatus.DRAFT, CaseStatus.PENDING_REVIEW].includes(s)) return 'pending'
  if ([CaseStatus.REVIEWING, CaseStatus.SCHEDULED, CaseStatus.IN_TRIAL].includes(s)) return 'active'
  if ([CaseStatus.JUDGED, CaseStatus.CLOSED].includes(s)) return 'done'
  if (s === CaseStatus.APPEALED) return 'warning'
  return 'pending'
})

const daysSinceCreation = computed(() => {
  if (!caseInfo.value?.createdAt) return 0
  const created = new Date(caseInfo.value.createdAt)
  const now = new Date()
  return Math.floor((now.getTime() - created.getTime()) / (1000 * 60 * 60 * 24))
})

function switchTab(key: string) {
  activeTab.value = key
  if (key === 'overview') {
    router.push(`/case/${caseId.value}`)
  } else {
    router.push(`/case/${caseId.value}/${key}`)
  }
}

function handleAction(key: string) {
  const map: Record<string, string> = {
    workflow: `/case/${caseId.value}/workflow`,
    evidence: `/case/${caseId.value}/evidence`,
    document: `/case/${caseId.value}/documents`,
    sign: `/case/${caseId.value}/documents`,
    ai: `/case/${caseId.value}/ai-assistant`,
  }
  const target = map[key]
  if (target) router.push(target)
}

function handleStepClick(index: number) {
  console.log('Step clicked:', index)
}

function goBack() {
  router.back()
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

function docStatusLabel(status: string): string {
  const map: Record<string, string> = {
    draft: '草稿',
    reviewing: '审核中',
    approved: '已生成',
    signed: '已签署',
    rejected: '已驳回',
  }
  return map[status] || status
}

async function fetchCaseData() {
  loading.value = true
  error.value = null
  try {
    await new Promise(resolve => setTimeout(resolve, 600))
    caseInfo.value = {
      id: caseId.value,
      caseNumber: 'TDEP-20260517-001',
      title: '民事合同纠纷案',
      caseType: 'civil_contract',
      status: CaseStatus.REVIEWING,
      plaintiff: { id: '1', name: '张三', type: 'individual', contactPhone: '138****1234' },
      defendant: { id: '2', name: '李四', type: 'individual', contactPhone: '139****5678' },
      judge: { id: 'j1', name: '王法官', title: '审判员' },
      clerk: { id: 'c1', name: '赵书记' },
      courtName: '北京市海淀区人民法院',
      filingDate: '2026-05-18',
      createdAt: '2026-05-17T10:30:00Z',
      updatedAt: '2026-05-18T09:00:00Z',
      evidenceCount: 5,
      documentCount: 3,
      trialCount: 0,
    } as Case
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}

watch(() => route.path, () => {
  const path = route.path
  if (path.endsWith('/timeline')) activeTab.value = 'timeline'
  else if (path.endsWith('/evidence')) activeTab.value = 'evidence'
  else if (path.endsWith('/trials')) activeTab.value = 'trials'
  else if (path.endsWith('/documents')) activeTab.value = 'documents'
  else if (path.endsWith('/workflow')) activeTab.value = 'workflow'
  else if (path.endsWith('/ai-assistant')) activeTab.value = 'ai-assistant'
  else activeTab.value = 'overview'
}, { immediate: true })

onMounted(fetchCaseData)
</script>

<style scoped>
.case-detail {
  max-width: 1400px;
  margin: 0 auto;
}

.case-detail__skeleton {
  padding: var(--spacing-card);
}

/* ── Hero ── */
.case-hero {
  background: linear-gradient(135deg, #1a73e8 0%, #0d47a1 100%);
  border-radius: var(--border-radius);
  padding: 24px 28px;
  color: white;
  margin-bottom: var(--spacing-card);
}

.case-hero__back {
  margin-bottom: 16px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.back-btn__icon {
  font-size: 16px;
}

.case-hero__title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.case-hero__title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.3;
}

.case-hero__badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(4px);
}

.case-hero__badge--pending {
  background: rgba(255, 255, 255, 0.2);
}
.case-hero__badge--active {
  background: rgba(76, 175, 80, 0.3);
  animation: pulse-badge 2s ease-in-out infinite;
}
.case-hero__badge--done {
  background: rgba(76, 175, 80, 0.25);
}
.case-hero__badge--warning {
  background: rgba(255, 152, 0, 0.3);
}

.case-hero__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #4caf50;
  animation: dot-pulse 1.5s ease-in-out infinite;
}

@keyframes pulse-badge {
  0%, 100% { box-shadow: 0 0 0 0 rgba(76, 175, 80, 0.3); }
  50% { box-shadow: 0 0 0 6px rgba(76, 175, 80, 0.1); }
}

@keyframes dot-pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(0.85); }
}

.case-hero__no {
  font-size: 13px;
  opacity: 0.75;
  font-family: monospace;
  margin-top: 4px;
}

.case-hero__meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.15);
}

.case-hero__meta-item dt {
  font-size: 11px;
  text-transform: uppercase;
  opacity: 0.65;
  letter-spacing: 0.5px;
}

.case-hero__meta-item dd {
  margin: 2px 0 0;
  font-size: 14px;
  font-weight: 500;
}

/* ── Tabs ── */
.case-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: var(--spacing-card);
  overflow-x: auto;
  padding-bottom: 4px;
  -webkit-overflow-scrolling: touch;
}

.case-tabs::-webkit-scrollbar {
  height: 0;
}

.case-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius);
  background: var(--bg-card);
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s ease;
  user-select: none;
}

.case-tab:hover {
  border-color: var(--color-primary-light);
  color: var(--color-primary);
  background: var(--color-primary-bg);
}

.case-tab--active {
  border-color: var(--color-primary);
  background: var(--color-primary);
  color: white;
  box-shadow: 0 2px 8px rgba(26, 115, 232, 0.25);
}

.case-tab__icon {
  font-size: 16px;
  line-height: 1;
}

/* ── Layout ── */
.case-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: var(--spacing-card);
  align-items: start;
}

.case-main {
  min-width: 0;
}

/* ── Default View (Overview Content) ── */
.case-default-view {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-card);
}

.section-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

.section-title__icon {
  font-size: 20px;
}

.section-subtitle {
  font-size: 13px;
  color: var(--text-hint);
}

.section-link {
  font-size: 13px;
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
}

.section-link:hover {
  text-decoration: underline;
}

/* Timeline */
.timeline-section {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius);
  padding: var(--spacing-card);
}

.timeline-logs {
  margin-top: 24px;
  padding-top: 8px;
}

.timeline-log__item {
  position: relative;
  padding-left: 32px;
  padding-bottom: 28px;
  border-left: 2px solid var(--border-color);
}

.timeline-log__item:last-child {
  border-left-color: transparent;
  padding-bottom: 0;
}

.timeline-log__dot {
  position: absolute;
  left: -9px;
  top: 4px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: var(--border-color);
  border: 3px solid var(--bg-card);
  transition: all 0.2s;
}

.timeline-log__item--done .timeline-log__dot {
  background: var(--color-success);
  box-shadow: 0 0 0 2px var(--color-success-bg);
}

.timeline-log__item--active .timeline-log__dot {
  background: var(--color-primary);
  box-shadow: 0 0 0 2px var(--color-primary-bg), 0 0 8px rgba(26, 115, 232, 0.35);
  animation: active-dot 2s ease-in-out infinite;
}

.timeline-log__item--pending .timeline-log__dot {
  background: var(--text-hint);
  box-shadow: none;
}

@keyframes active-dot {
  0%, 100% { box-shadow: 0 0 0 2px var(--color-primary-bg), 0 0 8px rgba(26, 115, 232, 0.35); }
  50% { box-shadow: 0 0 0 2px var(--color-primary-bg), 0 0 14px rgba(26, 115, 232, 0.2); }
}

.timeline-log__header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}

.timeline-log__time {
  font-size: 12px;
  color: var(--text-hint);
  font-family: monospace;
}

.timeline-log__tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.timeline-log__tag--done {
  background: var(--color-success-bg);
  color: var(--color-success-dark, #389e0d);
}
.timeline-log__tag--active {
  background: var(--color-primary-bg);
  color: var(--color-primary);
}
.timeline-log__tag--pending {
  background: var(--color-info-bg);
  color: var(--text-hint);
}

.timeline-log__title {
  margin: 0 0 4px;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.timeline-log__desc {
  margin: 0 0 4px;
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
}

.timeline-log__operator {
  font-size: 12px;
  color: var(--text-hint);
}

/* Recent Docs */
.recent-docs {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius);
  padding: var(--spacing-card);
}

.doc-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.doc-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: var(--border-radius-sm);
  background: var(--background-secondary, #fafafa);
  transition: background 0.15s;
}

.doc-item:hover {
  background: var(--color-info-bg);
}

.doc-item__icon {
  font-size: 22px;
}

.doc-item__info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.doc-item__name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.doc-item__date {
  font-size: 12px;
  color: var(--text-hint);
}

.doc-item__status {
  padding: 3px 10px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

.doc-item__status--draft { background: var(--color-info-bg); color: var(--text-hint); }
.doc-item__status--approved { background: var(--color-primary-bg); color: var(--color-primary); }
.doc-item__status--signed { background: var(--color-success-bg); color: var(--color-success-dark, #389e0d); }

/* ── Sidebar ── */
.case-sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-card);
  position: sticky;
  top: calc(var(--header-height, 60px) + var(--spacing-lg, 24px));
}

.sidebar-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius);
  padding: 20px;
}

.sidebar-panel__title {
  margin: 0 0 16px;
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  position: relative;
  padding-left: 10px;
}

.sidebar-panel__title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 3px;
  bottom: 3px;
  width: 3px;
  border-radius: 2px;
  background: var(--color-primary);
}

.action-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  background: var(--bg-card);
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.action-btn:hover {
  border-color: var(--color-primary);
  background: var(--color-primary-bg);
  color: var(--color-primary);
}

.action-btn--primary {
  border-color: var(--color-primary);
  background: var(--color-primary);
  color: white;
}

.action-btn--primary:hover {
  background: var(--color-primary-hover);
  color: white;
}

.action-btn__icon {
  font-size: 18px;
  line-height: 1;
}

.action-btn__label {
  flex: 1;
}

.action-btn__arrow {
  font-size: 14px;
  opacity: 0.7;
}

/* Participants */
.participant-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.participant-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid var(--border-color, #f0f0f0);
}

.participant-item:last-child {
  border-bottom: none;
}

.participant-item__icon {
  font-size: 22px;
  flex-shrink: 0;
}

.participant-item__info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.participant-item__role {
  font-size: 11px;
  color: var(--text-hint);
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.participant-item__name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

/* Stats */
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  border-radius: var(--border-radius-sm);
  background: var(--background-secondary, #fafafa);
}

.stat-item__value {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-primary);
  line-height: 1.2;
}

.stat-item__label {
  font-size: 11px;
  color: var(--text-hint);
  margin-top: 2px;
}

/* ── Responsive ── */
@media (max-width: 1024px) {
  .case-layout {
    grid-template-columns: 1fr;
  }

  .case-sidebar {
    position: static;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .sidebar-panel {
    flex: 1;
    min-width: 260px;
  }
}

@media (max-width: 640px) {
  .case-hero {
    padding: 18px 20px;
  }

  .case-hero__title {
    font-size: 19px;
  }

  .case-hero__meta {
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }

  .case-tabs {
    gap: 6px;
  }

  .case-tab {
    padding: 10px 14px;
    font-size: 13px;
  }

  .case-tab__label {
    display: none;
  }

  .case-sidebar {
    flex-direction: column;
  }

  .sidebar-panel {
    min-width: unset;
  }

  .stats-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>
