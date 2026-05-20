import apiClient from './client'

export interface EvidenceDetail {
  id: string
  caseId: string
  fileName: string
  originalFileName: string
  fileType: string
  fileSize: number
  fileHash: string
  hashAlgorithm: string
  uploader: {
    id: string
    name: string
    role: string
  }
  uploadedAt: string
  evidenceType: string
  description?: string
  tags?: string[]
  status: EvidenceStatus
  chainInfo?: ChainInfo
  sealInfo?: SealInfo
}

export enum EvidenceStatus {
  UPLOADED = 'UPLOADED',
  VERIFIED = 'VERIFIED',
  SEALED = 'SEALED',
  REJECTED = 'REJECTED',
  DISPUTED = 'DISPUTED'
}

export interface ChainInfo {
  txHash: string
  blockNumber: number
  blockTimestamp: string
  nodeUrl: string
  confirmations: number
  isFinalized: boolean
  chainId: string
}

export interface SealInfo {
  sealId: string
  sealedAt: string
  sealedBy: string
  sealMethod: string
  validityPeriod: string
  verificationUrl: string
  certificateUrl: string
}

export interface VerifyResult {
  evidenceId: string
  isValid: boolean
  currentHash: string
  storedHash: string
  match: boolean
  verifiedAt: string
  chainVerified: boolean
  details?: string
}

export interface EvidenceChainItem {
  evidenceId: string
  fileName: string
  fileHash: string
  sequence: number
  prevHash: string | null
  chainHash: string
  uploadedAt: string
  isSealed: boolean
  isVerified: boolean
}

export interface EvidenceChain {
  caseId: string
  totalEvidence: number
  rootHash: string
  items: EvidenceChainItem[]
  lastUpdated: string
}

export interface UploadEvidenceOptions {
  description?: string
  evidenceType?: string
  tags?: string[]
}

export const evidencesApi = {
  async uploadEvidence(
    caseId: string,
    file: File,
    onProgress?: (percent: number) => void,
    options?: UploadEvidenceOptions
  ): Promise<EvidenceDetail> {
    const additionalData: Record<string, string> = {}

    if (options?.description) {
      additionalData.description = options.description
    }
    if (options?.evidenceType) {
      additionalData.evidenceType = options.evidenceType
    }
    if (options?.tags && options.tags.length > 0) {
      additionalData.tags = JSON.stringify(options.tags)
    }

    const response = await apiClient.upload<EvidenceDetail>(
      `/evidences/upload?caseId=${caseId}`,
      file,
      onProgress
        ? (progressEvent: ProgressEvent) => {
            if (progressEvent.total && progressEvent.total > 0) {
              const percent = Math.round(
                (progressEvent.loaded * 100) / progressEvent.total
              )
              onProgress(percent)
            }
          }
        : undefined,
      Object.keys(additionalData).length > 0 ? additionalData : undefined
    )
    return response.data
  },

  async getEvidence(evidenceId: string): Promise<EvidenceDetail> {
    const response = await apiClient.get<EvidenceDetail>(`/evidences/${evidenceId}`)
    return response.data
  },

  async getCaseEvidences(caseId: string): Promise<EvidenceDetail[]> {
    const response = await apiClient.get<EvidenceDetail[]>(
      `/evidences/case/${caseId}`
    )
    return response.data
  },

  async getEvidenceChain(caseId: string): Promise<EvidenceChain> {
    const response = await apiClient.get<EvidenceChain>(
      `/evidences/${caseId}/chain`
    )
    return response.data
  },

  async verifyEvidence(evidenceId: string): Promise<VerifyResult> {
    const response = await apiClient.post<VerifyResult>(
      `/evidences/${evidenceId}/verify`
    )
    return response.data
  },

  async sealEvidence(evidenceId: string): Promise<SealInfo> {
    const response = await apiClient.post<SealInfo>(
      `/evidences/${evidenceId}/seal`
    )
    return response.data
  },

  downloadEvidence(evidenceId: string, filename?: string): void {
    apiClient.download(`/evidences/${evidenceId}/download`, filename)
  },

  async deleteEvidence(evidenceId: string): Promise<void> {
    await apiClient.delete(`/evidences/${evidenceId}`)
  },
}
