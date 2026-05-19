<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-indigo-50 px-4 py-12 sm:px-6 lg:px-8">
    <div class="w-full max-w-md space-y-8">
      <div class="text-center">
        <h2 class="text-3xl font-bold tracking-tight text-gray-900">
          创建账号
        </h2>
        <p class="mt-2 text-sm text-gray-600">
          加入 TDEP 可信数字证据平台
        </p>
      </div>

      <div class="bg-white shadow-soft rounded-2xl p-8 space-y-6">
        <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleRegister">
          <el-form-item prop="username">
            <label class="block text-sm font-medium text-gray-700 mb-2">用户名</label>
            <el-input
              v-model="form.username"
              size="large"
              placeholder="请输入用户名（3-20位）"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <label class="block text-sm font-medium text-gray-700 mb-2">密码</label>
            <el-input
              v-model="form.password"
              size="large"
              type="password"
              placeholder="请输入密码（至少6位）"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <label class="block text-sm font-medium text-gray-700 mb-2">确认密码</label>
            <el-input
              v-model="form.confirmPassword"
              size="large"
              type="password"
              placeholder="请再次输入密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="nickname">
            <label class="block text-sm font-medium text-gray-700 mb-2">昵称（可选）</label>
            <el-input
              v-model="form.nickname"
              size="large"
              placeholder="显示名称"
              prefix-icon="UserFilled"
            />
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="w-full"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
        </el-form>

        <div class="text-center text-sm text-gray-500">
          已有账号？
          <router-link to="/login" class="font-medium text-primary-600 hover:text-primary-500">
            返回登录
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
})

const validateConfirmPassword = (rule: any, value: string, callback: Function) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度在 6 到 50 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

async function handleRegister() {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const response = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: form.username,
        password: form.password,
        nickname: form.nickname || form.username,
      }),
    })
    
    const data = await response.json()
    
    if (data.code === 0) {
      ElMessage.success('注册成功！请登录')
      router.push('/login')
    } else {
      ElMessage.error(data.message || '注册失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '网络错误，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: var(--shadow-sm);
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  border: none;
  font-weight: 500;
}
</style>
