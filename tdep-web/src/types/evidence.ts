export enum EvidenceType {
  DOCUMENT = 'document',
  PHOTO = 'photo',
  VIDEO = 'video',
  AUDIO = 'audio',
  ELECTRONIC = 'electronic',
  PHYSICAL = 'physical',
  OTHER = 'other',
}

export enum EvidenceStatus {
  PENDING = 'pending',
  VERIFIED = 'verified',
  REJECTED = 'rejected',
  DISPUTED = 'disputed',
}

export interface Evidence {
  id: string
  caseId: string
  name: string
  type: EvidenceType
  status: EvidenceStatus
  description?: string
  fileName: string
  fileSize: number
  mimeType: string
  url: string
  thumbnailUrl?: string
  uploadedBy: string
  uploadedAt: string
  verifiedBy?: string
  verifiedAt?: string
  chainOfCustody?: ChainOfCustodyRecord[]
  metadata?: EvidenceMetadata
}

export interface ChainOfCustodyRecord {
  id: string
  action: string
  performedBy: string
  performedAt: string
  location: string
  notes?: string
}

export interface EvidenceMetadata {
  hash?: string
  gpsLocation?: {
    latitude: number
    longitude: number
  }
  captureDevice?: string
  captureTime?: string
  customFields?: Record<string, unknown>
}

export interface UploadEvidenceRequest {
  file: File
  caseId: string
  name: string
  type: EvidenceType
  description?: string
}

export interface EvidenceListItem {
  id: string
  name: string
  type: EvidenceType
  status: EvidenceStatus
  fileName: string
  fileSize: number
  uploadedAt: string
}
