<template>
  <main class="login-page">
    <section class="login-visual">
      <h1>可信数字证据平台</h1>
      <p>在线立案、提交证据、查看文书、跟踪案件进度 — 一站式司法服务，让法律程序更简单。</p>
    </section>
    <section class="login-panel">
      <h2>登录 TDEP</h2>
      <el-form :model="form" label-position="top" @submit.prevent="submitLogin">
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
        <el-button type="primary" size="large" class="login-button" :loading="loading" @click="submitLogin">
          登录
        </el-button>
        <div class="register-link">
          <span>还没有账号？</span>
          <el-button link type="primary" @click="showRegister = true">立即注册</el-button>
        </div>
      </el-form>
    </section>

    <!-- 注册弹窗 -->
    <el-dialog v-model="showRegister" title="用户注册" width="480px" :close-on-click-modal="false">
      <el-form :model="regForm" label-position="top" :rules="regRules" ref="regFormRef">
        <el-form-item label="身份类型" prop="roleCode">
          <el-radio-group v-model="regForm.roleCode" size="large">
            <el-radio-button value="USER">普通用户</el-radio-button>
            <el-radio-button value="JUDGE">法官</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="regForm.username" placeholder="4-32位，字母、数字或下划线" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="regForm.password" type="password" show-password placeholder="8-64位" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="regForm.confirmPassword" type="password" show-password placeholder="再次输入密码" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="regForm.nickname" placeholder="可选" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="regForm.phone" placeholder="可选" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="regForm.email" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegister = false">取消</el-button>
        <el-button type="primary" :loading="regLoading" @click="submitRegister">注册</el-button>
      </template>
    </el-dialog>
  </main>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'
import { register } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

async function submitLogin() {
  loading.value = true
  try {
    await authStore.login(form)
    const redirect = route.query.redirect as string
    if (redirect) {
      router.replace(redirect)
    } else {
      const isAdmin = authStore.roles.includes('ADMIN')
      router.replace(isAdmin ? '/admin/dashboard' : '/my-cases')
    }
  } finally {
    loading.value = false
  }
}

// ---- 注册逻辑 ----
const showRegister = ref(false)
const regLoading = ref(false)
const regFormRef = ref<FormInstance>()
const regForm = reactive({
  roleCode: 'USER',
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: '',
  email: ''
})

const validateConfirmPassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value !== regForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const regRules: FormRules = {
  roleCode: [{ required: true, message: '请选择身份类型', trigger: 'change' }],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 32, message: '长度在 4 到 32 位之间', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9_]+$/, message: '只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 64, message: '长度在 8 到 64 位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { pattern: /^$|^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

async function submitRegister() {
  if (!regFormRef.value) return
  await regFormRef.value.validate(async (valid) => {
    if (!valid) return
    regLoading.value = true
    try {
      await register({
        username: regForm.username,
        password: regForm.password,
        nickname: regForm.nickname || undefined,
        phone: regForm.phone || undefined,
        email: regForm.email || undefined,
        roleCode: regForm.roleCode
      })
      ElMessage.success('注册成功，请登录')
      showRegister.value = false
      form.username = regForm.username
      form.password = ''
      // 重置注册表单
      regForm.roleCode = 'USER'
      regForm.username = ''
      regForm.password = ''
      regForm.confirmPassword = ''
      regForm.nickname = ''
      regForm.phone = ''
      regForm.email = ''
    } finally {
      regLoading.value = false
    }
  })
}
</script>

<style scoped>
.register-link {
  text-align: center;
  margin-top: 16px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}
</style>
