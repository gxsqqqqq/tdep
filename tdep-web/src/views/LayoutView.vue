<template>
  <el-container class="app-shell">
    <el-aside class="app-sidebar" width="236px">
      <div class="brand">
        <span class="brand-mark">T</span>
        <div>
          <strong>TDEP</strong>
          <small>可信数字证据平台</small>
        </div>
      </div>

      <el-menu router class="nav-menu" :default-active="activePath">
        <el-menu-item index="/">
          <el-icon><DataBoard /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/evidences">
          <el-icon><Files /></el-icon>
          <span>电子证据</span>
        </el-menu-item>
        <el-menu-item index="/cases">
          <el-icon><Briefcase /></el-icon>
          <span>案件管理</span>
        </el-menu-item>
        <el-menu-item index="/documents">
          <el-icon><DocumentChecked /></el-icon>
          <span>法律文书</span>
        </el-menu-item>
        <el-menu-item index="/ai">
          <el-icon><ChatDotRound /></el-icon>
          <span>AI 法律助手</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div>
          <h1>{{ route.meta.title || '工作台' }}</h1>
        </div>
        <el-button :icon="Setting" circle />
      </el-header>

      <el-main class="app-main">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import {
  Briefcase,
  ChatDotRound,
  DataBoard,
  DocumentChecked,
  Files,
  Setting
} from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useRoute } from 'vue-router'

// 基础布局组件，仅提供导航框架和页面承载区域。
const route = useRoute()
const activePath = computed(() => route.path)
</script>
