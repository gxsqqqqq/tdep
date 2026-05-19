import { CaseStatus } from '@/lib/constants/caseStatus'

export interface Case {
  id: string
  caseNumber: string
  title: string
  caseType: string
  status: CaseStatus
  plaintiff: PartyInfo
  defendant: PartyInfo
  judge?: JudgeInfo
  clerk?: ClerkInfo
  courtName: string
  filingDate: string
  scheduledDate?: string
  trialDate?: string
  judgementDate?: string
  description?: string
  evidenceCount: number
  documentCount: number
  trialCount: number
  createdAt: string
  updatedAt: string
}

export interface PartyInfo {
  id: string
  name: string
  type: 'individual' | 'organization'
  lawyer?: LawyerInfo
  contactPhone: string
  contactAddress?: string
}

export interface LawyerInfo {
  id: string
  name: string
  lawFirm: string
  licenseNumber: string
  phone: string
}

export interface JudgeInfo {
  id: string
  name: string
  title: string
}

export interface ClerkInfo {
  id: string
  name: string
}

export interface CreateCaseRequest {
  title: string
  caseType: string
  plaintiffId: string
  defendantId: string
  description?: string
  evidenceIds?: string[]
}

export interface UpdateCaseRequest extends Partial<CreateCaseRequest> {
  status?: CaseStatus
}

export interface CaseListItem {
  id: string
  caseNumber: string
  title: string
  caseType: string
  status: CaseStatus
  plaintiffName: string
  defendantName: string
  filingDate: string
  updatedAt: string
}
