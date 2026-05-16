export interface NotifySocketMessage {
  type: 'CASE_STATUS' | 'DOCUMENT_GENERATED' | 'TRIAL_NOTICE' | 'EVIDENCE_REVIEW' | 'SYSTEM_NOTICE'
  messageId: number
  title: string
  content: string
  bizType?: string
  bizId?: string
  timestamp: string
}
