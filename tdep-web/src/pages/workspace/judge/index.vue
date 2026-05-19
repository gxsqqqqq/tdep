<template>
  <div class="judge-workspace space-y-6">
    <div class="bg-gradient-to-r from-purple-600 to-violet-700 rounded-2xl p-8 text-white shadow-lg">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-3xl font-bold mb-2">法官工作台</h2>
          <p class="text-purple-100 text-lg">审理案件、排期庭审、撰写文书</p>
        </div>
        <el-icon :size="64" class="opacity-20">
          <Stamp />
        </el-icon>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <el-card shadow="hover" class="cursor-pointer transition-all hover:scale-105 hover:shadow-xl" @click="$router.push('/workspace/judge/cases')">
        <div class="text-center p-4">
          <div class="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-3 relative">
            <el-icon :size="32" class="text-red-600">
              <Document />
            </el-icon>
            <span class="absolute -top-1 -right-1 bg-red-500 text-white text-xs w-5 h-5 rounded-full flex items-center justify-center font-bold">{{ pendingCases.length }}</span>
          </div>
          <h3 class="font-semibold text-gray-800 mb-1">待审案件</h3>
          <p class="text-sm text-gray-500">等待审理的案件</p>
        </div>
      </el-card>

      <el-card shadow="hover" class="cursor-pointer transition-all hover:scale-105 hover:shadow-xl" @click="$router.push('/workspace/judge/schedule')">
        <div class="text-center p-4">
          <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-3 relative">
            <el-icon :size="32" class="text-blue-600">
              <Calendar />
            </el-icon>
            <span class="absolute -top-1 -right-1 bg-blue-500 text-white text-xs w-5 h-5 rounded-full flex items-center justify-center font-bold">{{ todayTrials.length }}</span>
          </div>
          <h3 class="font-semibold text-gray-800 mb-1">庭审排期</h3>
          <p class="text-sm text-gray-500">管理庭审日程安排</p>
        </div>
      </el-card>

      <el-card shadow="hover" class="cursor-pointer transition-all hover:scale-105 hover:shadow-xl" @click="$router.push('/workspace/judge/documents')">
        <div class="text-center p-4">
          <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <el-icon :size="32" class="text-green-600">
              <EditPen />
            </el-icon>
          </div>
          <h3 class="font-semibold text-gray-800 mb-1">文书撰写</h3>
          <p class="text-sm text-gray-500">起草裁判文书</p>
        </div>
      </el-card>

      <el-card shadow="hover" class="cursor-pointer transition-all hover:scale-105 hover:shadow-xl">
        <div class="text-center p-4">
          <div class="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <el-icon :size="32" class="text-purple-600">
              <MagicStick />
            </el-icon>
          </div>
          <h3 class="font-semibold text-gray-800 mb-1">AI 辅助</h3>
          <p class="text-sm text-gray-500">智能辅助审判</p>
        </div>
      </el-card>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <el-card class="shadow-md">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="text-lg font-semibold text-gray-800">📋 待审案件</span>
            <el-button type="primary" link @click="$router.push('/workspace/judge/cases')">
              查看全部 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div v-if="pendingCases.length === 0" class="text-center py-12 text-gray-400">
          <el-icon :size="48" class="mb-3"><FolderOpened /></el-icon>
          <p>暂无待审案件</p>
        </div>
        <div v-else class="space-y-3">
          <div v-for="caseItem in pendingCases" :key="caseItem.id"
               class="flex items-start gap-4 p-4 bg-gray-50 rounded-lg hover:bg-purple-50 cursor-pointer transition-colors"
               @click="$router.push(`/case/${caseItem.id}`)">
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-1">
                <el-tag :type="getPriorityType(caseItem.priority)" size="small" effect="dark">
                  {{ getPriorityText(caseItem.priority) }}
                </el-tag>
                <h4 class="font-medium text-gray-800 truncate">{{ caseItem.title }}</h4>
              </div>
              <p class="text-sm text-gray-500 mt-1">案件号: {{ caseItem.caseNumber }}</p>
              <div class="flex items-center gap-4 mt-2 text-xs text-gray-400">
                <span>原告: {{ caseItem.plaintiff }}</span>
                <span>被告: {{ caseItem.defendant }}</span>
              </div>
            </div>
            <div class="text-right shrink-0">
              <p class="text-xs text-gray-400">{{ caseItem.submitTime }}</p>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="shadow-md">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="text-lg font-semibold text-gray-800">⏰ 今日庭审安排</span>
            <el-button type="primary" link @click="$router.push('/workspace/judge/schedule')">
              查看排期 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div v-if="todayTrials.length === 0" class="text-center py-12 text-gray-400">
          <el-icon :size="48" class="mb-3"><Calendar /></el-icon>
          <p>今日暂无庭审安排</p>
        </div>
        <el-timeline v-else>
          <el-timeline-item v-for="trial in todayTrials" :key="trial.id"
                           :timestamp="trial.time"
                           placement="top"
                           :type="getTrialStatusType(trial.status)"
                           size="large"
                           :hollow="trial.status === 'upcoming'">
            <div class="bg-gray-50 p-4 rounded-lg hover:shadow-md transition-shadow">
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <div class="flex items-center gap-2 mb-1">
                    <el-tag type="primary" size="small" effect="plain">
                      <el-icon class="mr-1"><OfficeBuilding /></el-icon>
                      {{ trial.courtRoom }}
                    </el-tag>
                    <el-tag v-if="trial.status === 'ongoing'" type="danger" size="small" effect="dark" class="animate-pulse">
                      进行中
                    </el-tag>
                  </div>
                  <h4 class="font-medium text-gray-800 mt-2">{{ trial.caseName }}</h4>
                  <p class="text-sm text-gray-500 mt-1">审判长: {{ trial.judge }} | 书记员: {{ trial.clerk }}</p>
                </div>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>

    <el-card class="shadow-md">
      <template #header>
        <span class="text-lg font-semibold text-gray-800">⚖️ 最近判决动态</span>
      </template>
      <el-timeline>
        <el-timeline-item v-for="judgment in recentJudgments" :key="judgment.id"
                         :timestamp="judgment.time"
                         placement="top"
                         :type="getJudgmentType(judgment.result)"
                         size="large">
          <div class="bg-gray-50 p-4 rounded-lg">
            <div class="flex items-start gap-3">
              <div :class="['w-10 h-10 rounded-full flex items-center justify-center text-white font-semibold', judgment.bgColor]">
                {{ judgment.icon }}
              </div>
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-1">
                  <h4 class="font-medium text-gray-800">{{ judgment.title }}</h4>
                  <el-tag :type="getJudgmentTagType(judgment.result)" size="small">
                    {{ getJudgmentResultText(judgment.result) }}
                  </el-tag>
                </div>
                <p class="text-sm text-gray-600 mt-1">{{ judgment.description }}</p>
                <p class="text-xs text-gray-400 mt-2">案号: {{ judgment.caseNumber }}</p>
              </div>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Stamp, Document, Calendar, EditPen, MagicStick, ArrowRight, FolderOpened, OfficeBuilding } from '@element-plus/icons-vue'

interface PendingCase {
  id: string
  title: string
  caseNumber: string
  priority: 'urgent' | 'high' | 'normal' | 'low'
  plaintiff: string
  defendant: string
  submitTime: string
}

interface TodayTrial {
  id: string
  time: string
  courtRoom: string
  caseName: string
  judge: string
  clerk: string
  status: 'completed' | 'ongoing' | 'upcoming'
}

interface RecentJudgment {
  id: string
  icon: string
  bgColor: string
  title: string
  description: string
  caseNumber: string
  time: string
  result: 'win' | 'partial' | 'loss' | 'settled'
}

const pendingCases = ref<PendingCase[]>([
  {
    id: '1',
    title: '张某诉李某民间借贷纠纷案',
    caseNumber: '(2024) 浙01民初888号',
    priority: 'urgent',
    plaintiff: '张某',
    defendant: '李某',
    submitTime: '2024-01-18',
  },
  {
    id: '2',
    title: '某某科技有限公司诉王某商业秘密侵权案',
    caseNumber: '(2024) 浙01民初777号',
    priority: 'high',
    plaintiff: '某某科技有限公司',
    defendant: '王某',
    submitTime: '2024-01-17',
  },
  {
    id: '3',
    title: '陈某与周某离婚纠纷案',
    caseNumber: '(2024) 浙01民初666号',
    priority: 'normal',
    plaintiff: '陈某',
    defendant: '周某',
    submitTime: '2024-01-15',
  },
  {
    id: '4',
    title: '赵某诉某建筑工程公司建设工程合同纠纷案',
    caseNumber: '(2024) 浙01民初555号',
    priority: 'low',
    plaintiff: '赵某',
    defendant: '某建筑工程公司',
    submitTime: '2024-01-12',
  },
])

const todayTrials = ref<TodayTrial[]>([
  {
    id: '1',
    time: '09:00',
    courtRoom: '第一审判庭',
    caseName: '张某诉李某民间借贷纠纷案',
    judge: '王法官',
    clerk: '李书记员',
    status: 'completed',
  },
  {
    id: '2',
    time: '10:30',
    courtRoom: '第三审判庭',
    caseName: '某某科技有限公司诉王某商业秘密侵权案',
    judge: '王法官',
    clerk: '张书记员',
    status: 'ongoing',
  },
  {
    id: '3',
    time: '14:00',
    courtRoom: '第二审判庭',
    caseName: '陈某与周某离婚纠纷案',
    judge: '王法官',
    clerk: '刘书记员',
    status: 'upcoming',
  },
])

const recentJudgments = ref<RecentJudgment[]>([
  {
    id: '1',
    icon: '⚖️',
    bgColor: 'bg-green-500',
    title: '判决已送达',
    description: '"刘某诉某物业公司物业服务合同纠纷案"一审判决：支持原告诉讼请求，被告需退还物业费并赔偿损失',
    caseNumber: '(2024) 浙01民初333号',
    time: '2024-01-18 16:30',
    result: 'win',
  },
  {
    id: '2',
    icon: '🤝',
    bgColor: 'bg-blue-500',
    title: '调解结案',
    description: '"吴某诉郑某相邻关系纠纷案"经法庭调解，双方达成和解协议，当场履行完毕',
    caseNumber: '(2024) 浙01民初222号',
    time: '2024-01-17 14:20',
    result: 'settled',
  },
  {
    id: '3',
    icon: '📋',
    bgColor: 'bg-orange-500',
    title: '部分支持',
    description: '"孙某诉某保险公司保险合同纠纷案"一审判决：部分支持原告诉请，驳回其他诉讼请求',
    caseNumber: '(2024) 浙01民初111号',
    time: '2024-01-16 11:00',
    result: 'partial',
  },
  {
    id: '4',
    icon: '❌',
    bgColor: 'bg-red-500',
    title: '驳回起诉',
    description: '"钱某诉某市政府行政诉讼案"裁定：原告主体不适格，依法驳回起诉',
    caseNumber: '(2024) 浙01行初001号',
    time: '2024-01-15 09:45',
    result: 'loss',
  },
])

function getPriorityType(priority: string): '' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<string, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    urgent: 'danger',
    high: 'warning',
    normal: '',
    low: 'info',
  }
  return map[priority] || 'info'
}

function getPriorityText(priority: string): string {
  const map: Record<string, string> = {
    urgent: '加急',
    high: '高优先',
    normal: '普通',
    low: '低优先',
  }
  return map[priority] || priority
}

function getTrialStatusType(status: string): 'primary' | 'success' | 'warning' | 'danger' {
  const map: Record<string, 'primary' | 'success' | 'warning' | 'danger'> = {
    completed: 'success',
    ongoing: 'danger',
    upcoming: 'primary',
  }
  return map[status] || 'primary'
}

function getJudgmentType(result: string): 'primary' | 'success' | 'warning' | 'danger' {
  const map: Record<string, 'primary' | 'success' | 'warning' | 'danger'> = {
    win: 'success',
    partial: 'warning',
    loss: 'danger',
    settled: 'primary',
  }
  return map[result] || 'primary'
}

function getJudgmentTagType(result: string): '' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<string, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    win: 'success',
    partial: 'warning',
    loss: 'danger',
    settled: 'primary',
  }
  return map[result] || 'info'
}

function getJudgmentResultText(result: string): string {
  const map: Record<string, string> = {
    win: '胜诉',
    partial: '部分支持',
    loss: '败诉/驳回',
    settled: '调解',
  }
  return map[result] || result
}
</script>

<style scoped>
.judge-workspace {
  padding: 24px;
}
</style>
