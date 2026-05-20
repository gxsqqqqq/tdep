<template>
  <div class="workspace-layout" :class="{ 'dark': isDark }">
    <header class="workspace-header">
      <div class="header-left">
        <button class="collapse-btn" @click="toggleSidebar" :title="isCollapsed ? '展开菜单' : '收起菜单'">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path v-if="isCollapsed" d="M3 12h18M3 6h18M3 18h18"/>
            <path v-else d="M4 6h16M4 12h16M4 18h16"/>
          </svg>
        </button>
        <div class="logo" @click="$router.push('/')">
          <div class="logo-icon">⚖️</div>
          <span class="logo-text" v-show="!isCollapsed || !isMobile">TDEP</span>
        </div>
      </div>

      <div class="header-center">
        <div class="search-box">
          <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <path d="M21 21l-4.35-4.35"/>
          </svg>
          <input
            type="text"
            placeholder="搜索案件、文档、证据..."
            v-model="searchQuery"
            @keyup.enter="handleSearch"
          />
        </div>
      </div>

      <div class="header-right">
        <ThemeToggle />

        <div class="notification-wrapper">
          <button class="notification-btn" @click="toggleNotifications">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 01-3.46 0"/>
            </svg>
            <span class="badge" v-if="unreadCount > 0">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
          </button>
        </div>

        <div class="user-dropdown" ref="userDropdownRef">
          <button class="user-avatar-btn" @click="toggleUserMenu">
            <div class="avatar">{{ userInitials }}</div>
            <span class="username">{{ userName }}</span>
            <svg class="chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M6 9l6 6 6-6"/>
            </svg>
          </button>

          <transition name="dropdown">
            <div class="dropdown-menu" v-show="showUserMenu">
              <div class="dropdown-header">
                <div class="user-info">
                  <div class="avatar large">{{ userInitials }}</div>
                  <div class="info-text">
                    <div class="name">{{ userName }}</div>
                    <div class="role">{{ userRoleLabel }}</div>
                  </div>
                </div>
              </div>
              <div class="divider"></div>
              <a href="/profile" class="menu-item">
                <svg class="item-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2M12 11a4 4 0 100-8 4 4 0 000 8z"/>
                </svg>
                <span>个人中心</span>
              </a>
              <a href="/profile/notifications" class="menu-item">
                <svg class="item-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
                </svg>
                <span>消息通知</span>
              </a>
              <div class="divider"></div>
              <button class="menu-item logout" @click="handleLogout">
                <svg class="item-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"/>
                </svg>
                <span>退出登录</span>
              </button>
            </div>
          </transition>
        </div>
      </div>
    </header>

    <div class="workspace-body">
      <aside class="sidebar" :class="{ 'collapsed': isCollapsed, 'mobile-hidden': isMobile && !showMobileSidebar }">
        <nav class="nav-menu">
          <template v-for="menu in filteredMenus" :key="menu.path">
            <div
              class="menu-item"
              :class="{ 'active': isActive(menu.path), 'has-submenu': menu.children?.length }"
              @click="handleMenuClick(menu)"
            >
              <component :is="menu.icon" class="menu-icon" v-if="menu.icon" />
              <span v-else class="menu-icon-placeholder">📋</span>
              <transition name="fade">
                <span class="menu-text" v-show="!isCollapsed">{{ menu.title }}</span>
              </transition>
              <svg
                v-if="menu.children?.length"
                class="submenu-arrow"
                :class="{ 'expanded': expandedMenus.includes(menu.path) }"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M9 18l6-6-6-6"/>
              </svg>
            </div>

            <transition name="submenu">
              <div class="submenu" v-if="menu.children?.length && expandedMenus.includes(menu.path) && !isCollapsed">
                <router-link
                  v-for="child in menu.children"
                  :key="child.path"
                  :to="child.path"
                  class="submenu-item"
                  :class="{ 'active': $route.path === child.path }"
                >
                  {{ child.title }}
                </router-link>
              </div>
            </transition>
          </template>
        </nav>
      </aside>

      <main class="main-content" @click="closeMobileSidebar">
        <div class="breadcrumb-bar" v-if="breadcrumbs.length > 0">
          <nav class="breadcrumb">
            <a href="#" @click.prevent="$router.push('/')" class="breadcrumb-item home">
              <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16">
                <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
              </svg>
            </a>
            <template v-for="(crumb, index) in breadcrumbs" :key="index">
              <span class="separator">/</span>
              <router-link
                v-if="index < breadcrumbs.length - 1"
                :to="crumb.path"
                class="breadcrumb-item"
              >
                {{ crumb.title }}
              </router-link>
              <span v-else class="breadcrumb-item current">{{ crumb.title }}</span>
            </template>
          </nav>
        </div>

        <div class="content-area">
          <router-view v-slot="{ Component }">
            <transition name="page-fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </main>
    </div>

    <div class="mobile-overlay" v-if="isMobile && showMobileSidebar" @click="closeMobileSidebar"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { UserRole, ROLE_LABELS } from '@/lib/constants/roles'
import ThemeToggle from '@/components/ui/ThemeToggle.vue'
import { useDarkMode } from '@/composables/useDarkMode'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { isDark } = useDarkMode()

const isCollapsed = ref(false)
const showUserMenu = ref(false)
const showMobileSidebar = ref(false)
const searchQuery = ref('')
const unreadCount = ref(3)
const userDropdownRef = ref<HTMLElement | null>(null)
const expandedMenus = ref<string[]>([])

const isMobile = ref(window.innerWidth < 768)

interface MenuItem {
  path: string
  title: string
  icon?: any
  roles?: UserRole[]
  children?: { path: string; title: string }[]
}

const menus: MenuItem[] = [
  {
    path: '/workspace',
    title: '工作台',
    roles: [UserRole.PARTY, UserRole.JUDGE, UserRole.CLERK, UserRole.ADMIN],
    icon: () => h('svg', {
      viewBox: '0 0 24 24',
      fill: 'none',
      stroke: 'currentColor',
      'stroke-width': '2',
      innerHTML: '<rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/>'
    })
  },
  {
    path: '/workspace/party',
    title: '我的案件',
    roles: [UserRole.PARTY],
    icon: () => h('svg', {
      viewBox: '0 0 24 24',
      fill: 'none',
      stroke: 'currentColor',
      'stroke-width': '2',
      innerHTML: '<path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14,2 14,8 20,8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>'
    }),
    children: [
      { path: '/workspace/party/cases', title: '案件列表' },
      { path: '/workspace/party/documents', title: '我的文书' }
    ]
  },
  {
    path: '/workspace/judge',
    title: '审判管理',
    roles: [UserRole.JUDGE],
    icon: () => h('svg', {
      viewBox: '0 0 24 24',
      fill: 'none',
      stroke: 'currentColor',
      'stroke-width': '2',
      innerHTML: '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>'
    }),
    children: [
      { path: '/workspace/judge/pending', title: '待审案件' },
      { path: '/workspace/judge/schedule', title: '庭审排期' }
    ]
  },
  {
    path: '/workspace/clerk',
    title: '庭审记录',
    roles: [UserRole.CLERK],
    icon: () => h('svg', {
      viewBox: '0 0 24 24',
      fill: 'none',
      stroke: 'currentColor',
      'stroke-width': '2',
      innerHTML: '<path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/>'
    }),
    children: [
      { path: '/workspace/clerk/trials', title: '庭审记录' },
      { path: '/workspace/clerk/evidence', title: '证据整理' }
    ]
  },
  {
    path: '/case/create',
    title: '新建案件',
    roles: [UserRole.PARTY],
    icon: () => h('svg', {
      viewBox: '0 0 24 24',
      fill: 'none',
      stroke: 'currentColor',
      'stroke-width': '2',
      innerHTML: '<circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/>'
    })
  },
  {
    path: '/evidence/upload',
    title: '上传证据',
    roles: [UserRole.PARTY, UserRole.CLERK],
    icon: () => h('svg', {
      viewBox: '0 0 24 24',
      fill: 'none',
      stroke: 'currentColor',
      'stroke-width': '2',
      innerHTML: '<path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"/><polyline points="17,8 12,3 7,8"/><line x1="12" y1="3" x2="12" y2="15"/>'
    })
  }
]

const filteredMenus = computed(() => {
  const userRole = authStore.currentUser?.roles[0] as UserRole | undefined
  if (!userRole) return []
  return menus.filter(menu => !menu.roles || menu.roles.includes(userRole))
})

const userInitials = computed(() => {
  const name = authStore.currentUser?.realName || authStore.currentUser?.username || 'U'
  return name.charAt(0).toUpperCase()
})

const userName = computed(() => {
  return authStore.currentUser?.realName || authStore.currentUser?.username || '用户'
})

const userRoleLabel = computed(() => {
  const role = authStore.currentUser?.roles[0] as UserRole | undefined
  return role ? ROLE_LABELS[role] : ''
})

const breadcrumbs = computed(() => {
  const crumbs: { path: string; title: string }[] = []

  if (route.meta.title) {
    crumbs.push({
      path: route.path,
      title: route.meta.title as string
    })

    if (route.matched.length > 1) {
      for (let i = 0; i < route.matched.length - 1; i++) {
        const matched = route.matched[i]
        if (matched.meta.title && matched.path !== '/') {
          crumbs.unshift({
            path: matched.path,
            title: matched.meta.title as string
          })
        }
      }
    }
  }

  return crumbs
})

function toggleSidebar() {
  if (isMobile.value) {
    showMobileSidebar.value = !showMobileSidebar.value
  } else {
    isCollapsed.value = !isCollapsed.value
  }
}

function toggleUserMenu() {
  showUserMenu.value = !showUserMenu.value
}

function toggleNotifications() {
  router.push('/profile/notifications')
}

function handleSearch() {
  if (searchQuery.value.trim()) {
    console.log('搜索:', searchQuery.value)
  }
}

function handleLogout() {
  showUserMenu.value = false
  authStore.logout()
  router.push('/login')
}

function isActive(path: string): boolean {
  return route.path.startsWith(path)
}

function handleMenuClick(menu: MenuItem) {
  if (menu.children?.length) {
    if (expandedMenus.value.includes(menu.path)) {
      expandedMenus.value = expandedMenus.value.filter(p => p !== menu.path)
    } else {
      expandedMenus.value.push(menu.path)
    }
  } else {
    router.push(menu.path)
    closeMobileSidebar()
  }
}

function closeMobileSidebar() {
  if (isMobile.value) {
    showMobileSidebar.value = false
  }
}

function handleClickOutside(event: MouseEvent) {
  if (userDropdownRef.value && !userDropdownRef.value.contains(event.target as Node)) {
    showUserMenu.value = false
  }
}

function handleResize() {
  isMobile.value = window.innerWidth < 768
  if (!isMobile.value) {
    showMobileSidebar.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  window.addEventListener('resize', handleResize)

  if (window.innerWidth < 1024) {
    isCollapsed.value = true
  }
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.workspace-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: var(--color-bg-primary);
  transition: background-color 0.3s ease, color 0.3s ease;
}

.workspace-layout.dark {
  background-color: var(--color-bg-primary);
}

.workspace-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: var(--color-bg-secondary);
  border-bottom: 1px solid var(--color-border);
  z-index: 1000;
  box-shadow: var(--shadow-sm);
}

.dark .workspace-header {
  background: var(--color-bg-tertiary);
  border-bottom-color: var(--color-border);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  color: #6b7280;
  transition: all 0.2s ease;
}

.collapse-btn:hover {
  background-color: #f3f4f6;
  color: #374151;
}

.dark .collapse-btn:hover {
  background-color: #374151;
  color: #e5e7eb;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-weight: 700;
  font-size: 20px;
  color: #2563eb;
}

.dark .logo {
  color: #60a5fa;
}

.logo-icon {
  font-size: 28px;
}

.header-center {
  flex: 1;
  max-width: 480px;
  margin: 0 32px;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 12px;
  width: 18px;
  height: 18px;
  color: #9ca3af;
  pointer-events: none;
}

.search-box input {
  width: 100%;
  padding: 8px 16px 8px 40px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background-color: var(--color-bg-tertiary);
  font-size: 14px;
  color: var(--color-text-primary);
  outline: none;
  transition: all 0.2s ease;
}

.search-box input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--color-primary-alpha);
  background-color: var(--color-bg-secondary);
}

.dark .search-box input {
  background-color: var(--color-bg-tertiary);
  border-color: var(--color-border);
  color: var(--color-text-primary);
}

.dark .search-box input:focus {
  border-color: var(--color-primary-light);
  box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.1);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notification-btn {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  color: #6b7280;
  transition: all 0.2s ease;
}

.notification-btn:hover {
  background-color: #f3f4f6;
  color: #374151;
}

.dark .notification-btn:hover {
  background-color: #374151;
  color: #e5e7eb;
}

.badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  font-size: 11px;
  font-weight: 600;
  color: white;
  background-color: #ef4444;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-dropdown {
  position: relative;
}

.user-avatar-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-avatar-btn:hover {
  background-color: #f3f4f6;
}

.dark .user-avatar-btn:hover {
  background-color: #374151;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.avatar.large {
  width: 48px;
  height: 48px;
  font-size: 18px;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dark .username {
  color: var(--color-text-primary);
}

.chevron {
  width: 16px;
  height: 16px;
  color: #9ca3af;
  transition: transform 0.2s ease;
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 280px;
  background: var(--color-bg-secondary);
  border-radius: 12px;
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  z-index: 1001;
}

.dark .dropdown-menu {
  background: var(--color-bg-tertiary);
  box-shadow: var(--shadow-dark-lg);
}

.dropdown-header {
  padding: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-text {
  flex: 1;
}

.info-text .name {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.dark .info-text .name {
  color: var(--color-text-primary);
}

.info-text .role {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-top: 2px;
}

.divider {
  height: 1px;
  background-color: var(--color-border);
}

.dark .divider {
  background-color: var(--color-border);
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 12px 20px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
  color: #374151;
  text-decoration: none;
  transition: all 0.2s ease;
}

.menu-item:hover {
  background-color: #f9fafb;
}

.dark .menu-item:hover {
  background-color: #374151;
}

.menu-item.logout {
  color: #dc2626;
}

.item-icon {
  width: 18px;
  height: 18px;
  color: #6b7280;
}

.workspace-body {
  display: flex;
  margin-top: 64px;
  min-height: calc(100vh - 64px);
}

.sidebar {
  width: 260px;
  background: var(--color-bg-secondary);
  border-right: 1px solid var(--color-border);
  overflow-y: auto;
  transition: width 0.3s ease, transform 0.3s ease;
  flex-shrink: 0;
}

.sidebar.collapsed {
  width: 72px;
}

.dark .sidebar {
  background: var(--color-bg-tertiary);
  border-right-color: var(--color-border);
}

.nav-menu {
  padding: 12px 8px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  margin: 2px 0;
  border-radius: 8px;
  cursor: pointer;
  color: #6b7280;
  transition: all 0.2s ease;
  position: relative;
}

.menu-item:hover {
  background-color: #f3f4f6;
  color: #374151;
}

.dark .menu-item:hover {
  background-color: #374151;
  color: #e5e7eb;
}

.menu-item.active {
  background-color: #eff6ff;
  color: #2563eb;
}

.dark .menu-item.active {
  background-color: rgba(59, 130, 246, 0.1);
  color: #60a5fa;
}

.menu-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.menu-icon-placeholder {
  font-size: 18px;
  flex-shrink: 0;
}

.menu-text {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}

.submenu-arrow {
  width: 16px;
  height: 16px;
  transition: transform 0.2s ease;
  opacity: 0.5;
}

.submenu-arrow.expanded {
  transform: rotate(90deg);
}

.submenu {
  padding-left: 44px;
  margin-top: 4px;
  overflow: hidden;
}

.submenu-item {
  display: block;
  padding: 8px 12px;
  font-size: 13px;
  color: #6b7280;
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.submenu-item:hover {
  background-color: #f3f4f6;
  color: #374151;
}

.dark .submenu-item:hover {
  background-color: #374151;
  color: #e5e7eb;
}

.submenu-item.active {
  color: #2563eb;
  font-weight: 500;
}

.dark .submenu-item.active {
  color: #60a5fa;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background-color: var(--color-bg-primary);
}

.dark .main-content {
  background-color: var(--color-bg-primary);
}

.breadcrumb-bar {
  margin-bottom: 20px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.breadcrumb-item {
  color: var(--color-text-secondary);
  text-decoration: none;
  transition: color 0.2s ease;
}

.breadcrumb-item:hover:not(.current):not(.home) {
  color: var(--color-primary);
}

.breadcrumb-item.home {
  display: flex;
  align-items: center;
}

.breadcrumb-item.current {
  color: var(--color-text-primary);
  font-weight: 500;
}

.dark .breadcrumb-item.current {
  color: var(--color-text-primary);
}

.separator {
  color: var(--color-border);
}

.content-area {
  min-height: calc(100vh - 180px);
}

.mobile-overlay {
  position: fixed;
  top: 64px;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
}

@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    top: 64px;
    left: 0;
    bottom: 0;
    z-index: 999;
    transform: translateX(0);
  }

  .sidebar.mobile-hidden {
    transform: translateX(-100%);
  }

  .header-center {
    display: none;
  }

  .username {
    display: none;
  }

  .main-content {
    padding: 16px;
  }
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.submenu-enter-active,
.submenu-leave-active {
  transition: all 0.3s ease;
}

.submenu-enter-from,
.submenu-leave-to {
  opacity: 0;
  max-height: 0;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: all 0.2s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateX(10px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}
</style>
