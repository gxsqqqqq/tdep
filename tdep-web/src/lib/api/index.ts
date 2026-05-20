export { apiClient } from './client'
export type { ApiResponse, PaginatedResponse, ApiError } from './client'

export { casesApi } from './cases.api'
export type {
  CaseDetail,
  CaseStatusEnum,
  CreateCaseRequest,
  TransitionRequest,
  TransitionResponse,
  TimelineEventDTO,
} from './cases.api'

export { evidencesApi } from './evidences.api'
export type {
  EvidenceDetail,
  EvidenceStatus,
  ChainInfo,
  SealInfo,
  VerifyResult,
  EvidenceChainItem,
  EvidenceChain,
  UploadEvidenceOptions,
} from './evidences.api'

export { documentsApi } from './documents.api'
export type {
  DocumentDetail,
  DocumentType,
  DocumentStatus,
  GenerateDocumentRequest,
  SignDocumentRequest,
} from './documents.api'
