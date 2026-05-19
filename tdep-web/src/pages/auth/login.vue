<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-indigo-50 px-4 py-12 sm:px-6 lg:px-8">
    <div class="w-full max-w-md space-y-8">
      <!-- Logo 和标题 -->
      <div class="text-center">
        <h2 class="text-3xl font-bold tracking-tight text-gray-900">
          TDEP 可信数字证据平台
        </h2>
        <p class="mt-2 text-sm text-gray-600">
          数字司法 · 在线诉讼 · 电子证据
        </p>
      </div>

      <!-- 登录表单 -->
      <div class="bg-white shadow-soft rounded-2xl p-8 space-y-6">
        <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin">
          <el-form-item prop="username">
            <label class="block text-sm font-medium text-gray-700 mb-2">用户名</label>
            <el-input
              v-model="form.username"
              size="large"
              placeholder="请输入用户名"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <label class="block text-sm font-medium text-gray-700 mb-2">密码</label>
            <el-input
              v-model="form.password"
              size="large"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="w-full"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form>

        <div class="text-center text-sm text-gray-500">
          还没有账号？
          <router-link to="/register" class="font-medium text-primary-600 hover:text-primary-500">
            立即注册
          </router-link>
        </div>
      </div>

      <!-- 测试账号提示 -->
      <div class="mt-4 p-4 bg-blue-50 rounded-lg border border-blue-100">
        <p class="text-xs text-blue-600 font-medium mb-2">💡 测试账号</p>
        <p class="text-xs text-blue-500">管理员: admin / Admin@123456</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/lib/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度在 6 到 50 个字符', trigger: 'blur' },
  ],
}

async function handleLogin() {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const result = await authStore.login(form)
    
    if (result.code === 0) {
      ElMessage.success('登录成功！')
      const redirect = (route.query.redirect as string) || '/workspace'
      router.push(redirect)
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '网络错误，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 自定义 Element Plus 样式覆盖 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: var(--shadow-sm);
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  border: none;
  font-weight: 500;
  letter-spacing: 0.5px;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #1d4ed8 0%, #2563eb 100%);
}
</style>
