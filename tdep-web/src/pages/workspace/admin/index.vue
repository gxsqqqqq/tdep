<template>
  <div class="admin-workspace min-h-screen bg-gray-50 p-6">
    <!-- 顶部横幅 -->
    <div class="hero-banner rounded-2xl bg-gradient-to-r from-slate-800 via-slate-700 to-slate-800 px-8 py-10 text-white shadow-lg">
      <h1 class="text-2xl font-bold tracking-wide">系统管理中心</h1>
      <p class="mt-2 text-sm text-slate-300">用户管理 · 权限配置 · 系统监控</p>
    </div>

    <!-- 快捷操作卡片 -->
    <div class="mt-6 grid grid-cols-4 gap-4">
      <el-card
        v-for="action in quickActions"
        :key="action.label"
        shadow="hover"
        class="cursor-pointer transition-all hover:-translate-y-1 hover:shadow-xl"
        @click="handleQuickAction(action.route)"
      >
        <div class="flex items-center gap-4">
          <div
            class="flex h-12 w-12 items-center justify-center rounded-xl"
            :class="action.bgClass"
          >
            <el-icon :size="22" :class="action.iconClass">
              <component :is="action.icon" />
            </el-icon>
          </div>
          <div>
            <div class="text-sm font-semibold text-gray-800">{{ action.label }}</div>
            <div class="mt-0.5 text-xs text-gray-400">{{ action.desc }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 主体两列布局 -->
    <div class="mt-6 grid grid-cols-12 gap-6">
      <!-- 左侧：系统概览统计 -->
      <div class="col-span-5">
        <el-card shadow="never" class="!border-gray-100">
          <template #header>
            <div class="flex items-center justify-between">
              <span class="font-semibold text-gray-700">系统概览</span>
              <el-button text type="primary" size="small">刷新</el-button>
            </div>
          </template>
          <div class="grid grid-cols-2 gap-4">
            <div
              v-for="stat in statsData"
              :key="stat.label"
              class="rounded-xl border border-gray-100 bg-gray-50/80 p-5 transition-all hover:shadow-md"
            >
              <div class="flex items-center justify-between">
                <span class="text-xs text-gray-400">{{ stat.label }}</span>
                <el-icon :size="16" :class="stat.trendUp ? 'text-emerald-500' : 'text-red-400'">
                  <component :is="stat.trendUp ? 'Top' : 'Bottom'" />
                </el-icon>
              </div>
              <div class="mt-2 text-3xl font-bold text-gray-800">{{ stat.value }}</div>
              <div class="mt-1 text-xs" :class="stat.trendUp ? 'text-emerald-500' : 'text-red-400'">
                {{ stat.change }}
              </div>
            </div>
          </div>

          <!-- 迷你图表占位 -->
          <div class="mt-5 rounded-xl bg-gradient-to-br from-slate-50 to-slate-100 p-5">
            <div class="mb-3 flex items-center justify-between">
              <span class="text-xs font-medium text-gray-500">近7日活跃趋势</span>
              <span class="text-xs text-emerald-500">+12.5%</span>
            </div>
            <div class="flex items-end gap-1.5">
              <div
                v-for="(bar, idx) in chartBars"
                :key="idx"
                class="w-full rounded-t-sm bg-gradient-to-t from-indigo-400 to-indigo-300 transition-all"
                :style="{ height: bar + '%' }"
              />
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧：最新操作日志 -->
      <div class="col-span-7">
        <el-card shadow="never" class="!border-gray-100">
          <template #header>
            <div class="flex items-center justify-between">
              <span class="font-semibold text-gray-700">最新操作日志</span>
              <el-button text type="primary" size="small">查看全部</el-button>
            </div>
          </template>
          <el-table :data="logData" stripe style="width: 100%" :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 600, fontSize: '13px' }" row-class-name="text-sm">
            <el-table-column prop="operator" label="操作人" width="110" />
            <el-table-column prop="type" label="操作类型" width="120">
              <template #default="{ row }">
                <el-tag :type="tagTypeMap[row.type] || 'info'" size="small" effect="plain">
                  {{ row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="target" label="目标" min-width="140" show-overflow-tooltip />
            <el-table-column prop="time" label="时间" width="170" />
            <el-table-column prop="ip" label="IP 地址" width="140" />
          </el-table>
        </el-card>
      </div>
    </div>

    <!-- 底部：系统健康状态 -->
    <div class="mt-6">
      <el-card shadow="never" class="!border-gray-100">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="font-semibold text-gray-700">系统服务状态</span>
            <el-tag type="success" size="small" effect="light">
              全部运行正常
            </el-tag>
          </div>
        </template>
        <div class="grid grid-cols-7 gap-3">
          <div
            v-for="svc in services"
            :key="svc.name"
            class="flex flex-col items-center gap-2 rounded-xl border border-gray-100 bg-gray-50/60 p-4 transition-all hover:shadow-sm"
          >
            <div class="relative">
              <div
                class="h-3 w-3 rounded-full animate-pulse"
                :class="svc.status === 'online' ? 'bg-emerald-500 shadow-emerald-200' : 'bg-red-400 shadow-red-200'"
                :style="{ boxShadow: svc.status === 'online' ? '0 0 8px rgba(16,185,129,.5)' : '0 0 8px rgba(248,113,113,.5)' }"
              />
            </div>
            <span class="text-xs font-medium text-gray-600">{{ svc.name }}</span>
            <span class="text-[11px]" :class="svc.status === 'online' ? 'text-emerald-500' : 'text-red-400'">
              {{ svc.status === 'online' ? '运行中' : '异常' }}
            </span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Setting, Document, Top, Bottom } from '@element-plus/icons-vue'

const router = useRouter()

interface QuickAction {
  label: string
  desc: string
  icon: any
  route: string
  bgClass: string
  iconClass: string
}

interface StatItem {
  label: string
  value: string | number
  change: string
  trendUp: boolean
}

interface LogItem {
  operator: string
  type: string
  target: string
  time: string
  ip: string
}

interface ServiceItem {
  name: string
  status: 'online' | 'offline'
}

const quickActions = ref<QuickAction[]>([
  {
    label: '用户管理',
    desc: '账号与角色维护',
    icon: User,
    route: '/workspace/admin/users',
    bgClass: 'bg-blue-50',
    iconClass: 'text-blue-500'
  },
  {
    label: '角色权限',
    desc: 'RBAC 权限配置',
    icon: Lock,
    route: '/workspace/admin/users',
    bgClass: 'bg-violet-50',
    iconClass: 'text-violet-500'
  },
  {
    label: '系统设置',
    desc: '参数与配置管理',
    icon: Setting,
    route: '/workspace/admin/system',
    bgClass: 'bg-amber-50',
    iconClass: 'text-amber-500'
  },
  {
    label: '操作日志',
    desc: '审计追踪记录',
    icon: Document,
    route: '/workspace/admin/system',
    bgClass: 'bg-emerald-50',
    iconClass: 'text-emerald-500'
  }
])

const statsData = ref<StatItem[]>([
  { label: '用户总数', value: 1286, change: '较昨日 +18', trendUp: true },
  { label: '在线人数', value: 142, change: '较昨日 -3', trendUp: false },
  { label: '案件总数', value: 3842, change: '较昨日 +26', trendUp: true },
  { label: '今日新增', value: 47, change: '较昨日 +12', trendUp: true }
])

const chartBars = ref([45, 62, 55, 78, 68, 85, 72])

const tagTypeMap: Record<string, any> = {
  '登录': '',
  '新增': 'success',
  '修改': 'warning',
  '删除': 'danger',
  '导出': 'info'
}

const logData = ref<LogItem[]>([
  { operator: '张管理员', type: '登录', target: '后台管理系统', time: '2026-05-19 14:32:08', ip: '192.168.1.100' },
  { operator: '李运维', type: '修改', target: '角色「法官」权限', time: '2026-05-19 14:15:43', ip: '192.168.1.101' },
  { operator: '王审核', type: '新增', target: '用户「陈某某」', time: '2026-05-19 13:58:21', ip: '192.168.1.102' },
  { operator: '赵审计', type: '导出', target: '操作日志报表', time: '2026-05-19 13:40:09', ip: '192.168.1.103' },
  { operator: '张管理员', type: '删除', target: '过期会话记录', time: '2026-05-19 12:22:55', ip: '192.168.1.100' }
])

const services = ref<ServiceItem[]>([
  { name: 'API 网关', status: 'online' },
  { name: '用户服务', status: 'online' },
  { name: '案件服务', status: 'online' },
  { name: '证据服务', status: 'online' },
  { name: '文书服务', status: 'online' },
  { name: '通知服务', status: 'online' },
  { name: '日志服务', status: 'online' }
])

function handleQuickAction(route: string) {
  router.push(route)
}
</script>
