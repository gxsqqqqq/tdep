<template>
  <div class="party-workspace space-y-6">
    <div class="bg-gradient-to-r from-blue-600 to-blue-700 rounded-2xl p-8 text-white shadow-lg">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-3xl font-bold mb-2">当事人工作台</h2>
          <p class="text-blue-100 text-lg">管理您的案件、证据和文书</p>
        </div>
        <el-icon :size="64" class="opacity-20">
          <UserFilled />
        </el-icon>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <el-card shadow="hover" class="cursor-pointer transition-all hover:scale-105 hover:shadow-xl" @click="$router.push('/case/create')">
        <div class="text-center p-4">
          <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <el-icon :size="32" class="text-blue-600">
              <DocumentAdd />
            </el-icon>
          </div>
          <h3 class="font-semibold text-gray-800 mb-1">新建案件</h3>
          <p class="text-sm text-gray-500">提交新的诉讼请求</p>
        </div>
      </el-card>

      <el-card shadow="hover" class="cursor-pointer transition-all hover:scale-105 hover:shadow-xl">
        <div class="text-center p-4">
          <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <el-icon :size="32" class="text-green-600">
              <Upload />
            </el-icon>
          </div>
          <h3 class="font-semibold text-gray-800 mb-1">上传证据</h3>
          <p class="text-sm text-gray-500">提交电子证据材料</p>
        </div>
      </el-card>

      <el-card shadow="hover" class="cursor-pointer transition-all hover:scale-105 hover:shadow-xl" @click="$router.push('/workspace/party/documents')">
        <div class="text-center p-4">
          <div class="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <el-icon :size="32" class="text-purple-600">
              <Files />
            </el-icon>
          </div>
          <h3 class="font-semibold text-gray-800 mb-1">我的文书</h3>
          <p class="text-sm text-gray-500">查看法律文书</p>
        </div>
      </el-card>

      <el-card shadow="hover" class="cursor-pointer transition-all hover:scale-105 hover:shadow-xl">
        <div class="text-center p-4">
          <div class="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <el-icon :size="32" class="text-orange-600">
              <EditPen />
            </el-icon>
          </div>
          <h3 class="font-semibold text-gray-800 mb-1">在线签字</h3>
          <p class="text-sm text-gray-500">电子签名确认</p>
        </div>
      </el-card>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <el-card class="shadow-md">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="text-lg font-semibold text-gray-800">📋 我的案件</span>
            <el-button type="primary" link @click="$router.push('/workspace/party/cases')">
              查看全部 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div v-if="myCases.length === 0" class="text-center py-12 text-gray-400">
          <el-icon :size="48" class="mb-3"><FolderOpened /></el-icon>
          <p>暂无案件</p>
          <el-button type="primary" class="mt-4" @click="$router.push('/case/create')">
            创建第一个案件
          </el-button>
        </div>
        <div v-else class="space-y-3">
          <div v-for="caseItem in myCases" :key="caseItem.id" 
               class="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-blue-50 cursor-pointer transition-colors"
               @click="$router.push(`/case/${caseItem.id}`)">
            <div class="flex-1">
              <h4 class="font-medium text-gray-800">{{ caseItem.title }}</h4>
              <p class="text-sm text-gray-500 mt-1">案件号: {{ caseItem.caseNumber }}</p>
            </div>
            <el-tag :type="getStatusType(caseItem.status)" size="small">
              {{ getStatusText(caseItem.status) }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <el-card class="shadow-md">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="text-lg font-semibold text-gray-800">⏰ 待办事项</span>
            <el-badge :value="pendingTasks.length" :max="99" class="mr-2" />
          </div>
        </template>
        <div v-if="pendingTasks.length === 0" class="text-center py-12 text-gray-400">
          <el-icon :size="48" class="mb-3"><CircleCheck /></el-icon>
          <p>暂无待办事项</p>
        </div>
        <el-timeline v-else>
          <el-timeline-item v-for="task in pendingTasks" :key="task.id"
                           :timestamp="task.deadline"
                           placement="top"
                           :type="getTaskType(task.priority)"
                           size="large">
            <div class="flex items-start gap-3">
              <el-icon class="mt-1" :class="getTaskIconClass(task.priority)"><Bell /></el-icon>
              <div class="flex-1">
                <h4 class="font-medium text-gray-800">{{ task.title }}</h4>
                <p class="text-sm text-gray-500 mt-1">{{ task.description }}</p>
                <el-button type="primary" link size="small" class="mt-2">
                  立即处理 <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>

    <el-card class="shadow-md">
      <template #header>
        <span class="text-lg font-semibold text-gray-800">📂 最近动态</span>
      </template>
      <el-timeline>
        <el-timeline-item v-for="activity in recentActivities" :key="activity.id"
                         :timestamp="activity.time"
                         placement="top">
          <div class="bg-gray-50 p-4 rounded-lg">
            <div class="flex items-start gap-3">
              <div :class="['w-10 h-10 rounded-full flex items-center justify-center text-white font-semibold', activity.bgColor]">
                {{ activity.icon }}
              </div>
              <div class="flex-1">
                <h4 class="font-medium text-gray-800">{{ activity.title }}</h4>
                <p class="text-sm text-gray-600 mt-1">{{ activity.description }}</p>
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
import { UserFilled, DocumentAdd, Upload, Files, EditPen, ArrowRight, FolderOpened, CircleCheck, Bell } from '@element-plus/icons-vue'

const myCases = ref([
  { id: '1', title: '合同纠纷案', caseNumber: '(2024) 浙01民初123号', status: 'PENDING_REVIEW' },
  { id: '2', title: '知识产权侵权案', caseNumber: '(2024) 浙01民初456号', status: 'EVIDENCE_CLOSING' },
])

const pendingTasks = ref([
  {
    id: '1',
    title: '补充证据材料',
    description: '请在3个工作日内补充提交合同原件扫描件',
    deadline: '2024-01-20',
    priority: 'high',
  },
  {
    id: '2',
    title: '确认庭审时间',
    description: '请确认是否可以参加2024年1月25日的庭审',
    deadline: '2024-01-18',
    priority: 'medium',
  },
])

const recentActivities = ref([
  {
    id: '1',
    icon: '📄',
    bgColor: 'bg-blue-500',
    title: '案件已受理',
    description: '您的"合同纠纷案"已通过审核，进入证据收集阶段',
    time: '2024-01-15 14:30',
  },
  {
    id: '2',
    icon: '✅',
    bgColor: 'bg-green-500',
    title: '证据已上传',
    description: '成功上传3份证据材料至"知识产权侵权案"',
    time: '2024-01-15 11:20',
  },
  {
    id: '3',
    icon: '📝',
    bgColor: 'bg-purple-500',
    title: '文书已生成',
    description: '系统已自动生成起诉状草案，请查阅并确认',
    time: '2024-01-14 16:45',
  },
])

function getStatusType(status: string): '' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<string, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    DRAFT: 'info',
    PENDING_REVIEW: 'warning',
    ACCEPTED: 'success',
    EVIDENCE_CLOSING: '',
    TRIAL_SCHEDULED: '',
    IN_TRIAL: 'warning',
    JUDGED: 'success',
    CLOSED: 'info',
  }
  return map[status] || 'info'
}

function getStatusText(status: string): string {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    PENDING_REVIEW: '待审核',
    ACCEPTED: '已受理',
    EVIDENCE_CLOSING: '举证中',
    TRIAL_SCHEDULED: '已排期',
    IN_TRIAL: '审理中',
    JUDGED: '已判决',
    CLOSED: '已结案',
  }
  return map[status] || status
}

function getTaskType(priority: string): 'primary' | 'success' | 'warning' | 'danger' {
  const map: Record<string, 'primary' | 'success' | 'warning' | 'danger'> = {
    high: 'danger',
    medium: 'warning',
    low: 'primary',
  }
  return map[priority] || 'primary'
}

function getTaskIconClass(priority: string): string {
  const map: Record<string, string> = {
    high: 'text-red-500',
    medium: 'text-orange-500',
    low: 'text-blue-500',
  }
  return map[priority] || 'text-gray-500'
}
</script>

<style scoped>
.party-workspace {
  padding: 24px;
}
</style>
