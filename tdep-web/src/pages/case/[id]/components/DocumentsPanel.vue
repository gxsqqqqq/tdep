<template>
  <el-card shadow="hover" class="documents-panel">
    <template #header>
      <div class="flex items-center justify-between flex-wrap gap-3">
        <div class="flex items-center gap-2">
          <span class="text-lg">📄</span>
          <span class="font-semibold text-gray-800">法律文书</span>
          <el-badge :value="filteredDocuments.length" :max="99" class="ml-1" />
        </div>

        <el-radio-group v-model="activeCategory" size="small">
          <el-radio-button value="all">全部</el-radio-button>
          <el-radio-button value="plaintiff">起诉状类</el-radio-button>
          <el-radio-button value="defense">答辩状类</el-radio-button>
          <el-radio-button value="judgement">判决书类</el-radio-button>
        </el-radio-group>
      </div>
    </template>

    <div v-if="filteredDocuments.length === 0" class="py-8">
      <el-empty description="暂无该分类的文书" :image-size="60" />
    </div>

    <div v-else class="documents-grid">
      <div
        v-for="doc in filteredDocuments"
        :key="doc.id"
        class="document-card"
      >
        <div class="document-card__icon-wrap" :style="{ background: getDocTypeColor(doc.type) }">
          <el-icon :size="28" color="#fff"><component :is="getDocIcon(doc.type)" /></el-icon>
        </div>

        <div class="document-card__body">
          <div class="document-card__title">{{ doc.title }}</div>
          <div class="document-card__type-tag">
            <el-tag :type="getDocTagType(doc.type)" size="small" effect="plain">
              {{ getDocTypeName(doc.type) }}
            </el-tag>
          </div>

          <div class="document-card__meta">
            <span class="text-xs text-gray-400">{{ formatDate(doc.createdAt) }}</span>
            <el-tag :type="getDocStatusType(doc.status)" size="small">
              {{ getDocStatusName(doc.status) }}
            </el-tag>
          </div>

          <div class="document-card__actions">
            <el-button size="small" text type="primary" @click="handlePreview(doc)">
              <el-icon><View /></el-icon> 预览
            </el-button>
            <el-button size="small" text type="primary" @click="handleDownload(doc)">
              <el-icon><Download /></el-icon> 下载
            </el-button>
            <el-button
              size="small"
              text
              type="success"
              :disabled="doc.status === 'signed'"
              @click="handleSign(doc)"
            >
              <el-icon><EditPen /></el-icon> 签字
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { View, Download, EditPen, Document, Reading, Files, NoteBook } from '@element-plus/icons-vue'
import { useCaseWorkflowStore } from '@/lib/stores/caseWorkflow'
import { formatDateTime } from '@/utils/formatter'

const workflowStore = useCaseWorkflowStore()

interface DocumentCard {
  id: string
  title: string
  type: 'complaint' | 'defense' | 'judgement' | 'other'
  status: 'draft' | 'published' | 'received'
  createdAt: string
}

const activeCategory = ref<'all' | 'plaintiff' | 'defense' | 'judgement'>('all')

function getCategoryMap(): Record<string, DocumentCard['type'][]> {
  return {
    all: [],
    plaintiff: ['complaint'],
    defense: ['defense'],
    judgement: ['judgement'],
  }
}

const filteredDocuments = computed(() => {
  const map = getCategoryMap()
  const types = map[activeCategory.value]
  if (types.length === 0) return documents.value
  return documents.value.filter(d => types.includes(d.type))
})

function getDocIcon(type: DocumentCard['type']): any {
  const iconMap: Record<string, any> = {
    complaint: Document,
    defense: Reading,
    judgement: NoteBook,
    other: Files,
  }
  return iconMap[type] || Files
}

function getDocTypeColor(type: DocumentCard['type']): string {
  const colorMap: Record<string, string> = {
    complaint: '#409eff',
    defence: '#e6a23c',
    defense: '#e6a23c',
    judgement: '#67c23a',
    other: '#909399',
  }
  return colorMap[type] || '#909399'
}

function getDocTagType(type: DocumentCard['type']): '' | 'warning' | 'success' | 'info' {
  const map: Record<string, '' | 'warning' | 'success' | 'info'> = {
    complaint: '',
    defense: 'warning',
    judgement: 'success',
    other: 'info',
  }
  return map[type] || 'info'
}

function getDocTypeName(type: DocumentCard['type']): string {
  const map: Record<string, string> = {
    complaint: '起诉状',
    defense: '答辩状',
    judgement: '判决书',
    other: '其他文书',
  }
  return map[type] || '未知'
}

function getDocStatusType(status: DocumentCard['status']): 'info' | 'warning' | 'success' {
  const map = { draft: 'info', published: 'warning', received: 'success' }
  return map[status]
}

function getDocStatusName(status: DocumentCard['status']): string {
  const map = { draft: '草案', published: '已发布', received: '已签收' }
  return map[status]
}

function formatDate(dateStr: string): string {
  return formatDateTime(dateStr)
}

function handlePreview(doc: DocumentCard) {
  ElMessage.info(`预览文书: ${doc.title}`)
}

function handleDownload(doc: DocumentCard) {
  ElMessage.success(`开始下载: ${doc.title}`)
}

function handleSign(doc: DocumentCard) {
  ElMessage.info(`进入签字流程: ${doc.title}`)
}

const documents = ref<DocumentCard[]>([
  {
    id: 'doc-001',
    title: '民事起诉状（合同纠纷）',
    type: 'complaint',
    status: 'published',
    createdAt: '2024-01-11T10:00:00Z',
  },
  {
    id: 'doc-002',
    title: '证据清单及说明',
    type: 'complaint',
    status: 'published',
    createdAt: '2024-01-12T14:30:00Z',
  },
  {
    id: 'doc-003',
    title: '民事答辩状',
    type: 'defense',
    status: 'draft',
    createdAt: '2024-01-18T09:15:00Z',
  },
  {
    id: 'doc-004',
    title: '管辖权异议申请书',
    type: 'defense',
    status: 'received',
    createdAt: '2024-01-20T16:00:00Z',
  },
  {
    id: 'doc-005',
    title: '民事判决书（一审）',
    type: 'judgement',
    status: 'draft',
    createdAt: '2024-03-15T11:00:00Z',
  },
])
</script>

<style scoped>
.documents-panel {
  width: 100%;
}

.documents-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.document-card {
  border: 1px solid #e4e7ed;
  border-radius: 10px;
  padding: 18px;
  background: #fff;
  transition: all 0.25s ease;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.document-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border-color: #c0c4cc;
  transform: translateY(-2px);
}

.document-card__icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.document-card__body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.document-card__title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
}

.document-card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 8px;
  border-top: 1px solid #f2f3f5;
}

.document-card__actions {
  display: flex;
  gap: 4px;
}
</style>
