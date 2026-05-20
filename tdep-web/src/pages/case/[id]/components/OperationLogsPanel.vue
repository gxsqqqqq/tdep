<template>
  <el-card shadow="hover" class="operation-logs-panel">
    <template #header>
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="text-lg">📋</span>
          <span class="font-semibold text-gray-800">操作日志</span>
        </div>
        <el-button type="primary" link size="small" @click="handleViewAll">
          查看全部
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
    </template>

    <div class="logs-timeline">
      <div
        v-for="(log, index) in displayLogs"
        :key="log.id"
        class="log-item"
      >
        <div class="log-item__time">
          {{ formatLogTime(log.timestamp) }}
        </div>

        <div class="log-item__dot-wrapper">
          <div
            class="log-item__dot"
            :class="`log-item__dot--${getLogTypeColor(log.type)}`"
          />
          <div v-if="index < displayLogs.length - 1" class="log-item__line" />
        </div>

        <div class="log-item__content">
          <div class="log-item__header">
            <div class="log-item__actor">
              <el-avatar :size="22" :style="{ background: getRoleColor(log.role) }">
                {{ log.actor.charAt(0) }}
              </el-avatar>
              <span class="font-medium text-gray-700">{{ log.actor }}</span>
              <el-tag :type="getActionTagType(log.type)" size="small" effect="plain" round>
                {{ getActionTypeName(log.type) }}
              </el-tag>
            </div>
            <span v-if="log.ip" class="log-item__ip text-xs text-gray-300">{{ log.ip }}</span>
          </div>

          <p class="log-item__description">{{ log.description }}</p>
        </div>
      </div>

      <div v-if="displayLogs.length === 0" class="py-6">
        <el-empty description="暂无操作日志" :image-size="50" />
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'
import { useCaseWorkflowStore } from '@/lib/stores/caseWorkflow'

const workflowStore = useCaseWorkflowStore()

interface OperationLog {
  id: string
  timestamp: string
  actor: string
  role: 'judge' | 'clerk' | 'plaintiff' | 'defendant' | 'system'
  type: 'status_change' | 'file_upload' | 'document_sign' | 'evidence_verify' | 'trial_schedule' | 'payment' | 'other'
  description: string
  ip?: string
}

const DISPLAY_LIMIT = 10

function formatLogTime(timestamp: string): string {
  const date = new Date(timestamp)
  const now = new Date()
  const isToday = date.toDateString() === now.toDateString()

  const timeStr = date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  })

  if (isToday) return timeStr

  const dateStr = date.toLocaleDateString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
  })
  return `${dateStr} ${timeStr}`
}

function getLogTypeColor(type: OperationLog['type']): string {
  const map: Record<string, string> = {
    status_change: 'blue',
    file_upload: 'green',
    document_sign: 'purple',
    evidence_verify: 'cyan',
    trial_schedule: 'orange',
    payment: 'gold',
    other: 'gray',
  }
  return map[type] || 'gray'
}

function getActionTagType(type: OperationLog['type']): '' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<string, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    status_change: '',
    file_upload: 'success',
    document_sign: 'warning',
    evidence_verify: '',
    trial_schedule: 'danger',
    payment: 'success',
    other: 'info',
  }
  return map[type] || 'info'
}

function getActionTypeName(type: OperationLog['type']): string {
  const map: Record<string, string> = {
    status_change: '状态变更',
    file_upload: '上传文件',
    document_sign: '签署文书',
    evidence_verify: '证据校验',
    trial_schedule: '庭审排期',
    payment: '缴费操作',
    other: '其他操作',
  }
  return map[type] || '未知'
}

function getRoleColor(role: OperationLog['role']): string {
  const map: Record<string, string> = {
    judge: '#409eff',
    clerk: '#67c23a',
    plaintiff: '#e6a23c',
    defendant: '#f56c6c',
    system: '#909399',
  }
  return map[role] || '#909399'
}

const allLogs = ref<OperationLog[]>([
  {
    id: 'log-001',
    timestamp: '2024-01-15T14:30:00Z',
    actor: '张法官',
    role: 'judge',
    type: 'status_change',
    description: '将案件状态变更为「举证中」，双方当事人可开始提交证据材料',
    ip: '192.168.1.100',
  },
  {
    id: 'log-002',
    timestamp: '2024-01-15T10:20:00Z',
    actor: '李书记员',
    role: 'clerk',
    type: 'evidence_verify',
    description: '完成证据「采购合同原件.pdf」的区块链固化校验，校验结果：通过',
    ip: '192.168.1.101',
  },
  {
    id: 'log-003',
    timestamp: '2024-01-14T16:45:00Z',
    actor: '王律师（原告）',
    role: 'plaintiff',
    type: 'file_upload',
    description: '上传证据文件「现场视频录像.mp4」（大小: 50.0 MB）',
    ip: '10.0.0.55',
  },
  {
    id: 'log-004',
    timestamp: '2024-01-13T14:30:00Z',
    actor: '王律师（原告）',
    role: 'plaintiff',
    type: 'file_upload',
    description: '上传证据文件「往来邮件记录.xlsx」（大小: 125.0 KB）',
    ip: '10.0.0.55',
  },
  {
    id: 'log-005',
    timestamp: '2024-01-12T10:15:00Z',
    actor: '王律师（原告）',
    role: 'plaintiff',
    type: 'file_upload',
    description: '上传证据文件「付款凭证_转账记录.jpg」（大小: 1.5 MB），已自动完成哈希计算',
    ip: '10.0.0.55',
  },
  {
    id: 'log-006',
    timestamp: '2024-01-12T09:23:00Z',
    actor: '王律师（原告）',
    role: 'plaintiff',
    type: 'file_upload',
    description: '上传证据文件「采购合同原件.pdf」（大小: 2.3 MB）',
    ip: '10.0.0.55',
  },
  {
    id: 'log-007',
    timestamp: '2024-01-11T10:30:00Z',
    actor: '系统',
    role: 'system',
    type: 'status_change',
    description: '案件已自动提交至法官审核队列，等待立案审批',
  },
  {
    id: 'log-008',
    timestamp: '2024-01-10T09:15:00Z',
    actor: '王律师（原告）',
    role: 'plaintiff',
    type: 'status_change',
    description: '创建案件并提交立案申请，案由：合同纠纷，诉讼请求金额: ¥500,000.00',
    ip: '10.0.0.55',
  },
  {
    id: 'log-009',
    timestamp: '2024-01-10T09:00:00Z',
    actor: '系统',
    role: 'system',
    type: 'other',
    description: '案件系统自动生成案件编号：(2024) 浙01民初0001号',
  },
  {
    id: 'log-010',
    timestamp: '2024-01-09T16:00:00Z',
    actor: '李书记员',
    role: 'clerk',
    type: 'document_sign',
    description: '发送电子送达回执通知至被告方，等待签收确认',
    ip: '192.168.1.101',
  },
])

const displayLogs = computed(() => allLogs.value.slice(0, DISPLAY_LIMIT))

function handleViewAll() {
  ElMessage.info(`共 ${allLogs.value.length} 条操作记录，正在加载完整日志...`)
}
</script>

<style scoped>
.operation-logs-panel {
  width: 100%;
}

.logs-timeline {
  display: flex;
  flex-direction: column;
}

.log-item {
  display: flex;
  gap: 12px;
  position: relative;
}

.log-item:last-child .log-item__line {
  display: none;
}

.log-item__time {
  width: 52px;
  flex-shrink: 0;
  font-size: 11px;
  color: #909399;
  text-align: right;
  padding-top: 3px;
  line-height: 1.4;
}

.log-item__dot-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  padding-top: 7px;
}

.log-item__dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
  z-index: 1;
}

.log-item__dot--blue { background-color: #409eff; box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.15); }
.log-item__dot--green { background-color: #67c23a; box-shadow: 0 0 0 3px rgba(103, 194, 58, 0.15); }
.log-item__dot--purple { background-color: #909399; box-shadow: 0 0 0 3px rgba(144, 147, 153, 0.15); }
.log-item__dot--cyan { background-color: #36cfc9; box-shadow: 0 0 0 3px rgba(54, 207, 201, 0.15); }
.log-item__dot--orange { background-color: #fa8c16; box-shadow: 0 0 0 3px rgba(250, 140, 22, 0.15); }
.log-item__dot--gold { background-color: #faad14; box-shadow: 0 0 0 3px rgba(250, 173, 20, 0.15); }
.log-item__dot--gray { background-color: #c0c4cc; box-shadow: 0 0 0 3px rgba(192, 196, 204, 0.15); }

.log-item__line {
  width: 2px;
  flex: 1;
  min-height: 16px;
  background: linear-gradient(to bottom, #dcdfe6, transparent);
  margin-top: 2px;
}

.log-item__content {
  flex: 1;
  padding-bottom: 16px;
  min-width: 0;
}

.log-item__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}

.log-item__actor {
  display: flex;
  align-items: center;
  gap: 6px;
}

.log-item__actor .font-medium {
  font-size: 13px;
}

.log-item__ip {
  flex-shrink: 0;
  font-family: monospace;
}

.log-item__description {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
  word-break: break-all;
}
</style>
