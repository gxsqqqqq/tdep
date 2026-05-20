<template>
  <el-card shadow="hover" class="audit-log-panel">
    <template #header>
      <div class="flex items-center justify-between flex-wrap gap-3">
        <div class="flex items-center gap-2">
          <span class="text-lg">📋</span>
          <span class="font-semibold text-gray-800">审计日志</span>
        </div>

        <div class="flex items-center gap-2 flex-wrap">
          <el-select v-model="filterAction" placeholder="操作类型" size="small" clearable style="width: 120px;">
            <el-option label="全部" value="" />
            <el-option label="创建" value="CREATE" />
            <el-option label="查看" value="READ" />
            <el-option label="签署" value="SIGN" />
            <el-option label="下载" value="DOWNLOAD" />
            <el-option label="归档" value="ARCHIVE" />
            <el-option label="审核" value="REVIEW" />
          </el-select>

          <el-select v-model="filterTimeRange" placeholder="时间范围" size="small" style="width: 120px;">
            <el-option label="全部" value="all" />
            <el-option label="今天" value="today" />
            <el-option label="最近7天" value="week" />
            <el-option label="最近30天" value="month" />
          </el-select>

          <el-input
            v-model="searchKeyword"
            placeholder="搜索操作人或描述"
            size="small"
            clearable
            :prefix-icon="Search"
            style="width: 200px;"
          />
        </div>
      </div>
    </template>

    <div class="audit-content">
      <!-- 统计摘要 -->
      <div class="stats-summary flex items-center justify-between flex-wrap gap-4 p-4 bg-gradient-to-r from-slate-50 to-gray-50 rounded-lg mb-6 border border-gray-100">
        <div class="stat-item">
          <span class="text-xs text-gray-500">总操作</span>
          <span class="ml-1 text-lg font-bold text-judicial-700">{{ filteredLogs.length }}</span>
          <span class="text-xs text-gray-400">次</span>
        </div>

        <div class="h-4 w-px bg-gray-200"></div>

        <div v-for="(count, action) in actionStats" :key="action" class="stat-item flex items-center gap-1.5">
          <span
            class="w-2.5 h-2.5 rounded-full"
            :style="{ backgroundColor: getActionColor(action as AuditActionType) }"
          ></span>
          <span class="text-sm text-gray-600">{{ getActionLabel(action as AuditActionType) }}</span>
          <span class="font-semibold text-judicial-700">{{ count }}</span>
        </div>
      </div>

      <!-- 日志列表 -->
      <div v-if="filteredLogs.length === 0" class="empty-state py-12 text-center">
        <el-empty description="暂无符合条件的审计日志" :image-size="80" />
      </div>

      <div v-else class="timeline-container relative pl-8">
        <!-- 时间轴主线 -->
        <div class="absolute left-[11px] top-0 bottom-0 w-0.5 bg-gray-200"></div>

        <!-- 按日期分组显示 -->
        <template v-for="(group, dateKey) in groupedLogs" :key="dateKey">
          <div class="date-header sticky top-0 z-10 py-2 mb-3">
            <span class="inline-flex items-center px-3 py-1 bg-white border border-gray-200 rounded-full text-sm font-medium text-gray-600 shadow-sm">
              📅 {{ formatDateGroup(dateKey) }}
            </span>
          </div>

          <div class="log-entries space-y-4">
            <div
              v-for="entry in group"
              :key="entry.id"
              class="log-entry relative group"
            >
              <!-- 时间轴节点 -->
              <div
                class="absolute -left-8 top-3 w-6 h-6 rounded-full border-2 flex items-center justify-center z-10 transition-all duration-200"
                :class="getActionNodeClass(entry.action)"
                :style="{ borderColor: getActionColor(entry.action), backgroundColor: getActionColor(entry.action) + '20' }"
              >
                <component :is="getActionIcon(entry.action)" :size="12" :color="getActionColor(entry.action)" />
              </div>

              <!-- 连接线 -->
              <div
                class="absolute -left-8 top-9 w-px h-full min-h-[20px] bg-gray-200"
                :class="{ '!bg-transparent': entry === group[group.length - 1] }"
              ></div>

              <!-- 日志卡片 -->
              <div
                class="log-card bg-white rounded-lg border transition-all duration-200 hover:shadow-md cursor-default"
                :class="[getActionCardClass(entry.action), { 'ring-2 ring-offset-1': hoveredEntry === entry.id }]"
                @mouseenter="hoveredEntry = entry.id"
                @mouseleave="hoveredEntry = ''"
              >
                <div class="p-4">
                  <div class="flex items-start justify-between gap-3">
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center gap-2 mb-1">
                        <span class="font-semibold text-gray-800">{{ entry.actor }}</span>
                        <el-tag size="small" :type="getActionTagType(entry.action)" effect="plain" round>
                          {{ getActionLabel(entry.action) }}
                        </el-tag>
                      </div>

                      <p class="text-sm text-gray-600 line-clamp-2">{{ entry.description }}</p>

                      <div class="mt-2 flex items-center gap-4 text-xs text-gray-400">
                        <span class="flex items-center gap-1">
                          <el-icon><Clock /></el-icon>
                          {{ formatTime(entry.timestamp) }}
                        </span>
                        <span v-if="entry.ipAddress" class="flex items-center gap-1">
                          <el-icon><Monitor /></el-icon>
                          {{ entry.ipAddress }}
                        </span>
                      </div>
                    </div>

                    <el-tag size="small" type="info" effect="plain" class="flex-shrink-0">
                      {{ entry.actorRole }}
                    </el-tag>
                  </div>

                  <!-- Trace ID 和扩展信息 -->
                  <div
                    v-show="hoveredEntry === entry.id || expandedEntries.includes(entry.id)"
                    class="mt-3 pt-3 border-t border-gray-100 space-y-2"
                  >
                    <div class="trace-id-row flex items-center gap-2">
                      <span class="text-xs text-gray-500 font-mono">TraceID:</span>
                      <code
                        class="trace-code text-xs px-2 py-1 bg-slate-900 text-green-400 rounded font-mono cursor-pointer hover:bg-slate-800 transition-colors select-all"
                        @click="copyTraceId(entry.traceId)"
                      >
                        {{ entry.traceId }}
                      </code>
                      <el-button link size="small" @click="copyTraceId(entry.traceId)">
                        <el-icon><CopyDocument /></el-icon>
                      </el-button>
                    </div>

                    <div v-if="entry.metadata && Object.keys(entry.metadata).length > 0" class="metadata-grid grid grid-cols-2 gap-2 text-xs">
                      <div
                        v-for="(value, key) in entry.metadata"
                        :key="key"
                        class="flex gap-1"
                      >
                        <span class="text-gray-500">{{ key }}:</span>
                        <span class="text-gray-700 font-mono truncate">{{ String(value) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- 加载更多 -->
      <div v-if="hasMore" class="load-more mt-6 text-center">
        <el-button text type="primary" @click="loadMore">
          加载更多记录
          <el-icon class="ml-1"><ArrowDown /></el-icon>
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  Clock,
  Monitor,
  CopyDocument,
  ArrowDown,
  Document,
  View,
  EditPen,
  Download,
  FolderOpened,
  CircleCheck,
  CircleClose,
} from '@element-plus/icons-vue'
import { useDocumentWorkflowStore } from '@/lib/stores/documentWorkflow'

const documentStore = useDocumentWorkflowStore()

type AuditActionType = 'CREATE' | 'READ' | 'SIGN' | 'DOWNLOAD' | 'ARCHIVE' | 'REVIEW'

interface AuditEntry {
  id: string
  timestamp: string
  actor: string
  actorRole: string
  action: AuditActionType
  description: string
  traceId: string
  ipAddress?: string
  userAgent?: string
  metadata?: Record<string, unknown>
}

interface Props {
  /** 自定义审计日志数据（可选，优先使用） */
  logs?: AuditEntry[]
}

const props = withDefaults(defineProps<Props>(), {
  logs: () => [],
})

const filterAction = ref('')
const filterTimeRange = ref('all')
const searchKeyword = ref('')
const hoveredEntry = ref('')
const expandedEntries = ref<string[]>([])
const hasMore = ref(false)

const allLogs = computed<AuditEntry[]>(() => {
  if (props.logs.length > 0) return props.logs

  const storeLogs = documentStore.auditLogs

  if (storeLogs && storeLogs.length > 0) {
    return storeLogs.map(log => ({
      ...log,
      action: log.action as AuditActionType,
    }))
  }

  return generateMockAuditLogs()
})

const filteredLogs = computed(() => {
  let result = [...allLogs.value]

  // 操作类型筛选
  if (filterAction.value) {
    result = result.filter(log => log.action === filterAction.value)
  }

  // 时间范围筛选
  if (filterTimeRange.value !== 'all') {
    const now = new Date()
    let startDate: Date

    switch (filterTimeRange.value) {
      case 'today':
        startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate())
        break
      case 'week':
        startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
        break
      case 'month':
        startDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000)
        break
      default:
        startDate = new Date(0)
    }

    result = result.filter(log => new Date(log.timestamp) >= startDate)
  }

  // 关键词搜索
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase().trim()
    result = result.filter(
      log =>
        log.actor.toLowerCase().includes(keyword) ||
        log.description.toLowerCase().includes(keyword) ||
        log.traceId.toLowerCase().includes(keyword)
    )
  }

  // 按时间倒序排列
  result.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())

  return result
})

const groupedLogs = computed(() => {
  const groups: Record<string, AuditEntry[]> = {}

  for (const log of filteredLogs.value) {
    const dateKey = new Date(log.timestamp).toDateString()
    if (!groups[dateKey]) {
      groups[dateKey] = []
    }
    groups[dateKey].push(log)
  }

  return groups
})

const actionStats = computed<Record<string, number>>(() => {
  const stats: Record<string, number> = {}

  for (const log of filteredLogs.value) {
    stats[log.action] = (stats[log.action] || 0) + 1
  }

  return stats
})

function generateMockAuditLogs(): AuditEntry[] {
  const now = new Date()
  const baseTraceId = `trace-${Date.now().toString(36)}`

  return [
    {
      id: `${baseTraceId}-1`,
      timestamp: new Date(now.getTime() - 5 * 60 * 1000).toISOString(),
      actor: '张三',
      actorRole: '原告代理人',
      action: 'SIGN',
      description: '完成电子签章签署，使用电子印章方式',
      traceId: `${baseTraceId}-sign001`,
      ipAddress: '192.168.1.100',
      metadata: {
        signatureMethod: 'DIGITAL_SEAL',
        signatureHash: 'a3f2...8c9d...',
      },
    },
    {
      id: `${baseTraceId}-2`,
      timestamp: new Date(now.getTime() - 15 * 60 * 1000).toISOString(),
      actor: '李四',
      actorRole: '书记员',
      action: 'DOWNLOAD',
      description: '下载了 PDF 版本文书，用于归档备份',
      traceId: `${baseTraceId}-dl001`,
      ipAddress: '192.168.1.101',
      metadata: {
        format: 'PDF',
        fileSize: '2.5MB',
      },
    },
    {
      id: `${baseTraceId}-3`,
      timestamp: new Date(now.getTime() - 30 * 60 * 1000).toISOString(),
      actor: '王五',
      actorRole: '法官',
      action: 'READ',
      description: '查看了文书内容，审阅时长约 12 分钟',
      traceId: `${baseTraceId}-read001`,
      ipAddress: '192.168.1.102',
      metadata: {
        viewDuration: '12m32s',
        pageViews: 15,
      },
    },
    {
      id: `${baseTraceId}-4`,
      timestamp: new Date(now.getTime() - 2 * 60 * 60 * 1000).toISOString(),
      actor: '系统',
      actorRole: 'SYSTEM',
      action: 'CREATE',
      description: '从模板「民事判决书」自动生成文书初稿',
      traceId: `${baseTraceId}-create001`,
      metadata: {
        templateId: 'TPL-CIVIL-JDG-001',
        aiGenerated: true,
        generationDuration: '3.2s',
      },
    },
    {
      id: `${baseTraceId}-5`,
      timestamp: new Date(now.getTime() - 24 * 60 * 60 * 1000).toISOString(),
      actor: '赵六',
      actorRole: '法官助理',
      action: 'REVIEW',
      description: '审核通过文书内容，提出修改意见已采纳',
      traceId: `${baseTraceId}-rev001`,
      ipAddress: '192.168.1.103',
      metadata: {
        reviewResult: 'APPROVED',
        comments: '格式规范，内容准确',
      },
    },
    {
      id: `${baseTraceId}-6`,
      timestamp: new Date(now.getTime() - 48 * 60 * 60 * 1000).toISOString(),
      actor: '系统',
      actorRole: 'SYSTEM',
      action: 'ARCHIVE',
      description: '文书已完成归档，生成归档编号',
      traceId: `${baseTraceId}-arch001`,
      metadata: {
        archiveNumber: 'ARCH-2024-01234',
        storageLocation: '区块链节点 #3',
      },
    },
    {
      id: `${baseTraceId}-7`,
      timestamp: new Date(now.getTime() - 72 * 60 * 60 * 1000).toISOString(),
      actor: '钱七',
      actorRole: '被告代理人',
      action: 'DOWNLOAD',
      description: '下载了完整证据包（ZIP格式）',
      traceId: `${baseTraceId}-dl002`,
      ipAddress: '10.0.0.55',
      metadata: {
        format: 'ZIP',
        fileSize: '15.8MB',
        includes: ['PDF', '签名凭证', '审计日志'],
      },
    },
    {
      id: `${baseTraceId}-8`,
      timestamp: new Date(now.getTime() - 96 * 60 * 60 * 1000).toISOString(),
      actor: '孙八',
      actorRole: '当事人',
      action: 'READ',
      description: '在线预览了文书内容',
      traceId: `${baseTraceId}-read002`,
      ipAddress: '10.0.0.56',
      metadata: {
        device: 'Mobile',
        platform: 'iOS',
      },
    },
    {
      id: `${baseTraceId}-9`,
      timestamp: new Date(now.getTime() - 120 * 60 * 60 * 1000).toISOString(),
      actor: '周九',
      actorRole: '管理员',
      action: 'CREATE',
      description: '创建了文书工作流程实例',
      traceId: `${baseTraceId}-create002`,
      ipAddress: '192.168.1.1',
      metadata: {
        workflowId: 'WF-2024-05678',
      },
    },
    {
      id: `${baseTraceId}-10`,
      timestamp: new Date(now.getTime() - 144 * 60 * 60 * 1000).toISOString(),
      actor: '吴十',
      actorRole: '法警',
      action: 'READ',
      description: '查看送达状态确认信息',
      traceId: `${baseTraceId}-read003`,
      ipAddress: '192.168.1.105',
    },
    {
      id: `${baseTraceId}-11`,
      timestamp: new Date(now.getTime() - 168 * 60 * 60 * 1000).toISOString(),
      actor: '郑十一',
      actorRole: '档案管理员',
      action: 'ARCHIVE',
      description: '执行物理归档流程，移交纸质版本文书',
      traceId: `${baseTraceId}-arch002`,
      ipAddress: '192.168.1.106',
      metadata: {
        physicalArchiveId: 'PHY-2024-A0789',
        location: 'A区3楼档案室',
      },
    },
    {
      id: `${baseTraceId}-12`,
      timestamp: new Date(now.getTime() - 192 * 60 * 60 * 1000).toISOString(),
      actor: '王十二',
      actorRole: '质检员',
      action: 'REVIEW',
      description: '质量检查通过，符合司法文书规范要求',
      traceId: `${baseTraceId}-rev002`,
      ipAddress: '192.168.1.107',
      metadata: {
        reviewResult: 'APPROVED',
        qualityScore: '98/100',
        checklistPassed: true,
      },
    },
  ]
}

function getActionColor(action: AuditActionType): string {
  const colors: Record<AuditActionType, string> = {
    CREATE: '#2563eb',
    READ: '#6b7280',
    SIGN: '#059669',
    DOWNLOAD: '#8b5cf6',
    ARCHIVE: '#d97706',
    REVIEW: '#0891b2',
  }
  return colors[action] || '#6b7280'
}

function getActionLabel(action: AuditActionType): string {
  const labels: Record<AuditActionType, string> = {
    CREATE: '创建',
    READ: '查看',
    SIGN: '签署',
    DOWNLOAD: '下载',
    ARCHIVE: '归档',
    REVIEW: '审核',
  }
  return labels[action] || action
}

function getActionIcon(action: AuditActionType) {
  const icons: Record<AuditActionType, any> = {
    CREATE: Document,
    READ: View,
    SIGN: EditPen,
    DOWNLOAD: Download,
    ARCHIVE: FolderOpened,
    REVIEW: CircleCheck,
  }
  return icons[action] || Document
}

function getActionTagType(action: AuditActionType): '' | 'success' | 'warning' | 'danger' | 'info' {
  const types: Record<AuditActionType, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    CREATE: '',
    READ: 'info',
    SIGN: 'success',
    DOWNLOAD: 'warning',
    ARCHIVE: 'warning',
    REVIEW: 'success',
  }
  return types[action] || 'info'
}

function getActionCardClass(action: AuditActionType): string {
  const classes: Record<AuditActionType, string> = {
    CREATE: 'border-blue-200 hover:border-blue-300',
    READ: 'border-gray-200 hover:border-gray-300',
    SIGN: 'border-green-200 hover:border-green-300',
    DOWNLOAD: 'border-purple-200 hover:border-purple-300',
    ARCHIVE: 'border-amber-200 hover:border-amber-300',
    REVIEW: 'border-cyan-200 hover:border-cyan-300',
  }
  return classes[action] || 'border-gray-200'
}

function getActionNodeClass(action: AuditActionType): string {
  return ''
}

function formatTime(isoString: string): string {
  const date = new Date(isoString)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${hours}:${minutes}:${seconds}`
}

function formatDateGroup(dateStr: string): string {
  const date = new Date(dateStr)
  const today = new Date()
  const yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000)

  if (date.toDateString() === today.toDateString()) {
    return '今天'
  }

  if (date.toDateString() === yesterday.toDateString()) {
    return '昨天'
  }

  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

function copyTraceId(traceId: string): void {
  navigator.clipboard.writeText(traceId).then(() => {
    ElMessage.success('TraceID 已复制到剪贴板')
  }).catch(() => {
    const textarea = document.createElement('textarea')
    textarea.value = traceId
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    ElMessage.success('TraceID 已复制到剪贴板')
  })
}

function loadMore(): void {
  hasMore.value = false
  ElMessage.info('暂无更多记录')
}
</script>

<style scoped>
.audit-log-panel {
  width: 100%;
}

.stats-summary {
  position: sticky;
  top: 0;
  z-index: 5;
}

.stat-item {
  display: flex;
  align-items: baseline;
  gap: 0.25rem;
}

.timeline-container {
  padding-bottom: 1rem;
}

.date-header {
  background: linear-gradient(to bottom, transparent 50%, white 50%);
}

.log-entry {
  animation: fade-in-up 0.3s ease-out forwards;
  opacity: 0;
}

.log-entry:nth-child(1) { animation-delay: 0ms; }
.log-entry:nth-child(2) { animation-delay: 50ms; }
.log-entry:nth-child(3) { animation-delay: 100ms; }
.log-entry:nth-child(4) { animation-delay: 150ms; }
.log-entry:nth-child(5) { animation-delay: 200ms; }

@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.log-card {
  transition: all 0.25s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.log-card:hover {
  transform: translateX(4px);
}

.trace-code {
  max-width: 280px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.trace-code:hover {
  white-space: normal;
  word-break: break-all;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.load-more {
  border-top: 1px dashed #e5e7eb;
  padding-top: 1rem;
}
</style>
