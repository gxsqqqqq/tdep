<template>
  <div class="clerk-workspace space-y-6">
    <div class="bg-gradient-to-r from-emerald-600 to-green-600 rounded-2xl p-8 text-white shadow-lg">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-3xl font-bold mb-2">书记员工作台</h2>
          <p class="text-emerald-100 text-lg">庭审记录、证据整理、文书归档</p>
        </div>
        <el-icon :size="64" class="opacity-20">
          <EditPen />
        </el-icon>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <el-card shadow="hover" class="cursor-pointer transition-all hover:scale-105 hover:shadow-xl">
        <div class="text-center p-4">
          <div class="w-16 h-16 bg-emerald-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <el-icon :size="32" class="text-emerald-600">
              <Notebook />
            </el-icon>
          </div>
          <h3 class="font-semibold text-gray-800 mb-1">庭审记录</h3>
          <p class="text-sm text-gray-500">录入庭审笔录信息</p>
        </div>
      </el-card>

      <el-card shadow="hover" class="cursor-pointer transition-all hover:scale-105 hover:shadow-xl">
        <div class="text-center p-4">
          <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <el-icon :size="32" class="text-blue-600">
              <FolderOpened />
            </el-icon>
          </div>
          <h3 class="font-semibold text-gray-800 mb-1">证据整理</h3>
          <p class="text-sm text-gray-500">分类归档证据材料</p>
        </div>
      </el-card>

      <el-card shadow="hover" class="cursor-pointer transition-all hover:scale-105 hover:shadow-xl">
        <div class="text-center p-4">
          <div class="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <el-icon :size="32" class="text-purple-600">
              <Document />
            </el-icon>
          </div>
          <h3 class="font-semibold text-gray-800 mb-1">文书归档</h3>
          <p class="text-sm text-gray-500">整理法律文书档案</p>
        </div>
      </el-card>

      <el-card shadow="hover" class="cursor-pointer transition-all hover:scale-105 hover:shadow-xl">
        <div class="text-center p-4">
          <div class="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <el-icon :size="32" class="text-orange-600">
              <DataAnalysis />
            </el-icon>
          </div>
          <h3 class="font-semibold text-gray-800 mb-1">数据统计</h3>
          <p class="text-sm text-gray-500">查看工作数据报表</p>
        </div>
      </el-card>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <el-card class="shadow-md">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="text-lg font-semibold text-gray-800">📋 待记录庭审</span>
            <el-button type="primary" link>
              查看全部 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div class="space-y-3">
          <div v-for="trial in pendingTrials" :key="trial.id"
               class="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-emerald-50 cursor-pointer transition-colors">
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-3 mb-1">
                <span class="text-xs text-gray-400">{{ trial.date }}</span>
                <span class="text-xs text-gray-400">{{ trial.time }}</span>
              </div>
              <h4 class="font-medium text-gray-800 truncate">{{ trial.caseName }}</h4>
              <p class="text-sm text-gray-500 mt-1">{{ trial.courtroom }}</p>
            </div>
            <el-tag :type="getTrialStatusType(trial.status)" size="small" class="ml-3 shrink-0">
              {{ getTrialStatusText(trial.status) }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <el-card class="shadow-md">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="text-lg font-semibold text-gray-800">📁 证据整理任务</span>
            <el-button type="primary" link>
              查看全部 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div class="space-y-4">
          <div v-for="task in evidenceTasks" :key="task.id" class="p-4 bg-gray-50 rounded-lg">
            <div class="flex items-start justify-between mb-2">
              <div class="flex-1 min-w-0">
                <h4 class="font-medium text-gray-800">{{ task.title }}</h4>
                <p class="text-sm text-gray-500 mt-0.5">负责人：{{ task.assignee }}</p>
              </div>
              <el-tag :type="task.progress >= 100 ? 'success' : 'warning'" size="small" class="shrink-0 ml-3">
                {{ task.progress >= 100 ? '已完成' : '进行中' }}
              </el-tag>
            </div>
            <el-progress :percentage="task.progress" :stroke-width="8"
                         :color="task.progress >= 100 ? '#10b981' : undefined"
                         class="mb-2" />
            <div class="flex items-center justify-between text-xs text-gray-400">
              <span>截止日期：{{ task.deadline }}</span>
              <span>{{ task.completed }}/{{ task.total }} 项</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <el-card class="shadow-md">
      <template #header>
        <span class="text-lg font-semibold text-gray-800">📊 本周工作统计</span>
      </template>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div class="bg-gradient-to-br from-emerald-50 to-emerald-100 rounded-xl p-5 text-center">
          <div class="text-3xl font-bold text-emerald-600 mb-1">{{ weekStats.recorded }}</div>
          <div class="text-sm text-gray-600">已记录</div>
        </div>
        <div class="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl p-5 text-center">
          <div class="text-3xl font-bold text-blue-600 mb-1">{{ weekStats.pendingOrganize }}</div>
          <div class="text-sm text-gray-600">待整理</div>
        </div>
        <div class="bg-gradient-to-br from-purple-50 to-purple-100 rounded-xl p-5 text-center">
          <div class="text-3xl font-bold text-purple-600 mb-1">{{ weekStats.completed }}</div>
          <div class="text-sm text-gray-600">已完成</div>
        </div>
        <div class="bg-gradient-to-br from-orange-50 to-orange-100 rounded-xl p-5 text-center">
          <div class="text-3xl font-bold text-orange-600 mb-1">{{ weekStats.inProgress }}</div>
          <div class="text-sm text-gray-600">进行中</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { EditPen, Notebook, FolderOpened, Document, DataAnalysis, ArrowRight } from '@element-plus/icons-vue'

interface Trial {
  id: string
  date: string
  time: string
  caseName: string
  courtroom: string
  status: string
}

interface EvidenceTask {
  id: string
  title: string
  assignee: string
  progress: number
  deadline: string
  completed: number
  total: number
}

const pendingTrials = ref<Trial[]>([
  { id: '1', date: '2026-05-19', time: '09:30', caseName: '张某诉李某合同纠纷案', courtroom: '第一审判庭', status: 'UPCOMING' },
  { id: '2', date: '2026-05-19', time: '14:00', caseName: '王某知识产权侵权案', courtroom: '第二审判庭', status: 'UPCOMING' },
  { id: '3', date: '2026-05-20', time: '09:00', caseName: '赵某劳动争议案', courtroom: '第三审判庭', status: 'SCHEDULED' },
  { id: '4', date: '2026-05-21', time: '10:30', caseName: '陈某离婚纠纷案', courtroom: '第一审判庭', status: 'SCHEDULED' },
  { id: '5', date: '2026-05-22', time: '15:00', caseName: '刘某交通事故赔偿案', courtroom: '第四审判庭', status: 'PENDING' },
])

const evidenceTasks = ref<EvidenceTask[]>([
  { id: '1', title: '张某诉李某案 - 合同证据组', assignee: '王书记员', progress: 75, deadline: '2026-05-21', completed: 12, total: 16 },
  { id: '2', title: '王某知识产权案 - 侵权证据链', assignee: '李书记员', progress: 40, deadline: '2026-05-23', completed: 6, total: 15 },
  { id: '3', title: '赵某劳动争议 - 工资流水证据', assignee: '张书记员', progress: 100, deadline: '2026-05-18', completed: 8, total: 8 },
  { id: '4', title: '陈某离婚纠纷 - 财产分割证据', assignee: '王书记员', progress: 25, deadline: '2026-05-25', completed: 3, total: 12 },
])

const weekStats = ref({
  recorded: 23,
  pendingOrganize: 8,
  completed: 45,
  inProgress: 12,
})

function getTrialStatusType(status: string): '' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<string, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    UPCOMING: 'danger',
    SCHEDULED: 'warning',
    PENDING: 'info',
    ONGOING: '',
    COMPLETED: 'success',
  }
  return map[status] || 'info'
}

function getTrialStatusText(status: string): string {
  const map: Record<string, string> = {
    UPCOMING: '即将开始',
    SCHEDULED: '已排期',
    PENDING: '待确认',
    ONGOING: '进行中',
    COMPLETED: '已完成',
  }
  return map[status] || status
}
</script>

<style scoped>
.clerk-workspace {
  padding: 24px;
}
</style>
