<template>
  <div class="version-download-container space-y-6">
    <!-- 版本历史面板 -->
    <el-card shadow="hover" class="version-history-panel">
      <template #header>
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-lg">📜</span>
            <span class="font-semibold text-gray-800">版本历史</span>
            <el-badge :value="versions.length" :max="99" type="primary" />
          </div>

          <div v-if="isAdmin" class="flex items-center gap-2">
            <el-button
              v-if="selectedVersions.length === 2"
              size="small"
              type="warning"
              @click="compareVersions"
            >
              <el-icon><Connection /></el-icon> 对比版本
            </el-button>

            <el-dropdown trigger="click" @command="handleVersionAction">
              <el-button size="small" text type="primary">
                操作
                <el-icon class="ml-1"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="export" :disabled="versions.length === 0">
                    导出版本列表
                  </el-dropdown-item>
                  <el-dropdown-item command="rollback" divided :disabled="!canRollback">
                    ⚠️ 回滚到指定版本
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </template>

      <div v-if="versions.length === 0" class="empty-state py-12 text-center">
        <el-empty description="暂无版本历史记录" :image-size="80" />
      </div>

      <div v-else class="version-timeline relative pl-8 pb-4">
        <!-- 时间轴主线 -->
        <div class="absolute left-[11px] top-2 bottom-4 w-0.5 bg-gradient-to-b from-judicial-400 to-gray-300"></div>

        <div
          v-for="(version, index) in versions"
          :key="version.id"
          class="version-item relative mb-4 group"
        >
          <!-- 版本节点 -->
          <div
            class="absolute -left-8 top-3 w-6 h-6 rounded-full flex items-center justify-center z-10 border-2 transition-all duration-200 cursor-pointer"
            :class="[
              version.isCurrent
                ? 'bg-green-500 border-green-500 shadow-lg shadow-green-200 scale-110'
                : 'bg-white border-gray-300 hover:border-judicial-400 hover:scale-110',
            ]"
            @click="toggleVersionSelect(version)"
          >
            <span v-if="version.isCurrent" class="text-white text-xs font-bold">✓</span>
            <span v-else class="text-gray-400 text-xs font-bold">{{ index + 1 }}</span>
          </div>

          <!-- 连接线 -->
          <div
            v-if="index < versions.length - 1"
            class="absolute -left-8 top-9 w-px min-h-[20px] bg-gray-200"
          ></div>

          <!-- 版本卡片 -->
          <div
            class="version-card bg-white rounded-lg border p-4 transition-all duration-200 hover:shadow-md"
            :class="[
              version.isCurrent ? 'border-green-300 ring-1 ring-green-100' : 'border-gray-200',
              selectedVersions.includes(version.id) ? 'ring-2 ring-judicial-400' : '',
            ]"
          >
            <div class="flex items-start justify-between gap-3">
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-2">
                  <h4 class="font-bold text-lg" :class="version.isCurrent ? 'text-green-700' : 'text-gray-800'">
                    v{{ version.version }}
                  </h4>

                  <el-tag v-if="version.isCurrent" type="success" size="small" effect="dark" round>
                    当前版本
                  </el-tag>

                  <el-tag v-if="version.isFinal" type="warning" size="small" effect="plain" round>
                    最终版
                  </el-tag>

                  <el-checkbox
                    v-if="isAdmin && !version.isCurrent"
                    :model-value="selectedVersions.includes(version.id)"
                    @change="(checked: boolean) => handleVersionCheck(checked, version.id)"
                    class="!ml-2"
                  >
                    对比
                  </el-checkbox>
                </div>

                <p class="text-sm text-gray-600 mb-3">{{ version.changeDescription }}</p>

                <div class="meta-info flex items-center gap-4 text-xs text-gray-400 flex-wrap">
                  <span class="flex items-center gap-1">
                    <el-icon><User /></el-icon>
                    {{ version.createdBy }}
                  </span>
                  <span class="flex items-center gap-1">
                    <el-icon><Clock /></el-icon>
                    {{ formatDateTime(version.createdAt) }}
                  </span>
                  <span class="flex items-center gap-1">
                    <el-icon><Document /></el-icon>
                    {{ formatFileSize(version.fileSize) }}
                  </span>
                </div>
              </div>

              <div class="action-buttons flex flex-col gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                <el-tooltip content="预览此版本" placement="top">
                  <el-button circle size="small" @click="previewVersion(version)">
                    <el-icon><View /></el-icon>
                  </el-button>
                </el-tooltip>

                <el-tooltip content="下载此版本" placement="top">
                  <el-button circle size="small" type="primary" @click="downloadVersion(version)">
                    <el-icon><Download /></el-icon>
                  </el-button>
                </el-tooltip>

                <el-tooltip
                  v-if="isAdmin && !version.isCurrent"
                  content="回滚到此版本"
                  placement="top"
                >
                  <el-button circle size="small" type="warning" @click="confirmRollback(version)">
                    <el-icon><RefreshRight /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 下载中心面板 -->
    <el-card shadow="hover" class="download-center-panel">
      <template #header>
        <div class="flex items-center gap-2">
          <span class="text-lg">📦</span>
          <span class="font-semibold text-gray-800">下载中心</span>
        </div>
      </template>

      <div class="download-grid grid grid-cols-2 md:grid-cols-4 gap-4">
        <div
          v-for="format in downloadFormats"
          :key="format.type"
          class="download-card bg-white rounded-xl border-2 p-5 text-center cursor-pointer transition-all duration-300 hover:-translate-y-1 hover:shadow-lg group"
          :class="[
            format.available ? 'border-gray-200 hover:border-judicial-400' : 'border-gray-100 opacity-60 cursor-not-allowed',
          ]"
          @click="format.available ? handleDownload(format.type) : null"
        >
          <div
            class="icon-wrapper w-16 h-16 mx-auto rounded-xl flex items-center justify-center mb-3 transition-all duration-300"
            :class="[
              format.available
                ? `bg-${format.color}-50 group-hover:bg-${format.color}-100`
                : 'bg-gray-100',
            ]"
            :style="{ backgroundColor: format.available ? format.bgColor : '#f3f4f6' }"
          >
            <span class="text-3xl">{{ format.icon }}</span>
          </div>

          <h4 class="font-semibold text-gray-800 mb-1">{{ format.label }}</h4>
          <p class="text-xs text-gray-500 mb-2">{{ format.description }}</p>

          <div class="file-meta flex items-center justify-center gap-2 text-xs text-gray-400">
            <span>{{ format.fileSize }}</span>
            <span v-if="format.updatedAt" class="text-gray-300">|</span>
            <span v-if="format.updatedAt">{{ format.updatedAt }}</span>
          </div>

          <el-button
            v-if="format.available"
            class="mt-3 w-full"
            size="small"
            :type="format.primary ? 'primary' : 'default'"
            @click.stop="handleDownload(format.type)"
          >
            <el-icon><Download /></el-icon>
            下载
          </el-button>

          <el-button
            v-else
            class="mt-3 w-full"
            size="small"
            disabled
          >
            暂不可用
          </el-button>
        </div>
      </div>

      <!-- 批量下载区域 -->
      <div class="batch-download mt-6 p-4 bg-gradient-to-r from-slate-50 to-gray-50 rounded-lg border border-gray-200">
        <div class="flex items-center justify-between flex-wrap gap-4">
          <div class="flex items-center gap-3">
            <el-icon :size="24" class="text-judicial-600"><FolderOpened /></el-icon>
            <div>
              <h4 class="font-medium text-gray-800">一键下载全部格式</h4>
              <p class="text-xs text-gray-500">包含 PDF、Word、TXT 及完整证据包（ZIP）</p>
            </div>
          </div>

          <div class="flex items-center gap-3">
            <div class="total-size text-sm text-gray-600">
              总计约 <span class="font-semibold text-judicial-700">{{ totalDownloadSize }}</span>
            </div>

            <el-button
              type="primary"
              :loading="isBatchDownloading"
              :disabled="isBatchDownloading"
              @click="handleBatchDownload"
            >
              <el-icon v-if="!isBatchDownloading"><Download /></el-icon>
              {{ isBatchDownloading ? `下载中 ${batchProgress}%` : '📥 一键下载全部' }}
            </el-button>
          </div>
        </div>

        <!-- 下载进度条 -->
        <div v-if="isBatchDownloading" class="mt-4">
          <el-progress
            :percentage="batchProgress"
            :stroke-width="8"
            :color="progressColors"
            striped
            striped-flow
          />

          <div class="mt-2 flex items-center justify-between text-xs text-gray-500">
            <span>正在打包：{{ currentPackingFile }}</span>
            <span>预计剩余时间：{{ estimatedTimeRemaining }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 版本对比对话框 -->
    <el-dialog
      v-model="showCompareDialog"
      title="版本对比"
      width="80%"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="compare-container grid grid-cols-2 gap-4">
        <div class="compare-side">
          <div class="side-header bg-blue-50 px-4 py-2 rounded-t-lg font-medium text-blue-800">
            版本 v{{ compareData.oldVersion?.version }} (旧版本)
          </div>
          <div class="side-content border border-t-0 rounded-b-lg p-4 max-h-[500px] overflow-auto font-mono text-sm whitespace-pre-wrap bg-gray-50">
            {{ compareData.oldContent || '加载中...' }}
          </div>
        </div>

        <div class="compare-side">
          <div class="side-header bg-green-50 px-4 py-2 rounded-t-lg font-medium text-green-800">
            版本 v{{ compareData.newVersion?.version }} (新版本)
          </div>
          <div class="side-content border border-t-0 rounded-b-lg p-4 max-h-[500px] overflow-auto font-mono text-sm whitespace-pre-wrap bg-gray-50">
            {{ compareData.newContent || '加载中...' }}
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="showCompareDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  Connection,
  User,
  Clock,
  Document,
  View,
  Download,
  RefreshRight,
  FolderOpened,
} from '@element-plus/icons-vue'
import { useDocumentWorkflowStore } from '@/lib/stores/documentWorkflow'

const documentStore = useDocumentWorkflowStore()

interface VersionItem {
  id: string
  version: number
  createdAt: string
  createdBy: string
  changeDescription: string
  fileUrl: string
  fileSize: number
  isCurrent?: boolean
  isFinal?: boolean
}

interface DownloadFormat {
  type: string
  icon: string
  label: string
  description: string
  fileSize: string
  updatedAt: string
  available: boolean
  primary: boolean
  color: string
  bgColor: string
}

interface Props {
  /** 是否为管理员 */
  isAdmin?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isAdmin: false,
})

const emit = defineEmits<{
  (e: 'download', format: string): void
  (e: 'preview', versionId: string): void
  (e: 'rollback', versionId: string): void
}>()

const versions = ref<VersionItem[]>([])
const selectedVersions = ref<string[]>([])
const showCompareDialog = ref(false)
const isBatchDownloading = ref(false)
const batchProgress = ref(0)
const currentPackingFile = ref('')

const compareData = ref<{
  oldVersion?: VersionItem
  newVersion?: VersionItem
  oldContent?: string
  newContent?: string
}>({})

const progressColors = [
  { color: '#2563eb', percentage: 30 },
  { color: '#d97706', percentage: 70 },
  { color: '#059669', percentage: 100 },
]

const downloadFormats = computed<DownloadFormat[]>(() => [
  {
    type: 'PDF',
    icon: '📄',
    label: 'PDF 原文',
    description: '最终版 PDF 文档',
    fileSize: documentStore.currentDocument?.fileSize
      ? formatFileSize(documentStore.currentDocument.fileSize)
      : '2.5MB',
    updatedAt: '刚刚更新',
    available: true,
    primary: true,
    color: 'red',
    bgColor: '#fef2f2',
  },
  {
    type: 'WORD',
    icon: '📝',
    label: 'Word 版本',
    description: '可编辑 Word 文档',
    fileSize: '1.8MB',
    updatedAt: '今天 14:20',
    available: true,
    primary: false,
    color: 'blue',
    bgColor: '#eff6ff',
  },
  {
    type: 'TXT',
    icon: '💾',
    label: '纯文本',
    description: '提取的文本内容',
    fileSize: '50KB',
    updatedAt: '今天 14:20',
    available: true,
    primary: false,
    color: 'gray',
    bgColor: '#f9fafb',
  },
  {
    type: 'ZIP',
    icon: '📦',
    label: '证据包',
    description: '文书+签名凭证+审计日志',
    fileSize: '3.1MB',
    updatedAt: '今天 14:35',
    available: true,
    primary: false,
    color: 'purple',
    bgColor: '#f5f3ff',
  },
])

const canRollback = computed(() => props.isAdmin && versions.value.some(v => !v.isCurrent))

const totalDownloadSize = computed(() => {
  const totalBytes = downloadFormats.value.reduce((sum, fmt) => {
    if (!fmt.available) return sum
    const sizeStr = fmt.fileSize.replace(/[^\d.]/g, '')
    const unit = fmt.fileSize.replace(/[\d.]/g, '').trim()
    let multiplier = 1

    switch (unit.toUpperCase()) {
      case 'KB': multiplier = 1024; break
      case 'MB': multiplier = 1024 * 1024; break
      case 'GB': multiplier = 1024 * 1024 * 1024; break
    }

    return sum + parseFloat(sizeStr) * multiplier
  }, 0)

  if (totalBytes > 1024 * 1024) {
    return `${(totalBytes / (1024 * 1024)).toFixed(1)} MB`
  }
  return `${Math.round(totalBytes / 1024)} KB`
})

const estimatedTimeRemaining = computed(() => {
  if (!isBatchDownloading.value) return ''

  const remaining = 100 - batchProgress.value
  const seconds = Math.ceil((remaining / 100) * 15)

  if (seconds <= 0) return '即将完成'
  if (seconds < 60) return `约 ${seconds} 秒`

  return `约 ${Math.ceil(seconds / 60)} 分钟`
})

onMounted(() => {
  loadVersions()
})

function loadVersions(): void {
  // 使用 store 数据或生成模拟数据
  if (documentStore.versions && documentStore.versions.length > 0) {
    versions.value = documentStore.versions.map(v => ({
      ...v,
      id: `v${v.version}`,
      isCurrent: v.version === Math.max(...documentStore.versions.map(vv => vv.version)),
    }))
  } else {
    versions.value = generateMockVersions()
  }
}

function generateMockVersions(): VersionItem[] {
  const now = new Date()
  return [
    {
      id: 'v2.0',
      version: 2.0,
      createdAt: new Date(now.getTime() - 5 * 60 * 1000).toISOString(),
      createdBy: '法官张三',
      changeDescription: '发布最终版判决书，完成所有审核流程',
      fileUrl: '/documents/final.pdf',
      fileSize: 2621440,
      isCurrent: true,
      isFinal: true,
    },
    {
      id: 'v1.1',
      version: 1.1,
      createdAt: new Date(now.getTime() - 24 * 60 * 60 * 1000).toISOString(),
      createdBy: 'AI助手',
      changeDescription: '根据庭审记录修正事实描述部分，补充证据引用',
      fileUrl: '/documents/v1.1.pdf',
      fileSize: 2457600,
    },
    {
      id: 'v1.0',
      version: 1.0,
      createdAt: new Date(now.getTime() - 48 * 60 * 60 * 1000).toISOString(),
      createdBy: '系统自动',
      changeDescription: '从模板「民事判决书」初始生成，基于案件信息填充变量',
      fileUrl: '/documents/v1.0.pdf',
      fileSize: 2300000,
    },
  ]
}

function toggleVersionSelect(version: VersionItem): void {
  if (!props.isAdmin || version.isCurrent) return

  const idx = selectedVersions.value.indexOf(version.id)
  if (idx >= 0) {
    selectedVersions.value.splice(idx, 1)
  } else if (selectedVersions.value.length < 2) {
    selectedVersions.value.push(version.id)
  } else {
    selectedVersions.value.shift()
    selectedVersions.value.push(version.id)
  }
}

function handleVersionCheck(checked: boolean, versionId: string): void {
  if (checked) {
    if (selectedVersions.value.length < 2) {
      selectedVersions.value.push(versionId)
    } else {
      selectedVersions.value.shift()
      selectedVersions.value.push(versionId)
    }
  } else {
    const idx = selectedVersions.value.indexOf(versionId)
    if (idx >= 0) {
      selectedVersions.value.splice(idx, 1)
    }
  }
}

async function compareVersions(): Promise<void> {
  if (selectedVersions.value.length !== 2) {
    ElMessage.warning('请选择两个版本进行对比')
    return
  }

  const oldVer = versions.value.find(v => v.id === selectedVersions.value[0])
  const newVer = versions.value.find(v => v.id === selectedVersions.value[1])

  if (!oldVer || !newVer) return

  compareData.value = {
    oldVersion: oldVer,
    newVersion: newVer,
    oldContent: generateMockDiffContent(oldVer),
    newContent: generateMockDiffContent(newVer),
  }

  showCompareDialog.value = true
}

function generateMockDiffContent(version: VersionItem): string {
  return `
【${documentStore.currentDocument?.title || '司法文书'}】
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
版本：v${version.version}
修改人：${version.createdBy}
修改时间：${formatDateTime(version.createdAt)}
变更说明：${version.changeDescription}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

【正文内容示例】

原告：张三，男，1985年3月15日出生，汉族，
住址：某某市某某区某某路123号。

被告：李四，女，1988年7月22日出生，汉族，
住址：某某市某某区某某路456号。

案由：合同纠纷

【诉讼请求】

一、判令被告向原告支付货款人民币50万元；
二、判令被告支付逾期付款利息（自2024年1月1日起至实际付清之日止）；
三、本案诉讼费用由被告承担。

【事实与理由】

（此处为文档正文内容，实际使用时将从服务器获取真实内容）

...
`.trim()
}

function previewVersion(version: VersionItem): void {
  emit('preview', version.id)
  ElMessage.info(`预览版本 v${version.version}`)
}

function downloadVersion(version: VersionItem): void {
  try {
    // 实际项目中调用 API 下载特定版本
    const link = document.createElement('a')
    link.href = version.fileUrl
    link.download = `文档_v${version.version}.pdf`
    link.click()

    ElMessage.success(`开始下载版本 v${version.version}`)
  } catch (err) {
    ElMessage.error('下载失败，请重试')
    console.error('Download error:', err)
  }
}

async function confirmRollback(version: VersionItem): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `确定要将文档回滚到版本 v${version.version} 吗？\n\n⚠️ 此操作不可撤销，当前版本将被覆盖。\n\n变更说明：${version.changeDescription}`,
      '确认回滚操作',
      {
        confirmButtonText: '确认回滚',
        cancelButtonText: '取消',
        type: 'error',
        confirmButtonClass: 'el-button--danger',
      }
    )

    emit('rollback', version.id)
    ElMessage.success(`已发起回滚到版本 v${version.version} 的请求`)
  } catch {
    // 用户取消
  }
}

function handleVersionAction(command: string): void {
  switch (command) {
    case 'export':
      exportVersionList()
      break
    case 'rollback':
      if (versions.value.length > 1 && !versions.value[0].isCurrent) {
        confirmRollback(versions.value[1])
      } else {
        ElMessage.info('没有可回滚的历史版本')
      }
      break
  }
}

function exportVersionList(): void {
  const csvContent = [
    ['版本号', '创建时间', '操作人', '变更说明', '文件大小'],
    ...versions.value.map(v => [
      `v${v.version}`,
      formatDateTime(v.createdAt),
      v.createdBy,
      v.changeDescription,
      formatFileSize(v.fileSize),
    ]),
  ].map(row => row.join(',')).join('\n')

  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `版本历史_${new Date().toISOString().split('T')[0]}.csv`
  link.click()

  ElMessage.success('版本列表已导出')
}

function handleDownload(format: string): void {
  emit('download', format)

  switch (format) {
    case 'PDF':
      documentStore.downloadPdf()
      ElMessage.success('开始下载 PDF 文档')
      break
    case 'WORD':
      ElMessage.info('Word 格式下载功能开发中...')
      break
    case 'TXT':
      ElMessage.info('纯文本格式下载功能开发中...')
      break
    case 'ZIP':
      ElMessage.info('证据包打包下载中...')
      break
    default:
      ElMessage.warning(`不支持的格式: ${format}`)
  }
}

async function handleBatchDownload(): Promise<void> {
  isBatchDownloading.value = true
  batchProgress.value = 0

  const files = downloadFormats.value.filter(f => f.available)

  for (let i = 0; i < files.length; i++) {
    currentPackingFile.value = files[i].label

    for (let progress = 0; progress <= 25; progress += 5) {
      await new Promise(resolve => setTimeout(resolve, 100))
      batchProgress.value = Math.round(((i * 25 + progress) / files.length) * 100)
    }
  }

  batchProgress.value = 100
  currentPackingFile.value = '打包完成'

  setTimeout(() => {
    isBatchDownloading.value = false
    batchProgress.value = 0
    currentPackingFile.value = ''
    ElMessage.success('🎉 全部文件下载/打包完成！')
  }, 1500)
}

function formatDateTime(isoString: string): string {
  const date = new Date(isoString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')

  return `${year}-${month}-${day} ${hours}:${minutes}`
}

function formatFileSize(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  if (bytes < 1024 * 1024 * 1024) return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
  return `${(bytes / (1024 * 1024 * 1024)).toFixed(2)} GB`
}
</script>

<style scoped>
.version-download-container {
  width: 100%;
}

.version-history-panel,
.download-center-panel {
  width: 100%;
}

.version-item {
  animation: slide-in-right 0.3s ease-out forwards;
  opacity: 0;
}

.version-item:nth-child(1) { animation-delay: 0ms; }
.version-item:nth-child(2) { animation-delay: 80ms; }
.version-item:nth-child(3) { animation-delay: 160ms; }

@keyframes slide-in-right {
  from {
    opacity: 0;
    transform: translateX(-12px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.download-card {
  position: relative;
  overflow: hidden;
}

.download-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--el-color-primary), var(--el-color-success));
  opacity: 0;
  transition: opacity 0.3s;
}

.download-card:hover::before {
  opacity: 1;
}

.icon-wrapper {
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.download-card:hover .icon-wrapper {
  transform: scale(1.1) rotate(-3deg);
}

.compare-container {
  min-height: 400px;
}

.side-header {
  font-weight: 600;
}

.side-content {
  line-height: 1.6;
}
</style>
