<template>
  <el-card shadow="hover" class="pdf-preview-panel">
    <template #header>
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="text-lg">📄</span>
          <span class="font-semibold text-gray-800">文书预览</span>
        </div>

        <el-radio-group v-model="previewMode" size="small" @change="handleModeChange">
          <el-radio-button value="pdf">PDF 模式</el-radio-button>
          <el-radio-button value="html">富文本模式</el-radio-button>
        </el-radio-group>
      </div>
    </template>

    <div v-if="!pdfUrl && !documentStore.currentDocument?.fileUrl && !hasContent" class="empty-state py-16">
      <el-empty description="暂无预览内容" :image-size="80" />
    </div>

    <div v-else class="preview-container">
      <div v-if="isLoading" class="loading-skeleton min-h-[600px] flex items-center justify-center">
        <div class="skeleton-content w-full max-w-2xl space-y-4 p-6">
          <div class="skeleton-line h-4 bg-gray-200 rounded animate-pulse w-full"></div>
          <div class="skeleton-line h-4 bg-gray-200 rounded animate-pulse w-5/6"></div>
          <div class="skeleton-line h-4 bg-gray-200 rounded animate-pulse w-4/5"></div>
          <div class="skeleton-line h-32 bg-gray-200 rounded animate-pulse w-full mt-6"></div>
          <div class="skeleton-line h-4 bg-gray-200 rounded animate-pulse w-3/4"></div>
          <div class="skeleton-line h-4 bg-gray-200 rounded animate-pulse w-full"></div>
          <div class="skeleton-line h-20 bg-gray-200 rounded animate-pulse w-full mt-4"></div>
        </div>
      </div>

      <div v-else-if="hasError" class="error-state py-12 text-center">
        <el-result icon="error" title="加载失败" :sub-title="errorMessage">
          <template #extra>
            <el-button type="primary" @click="retryLoad">重新加载</el-button>
          </template>
        </el-result>
      </div>

      <template v-else>
        <div class="toolbar sticky top-0 z-10 bg-white border-b border-gray-100 px-4 py-3 -mx-4 mb-4 flex items-center justify-between gap-3 flex-wrap">
          <div class="flex items-center gap-2">
            <el-tooltip content="缩小 (−)" placement="top">
              <el-button circle size="small" :disabled="currentScale <= 50" @click="zoomOut">
                <el-icon><ZoomOut /></el-icon>
              </el-button>
            </el-tooltip>

            <span class="text-sm font-medium min-w-[60px] text-center">{{ currentScale }}%</span>

            <el-tooltip content="放大 (+)" placement="top">
              <el-button circle size="small" :disabled="currentScale >= 300" @click="zoomIn">
                <el-icon><ZoomIn /></el-icon>
              </el-button>
            </el-tooltip>

            <el-divider direction="vertical" />

            <el-tooltip content="适应宽度" placement="top">
              <el-button size="small" @click="fitWidth">
                <el-icon><FullScreen /></el-icon> 适应宽度
              </el-button>
            </el-tooltip>

            <el-tooltip content="适应页面" placement="top">
              <el-button size="small" @click="fitPage">
                <el-icon><ScaleToOriginal /></el-icon> 适应页面
              </el-button>
            </el-tooltip>
          </div>

          <div v-if="previewMode === 'pdf'" class="flex items-center gap-2">
            <el-tooltip content="上一页" placement="top">
              <el-button circle size="small" :disabled="currentPage <= 1" @click="prevPage">
                <el-icon><ArrowLeft /></el-icon>
              </el-button>
            </el-tooltip>

            <div class="page-input-wrapper flex items-center gap-1 px-2 py-1 border border-gray-200 rounded">
              <input
                ref="pageInputRef"
                v-model.number="pageInputValue"
                type="number"
                class="page-input w-10 text-center text-sm outline-none"
                min="1"
                :max="totalPages || 9999"
                @keyup.enter="jumpToPage"
                @blur="jumpToPage"
              />
              <span class="text-xs text-gray-400">/ {{ totalPages || '--' }}</span>
            </div>

            <el-tooltip content="下一页" placement="top">
              <el-button circle size="small" :disabled="totalPages > 0 && currentPage >= totalPages" @click="nextPage">
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </el-tooltip>
          </div>

          <div class="flex items-center gap-2">
            <el-tooltip content="全屏查看" placement="top">
              <el-button circle size="small" @click="toggleFullscreen">
                <el-icon><FullScreen /></el-icon>
              </el-button>
            </el-tooltip>

            <el-divider direction="vertical" />

            <el-tooltip content="打印文书" placement="top">
              <el-button circle size="small" type="warning" @click="handlePrint">
                <el-icon><Printer /></el-icon>
              </el-button>
            </el-tooltip>

            <el-tooltip content="下载 PDF" placement="top">
              <el-button circle size="small" type="primary" @click="handleDownload">
                <el-icon><Download /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
        </div>

        <div
          ref="previewAreaRef"
          class="preview-area relative overflow-hidden rounded-lg border border-gray-200"
          :class="{ 'fullscreen-mode': isFullscreen }"
          :style="{ minHeight: '600px' }"
        >
          <iframe
            v-if="previewMode === 'pdf' && currentPdfUrl"
            ref="iframeRef"
            :src="currentPdfUrl"
            class="w-full border-0 transition-all duration-300"
            :style="{
              height: iframeHeight,
              transform: `scale(${currentScale / 100})`,
              transformOrigin: 'top left',
              width: `${10000 / currentScale}%`,
            }"
            @load="handleIframeLoad"
            @error="handleIframeError"
          ></iframe>

          <div
            v-else-if="previewMode === 'html' && htmlContent"
            class="html-content-wrapper p-8 overflow-auto"
            style="max-height: 800px;"
            v-html="htmlContent"
          ></div>
        </div>

        <div v-if="previewMode === 'pdf' && totalPages > 0" class="page-info mt-3 text-center text-sm text-gray-500">
          第 <span class="font-medium text-judicial-700">{{ currentPage }}</span> 页 / 共 <span class="font-medium">{{ totalPages }}</span> 页
        </div>
      </template>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  ZoomIn,
  ZoomOut,
  FullScreen,
  ScaleToOriginal,
  ArrowLeft,
  ArrowRight,
  Printer,
  Download,
} from '@element-plus/icons-vue'
import { useDocumentWorkflowStore } from '@/lib/stores/documentWorkflow'

const documentStore = useDocumentWorkflowStore()

interface Props {
  /** HTML 富文本内容（可选） */
  htmlContent?: string
}

const props = withDefaults(defineProps<Props>(), {
  htmlContent: '',
})

const previewMode = ref<'pdf' | 'html'>('pdf')
const currentScale = ref(100)
const currentPage = ref(1)
const totalPages = ref(0)
const isLoading = ref(false)
const hasError = ref(false)
const errorMessage = ref('')
const isFullscreen = ref(false)
const pageInputValue = ref(1)

const iframeRef = ref<HTMLIFrameElement>()
const pageInputRef = ref<HTMLInputElement>()
const previewAreaRef = ref<HTMLElement>()

const pdfUrl = computed(() => documentStore.pdfUrl)

const currentPdfUrl = computed(() => {
  if (previewMode.value !== 'pdf') return ''
  return documentStore.getPdfPreviewUrl() || documentStore.currentDocument?.fileUrl || ''
})

const hasContent = computed(() => !!props.htmlContent)

const iframeHeight = computed(() => {
  const baseHeight = isFullscreen.value ? window.innerHeight - 120 : 750
  return `${baseHeight}px`
})

watch(previewMode, () => {
  hasError.value = false
  errorMessage.value = ''
})

watch(currentPdfUrl, (newUrl) => {
  if (newUrl) {
    loadPdf(newUrl)
  }
}, { immediate: true })

function handleModeChange(mode: 'pdf' | 'html') {
  previewMode.value = mode
}

function loadPdf(url: string): void {
  isLoading.value = true
  hasError.value = false

  setTimeout(() => {
    isLoading.value = false
  }, 800)
}

function handleIframeLoad(): void {
  isLoading.value = false

  try {
    if (iframeRef.value?.contentWindow?.document) {
      const doc = iframeRef.value.contentWindow.document
      const pageCount = doc.querySelectorAll('.page').length || 0
      if (pageCount > 0) {
        totalPages.value = pageCount
      }
    }
  } catch {
    // 跨域情况下无法访问 iframe 内容，使用默认值
    totalPages.value = 1
  }

  ElMessage.success('文档加载完成')
}

function handleIframeError(): void {
  isLoading.value = false
  hasError.value = true
  errorMessage.value = 'PDF 文档加载失败，请检查文件是否存在或网络连接'
}

function retryLoad(): void {
  hasError.value = false
  errorMessage.value = ''
  if (currentPdfUrl.value) {
    loadPdf(currentPdfUrl.value)
  }
}

function zoomIn(): void {
  currentScale.value = Math.min(currentScale.value + 25, 300)
}

function zoomOut(): void {
  currentScale.value = Math.max(currentScale.value - 25, 50)
}

function fitWidth(): void {
  currentScale.value = 100
  ElMessage.info('已切换至适应宽度')
}

function fitPage(): void {
  currentScale.value = 100
  ElMessage.info('已切换至适应页面')
}

function prevPage(): void {
  if (currentPage.value > 1) {
    currentPage.value--
    pageInputValue.value = currentPage.value
  }
}

function nextPage(): void {
  if (totalPages.value > 0 && currentPage.value < totalPages.value) {
    currentPage.value++
    pageInputValue.value = currentPage.value
  }
}

function jumpToPage(): void {
  let targetPage = parseInt(String(pageInputValue.value), 10)
  if (isNaN(targetPage)) targetPage = 1
  targetPage = Math.max(1, Math.min(targetPage, totalPages.value || 9999))
  currentPage.value = targetPage
  pageInputValue.value = targetPage
}

function toggleFullscreen(): void {
  isFullscreen.value = !isFullscreen.value

  nextTick(() => {
    if (isFullscreen.value && previewAreaRef.value) {
      if (previewAreaRef.value.requestFullscreen) {
        previewAreaRef.value.requestFullscreen()
      }
    } else {
      if (document.exitFullscreen) {
        document.exitFullscreen()
      }
    }
  })
}

function handlePrint(): void {
  try {
    if (previewMode.value === 'pdf' && iframeRef.value?.contentWindow) {
      iframeRef.value.contentWindow.print()
    } else {
      window.print()
    }
  } catch (err) {
    ElMessage.error('打印功能暂时不可用')
    console.error('Print error:', err)
  }
}

function handleDownload(): void {
  try {
    documentStore.downloadPdf()
    ElMessage.success('开始下载 PDF 文档')
  } catch (err) {
    ElMessage.error('下载失败，请重试')
    console.error('Download error:', err)
  }
}

function handleKeydown(e: KeyboardEvent): void {
  if (e.ctrlKey || e.metaKey) {
    switch (e.key) {
      case '=':
      case '+':
        e.preventDefault()
        zoomIn()
        break
      case '-':
        e.preventDefault()
        zoomOut()
        break
      case 'p':
        e.preventDefault()
        handlePrint()
        break
    }
  }

  if (!e.ctrlKey && !e.metaKey) {
    switch (e.key) {
      case 'ArrowLeft':
        prevPage()
        break
      case 'ArrowRight':
        nextPage()
        break
    }
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
  document.addEventListener('fullscreenchange', () => {
    isFullscreen.value = !!document.fullscreenElement
  })
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.pdf-preview-panel {
  width: 100%;
}

.preview-container {
  position: relative;
}

.loading-skeleton {
  background: linear-gradient(90deg, #f9fafb 25%, #f3f4f6 50%, #f9fafb 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.skeleton-line {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.page-input {
  -moz-appearance: textfield;
}

.page-input::-webkit-inner-spin-button,
.page-input::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.page-input:focus {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px rgba(var(--el-color-primary-rgb), 0.2);
}

.fullscreen-mode {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  background: white;
  padding: 1rem;
  border-radius: 0;
}

.html-content-wrapper {
  line-height: 1.8;
  color: #303133;
  font-family: 'Noto Serif SC', 'Source Han Serif SC', SimSun, serif;
  font-size: 15px;
}

.html-content-wrapper :deep(h1),
.html-content-wrapper :deep(h2),
.html-content-wrapper :deep(h3) {
  color: #1f2937;
  margin-top: 1.5rem;
  margin-bottom: 0.75rem;
  font-weight: 600;
}

.html-content-wrapper :deep(p) {
  text-indent: 2em;
  margin-bottom: 0.75rem;
}
</style>
