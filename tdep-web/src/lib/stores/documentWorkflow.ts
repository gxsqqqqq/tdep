import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  DocumentStatus,
  DocumentTransition,
  DocumentStateDefinition,
  DocumentTransitionRule,
  DOCUMENT_STATES,
  DOCUMENT_TRANSITION_RULES,
  getAvailableDocumentTransitions,
  canDocumentTransition,
} from '@/lib/constants/documentLifecycle'
import { documentsApi, type DocumentDetail, type SignDocumentRequest, type GenerateDocumentRequest } from '@/lib/api/documents.api'

export interface DocumentInfo {
  id: string
  documentNumber: string
  title: string
  documentType: string
  status: DocumentStatus
  caseId: string
  author: string
  createdAt: string
  updatedAt: string
  fileUrl?: string
}

export interface AuditLogEntry {
  id: string
  timestamp: string
  actor: string
  actorRole: string
  action: 'CREATE' | 'READ' | 'SIGN' | 'DOWNLOAD' | 'ARCHIVE' | 'REVIEW'
  description: string
  traceId: string
  ipAddress?: string
  metadata?: Record<string, unknown>
}

export interface DocumentVersion {
  version: number
  createdAt: string
  createdBy: string
  changeDescription: string
  fileUrl: string
  fileSize: number
}

function mapDocumentDetailToDocumentInfo(detail: DocumentDetail): DocumentInfo {
  return {
    id: detail.id,
    documentNumber: detail.documentNumber,
    title: detail.title,
    documentType: detail.documentType.toString(),
    status: detail.status as DocumentStatus,
    caseId: detail.caseId,
    author: detail.author?.name || '',
    createdAt: detail.createdAt,
    updatedAt: detail.updatedAt,
    fileUrl: detail.fileUrl,
  }
}

function generateMockAuditLogs(documentId: string): AuditLogEntry[] {
  return [
    {
      id: `${documentId}-log-1`,
      timestamp: new Date().toISOString(),
      actor: '系统',
      actorRole: 'SYSTEM',
      action: 'CREATE',
      description: '创建文书草稿',
      traceId: `trace-${Date.now()}`,
    },
  ]
}

export const useDocumentWorkflowStore = defineStore('documentWorkflow', () => {
  const currentDocument = ref<DocumentInfo | null>(null)
  const auditLogs = ref<AuditLogEntry[]>([])
  const versions = ref<DocumentVersion[]>([])
  const isProcessing = ref(false)
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const pdfUrl = ref<string | null>(null)

  const currentStatus = computed<DocumentStatus>(() => currentDocument.value?.status || DocumentStatus.DRAFT)

  const currentStateDefinition = computed<DocumentStateDefinition>(() => {
    return DOCUMENT_STATES[currentStatus.value] || DOCUMENT_STATES[DocumentStatus.DRAFT]
  })

  const availableActions = computed<DocumentTransitionRule[]>(() => {
    return getAvailableDocumentTransitions(currentStatus.value)
  })

  const statusTimeline = computed<DocumentStateDefinition[]>(() => {
    return Object.values(DOCUMENT_STATES)
      .filter(state => state.order > 0)
      .sort((a, b) => a.order - b.order)
  })

  const currentStepIndex = computed<number>(() => {
    const timeline = statusTimeline.value
    return timeline.findIndex(state => state.status === currentStatus.value)
  })

  const progressPercentage = computed<number>(() => {
    const totalSteps = statusTimeline.value.length
    if (totalSteps <= 1) return 0
    return Math.round(((currentStepIndex.value + 1) / totalSteps) * 100)
  })

  const isCompleted = computed<boolean>(() => {
    return currentStateDefinition.value.isTerminal
  })

  async function fetchDocumentDetail(documentId: string): Promise<void> {
    isLoading.value = true
    error.value = null

    try {
      const detail = await documentsApi.getDocument(documentId)
      currentDocument.value = mapDocumentDetailToDocumentInfo(detail)
      auditLogs.value = generateMockAuditLogs(documentId)
      pdfUrl.value = documentsApi.getPdfPreviewUrl(documentId)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '加载文书信息失败'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function generateDocument(request: GenerateDocumentRequest): Promise<DocumentDetail> {
    if (!currentDocument.value) {
      throw new Error('未加载文书信息')
    }

    if (!canDocumentTransition(currentStatus.value, DocumentTransition.GENERATE)) {
      error.value = `当前状态 ${currentStatus.value} 不允许执行生成操作`
      throw new Error(error.value)
    }

    isProcessing.value = true
    error.value = null

    try {
      const result = await documentsApi.generateDocument(request)
      await fetchDocumentDetail(result.id)
      return result
    } catch (err) {
      error.value = err instanceof Error ? err.message : '生成文书失败'
      throw err
    } finally {
      isProcessing.value = false
    }
  }

  async function signDocument(signatureData: string, method: 'HANDWRITTEN' | 'DIGITAL_SEAL'): Promise<void> {
    if (!currentDocument.value) {
      throw new Error('未加载文书信息')
    }

    if (!canDocumentTransition(currentStatus.value, DocumentTransition.SIGN)) {
      error.value = `当前状态 ${currentStatus.value} 不允许执行签署操作`
      throw new Error(error.value)
    }

    isProcessing.value = true
    error.value = null

    try {
      const request: SignDocumentRequest = {
        signatureData,
        signatureMethod: method,
        timestamp: new Date().toISOString(),
      }

      await documentsApi.signDocument(currentDocument.value.id, request)

      auditLogs.value.unshift({
        id: `${currentDocument.value.id}-log-${Date.now()}`,
        timestamp: new Date().toISOString(),
        actor: '当前用户',
        actorRole: method === 'DIGITAL_SEAL' ? 'JUDGE' : 'PARTY',
        action: 'SIGN',
        description: `完成${method === 'DIGITAL_SEAL' ? '电子签章' : '手写签名'}签署`,
        traceId: `trace-${Date.now()}`,
      })

      await fetchDocumentDetail(currentDocument.value.id)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '签署文书失败'
      throw err
    } finally {
      isProcessing.value = false
    }
  }

  async function archiveDocument(): Promise<void> {
    if (!currentDocument.value) {
      throw new Error('未加载文书信息')
    }

    if (!canDocumentTransition(currentStatus.value, DocumentTransition.ARCHIVE)) {
      error.value = `当前状态 ${currentStatus.value} 不允许执行归档操作`
      throw new Error(error.value)
    }

    isProcessing.value = true
    error.value = null

    try {
      await documentsApi.publishDocument(currentDocument.value.id)

      auditLogs.value.unshift({
        id: `${currentDocument.value.id}-log-${Date.now()}`,
        timestamp: new Date().toISOString(),
        actor: '当前用户',
        actorRole: 'JUDGE',
        action: 'ARCHIVE',
        description: '完成文书归档',
        traceId: `trace-${Date.now()}`,
      })

      await fetchDocumentDetail(currentDocument.value.id)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '归档文书失败'
      throw err
    } finally {
      isProcessing.value = false
    }
  }

  function getPdfPreviewUrl(): string | null {
    if (!currentDocument.value) return null
    return pdfUrl.value || documentsApi.getPdfPreviewUrl(currentDocument.value.id)
  }

  function downloadPdf(): void {
    if (!currentDocument.value) {
      throw new Error('未加载文书信息')
    }
    documentsApi.downloadPdf(currentDocument.value.id)
  }

  function reset(): void {
    currentDocument.value = null
    auditLogs.value = []
    versions.value = []
    isProcessing.value = false
    isLoading.value = false
    error.value = null
    pdfUrl.value = null
  }

  return {
    currentDocument,
    auditLogs,
    versions,
    isProcessing,
    isLoading,
    error,
    pdfUrl,
    currentStatus,
    currentStateDefinition,
    availableActions,
    statusTimeline,
    currentStepIndex,
    progressPercentage,
    isCompleted,
    fetchDocumentDetail,
    generateDocument,
    signDocument,
    archiveDocument,
    getPdfPreviewUrl,
    downloadPdf,
    reset,
  }
})
