<template>
  <el-card class="evidence-upload-panel" shadow="hover">
    <template #header>
      <div class="panel-header">
        <span class="panel-title">📤 证据上传与存证</span>
        <span class="panel-subtitle">支持文件校验与区块链固化存证</span>
      </div>
    </template>

    <div class="upload-section">
      <div
        ref="dropZoneRef"
        class="drop-zone"
        :class="{ 'is-dragover': isDragOver }"
        @click="triggerFileInput"
        @dragenter.prevent="handleDragEnter"
        @dragover.prevent="handleDragOver"
        @dragleave.prevent="handleDragLeave"
        @drop.prevent="handleDrop"
      >
        <div class="drop-zone-content">
          <el-icon :size="48" class="upload-icon">
            <Upload />
          </el-icon>
          <div class="drop-zone-text">
            <p class="main-text">{{ isDragOver ? '释放以上传' : '拖拽文件到此处或点击选择' }}</p>
            <p class="sub-text">支持 PDF、JPG、PNG、DOC、DOCX、XLS、XLSX、MP4、MP3 格式，单文件最大 100MB</p>
          </div>
        </div>
        <input
          ref="fileInputRef"
          type="file"
          multiple
          :accept="acceptedFileTypes.join(',')"
          @change="handleFileSelect"
          style="display: none"
        />
      </div>
    </div>

    <div v-if="uploadingFiles.length > 0" class="upload-progress-list">
      <h3 class="section-title">上传进度</h3>
      <div
        v-for="file in uploadingFiles"
        :key="file.name"
        class="progress-item"
      >
        <div class="progress-header">
          <el-icon :size="16" class="file-type-icon">
            <component :is="getFileIcon(file.file.type)" />
          </el-icon>
          <span class="file-name">{{ file.file.name }}</span>
          <el-tag :type="getProgressStatusType(file.status)" size="small">
            {{ getProgressStatusText(file.status) }}
          </el-tag>
          <el-button
            v-if="file.status === 'uploading'"
            type="danger"
            text
            size="small"
            @click.stop="cancelUpload(file.name)"
          >
            取消
          </el-button>
        </div>
        <el-progress
          :percentage="file.progress"
          :stroke-width="8"
          :status="file.status === 'failed' ? 'exception' : undefined"
          striped
          :flow="file.status === 'uploading'"
          color="#3b82f6"
        />
        <div class="progress-meta">
          <span>{{ formatFileSize(file.file.size) }}</span>
          <span v-if="file.status === 'uploading' && file.speed">
            {{ file.speed }}
          </span>
          <span v-if="file.status === 'uploading' && file.remainingTime">
            {{ file.remainingTime }}
          </span>
        </div>
      </div>
    </div>

    <div v-if="uploadedEvidences.length > 0" class="evidence-list">
      <h3 class="section-title">已上传证据 ({{ uploadedEvidences.length }})</h3>
      <div class="evidence-grid">
        <div
          v-for="evidence in uploadedEvidences"
          :key="evidence.id"
          class="evidence-card"
          :class="{ 'is-sealed': evidence.status === 'SEALED' }"
        >
          <div class="card-header">
            <el-icon :size="24" class="file-icon">
              <component :is="getFileIcon(evidence.fileType)" />
            </el-icon>
            <div class="file-info">
              <p class="file-name-text">{{ evidence.fileName }}</p>
              <p class="file-size-text">{{ formatFileSize(evidence.fileSize) }}</p>
            </div>
            <el-tag
              :type="getStatusTagType(evidence.status)"
              :class="['status-tag', `status-${evidence.status.toLowerCase()}`]"
              effect="dark"
            >
              <el-icon v-if="evidence.status === 'VERIFIED'" :size="12"><Check /></el-icon>
              <el-icon v-if="evidence.status === 'SEALED'" :size="12"><Lock /></el-icon>
              {{ getStatusText(evidence.status) }}
            </el-tag>
          </div>

          <div class="hash-display">
            <div class="hash-label-row">
              <span class="hash-label">SHA-256 Hash</span>
              <el-button
                text
                size="small"
                type="primary"
                @click="copyHash(evidence.fileHash)"
              >
                <el-icon><CopyDocument /></el-icon>
                复制
              </el-button>
            </div>
            <div class="hash-value-container">
              <el-tooltip :content="evidence.fileHash" placement="top" :show-after="500">
                <code class="hash-code">{{ truncateHash(evidence.fileHash) }}</code>
              </el-tooltip>
            </div>
          </div>

          <div class="meta-info">
            <div class="meta-item">
              <el-icon><Clock /></el-icon>
              <span>上传时间：{{ formatDate(evidence.uploadedAt) }}</span>
            </div>
            <div v-if="evidence.sealInfo" class="meta-item seal-info">
              <el-icon><Lock /></el-icon>
              <span>固化时间：{{ formatDate(evidence.sealInfo.sealedAt) }}</span>
            </div>
          </div>

          <div class="action-buttons">
            <el-dropdown trigger="click" class="mobile-dropdown">
              <el-button type="primary" text>
                操作
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-if="evidence.status !== 'VERIFIED' && evidence.status !== 'SEALED'"
                    @click="handleVerify(evidence.id)"
                  >
                    <el-icon><CircleCheck /></el-icon>
                    校验完整性
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="evidence.status === 'VERIFIED'"
                    @click="handleSeal(evidence.id)"
                  >
                    <el-icon><Lock /></el-icon>
                    固化存证
                  </el-dropdown-item>
                  <el-dropdown-item @click="handleDownload(evidence)">
                    <el-icon><Download /></el-icon>
                    下载
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleDelete(evidence.id, evidence.fileName)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <div class="desktop-actions">
              <el-button
                v-if="evidence.status !== 'VERIFIED' && evidence.status !== 'SEALED'"
                type="primary"
                size="small"
                :loading="verifyingId === evidence.id"
                @click="handleVerify(evidence.id)"
              >
                <el-icon><CircleCheck /></el-icon>
                校验完整性
              </el-button>
              <el-button
                v-if="evidence.status === 'VERIFIED'"
                type="warning"
                size="small"
                :loading="sealingId === evidence.id"
                @click="handleSeal(evidence.id)"
              >
                <el-icon><Lock /></el-icon>
                固化存证
              </el-button>
              <el-button
                type="success"
                size="small"
                @click="handleDownload(evidence)"
              >
                <el-icon><Download /></el-icon>
                下载
              </el-button>
              <el-popconfirm
                title="确定要删除此证据吗？此操作不可逆。"
                confirm-button-text="确定"
                cancel-button-text="取消"
                @confirm="handleDelete(evidence.id, evidence.fileName)"
              >
                <template #reference>
                  <el-button type="danger" size="small">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import {
  ElMessage,
  ElMessageBox,
  ElProgress,
} from 'element-plus'
import {
  Upload,
  Document,
  Picture,
  VideoPlay,
  Check,
  Lock,
  Delete,
  Download,
  CopyDocument,
  CircleCheck,
  ArrowDown,
  Clock,
} from '@element-plus/icons-vue'
import { evidencesApi } from '@/lib/api/evidences.api'
import type { EvidenceDetail, VerifyResult, SealInfo } from '@/lib/api/evidences.api'
import { useCaseWorkflowStore } from '@/lib/stores/caseWorkflow'

const workflowStore = useCaseWorkflowStore()

interface UploadProgressItem {
  file: File
  progress: number
  status: 'waiting' | 'uploading' | 'completed' | 'failed'
  speed?: string
  remainingTime?: string
  startTime?: number
  abortController?: AbortController
}

const dropZoneRef = ref<HTMLElement>()
const fileInputRef = ref<HTMLInputElement>()
const isDragOver = ref(false)
const uploadingFiles = ref<UploadProgressItem[]>([])
const uploadedEvidences = ref<EvidenceDetail[]>([])
const verifyingId = ref<string | null>(null)
const sealingId = ref<string | null>(null)

const acceptedFileTypes = [
  '.pdf',
  '.jpg',
  '.jpeg',
  '.png',
  '.doc',
  '.docx',
  '.xls',
  '.xlsx',
  '.mp4',
  '.mp3',
]
const maxFileSize = 100 * 1024 * 1024 // 100MB

function getFileIcon(fileType: string): any {
  const type = fileType.toLowerCase()
  if (type.includes('pdf') || type.includes('document') || type.includes('doc')) return Document
  if (type.includes('image') || type.includes('png') || type.includes('jpg')) return Picture
  if (type.includes('video') || type.includes('mp4')) return VideoPlay
  return Document
}

function validateFiles(files: File[]): { valid: File[]; errors: string[] } {
  const validFiles: File[] = []
  const errors: string[] = []

  for (const file of files) {
    const extension = '.' + file.name.split('.').pop()?.toLowerCase()
    
    if (!acceptedFileTypes.includes(extension)) {
      errors.push(`"${file.name}"：不支持的文件格式`)
      continue
    }

    if (file.size > maxFileSize) {
      errors.push(`"${file.name}"：文件大小超过 100MB 限制`)
      continue
    }

    validFiles.push(file)
  }

  return { valid: validFiles, errors }
}

function handleDragEnter() {
  isDragOver.value = true
}

function handleDragOver() {
  isDragOver.value = true
}

function handleDragLeave(event: DragEvent) {
  if (!dropZoneRef.value?.contains(event.relatedTarget as Node)) {
    isDragOver.value = false
  }
}

function handleDrop(event: DragEvent) {
  isDragOver.value = false
  
  const files = event.dataTransfer?.files
  if (!files || files.length === 0) return

  processFiles(Array.from(files))
}

function triggerFileInput() {
  fileInputRef.value?.click()
}

function handleFileSelect(event: Event) {
  const input = event.target as HTMLInputElement
  const files = input.files
  if (!files || files.length === 0) return

  processFiles(Array.from(files))
  input.value = ''
}

async function processFiles(files: File[]) {
  const { valid, errors } = validateFiles(files)

  if (errors.length > 0) {
    ElMessage.error({
      message: `文件验证失败：<br/>${errors.join('<br/>')}`,
      dangerouslyUseHTMLString: true,
      duration: 5000,
    })
  }

  if (valid.length === 0) return

  for (const file of valid) {
    await uploadSingleFile(file)
  }
}

async function uploadSingleFile(file: File) {
  const progressItem: UploadProgressItem = {
    file,
    progress: 0,
    status: 'waiting',
    startTime: Date.now(),
    abortController: new AbortController(),
  }
  
  uploadingFiles.value.push(progressItem)

  try {
    progressItem.status = 'uploading'

    const result = await evidencesApi.uploadEvidence(
      workflowStore.currentCase!.id,
      file,
      (percent) => {
        progressItem.progress = percent
        
        const elapsed = (Date.now() - (progressItem.startTime || Date.now())) / 1000
        const loaded = (percent / 100) * file.size
        if (elapsed > 0) {
          const speedBytes = loaded / elapsed
          progressItem.speed = formatSpeed(speedBytes)
          
          const remainingBytes = file.size - loaded
          if (speedBytes > 0) {
            const remainingSeconds = remainingBytes / speedBytes
            progressItem.remainingTime = `预计剩余 ${formatRemainingTime(remainingSeconds)}`
          }
        }
      },
      { evidenceType: 'DOCUMENT', description: '' }
    )

    progressItem.status = 'completed'
    progressItem.progress = 100
    uploadedEvidences.value.push(result)

    ElMessage.success(`"${file.name}" 上传成功`)

    setTimeout(() => {
      const index = uploadingFiles.value.findIndex(item => item.file.name === file.name)
      if (index > -1) {
        uploadingFiles.value.splice(index, 1)
      }
    }, 2000)

  } catch (error: any) {
    progressItem.status = 'failed'
    ElMessage.error({
      message: `"${file.name}" 上传失败：${error.message || '未知错误'}`,
      duration: 5000,
    })
  }
}

function cancelUpload(fileName: string) {
  const index = uploadingFiles.value.findIndex(item => item.file.name === fileName)
  if (index > -1) {
    const item = uploadingFiles.value[index]
    item.abortController?.abort()
    uploadingFiles.value.splice(index, 1)
    ElMessage.info(`已取消 "${fileName}" 的上传`)
  }
}

async function handleVerify(evidenceId: string) {
  verifyingId.value = evidenceId
  
  try {
    const result: VerifyResult = await evidencesApi.verifyEvidence(evidenceId)
    
    const evidence = uploadedEvidences.value.find(e => e.id === evidenceId)
    if (evidence) {
      evidence.status = 'VERIFIED'
    }

    await ElMessageBox.alert(
      `<div style="text-align: left;">
        <p><strong>校验结果：</strong>${result.match ? '✅ Hash 匹配' : '❌ Hash 不匹配'}</p>
        <p><strong>当前 Hash：</strong><code>${result.currentHash}</code></p>
        <p><strong>存储 Hash：</strong><code>${result.storedHash}</code></p>
        <p><strong>区块链验证：</strong>${result.chainVerified ? '✅ 已通过' : '⚠️ 未通过'}</p>
        <p><strong>校验时间：</strong>${formatDate(result.verifiedAt)}</p>
        ${result.details ? `<p><strong>详情：</strong>${result.details}</p>` : ''}
      </div>`,
      '校验结果',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        type: result.match ? 'success' : 'error',
      }
    )

    ElMessage.success('证据校验成功，可以执行固化操作')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error({
        message: `校验失败：${error.message || '未知错误'}`,
        duration: 5000,
      })
    }
  } finally {
    verifyingId.value = null
  }
}

async function handleSeal(evidenceId: string) {
  try {
    await ElMessageBox.confirm(
      '确定要将此证据上链固化吗？此操作不可逆。',
      '确认固化存证',
      {
        confirmButtonText: '确定固化',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    sealingId.value = evidenceId
    
    const sealInfo: SealInfo = await evidencesApi.sealEvidence(evidenceId)
    
    const evidence = uploadedEvidences.value.find(e => e.id === evidenceId)
    if (evidence) {
      evidence.status = 'SEALED'
      evidence.sealInfo = sealInfo
    }

    await ElMessageBox.alert(
      `<div style="text-align: left;">
        <p><strong>✅ 固化成功！</strong></p>
        <p><strong>凭证编号：</strong><code>${sealInfo.sealId}</code></p>
        <p><strong>固化时间：</strong>${formatDate(sealInfo.sealedAt)}</p>
        <p><strong>有效期：</strong>${sealInfo.validityPeriod}</p>
        <p><strong>固化方式：</strong>${sealInfo.sealMethod}</p>
        <p><strong>验证地址：</strong><a href="${sealInfo.verificationUrl}" target="_blank">${sealInfo.verificationUrl}</a></p>
      </div>`,
      '固化凭证信息',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '查看凭证',
        type: 'success',
      }
    )

    ElMessage.success('证据已成功上链固化')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error({
        message: `固化失败：${error.message || '未知错误'}`,
        duration: 5000,
      })
    }
  } finally {
    sealingId.value = null
  }
}

function handleDownload(evidence: EvidenceDetail) {
  evidencesApi.downloadEvidence(evidence.id, evidence.fileName)
  ElMessage.success(`开始下载 "${evidence.fileName}"`)
}

async function handleDelete(evidenceId: string, fileName: string) {
  try {
    await evidencesApi.deleteEvidence(evidenceId)
    
    const index = uploadedEvidences.value.findIndex(e => e.id === evidenceId)
    if (index > -1) {
      uploadedEvidences.value.splice(index, 1)
    }

    ElMessage.success(`已删除 "${fileName}"`)
  } catch (error: any) {
    ElMessage.error({
      message: `删除失败：${error.message || '未知错误'}`,
      duration: 5000,
    })
  }
}

async function copyHash(hash: string) {
  try {
    await navigator.clipboard.writeText(hash)
    ElMessage.success('Hash 值已复制到剪贴板')
  } catch (error) {
    const textarea = document.createElement('textarea')
    textarea.value = hash
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    ElMessage.success('Hash 值已复制到剪贴板')
  }
}

function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

function formatSpeed(bytesPerSecond: number): string {
  if (bytesPerSecond < 1024) {
    return bytesPerSecond.toFixed(1) + ' B/s'
  } else if (bytesPerSecond < 1024 * 1024) {
    return (bytesPerSecond / 1024).toFixed(1) + ' KB/s'
  } else {
    return (bytesPerSecond / (1024 * 1024)).toFixed(1) + ' MB/s'
  }
}

function formatRemainingTime(seconds: number): string {
  if (seconds < 60) {
    return `${Math.ceil(seconds)} 秒`
  } else if (seconds < 3600) {
    return `${Math.floor(seconds / 60)} 分 ${Math.ceil(seconds % 60)} 秒`
  } else {
    return `${Math.floor(seconds / 3600)} 小时 ${Math.floor((seconds % 3600) / 60)} 分`
  }
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

function truncateHash(hash: string): string {
  if (hash.length <= 32) return hash
  return `${hash.substring(0, 8)}...${hash.substring(hash.length / 2 - 4, hash.length / 2 + 4)}...${hash.substring(hash.length - 8)}`
}

function getProgressStatusText(status: string): string {
  const statusMap: Record<string, string> = {
    waiting: '等待中',
    uploading: '上传中',
    completed: '已完成',
    failed: '失败',
  }
  return statusMap[status] || status
}

function getProgressStatusType(status: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const typeMap: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    waiting: 'info',
    uploading: 'warning',
    completed: 'success',
    failed: 'danger',
  }
  return typeMap[status] || 'info'
}

function getStatusText(status: string): string {
  const statusMap: Record<string, string> = {
    UPLOADED: '已上传',
    VERIFIED: '已校验',
    SEALED: '已固化',
    REJECTED: '已拒绝',
    DISPUTED: '有争议',
  }
  return statusMap[status] || status
}

function getStatusTagType(status: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const typeMap: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    UPLOADED: '',
    VERIFIED: 'success',
    SEALED: 'warning',
    REJECTED: 'danger',
    DISPUTED: 'danger',
  }
  return typeMap[status] || ''
}

onMounted(async () => {
  if (workflowStore.currentCase?.id) {
    try {
      const evidences = await evidencesApi.getCaseEvidences(workflowStore.currentCase.id)
      uploadedEvidences.value = evidences
    } catch (error: any) {
      console.error('加载证据列表失败:', error)
    }
  }
})
</script>

<style scoped>
.evidence-upload-panel {
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: linear-gradient(to bottom, #ffffff, #f9fafb);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}

.panel-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e40af;
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel-subtitle {
  font-size: 13px;
  color: #6b7280;
}

.drop-zone {
  border: 2px dashed #d1d5db;
  border-radius: 12px;
  padding: 48px 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #f9fafb;
  position: relative;
  overflow: hidden;
}

.drop-zone:hover {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.drop-zone.is-dragover {
  border-color: #3b82f6;
  background-color: #dbeafe;
  transform: scale(1.01);
  box-shadow: 0 0 20px rgba(59, 130, 246, 0.2);
}

.drop-zone.is-dragover .main-text {
  color: #3b82f6;
  font-weight: 600;
}

.drop-zone-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.upload-icon {
  color: #9ca3af;
  transition: color 0.3s ease;
}

.drop-zone:hover .upload-icon,
.drop-zone.is-dragover .upload-icon {
  color: #3b82f6;
}

.drop-zone-text {
  max-width: 400px;
}

.main-text {
  font-size: 16px;
  font-weight: 500;
  color: #374151;
  margin: 0 0 8px 0;
  transition: all 0.3s ease;
}

.sub-text {
  font-size: 13px;
  color: #9ca3af;
  margin: 0;
  line-height: 1.5;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #374151;
  margin: 24px 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e5e7eb;
}

.upload-progress-list {
  margin-top: 24px;
}

.progress-item {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.progress-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.file-type-icon {
  color: #6b7280;
}

.file-name {
  flex: 1;
  font-weight: 500;
  color: #374151;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 150px;
}

.progress-meta {
  display: flex;
  gap: 16px;
  margin-top: 8px;
  font-size: 12px;
  color: #6b7280;
  flex-wrap: wrap;
}

.evidence-list {
  margin-top: 24px;
}

.evidence-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 16px;
}

.evidence-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 20px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.evidence-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.evidence-card.is-sealed {
  border-color: #fbbf24;
  background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%);
  position: relative;
}

.evidence-card.is-sealed::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #7c3aed, #f59e0b);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% {
    opacity: 0.5;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.5;
  }
}

.card-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.file-icon {
  color: #3b82f6;
  flex-shrink: 0;
}

.file-info {
  flex: 1;
  min-width: 120px;
}

.file-name-text {
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
  word-break: break-all;
}

.file-size-text {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

.status-verified {
  animation: pulse-green 2s infinite;
}

@keyframes pulse-green {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(5, 150, 105, 0.4);
  }
  50% {
    box-shadow: 0 0 0 8px rgba(5, 150, 105, 0);
  }
}

.status-sealed {
  background: linear-gradient(135deg, #7c3aed, #f59e0b);
  border: none;
  animation: glow-purple 2s ease-in-out infinite alternate;
}

@keyframes glow-purple {
  from {
    box-shadow: 0 0 8px rgba(124, 58, 237, 0.4), 0 0 16px rgba(245, 158, 11, 0.2);
  }
  to {
    box-shadow: 0 0 16px rgba(124, 58, 237, 0.6), 0 0 24px rgba(245, 158, 11, 0.4);
  }
}

.hash-display {
  background: #1f2937;
  border-radius: 8px;
  padding: 14px;
  margin-bottom: 16px;
}

.hash-label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.hash-label {
  font-size: 12px;
  font-weight: 600;
  color: #9ca3af;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.hash-value-container {
  background: #111827;
  border-radius: 6px;
  padding: 10px 12px;
  overflow-x: auto;
}

.hash-code {
  font-family: 'Courier New', Courier, monospace;
  font-size: 13px;
  color: #10b981;
  word-break: break-all;
  line-height: 1.6;
  letter-spacing: 0.3px;
}

.meta-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
  font-size: 13px;
  color: #6b7280;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.seal-info {
  color: #d97706;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.mobile-dropdown {
  display: none;
}

.desktop-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .evidence-grid {
    grid-template-columns: 1fr;
  }

  .mobile-dropdown {
    display: block;
  }

  .desktop-actions {
    display: none;
  }

  .drop-zone {
    padding: 32px 16px;
  }

  .panel-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .card-header {
    flex-direction: column;
  }

  .action-buttons {
    width: 100%;
  }

  .mobile-dropdown {
    width: 100%;
  }

  .mobile-dropdown .el-button {
    width: 100%;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .evidence-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1025px) {
  .evidence-grid {
    grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  }
}
</style>
