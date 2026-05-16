<template>
  <main class="login-page">
    <section class="login-visual">
      <h1>Trusted Digital Evidence Platform</h1>
      <p>可信数字证据、案件流转、法律文书和实时通知的一体化 LegalTech 管理平台。</p>
    </section>
    <section class="login-panel">
      <h2>登录 TDEP</h2>
      <el-form :model="form" label-position="top" @submit.prevent="submit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" size="large" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" size="large" type="password" autocomplete="current-password" show-password />
        </el-form-item>
        <div class="login-options">
          <span class="login-options__hint">JWT 登录态由服务端签发，过期后需重新登录</span>
          <el-button link type="primary" @click="appStore.toggleDarkMode()">
            {{ appStore.darkMode ? '浅色模式' : '暗黑模式' }}
          </el-button>
        </div>
        <el-button type="primary" size="large" class="login-button" :loading="loading" @click="submit">
          登录
        </el-button>
      </el-form>
    </section>
  </main>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

async function submit() {
  loading.value = true
  try {
    await authStore.login(form)
    router.replace(String(route.query.redirect || '/dashboard'))
  } finally {
    loading.value = false
  }
}
</script>
