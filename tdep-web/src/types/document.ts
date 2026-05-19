export enum DocumentType {
  INDICTMENT = 'indictment',
  DEFENSE = 'defense',
  EVIDENCE_LIST = 'evidence_list',
  COURT_RECORD = 'court_record',
  JUDGEMENT = 'judgement',
  SETTLEMENT = 'settlement',
  OTHER = 'other',
}

export enum DocumentStatus {
  DRAFT = 'draft',
  REVIEWING = 'reviewing',
  APPROVED = 'approved',
  SIGNED = 'signed',
  REJECTED = 'rejected',
}

export interface Document {
  id: string
  caseId: string
  title: string
  type: DocumentType
  status: DocumentStatus
  content?: string
  fileName: string
  fileSize: number
  mimeType: string
  url: string
  previewUrl?: string
  version: number
  createdBy: string
  createdAt: string
  updatedBy?: string
  updatedAt: string
  signedBy?: SignatureInfo[]
  templateId?: string
}

export interface SignatureInfo {
  userId: string
  userName: string
  signatureData: string
  signedAt: string
  ipAddress?: string
}

export interface DocumentListItem {
  id: string
  title: string
  type: DocumentType
  status: DocumentStatus
  fileName: string
  createdAt: string
  isSigned: boolean
}

export interface SignDocumentRequest {
  signatureData: string
  password?: string
}

export interface DocumentTemplate {
  id: string
  name: string
  type: DocumentType
  description?: string
  variables: TemplateVariable[]
}

export interface TemplateVariable {
  name: string
  label: string
  type: 'text' | 'date' | 'select' | 'textarea'
  required: boolean
  options?: { label: string; value: string }[]
}
