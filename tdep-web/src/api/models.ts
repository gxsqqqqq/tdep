export interface CaseItem {
  id: number
  caseNo: string
  caseTitle: string
  caseType: string
  causeAction: string
  claimAmount?: number
  status: string
  judgeId?: number
  createdBy?: number
  createdAt?: string
}

export interface CaseDetail extends CaseItem {
  procedureType?: string
  claimRequest?: string
  courtName?: string
  parties?: Array<Record<string, unknown>>
  processes?: Array<Record<string, unknown>>
}

export interface EvidenceItem {
  id: number
  evidenceNo: string
  caseId: number
  uploaderId: number
  fileName: string
  fileExt?: string
  contentType?: string
  fileSize: number
  status: string
  description?: string
  uploadedAt?: string
  sealedAt?: string
  hash?: {
    hashAlgorithm?: string
    hashValue?: string
    fileFingerprint?: string
  }
}

export interface DocumentItem {
  id: number
  caseId: number
  documentNo: string
  documentType: string
  title: string
  status: string
  templateId?: number
  wordObjectKey?: string
  pdfObjectKey?: string
  signedObjectKey?: string
  fileHash?: string
  generatedAt?: string
  signedAt?: string
  createdAt?: string
}

export interface DocumentTemplateItem {
  id: number
  templateCode: string
  documentType: string
  templateName: string
  version: number
  enabled: number
  currentVersion: number
  previewUrl?: string
  placeholders?: string[]
  createdAt?: string
}

export interface NotifyMessageItem {
  id?: number
  messageId?: number
  notifyType?: string
  type?: string
  title: string
  content: string
  status?: string
  readAt?: string
  createdAt?: string
  timestamp?: string
}

export interface DownloadUrlResult {
  downloadUrl: string
  url?: string
}
