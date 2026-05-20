import apiClient from './client'

// ==================== 类型定义 ====================

/** 文书基本信息 */
export interface DocumentDetail {
  id: string
  caseId: string
  documentNumber: string
  title: string
  documentType: DocumentType
  templateId?: string
  status: DocumentStatus
  content?: string
  author: {
    id: string
    name: string
    role: string
  }
  reviewer?: {
    id: string
    name: string
    reviewedAt: string
  }
  signatories?: Array<{
    partyName: string
    signedAt: string
    signatureHash: string
  }>
  createdAt: string
  updatedAt: string
  publishedAt?: string
  fileUrl?: string
  fileSize?: number
  pageCount?: number
}

/** 文书类型枚举 */
export enum DocumentType {
  COMPLAINT = 'COMPLAINT',
  ANSWER = 'ANSWER',
  CIVIL_VERDICT = 'CIVIL_VERDICT',
  MEDIATION_PROTOCOL = 'MEDIATION_PROTOCOL',
  EXECUTION_NOTICE = 'EXECUTION_NOTICE',
  EVIDENCE_EXCHANGE = 'EVIDENCE_EXCHANGE',
  COURT_SUMMONS = 'COURT_SUMMONS',
  PRELIMINARY_HEARING = 'PRELIMINARY_HEARING'
}

/** 文书状态枚举 */
export enum DocumentStatus {
  DRAFT = 'DRAFT',
  REVIEWING = 'REVIEWING',
  APPROVED = 'APPROVED',
  PUBLISHED = 'PUBLISHED',
  SIGNED = 'SIGNED',
  ARCHIVED = 'ARCHIVED'
}

/** 生成文书请求体 */
export interface GenerateDocumentRequest {
  caseId: string
  templateId: string
  documentType: DocumentType
  variables?: Record<string, string>
}

/** 签署请求体 */
export interface SignDocumentRequest {
  signatureData: string
  signatureMethod: string
  timestamp: string
}

// ==================== API 模块 ====================

/**
 * Documents API module for legal document management.
 * Provides CRUD operations, review workflow, signing, and PDF export capabilities.
 */
export const documentsApi = {
  /**
   * Generate a new document using AI assistance.
   * @param request - Document generation parameters including case ID, template, and type.
   * @returns The generated document detail.
   */
  async generateDocument(request: GenerateDocumentRequest): Promise<DocumentDetail> {
    const { data } = await apiClient.post<DocumentDetail>('/document/generate', request)
    return data
  },

  /**
   * Get document details by ID.
   * @param documentId - Unique identifier of the document.
   * @returns Full document information including content and metadata.
   */
  async getDocument(documentId: string): Promise<DocumentDetail> {
    const { data } = await apiClient.get<DocumentDetail>(`/document/${documentId}`)
    return data
  },

  /**
   * Get all documents belonging to a specific case.
   * @param caseId - Case identifier to filter documents by.
   * @returns Array of document details associated with the case.
   */
  async getCaseDocuments(caseId: string): Promise<DocumentDetail[]> {
    const { data } = await apiClient.get<DocumentDetail[]>(`/document/case/${caseId}`)
    return data
  },

  /**
   * Update document content (HTML or Markdown).
   * @param documentId - Target document identifier.
   * @param content - New document content in HTML or Markdown format.
   * @returns Updated document detail.
   */
  async updateContent(documentId: string, content: string): Promise<DocumentDetail> {
    const { data } = await apiClient.put<DocumentDetail>(`/document/${documentId}/content`, { content })
    return data
  },

  /**
   * Submit document for review (transitions status to REVIEWING).
   * @param documentId - Target document identifier.
   * @returns Updated document detail after submission.
   */
  async submitForReview(documentId: string): Promise<DocumentDetail> {
    const { data } = await apiClient.post<DocumentDetail>(`/document/${documentId}/review`)
    return data
  },

  /**
   * Review a document (judge operation). Approve or reject based on the approved flag.
   * @param documentId - Target document identifier.
   * @param approved - True to approve, false to reject.
   * @param comment - Optional review comment explaining the decision.
   * @returns Updated document detail after review.
   */
  async reviewDocument(documentId: string, approved: boolean, comment?: string): Promise<DocumentDetail> {
    const endpoint = approved
      ? `/document/${documentId}/approve`
      : `/document/${documentId}/reject`
    const { data } = await apiClient.post<DocumentDetail>(endpoint, comment ? { comment } : undefined)
    return data
  },

  /**
   * Publish a document (requires approved status).
   * @param documentId - Target document identifier.
   * @returns Updated document detail after publication.
   */
  async publishDocument(documentId: string): Promise<DocumentDetail> {
    const { data } = await apiClient.post<DocumentDetail>(`/document/${documentId}/publish`)
    return data
  },

  /**
   * Sign a document online (handwritten or digital seal).
   * @param documentId - Target document identifier.
   * @param request - Signature data including base64 image and method.
   * @returns Updated document detail after signing.
   */
  async signDocument(documentId: string, request: SignDocumentRequest): Promise<DocumentDetail> {
    const { data } = await apiClient.post<DocumentDetail>(`/document/${documentId}/sign`, request)
    return data
  },

  /**
   * Download document as PDF file. Triggers browser download.
   * @param documentId - Target document identifier.
   * @param filename - Optional custom filename (defaults to server-provided name).
   */
  downloadPdf(documentId: string, filename?: string): void {
    // 使用 apiClient 内置的 download 方法，自动处理 blob 响应和文件下载
    apiClient.download(`/document/${documentId}/pdf`, filename)
  },

  /**
   * Get PDF preview URL for embedding in iframe or opening in new tab.
   * @param documentId - Target document identifier.
   * @returns Full URL pointing to the PDF resource.
   */
  getPdfPreviewUrl(documentId: string): string {
    // 从 axios 实例获取 baseURL 并拼接完整路径
    const baseURL = apiClient.getInstance().defaults.baseURL || ''
    return `${baseURL}/document/${documentId}/pdf`
  },

  /**
   * Delete a document permanently.
   * @param documentId - Target document identifier.
   */
  async deleteDocument(documentId: string): Promise<void> {
    await apiClient.delete<void>(`/document/${documentId}`)
  }
}
