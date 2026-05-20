<template>
  <el-card shadow="hover" class="current-todo-panel">
    <template #header>
      <div class="flex items-center gap-2">
        <span class="text-lg">📌</span>
        <span class="font-semibold text-gray-800">当前待办</span>
        <el-badge :value="pendingCount" :max="99" class="ml-2" />
      </div>
    </template>

    <div class="todo-list">
      <div
        v-for="todo in todoList"
        :key="todo.id"
        class="todo-item"
        :class="{ 'todo-item--high': todo.priority === 'high', 'todo-item--completed': todo.completed }"
      >
        <div class="todo-item__checkbox">
          <el-checkbox
            :model-value="todo.completed"
            @change="(val: boolean) => handleToggle(todo.id, val)"
          />
        </div>

        <div class="todo-item__content">
          <div class="todo-item__header">
            <span class="todo-item__name" :class="{ 'line-through text-gray-400': todo.completed }">
              {{ todo.name }}
            </span>
            <el-tag
              :type="getPriorityType(todo.priority)"
              size="small"
              effect="plain"
            >
              {{ getPriorityName(todo.priority) }}
            </el-tag>
          </div>

          <div class="todo-item__meta">
            <span v-if="todo.deadline" class="todo-item__deadline" :class="{ 'text-red-500': isOverdue(todo.deadline) && !todo.completed }">
              <el-icon><Clock /></el-icon>
              截止: {{ formatDate(todo.deadline) }}
            </span>
          </div>

          <div v-if="!todo.completed" class="todo-item__action">
            <el-button type="primary" link size="small" @click="handleAction(todo)">
              立即处理
            </el-button>
          </div>
        </div>
      </div>

      <el-empty v-if="todoList.length === 0" description="暂无待办事项" :image-size="60" />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Clock } from '@element-plus/icons-vue'
import { useCaseWorkflowStore } from '@/lib/stores/caseWorkflow'
import { formatDateTime } from '@/utils/formatter'

const workflowStore = useCaseWorkflowStore()

interface TodoItem {
  id: string
  name: string
  priority: 'high' | 'medium' | 'low'
  deadline?: string
  completed: boolean
  action?: string
}

function getPriorityType(priority: TodoItem['priority']): 'danger' | 'warning' | 'info' {
  const map = { high: 'danger', medium: 'warning', low: 'info' }
  return map[priority]
}

function getPriorityName(priority: TodoItem['priority']): string {
  const map = { high: '高', medium: '中', low: '低' }
  return map[priority]
}

function formatDate(dateStr: string): string {
  return formatDateTime(dateStr)
}

function isOverdue(dateStr: string): boolean {
  return new Date(dateStr) < new Date()
}

function handleToggle(id: string, val: boolean) {
  const item = todoList.value.find(t => t.id === id)
  if (item) item.completed = val
  ElMessage.success(val ? '已标记完成' : '已取消完成')
}

function handleAction(todo: TodoItem) {
  ElMessage.info(`正在处理: ${todo.name}`)
}

const pendingCount = computed(() => todoList.value.filter(t => !t.completed).length)

const todoList = ref<TodoItem[]>([
  {
    id: 'todo-001',
    name: '补充提交证据材料',
    priority: 'high',
    deadline: '2024-01-20T18:00:00Z',
    completed: false,
  },
  {
    id: 'todo-002',
    name: '确认庭审排期时间',
    priority: 'high',
    deadline: '2024-01-22T12:00:00Z',
    completed: false,
  },
  {
    id: 'todo-003',
    name: '核对诉讼请求金额',
    priority: 'medium',
    deadline: '2024-01-25T18:00:00Z',
    completed: true,
  },
  {
    id: 'todo-004',
    name: '签署电子送达回执',
    priority: 'medium',
    deadline: '2024-01-28T18:00:00Z',
    completed: false,
  },
  {
    id: 'todo-005',
    name: '缴纳案件受理费',
    priority: 'low',
    completed: true,
  },
])
</script>

<style scoped>
.current-todo-panel {
  width: 100%;
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  background: #fafafa;
  transition: all 0.2s ease;
}

.todo-item:hover {
  background: #f5f7fa;
  border-color: #dcdfe6;
}

.todo-item--high {
  border-left: 3px solid #f56c6c;
}

.todo-item--completed {
  opacity: 0.65;
}

.todo-item__checkbox {
  padding-top: 2px;
}

.todo-item__content {
  flex: 1;
  min-width: 0;
}

.todo-item__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 6px;
}

.todo-item__name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.todo-item__meta {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 6px;
}

.todo-item__deadline {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.todo-item__action {
  padding-top: 4px;
}
</style>
