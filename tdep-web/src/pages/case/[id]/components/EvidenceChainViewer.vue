<template>
  <div class="evidence-chain-viewer">
    <!-- 顶部标题栏 + 统计信息 -->
    <div class="viewer-header">
      <div class="header-left">
        <span class="header-icon">⛓️</span>
        <span class="header-title">证据链可视化</span>
      </div>
      <div class="header-center" v-if="chainData">
        <div class="stat-item">
          <span class="stat-label">总证据</span>
          <span class="stat-value">{{ chainData.totalEvidence }}</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-label">已固化</span>
          <span class="stat-value stat-sealed">{{ sealedCount }}</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-label">待处理</span>
          <span class="stat-value stat-pending">{{ pendingCount }}</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-label">完整性</span>
          <span class="stat-value" :class="chainIntegrity === 100 ? 'stat-ok' : 'stat-warn'">
            {{ chainIntegrity }}%
            <el-icon v-if="chainIntegrity === 100"><CircleCheck /></el-icon>
            <el-icon v-else><Warning /></el-icon>
          </span>
        </div>
      </div>
      <div class="header-actions">
        <el-button :icon="Refresh" size="small" :loading="isLoading" @click="loadEvidenceChain">刷新</el-button>
        <el-button :icon="Download" size="small" type="primary" plain @click="handleExportReport">导出报告</el-button>
      </div>
    </div>

    <!-- 加载骨架屏 -->
    <div v-if="isLoading && !chainData" class="skeleton-container">
      <div class="skeleton-merkle"></div>
      <div class="skeleton-chain">
        <div v-for="i in 4" :key="i" class="skeleton-node"></div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      <el-icon :size="48" color="#ef4444"><CircleCloseFilled /></el-icon>
      <p class="error-text">{{ error }}</p>
      <el-button type="primary" size="small" @click="loadEvidenceChain">重新加载</el-button>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!chainData || chainData.items.length === 0" class="empty-state">
      <el-icon :size="64" color="#d1d5db"><FolderOpened /></el-icon>
      <h4 class="empty-title">暂无证据</h4>
      <p class="empty-desc">该案件尚未提交任何证据材料</p>
    </div>

    <!-- 主内容区 -->
    <template v-else>
      <!-- Merkle Root 展示区 -->
      <div class="merkle-root-card">
        <div class="merkle-bg-effect"></div>
        <div class="merkle-content">
          <div class="merkle-header-row">
            <div class="merkle-title">
              <el-icon><Link /></el-icon>
              <span>Merkle Root Hash</span>
            </div>
            <div class="merkle-integrity" :class="{ 'is-full': chainIntegrity === 100 }">
              <el-icon v-if="chainIntegrity === 100"><Lock /></el-icon>
              <el-icon v-else><Unlock /></el-icon>
              <span>{{ chainIntegrity === 100 ? '链完整' : `完整度 ${chainIntegrity}%` }}</span>
            </div>
          </div>
          <div class="merkle-hash-block" @click="copyHash(chainData.rootHash)" title="点击复制完整 Hash">
            <span class="merkle-hash-text">{{ chainData.rootHash }}</span>
            <el-icon class="merkle-copy-icon"><CopyDocument /></el-icon>
          </div>
          <div class="merkle-meta">
            <span>最后更新: {{ formatTime(chainData.lastUpdated) }}</span>
            <span>包含 {{ chainData.totalEvidence }} 个证据节点</span>
          </div>
        </div>
      </div>

      <!-- 证据链条可视化（水平链式结构） -->
      <div class="chain-nodes-wrapper">
        <div class="chain-nodes-scroll">
          <div class="chain-nodes">
            <template v-for="(item, index) in chainData.items" :key="item.evidenceId">
              <!-- 证据节点卡片 -->
              <div
                class="chain-node"
                :class="{
                  'is-selected': selectedEvidenceId === item.evidenceId,
                  'is-sealed': item.isSealed,
                  'is-verified': item.isVerified,
                  'is-pending': !item.isSealed && !item.isVerified
                }"
                @click="selectEvidence(item)"
              >
                <div class="node-seq">#{{ item.sequence }}</div>
                <div class="node-filename" :title="item.fileName">
                  <el-icon :size="16" :color="getFileColor(getFileExt(item.fileName))">
                    <component :is="getFileIcon(getFileExt(item.fileName))" />
                  </el-icon>
                  <span>{{ truncateFileName(item.fileName) }}</span>
                </div>
                <div class="node-status">
                  <span v-if="item.isSealed" class="status-badge status-sealed">
                    <el-icon><Lock /></el-icon>已固化
                  </span>
                  <span v-else-if="item.isVerified" class="status-badge status-verified">
                    <el-icon><Check /></el-icon>已校验
                  </span>
                  <span v-else class="status-badge status-pending">
                    <el-icon><Loading /></el-icon>待处理
                  </span>
                </div>
              </div>

              <!-- 连接器（箭头线） -->
              <div
                v-if="index < chainData.items.length - 1"
                class="chain-connector"
                :class="{
                  'is-verified': item.isSealed && chainData.items[index + 1].isSealed
                }"
              >
                <div class="connector-line"></div>
                <div class="connector-arrow">→</div>
                <div class="connector-line"></div>
              </div>
            </template>
          </div>
        </div>
      </div>

      <!-- 选中证据详情面板 -->
      <transition name="detail-slide">
        <div v-if="selectedEvidence" class="evidence-detail-panel">
          <!-- 详情头部：文件名 + 类型图标 -->
          <div class="detail-header">
            <div class="detail-file-info">
              <el-icon :size="28" :color="getFileColor(getFileExt(selectedEvidence.fileName))">
                <component :is="getFileIcon(getFileExt(selectedEvidence.fileName))" />
              </el-icon>
              <div class="detail-file-meta">
                <h3 class="detail-filename">{{ selectedEvidence.originalFileName }}</h3>
                <div class="detail-file-sub">
                  <span>{{ formatFileSize(selectedEvidence.fileSize) }}</span>
                  <span class="sub-dot">·</span>
                  <span>{{ selectedEvidence.fileType.toUpperCase() }}</span>
                </div>
              </div>
            </div>
            <div class="detail-uploader">
              <el-avatar :size="32" class="uploader-avatar">
                {{ selectedEvidence.uploader.name.charAt(0) }}
              </el-avatar>
              <div class="uploader-info">
                <span class="uploader-name">{{ selectedEvidence.uploader.name }}</span>
                <span class="uploader-role">{{ selectedEvidence.uploader.role }}</span>
              </div>
            </div>
          </div>

          <!-- Hash 信息区 -->
          <div class="detail-section">
            <div class="section-title">🔐 文件完整性的数字指纹</div>
            <div class="hash-grid">
              <div class="hash-card">
                <div class="hash-card-label">SHA-256 (文件 Hash)</div>
                <div class="hash-card-value" @click="copyHash(selectedEvidence.fileHash)">
                  <span>{{ selectedEvidence.fileHash }}</span>
                  <el-icon class="hash-copy-btn"><CopyDocument /></el-icon>
                </div>
              </div>
              <div class="hash-card">
                <div class="hash-card-label">Chain Hash (Merkle)</div>
                <div class="hash-card-value" @click="copyHash(chainItemForSelected?.chainHash || '')">
                  <span>{{ chainItemForSelected?.chainHash || '-' }}</span>
                  <el-icon class="hash-copy-btn"><CopyDocument /></el-icon>
                </div>
              </div>
              <div class="hash-card">
                <div class="hash-card-label">Previous Hash (前驱)</div>
                <div class="hash-card-value" @click="copyHash(chainItemForSelected?.prevHash || '')">
                  <span v-if="chainItemForSelected?.prevHash">{{ chainItemForSelected.prevHash }}</span>
                  <span v-else class="genesis-text">null (Genesis Block)</span>
                  <el-icon v-if="chainItemForSelected?.prevHash" class="hash-copy-btn"><CopyDocument /></el-icon>
                </div>
              </div>
            </div>
          </div>

          <!-- 区块链信息区 -->
          <div class="detail-section" v-if="selectedEvidence.chainInfo">
            <div class="section-title">⛓️ 区块链存证信息</div>
            <div class="blockchain-grid">
              <div class="bc-info-row">
                <span class="bc-label">区块高度</span>
                <span class="bc-value bc-link" @click="openBlockExplorer(selectedEvidence.chainInfo.blockNumber)">
                  #{{ selectedEvidence.chainInfo.blockNumber.toLocaleString() }}
                  <el-icon class="bc-link-icon"><View /></el-icon>
                </span>
              </div>
              <div class="bc-info-row">
                <span class="bc-label">交易 Hash</span>
                <span class="bc-value bc-mono">{{ selectedEvidence.chainInfo.txHash }}</span>
              </div>
              <div class="bc-info-row">
                <span class="bc-label">上链时间</span>
                <span class="bc-value">{{ formatDateTime(selectedEvidence.chainInfo.blockTimestamp) }}</span>
              </div>
              <div class="bc-info-row">
                <span class="bc-label">确认数</span>
                <span class="bc-value" :class="selectedEvidence.chainInfo.isFinalized ? 'text-green-600' : 'text-yellow-600'">
                  {{ selectedEvidence.chainInfo.confirmations }}/12
                  <el-icon v-if="selectedEvidence.chainInfo.isFinalized"><Check /></el-icon>
                  <el-icon v-else><Loading /></el-icon>
                </span>
              </div>
              <div class="bc-info-row">
                <span class="bc-label">节点地址</span>
                <span class="bc-value bc-mono text-xs">{{ selectedEvidence.chainInfo.nodeUrl }}</span>
              </div>
            </div>
          </div>

          <!-- 固化凭证区 -->
          <div class="detail-section seal-section" v-if="selectedEvidence.sealInfo">
            <div class="section-title">📜 固化凭证</div>
            <div class="seal-grid">
              <div class="seal-info-row">
                <span class="seal-label">凭证编号</span>
                <span class="seal-value seal-highlight">{{ selectedEvidence.sealInfo.sealId }}</span>
              </div>
              <div class="seal-info-row">
                <span class="seal-label">固化时间</span>
                <span class="seal-value">{{ formatDateTime(selectedEvidence.sealInfo.sealedAt) }}</span>
              </div>
              <div class="seal-info-row">
                <span class="seal-label">有效期至</span>
                <span class="seal-value">{{ selectedEvidence.sealInfo.validityPeriod }}</span>
              </div>
              <div class="seal-info-row">
                <span class="seal-label">固化方式</span>
                <el-tag size="small" :type="selectedEvidence.sealInfo.sealMethod === 'FULL_FILE' ? 'success' : 'info'">
                  {{ selectedEvidence.sealInfo.sealMethod === 'FULL_FILE' ? '完整存证' : '仅 Hash 存证' }}
                </el-tag>
              </div>
            </div>
            <div class="seal-actions">
              <el-button size="small" type="warning" plain :icon="Download" @click="downloadCertificate(selectedEvidence)">凭证下载</el-button>
              <el-button size="small" type="success" plain :icon="View" @click="verifyCertificate(selectedEvidence)">验证凭证</el-button>
              <el-button size="small" plain :icon="Link" @click="openBlockExplorer(selectedEvidence.chainInfo?.blockNumber)">查看区块</el-button>
            </div>
          </div>

          <!-- 操作按钮区 -->
          <div class="detail-actions">
            <el-button
              type="primary"
              plain
              :icon="Loading"
              :loading="verifyingId === selectedEvidence.id"
              @click="handleVerifyEvidence(selectedEvidence.id)"
            >重新校验</el-button>
            <el-button
              v-if="selectedEvidence.chainInfo"
              plain
              :icon="Link"
              @click="openBlockExplorer(selectedEvidence.chainInfo!.blockNumber)"
            >⛓️ 查看区块详情</el-button>
            <el-button
              type="success"
              plain
              :icon="Download"
              @click="handleDownloadEvidence(selectedEvidence)"
            >下载原始文件</el-button>
            <el-button
              v-if="!selectedEvidence.sealInfo"
              type="warning"
              :icon="Lock"
              :loading="sealingId === selectedEvidence.id"
              @click="handleSealEvidence(selectedEvidence)"
            >立即固化</el-button>
          </div>
        </div>
      </transition>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Link,
  Lock,
  Unlock,
  Check,
  Warning,
  CopyDocument,
  Refresh,
  Download,
  View,
  CircleCheck,
  CircleCloseFilled,
  Loading,
  FolderOpened,
  Document,
  Picture,
  VideoPlay,
  Headset,
  Grid
} from '@element-plus/icons-vue'
import {
  evidencesApi,
  type EvidenceChain,
  type EvidenceChainItem as ApiEvidenceChainItem,
  type EvidenceDetail,
  type VerifyResult,
  type SealInfo
} from '@/lib/api/evidences.api'
import { useCaseWorkflowStore } from '@/lib/stores/caseWorkflow'
import { formatDateTime } from '@/utils/formatter'

const workflowStore = useCaseWorkflowStore()

const isLoading = ref(false)
const error = ref<string | null>(null)
const chainData = ref<EvidenceChain | null>(null)
const evidenceDetails = ref<EvidenceDetail[]>([])
const selectedEvidenceId = ref<string | null>(null)
const sealingId = ref<string | null>(null)
const verifyingId = ref<string | null>(null)

const sealedCount = computed<number>(() => {
  if (!chainData.value) return 0
  return chainData.value.items.filter((item: ApiEvidenceChainItem) => item.isSealed).length
})

const pendingCount = computed<number>(() => {
  if (!chainData.value) return 0
  return chainData.value.items.filter((item: ApiEvidenceChainItem) => !item.isSealed).length
})

const chainIntegrity = computed<number>(() => {
  if (!chainData.value || chainData.value.items.length === 0) return 0
  const sealed = chainData.value.items.filter((item: ApiEvidenceChainItem) => item.isSealed).length
  return Math.round((sealed / chainData.value.items.length) * 100)
})

const selectedEvidence = computed<EvidenceDetail | undefined>(() =>
  evidenceDetails.value.find((e: EvidenceDetail) => e.id === selectedEvidenceId.value)
)

const chainItemForSelected = computed<ApiEvidenceChainItem | undefined>(() => {
  if (!chainData.value || !selectedEvidenceId.value) return undefined
  return chainData.value.items.find((item: ApiEvidenceChainItem) => item.evidenceId === selectedEvidenceId.value)
})

async function loadEvidenceChain() {
  if (!workflowStore.currentCase?.id) return

  isLoading.value = true
  error.value = null

  try {
    const caseId = workflowStore.currentCase.id
    const [chain, details] = await Promise.all([
      evidencesApi.getEvidenceChain(caseId),
      evidencesApi.getCaseEvidences(caseId)
    ])
    chainData.value = chain
    evidenceDetails.value = details

    if (details.length > 0 && !selectedEvidenceId.value) {
      selectedEvidenceId.value = chain.items[0]?.evidenceId ?? null
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载失败'
    console.error('加载证据链失败:', err)
  } finally {
    isLoading.value = false
  }
}

function selectEvidence(item: ApiEvidenceChainItem) {
  selectedEvidenceId.value = item.evidenceId
}

async function handleVerifyEvidence(evidenceId: string) {
  verifyingId.value = evidenceId

  try {
    const result: VerifyResult = await evidencesApi.verifyEvidence(evidenceId)
    if (result.isValid && result.match) {
      ElMessage.success('校验通过，文件完整性验证成功')
    } else {
      ElMessage.warning(`校验未通过: ${result.details || '文件 Hash 不匹配'}`)
    }

    await loadEvidenceChain()
  } catch (err) {
    ElMessage.error(err instanceof Error ? err.message : '校验失败')
    console.error('校验证据失败:', err)
  } finally {
    verifyingId.value = null
  }
}

async function handleSealEvidence(evidence: EvidenceDetail) {
  try {
    await ElMessageBox.confirm(
      `确认固化证据「${evidence.originalFileName}」？\n\n固化后该证据将不可篡改并生成司法可信凭证。`,
      '确认固化',
      { confirmButtonText: '确认固化', cancelButtonText: '取消', type: 'warning' }
    )

    sealingId.value = evidence.id
    const sealResult: SealInfo = await evidencesApi.sealEvidence(evidence.id)
    ElMessage.success(`固化成功！凭证编号: ${sealResult.sealId}`)
    await loadEvidenceChain()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error(err instanceof Error ? err.message : '固化失败')
      console.error('固化证据失败:', err)
    }
  } finally {
    sealingId.value = null
  }
}

function handleDownloadEvidence(evidence: EvidenceDetail) {
  evidencesApi.downloadEvidence(evidence.id, evidence.originalFileName)
}

function downloadCertificate(evidence: EvidenceDetail) {
  if (evidence.sealInfo?.certificateUrl) {
    window.open(evidence.sealInfo.certificateUrl, '_blank', 'noopener,noreferrer')
  } else {
    ElMessage.info('凭证文件正在生成中，请稍后再试')
  }
}

function verifyCertificate(evidence: EvidenceDetail) {
  if (evidence.sealInfo?.verificationUrl) {
    window.open(evidence.sealInfo.verificationUrl, '_blank', 'noopener,noreferrer')
  } else {
    ElMessage.info('验证链接暂不可用')
  }
}

function openBlockExplorer(blockNumber?: number) {
  if (!blockNumber) {
    ElMessage.warning('暂无区块信息')
    return
  }
  ElMessage.info(`跳转到区块链浏览器查看区块 #${blockNumber}`)
}

async function copyHash(hash: string) {
  if (!hash) return

  try {
    await navigator.clipboard.writeText(hash)
    ElMessage.success('Hash 值已复制到剪贴板')
  } catch {
    const textarea = document.createElement('textarea')
    textarea.value = hash
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    ElMessage.success('Hash 值已复制到剪贴板')
  }
}

function handleExportReport() {
  ElMessage.info('证据链报告导出功能开发中...')
}

function getFileIcon(ext: string): typeof Document {
  const iconMap: Record<string, typeof Document> = {
    pdf: Document,
    jpg: Picture,
    jpeg: Picture,
    png: Picture,
    gif: Picture,
    mp4: VideoPlay,
    avi: VideoPlay,
    mov: VideoPlay,
    mp3: Headset,
    wav: Headset,
    doc: Document,
    docx: Document,
    xls: Grid,
    xlsx: Grid,
    zip: FolderOpened,
    rar: FolderOpened,
  }
  return iconMap[ext.toLowerCase()] || Document
}

function getFileColor(ext: string): string {
  const colorMap: Record<string, string> = {
    pdf: '#F56C6C',
    jpg: '#E6A23C',
    jpeg: '#E6A23C',
    png: '#E6A23C',
    gif: '#E6A23C',
    mp4: '#409EFF',
    avi: '#409EFF',
    mov: '#409EFF',
    mp3: '#67C23A',
    wav: '#67C23A',
    doc: '#409EFF',
    docx: '#409EFF',
    xls: '#67C23A',
    xlsx: '#67C23A',
    zip: '#909399',
    rar: '#909399',
  }
  return colorMap[ext.toLowerCase()] || '#909399'
}

function getFileExt(fileName: string): string {
  const parts = fileName.split('.')
  return parts.length > 1 ? parts[parts.length - 1].toLowerCase() : ''
}

function truncateFileName(fileName: string, maxLen: number = 12): string {
  if (fileName.length <= maxLen) return fileName
  const ext = getFileExt(fileName)
  const nameWithoutExt = fileName.slice(0, -(ext.length + 1))
  const truncated = nameWithoutExt.slice(0, maxLen - ext.length - 2)
  return `${truncated}...${ext}`
}

function truncateHash(hash: string, start: number = 8, end: number = 8): string {
  if (!hash || hash.length <= start + end) return hash || ''
  return `${hash.substring(0, start)}...${hash.substring(hash.length - end)}`
}

function formatTime(timeStr?: string): string {
  if (!timeStr) return '-'
  return new Date(timeStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return `${(bytes / Math.pow(1024, i)).toFixed(i > 0 ? 1 : 0)} ${units[i]}`
}

let watchTimeout: ReturnType<typeof setTimeout> | null = null

onMounted(() => {
  loadEvidenceChain()
})

watch(() => workflowStore.currentCase?.id, () => {
  if (watchTimeout) clearTimeout(watchTimeout)
  watchTimeout = setTimeout(() => {
    loadEvidenceChain()
  }, 300)
})
</script>

<style scoped>
.evidence-chain-viewer {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ========== 顶部标题栏 ========== */
.viewer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #1e3a5f 0%, #0f2744 100%);
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(15, 39, 68, 0.3);
  flex-wrap: wrap;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  font-size: 22px;
}

.header-title {
  font-size: 17px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 0.5px;
}

.header-center {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.stat-value {
  font-size: 14px;
  font-weight: 700;
  color: #ffffff;
  display: flex;
  align-items: center;
  gap: 3px;
}

.stat-sealed { color: #fbbf24; }
.stat-pending { color: #94a3b8; }
.stat-ok { color: #34d399; }
.stat-warn { color: #fbbf24; }

.stat-divider {
  width: 1px;
  height: 18px;
  background: rgba(255, 255, 255, 0.15);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* ========== 骨架屏 ========== */
.skeleton-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 20px 0;
}

.skeleton-merkle {
  height: 140px;
  border-radius: 12px;
  background: linear-gradient(90deg, #1e293b 25%, #334155 50%, #1e293b 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-chain {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 10px 0;
}

.skeleton-node {
  width: 120px;
  height: 110px;
  border-radius: 12px;
  background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

/* ========== 错误状态 ========== */
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 12px;
}

.error-text {
  font-size: 15px;
  color: #ef4444;
  margin: 0;
}

/* ========== 空状态 ========== */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 8px;
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #6b7280;
  margin: 0;
}

.empty-desc {
  font-size: 14px;
  color: #9ca3af;
  margin: 0;
}

/* ========== Merkle Root 卡片 ========== */
.merkle-root-card {
  position: relative;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border-radius: 14px;
  padding: 24px;
  overflow: hidden;
  border: 1px solid rgba(59, 130, 246, 0.25);
  box-shadow:
    0 0 30px rgba(59, 130, 246, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

.merkle-bg-effect {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at center, rgba(59, 130, 246, 0.07) 0%, transparent 65%);
  animation: merkleRotate 20s linear infinite;
  pointer-events: none;
}

@keyframes merkleRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.merkle-content {
  position: relative;
  z-index: 1;
}

.merkle-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.merkle-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #e2e8f0;
}

.merkle-integrity {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #94a3b8;
  padding: 4px 12px;
  border-radius: 20px;
  background: rgba(148, 163, 184, 0.1);
  border: 1px solid rgba(148, 163, 184, 0.2);
}

.merkle-integrity.is-full {
  color: #34d399;
  background: rgba(52, 211, 153, 0.1);
  border-color: rgba(52, 211, 153, 0.25);
}

.merkle-hash-block {
  font-family: 'Courier New', Consolas, Monaco, monospace;
  font-size: 15px;
  font-weight: 500;
  color: #60a5fa;
  background: rgba(15, 23, 42, 0.6);
  padding: 14px 18px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  word-break: break-all;
  transition: all 0.25s ease;
  border: 1px solid rgba(96, 165, 250, 0.2);
  margin-bottom: 14px;
  gap: 12px;
}

.merkle-hash-block:hover {
  background: rgba(15, 23, 42, 0.85);
  border-color: rgba(96, 165, 250, 0.45);
  box-shadow: 0 0 20px rgba(96, 165, 250, 0.15);
}

.merkle-hash-text {
  flex: 1;
  line-height: 1.6;
}

.merkle-copy-icon {
  flex-shrink: 0;
  color: rgba(96, 165, 250, 0.5);
  transition: color 0.2s;
  cursor: pointer;
}

.merkle-hash-block:hover .merkle-copy-icon {
  color: #60a5fa;
}

.merkle-meta {
  font-size: 13px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

/* ========== 证据链条可视化 ========== */
.chain-nodes-wrapper {
  width: 100%;
  overflow-x: auto;
  padding: 8px 0 16px;
}

.chain-nodes-scroll {
  min-width: max-content;
}

.chain-nodes {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 0;
  padding: 8px 16px;
}

.chain-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  min-width: 120px;
  max-width: 150px;
  padding: 14px 12px;
  border-radius: 12px;
  background: #ffffff;
  border: 2px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  user-select: none;
}

.chain-node:hover {
  transform: scale(1.05) translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #93c5fd;
}

.chain-node.is-selected {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2), 0 8px 24px rgba(59, 130, 246, 0.15);
  background: #eff6ff;
}

.chain-node.is-sealed {
  border-color: #fbbf24;
  background: linear-gradient(180deg, #fffbeb 0%, #fefce8 100%);
}

.chain-node.is-sealed:hover {
  border-color: #f59e0b;
  box-shadow: 0 8px 24px rgba(245, 158, 11, 0.2);
}

.chain-node.is-sealed.is-selected {
  border-color: #d97706;
  box-shadow: 0 0 0 3px rgba(217, 119, 6, 0.2), 0 8px 24px rgba(245, 158, 11, 0.2);
}

.node-seq {
  font-size: 13px;
  font-weight: 800;
  color: #64748b;
  background: #f1f5f9;
  padding: 2px 10px;
  border-radius: 10px;
  letter-spacing: 0.5px;
}

.is-sealed .node-seq {
  color: #b45309;
  background: #fef3c7;
}

.is-selected .node-seq {
  color: #1d4ed8;
  background: #dbeafe;
}

.node-filename {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
  color: #374151;
  text-align: center;
  line-height: 1.3;
  overflow: hidden;
  max-width: 100%;
}

.node-filename span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.node-status {
  display: flex;
  align-items: center;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 11px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 10px;
  white-space: nowrap;
}

.status-sealed {
  color: #b45309;
  background: linear-gradient(135deg, #fef3c7, #fde68a);
}

.status-verified {
  color: #15803d;
  background: linear-gradient(135deg, #dcfce7, #bbf7d0);
}

.status-pending {
  color: #6b7280;
  background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
}

/* ========== 连接器 ========== */
.chain-connector {
  display: flex;
  align-items: center;
  gap: 0;
  padding: 0 4px;
  flex-shrink: 0;
}

.connector-line {
  width: 24px;
  height: 2px;
  background: #cbd5e1;
  border-radius: 1px;
  transition: all 0.3s ease;
}

.connector-arrow {
  font-size: 14px;
  font-weight: 700;
  color: #94a3b8;
  line-height: 1;
}

.chain-connector.is-verified .connector-line {
  height: 3px;
  background: linear-gradient(90deg, #f59e0b, #d97706);
  box-shadow: 0 0 6px rgba(245, 158, 11, 0.3);
}

.chain-connector.is-verified .connector-arrow {
  color: #d97706;
}

/* ========== 选中证据详情面板 ========== */
.evidence-detail-panel {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 22px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  padding-bottom: 18px;
  border-bottom: 1px solid #f3f4f6;
}

.detail-file-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.detail-file-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-filename {
  font-size: 17px;
  font-weight: 700;
  color: #111827;
  margin: 0;
  word-break: break-word;
}

.detail-file-sub {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #6b7280;
}

.sub-dot { opacity: 0.4; }

.detail-uploader {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 14px;
  background: #f9fafb;
  border-radius: 10px;
  border: 1px solid #f3f4f6;
}

.uploader-avatar {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: #fff;
  font-weight: 700;
  font-size: 14px;
}

.uploader-info {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.uploader-name {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

.uploader-role {
  font-size: 11px;
  color: #9ca3af;
}

/* ========== 详情分区通用 ========== */
.detail-section {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.section-title {
  font-size: 14px;
  font-weight: 700;
  color: #374151;
  padding-left: 10px;
  border-left: 3px solid #3b82f6;
}

/* ========== Hash 信息区 ========== */
.hash-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 12px;
}

.hash-card {
  background: #0f172a;
  border-radius: 10px;
  padding: 14px 16px;
  border: 1px solid rgba(51, 65, 85, 0.6);
}

.hash-card-label {
  font-size: 11px;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.8px;
  margin-bottom: 8px;
}

.hash-card-value {
  font-family: 'Courier New', Consolas, Monaco, monospace;
  font-size: 12px;
  color: #38bdf8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  cursor: pointer;
  padding: 8px 10px;
  background: rgba(15, 23, 42, 0.5);
  border-radius: 6px;
  border: 1px solid transparent;
  transition: all 0.2s ease;
  word-break: break-all;
  line-height: 1.5;
}

.hash-card-value:hover {
  border-color: rgba(56, 189, 248, 0.35);
  background: rgba(15, 23, 42, 0.8);
}

.hash-copy-btn {
  flex-shrink: 0;
  color: #475569;
  font-size: 14px;
  transition: color 0.2s;
}

.hash-card-value:hover .hash-copy-btn {
  color: #38bdf8;
}

.genesis-text {
  color: #94a3b8;
  font-style: italic;
  font-family: 'Courier New', Consolas, Monaco, monospace;
  font-size: 12px;
}

/* ========== 区块链信息区 ========== */
.blockchain-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 10px 20px;
}

.bc-info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #f1f5f9;
}

.bc-label {
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  min-width: 72px;
  flex-shrink: 0;
}

.bc-value {
  font-size: 13px;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 4px;
}

.bc-link {
  color: #3b82f6;
  cursor: pointer;
  font-weight: 600;
  transition: color 0.2s;
}

.bc-link:hover {
  color: #2563eb;
  text-decoration: underline;
}

.bc-link-icon {
  font-size: 12px;
}

.bc-mono {
  font-family: 'Courier New', Consolas, Monaco, monospace;
  font-size: 11px;
  word-break: break-all;
}

/* ========== 固化凭证区 ========== */
.seal-section {
  background: linear-gradient(135deg, #fffbeb 0%, #fefce8 100%);
  border: 1px solid #fde68a;
  border-radius: 12px;
  padding: 20px;
}

.seal-section .section-title {
  border-left-color: #f59e0b;
}

.seal-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 10px 20px;
  margin-bottom: 16px;
}

.seal-info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 0;
}

.seal-label {
  font-size: 12px;
  font-weight: 600;
  color: #92400e;
  min-width: 72px;
  flex-shrink: 0;
}

.seal-value {
  font-size: 13px;
  color: #451a03;
}

.seal-highlight {
  font-family: 'Courier New', Consolas, Monaco, monospace;
  font-weight: 700;
  color: #b45309;
}

.seal-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 14px;
  border-top: 1px solid rgba(245, 158, 11, 0.2);
  flex-wrap: wrap;
}

/* ========== 操作按钮区 ========== */
.detail-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 18px;
  border-top: 1px solid #f3f4f6;
  flex-wrap: wrap;
}

/* ========== 过渡动画 ========== */
.detail-slide-enter-active {
  animation: slideDownFadeIn 0.35s ease-out;
}

.detail-slide-leave-active {
  animation: slideDownFadeIn 0.2s ease-in reverse;
}

@keyframes slideDownFadeIn {
  from {
    opacity: 0;
    transform: translateY(-12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .viewer-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .header-center {
    width: 100%;
    justify-content: flex-start;
  }

  .header-actions {
    width: 100%;
  }

  .header-actions .el-button {
    flex: 1;
  }

  .merkle-root-card {
    padding: 16px;
  }

  .merkle-hash-block {
    font-size: 12px;
    padding: 10px 12px;
  }

  .merkle-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .chain-nodes {
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 8px;
  }

  .chain-node {
    max-width: 280px;
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    padding: 12px 16px;
  }

  .chain-connector {
    flex-direction: column;
    padding: 4px 0;
  }

  .connector-line {
    width: 2px;
    height: 20px;
  }

  .connector-arrow {
    transform: rotate(90deg);
  }

  .hash-grid {
    grid-template-columns: 1fr;
  }

  .blockchain-grid {
    grid-template-columns: 1fr;
  }

  .seal-grid {
    grid-template-columns: 1fr;
  }

  .detail-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .detail-actions {
    flex-direction: column;
  }

  .detail-actions .el-button {
    width: 100%;
  }

  .seal-actions {
    flex-direction: column;
  }

  .seal-actions .el-button {
    width: 100%;
  }
}
</style>
