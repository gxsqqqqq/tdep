<template>
  <div class="my-cases">
    <!-- Agent Welcome (new users) -->
    <InlineHint
      v-if="showWelcome"
      type="action"
      title="开始您的第一个案件"
      content="您还没有案件记录。点击下方按钮创建您的立案申请，系统将全程引导您完成流程。"
      action-label="创建新案件"
      @action="$router.push('/case/create')"
    />

    <!-- Agent: has active cases needing attention -->
    <InlineHint
      v-if="attentionHint"
      :type="attentionHint.type"
      :title="attentionHint.title"
      :content="attentionHint.content"
      :action-label="attentionHint.actionLabel"
      @action="$router.push(attentionHint.route)"
    />

    <!-- Page Header -->
    <div class="page-header">
      <h1 class="page-header__title">我的案件</h1>
      <button class="btn-primary" @click="$router.push('/case/create')">
        + 创建新案件
      </button>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <div class="filter-tabs">
        <button
          v-for="tab in filterTabs"
          :key="tab.value"
          class="filter-tab"
          :class="{ 'filter-tab--active': activeFilter === tab.value }"
          @click="activeFilter = tab.value"
        >
          {{ tab.label }}
          <span v-if="tab.count > 0" class="filter-tab__count">{{ tab.count }}</span>
        </button>
      </div>
      <input
        v-model="searchKeyword"
        class="search-input"
        placeholder="搜索案件号或标题..."
        clearable
      />
    </div>

    <!-- Case Grid -->
    <div v-loading="caseStore.loading" class="case-grid">
      <div
        v-for="item in filteredCases"
        :key="item.id"
        class="case-card"
        :class="{ 'case-card--attention': needsAttention(item) }"
        @click="$router.push(`/case/${item.id}`)"
      >
        <div class="case-card__top">
          <span class="case-card__no">{{ item.caseNo }}</span>
          <span class="case-card__status" :class="`case-card__status--${statusCategory(item.status)}`">
            {{ getCaseStatusName(item.status) }}
          </span>
        </div>
        <h3 class="case-card__title">{{ item.caseTitle }}</h3>
        <p class="case-card__cause">{{ item.causeAction || '-' }}</p>

        <!-- Progress dots -->
        <div class="case-card__progress">
          <div
            v-for="i in 5"
            :key="i"
            class="case-card__dot"
            :class="{
              'case-card__dot--done': i - 1 < getStep(item.status),
              'case-card__dot--active': i - 1 === getStep(item.status)
            }"
          />
        </div>

        <!-- Agent hint for this case -->
        <div v-if="getCaseHint(item)" class="case-card__agent">
          {{ getCaseHint(item) }}
        </div>

        <div class="case-card__footer">
          <span class="case-card__time">{{ formatDateTime(item.createdAt) }}</span>
          <span class="case-card__link">查看详情 →</span>
        </div>
      </div>

      <div v-if="!caseStore.loading && filteredCases.length === 0" class="empty-state">
        <div class="empty-state__icon">📂</div>
        <div class="empty-state__text">暂无案件记录</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'

import InlineHint from '@/components/InlineHint/index.vue'
import { useCaseStore } from '@/stores/case'
import { useAgentStore } from '@/stores/agent'
import { useAuthStore } from '@/stores/auth'
import { formatDateTime } from '@/utils/formatter'
import { getCaseStatusName } from '@/utils/status'

const caseStore = useCaseStore()
const agentStore = useAgentStore()
const authStore = useAuthStore()

const searchKeyword = ref('')
const activeFilter = ref('all')

const statusStepMap: Record<string, number> = {
  DRAFT: 0, SUBMITTED: 0, FILING_REVIEW: 0, FILING_REJECTED: 0,
  ACCEPTED: 1, EVIDENCE_SUBMITTING: 2,
  SCHEDULED: 3, IN_TRIAL: 3, JUDGED: 4, EFFECTIVE: 4, CLOSED: 4
}

const filterTabs = computed(() => [
  { label: '全部', value: 'all', count: caseStore.myCases.length },
  { label: '进行中', value: 'active', count: caseStore.activeCases.length },
  { label: '已结案', value: 'closed', count: caseStore.closedCases.length }
])

const filteredCases = computed(() => {
  let list = caseStore.myCases
  if (activeFilter.value === 'active') list = caseStore.activeCases
  else if (activeFilter.value === 'closed') list = caseStore.closedCases

  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase()
    list = list.filter((c) => c.caseNo.toLowerCase().includes(kw) || c.caseTitle.toLowerCase().includes(kw))
  }
  return list
})

const showWelcome = computed(() => !caseStore.loading && caseStore.myCases.length === 0)

const attentionHint = computed(() => {
  // Find first case that needs attention
  const urgentCase = caseStore.activeCases.find((c) =>
    c.status === 'FILING_REJECTED' || c.status === 'EVIDENCE_SUBMITTING'
  )
  if (!urgentCase) return null

  if (urgentCase.status === 'FILING_REJECTED') {
    return {
      type: 'warning' as const,
      title: '立案被驳回',
      content: `案件"${urgentCase.caseTitle}"的立案申请被驳回，请查看原因并重新提交。`,
      actionLabel: '立即处理',
      route: `/case/${urgentCase.id}`
    }
  }

  if (urgentCase.status === 'EVIDENCE_SUBMITTING') {
    return {
      type: 'action' as const,
      title: '需要上传证据',
      content: `案件"${urgentCase.caseTitle}"正处于举证阶段，请尽快上传相关证据材料。`,
      actionLabel: '去上传',
      route: `/case/${urgentCase.id}/evidence`
    }
  }

  return null
})

function getStep(status: string) {
  return statusStepMap[status] ?? 0
}

function statusCategory(status: string): string {
  if (['DRAFT', 'SUBMITTED', 'FILING_REVIEW'].includes(status)) return 'pending'
  if (['FILING_REJECTED'].includes(status)) return 'rejected'
  if (['ACCEPTED', 'EVIDENCE_SUBMITTING', 'SCHEDULED', 'IN_TRIAL'].includes(status)) return 'active'
  return 'done'
}

function needsAttention(item: { status: string }) {
  return ['FILING_REJECTED', 'EVIDENCE_SUBMITTING'].includes(item.status)
}

function getCaseHint(item: { status: string; caseTitle: string }): string | null {
  if (item.status === 'FILING_REJECTED') return '⚠️ 立案被驳回，需要重新提交'
  if (item.status === 'EVIDENCE_SUBMITTING') return '📎 举证阶段，请上传证据'
  if (item.status === 'SUBMITTED' || item.status === 'FILING_REVIEW') return '⏳ 立案审核中'
  if (item.status === 'JUDGED' || item.status === 'EFFECTIVE') return '📜 判决已下达'
  return null
}

onMounted(async () => {
  await caseStore.loadMyCases({ pageNo: 1, pageSize: 100 })

  // Greet user
  const name = authStore.currentUser?.realName || authStore.currentUser?.username
  agentStore.greet(name)

  // Show welcome for new users
  if (caseStore.myCases.length === 0) {
    agentStore.welcomeNewUser()
  }
})
</script>

<style scoped>
.my-cases {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-header__title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #202124;
}

.btn-primary {
  padding: 10px 24px;
  border-radius: 8px;
  border: none;
  background: #1a73e8;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-primary:hover {
  background: #1557b0;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(26, 115, 232, 0.3);
}

/* Filter */
.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-tabs {
  display: flex;
  gap: 4px;
  background: #f1f3f4;
  border-radius: 10px;
  padding: 4px;
}

.filter-tab {
  padding: 7px 16px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #5f6368;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.filter-tab--active {
  background: white;
  color: #1a73e8;
  font-weight: 600;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.filter-tab__count {
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 10px;
  background: #e8f0fe;
  color: #1a73e8;
}

.search-input {
  padding: 8px 14px;
  border: 1px solid #e0e4e8;
  border-radius: 8px;
  font-size: 13px;
  width: 220px;
  outline: none;
  transition: border-color 0.2s;
}

.search-input:focus {
  border-color: #1a73e8;
}

/* Case Grid */
.case-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.case-card {
  padding: 20px;
  border-radius: 16px;
  background: white;
  border: 1px solid #e0e4e8;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.case-card:hover {
  border-color: #a8c7fa;
  box-shadow: 0 4px 16px rgba(26, 115, 232, 0.1);
  transform: translateY(-2px);
}

.case-card--attention {
  border-color: #f5c6c6;
  animation: attention-glow 3s ease-in-out infinite;
}

.case-card__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.case-card__no {
  font-size: 12px;
  color: #9aa0a6;
  font-family: monospace;
}

.case-card__status {
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.case-card__status--pending  { background: #e8f0fe; color: #1a73e8; }
.case-card__status--active   { background: #e6f4ea; color: #1e8e3e; }
.case-card__status--rejected { background: #fce8e6; color: #c5221f; }
.case-card__status--done     { background: #f1f3f4; color: #5f6368; }

.case-card__title {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 600;
  color: #202124;
  line-height: 1.4;
}

.case-card__cause {
  margin: 0 0 14px;
  font-size: 13px;
  color: #5f6368;
}

.case-card__progress {
  display: flex;
  gap: 4px;
  margin-bottom: 12px;
}

.case-card__dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #e0e4e8;
  transition: background 0.2s;
}

.case-card__dot--done {
  background: #34a853;
}

.case-card__dot--active {
  background: #1a73e8;
  box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.2);
}

.case-card__agent {
  padding: 8px 12px;
  border-radius: 8px;
  background: #fffdf0;
  border: 1px solid #fbbc04;
  font-size: 12px;
  color: #5f6368;
  margin-bottom: 12px;
}

.case-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.case-card__time {
  font-size: 12px;
  color: #9aa0a6;
}

.case-card__link {
  font-size: 13px;
  color: #1a73e8;
  font-weight: 500;
}

/* Empty */
.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 48px 20px;
}

.empty-state__icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-state__text {
  color: #9aa0a6;
  font-size: 14px;
}

@keyframes attention-glow {
  0%, 100% { box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04); }
  50% { box-shadow: 0 0 0 3px rgba(234, 67, 53, 0.15), 0 4px 16px rgba(234, 67, 53, 0.08); }
}

/* Dark mode */
:root.dark .page-header__title { color: #e2e8f0; }
:root.dark .case-card { background: #1e293b; border-color: #334155; }
:root.dark .case-card:hover { border-color: #1a73e8; }
:root.dark .case-card__title { color: #e2e8f0; }
:root.dark .case-card__cause { color: #94a3b8; }
:root.dark .case-card__dot { background: #334155; }
:root.dark .case-card__agent { background: #2d2a1a; border-color: #92760b; color: #94a3b8; }
:root.dark .filter-tabs { background: #1e293b; }
:root.dark .filter-tab--active { background: #334155; color: #60a5fa; }
:root.dark .search-input { background: #1e293b; border-color: #334155; color: #e2e8f0; }
</style>
