<template>
  <div class="h-screen flex overflow-hidden bg-gray-50">
    <!-- 侧边栏 -->
    <aside 
      :class="[
        'flex flex-col bg-white border-r border-gray-200 transition-all duration-300',
        collapsed ? 'w-20' : 'w-64'
      ]"
    >
      <!-- Logo 区域 -->
      <div class="h-16 flex items-center justify-between px-4 border-b border-gray-200">
        <div v-if="!collapsed" class="flex items-center space-x-2">
          <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center">
            <span class="text-white font-bold text-sm">T</span>
          </div>
          <span class="font-bold text-lg text-gray-800">TDEP</span>
        </div>
        <button @click="toggleSidebar" class="p-1.5 rounded-lg hover:bg-gray-100">
          <el-icon :size="20"><Fold /></el-icon>
        </button>
      </div>

      <!-- 导航菜单 -->
      <nav class="flex-1 overflow-y-auto py-4 px-3 space-y-1">
        <template v-for="menu in menuList" :key="menu.title">
          <div v-if="menu.title" :class="[
            'px-3 py-2 text-xs font-semibold text-gray-400 uppercase tracking-wider',
            collapsed && 'text-center'
          ]">
            {{ !collapsed ? menu.title : menu.title.charAt(0) }}
          </div>

          <router-link
            v-for="item in menu.items"
            :key="item.path"
            :to="item.path"
            active-class="bg-primary-50 text-primary-600"
            exact-active-class="bg-primary-100 text-primary-700"
            class="flex items-center space-x-3 px-3 py-2.5 rounded-xl text-gray-600 hover:bg-gray-50 hover:text-gray-900 transition-colors group"
          >
            <component :is="item.icon" :size="20" />
            <span v-if="!collapsed" class="font-medium">{{ item.label }}</span>
            
            <!-- 未折叠时显示 badge -->
            <span v-if="!collapsed && item.badge" class="ml-auto px-2 py-0.5 text-xs font-medium bg-red-100 text-red-600 rounded-full">
              {{ item.badge }}
            </span>
          </router-link>
        </template>
      </nav>

      <!-- 底部用户信息 -->
      <div class="p-4 border-t border-gray-200">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-medium">
            {{ userAvatar }}
          </div>
          <div v-if="!collapsed" class="flex-1 min-w-0">
            <p class="text-sm font-medium text-gray-900 truncate">{{ userName }}</p>
            <p class="text-xs text-gray-500 truncate">{{ userRoleLabel }}</p>
          </div>
          <button 
            v-if="!collapsed" 
            @click="handleLogout"
            class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500"
            title="退出登录"
          >
            <el-icon :size="18"><SwitchButton /></el-icon>
          </button>
        </div>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- 顶部导航栏 -->
      <header class="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-6">
        <div class="flex items-center space-x-4">
          <button @click="toggleSidebar" class="lg:hidden p-2 rounded-lg hover:bg-gray-100">
            <el-icon :size="20"><Menu /></el-icon>
          </button>
          
          <!-- 面包屑 -->
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta?.title">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="flex items-center space-x-4">
          <!-- 搜索框（可选） -->
          <el-input
            v-model="searchQuery"
            placeholder="搜索案件..."
            prefix-icon="Search"
            size="default"
            class="w-64"
            clearable
          />

          <!-- 通知铃铛 -->
          <el-badge :value="3" :max="99" class="cursor-pointer">
            <el-button :icon="Bell" circle size="default" />
          </el-badge>

          <!-- 用户头像下拉菜单 -->
          <el-dropdown trigger="click">
            <div class="flex items-center space-x-2 cursor-pointer p-1.5 rounded-lg hover:bg-gray-100">
              <div class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white text-sm font-medium">
                {{ userAvatar }}
              </div>
              <span class="hidden md:block text-sm font-medium text-gray-700">{{ userName }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 页面内容区域 -->
      <main class="flex-1 overflow-y-auto p-6 bg-gray-50">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" :key="currentRoute.path" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/lib/stores/auth'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Fold,
  Menu,
  Bell,
  Search,
  SwitchButton,
  Document,
  Files,
  Upload,
  Scale,
  Setting,
  User,
  DataAnalysis,
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// State
const collapsed = ref(false)
const searchQuery = ref('')

// Computed
const currentRoute = computed(() => route)
const userAvatar = computed(() => authStore.user?.nickname?.charAt(0)?.toUpperCase() || 'U')
const userName = computed(() => authStore.user?.nickname || '未登录')
const userRoleLabel = computed(() => {
  const roleMap: Record<string, string> = {
    ADMIN: '管理员',
    JUDGE: '法官',
    CLERK: '书记员',
    PARTY: '当事人',
  }
  return roleMap[authStore.userRole || ''] || '未知'
})

// Menu Configuration (根据角色动态生成)
const menuList = computed(() => {
  const baseMenu = [
    {
      title: '主菜单',
      items: [
        { path: '/workspace', label: '工作台', icon: DataAnalysis },
        { path: '/cases', label: '我的案件', icon: Document, badge: 5 },
        { path: '/evidences', label: '证据管理', icon: Files },
        { path: '/documents', label: '文书中心', icon: Upload },
      ],
    },
  ]

  // 管理员专属菜单
  if (authStore.isAdmin) {
    baseMenu.push({
      title: '系统管理',
      items: [
        { path: '/workspace/admin', label: '用户管理', icon: User },
        { path: '/workspace/admin/system', label: '系统设置', icon: Setting },
      ],
    })
  }

  return baseMenu
})

// Methods
function toggleSidebar() {
  collapsed.value = !collapsed.value
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    authStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
/* 页面切换动画 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.15s ease;
}
.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}
</style>
