<template>
  <section v-loading="loading" class="dashboard-page">
    <div class="metric-grid">
      <div v-for="item in metrics" :key="item.label" class="metric-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </div>
    </div>

    <div class="dashboard-grid">
      <div class="work-panel">
        <div class="panel-header">
          <h2>案件状态分布</h2>
        </div>
        <div ref="caseChartRef" class="chart-box" />
      </div>

      <div class="work-panel">
        <div class="panel-header">
          <h2>今日待办</h2>
        </div>
        <el-empty v-if="todos.length === 0" description="暂无待办" />
        <el-timeline v-else>
          <el-timeline-item v-for="todo in todos" :key="todo" :timestamp="todo">
            需要处理
          </el-timeline-item>
        </el-timeline>
      </div>

      <div class="work-panel">
        <div class="panel-header">
          <h2>最近通知</h2>
        </div>
        <el-empty v-if="recentNotices.length === 0" description="暂无通知" />
        <div v-for="notice in recentNotices" :key="notice.title + notice.createdAt" class="notice-row">
          <strong>{{ notice.title }}</strong>
          <span>{{ notice.content }}</span>
        </div>
      </div>

      <div class="work-panel">
        <div class="panel-header">
          <h2>系统运行状态</h2>
        </div>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="Gateway">在线</el-descriptions-item>
          <el-descriptions-item label="WebSocket">{{ notifyStore.socketStatus }}</el-descriptions-item>
          <el-descriptions-item label="在线用户">接口待后端提供</el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import * as echarts from 'echarts'
import { computed, nextTick, onMounted, ref } from 'vue'

import { getCaseList } from '@/api/case'
import { getTemplateList } from '@/api/document'
import { getEvidenceList } from '@/api/evidence'
import { getNotifyMessages, getUnreadCount } from '@/api/notify'
import type { CaseItem, NotifyMessageItem } from '@/api/models'
import { useNotifyStore } from '@/stores/notify'

const loading = ref(false)
const cases = ref<CaseItem[]>([])
const evidenceTotal = ref(0)
const templateTotal = ref(0)
const unreadCount = ref(0)
const recentNotices = ref<NotifyMessageItem[]>([])
const caseChartRef = ref<HTMLElement>()
const notifyStore = useNotifyStore()

const metrics = computed(() => [
  { label: '案件总数', value: cases.value.length },
  { label: '证据总数', value: evidenceTotal.value },
  { label: '文书模板', value: templateTotal.value },
  { label: '未读通知', value: unreadCount.value }
])

const todos = computed(() => cases.value.filter((item) => ['SUBMITTED', 'FILING_REVIEW', 'JUDGEMENT_PENDING'].includes(item.status)).map((item) => item.caseTitle))

onMounted(loadDashboard)

async function loadDashboard() {
  loading.value = true
  try {
    const [casePage, evidencePage, templates, unread, notices] = await Promise.allSettled([
      getCaseList({ pageNo: 1, pageSize: 100 }),
      getEvidenceList({ pageNo: 1, pageSize: 1 }),
      getTemplateList(),
      getUnreadCount(),
      getNotifyMessages({ pageNo: 1, pageSize: 5 })
    ])

    if (casePage.status === 'fulfilled') cases.value = casePage.value.records || []
    if (evidencePage.status === 'fulfilled') evidenceTotal.value = evidencePage.value.total || 0
    if (templates.status === 'fulfilled') templateTotal.value = templates.value.length
    if (unread.status === 'fulfilled') unreadCount.value = unread.value
    if (notices.status === 'fulfilled') {
      recentNotices.value = Array.isArray(notices.value) ? notices.value : notices.value.records || []
    }

    await nextTick()
    renderCaseChart()
  } finally {
    loading.value = false
  }
}

function renderCaseChart() {
  if (!caseChartRef.value) return
  const chart = echarts.init(caseChartRef.value)
  const grouped = cases.value.reduce<Record<string, number>>((acc, item) => {
    acc[item.status] = (acc[item.status] || 0) + 1
    return acc
  }, {})
  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['48%', '72%'],
        data: Object.entries(grouped).map(([name, value]) => ({ name, value }))
      }
    ]
  })
}
</script>
