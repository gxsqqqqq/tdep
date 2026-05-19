<template>
  <div class="space-y-6">
    <!-- 欢迎横幅 -->
    <div class="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-2xl p-8 text-white shadow-lg">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold mb-2">
            {{ greeting }}，{{ authStore.user?.nickname || '用户' }}
          </h1>
          <p class="text-blue-100 text-lg">
            欢迎回到 TDEP 可信数字证据平台
          </p>
        </div>
        <div class="hidden md:block">
          <img 
            src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=digital%20court%20platform%20dashboard%20interface%20with%20blue%20gradient%20background%20modern%20UI&image_size=landscape_16_9" 
            alt="工作台" 
            class="w-64 h-40 object-contain opacity-90"
          />
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <button
        v-for="action in quickActions"
        :key="action.label"
        @click="$router.push(action.path)"
        class="group bg-white rounded-xl p-6 shadow-card hover:shadow-lg transition-all duration-200 border border-gray-100 hover:border-primary-200 hover:-translate-y-0.5"
      >
        <div class="w-12 h-12 rounded-xl flex items-center justify-center mb-4" :class="action.bgColor">
          <component :is="action.icon" :size="24" :class="action.iconColor" />
        </div>
        <h3 class="text-lg font-semibold text-gray-800 group-hover:text-primary-600 transition-colors">
          {{ action.label }}
        </h3>
        <p class="text-sm text-gray-500 mt-1">{{ action.description }}</p>
      </button>
    </div>

    <!-- 数据概览卡片 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-6">
      <!-- 待处理事项 -->
      <div class="bg-white rounded-xl p-6 shadow-card border border-gray-100">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-800">待我处理</h3>
          <span class="px-3 py-1 rounded-full text-xs font-medium bg-warning-100 text-warning-600">
            {{ pendingCount }} 项
          </span>
        </div>
        
        <div class="space-y-3">
          <div 
            v-for="item in pendingItems" 
            :key="item.id"
            class="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
          >
            <div class="flex items-center space-x-3">
              <div class="w-2 h-2 rounded-full" :class="item.dotColor"></div>
              <span class="text-sm font-medium text-gray-700">{{ item.title }}</span>
            </div>
            <span class="text-xs text-gray-400">{{ item.time }}</span>
          </div>

          <div v-if="pendingCount === 0" class="text-center py-8 text-gray-400">
            <el-icon :size="48" color="#d1d5db"><CircleCheck /></el-icon>
            <p class="mt-2 text-sm">暂无待办事项</p>
          </div>
        </div>
      </div>

      <!-- 我的案件 -->
      <div class="bg-white rounded-xl p-6 shadow-card border border-gray-100">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-800">我的案件</h3>
          <router-link to="/cases" class="text-sm text-primary-600 hover:text-primary-500 font-medium">
            查看全部 →
          </router-link>
        </div>

        <div class="space-y-3">
          <div 
            v-for="caseItem in recentCases" 
            :key="caseItem.id"
            class="p-4 bg-gradient-to-r from-gray-50 to-white rounded-lg border border-gray-100 hover:border-primary-200 transition-all cursor-pointer"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1 min-w-0">
                <p class="text-sm font-semibold text-gray-800 truncate">{{ caseItem.title }}</p>
                <p class="text-xs text-gray-500 mt-1">{{ caseItem.caseNo }}</p>
              </div>
              <span 
                class="ml-3 px-2 py-1 rounded-full text-xs font-medium whitespace-nowrap"
                :class="getStatusClass(caseItem.status)"
              >
                {{ getStatusLabel(caseItem.status) }}
              </span>
            </div>
            
            <!-- 进度条 -->
            <div class="mt-3">
              <div class="flex items-center justify-between text-xs text-gray-500 mb-1">
                <span>流程进度</span>
                <span>{{ caseItem.progress }}%</span>
              </div>
              <div class="w-full h-1.5 bg-gray-200 rounded-full overflow-hidden">
                <div 
                  class="h-full rounded-full transition-all duration-300"
                  :class="getProgressBarClass(caseItem.status)"
                  :style="{ width: caseItem.progress + '%' }"
                ></div>
              </div>
            </div>
          </div>

          <div v-if="recentCases.length === 0" class="text-center py-8 text-gray-400">
            <el-icon :size="48" color="#d1d5db"><Document /></el-icon>
            <p class="mt-2 text-sm">暂无案件</p>
            <button 
              @click="$router.push('/cases/create')"
              class="mt-4 px-4 py-2 bg-primary-500 text-white rounded-lg text-sm font-medium hover:bg-primary-600"
            >
              创建第一个案件
            </button>
          </div>
        </div>
      </div>

      <!-- 最近动态 -->
      <div class="bg-white rounded-xl p-6 shadow-card border border-gray-100">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-800">最近动态</h3>
          <el-button text size="small">查看全部</el-button>
        </div>

        <div class="relative">
          <div class="absolute left-3 top-0 bottom-0 w-0.5 bg-gray-200"></div>
          
          <div class="space-y-6 pl-8 relative">
            <div 
              v-for="(activity, index) in recentActivities" 
              :key="activity.id"
              class="relative"
            >
              <!-- 时间点 -->
              <div 
                class="absolute -left-[calc(1.75rem+3px)] top-1.5 w-3 h-3 rounded-full border-2 border-white"
                :class="activity.dotColor"
              ></div>
              
              <!-- 连接线 -->
              <div 
                v-if="index !== recentActivities.length - 1"
                class="absolute -left-[calc(1.65rem+3px)] top-4 w-0.5 h-[calc(100%-0.25rem)]"
                :class="activity.lineColor"
              ></div>

              <!-- 内容卡片 -->
              <div class="bg-gray-50 rounded-lg p-4 ml-4 hover:bg-gray-100 transition-colors">
                <div class="flex items-start justify-between">
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-medium text-gray-800">{{ activity.title }}</p>
                    <p class="text-xs text-gray-500 mt-1 line-clamp-2">{{ activity.description }}</p>
                  </div>
                  <span class="text-xs text-gray-400 whitespace-nowrap ml-3">{{ activity.time }}</span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="recentActivities.length === 0" class="text-center py-8 text-gray-400">
            <el-icon :size="48" color="#d1d5db"><Bell /></el-icon>
            <p class="mt-2 text-sm">暂无动态</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/lib/stores/auth'
import {
  Plus,
  Upload,
  Document,
  Files,
  CircleCheck,
  Bell,
} from '@element-plus/icons-vue'

const authStore = useAuthStore()

// 计算属性：问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

// 快捷操作配置
const quickActions = [
  {
    label: '新建案件',
    description: '创建新的诉讼案件',
    path: '/cases/create',
    icon: Plus,
    bgColor: 'bg-blue-50',
    iconColor: 'text-blue-600',
  },
  {
    label: '上传证据',
    description: '上传电子证据文件',
    path: '/evidences/upload',
    icon: Upload,
    bgColor: 'bg-green-50',
    iconColor: 'text-green-600',
  },
  {
    label: '查看文书',
    description: '浏览法律文书中心',
    path: '/documents',
    icon: Document,
    bgColor: 'bg-purple-50',
    iconColor: 'text-purple-600',
  },
  {
    label: '证据管理',
    description: '管理所有证据材料',
    path: '/evidences',
    icon: Files,
    bgColor: 'bg-orange-50',
    iconColor: 'text-orange-600',
  },
]

// 待办事项（模拟数据，后续替换为真实API）
const pendingCount = computed(() => pendingItems.value.length)
const pendingItems = ref([
  { id: 1, title: '提交答辩状', time: '2小时前', dotColor: 'bg-red-500' },
  { id: 2, title: '补交证据材料', time: '昨天', dotColor: 'bg-yellow-500' },
  { id: 3, title: '确认开庭时间', time: '3天前', dotColor: 'bg-blue-500' },
])

// 我的案件（模拟数据）
const recentCases = ref([
  {
    id: 1,
    title: '张三诉李四民间借贷纠纷案',
    caseNo: 'TDEP-20260517-001',
    status: 'IN_TRIAL',
    progress: 70,
  },
  {
    id: 2,
    title: '王五诉赵六合同违约案',
    caseNo: 'TDEP-20260517-002',
    status: 'EVIDENCE_CLOSING',
    progress: 45,
  },
])

// 最近动态（模拟数据）
const recentActivities = ref([
  {
    id: 1,
    title: '案件状态更新',
    description: '"张三诉李四民间借贷纠纷案"已进入庭审阶段',
    time: '30分钟前',
    dotColor: 'bg-green-500',
    lineColor: 'bg-gray-300',
  },
  {
    id: 2,
    title: '新证据上传',
    description: '您已成功上传证据文件"借款合同扫描件.pdf"',
    time: '2小时前',
    dotColor: 'bg-blue-500',
    lineColor: 'bg-gray-300',
  },
  {
    id: 3,
    title: '系统通知',
    description: '您的案件将于2024年5月20日上午9:00在第三法庭开庭',
    time: '昨天',
    dotColor: 'bg-yellow-500',
    lineColor: 'bg-gray-300',
  },
])

// 状态相关方法
function getStatusClass(status: string): string {
  const map: Record<string, string> = {
    DRAFT: 'bg-gray-100 text-gray-600',
    PENDING_REVIEW: 'bg-yellow-100 text-yellow-600',
    ACCEPTED: 'bg-blue-100 text-blue-600',
    IN_TRIAL: 'bg-red-100 text-red-600',
    JUDGED: 'bg-green-100 text-green-600',
    CLOSED: 'bg-gray-100 text-gray-500',
  }
  return map[status] || 'bg-gray-100 text-gray-600'
}

function getStatusLabel(status: string): string {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    PENDING_REVIEW: '待审核',
    ACCEPTED: '已受理',
    EVIDENCE_CLOSING: '举证中',
    TRIAL_SCHEDULED: '已排期',
    IN_TRIAL: '庭审中',
    JUDGED: '已判决',
    CLOSED: '已结案',
  }
  return map[status] || status
}

function getProgressBarClass(status: string): string {
  const map: Record<string, string> = {
    DRAFT: 'bg-gray-400',
    PENDING_REVIEW: 'bg-yellow-400',
    ACCEPTED: 'bg-blue-400',
    IN_TRIAL: 'bg-red-400',
    JUDGED: 'bg-green-400',
  }
  return map[status] || 'bg-gray-300'
}
</script>

<style scoped>
/* 自定义样式 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
