<template>
  <el-card shadow="hover" class="trial-records-panel">
    <template #header>
      <div class="flex items-center gap-2">
        <span class="text-lg">⚖️</span>
        <span class="font-semibold text-gray-800">庭审记录</span>
      </div>
    </template>

    <div v-if="!hasTrialStarted" class="trial-not-started">
      <el-empty description="庭审尚未开始" :image-size="80">
        <template #image>
          <div class="flex flex-col items-center">
            <el-icon :size="48" color="#c0c4cc"><Timer /></el-icon>
          </div>
        </template>
        <p class="text-sm text-gray-400 mt-2">
          当前案件处于「{{ currentStatusLabel }}」阶段，暂无庭审记录
        </p>
      </el-empty>
    </div>

    <div v-else class="trial-timeline">
      <el-timeline>
        <el-timeline-item
          v-for="record in trialRecords"
          :key="record.id"
          :timestamp="formatDateTime(record.date)"
          placement="top"
          :type="getTimelineType(record.status)"
          :hollow="record.status === 'pending'"
          size="large"
        >
          <div class="trial-record-card">
            <div class="trial-record-card__header">
              <div class="flex items-center gap-3">
                <span class="font-semibold text-gray-800">{{ record.courtRoom }}</span>
                <el-tag :type="getTrialStatusType(record.status)" size="small" effect="dark">
                  {{ getTrialStatusName(record.status) }}
                </el-tag>
              </div>
              <span class="text-sm text-gray-400">{{ record.duration }}</span>
            </div>

            <div class="trial-record-card__info">
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">审判长</span>
                  <span class="info-value">{{ record.judge }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">书记员</span>
                  <span class="info-value">{{ record.clerk }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">案件编号</span>
                  <span class="info-value font-mono text-xs">{{ record.caseNumber }}</span>
                </div>
              </div>
            </div>

            <div v-if="record.summary" class="trial-record-card__summary">
              <div class="summary-title">庭审要点</div>
              <p class="summary-content">{{ record.summary }}</p>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { Timer } from '@element-plus/icons-vue'
import { useCaseWorkflowStore } from '@/lib/stores/caseWorkflow'
import { formatDateTime } from '@/utils/formatter'
import { caseStatusNames } from '@/utils/status'

const workflowStore = useCaseWorkflowStore()

interface TrialRecord {
  id: string
  date: string
  courtRoom: string
  judge: string
  clerk: string
  caseNumber: string
  status: 'ongoing' | 'finished' | 'pending'
  duration: string
  summary?: string
}

function getTrialStatusType(status: TrialRecord['status']): '' | 'success' | 'warning' {
  const map = { ongoing: '', finished: 'success', pending: 'warning' }
  return map[status]
}

function getTrialStatusName(status: TrialRecord['status']): string {
  const map = { ongoing: '进行中', finished: '已结束', pending: '待开庭' }
  return map[status]
}

function getTimelineType(status: TrialRecord['status']): 'primary' | 'success' | 'warning' {
  const map = { ongoing: 'primary', finished: 'success', pending: 'warning' }
  return map[status]
}

const currentStatusLabel = computed(() => caseStatusNames[workflowStore.currentStatus] || workflowStore.currentStatus)

const trialPhaseStatuses = ['SCHEDULED', 'IN_TRIAL', 'JUDGED', 'EFFECTIVE', 'CLOSED']
const hasTrialStarted = computed(() => trialPhaseStatuses.includes(workflowStore.currentStatus))

const trialRecords = ref<TrialRecord[]>([
  {
    id: 'trial-001',
    date: '2024-02-05T09:00:00Z',
    courtRoom: '第三审判庭',
    judge: '张法官',
    clerk: '李书记员',
    caseNumber: '(2024) 浙01民初0001号',
    status: 'finished',
    duration: '约 2 小时 30 分',
    summary:
      '本次庭审主要围绕合同效力争议展开。原告主张合同因被告违约而解除，被告抗辩称原告未履行先履行义务。双方就违约金计算方式存在较大分歧，法庭要求双方于下次开庭前补充提交相关财务凭证。',
  },
  {
    id: 'trial-002',
    date: '2024-03-10T14:00:00Z',
    courtRoom: '第三审判庭',
    judge: '张法官',
    clerk: '李书记员',
    caseNumber: '(2024) 浙01民初0001号',
    status: 'pending',
    duration: '预计 2 小时',
    summary:
      '第二次庭审，将针对第一次庭审中双方争议的违约金计算方式进行质证和辩论。',
  },
])
</script>

<style scoped>
.trial-records-panel {
  width: 100%;
}

.trial-not-started {
  padding: 20px 0;
}

.trial-timeline {
  padding-left: 8px;
}

.trial-record-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.trial-record-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f2f5;
}

.trial-record-card__info {
  margin-bottom: 12px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.info-label {
  font-size: 12px;
  color: #909399;
}

.info-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.trial-record-card__summary {
  background: #fafbfc;
  border-radius: 6px;
  padding: 12px;
}

.summary-title {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 6px;
}

.summary-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.7;
  margin: 0;
}
</style>
