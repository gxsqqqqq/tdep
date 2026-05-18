export function statusType(status?: string): 'success' | 'warning' | 'info' | 'primary' | 'danger' {
  if (!status) return 'info'
  if (['SUCCESS', 'SIGNED', 'EFFECTIVE', 'ARCHIVED', 'ACCEPTED', 'PAID', 'PASSED', 'VERIFIED'].includes(status)) return 'success'
  if (['PENDING', 'GENERATED', 'UNDER_REVIEW', 'SUBMITTED', 'FILING_REVIEW', 'EVIDENCE_SUBMITTING', 'SCHEDULED', 'IN_TRIAL'].includes(status)) return 'warning'
  if (['FAILED', 'VOIDED', 'REJECTED', 'FILING_REJECTED'].includes(status)) return 'danger'
  if (['DRAFT'].includes(status)) return 'info'
  return 'primary'
}

export function formatFileSize(size?: number) {
  if (!size) return '0 B'
  if (size < 1024) return `${size} B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`
  if (size < 1024 * 1024 * 1024) return `${(size / 1024 / 1024).toFixed(1)} MB`
  return `${(size / 1024 / 1024 / 1024).toFixed(1)} GB`
}

export const caseStatusNames: Record<string, string> = {
  DRAFT: '草稿',
  SUBMITTED: '已提交',
  FILING_REVIEW: '立案审核中',
  FILING_REJECTED: '立案被驳回',
  ACCEPTED: '已受理',
  EVIDENCE_SUBMITTING: '举证中',
  SCHEDULED: '已排期',
  IN_TRIAL: '审理中',
  JUDGED: '已判决',
  EFFECTIVE: '已生效',
  CLOSED: '已结案'
}

export const evidenceStatusNames: Record<string, string> = {
  UPLOADED: '已上传',
  SEALED: '已固化',
  FROZEN: '已冻结',
  VERIFIED: '已校验'
}

export const documentStatusNames: Record<string, string> = {
  DRAFT: '草稿',
  GENERATED: '已生成',
  SIGNED: '已签章',
  VOIDED: '已作废'
}

export const stepNames = ['立案', '缴费', '举证', '文书', '进度']

export function getCaseStatusName(status?: string) {
  return caseStatusNames[status || ''] || status || '-'
}

export function getEvidenceStatusName(status?: string) {
  return evidenceStatusNames[status || ''] || status || '-'
}

export function getDocumentStatusName(status?: string) {
  return documentStatusNames[status || ''] || status || '-'
}

export function getFileIcon(ext?: string): string {
  if (!ext) return 'Document'
  const lower = ext.toLowerCase()
  if (['pdf'].includes(lower)) return 'Document'
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(lower)) return 'Picture'
  if (['mp4', 'avi', 'mov', 'wmv', 'flv'].includes(lower)) return 'VideoPlay'
  if (['mp3', 'wav', 'ogg', 'flac'].includes(lower)) return 'Headset'
  if (['doc', 'docx'].includes(lower)) return 'Document'
  if (['xls', 'xlsx'].includes(lower)) return 'Grid'
  if (['zip', 'rar', '7z', 'tar', 'gz'].includes(lower)) return 'FolderOpened'
  return 'Document'
}
